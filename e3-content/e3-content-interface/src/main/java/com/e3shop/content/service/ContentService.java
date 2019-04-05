package com.e3shop.content.service;

import java.util.List;

import com.e3shop.common.pojo.EasyUIDataGridReslut;
import com.e3shop.common.utils.E3Result;
import com.e3shop.pojo.TbContent;

public interface ContentService {
	List<TbContent> getTbContentList(long categoryId,int page,int rows);
	E3Result addTbContent(TbContent tbContent);
	E3Result updateTbContent(TbContent tbContent);
	E3Result deleteTbContent(Long[] ids);
	List<TbContent> gettbContent(long cid);
}
