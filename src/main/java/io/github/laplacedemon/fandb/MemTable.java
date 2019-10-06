package io.github.laplacedemon.fandb;

public interface MemTable {
    
    public void put(byte[] key, long valueOffset, int valueSize);
    
    public ValueIndexer get(byte[] key);

    public ValueIndexer delete(byte[] key);
    
//    public TreeMap<String, Long> range(byte[] start, byte[] end) {
//        ConcurrentNavigableMap<String, Long> cnMap = this.map.subMap(new String(start), true, new String(end), false);
//        TreeMap<String, Long> resultMap = new TreeMap<>();
//        for (Entry<String, Long> entry : cnMap.entrySet()) {
//            String key = entry.getKey();
//            Long valueOffset = entry.getValue();
//            resultMap.put(key, valueOffset);
//        }
//
//        return resultMap;
//    }

}
