package org.liveSense.jcr.explorer.server;


import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.mime.MimeTypeService;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.apache.sling.jcr.api.SlingRepository;
import org.liveSense.service.securityManager.AccessRightsImpl;
import org.liveSense.service.securityManager.SecurityManagerService;
import org.liveSense.service.securityManager.SerializablePrivilege;
import org.liveSense.service.securityManager.exceptions.InternalException;
import org.liveSense.service.securityManager.exceptions.PrincipalNotExistsException;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


class UploadServletParameterProvider {
	public static final String PROP_TMPDIR = "tmpDir";
	public static final String VALUE_TMPDIR = "/jcrexplorer/tmp";

	public static String getTmpDir(ComponentContext context) {
		return PropertiesUtil.toString(context.getProperties().get(PROP_TMPDIR), VALUE_TMPDIR);
	}
}

@Component(immediate = true,  metatype = true, label = "%organization.post.servlet.post.name", description = "%organization.post.servlet.post.description")
@Service(value = Servlet.class)
@Properties({
	@Property(name = "service.description", value = "Jcr Explorer upload Post Servlet"),
	@Property(name = "service.vendor", value = "Ajanlom.hu"),
	@Property(name = "sling.servlet.paths", value = "/jcrexplorer/UploadServlet"),
	@Property(name = "sling.servlet.methods", value = {"GET","POST"}),
	@Property(name = UploadServletParameterProvider.PROP_TMPDIR, value = UploadServletParameterProvider.VALUE_TMPDIR)
})

public class UploadServlet extends SlingAllMethodsServlet {

	Logger log = LoggerFactory.getLogger(UploadServlet.class);
	
	ComponentContext context;
	
	@Reference(policy=ReferencePolicy.DYNAMIC, cardinality=ReferenceCardinality.MANDATORY_UNARY)
	MimeTypeService mimeService;
	
	@Reference(policy=ReferencePolicy.DYNAMIC, cardinality=ReferenceCardinality.MANDATORY_UNARY)
	SlingRepository repository;

	@Reference(policy=ReferencePolicy.DYNAMIC, cardinality=ReferenceCardinality.MANDATORY_UNARY)
	SecurityManagerService securityService;

	
	private Node getUserTemp(Session session) {
		Session adminSession = null;

		try {
			adminSession = repository.loginAdministrative(null);
			if (!adminSession.nodeExists(UploadServletParameterProvider.getTmpDir(context))) {
				Node node = adminSession.getRootNode();
				String[] patElements = UploadServletParameterProvider.getTmpDir(context).split("/");
				for (String path : patElements) {
					if (StringUtils.isNotEmpty(path)) {
						if (node.hasNode(path)) {
							node = node.getNode(path);
						} else {
							node = node.addNode(path);
						}
						node = node.addNode(path);
					}
				}
				
				AccessRightsImpl everyOneRights = new AccessRightsImpl();
				everyOneRights.getDenied().add(new SerializablePrivilege(SerializablePrivilege.JCR_ALL));

				// Last is temp. Nobody can see the contents
				securityService.setAclByName(adminSession, session.getUserID(), node.getPath(), everyOneRights);
				
				
				Node userTmpNode = null;
				if (node.hasNode(session.getUserID())) {
					userTmpNode = node.getNode(session.getUserID());
				} else {
					userTmpNode = node.addNode(session.getUserID());
					
					// Setting all rights for user
					AccessRightsImpl userRights = new AccessRightsImpl();
					userRights.getGranted().add(new SerializablePrivilege(SerializablePrivilege.JCR_ALL));
					securityService.setAclByName(adminSession, session.getUserID(), userTmpNode.getPath(), everyOneRights);
				}
				
				String path = userTmpNode.getPath();
				if (adminSession.hasPendingChanges()) {
					adminSession.save();
				}
				return session.getNode(path);
			}
		} catch (RepositoryException e) {
			log.error("Could not create Admin session", e);
		} catch (InternalException e) {
			log.error("Could not set access rights",e);
		} catch (PrincipalNotExistsException e) {
			log.error("Could not create Admin session");
		} finally {
			if (adminSession != null) {
				try {
					adminSession.logout();
				} catch (Throwable th) {
				}
			}
		}
		return null;
	}

	
	@Activate()
	protected void activate(ComponentContext context) {
		this.context = context;
	}
	
