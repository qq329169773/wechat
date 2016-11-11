package com.ray.wechat.utils;

import java.util.List;

/**
 * Created by zhangrui25 on 2016/11/11.
 */
public abstract class BasicServers {

	protected abstract BasicDao getBasicDao();

	/**
	 * 添加一条记录
	 * 
	 * @param insertRow
	 * @return
	 */
	public Long addRecord(DBRow insertRow) {
		return getBasicDao().insertEntry(insertRow);
	}

	/**
	 * 修改一条记录
	 * 
	 * @param modifyDBRow
	 * @param id
	 * @return
	 */
	public Integer modifyRecordById(DBRow modifyDBRow, Long id) {
		return getBasicDao().updateEntryById(modifyDBRow, id);
	}

	/**
	 * 删除一条记录
	 * 
	 * @param id
	 * @return
	 */
	public Integer removeRecordById(Long id) {
		return getBasicDao().deleteEntryById(id);
	}

	/**
	 * 通过ID查询一条记录
	 * 
	 * @param id
	 * @return
	 */
	public DBRow findRecordById(Long id) {
		return getBasicDao().selectEntryById(id);
	}
	/**
	 * 查询多条记录
	 * @param sql
	 * @param params
	 * @return
	 */
	public List<DBRow> findRecords(String sql, DBRow params) {
		return getBasicDao().selectMutipleEntrys(sql, params);
	}
}
