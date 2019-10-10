package nl.phoneplaats.phoneplaats;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ClickjackingFilter implements Filter{
	private static final Logger logger = LoggerFactory.getLogger(ClickjackingFilter.class);

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		httpResponse.addHeader("X-Frame-Options","Deny");		
		chain.doFilter(request, httpResponse);		
	}

}
