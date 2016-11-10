package com.ray.wechat.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ray.wechat.utils.DBRow;
import com.ray.wechat.utils.DBUtil;

@Repository
public class BookDao {
	
	
	@Autowired
	private DBUtil dbUtil ;
	
	
	public Long addBook(DBRow insertRow){
		return dbUtil.insert(insertRow, "books");
	}
	public Integer updateBookById(String whereCase , String tableName, DBRow updateRow){
		return dbUtil.update(whereCase, tableName, updateRow);
	}
}
