package com.e3shop.content.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.e3shop.mapper.TbContentMapper;
import com.e3shop.common.jedis.JedisClient;
import com.e3shop.common.pojo.EasyUIDataGridReslut;
import com.e3shop.common.utils.E3Result;
import com.e3shop.common.utils.JsonUtils;
import com.e3shop.content.service.ContentService;
import com.e3shop.pojo.TbContent;
import com.e3shop.pojo.TbContentExample;
import com.e3shop.pojo.TbContentExample.Criteria;
import com.e3shop.pojo.TbItem;
import com.e3shop.pojo.TbItemExample;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
@Service
public class ContentServiceImpl implements ContentService {

	@Value("${CONTENT_LIST}")
	private String CONTENT_LIST;
	@Autowired
	private JedisClient JedisClient;
	@Autowired
	private TbContentMapper tbContentMapper;
	@Override
	/**
	 *  根据categoryId查询信息
	 */
	public List<TbContent> getTbContentList(long categoryId, int page, int rows) {
		//从缓存中查询数据
		try {
			String json = JedisClient.hget(CONTENT_LIST, categoryId+"");
			//缓存中有就响应结果
			if (StringUtils.isNotBlank(json)) {
				List<TbContent> list = JsonUtils.jsonToList(json, TbContent.class);
				System.out.println("redis缓存");
				return list;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//缓存没有就从数据库查询
		TbContentExample example = new TbContentExample();
		Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(categoryId);
		//执行查询
		List<TbContent> list = tbContentMapper.selectByExample(example);
		//存到缓存
		try {
			JedisClient.hset(CONTENT_LIST, categoryId+"", JsonUtils.objectToJson(list));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 增加TbCOntent
	 */
	@Override
	public E3Result addTbContent(TbContent tbContent) {
		tbContent.setCreated(new Date());
		tbContent.setUpdated(new Date());
		tbContentMapper.insert(tbContent);
		return E3Result.ok();
	}
	/**
	 * 编辑更新
	 */
	@Override
	public E3Result updateTbContent(TbContent tbContent) {
		tbContent.setUpdated(new Date());//更新修改时间
		tbContentMapper.updateByPrimaryKeySelective(tbContent);
		return E3Result.ok();
	}
	/**
	 * 删除
	 */
	@Override
	public E3Result deleteTbContent(Long[] ids) {
		for (long id : ids) {
			tbContentMapper.deleteByPrimaryKey(id);
		}
		return E3Result.ok();
	}
	/**
	 * 通过分类id查询
	 */
	@Override
	public List<TbContent> gettbContent(long cid) {
		// TODO Auto-generated method stub
		TbContentExample example = new TbContentExample();
		Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(cid);
		List<TbContent> list = tbContentMapper.selectByExampleWithBLOBs(example);
		return list;
	}

}
