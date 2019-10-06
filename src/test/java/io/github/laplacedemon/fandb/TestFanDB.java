package io.github.laplacedemon.fandb;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

public class TestFanDB {
    
    @Test
    public void testPut() throws IOException {
        DBConfig dbconfig = DBConfig.newBuilder().path("testdb.dat").build();
        DB db = new FanDB(dbconfig);
        db.put("hello".getBytes(), "world".getBytes());
        
        byte[] value = db.get("hello".getBytes());
        Assert.assertEquals("world", new String(value));
    }
    
    @Test
    public void testPutPut() throws IOException {
        DBConfig dbconfig = DBConfig.newBuilder().path("testdb.dat").build();
        DB db = new FanDB(dbconfig);
        db.put("hello".getBytes(), "world0".getBytes());
        db.put("hello".getBytes(), "world1".getBytes());
        db.put("hello".getBytes(), "world2".getBytes());
        db.put("hello".getBytes(), "world3".getBytes());
        
        byte[] value = db.get("hello".getBytes());
        Assert.assertEquals("world3", new String(value));
    }
    
    @Test
    public void testPutPutPut() throws IOException {
        DBConfig dbconfig = DBConfig.newBuilder().path("testdb.dat").build();
        DB db = new FanDB(dbconfig);
        db.put("hello".getBytes(), "world0".getBytes());
        db.put("hello".getBytes(), "world1".getBytes());
        db.put("hello".getBytes(), "world2".getBytes());
        db.put("hello".getBytes(), "world3".getBytes());
        {
            byte[] value = db.get("hello".getBytes());
            Assert.assertEquals("world3", new String(value));
        }
        db.put("hello".getBytes(), "world5".getBytes());
        db.put("hello".getBytes(), "world6".getBytes());
        db.put("hello".getBytes(), "world7".getBytes());
        db.put("hello".getBytes(), "world8".getBytes());
        {
            byte[] value = db.get("hello".getBytes());
            Assert.assertEquals("world8", new String(value));
        }
    }
    
    @Test
    public void testPutGetDeleteGet() throws IOException {
        DBConfig dbconfig = DBConfig.newBuilder().path("testdb.dat").build();
        DB db = new FanDB(dbconfig);
        db.put("hello".getBytes(), "world9".getBytes());
        
        {
            byte[] value = db.get("hello".getBytes());
            Assert.assertEquals("world9", new String(value));
        }
        
        db.delete("hello".getBytes());
        
        {
            byte[] value = db.get("hello".getBytes());
            Assert.assertEquals(value, null);
        }
        
    }
    
//    @Test
//    public void test() throws IOException {
//        DB db = new FanDB("testdb.dat");
//        db.put("hello".getBytes(), "world".getBytes());
//        
//        byte[] value = db.get("hello".getBytes());
//        System.out.println(new String(value));
//    }
}
