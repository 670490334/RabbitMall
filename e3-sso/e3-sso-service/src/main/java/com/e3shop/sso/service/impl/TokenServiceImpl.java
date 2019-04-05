package com.e3shop.sso.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.e3shop.common.jedis.JedisClient;
import com.e3shop.common.utils.E3Result;
import com.e3shop.common.utils.JsonUtils;
import com.e3shop.pojo.TbUser;
import com.e3shop.sso.service.TokenService;
/**
 * 根据token到redis中取用户信息
 * @author Admin
 *
 */
@Service
public class TokenServiceImpl implements TokenService {

	@Value("${EXPIRE}")
	private Integer EXPIRE;
	@Value("${USER_INFO}")
	private String USER_INFO;
	@Autowired
	private JedisClient jedisClient;
	@Override
	public E3Result getUserByToken(String token) {
		// 用token取redis取用户信息，session共享
		String json = jedisClient.get(USER_INFO+":"+token);
		//取不到用户信息，返回已过期
		if(StringUtils.isBlank(json)) {
			return E3Result.build(400, "用户登录信息已过期，请重新登录");
		}
		//取到用户信息
		TbUser tbUser = JsonUtils.jsonToPojo(json, TbUser.class);
		//刷新redis过期时间
		jedisClient.expire(USER_INFO+":"+token, EXPIRE);
		return E3Result.ok(tbUser);
	}

}
