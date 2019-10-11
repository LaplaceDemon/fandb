package io.github.laplacedemon.fandb;

import java.io.IOException;

import org.junit.Test;

public class TestKVLog {
    
    @Test
    public void testPut() throws IOException {
        KVLog kvLog = new KVLog("data_test.dat");
        kvLog.append("a".getBytes(), "b".getBytes());
        kvLog.append("c".getBytes(), "d".getBytes());
        MemTable table = new HashMemTable();
        long scanIntoMemtable = kvLog.scanIntoMemtable(table);
        System.out.println(table);
    }
}
