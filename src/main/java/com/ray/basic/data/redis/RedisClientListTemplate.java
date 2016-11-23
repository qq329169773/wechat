package com.ray.basic.data.redis;

import com.ray.basic.data.RedisDataSource;
import com.ray.basic.exceptions.RedisOperationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.ShardedJedis;

import java.util.List;

/**
 * Created by zhangrui25 on 2016/11/22.
 */
@Repository("redisClientListTemplate")
public class RedisClientListTemplate {

    private final static String SET_OK = "OK";

    @Autowired
    private RedisDataSource redisDataSource;

    public void disconnect() {
        ShardedJedis shardedJedis = redisDataSource.getRedisClient();
        shardedJedis.disconnect();
    }

    /**
     * List 左边添加元素
     * @param key
     * @param list
     * @return
     */
    public Long addLeftList(String key, String... list) {
        ShardedJedis shardedJedis = null;
        boolean broken = false;
        Long result = 0L;
        try {
            shardedJedis = redisDataSource.getRedisClient();

            result = shardedJedis.lpush(key, list);
        } catch (Exception e) {
            broken = true;
            throw new RedisOperationException("RedisClientTemplate->addLeftList", e);
        } finally {
            redisDataSource.returnResource(shardedJedis, broken);
            return result;
        }
    }

    /**
     * List 右边添加元素
     * @param key
     * @param list
     * @return
     */
    public Long addRightList(String key, String... list) {
        ShardedJedis shardedJedis = null;
        boolean broken = false;
        Long result = 0L;
        try {
            shardedJedis = redisDataSource.getRedisClient();
            result = shardedJedis.rpush(key, list);
        } catch (Exception e) {
            broken = true;
            throw new RedisOperationException("RedisClientTemplate->addRightList", e);
        } finally {
            redisDataSource.returnResource(shardedJedis, broken);
            return result;
        }
    }
    public List<String> getList(String key, Long start , Long end) {
        ShardedJedis shardedJedis = null;
        boolean broken = false;
        List<String> result = null;
        try {
            shardedJedis = redisDataSource.getRedisClient();
            result = shardedJedis.lrange(key,start,end);
        } catch (Exception e) {
            broken = true;
            throw new RedisOperationException("RedisClientTemplate->getList", e);
        } finally {
            redisDataSource.returnResource(shardedJedis, broken);
            return result;
        }
    }
    public List<String> getListAll(String key){
        return getList(key,0L,-1L);
    }

}
