package io.github.laplacedemon.fandb;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class KVLog {
    private FileChannel appendFileChannel;
    private FileChannel readFileChannel;

    public KVLog(String pathname) throws IOException {
        File file = new File(pathname);
        Path path = file.toPath();
        this.appendFileChannel = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        this.readFileChannel = FileChannel.open(path, StandardOpenOption.READ);
    }
    
    /**
     * 
     * [key.length | key | value.length |value]
     * @param key
     * @param value
     * @return
     * @throws IOException
     */
    public long append(byte[] key, byte[] value) throws IOException {
        long position;
        try {
            position = appendFileChannel.position();
        } catch (IOException e) {
            throw e;
        }
        
        int logDataSize = 0;
        logDataSize += 8;
        logDataSize += key.length;
        logDataSize += value.length;
        
        ByteBuffer messageBuf = ByteBuffer.allocate(logDataSize);
        
        messageBuf.putInt(key.length);
        messageBuf.put(key);
        messageBuf.putInt(value.length);
        messageBuf.put(value);
        messageBuf.flip();
        
        int write = appendFileChannel.write(messageBuf);
        appendFileChannel.force(true);
        long valueOffset = position + 4 + key.length;
        return valueOffset;
    }

    public byte[] getValue(long valueOffset) throws IOException {
        ByteBuffer valueLengthBuffer = ByteBuffer.allocate(4);
        this.readFileChannel.read(valueLengthBuffer, valueOffset);
        valueLengthBuffer.flip();
        int valueLength = valueLengthBuffer.getInt();
        ByteBuffer valueBuffer = ByteBuffer.allocate(valueLength);
        this.readFileChannel.read(valueBuffer, valueOffset+4);
        valueBuffer.flip();
        return valueBuffer.array();
    }
    
    public long scanIntoMemtable(MemTable memtable) throws IOException {
        int size = 1024*256;  // 256kb
        int readOffset = 0;
        while(true) {
            ByteBuffer fileBuffer = ByteBuffer.allocate(size);
            int readBytes = this.readFileChannel.read(fileBuffer, 0);
            fileBuffer.flip();
            if (readBytes > 0) {
                // in memory
                while(true) {
                    if(readOffset >= readBytes) {
                        return 0l;
                    }
                    
                    int keyLength = fileBuffer.getInt(); // read int, +4
                    byte[] keyBytes = new byte[keyLength];
                    fileBuffer.get(keyBytes);  // read bytes, +keyLength
                    readOffset += (4 + keyLength);
                    
                    int valueOffset = readOffset;
                    
                    int valueLength = fileBuffer.getInt();  // read int, +4
                    readOffset +=(4 + valueLength);
                    fileBuffer.position(readOffset);
                    
                    memtable.put(keyBytes, valueOffset);
                }
                
            }
        }
        
    }

    public long append(long seqid, byte[] key, byte[] value) {
        return 0;
    }

}
