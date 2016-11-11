package com.ray.basic.sysutils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContextUtil implements ApplicationContextAware {

	private static ApplicationContext context;
 
    @SuppressWarnings("static-access")
    public void setApplicationContext(ApplicationContext contex) throws BeansException {
        this.context=contex;
    }
    
    public static ApplicationContext getContext() {
		return context;
	}

	public static Object getBean(String beanName){
        return context.getBean(beanName);
    }
}