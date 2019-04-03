package com.babymm.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 对forgetpswd2.html访问设置的防盗链访问，该页面只能从forgetpswd.html跳转过来
 * 
 * @author yluo0
 *
 */
public class Forget2PasswordFilter implements Filter {

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain filter)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) arg0;
		HttpServletResponse response = (HttpServletResponse) arg1;
		String uri = request.getRequestURI(); // 访问的uri
		String url = request.getRequestURL().toString(); // 访问的url
		String contextPath = request.getContextPath();
		System.out.println("访问的页面是：" + url);
		System.out.println("前端页面是：" + request.getHeader("Referer"));
		// 将url最后面的//forgetpswd2.html替换成/forgetpswd.html

		System.out.println("我被拦截了");
		String ref = request.getHeader("Referer");
		String url_reference = url.replace("forgetpswd2.html", "forgetpswd.html");

		if (ref == null || (!ref.equals(url_reference))) { // 如果不是从忘记密码第一页过来的就直接跳转至403页面
			response.sendRedirect(contextPath + "/403.html");
		}

		filter.doFilter(request, response);

	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}

}
