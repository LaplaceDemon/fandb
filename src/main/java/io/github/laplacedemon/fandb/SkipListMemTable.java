package io.github.laplacedemon.fandb;

import java.util.concurrent.ConcurrentSkipListMap;

public class SkipListMemTable {
    private ConcurrentSkipListMap<String, Long> map;
    
    public SkipListMemTable() {
        this.map = new ConcurrentSkipListMap<String, Long>();
    }

    public void put(byte[] key, long valueOffset) {
        this.map.put(new String(key), valueOffset);
    }
    
    public long get(byte[] key) {
        Long offset = this.map.get(new String(key));
        if (offset == null) {
            return -1l;
        }

        return offset;
    }

    public long delete(byte[] key) {
        return this.map.remove(new String(key));
    }
}
