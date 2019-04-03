package com.babymm.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EmailUtils {
	
	public static boolean isEmail(String email) {
		  String regExp = "^[A-Za-z0-9]{1}([A-Za-z0-9_.])*[A-Za-z0-9]{1}@(163.com|qq.com)$"; 
	        Pattern p = Pattern.compile(regExp);  
	        Matcher m = p.matcher(email);  
	        return m.matches();  
	}
}
