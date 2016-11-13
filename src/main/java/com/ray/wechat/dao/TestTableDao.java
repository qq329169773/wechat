package com.ray.wechat.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ray.basic.data.BasicDao;
import com.ray.basic.data.DBUtil;
import com.ray.basic.model.HoldDoubleValue;

@Repository
public class TestTableDao extends BasicDao{
	
	@Autowired
	private DBUtil dbUtil ;
	
	@Override
	protected DBUtil getDBUtil() {
 		return dbUtil;
	}

	@Override
	protected HoldDoubleValue<String, String> getTableNameAndPrimaryKey() {
 		return new HoldDoubleValue<String, String>("test_table", "a");
	}
	
	
}
