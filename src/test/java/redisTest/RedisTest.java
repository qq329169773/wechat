package redisTest;

import com.ray.basic.data.redis.RedisClientStringTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by zhangrui25 on 2017/1/15.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-context.xml")
public class RedisTest {

    @Autowired
    private RedisClientStringTemplate redisClientStringTemplate ;

    @Test
    public void initTest(){
        redisClientStringTemplate.set("name","lisi");
        System.out.println(redisClientStringTemplate.get("name"));
    }
}

