package com.ray.wechat.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;


@Repository
public class DBUtil {

	 
	@Autowired
	private JdbcTemplate jdbcTemplate ;
	 
	
	private static String insertSql(DBRow insertRow,String table_name){
		
	 
		
 		StringBuffer sql = new StringBuffer();
		Iterator<Entry<String, Object>> it = insertRow.entrySet().iterator();
		sql.append("insert into ").append(table_name);
		StringBuffer keys = new StringBuffer();
		StringBuffer values = new StringBuffer();
		while(it.hasNext()){
			Entry<String, Object> entry = it.next(); 
			keys.append(",").append(entry.getKey());
			values.append(",").append("?");
  		}
		sql.append(" (").append(keys.substring(1)).append(")")
		.append(" values (").append(values.substring(1)).append(")");
		return sql.toString().toUpperCase();
	}
	
	public static void main(String[] args) {
		DBRow insertRow = new DBRow();
		insertRow.put("user_name", "zhangsan");
		insertRow.put("age", 12);
		String sql = insertSql(insertRow, "users");
	}
	
 	public Long insert(final DBRow insertRow ,final String table_name){
		KeyHolder keyHolder = new GeneratedKeyHolder();
		
 		jdbcTemplate.update(new PreparedStatementCreator(){
           @Override
           public PreparedStatement createPreparedStatement(Connection conn) throws SQLException{
                PreparedStatement  ps = conn.prepareStatement(insertSql(insertRow, table_name), Statement.RETURN_GENERATED_KEYS);
        		Iterator<Entry<String, Object>> it = insertRow.entrySet().iterator();
                int parameterIndex = 0 ;
        		while(it.hasNext()){
        			Entry<String, Object> entry = it.next(); 
                    ps.setObject(++parameterIndex, entry.getValue());
          		}
                return ps;
            }
           },keyHolder);
		return keyHolder.getKey().longValue();
   
	}
}
