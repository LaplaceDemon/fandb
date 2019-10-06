package io.github.laplacedemon.fandb;

import java.util.concurrent.ConcurrentHashMap;

public class HashMemTable implements MemTable {
    private ConcurrentHashMap<String, ValueIndexer> map;
    
    public HashMemTable() {
        this.map = new ConcurrentHashMap<String, ValueIndexer>();
    }

    public void put(byte[] key, long valueOffset, int valueSize) {
        ValueIndexer valueIndexer = new ValueIndexer(valueOffset, valueSize);
        this.map.put(new String(key), valueIndexer);
    }
    
    public ValueIndexer get(byte[] key) {
        return this.map.get(new String(key));
    }

    public ValueIndexer delete(byte[] key) {
        return this.map.remove(new String(key));
    }
}
