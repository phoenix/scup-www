package edu.scup.data.redis;

public interface JedisStatisticsMXBean {

    long getCacheHits();

    long getCacheMisses();
}
