package com.ray.wechat.utils;

/**
 * Created by zhangrui25 on 2016/11/11.
 */
public class SystemStringUtil {

    /**
     * 生成 number个 character
     * @param character
     * @param number
     * @return
     */
    public static String copyChar(Character character , Integer number){
           StringBuilder result = new StringBuilder();
           for(Integer index = 0 ; index < number ; index++ ){
               result.append(",").append(character);
           }
           return  result.length() > 1 ?  result.substring(1) : "" ;
    }
}
