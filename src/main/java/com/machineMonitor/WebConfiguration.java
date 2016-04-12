package com.machineMonitor;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class WebConfiguration implements Filter {
	  private static Logger logger = Logger.getLogger(WebConfiguration.class); 


	    @Override
	    public void doFilter(ServletRequest req, ServletResponse res,FilterChain chain) throws IOException, ServletException {

	      logger.info("==========in filter============");
	      HttpServletResponse response = (HttpServletResponse) res;
	      response.setHeader("Access-Control-Allow-Origin", "*");
	      response.setHeader("Access-Control-Allow-Methods", "POST, GET, DELETE, PUT");
	      response.setHeader("Access-Control-Max-Age", "3600");
	      response.setHeader("Access-Control-Allow-Headers", "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

	      chain.doFilter(req, res);
	      logger.info("==========End filter============");
	    }

	    @Override
	    public void destroy() {}

	    @Override
	    public void init(FilterConfig arg0) throws ServletException {}


	 
}
