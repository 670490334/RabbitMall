package com.e3shop.cart.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.e3shop.cart.service.CartService;
import com.e3shop.common.jedis.JedisClient;
import com.e3shop.common.utils.CookieUtils;
import com.e3shop.common.utils.E3Result;
import com.e3shop.common.utils.JsonUtils;
import com.e3shop.pojo.TbItem;
import com.e3shop.pojo.TbUser;
import com.e3shop.service.ItemService;

/**
 * 购物车Controller
 * 
 * @author Admin
 *
 */
@Controller
public class CartController {

	@Autowired
	private CartService cartService;
	@Value("${COOKIE_CART_EXPIRE}")
	private Integer COOKIE_CART_EXPIRE;
	@Autowired
	private ItemService itemService;

	/**
	 * 添加购物车
	 * 
	 * @param itemId
	 * @param num
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/cart/add/{itemId}")
	public String addCart(@PathVariable Long itemId, @RequestParam(defaultValue = "1") Integer num,
			HttpServletRequest request, HttpServletResponse response) {
		// 判断用户是否为登录状态
		TbUser tbUser = (TbUser) request.getAttribute("tbUser");
		// 如果为登录状态，将购物车写入redis
		if (tbUser != null) {
			// 调用cartService将登录后的购物车商品信息添加到redis中
			cartService.addCart(tbUser.getId(), itemId, num);
			return "cartSuccess";
		}
		// 如果未登录使用cookie
		// 先从cookie中取购物车列表
		List<TbItem> list = geTbItemFromCookie(request);
		// 判断商品在商品列表中是否存在
		boolean flag = false;
		// 如果存在数量相加
		for (TbItem tbItem : list) {
			// 包装数据类型不能直接==
			// 如果商品在商品列表中存在
			if (tbItem.getId() == itemId.longValue()) {
				flag = true;
				// 找到商品数量相加
				tbItem.setNum(tbItem.getNum() + num);
				break;
			}
		}
		// 如果不存在，根据itemId查询商品信息得到一个TbItem
		if (!flag) {
			TbItem item = itemService.getItemId(itemId);
			// 设置商品数量
			item.setNum(num);
			// 取一张图片
			String image = item.getImage();
			if (StringUtils.isNoneBlank(image)) {
				item.setImage(image.split(",")[0]);
				// 把商品添加到商品列表
				list.add(item);
			}
		}
		// 写入Cookie,需要转码，设置过期时间
		CookieUtils.setCookie(request, response, "cart", JsonUtils.objectToJson(list), COOKIE_CART_EXPIRE, true);
		// 返回添加成功页面
		return "cartSuccess";
	}

	/**
	 * 从Cookie中购物车商品信息返回给页面展示
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/cart/cart")
	public String showCartList(HttpServletRequest request, HttpServletResponse response) {
		// 从cookie中获取购物车商品信息
		List<TbItem> cartList = geTbItemFromCookie(request);
		// 判断是否登录
		TbUser tbUser = (TbUser) request.getAttribute("tbUser");
		if (tbUser != null) {
			System.out.println("用户已经登录，用户名为：" + tbUser.getUsername());
			// 判断cookie中是否有商品数据，如果有添加到redis中并删除cookie
			if (!cartList.isEmpty()) {
				// 合并cookie和redis中的数据
				cartService.mergeCart(tbUser.getId(), cartList);
				// 删除cookie中的商品信息,设置其值为空就行
				CookieUtils.setCookie(request, response, "cart", "");
			}
			// 获取购物车列表显示到页面
			List<TbItem> list = cartService.getCartListFromRedis(tbUser.getId());
			request.setAttribute("cartList", list);
			return "cart";
		}
		// 把商品传递给页面
		request.setAttribute("cartList", cartList);
		return "cart";
	}

	/**
	 * Ajax刷新页面，更新购物车商品数量与价格信息
	 * 
	 * @param itemId
	 * @param num
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/cart/update/num/{itemId}/{num}", method = RequestMethod.POST)
	@ResponseBody
	public E3Result updataCartNum(@PathVariable Long itemId, @PathVariable Integer num, HttpServletRequest request,
			HttpServletResponse response) {
		// 判断其是否登录
		TbUser tbUser = (TbUser) request.getAttribute("tbUser");
		if (tbUser != null) {
			// 更新服务端的商品数量
			cartService.updateCartItemNum(tbUser.getId(), itemId, num);
			//展示给页面
			
			return E3Result.ok();
		}
		// 从Cookie中取购物车列表
		List<TbItem> cartList = geTbItemFromCookie(request);
		// 遍历商品列表找到对应的商品
		for (TbItem tbItem : cartList) {
			// 更新数量
			if (tbItem.getId().longValue() == itemId) {
				tbItem.setNum(num);
				break;
			}
		}
		// 把购物车列表写回Cookie
		CookieUtils.setCookie(request, response, "cart", JsonUtils.objectToJson(cartList), COOKIE_CART_EXPIRE, true);
		// 返回成功
		return E3Result.ok();
	}

	/**
	 * 从cookie中获取购物车列表的处理
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	private List<TbItem> geTbItemFromCookie(HttpServletRequest request) {
		String json = CookieUtils.getCookieValue(request, "cart", true);
		// 如果为空
		if (StringUtils.isBlank(json)) {
			return new ArrayList<TbItem>();
		}
		// 如果不为空
		List<TbItem> list = JsonUtils.jsonToList(json, TbItem.class);
		return list;
	}

	@RequestMapping(value = "/cart/delete/{itemId}")
	public String deleteCart(@PathVariable Long itemId, HttpServletRequest request, HttpServletResponse response) {
		// 判断其是否登录
		// 判断其是否登录
		TbUser tbUser = (TbUser) request.getAttribute("tbUser");
		if (tbUser != null) {
			// 删除被选中的服务端的购物车商品
			cartService.deleteCartFromRedis(tbUser.getId(), itemId);
			return "redirect:/cart/cart.html";
		}
		List<TbItem> cartList = geTbItemFromCookie(request);
		int i = 0;
		for (TbItem tbItem : cartList) {
			// 判断是那个商品，然后将其删除
			if (tbItem.getId().longValue() == itemId) {
				cartList.remove(i);
				break;
			}
			i++;
		}
		// 将更新后的数据存入Cookie
		CookieUtils.setCookie(request, response, "cart", JsonUtils.objectToJson(cartList), COOKIE_CART_EXPIRE, true);
		// 重定向到购物测详情页面
		return "redirect:/cart/cart.html";

	}
}
