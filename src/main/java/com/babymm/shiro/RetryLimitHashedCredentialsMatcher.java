package com.babymm.shiro;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;

public class RetryLimitHashedCredentialsMatcher extends HashedCredentialsMatcher {

	private Cache<String, AtomicInteger> passwordRetryCache;

	public RetryLimitHashedCredentialsMatcher(CacheManager cacheManager) {
		passwordRetryCache = cacheManager.getCache("passwordRetryCache");
	}

	@Override
	public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
		// 获取用户名
		String username = (String) token.getPrincipal();
		// 获取用户登录次数
		AtomicInteger retryCount = passwordRetryCache.get(username);
		if (retryCount == null) {
			// 如果用户没有登陆过,登陆次数加1 并放入缓存
			retryCount = new AtomicInteger(0);
			passwordRetryCache.put(username, retryCount);
		}
		if (retryCount.incrementAndGet() > 3) {
			// 如果用户登陆失败次数大于3次 抛出锁定用户异常
			throw new LockedAccountException("错误次数过多，请24小时后尝试");
		}
		// 判断用户账号和密码是否正确
		boolean matches = super.doCredentialsMatch(token, info);
		if (matches) {
			// 如果正确,从缓存中将用户登录计数 清除
			passwordRetryCache.remove(username);
		}
		return matches;
	}

	@Override
	public void setHashAlgorithmName(String hashAlgorithmName) {
		super.setHashAlgorithmName(hashAlgorithmName);
	}

	@Override
	public void setHashIterations(int hashIterations) {
		super.setHashIterations(hashIterations);
	}
	
	
	
	

}
