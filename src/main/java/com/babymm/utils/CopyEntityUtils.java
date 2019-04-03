package com.babymm.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
/**
 * 对象属性值复制
 * @author yluo0
 *
 */
public class CopyEntityUtils {

	// 将父类对象中的属性值全部装填到子类中
	public static void copyFatherToChild(Object child, Object father) {
		if (!(child.getClass().getSuperclass() == father.getClass())) {
			System.err.println("传入参数不是该类的父类");
		}
		// 获取父类对象
		Class fatherClazz = father.getClass();

		// 获取父类的所有属性
		Field[] fields = fatherClazz.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i]; // 获取到每一个属性
			field.setAccessible(true); // 设置其为可见
			String fieldName = field.getName(); // 获取到属性所对应的名字
			try {
				// 根据名字，获取到其所对对应的方法
				Method m = fatherClazz.getMethod("get" + upperHeadChar(fieldName));
				// 执行方法获取到属性值
				Object value = m.invoke(father);
				// 将该属性设置到子类上
				field.set(child, value);
			} catch (NoSuchMethodException e) {

				e.printStackTrace();
			} catch (SecurityException e) {

				e.printStackTrace();
			} catch (IllegalAccessException e) {

				e.printStackTrace();
			} catch (IllegalArgumentException e) {

				e.printStackTrace();
			} catch (InvocationTargetException e) {

				e.printStackTrace();
			}
		}
	}

	// 将首字母大写返回
	private static String upperHeadChar(String fieldName) {
		return fieldName.substring(0, 1).toUpperCase() + fieldName.subSequence(1, fieldName.length());
	}

}
