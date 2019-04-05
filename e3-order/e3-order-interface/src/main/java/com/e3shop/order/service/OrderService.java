package com.e3shop.order.service;

import com.e3shop.common.utils.E3Result;
import com.e3shop.order.pojo.OrderInfo;

public interface OrderService {

	E3Result createOrder(OrderInfo orderInfo);
}
