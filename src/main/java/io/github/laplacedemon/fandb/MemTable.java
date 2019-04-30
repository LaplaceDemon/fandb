package io.github.laplacedemon.fandb;

import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class MemTable {
    private ConcurrentSkipListMap<String, Long> map;

    public MemTable() {
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

    public TreeMap<String, Long> range(byte[] start, byte[] end) {
        ConcurrentNavigableMap<String, Long> cnMap = this.map.subMap(new String(start), true, new String(end), false);
        TreeMap<String, Long> resultMap = new TreeMap<>();
        for (Entry<String, Long> entry : cnMap.entrySet()) {
            String key = entry.getKey();
            Long valueOffset = entry.getValue();
            resultMap.put(key, valueOffset);
        }

        return resultMap;
    }

}
