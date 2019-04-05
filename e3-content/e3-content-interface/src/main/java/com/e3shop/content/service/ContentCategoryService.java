package com.e3shop.content.service;

import java.util.List;

import com.e3shop.common.pojo.EasyUIDataGridReslut;
import com.e3shop.common.pojo.EasyUiTreeNode;
import com.e3shop.common.utils.E3Result;

public interface ContentCategoryService {

	List<EasyUiTreeNode> getContentCatList(long parentId);
	E3Result addContentCategory(long parentId,String name);
	E3Result updateContentCategory(long id ,String name);
	E3Result deleteContentCatgory(long id);
}
