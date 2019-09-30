package io.github.laplacedemon.fandb;

public final class ValueIndexer {
    private int fileId;
    private long offset;
    private long size;

    public ValueIndexer(long offset, long size) {
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

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

}
