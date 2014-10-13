package edu.scup.data.redis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class MasterSlavePool {

    private static final Logger logger = LoggerFactory.getLogger(MasterSlavePool.class);
    protected JedisPool masterPool;
    protected JedisPool slavePool;
    private String redisName = "default";
    private int masterReadPercent = 50;

    protected GenericObjectPoolConfig poolConfig;
    protected String masterHost;
    protected int masterPort;
    protected int timeout;

    /**
     * Using this constructor means you have to set and initialize the
     * internalPool yourself.
     */
    public MasterSlavePool() {
    }

    public MasterSlavePool(final GenericObjectPoolConfig poolConfig, final String host, final int port, final int timeout) throws Exception {
        this.poolConfig = poolConfig;
        this.masterHost = host;
        this.masterPort = port;
        this.timeout = timeout;
    }

    @PostConstruct
    public void initPool() throws Exception {
        if (this.masterPool != null) {
            try {
                closeInternalPool();
            } catch (Exception ignored) {
            }
        }

        String clientName = System.getProperty("app.name");
        poolConfig.setJmxNameBase("redis.pool:redis=" + redisName + ",name=");
        poolConfig.setJmxNamePrefix("master");
        this.masterPool = new JedisPool(poolConfig, masterHost, masterPort, timeout, null, Protocol.DEFAULT_DATABASE, clientName);

        Jedis jedis = masterPool.getResource();
        String replication = jedis.info("Replication");
        jedis.close();

        Map<String, String> redisInfo = new HashMap<>();
        for (String s : replication.split("\r\n")) {
            String[] info = s.split(":");
            if (info.length == 2) {
                redisInfo.put(info[0], info[1]);
            }
        }
        if ("master".equals(redisInfo.get("role"))) {
            int slaves = Integer.parseInt(redisInfo.get("connected_slaves"));
            for (int i = 0; i < slaves; i++) {
                String[] slave = redisInfo.get("slave" + i).split(",");
                if (slave.length == 3 && "online".equals(slave[2])) {
                    String slaveHost = slave[0];
                    String slavePort = slave[1];
                    GenericObjectPoolConfig slavePoolConfig = poolConfig.clone();
                    slavePoolConfig.setJmxNamePrefix("slave");
                    slavePool = new JedisPool(slavePoolConfig, slaveHost, Integer.parseInt(slavePort)
                            , timeout, null, Protocol.DEFAULT_DATABASE, clientName);
                    logger.info("created redis slave pool {}:{}", slaveHost, slavePort);
                    break;
                }
            }
        }
    }

    /**
     * 获取redis master
     *
     * @return master only
     */
    public Jedis getMasterResource() {
        try {
            return masterPool.getResource();
        } catch (Exception e) {
            throw new JedisConnectionException(
                    "Could not get a resource from the pool", e);
        }
    }

    /**
     * 随机获取主节点或者从节点
     *
     * @return master or slave
     */
    public Jedis getReadableResource() {
        Jedis rt = null;
        if (slavePool == null || ThreadLocalRandom.current().nextInt(100) < masterReadPercent) {
            try {
                rt = masterPool.getResource();
            } catch (Exception e) {
                logger.error("get read jedis from jedisMasterPool error,try get from slave", e);
            }
        }

        if (rt == null && slavePool != null) {
            try {
                rt = slavePool.getResource();
            } catch (Exception e) {
                logger.error("get read jedis from jedisSlavePool error ,try get from master", e);
                try {
                    rt = masterPool.getResource();
                } catch (Exception e1) {
                    throw new JedisConnectionException("Could not get a resource from the pool", e1);
                }

            }
        }

        if (rt == null) {
            throw new JedisConnectionException("Could not get a resource from the pool");
        }
        return rt;
    }

    @PreDestroy
    public void destroy() {
        closeInternalPool();
    }

    protected void closeInternalPool() {
        try {
            if (masterPool != null) {
                masterPool.destroy();
            }
            if (slavePool != null) {
                slavePool.destroy();
            }
        } catch (Exception e) {
            throw new JedisException("Could not destroy the pool", e);
        }
    }

    public int getMasterReadPercent() {
        return masterReadPercent;
    }

    public void setMasterReadPercent(int masterReadPercent) {
        this.masterReadPercent = masterReadPercent;
    }

    public String getRedisName() {
        return redisName;
    }

    /**
     * 用来在jmx中区别多个redis的名称
     *
     * @param redisName 该redis的描述名称
     */
    public void setRedisName(String redisName) {
        this.redisName = redisName;
    }
}
