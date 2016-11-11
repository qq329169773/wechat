package com.ray.wechat.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class DBRow{


	private LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
	
	
	public DBRow(){
		super();
	}
	
	public DBRow(Map map){
		data = new LinkedHashMap<>(map);
	}
	public void put(String key , Object value){
		data.put(key, value);
	}
	
	public Set<Entry<String, Object>> entrySet(){
		return data.entrySet();
	}
	
	public Object[] arrayValues(){
		Collection<Object> values = values();
		Object[] results = new Object[values.size()];
		int index = 0 ;
		for(Object obj : values){
			results[index++] = obj ;
		}
		return results ;
	}
	public Collection<Object> values(){
		return data.values() ;
	}
	public <T> T getValue(String key){
		return (T) data.get(key);
	}
	
	public Integer size(){
		return data.size() ;
	}
	@Override
	public String toString() {
 		return data.toString();
	}
	
	public static void main(String[] args){
		DBRow row = new DBRow();
		row.put("key","value");
		row.put("ssss","ssss");
		row.put("aaacc","ssss");
		row.put("ccc","ssss");
		row.put("bb","ssss");
		Iterator<Entry<String, Object>> it = row.entrySet().iterator();
		while (it.hasNext()){
			System.out.println(it.next().getKey());
		}
	}
}
