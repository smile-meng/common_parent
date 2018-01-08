package cn.zm.bos.utils;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter;

/**
 * 自定义过滤器
 * @author zhaomeng
 *
 */
public class MyStruts2Filter extends StrutsPrepareAndExecuteFilter{
	
	public void doFilter(ServletRequest req,ServletResponse res,FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest) req;
		
		String servletPath = request.getServletPath();
		
//		System.out.println(servletPath);
		
		if(servletPath.endsWith("/services")){
			//Servlet的请求
			chain.doFilter(req, res);
		}else{
			//filter的请求
			super.doFilter(req, res, chain);
		}
	}
}
