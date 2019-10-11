package io.github.laplacedemon.fandb;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class KVLog {
    private FileChannel appendFileChannel;
    private FileChannel readFileChannel;
    private Lock appendLock;
    private SequenceGenerator sequenceGenerator;

    public KVLog(String pathname) throws IOException {
        File file = new File(pathname);
        Path path = file.toPath();
        this.appendFileChannel = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        this.readFileChannel = FileChannel.open(path, StandardOpenOption.READ);
        this.appendLock = new ReentrantLock();
        this.sequenceGenerator = new SequenceGenerator();
    }
    
    /**
     * position (before writing data)
     *    |
     *    [ key.length | key | value.length | value | sn ]
     *                                      |
     *                                 value offset
     * @param key
     * @param value
     * @return the value's offset
     * @throws IOException
     */
    public long append(byte[] key, byte[] value) throws IOException {
        int logDataSize = 0;
        logDataSize += (4 + 4);
        logDataSize += key.length;
        logDataSize += value.length;
        logDataSize += 8;
        
        ByteBuffer messageBuf = ByteBuffer.allocate(logDataSize);
        
        messageBuf.putInt(key.length);
        messageBuf.put(key);
        messageBuf.putInt(value.length);
        messageBuf.put(value);
        
        // "get position"与"append write"两个动作的组合必须是原子的
        long position = 0l;
        try {
            appendLock.lock();
            long nextSequenceId = sequenceGenerator.nextSequenceId();
            messageBuf.putLong(nextSequenceId);
            messageBuf.flip();
            position = appendFileChannel.position();
            appendFileChannel.write(messageBuf);
            appendFileChannel.force(true);
        } finally {
            appendLock.unlock();
        }
        
        long valueOffset = position + 4 + key.length + 4;
        return valueOffset;
    }

    public byte[] getValue(long valueOffset, int valueSize) throws IOException {
        ByteBuffer valueBuffer = ByteBuffer.allocate(valueSize);
        this.readFileChannel.read(valueBuffer, valueOffset);
        valueBuffer.flip();
        return valueBuffer.array();
    }
    
    public long scanIntoMemtable(MemTable memtable) throws IOException {
        int size = 1024*256;  // 256kb
        int readOffset = 0;
        while(true) {
            ByteBuffer fileBuffer = ByteBuffer.allocate(size);
            int readBytes = this.readFileChannel.read(fileBuffer, 0);
            if (readBytes == -1) {
                return 0;
            }
            fileBuffer.flip();
            if (readBytes > 0) {
                // in memory
                while(true) {
                    if(readOffset >= readBytes) {
                        return 0l;
                    }
                    
                    // read key
                    int keyLength = fileBuffer.getInt(); // read key length
                    byte[] keyBytes = new byte[keyLength];
                    fileBuffer.get(keyBytes);  // read key bytes
                    
                    // read key offset 
                    readOffset += (4 + keyLength);
                    
                    int valueLengthOffset = readOffset;
                    int valueOffset = valueLengthOffset + 4;
                    int valueLength = fileBuffer.getInt();  // read value length
                    readOffset +=(4 + valueLength);
                    long sn = fileBuffer.getLong(readOffset);         // read int, +4
                    readOffset += 8;
                    fileBuffer.position(readOffset);
                    if(sn > 0) {
                        memtable.put(keyBytes, valueOffset, valueLength);
                    }
                }
            }
        }
        
    }

}
