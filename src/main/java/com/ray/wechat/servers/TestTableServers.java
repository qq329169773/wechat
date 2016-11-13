package com.ray.wechat.servers;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import com.ray.basic.data.BasicDao;
import com.ray.basic.data.BasicServers;
import com.ray.basic.model.DBRow;
import com.ray.basic.sysutils.SystemStringUtil;
import com.ray.wechat.dao.TestTableDao;

@Service("testTableServers")
public class TestTableServers extends BasicServers{

	@Autowired
	private TestTableDao testTableDao;
	
	@Override
	protected BasicDao getBasicDao() {
 		return testTableDao;
	}
	public static void main(String[] args){
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-context.xml");
		TestTableServers testTableServers = applicationContext.getBean(TestTableServers.class);
		for(int index = 1 ; index < 1000000 ; index++){
			DBRow insertRow = new DBRow();
			insertRow.put("b", index % 1000);
			insertRow.put("c", new Random().nextInt(1000));
			insertRow.put("d", SystemStringUtil.getMD5(index+"f"));
			testTableServers.addRecord(insertRow);
		}
 	}
}
