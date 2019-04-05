package com.e3shop.redisTest;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.e3shop.common.jedis.JedisClient;

public class JedisClientTest {

	@Test
	public void testJedisClient() throws Exception{
		//初始化spring容器
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-redis.xml");
		//从容器中获得bean
		JedisClient jedisClient = applicationContext.getBean(JedisClient.class);
		jedisClient.set("mytest1", "haha");
		String string = jedisClient.get("mytest1");
		System.out.println(string);
	}
}
