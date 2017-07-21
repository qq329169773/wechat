package com.ray.basic.data.redis;

import com.ray.basic.data.RedisDataSource;
import com.ray.basic.exceptions.RedisOperationException;
import com.ray.basic.model.HoldDoubleValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.ShardedJedis;

import java.util.List;

/**
 * Created by zhangrui25 on 2016/11/22.
 */


/*@Repository("redisClientStringTemplate")*/
public class RedisClientStringTemplate {


    private final static String SET_OK = "OK";

    @Autowired
    private RedisDataSource redisDataSource;

    public void disconnect() {
        ShardedJedis shardedJedis = redisDataSource.getRedisClient();
        shardedJedis.disconnect();
    }


    /**
     * 设置单个值
     *
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, String value) {
        String result = null;
        ShardedJedis shardedJedis = redisDataSource.getRedisClient();
        if (shardedJedis == null) {
            return false;
        }
        boolean broken = false;
        try {
            result =   shardedJedis.set(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            broken = true;
        } finally {
            redisDataSource.returnResource(shardedJedis, broken);
        }
        if (SET_OK.equals(result)) {
            return true;
        }
        return false;
    }


    /**
     * 获取单个值
     *
     * @param key
     * @return
     */
    public String get(String key) {
        String result = null;
        ShardedJedis shardedJedis = redisDataSource.getRedisClient();
        if (shardedJedis == null) {
            return result;
        }
        boolean broken = false;
        try {
            result = shardedJedis.get(key);
        } catch (Exception e) {
            broken = true;
        } finally {
            redisDataSource.returnResource(shardedJedis, broken);
        }
        return result;
    }


}
