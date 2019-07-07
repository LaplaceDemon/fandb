package io.github.laplacedemon.fandb;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class FanDB implements DB {
    private Object lock;
    private MemTable memTable;
    private KVLog wal;
    private SequenceGenerator sequenceGenerator;
    
    public FanDB(String path) throws IOException {
        this.memTable = new MemTable();
        this.wal = new KVLog(path);
        this.lock = new Object();
        long lastSequenceId = this.wal.scanIntoMemtable(this.memTable);
        this.sequenceGenerator = new SequenceGenerator(lastSequenceId);
    }
    
    public void put(byte[] key, byte[] value) throws IOException {
        long valueOffset;
        synchronized (lock) {
            long seqid = this.sequenceGenerator.nextSequenceId();
            valueOffset = this.wal.append(key, value);
        }
        
        this.memTable.put(key, valueOffset);
    }

    @Override
    public byte[] get(byte[] key) throws IOException {
        long valueOffset = this.memTable.get(key);
        if(valueOffset == -1) {
            return null;
        }
        
        return this.wal.getValue(valueOffset);
    }
    
    @Override
    public void delete(byte[] key) {
        this.memTable.delete(key);
    }

    public LinkedHashMap<byte[],byte[]> range(byte[] start, byte[] end) throws IOException {
        TreeMap<String, Long> range = this.memTable.range(start, end);
        LinkedHashMap<byte[],byte[]> result = new LinkedHashMap<>();
        Set<Entry<String, Long>> entrySet = range.entrySet();
        for(Entry<String, Long> entry:entrySet) {
            String key = entry.getKey();
            byte[] keyBytes = key.getBytes();
            Long valueOffset = entry.getValue();
            byte[] valueBytes = this.wal.getValue(valueOffset);
            result.put(keyBytes, valueBytes);
        }
        
        return result;
    }

    

}
