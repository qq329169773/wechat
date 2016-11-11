package com.ray.wechat.dao;

import com.ray.basic.data.BasicDao;
import com.ray.basic.data.DBUtil;
import com.ray.basic.model.DBRow;
import com.ray.basic.model.HoldDoubleValue;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class BookDao extends BasicDao {
	
	
	@Autowired
	private DBUtil dbUtil ;

	@Override
	protected DBUtil getDBUtil() {
		return dbUtil;
	}

	@Override
	protected HoldDoubleValue<String, String> getTableNameAndPrimaryKey() {
		return new HoldDoubleValue<String, String>("books","book_id");
	}
	
	public List<DBRow> queryTest(){
		return dbUtil.selectMutiPre("select * from books", null);
	}
	public DBRow findById(Long id){
		DBRow params = new DBRow();
		params.put("book_price", 10.2);
		return dbUtil.selectSingle("select * from books where book_price = 10.2   " );
	}
}
