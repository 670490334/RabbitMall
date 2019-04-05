package com.e3shop.order.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.e3shop.cart.service.CartService;
import com.e3shop.common.jedis.JedisClient;
import com.e3shop.common.utils.E3Result;
import com.e3shop.order.pojo.OrderInfo;
import com.e3shop.order.service.OrderService;
import com.e3shop.pojo.TbItem;
import com.e3shop.pojo.TbUser;

/**
 * 订单Controller
 * @author Admin
 *
 */
@Controller
public class OrderController {

	@Autowired
	private OrderService orderService;
	@Autowired
	private CartService cartService;
	
	@RequestMapping("/order/order-cart")
	public String showOrder(HttpServletRequest request) {
		TbUser tbUser = (TbUser) request.getAttribute("tbUser");
		List<TbItem> cartList = cartService.getCartListFromRedis(tbUser.getId());
		request.setAttribute("cartList", cartList);
		return "order-cart";
	}
	@RequestMapping(value="/order/create",method=RequestMethod.POST)
	public String createOrder(OrderInfo orderInfo,HttpServletRequest request) {
		// 取用户信息
		TbUser tbUser = (TbUser) request.getAttribute("tbUser");
		// 把用户信息添加到pojo
		orderInfo.setUserId(tbUser.getId());
		orderInfo.setBuyerNick(tbUser.getUsername());
		// 调用服务生成订单
		E3Result result = orderService.createOrder(orderInfo);
		if(result.getStatus()==200) {
			// 订单生成成功 删除购物车
			cartService.deleteCartItem(tbUser.getId());
		}
		// 把订单号传递给页面
		request.setAttribute("orderId", result.getData());
		request.setAttribute("payment", orderInfo.getPayment());
		// 返回逻辑视图
		return "success";
	}
}
