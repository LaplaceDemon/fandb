package io.github.laplacedemon.fandb;

import java.io.IOException;

public class FanDB implements DB {
    private MemTable memTable;
    private KVLog wal;
//    private ReadWriteLock rwlock;
//    private Lock writeLock;
//    private Lock readLock;

    public FanDB(String path) throws IOException {
        this.memTable = new HashMemTable();
        this.wal = new KVLog(path);
        this.wal.scanIntoMemtable(this.memTable);
//        this.rwlock = new ReentrantReadWriteLock();
//        this.writeLock = this.rwlock.writeLock();
//        this.readLock = this.rwlock.readLock();
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
        return this.wal.getValue(valueOffset, valueSize);
    }

    @Override
    public void delete(byte[] key) {
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
