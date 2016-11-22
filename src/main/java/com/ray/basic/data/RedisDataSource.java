package com.ray.basic.data;

import redis.clients.jedis.ShardedJedis;

/**
 * Created by zhangrui25 on 2016/11/22.
 */
public interface RedisDataSource {

    public abstract ShardedJedis getRedisClient();

    public void returnResource(ShardedJedis shardedJedis);

    public void returnResource(ShardedJedis shardedJedis, boolean broken);
}
