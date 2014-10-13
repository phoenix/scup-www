package edu.scup.data.redis;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.BuilderFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.management.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

public class JedisUtil {
    private static final Logger logger = LoggerFactory.getLogger(JedisUtil.class);
        private static final String DEFAULT_CHARSET = "UTF-8";
        private MasterSlavePool jedisPool;
        private JedisStatistics jedisStatistics = new JedisStatistics();
        private ObjectName oname;

        /**
         * @see redis.clients.jedis.Jedis#smembers(String)
         */
        public Set<String> smembers(String key) {
            try (Jedis jedis = jedisPool.getReadableResource()) {
                return jedis.smembers(key);
            }
        }

        /**
         * @see redis.clients.jedis.Jedis#sadd(String, String...)
         */
        public long sadd(String key, String... members) {
            try (Jedis jedis = jedisPool.getMasterResource()) {
                return jedis.sadd(key, members);
            }
        }

        /**
         * @see redis.clients.jedis.Jedis#sinter(String...)
         */
        public Set<String> sinter(final String... keys) {
            try (Jedis jedis = jedisPool.getReadableResource()) {
                return jedis.sinter(keys);
            }
        }

        /**
         * @see redis.clients.jedis.Jedis#srem(String, String...)
         */
        public Long srem(final String key, final String... members) {
            try (Jedis jedis = jedisPool.getMasterResource()) {
                return jedis.srem(key, members);
            }
        }

        /**
         * @see redis.clients.jedis.Jedis#lrange(String, long, long)
         */
        public List<byte[]> lrange(final String key, final long start, final long end) {
            try (Jedis jedis = jedisPool.getReadableResource()) {
                return jedis.lrange(key.getBytes(), start, end);
            }
        }

        /**
         * @see redis.clients.jedis.Jedis#lrange(String, long, long)
         */
        public List<String> lrangeString(final String key, final long start, final long end) {
            return BuilderFactory.STRING_LIST.build(lrange(key, start, end));
        }

        /**
         * @see redis.clients.jedis.Jedis#rpush(byte[], byte[]...)
         */
        public void rpush(final String key, List<byte[]> bytes) {
            try (Jedis jedis = jedisPool.getMasterResource()) {
                for (byte[] data : bytes) {
                    jedis.rpush(key.getBytes(), data);
                }
            }
        }

        /**
         * @see redis.clients.jedis.Jedis#set(String, String)
         */
        public String set(final String key, String value) {
            try (Jedis jedis = jedisPool.getMasterResource()) {
                return jedis.set(key, value);
            } catch (Exception e) {
                logger.error("set value error", e);
                return null;
            }
        }

        /**
         * @see redis.clients.jedis.Jedis#setex(String, int, String)
         */
        public String setex(final String key, final int seconds, final String value) {
            try (Jedis jedis = jedisPool.getMasterResource()) {
                return jedis.setex(key, seconds, value);
            }
        }

        /**
         * @see redis.clients.jedis.Jedis#setnx(String, String)
         */
        public Long setnx(final String key, final String value) {
            try (Jedis jedis = jedisPool.getMasterResource()) {
                return jedis.setnx(key, value);
            }
        }

        public Long setnx(final String key, final String value, int seconds) {
            try (Jedis jedis = jedisPool.getMasterResource()) {
                Long result = jedis.setnx(key, value);
                if (result == 1) {
                    jedis.expire(key, seconds);
                }
                // 为了防止没有设置过期时间的key存在
                if (jedis.ttl(key) == -1) {
                    jedis.expire(key, seconds);
                }
                return result;
            } catch (Exception e) {
                logger.error("set value error", e);
                return 0L;
            }
        }

        /**
         * @see redis.clients.jedis.Jedis#del(String...)
         */
        public Long del(final String... keys) {
            try (Jedis jedis = jedisPool.getMasterResource()) {
                return jedis.del(keys);
            }
        }

        /**
         * @see redis.clients.jedis.Jedis#get(String)
         */
        public String get(final String key) {
            try (Jedis jedis = jedisPool.getReadableResource()) {
                return jedis.get(key);
            }
        }