	@Override
	protected void doPost(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServletException,
			IOException {
		doGet(request, response);
	}

	
	@Override
	protected void doGet(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServletException,
			IOException {

		Session jcrSession = request.getResourceResolver().adaptTo(Session.class);
		Node root;

		Map<String, RequestParameter[]> requestParams = request.getRequestParameterMap();
		
		Iterator<String> iter = requestParams.keySet().iterator();
		
		while (iter.hasNext()) {
			String paramName = iter.next();
			RequestParameter[] params = requestParams.get(paramName);
			for (RequestParameter item : params) {
				//args.put("contentType", item.getContentType());
				if (!item.isFormField()) {
				
					String fileName = item.getFileName();
					int slash = fileName.lastIndexOf("/");
					if (slash < 0)
						slash = fileName.lastIndexOf("\\");
					if (slash > 0)
						fileName = fileName.substring(slash + 1);
					//args.put("fileName", fileName);
	
					InputStream in = null;
					try {
						//in = item.getInputStream();
						//writeToFile(request.getSession().getId() + "/" +fileName, in, true, request.getSession().getServletContext().getRealPath("/"));
	
						root = jcrSession.getRootNode();
						
						// create a new node of type nt:fileNode 
						Node myNewNode = getUserTemp(jcrSession).addNode(item.getFileName(), "nt:file");
						Node contentNode = myNewNode.addNode("jcr:content", "nt:resource");
	
						// set the mandatory properties
						contentNode.setProperty("jcr:data", jcrSession.getValueFactory().createBinary(item.getInputStream()));
						contentNode.setProperty("jcr:lastModified", Calendar.getInstance());contentNode.setProperty("jcr:mimeType", mimeService.getMimeType(item.getFileName()));
						jcrSession.save();
	
					} catch (Exception e) {
	//						e.printStackTrace();
						log.error("Fail to upload " + fileName);
						
						response.setContentType("text/html");
						response.setHeader("Pragma", "No-cache");
						response.setDateHeader("Expires", 0);
						response.setHeader("Cache-Control", "no-cache");
						PrintWriter out = response.getWriter();
						out.println("<html>");
						out.println("<body>");
						out.println("<script type=\"text/javascript\">");
						out.println("if (parent.uploadFailed) parent.uploadFailed('" + e.getLocalizedMessage().replaceAll("\'|\"", "") + "');");
						out.println("</script>");
						out.println("</body>");
						out.println("</html>");
						out.flush();
						return;
					} finally {
						if (in != null)
							try {
								in.close();
							} catch (Exception e) {
							}
					}
				}
			}
		}
		/*
		String action = request.getParameter("action");
		if (action == null) {
			error = "Imseretlen akció";
		} else if ("logoupload".equals(action)) {
			try {
				root = jcrSession.getRootNode();
				// create a new node of type nt:fileNode 
				Node myNewNode = root.getNode("content").addNode("mynewnode", "nt:file");
				Node contentNode = myNewNode.addNode("jcr:content", "nt:resource");
				// set the mandatory properties
				contentNode.setProperty("jcr:data", new BinaryValue(request.getParameter("myfile").getBytes("utf-8")));
				contentNode.setProperty("jcr:lastModified", Calendar.getInstance());contentNode.setProperty("jcr:mimeType", "mymimetype");
				jcrSession.save();

				error = "OK";
			} catch (AccessDeniedException e) {
				error = "Hozzáférés megtagadva";
			} catch (ConstraintViolationException e) {
				error = "Érvénytelen érték";
			} catch (LockException e) {
				error = "Zárolási hiba";
			} catch (PathNotFoundException e) {
				error = "Elérési út nem található";
			} catch (ItemExistsException e) {
				error = "Az adat már létezik";
			} catch (ItemNotFoundException e) {
				error = "Az adat nem található";
			} catch (NoSuchNodeTypeException e) {
				error = "Ismeretlen adattipus";
			} catch (UnsupportedEncodingException e) {
				error = "Nem támogatott karakterkódolás";
			} catch (Exception e) {
				error = "Ismeretlen hiba: "+e.getMessage();
			}
		} else if ("logoupload".equals(action)) {
			
		}

		response.setContentType("text/plain");
		response.setCharacterEncoding("utf-8");
		response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
		response.setHeader("Pragma","no-cache"); //HTTP 1.0
		response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
		response.getWriter().append(error);
		*/

	}

}
