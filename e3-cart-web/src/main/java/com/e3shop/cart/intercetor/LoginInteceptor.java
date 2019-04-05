package com.e3shop.cart.intercetor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.e3shop.common.utils.CookieUtils;
import com.e3shop.common.utils.E3Result;
import com.e3shop.pojo.TbUser;
import com.e3shop.sso.service.TokenService;

public class LoginInteceptor implements HandlerInterceptor{

	@Autowired
	private TokenService tokenService;
	//前处理  返回true放行，，false拦截
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// 从Cookie中取token
		String token = CookieUtils.getCookieValue(request, "token");
		// 如果没有token就是未登录状态，直接放行
		if(StringUtils.isBlank(token)) {
			return true;
		}
		// 如果取到token，需要调用sso系统的服务，根据token取用户信息
		E3Result result = tokenService.getUserByToken(token);
		// 没有取到用户信息，登录过期，直接放行
		if(result.getStatus()!=200) {
			return true;
		}
		// 取到用户信息，登录状态
		TbUser tbUser = (TbUser) result.getData();
		// 把用户信息放到request中，只需要在Controller中判断request
		request.setAttribute("tbUser", tbUser);
		// 放行
		return true;
	}
	//handler执行之后，放回ModelAnView之前
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		
	}
	//完成处理，返回ModelAndView之后，可以在此处理异常
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

}
