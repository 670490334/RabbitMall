package com.e3shop.content.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.e3shop.common.jedis.JedisClient;
import com.e3shop.common.pojo.EasyUIDataGridReslut;
import com.e3shop.common.pojo.EasyUiTreeNode;
import com.e3shop.common.utils.E3Result;
import com.e3shop.content.service.ContentCategoryService;
import com.e3shop.mapper.TbContentCategoryMapper;
import com.e3shop.pojo.TbContent;
import com.e3shop.pojo.TbContentCategory;
import com.e3shop.pojo.TbContentCategoryExample;
import com.e3shop.pojo.TbItem;
import com.e3shop.pojo.TbItemExample;
import com.e3shop.pojo.TbContentCategoryExample.Criteria;
import com.e3shop.pojo.TbContentExample;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
/**
 * 内容管理Service
 * @author Administrator
 * 用Spring的Service 不要用到dubbo的Service
 */
@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {

	@Autowired
	private JedisClient jedisClient;
	@Autowired
	private TbContentCategoryMapper tbContentCategoryMapper;
	@Override
	public List<EasyUiTreeNode> getContentCatList(long parentId) {
		// 根据parentId查询子节点列表
		TbContentCategoryExample example = new TbContentCategoryExample();
		Criteria criteria = example.createCriteria();
		//设置查询条件
		criteria.andParentIdEqualTo(parentId);
		//执行查询
		List<TbContentCategory> catList = tbContentCategoryMapper.selectByExample(example);
		//转换为EasUITreeNode列表
		List<EasyUiTreeNode> nodeList = new ArrayList<EasyUiTreeNode>();
		for (TbContentCategory tbContentCategory : catList) {
			EasyUiTreeNode node = new EasyUiTreeNode();
			node.setId(tbContentCategory.getId());
			node.setText(tbContentCategory.getName());
			node.setState(tbContentCategory.getIsParent()?"closed":"open");
			nodeList.add(node);
		}
		return nodeList;
	}
	@Override
	public E3Result addContentCategory(long parentId, String name) {
		// 创建一个表对应的pojo
		TbContentCategory tbContentCategory = new TbContentCategory();
		//设置pojo属性
		tbContentCategory.setParentId(parentId);
		tbContentCategory.setName(name);
		//1正常，2删除
		tbContentCategory.setStatus(1);
		tbContentCategory.setCreated(new Date());
		tbContentCategory.setSortOrder(1);//默认排序1
		tbContentCategory.setUpdated(new Date());
		tbContentCategory.setIsParent(false);//新增节点一定是叶子节点
		//插入到数据库
		tbContentCategoryMapper.insert(tbContentCategory);
		//判断父节点的isparent属性，如果不是true，改为true。
		TbContentCategory parent = tbContentCategoryMapper.selectByPrimaryKey(parentId);
		if(!parent.getIsParent()) {
			parent.setIsParent(true);
			tbContentCategoryMapper.updateByPrimaryKey(parent);
		}
		//返回结果
		return E3Result.ok(tbContentCategory);
	}
	@Override
	public E3Result updateContentCategory(long id, String name) {
		TbContentCategoryExample example = new TbContentCategoryExample();
		Criteria criteria = example.createCriteria();
		criteria.andIdEqualTo(id);
		TbContentCategory tbContentCategory = new TbContentCategory();
		//设置pojo属性
		tbContentCategory.setName(name);
		tbContentCategoryMapper.updateByExampleSelective(tbContentCategory, example);
		return E3Result.ok();
	}
	@Override
	public E3Result deleteContentCatgory(long id) {
		// TODO Auto-generated method stub
		TbContentCategory tbContentCategory = tbContentCategoryMapper.selectByPrimaryKey(id);
		if(tbContentCategory.getIsParent()) {
			return E3Result.ok();
		}
		tbContentCategoryMapper.deleteByPrimaryKey(id);
		TbContentCategoryExample example = new TbContentCategoryExample();
		Criteria criteria = example.createCriteria();
		//查询当前删除的节点的父节点是否还有其他子节点
		criteria.andParentIdEqualTo(tbContentCategory.getParentId());
		List<TbContentCategory> list = tbContentCategoryMapper.selectByExample(example);
		if(list.size() ==0 ) {
			TbContentCategoryExample parentexample = new TbContentCategoryExample();
			Criteria parentCriteria = parentexample.createCriteria();
			parentCriteria.andIdEqualTo(tbContentCategory.getParentId());
			TbContentCategory parent = new TbContentCategory();
			parent.setIsParent(false);
			tbContentCategoryMapper.updateByExampleSelective(parent, parentexample);
		}
		
		return E3Result.ok();
	}

}
