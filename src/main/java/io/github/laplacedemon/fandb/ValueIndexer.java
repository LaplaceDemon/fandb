package io.github.laplacedemon.fandb;

public final class ValueIndexer {
    private int fileId;
    private long offset;
    private int size;

    public ValueIndexer(long offset, int size) {
        super();
        this.offset = offset;
        this.size = size;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
    
}
