package com.e3shop.order.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.e3shop.cart.service.CartService;
import com.e3shop.common.utils.CookieUtils;
import com.e3shop.common.utils.E3Result;
import com.e3shop.common.utils.JsonUtils;
import com.e3shop.pojo.TbItem;
import com.e3shop.pojo.TbUser;
import com.e3shop.sso.service.TokenService;

/**
 * 用户登录拦截器
 * 
 * @author Admin
 *
 */
public class OrderInterceptor implements HandlerInterceptor {

	@Autowired
	private CartService cartService;
	@Autowired
	private TokenService tokenService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// 判断是否登录
		String token = CookieUtils.getCookieValue(request, "token");
		// 如果token不存在，未登录状态，条状到sso系统的登录界面，
		if (StringUtils.isBlank(token)) {
			// token不存在，未登录，跳转到sso系统的登录页面，登录成功之后，跳转到当前请求的url
			response.sendRedirect("http://localhost:8088/page/login?redirect=" + request.getRequestURL());
			// 拦截
			return false;
		}
		// 如果存在，调用sso服务根据token取用户i西南西
		E3Result result = tokenService.getUserByToken(token);
		// 如果取不到，用户登录过期，跳转到sso登录
		if (result.getStatus() != 200) {
			response.sendRedirect("http://localhost:8088/page/login?redirect=" + request.getRequestURL());
			// 拦截
			return false;
		}
		// 如果渠道，是登录状态，需要把用户信息存在request中
		TbUser tbUser = (TbUser) result.getData();
		request.setAttribute("tbUser", tbUser);
		// 判断cookie是否有购物车数据，如果有合并到服务端
		String jsonCartList = CookieUtils.getCookieValue(request, "cart", true);
		if (StringUtils.isNotBlank(jsonCartList)) {
			List<TbItem> cartList = JsonUtils.jsonToList(jsonCartList, TbItem.class);
			// 嗲用cart服务中的service合并购物车
			cartService.mergeCart(tbUser.getId(), cartList);
		}
		// 放行
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub

	}

}
