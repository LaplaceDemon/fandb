package io.github.laplacedemon.fandb;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

public class KVLogTest {
    
    @Test
    public void testPutAndScan() throws IOException {
        File f = new File("data_test.dat");
        if (f.exists()) {
            boolean delete = f.delete();
            if (!delete) {
                Assert.fail();
            }
        }
        KVLog kvLog = new KVLog(f);
        kvLog.append("a".getBytes(), "b".getBytes());
        kvLog.append("c".getBytes(), "d".getBytes());
        kvLog.append("e".getBytes(), "f".getBytes());
        kvLog.append("g".getBytes(), "h".getBytes());
        MemTable table = new HashMemTable();
        long scanIntoMemtable = kvLog.scanIntoMemtable(table);
        kvLog.close();
        Assert.assertEquals(scanIntoMemtable, 4l);
    }
    
    @Test
    public void testRecover() throws IOException {
        File f = new File("data_test.dat");
        if (f.exists()) {
            boolean delete = f.delete();
            if (!delete) {
                Assert.fail();
            }
        }
        KVLog kvLog = new KVLog(f);
        kvLog.append("a".getBytes(), "b".getBytes());
        kvLog.append("c".getBytes(), "d".getBytes());
        kvLog.append("e".getBytes(), "f".getBytes());
        kvLog.append("g".getBytes(), "h".getBytes());
        kvLog.close();
        
        File f1 = new File("data_test.dat");
        KVLog kvLog1 = new KVLog(f1);
        MemTable table = new HashMemTable();
        long lastSN = kvLog1.scanIntoMemtable(table);
        kvLog1.close();
        Assert.assertEquals(lastSN, 4l);
    }

}
