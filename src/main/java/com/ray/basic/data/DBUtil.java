package com.ray.basic.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.ray.basic.exceptions.DBUtilException;
import com.ray.basic.model.DBRow;
import com.ray.basic.model.HoldDoubleValue;
import com.ray.basic.model.PageCtrl;
import com.ray.basic.sysutils.SystemStringUtil;

@Repository
public class DBUtil {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * update table set xxx = ? , xxx2 = ? where whereCase
	 * 
	 * @param updateRow
	 * @param tableName
	 * @param whereCase
	 * @return
	 */
	private static HoldDoubleValue<String, Object[]> updateSql(DBRow updateRow,
			String tableName, String whereCase) {
		StringBuilder updateSql = new StringBuilder();
		updateSql.append("UPDATE ").append(tableName).append(" SET ");
		Iterator<Entry<String, Object>> it = updateRow.entrySet().iterator();
		StringBuilder keys = new StringBuilder();
		Object[] values = new Object[updateRow.size()];
		int index = 0;
		while (it.hasNext()) {
			Entry<String, Object> entry = it.next();
			keys.append(",").append(entry.getKey().toUpperCase()).append(" = ")
					.append(" ? ");
			values[index++] = entry.getValue();
		}
		updateSql.append(keys.substring(1));
		updateSql.append(whereCase);
		return new HoldDoubleValue<String, Object[]>(updateSql.toString(),
				values);
	}

	/**
	 * 生成insert语句
	 * 
	 * @param insertRow
	 * @param tableName
	 * @return
	 */
	private static HoldDoubleValue<String, Object[]> insertSql(DBRow insertRow,
			String tableName) {
		StringBuilder sql = new StringBuilder();
		Iterator<Entry<String, Object>> it = insertRow.entrySet().iterator();
		sql.append("insert into ").append(tableName);
		StringBuilder keys = new StringBuilder();
		Object[] values = new Object[insertRow.size()];
		int index = 0;
		while (it.hasNext()) {
			Entry<String, Object> entry = it.next();
			keys.append(",").append(entry.getKey());
			values[index++] = entry.getValue();
		}
		sql.append(" (").append(keys.substring(1)).append(")")
				.append(" values (")
				.append(SystemStringUtil.copyChar('?', insertRow.size()))
				.append(")");
		return new HoldDoubleValue<String, Object[]>(sql.toString()
				.toUpperCase(), values);
	}

	public static void main(String[] args) {
		DBRow insertRow = new DBRow();
		insertRow.put("user_name", "zhangsan");
		insertRow.put("age", 12);
		String insertSql = insertSql(insertRow, "users").a;
		HoldDoubleValue<String, Object[]> r = updateSql(insertRow, "users",
				"where id = 1");
		System.out.println(r.a);
		System.out.println(r.b[0]);
		System.out.println("insert " + insertSql);
	}

	/**
	 * 删除 DELETE tableName WHERE A = ? AND B=?
	 * 
	 * @param tableName
	 * @return
	 */
	public Integer delete(final String tableName, final String whereCase) {
		StringBuilder deleteSql = new StringBuilder(" DELETE FROM ").append(
				tableName.toUpperCase()).append(whereCase);
		return jdbcTemplate.update(deleteSql.toString());
	}

	/**
	 * 通过ID去更新
	 * 
	 * @param updateRow
	 * @return
	 */
	public Integer update(final String whereCase, final String tableName,
			final DBRow updateRow) {

		return jdbcTemplate.update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection conn)
					throws SQLException {
				HoldDoubleValue<String, Object[]> updateSql = updateSql(
						updateRow, tableName, whereCase);
				PreparedStatement ps = conn.prepareStatement(updateSql.a);
				int index = 0;
				for (Object object : updateSql.b) {
					ps.setObject(++index, object);
				}
				return ps;
			}
		});
	}

	public Long insert(final DBRow insertRow, final String tableName) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection conn)
					throws SQLException {
				HoldDoubleValue<String, Object[]> insertSqlAndParams = insertSql(
						insertRow, tableName);
				PreparedStatement ps = conn.prepareStatement(
						insertSqlAndParams.a, Statement.RETURN_GENERATED_KEYS);
				int index = 0;
				for (Object obj : insertSqlAndParams.b) {
					ps.setObject(++index, obj);
				}
				return ps;
			}
		}, keyHolder);
		return keyHolder.getKey().longValue();
	}

	/**
	 * pre 方式查询单条记录
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	public DBRow selectSinglePre(String sql, DBRow params) {
		List<DBRow> results = selectMutiPre(sql, params);
		if (results == null || results.size() == 0) {
			return null;
		}
		if (results.size() > 1) {
			throw new DBUtilException("查询到多条数据");
		}
		return results.get(0);
	}

	/**
	 * 查询单条记录
	 * 
	 * @param sql
	 * @return
	 */
	public DBRow selectSingle(String sql) {
		List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);
		if (results == null || results.size() < 1) {
			return null;
		}
		if (results.size() > 1) {
			throw new DBUtilException("查询到多条数据");
		}
		return new DBRow(results.get(0));
	}

	/**
	 * pre 方式查询多条记录
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	public List<DBRow> selectMutiPre(final String sql, final DBRow params) {
		PreparedStatementSetter pss = new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement prepareStatement)
					throws SQLException {
				int parameterIndex = 0;
				if (params != null && params.values() != null) {
					for (Object object : params.values()) {
						prepareStatement.setObject(++parameterIndex, object);
					}
				}
			}
		};
		return jdbcTemplate.query(sql, pss, new DBRowMapper());
	}
	 
	/**
	 * 分页查询
	 * @param sql
	 * @param params
	 * @param pageCtrl
	 */
	public void selectMutiPre(final String sql, final DBRow params,
			PageCtrl pageCtrl) {
			//重新拼装sql
			String fixSql = convertSql(sql, pageCtrl);
			System.out.println(Thread.currentThread() + "fixSql = " + fixSql);
			//执行查询
			List<DBRow> datas = selectMutiPre(fixSql, params);
			pageCtrl.setDatas(datas);
			//查询总数
			Map<String, Object> totalMaps =	jdbcTemplate.queryForMap("SELECT FOUND_ROWS() as total ");
			pageCtrl.setTotal(Integer.parseInt(totalMaps.get("total").toString()));
			System.out.println(Thread.currentThread() + "totalMaps = " + totalMaps + datas);
			
			
 	}
	private String convertSql(String sql, PageCtrl pageCtrl) {
		int pageNo = pageCtrl.getPageNo();
		int pageSize = pageCtrl.getPageSize();
		int offset = (pageNo-1) * pageSize;
		sql = sql.replaceFirst("^\\s*(select|SELECT)\\s+",
				"SELECT SQL_CALC_FOUND_ROWS ")
				+ " LIMIT "
				+ offset
				+ ","
				+ pageSize;
 		return sql;
	}
}
