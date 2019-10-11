package io.github.laplacedemon.fandb;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

public class TestFanDBDeleteAndGet {
    
    @Test
    public void testPut() throws IOException {
        DBConfig dbconfig = DBConfig.newBuilder().path("testdb.dat").build();
        DB db = new FanDB(dbconfig);
        db.put("hello".getBytes(), "world".getBytes());
        
        byte[] value = db.get("hello".getBytes());
        Assert.assertEquals("world", new String(value));
        db.delete("hello".getBytes());
        byte[] valuedeleteed = db.get("hello".getBytes());
        System.out.println(valuedeleteed);
    }
}
