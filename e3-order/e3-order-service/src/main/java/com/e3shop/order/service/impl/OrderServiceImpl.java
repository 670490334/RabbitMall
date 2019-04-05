package com.e3shop.order.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.e3shop.common.jedis.JedisClient;
import com.e3shop.common.utils.E3Result;
import com.e3shop.mapper.TbOrderItemMapper;
import com.e3shop.mapper.TbOrderMapper;
import com.e3shop.mapper.TbOrderShippingMapper;
import com.e3shop.order.pojo.OrderInfo;
import com.e3shop.order.service.OrderService;
import com.e3shop.pojo.TbOrderItem;
import com.e3shop.pojo.TbOrderShipping;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private TbOrderMapper orderMapper;
	@Autowired
	private TbOrderItemMapper orderItemMapper;
	@Autowired
	private TbOrderShippingMapper orderShippingMapper;
	@Autowired
	private JedisClient jedisClient;
	@Value("${ORDER_ID_GEN_KEY}")
	private String ORDER_ID_GEN_KEY;
	@Value("${ORDER_ID_START}")
	private String ORDER_ID_START;
	@Value("${ORDER_DETAIL_ID}")
	private String ORDER_DETAIL_ID;
	
	@Override
	public E3Result createOrder(OrderInfo orderInfo) {
		// 生成订单号:使用redis的incr生成；
		if(!jedisClient.exists(ORDER_ID_GEN_KEY)) {
			jedisClient.set(ORDER_ID_GEN_KEY,ORDER_ID_START);
		}
		// 生成订单号
		String orderId = jedisClient.incr(ORDER_ID_GEN_KEY).toString();
		// 补全orderInfo的属性
		orderInfo.setOrderId(orderId);
		// 状态：1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭
		orderInfo.setStatus(1);
		orderInfo.setCreateTime(new Date());
		orderInfo.setUpdateTime(new Date());
		// 插入订单表
		orderMapper.insert(orderInfo);
		// 想订单明细表插入数据
		List<TbOrderItem> orderItems = orderInfo.getOrderItems();
		for (TbOrderItem tbOrderItem : orderItems) {
			// 生成明细id
			String odId = jedisClient.incr(ORDER_DETAIL_ID).toString();
			tbOrderItem.setId(odId);
			tbOrderItem.setOrderId(orderId);
			// 想明细表插入数据
			orderItemMapper.insert(tbOrderItem);
		}
		// 向物流表插入数据
		TbOrderShipping orderShipping = orderInfo.getOrderShipping();
		orderShipping.setOrderId(orderId);
		orderShipping.setCreated(new Date());
		orderShipping.setUpdated(new Date());
		orderShippingMapper.insert(orderShipping);
		// 返回E3result 包含订单号
		return E3Result.ok(orderId);
	}
}