        /**
         * 获取key对应的 value 值，如果没有，则执行 ifAbsent 然后缓存并返回结果
         *
         * @param key           redis key
         * @param ifAbsent      如果缓存中不存在，则根据这个获取值并缓存
         * @param expireSeconds 过期时间
         * @return value
         */
        public String get(final String key, final Callable<String> ifAbsent, final int expireSeconds) {
            try (Jedis jedis = jedisPool.getReadableResource()) {
                String value = jedis.get(key);
                if (value == null) {
                    jedisStatistics.commandGetExecuted(false);
                    try {
                        value = ifAbsent.call();
                        if (value != null) {
                            setex(key, expireSeconds, value);
                        }
                    } catch (Exception e) {
                        logger.error("", e);
                    }
                } else {
                    jedisStatistics.commandGetExecuted(true);
                }
                return value;
            }
        }

        /**
         * @see redis.clients.jedis.Jedis#getSet(String, String)
         */
        public String getSet(final String key, final String value) {
            try (Jedis jedis = jedisPool.getMasterResource()) {
                return jedis.getSet(key, value);
            }
        }

        /**
         * @see redis.clients.jedis.Jedis#keys(String)
         */
        public Set<String> keys(final String pattern) {
            try (Jedis jedis = jedisPool.getReadableResource()) {
                return jedis.keys(pattern);
            }
        }

        /**
         * @see redis.clients.jedis.Jedis#incr(String)
         */
        public Long incr(final String key) {
            try (Jedis jedis = jedisPool.getMasterResource()) {
                return jedis.incr(key);
            }
        }

        /**
         * @see redis.clients.jedis.Jedis#incrBy(String, long)
         */
        public Long incrBy(final String key, long integer) {
            try (Jedis jedis = jedisPool.getMasterResource()) {
                return jedis.incrBy(key, integer);
            }
        }

        /**
         * @see redis.clients.jedis.Jedis#expire(String, int)
         */
        public Long expire(final String key, final int seconds) {
            try (Jedis jedis = jedisPool.getMasterResource()) {
                return jedis.expire(key, seconds);
            }
        }

        /**
         * @see redis.clients.jedis.Jedis#expire(byte[], int)
         */
        public Long expire(final byte[] key, final int seconds) {
            try (Jedis jedis = jedisPool.getMasterResource()) {
                return jedis.expire(key, seconds);
            }
        }

        /**
         * @see redis.clients.jedis.Jedis#mget(String...)
         */
        public List<String> mget(final String... keys) {
            try (Jedis jedis = jedisPool.getReadableResource()) {
                return jedis.mget(keys);
            }
        }

        /**
         * @see redis.clients.jedis.Jedis#exists(String)
         */
        public Boolean exists(final String key) {
            try (Jedis jedis = jedisPool.getReadableResource()) {
                return jedis.exists(key);
            }
        }

        /**
         * @see redis.clients.jedis.Jedis#ttl(String)
         */
        public Long ttl(final String key) {
            try (Jedis jedis = jedisPool.getReadableResource()) {
                return jedis.ttl(key);
            }
        }

        /**
         * @see redis.clients.jedis.Jedis#zincrby(String, double, String)
         */
        public Double zincrby(final String key, final double score, final String member) {
            try (Jedis jedis = jedisPool.getMasterResource()) {
                return jedis.zincrby(key, score, member);
            }
        }

        /**
         * @see redis.clients.jedis.Jedis#zrangeWithScores(String, long, long)
         */
        public Set<Tuple> zrangeWithScores(final String key, final long start, final long end) {
            try (Jedis jedis = jedisPool.getReadableResource()) {
                return jedis.zrangeWithScores(key, start, end);
            }
        }

        /**
         * @see redis.clients.jedis.Jedis#zinterstore(String, String...)
         */
        public void zUnionStore(final String destinationKey, final String... sets) {
            try (Jedis jedis = jedisPool.getMasterResource()) {
                jedis.zunionstore(destinationKey, sets);
            }
        }

