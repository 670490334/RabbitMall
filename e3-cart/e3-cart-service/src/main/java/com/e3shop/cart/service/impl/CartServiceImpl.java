package com.e3shop.cart.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.e3shop.cart.service.CartService;
import com.e3shop.common.jedis.JedisClient;
import com.e3shop.common.utils.E3Result;
import com.e3shop.common.utils.JsonUtils;
import com.e3shop.mapper.TbItemMapper;
import com.e3shop.pojo.TbItem;
@Service
public class CartServiceImpl implements CartService {

	@Autowired
	private TbItemMapper tbItemMapper;
	@Value("${REDIS_CART_PRE}")
	private String REDIS_CART_PRE;
	@Autowired
	private JedisClient jedisClient;
	@Override
	public E3Result addCart(long userId, long itemId,int num) {
		// 向redis中添加购物车
		// 数据类型是hashKey：用户id field：商品id value 商品信息
		// 判断商品是否存在
		Boolean hexists = jedisClient.hexists(REDIS_CART_PRE+":"+userId, itemId+"");
		// 如果存在,数量相加
		if(hexists) {
			String json = jedisClient.hget(REDIS_CART_PRE+":"+userId,itemId+"");
			TbItem tbItem = JsonUtils.jsonToPojo(json, TbItem.class);
			tbItem.setNum(tbItem.getNum()+num);
			//写回redis
			jedisClient.hset(REDIS_CART_PRE+":"+userId, itemId+"", JsonUtils.objectToJson(tbItem));
			return E3Result.ok();
		}
		// 如果不存在。根据商品id取商品信息
		TbItem tbItem = tbItemMapper.selectByPrimaryKey(itemId);
		//把商品数量设置进tbItem中
		tbItem.setNum(num);
		//取一张图片
		String image = tbItem.getImage();
		if(StringUtils.isNotBlank(image)) {
			tbItem.setImage(image.split(",")[0]);
		}
		// 添加到购物车列表
		jedisClient.hset(REDIS_CART_PRE+":"+userId, itemId+"", JsonUtils.objectToJson(tbItem));
		// 返回成功
		return E3Result.ok();
	}
	@Override
	public E3Result mergeCart(long userId, List<TbItem> list) {
		// 把cookie中的商品信息添加到redis
		for (TbItem tbItem : list) {
			addCart(userId, tbItem.getId(), tbItem.getNum());
		}
		return E3Result.ok();
	}
	@Override
	public List<TbItem> getCartListFromRedis(long userId) {
		// 从redis获取hset的所有数据数据
		List<String> jsonList = jedisClient.hvals(REDIS_CART_PRE+":"+userId);
		List<TbItem> cartList = new ArrayList<TbItem>();
		// 遍历jsonList
		for (String string : jsonList) {
			// 得到tbItem
			TbItem tbItem = JsonUtils.jsonToPojo(string, TbItem.class);
			// 添加到CartList
			cartList.add(tbItem);
		}
		return cartList;
	}
	@Override
	public E3Result updateCartItemNum(long userId, long itemId, int num) {
		// 从hset中获取数据
		String json = jedisClient.hget(REDIS_CART_PRE+":"+userId,itemId+"");
		// 
		TbItem tbItem = JsonUtils.jsonToPojo(json, TbItem.class);
		tbItem.setNum(num);
		//存入reids中
		jedisClient.hset(REDIS_CART_PRE+":"+userId,itemId+"", JsonUtils.objectToJson(tbItem));
		return E3Result.ok();
	}
	@Override
	public E3Result deleteCartFromRedis(long userId, long itemId) {
		//从redis中删除相应的filed数据
		jedisClient.hdel(REDIS_CART_PRE+":"+userId, itemId+"");
		return E3Result.ok();
	}
	@Override
	public E3Result deleteCartItem(long userId) {
		//删除购物车信息
		jedisClient.del(REDIS_CART_PRE+":"+userId);
		return E3Result.ok();
	}

}
