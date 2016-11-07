package com.ray.wechat.utils;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

public class DBRow{
	
	private HashMap<String, Object> data = new HashMap<String, Object>();
	
	public void put(String key , Object value){
		data.put(key, value);
	}
	
	public Set<Entry<String, Object>> entrySet(){
		return data.entrySet();
	}
	
	public <T> T getValue(String key){
		return (T) data.get(key);
	}
	
}
