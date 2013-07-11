package org.liveSense.jcr.explorer.server;

import java.io.InputStream;

import javax.jcr.Item;
import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.jcr.api.SlingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Servlet is used for returing binary property values.
 */

@Component(immediate = true,  metatype = true, label = "%jcrexplorer.binaryservlet.name", description = "%jcrexplorer.binaryservlet.description")
@Service(value = Servlet.class)
@Properties({
	@Property(name = "service.description", value = "%jcrexplorer.binaryservlet.description"),
    @Property(name = "service.vendor", value = "org.liveSense"),
	@Property(name = "sling.servlet.paths", value = "/jcrexplorer/BinaryServlet"),
    @Property(name = "sling.servlet.methods", value = {"GET","POST"})
})
public class BinaryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Reference
	private SlingRepository repository;
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
		
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		// TODO: Authentication here
		
		Session session = null;
		try {
			String binaryPath = request.getParameter("path");
			log.info("Binary Content Path: "+ binaryPath);
			session = repository.loginAdministrative(null);
			
			Item it = session.getItem(binaryPath);
			if (it instanceof javax.jcr.Property) {
				javax.jcr.Property property = (javax.jcr.Property) it;
				InputStream inputStream = property.getBinary().getStream();
				ServletOutputStream servletOutputStream = response.getOutputStream();
				if (null != request.getParameter("mimeType") && !request.getParameter("mimeType").equals("")) {
					response.setContentType(request.getParameter("mimeType"));
					response.setHeader("Content-Disposition", " filename=" + getFileName(binaryPath) );
				}
				IOUtils.copy(inputStream, servletOutputStream);
				servletOutputStream.flush();
				return;
			}

		} catch (Exception e) {
			log.error("Failed to return binary stream! ", e);
		} finally {
			try {
				session.logout();
			} catch (Exception e) {
				log.warn("problems while logout from repository ", e);
			}
		}
	}
	
	private String getFileName(String binaryPath) {
		String[] pathArray = binaryPath.split("/");
		return pathArray[pathArray.length-3];
	}

}
