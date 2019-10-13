package io.github.laplacedemon.fandb;

import java.util.concurrent.atomic.AtomicLong;

/**
 * the first number is 1. <br>
 * max long is 9223372036854775807.<br>
 * if write tps : 1kw/s.<br>
 * 29247 year will over.<br>
 * @author zhuoyun
 */
public class SequenceGenerator {
    private AtomicLong lastSequenceId;
    
    public SequenceGenerator() {
        this.lastSequenceId = new AtomicLong(0);
    }
    
    public SequenceGenerator(final long id) {
        this.lastSequenceId = new AtomicLong(id);
    }
    
    public long nextSequenceId() {
        return lastSequenceId.incrementAndGet();
    }
    
}