package io.github.laplacedemon.fandb;

import java.io.IOException;

public class FanDB implements DB {
    private MemTable memTable;
    private KVLog wal;

    public FanDB(DBConfig dbconfig) throws IOException {
        String path = dbconfig.getPath();
        this.memTable = new HashMemTable();
        this.wal = new KVLog(path);
        this.wal.scanIntoMemtable(this.memTable);
    }

    public void put(byte[] key, byte[] value) throws IOException {
        long valueOffset = this.wal.append(key, value);
        this.memTable.put(key, valueOffset, value.length);
    }

    @Override
    public byte[] get(byte[] key) throws IOException {
        ValueIndexer valueIndexer = this.memTable.get(key);
        if (valueIndexer == null) {
            return null;
        }
        
        return this.wal.getValue(valueIndexer.getOffset(), valueIndexer.getSize());
    }

    @Override
    public void delete(byte[] key) throws IOException {
        ValueIndexer valueIndexer = this.memTable.get(key);
        if (valueIndexer == null) {
            return ;
        }
        this.wal.clean(valueIndexer.getOffset() + valueIndexer.getSize(), 8);
        this.memTable.delete(key);
    }

//    public LinkedHashMap<byte[], byte[]> range(byte[] start, byte[] end) throws IOException {
//        TreeMap<String, Long> range = this.memTable.range(start, end);
//        LinkedHashMap<byte[], byte[]> result = new LinkedHashMap<>();
//        Set<Entry<String, Long>> entrySet = range.entrySet();
//        for (Entry<String, Long> entry : entrySet) {
//            String key = entry.getKey();
//            byte[] keyBytes = key.getBytes();
//            Long valueOffset = entry.getValue();
//            byte[] valueBytes = this.wal.getValue(valueOffset);
//            result.put(keyBytes, valueBytes);
//        }
//
//        return result;
//    }

}
