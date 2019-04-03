package com.babymm.utils;

import java.util.ResourceBundle;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

//用于生成shiro加密后的密码
public class PasswordUtils {
	/**
	 * 生成shiro加密后的密码
	 * @param rawPassword   原始密码
	 */
	public static String encode(String rawPassword) {
		//获取加密方式
        String hashAlgorithmName = ResourceBundle.getBundle("config/shiro").getString("hashAlgorithmName");
        //获取加密的盐
        String saltSource = ResourceBundle.getBundle("config/shiro").getString("saltSource");    
        int hashIterations = Integer.parseInt(ResourceBundle.getBundle("config/shiro").getString("hashIterations"));
        ByteSource credentialsSalt = ByteSource.Util.bytes(saltSource);
        Object obj = new SimpleHash(hashAlgorithmName, rawPassword, credentialsSalt, hashIterations);
        return obj.toString();
    }
}
