package com.e3shop.sso.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.e3shop.common.utils.E3Result;
import com.e3shop.pojo.TbUser;
import com.e3shop.sso.service.UserService;

/**
 * 注册功能Controller
 * @author Admin
 *
 */
@Controller
public class RegisterController {

	@Autowired
	private UserService userService;
	
	@RequestMapping("/page/register")
	public String showRegister() {
		
		return "register";
	}
	@RequestMapping("/user/check/{param}/{type}")
	@ResponseBody
	public E3Result checkData(@PathVariable String param,@PathVariable Integer type) {
		E3Result result = userService.checkData(param, type);
		return result;
	}
	/**
	 * 用户注册
	 * @param tbUser
	 * @return
	 */
	@RequestMapping(value="/user/register",method=RequestMethod.POST)
	@ResponseBody
	public E3Result register(TbUser tbUser) {
		E3Result result = userService.registUser(tbUser);
		return result;
	}
}
