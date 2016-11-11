package com.ray.wechat.utils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class DBRowMapper implements RowMapper<DBRow> {
	
	ResultSetMetaData metaData = null ;
	
	private ResultSetMetaData getMetaData(ResultSet rs) throws SQLException{
		if(metaData == null){
			metaData = rs.getMetaData();
			return metaData ;
		}
		return metaData ;
	}
	@Override
	public DBRow mapRow(ResultSet rs, int rowNum) throws SQLException {
		DBRow result = new DBRow();
		ResultSetMetaData metaData = getMetaData(rs);
		for (int index = 1; index <= metaData.getColumnCount(); index++) {
			result.put(metaData.getColumnName(index), rs.getObject(index));
		}
		return result;
	}
}
