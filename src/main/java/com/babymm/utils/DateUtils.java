package com.babymm.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 产生一些年相关的工具类
 * @author yluo0
 *
 */
public class DateUtils {
	
	/**
	 * 产生当年第一天，//2019-01-01 00:00:00
	 * @return
	 */
	public static Date FirstDayOfCurrentYear() {
		Calendar calendar = Calendar.getInstance(Locale.CHINESE);
		int currentYear = calendar.get(Calendar.YEAR);
		calendar.set(currentYear,0,1,0,0,0);
		return calendar.getTime();		
	}
	
	/**
	 * 产生下一年第一天，//2020-01-01 00:00:00
	 * @return
	 */
	public static Date FirstDayOfNextYear() {
		Calendar calendar = Calendar.getInstance(Locale.CHINESE);
		int currentYear = calendar.get(Calendar.YEAR);
		calendar.set(currentYear+1,0,1,0,0,0);
		return calendar.getTime();		
	}
	
	/**
	 * 产生去年第一天，//2018-01-01 00:00:00
	 * @return
	 */
	public static Date FirstDayOfPreYear() {
		Calendar calendar = Calendar.getInstance(Locale.CHINESE);
		int currentYear = calendar.get(Calendar.YEAR);
		calendar.set(currentYear-1,0,1,0,0,0);
		return calendar.getTime();
	}

}
