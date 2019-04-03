package com.babymm.shiro;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
/**
 * 重写方法，实现登陆成功后，固定定向到固定页面
 * @author yluo0
 *
 */
public class LoginFormAuthenticationFilter extends FormAuthenticationFilter {

	@Override
	protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request,
			ServletResponse response) throws Exception {		
		WebUtils.getAndClearSavedRequest(request);		//清除保存在session中的原请求路径
		WebUtils.redirectToSavedRequest(request, response, "/index.jsp");		//重新定向到首页
		return false;	
	}

	@Override
	protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request,
			ServletResponse response) {
		request.setAttribute("errorMessage", "用户名或密码错误");
		return super.onLoginFailure(token, e, request, response);
	}
	
	
}
