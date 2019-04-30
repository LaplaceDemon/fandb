package io.github.laplacedemon.fandb;

import java.util.concurrent.atomic.AtomicLong;

/**
 * max long is 9223372036854775807.
 * if write tps : 1kw/s.
 * 29247 year will over.
 * @author zhuoyun
 *
 */
public class SequenceGenerator {
    private AtomicLong lastSequenceId;
    public SequenceGenerator() {
        this.lastSequenceId = new AtomicLong(0);
    }
    
    public long nextSequenceId() {
        return lastSequenceId.decrementAndGet();
    }
    
    public void init(long id) {
        this.lastSequenceId.set(id);
    }
}
