package io.github.laplacedemon.fandb;

import java.io.IOException;

public interface DB {
    void put(byte[] key, byte[] value) throws IOException;

    byte[] get(byte[] key) throws IOException;

    void delete(byte[] key) throws IOException;
}