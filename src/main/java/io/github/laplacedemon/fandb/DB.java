package io.github.laplacedemon.fandb;

import java.io.IOException;
import java.util.LinkedHashMap;

public interface DB {
    void put(byte[] key, byte[] value) throws IOException;
    byte[] get(byte[] key) throws IOException;
    public LinkedHashMap<byte[],byte[]> range(byte[] start, byte[] end) throws IOException;
    void delete(byte[] key);
}