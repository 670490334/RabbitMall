package com.e3shop.sso.controller;
/**
 * 根据token查询Controller
 * @author Admin
 *
 */

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.e3shop.common.utils.E3Result;
import com.e3shop.common.utils.JsonUtils;
import com.e3shop.sso.service.TokenService;
/**
 * 根据token取用户信息
 * 使用jsonp来跨域处理
 * @author Admin
 *
 */
@Controller
public class TokenController {

	@Autowired
	private TokenService tokenService;
	
	//方式一
	//produces=MediaType.APPLICATION_JSON_UTF8_VALUE  想要contentType为json，
	/*
	 * @RequestMapping(value = "/user/token/{token}" ,
	 * produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	 * 
	 * @ResponseBody public String gertUserByTokem(@PathVariable String token ,
	 * String callback) { E3Result result = tokenService.getUserByToken(token);
	 * //判断其是否为jsonp请求 if(StringUtils.isNotBlank(callback)) { return callback +"(" +
	 * JsonUtils.objectToJson(result) + ");"; } return
	 * JsonUtils.objectToJson(result); }
	 */
	//方式二
	@RequestMapping(value = "/user/token/{token}")
	@ResponseBody
	public Object gertUserByTokem(@PathVariable String token , String callback) {
		E3Result result = tokenService.getUserByToken(token);
		//判断其是否为jsonp请求
		if(StringUtils.isNotBlank(callback)) {
			//把记过封装成一个js语句响应
			MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(result);
			mappingJacksonValue.setJsonpFunction(callback);
			return mappingJacksonValue;
		}
		return result;
	}
}
