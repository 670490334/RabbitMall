package com.e3shop.web.messagelistener;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import com.e3shop.pojo.TbItem;
import com.e3shop.pojo.TbItemDesc;
import com.e3shop.service.ItemService;
import com.e3shop.web.pojo.Item;

import freemarker.template.Configuration;
import freemarker.template.Template;
/**
 * 监听商品添加信息，生产对应的静态页面
 * @author Admin
 *
 */
public class HtmlGenListener implements MessageListener{

	private String username;
	@Autowired
	private ItemService itemService;
	@Autowired
	private FreeMarkerConfig freeMarkerConfig;
	@Value("${HTML_GEN_PATH}")
	private String HTML_GEN_PATH;
	@Override
	public void onMessage(Message message) {
		System.err.println("静态页面成");
		//创建一个模板
		try {
			//从消息中取商品id
			TextMessage textMessage = (TextMessage) message;
			String text = textMessage.getText();
			Long itemId = new Long(text);
			//等待事务提交
			Thread.sleep(1000);
			//根据id查询商品信息，商品基本信息和描述
			TbItem tbItem = itemService.getItemId(itemId);
			Item item = new Item(tbItem);
			//取商品描述
			TbItemDesc itemDesc = itemService.getItemDesc(itemId);
			//创建一个数据集，把商品数据封装
			Map data = new HashMap<>();
			data.put("item", item);
			data.put("itemDesc", itemDesc);
			//加载模板对象
			Configuration configuration = freeMarkerConfig.getConfiguration();
			Template template = configuration.getTemplate("item.ftl");
			//创建一个输出流，指定输出目录及文件名
			Writer out = new FileWriter(new File(HTML_GEN_PATH+itemId+".html"));
			template.process(data, out);
			//关闭流
			out.close();
		} 
		catch (Exception e) {
			e.printStackTrace();		
		}
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

}
