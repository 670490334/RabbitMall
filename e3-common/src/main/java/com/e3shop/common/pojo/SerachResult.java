package com.e3shop.common.pojo;

import java.io.Serializable;
import java.util.List;
/**
 * 搜索结果
 * @author Admin
 *
 */
public class SerachResult implements Serializable {
	private long recordCount;
	private int totalPages;
	private List<SerachItem> itemList;
	public long getRecordCount() {
		return recordCount;
	}
	public void setRecordCount(long recordCount) {
		this.recordCount = recordCount;
	}
	public int getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}
	public List<SerachItem> getItemList() {
		return itemList;
	}
	public void setItemList(List<SerachItem> itemList) {
		this.itemList = itemList;
	}
}