        /**
         * set Object to redis
         *
         * @param key
         * @param value
         * @return Status code reply
         * @throws java.io.IOException
         */
        public String setObject(final String key, Object value, final int seconds) {
            try (Jedis jedis = jedisPool.getMasterResource()) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(value);
                return jedis.setex(key.getBytes(DEFAULT_CHARSET), seconds, baos.toByteArray());
            } catch (Exception e) {
                logger.error("", e);
            }
            return null;
        }

        public String setJson(final String key, Object value, final int seconds) {
            try (Jedis jedis = jedisPool.getMasterResource()) {
                return jedis.setex(key.getBytes(DEFAULT_CHARSET), seconds, JSON.toJSONBytes(value));
            } catch (Exception e) {
                logger.error("", e);
            }
            return null;
        }

        public long setList(final String key, final int seconds, List<String> values) {
            long len = 0;
            try (Jedis jedis = jedisPool.getMasterResource()) {
                for (String str : values) {
                    len = jedis.lpush(key, str);
                }
                jedis.expire(key, seconds);
            } catch (Exception e) {
                logger.error("", e);
            }
            return len;
        }

        /**
         * get Object from redis
         *
         * @param key
         * @return Object
         */
        @SuppressWarnings("unchecked")
        public <T> T getObject(final String key) {
            try (Jedis jedis = jedisPool.getReadableResource()) {
                byte[] bt = jedis.get(key.getBytes(DEFAULT_CHARSET));
                if (bt != null && bt.length > 0) {
                    ByteArrayInputStream bais = new ByteArrayInputStream(bt);
                    ObjectInputStream ois = new ObjectInputStream(bais);
                    return (T) ois.readObject();
                } else {
                    return null;
                }
            } catch (Exception e) {
                logger.error("", e);
            }
            return null;
        }

        public <T> T getObjectFromJson(final String key, Class<? extends T> clz) {
            try (Jedis jedis = jedisPool.getReadableResource()) {
                byte[] bytes = jedis.get(key.getBytes(DEFAULT_CHARSET));
                if (bytes != null && bytes.length > 1) {
                    return JSON.parseObject(bytes, clz);
                } else {
                    return null;
                }
            } catch (Exception e) {
                logger.error("", e);
            }
            return null;
        }

        public <T> T getObjectFromJson(final String key, Class<? extends T> clz, final Callable<T> ifAbsent, final int expireSeconds) {
            try (Jedis jedis = jedisPool.getReadableResource()) {
                byte[] bytes = jedis.get(key.getBytes(DEFAULT_CHARSET));
                if (bytes != null && bytes.length > 1) {
                    jedisStatistics.commandGetExecuted(true);
                    return JSON.parseObject(bytes, clz);
                } else {
                    jedisStatistics.commandGetExecuted(false);
                    T value = ifAbsent.call();
                    if (value != null) {
                        setJson(key, value, expireSeconds);
                        return value;
                    }
                }
            } catch (Exception e) {
                logger.error("", e);
            }
            return null;
        }

        public <T> List<T> getListFromJson(final String key, Class<T> clz, final Callable<List<T>> ifAbsent, final int expireSeconds) {
            try (Jedis jedis = jedisPool.getReadableResource()) {
                byte[] bytes = jedis.get(key.getBytes(DEFAULT_CHARSET));
                if (bytes != null && bytes.length > 1) {
                    jedisStatistics.commandGetExecuted(true);
                    return JSON.parseArray(new String(bytes, DEFAULT_CHARSET), clz);
                } else {
                    jedisStatistics.commandGetExecuted(false);
                    List<T> value = ifAbsent.call();
                    if (value != null) {
                        setJson(key, value, expireSeconds);
                        return value;
                    }
                }
            } catch (Exception e) {
                logger.error("", e);
            }
            return null;
        }

        @PostConstruct
        private void jmxRegister() {
            String jmxNameBase = "redis.pool:redis=" + this.jedisPool.getRedisName() + ",name=";
            String jmxNamePrefix = "statistics";
            ObjectName objectName = null;
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            int i = 1;
            boolean registered = false;
            while (!registered) {
                try {
                    ObjectName objName;
                    // Skip the numeric suffix for the first pool in case there is
                    // only one so the names are cleaner.
                    if (i == 1) {
                        objName = new ObjectName(jmxNameBase + jmxNamePrefix);
                    } else {
                        objName = new ObjectName(jmxNameBase + jmxNamePrefix + i);
                    }
                    mbs.registerMBean(this.jedisStatistics, objName);
                    objectName = objName;
                    registered = true;
                } catch (InstanceAlreadyExistsException e) {
                    // Increment the index and try again
                    i++;
                } catch (MalformedObjectNameException | MBeanRegistrationException | NotCompliantMBeanException e) {
                    // Shouldn't happen. Skip registration if it does.
                    registered = true;
                }
            }
            oname = objectName;
        }

        @PreDestroy
        private void jmxUnregister() {
            if (oname != null) {
                try {
                    ManagementFactory.getPlatformMBeanServer().unregisterMBean(
                            oname);
                } catch (MBeanRegistrationException | InstanceNotFoundException e) {
                    logger.error("jmxUnregister error", e);
                }
            }
        }

        public void setJedisPool(MasterSlavePool jedisPool) {
            this.jedisPool = jedisPool;
        }
}
