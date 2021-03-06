package com.ray.wechat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ray.wechat.servers.BookServers;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.support.RequestContextUtils;


@Controller
public class IndexController {


	@Autowired
	private BookServers bookServers;
	@RequestMapping("hello2")
	@ResponseBody
	public String index(
			@RequestParam(name = "userName" , required = false ) String userName ,
			@RequestParam(name = "age" , required = false ) Integer age 
			){
		return "index" + bookServers   + "userName : " + userName + "age :" + age ;
	}
	
	@RequestMapping("addBook")
	@ResponseBody
	public String addBook(){
		bookServers.addBook();
		return "success" ;
	}
	@RequestMapping("add.html")
	public String add(){
		return "wechat/add" ;
	}
	@RequestMapping("index2.html")
	public String index2(){
		return "wechat/index2";
	}
	@RequestMapping("top.html")
	public String top(){
		return "wechat/top";
	}
	@RequestMapping("left.html")
	public String left(){
		return "wechat/left";
	}
	@RequestMapping("right.html")
	public String right(){
		return "wechat/right";
	}
}
