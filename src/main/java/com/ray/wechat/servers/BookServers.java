package com.ray.wechat.servers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ray.wechat.dao.BookDao;
import com.ray.wechat.utils.DBRow;

@Service("bookServers")
@Transactional
public class BookServers {

	@Autowired
	private BookDao bookDao ;

 	public void addBook(){
		DBRow insertRow = new DBRow();
		insertRow.put("book_name", "京东发展历史");
		insertRow.put("book_title", "京东发展历史1");
		insertRow.put("book_price", 10.2D);
		bookDao.addBook(insertRow);
		DBRow insertRow2= new DBRow();
		insertRow2.put("book_name", "京东发展历史2");
		insertRow2.put("book_title", "京东发展历史2");
		insertRow2.put("book_price", 10.2D);
		bookDao.addBook(insertRow2);
		if(1 > 0){
			 throw new RuntimeException();
		 }
	}
	public static void main(String[] args){
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-context.xml");
		BookServers bookServers = applicationContext.getBean(BookServers.class);
		bookServers.addBook();
	}
}
