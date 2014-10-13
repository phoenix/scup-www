package edu.scup.data.redis;

import java.util.concurrent.atomic.AtomicLong;

public class JedisStatistics implements JedisStatisticsMXBean {

    private final AtomicLong cacheHits = new AtomicLong();
    private final AtomicLong cacheMisses = new AtomicLong();

    public void commandGetExecuted(boolean isHits) {
        if (isHits) {
            cacheHits.incrementAndGet();
        } else {
            cacheMisses.incrementAndGet();
        }
    }

    @Override
    public long getCacheHits() {
        return cacheHits.longValue();
    }

    @Override
    public long getCacheMisses() {
        return cacheMisses.longValue();
    }
}
