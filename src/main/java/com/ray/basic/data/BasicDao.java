package com.ray.basic.data;

import java.util.List;

import com.ray.basic.model.DBRow;
import com.ray.basic.model.HoldDoubleValue;

/**
 * Created by zhangrui25 on 2016/11/11.
 */
public abstract class BasicDao {

	protected abstract DBUtil getDBUtil();

	protected abstract HoldDoubleValue<String, String> getTableNameAndPrimaryKey();

	private String tableName() {
		return getTableNameAndPrimaryKey().a;
	}

	private String primaryKey() {
		return getTableNameAndPrimaryKey().b;
	}

	/**
	 * 添加entry
	 * 
	 * @param insertRow
	 */
	public Long insertEntry(DBRow insertRow) {
		return getDBUtil().insert(insertRow, tableName());
	}

	/**
	 * 通过ID更新
	 * 
	 * @param updateRow
	 * @param entryId
	 * @return
	 */
	public Integer updateEntryById(DBRow updateRow, Long entryId) {
		return getDBUtil().update(" WHERE " + primaryKey() + " = " + entryId, tableName(), updateRow);
	}

	/**
	 * 通过ID删除一条数据
	 * 
	 * @param entryId
	 * @return
	 */
	public Integer deleteEntryById(Long entryId) {
		return getDBUtil().delete(tableName(), " WHERE " + primaryKey() + " = " + entryId);
	}

	/**
	 * 通过ID查询一条数据
	 * 
	 * @param id
	 * @return
	 */
	public DBRow selectEntryById(Long id) {
		DBRow params = new DBRow();
		params.put(primaryKey(), id);
		return getDBUtil().selectSinglePre("SELECT * FROM " + tableName() + " WHERE " + primaryKey() + " = ? ", params);
	}
	/**
	 * 查询一个List
	 * @param sql
	 * @param params
	 * @return
	 */
	public List<DBRow> selectMutipleEntrys(String sql, DBRow params) {
		return getDBUtil().selectMutiPre(sql, params);
	}
}
