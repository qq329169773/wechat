package com.ray.basic.model;

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
		data = new LinkedHashMap<String,Object>(map);
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
	
	public Long getLong(String key){
		if(data.containsKey(key)){
			Object value = data.get(key);
			if(value != null){
				return Long.parseLong(value.toString());
			}
		}
		return null ;
	}
	public Integer getInteger(String key){
		if(data.containsKey(key)){
			Object value = data.get(key);
			if(value != null){
				return Integer.parseInt(value.toString());
			}
		}
		return null ;
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
