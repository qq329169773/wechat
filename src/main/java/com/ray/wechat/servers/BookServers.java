package com.ray.wechat.servers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ray.wechat.dao.BookDao;
import com.ray.wechat.utils.BasicDao;
import com.ray.wechat.utils.BasicServers;
import com.ray.wechat.utils.DBRow;

@Service("bookServers")
@Transactional
public class BookServers extends BasicServers{

	@Autowired
	private BookDao bookDao ;

	@Override
	protected BasicDao getBasicDao(){
		return  bookDao;
	}

	public void updateBook(){
		DBRow updateRow = new DBRow();
		updateRow.put("book_name", "京东发展历史11111");
		updateRow.put("book_title", "京东发展历史1111");
		updateRow.put("book_price", 10.21D);
		Integer result  = bookDao.updateEntryById(updateRow,14L);
		System.out.println("update result : " + result);
	}
	
 	public void addBook(){
		DBRow insertRow = new DBRow();
		insertRow.put("book_name", "京东发展历史");
		insertRow.put("book_title", "京东发展历史1");
		insertRow.put("book_price", 10.2D);
		bookDao.insertEntry(insertRow);
		DBRow insertRow2= new DBRow();
		insertRow2.put("book_name", "京东发展历史2");
		insertRow2.put("book_title", "京东发展历史2");
		insertRow2.put("book_price", 10.2D);
		bookDao.insertEntry(insertRow2);
	}
 	public List<DBRow> testQuery(){
 		return bookDao.queryTest();
 	}
 	public DBRow testQueryById(Long id){
 		return bookDao.findById(id);
 	}
 	public static void main(String[] args){
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-context.xml");
		BookServers bookServers = applicationContext.getBean(BookServers.class);
		//bookServers.addBook();
 		System.out.println(bookServers.findRecordById(18L));
 		DBRow params = new DBRow();
 		params.put("book_price", "20");
		System.out.println(bookServers.findRecords("SELECT book_name FROM books WHERE book_price < ? ", params ));;
 	}
}
