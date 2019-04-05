package com.e3shop.web.pojo;

import com.e3shop.pojo.TbItem;
/**
 *  商品详情信息的Pojo
 * @author Admin
 *
 */
public class Item extends TbItem {

	public Item(TbItem tbItem) {
		this.setId(tbItem.getId());
		this.setTitle(tbItem.getTitle());
		this.setSellPoint(tbItem.getSellPoint());
		this.setPrice(tbItem.getPrice());
		this.setNum(tbItem.getNum());
		this.setBarcode(tbItem.getBarcode());
		this.setImage(tbItem.getImage());
		this.setCid(tbItem.getCid());
		this.setStatus(tbItem.getStatus());
		this.setCreated(tbItem.getCreated());
		this.setUpdated(tbItem.getUpdated());
	}
	public String[] getImages() {
		String image2 = this.getImage();
		if(image2 !=null && !"".equals(image2)) {
			String [] string2 = image2.split(",");
			return string2;
		}
		return null;
	}
}
