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
	 
	/**
	 * 
	 * update table set xxx = ? , xxx2 = ?  where whereCase 
	 * @param updateRow
	 * @param tableName
	 * @param whereCase
	 * @return
	 */
	private static HoldDoubleValue<String,Object[]> updateSql(DBRow updateRow , String tableName , String whereCase){
		StringBuilder updateSql = new StringBuilder();
		updateSql.append("UPDATE ").append(tableName).append(" SET ");
		Iterator<Entry<String, Object>> it = updateRow.entrySet().iterator();
		StringBuilder keys = new StringBuilder();
		Object[] values = new Object[updateRow.size()];
		int index = 0 ;
		while(it.hasNext()){
			Entry<String, Object> entry = it.next(); 
			keys.append(",").append(entry.getKey().toUpperCase()).append(" = ").append(" ? ");
			values[index++] = entry.getValue();
  		}
		updateSql.append(keys.substring(1));
		updateSql.append(whereCase);
		return new HoldDoubleValue<String, Object[]>(updateSql.toString(), values);
	}
	/**
	 * 生成insert语句
	 * @param insertRow
	 * @param table_name
	 * @return
	 */
	private static String insertSql(DBRow insertRow,String tableName){
		
 		StringBuffer sql = new StringBuffer();
		Iterator<Entry<String, Object>> it = insertRow.entrySet().iterator();
		sql.append("insert into ").append(tableName);
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
		HoldDoubleValue<String, Object[]> r = updateSql(insertRow, "users", "where id = 1");
		System.out.println(r.a);
		System.out.println(r.b[0]);
		
	}
	
	
	/**
	 * 通过ID去更新
	 * @param updateRow
	 * @param Id
	 * @return
	 */
	public Integer update(final String whereCase,final String tableName,final DBRow updateRow ){
		
		return jdbcTemplate.update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection conn)
					throws SQLException {
				HoldDoubleValue<String, Object[]> updateSql = updateSql(updateRow, tableName, whereCase);
                PreparedStatement  ps = conn.prepareStatement(updateSql.a);
                int index = 0 ;
                for(Object object : updateSql.b){
                    ps.setObject(++index, object);
                }
				return ps;
			}
		});
	 
	}
	
 	public Long insert(final DBRow insertRow ,final String tableName){
		KeyHolder keyHolder = new GeneratedKeyHolder();
 		jdbcTemplate.update(new PreparedStatementCreator(){
           @Override
           public PreparedStatement createPreparedStatement(Connection conn) throws SQLException{
                PreparedStatement  ps = conn.prepareStatement(insertSql(insertRow, tableName), Statement.RETURN_GENERATED_KEYS);
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
