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
    private Lock appendLock;
    private Path path;
    private SequenceGenerator sequenceGenerator;

    public KVLog(String pathname) throws IOException {
        this(new File(pathname));
    }

    public KVLog(File file) throws IOException {
        this.path = file.toPath();
        this.appendFileChannel = FileChannel.open(this.path, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        this.appendLock = new ReentrantLock();
        this.sequenceGenerator = new SequenceGenerator();
    }

    /**
     * position (before writing data) | [ key.length | key | value.length | value | sn ] | value offset
     * 
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
            // first number is 1
            long sn = sequenceGenerator.nextSequenceId();
            messageBuf.putLong(sn);
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
        try (FileChannel readFileChannel = FileChannel.open(this.path, StandardOpenOption.READ)) {
            readFileChannel.read(valueBuffer, valueOffset);
        }
        valueBuffer.flip();
        return valueBuffer.array();
    }

    public long scanIntoMemtable(MemTable memtable) throws IOException {
        int size = 1024 * 256; // 256kb
        int readOffset = 0;
        long lastSN = 0l;
        while (true) {
            ByteBuffer fileBuffer = ByteBuffer.allocate(size);
            int readBytes;
            try (FileChannel readFileChannel = FileChannel.open(this.path, StandardOpenOption.READ)) {
                readBytes = readFileChannel.read(fileBuffer, 0);
            }

            if (readBytes == -1) {
                return 0;
            }
            fileBuffer.flip();
            if (readBytes > 0) {
                // in memory
                while (true) {
                    if (readOffset >= readBytes) {
                        return lastSN;
                    }

                    // read key
                    int keyLength = fileBuffer.getInt(); // read key length
                    byte[] keyBytes = new byte[keyLength];
                    fileBuffer.get(keyBytes); // read key bytes

                    // read key offset
                    readOffset += (4 + keyLength);

                    int valueLengthOffset = readOffset;
                    int valueOffset = valueLengthOffset + 4;
                    int valueLength = fileBuffer.getInt(); // read value length
                    readOffset += (4 + valueLength);
                    lastSN = fileBuffer.getLong(readOffset); // read int, +4
                    readOffset += 8;
                    fileBuffer.position(readOffset);

                    // lastSN == 0, the row's data has been deleted
                    if (lastSN > 0) {
                        memtable.put(keyBytes, valueOffset, valueLength);
                    }
                }
            }
        }
    }

    public void close() throws IOException {
        this.appendFileChannel.close();
    }

    public void clean(long offset, int size) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(size);
        try (FileChannel fileChannel = FileChannel.open(this.path, StandardOpenOption.WRITE)) {
            fileChannel.position(offset);
            fileChannel.write(buf);
            fileChannel.force(true);
        }
    }

}
