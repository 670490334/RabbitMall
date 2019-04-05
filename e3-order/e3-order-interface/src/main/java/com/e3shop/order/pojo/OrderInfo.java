package com.e3shop.order.pojo;

import java.io.Serializable;
import java.util.List;

import com.e3shop.pojo.TbOrder;
import com.e3shop.pojo.TbOrderItem;
import com.e3shop.pojo.TbOrderShipping;
/**
 *  订单pojo
 * @author Admin
 *
 */
public class OrderInfo extends TbOrder implements Serializable {

	private List<TbOrderItem> orderItems;
	private TbOrderShipping orderShipping;
	public List<TbOrderItem> getOrderItems() {
		return orderItems;
	}
	public void setOrderItems(List<TbOrderItem> orderItems) {
		this.orderItems = orderItems;
	}
	public TbOrderShipping getOrderShipping() {
		return orderShipping;
	}
	public void setOrderShipping(TbOrderShipping orderShipping) {
		this.orderShipping = orderShipping;
	}

}
