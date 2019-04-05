package com.e3shop.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.e3shop.pojo.TbItem;
import com.e3shop.pojo.TbItemDesc;
import com.e3shop.service.ItemService;
import com.e3shop.web.pojo.Item;

/**
 * 商品详情页面
 * @author Admin
 *
 */
@Controller
public class ItemController {

	@Autowired
	private ItemService itemService;
	
	@RequestMapping("item/{itemId}")
	public String showItemInfo(@PathVariable Long itemId,Model model) {
		//调取服务获取商品基本信息
		TbItem tbItem = itemService.getItemId(itemId);
		Item item = new Item(tbItem);
		//商品描述信息
		TbItemDesc itemDesc = itemService.getItemDesc(itemId);
		//传递给页面
		model.addAttribute("item", item);
		model.addAttribute("itemDesc", itemDesc);
		return "item";
	}
}
