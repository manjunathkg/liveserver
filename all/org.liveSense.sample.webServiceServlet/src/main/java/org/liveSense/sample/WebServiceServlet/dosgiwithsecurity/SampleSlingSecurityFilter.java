package org.liveSense.sample.WebServiceServlet.dosgiwithsecurity;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.auth.core.AuthenticationSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A filter that requires a valid JCR user name
 */
public class SampleSlingSecurityFilter implements Filter {
	private final Logger LOG = LoggerFactory.getLogger(SampleSlingSecurityFilter.class);
	AuthenticationSupport auth;

	public void setAuthenticationSupport(AuthenticationSupport auth) {
		this.auth = auth;
	}

	public void destroy() {
		LOG.info("destroy()");
	}

	protected String getAuthenticatedUser(HttpServletRequest request) {
		return (String)request.getAttribute("org.osgi.service.http.authentication.remote.user");
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
	ServletException {
		LOG.info("Do filtering on URL: "+((HttpServletRequest)request).getRequestURL().toString());

		if (auth == null) {
			LOG.warn("No authentication service");
			((HttpServletResponse)response).sendError(HttpServletResponse.SC_FORBIDDEN);
		} else  {
			boolean valid = auth.handleSecurity((HttpServletRequest)request, (HttpServletResponse)response);
			if (!valid || StringUtils.isEmpty(getAuthenticatedUser((HttpServletRequest)request)) || getAuthenticatedUser((HttpServletRequest)request).equalsIgnoreCase("anonymous")) {
				LOG.info("Unauthorized user!");
				// The user is unauthenticated, request login
				//request.setAttribute("sling:authRequestLogin", "true");
				//auth.handleSecurity((HttpServletRequest)request, (HttpServletResponse)response);
				((HttpServletResponse)response).setHeader("WWW-Authenticate", "No HTTP Basic Info presented");
				((HttpServletResponse)response).sendError(401);
			} else {
				LOG.info("Authenticated user: "+getAuthenticatedUser((HttpServletRequest)request));
				chain.doFilter(request, response);
			}
		}
	}


	public void init(FilterConfig config) throws ServletException {
		LOG.info("init()");
	}
}