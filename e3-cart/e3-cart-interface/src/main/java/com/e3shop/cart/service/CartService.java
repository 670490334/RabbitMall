package com.e3shop.cart.service;
/**
 * 添加购物车
 * @author Admin
 *
 */

import java.util.List;

import com.e3shop.common.utils.E3Result;
import com.e3shop.pojo.TbItem;

public interface CartService {

	/**
	 * 添加到购物车
	 * @param userId
	 * @param itemId
	 * @param num
	 * @return
	 */
	E3Result addCart(long userId,long itemId,int num);
	/**
	 * 合并redis和cookie中的购物车商品信息
	 * @param userId
	 * @param list
	 * @return
	 */
	E3Result mergeCart(long userId,List<TbItem> list);
	/**
	 * 从redis中获取购物车信息
	 * @param userId
	 * @return
	 */
	List<TbItem> getCartListFromRedis(long userId);
	/**
	 * 修改redis中商品信息的num
	 * @param userId
	 * @param itemId
	 * @param num
	 * @return
	 */
	E3Result updateCartItemNum(long userId, long itemId, int num);
	/**
	 * 删除购物车中的数据
	 * @param userId
	 * @param itemId
	 * @return
	 */
	E3Result deleteCartFromRedis(long userId, long itemId);
	/**
	 *  清空已经下单的购物车商品
	 * @param userId
	 * @return
	 */
	E3Result deleteCartItem(long userId);
}
