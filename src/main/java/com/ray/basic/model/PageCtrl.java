package com.ray.basic.model;

import java.util.List;

import com.ray.basic.exceptions.SystemParamsInvalidateException;

/**
 * Created by zhangrui25 on 2016/11/11.
 */
public class PageCtrl {

	private List<DBRow> datas ;
	private Integer pageNo ;
	private Integer pageSize;
	private Integer total ;
	private Integer totalPage ;
	
	
	
	public PageCtrl(Integer pageNo , Integer pageSize){
		if(pageNo <= 0 || pageSize <= 0){
			throw new SystemParamsInvalidateException("pageNo,pageSize参数错误"+pageNo + ","+pageSize);
		}
		this.pageNo = pageNo ;
		this.pageSize = pageSize ;
	}
	
	public List<DBRow> getDatas() {
		return datas;
	}
	public void setDatas(List<DBRow> datas) {
		this.datas = datas;
	}
	public Integer getPageNo() {
		return pageNo;
	}
	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public Integer getTotal() {
		return total;
	}
	/**
	 * total: 4 pageSize:5 totalPageSize 1 
	 * total: 20 pageSize:5 totalPageSize 4 ;
	 * total: 24  pageSize:5 totalPageSize 5 ; 
	 * @param total
	 */
	public void setTotal(Integer total) {
		this.total = total;
		Integer fixTotal = total % pageSize ;
		totalPage = fixTotal == 0 ? total / pageSize : total / pageSize  + 1 ;
	}
	public Integer getTotalPage() {
		return totalPage;
	}
	
/*	public static void main(String[] args) {
		PageCtrl ctrl = new PageCtrl(1, 4);
		ctrl.setTotal(23);
		System.out.println(ctrl.getTotalPage());
	}*/
}
