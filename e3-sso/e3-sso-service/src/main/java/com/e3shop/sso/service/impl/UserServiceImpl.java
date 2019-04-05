package com.e3shop.sso.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.e3shop.common.jedis.JedisClient;
import com.e3shop.common.utils.E3Result;
import com.e3shop.common.utils.IDUtils;
import com.e3shop.common.utils.JsonUtils;
import com.e3shop.mapper.TbUserMapper;
import com.e3shop.pojo.TbUser;
import com.e3shop.pojo.TbUserExample;
import com.e3shop.pojo.TbUserExample.Criteria;
import com.e3shop.sso.service.UserService;
@Service
public class UserServiceImpl implements UserService {

	@Value("${EXPIRE}")
	private Integer EXPIRE;
	@Value("${USER_INFO}")
	private String USER_INFO;
	@Autowired
	private JedisClient jedisClient;
	@Autowired
	private TbUserMapper tbUserMapper;
	@Override
	public E3Result checkData(String param, int type) {
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		switch (type) {
		case 1:
			criteria.andUsernameEqualTo(param);
			break;
		case 2:
			criteria.andPhoneEqualTo(param);
			break;
		case 3:
			criteria.andEmailEqualTo(param);
			break;
		default:
			return E3Result.build(400, "非法参数");
		}
		List<TbUser> list = tbUserMapper.selectByExample(example);
		if (list==null && list.size()==0) {
			return E3Result.ok(false);
		}else {
			return E3Result.ok(true);
		}
	}
	@Override
	public E3Result registUser(TbUser tbUser) {
		if(StringUtils.isBlank(tbUser.getUsername())) {
			return E3Result.build(400, "用户名不能为空");
		}
		if(StringUtils.isBlank(tbUser.getPassword())) {
			return E3Result.build(400, "密码不能为空");
		}
		//检验用户名是否有效
		E3Result result = checkData(tbUser.getUsername(), 1);
		if (!(boolean)result.getData()) {
			return E3Result.build(400, "用户名已被注册");
		}
		//校验电话是否可用
		if (StringUtils.isNotBlank(tbUser.getPhone())) {
			result = checkData(tbUser.getPhone(), 2);
			if (!(boolean)result.getData()) {
				return E3Result.build(400, "手机号已被注册");
			}
		}
		//校验邮箱是否可用
		if (StringUtils.isNotBlank(tbUser.getEmail())) {
			result = checkData(tbUser.getEmail(), 2);
			if (!(boolean)result.getData()) {
				return E3Result.build(400, "邮箱已被注册");
			}
		}
		//密码加密
		String md5Pass = DigestUtils.md5DigestAsHex(tbUser.getPassword().getBytes());
		tbUser.setPassword(md5Pass);
		// 生成id
		tbUser.setCreated(new Date());
		tbUser.setUpdated(new Date());
		tbUserMapper.insert(tbUser);
		return E3Result.ok();
	}
	@Override
	public E3Result login(String username, String password) {
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(username);
		List<TbUser> list = tbUserMapper.selectByExample(example);
		//监测用户是否存在
		if(list == null || list.size() == 0) {
			return E3Result.build(400, "用户名或密码错误");
		}
		TbUser tbUser = list.get(0);
		//监测密码
		if(!DigestUtils.md5DigestAsHex(password.getBytes()).equals(tbUser.getPassword())) {
			return E3Result.build(400, "密码错误");
		}
		//登录成功之后使用token
		String token = UUID.randomUUID().toString();
		// 3、把用户信息保存到redis。Key就是token，value就是TbUser对象转换成json。
		// 4、使用String类型保存Session信息。可以使用“前缀:token”为key
		tbUser.setPassword(null);
		jedisClient.set(USER_INFO+":"+token, JsonUtils.objectToJson(tbUser));
		//设置过期时间
		jedisClient.expire(USER_INFO+":"+token, EXPIRE);
		//使用e3Result包装token
		return E3Result.ok(token);
	}


}
