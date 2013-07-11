<%@page import="org.apache.sling.api.scripting.SlingScriptHelper"%>
<%@page import="javax.jcr.Node"%>
<%@page import="org.liveSense.core.wrapper.GenericValue"%>
<%@page import="java.util.Map"%>
<%@page import="org.liveSense.service.securityManager.SecurityManagerService"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%
{
	
	Node currentNode = (Node)pageContext.getAttribute("currentNode");
	SlingScriptHelper sling = (SlingScriptHelper)pageContext.getAttribute("slingScriptHelper");

	String resourcePath = (String)request.getParameter("resource");
	
	// If redirect is not defined, we tries determinate the user's default path
	if (StringUtils.isEmpty(resourcePath)) {
		SecurityManagerService sm = (SecurityManagerService)sling.getService(SecurityManagerService.class);
		if (sm != null && StringUtils.isNotEmpty(request.getRemoteUser())) {
			Map<String, GenericValue> userProps = null;
			try {
				userProps = sm.getPrincipalPropertiesByName(currentNode.getSession(), request.getRemoteUser());
			} catch (Exception e) {
			}
			if (userProps != null && userProps.containsKey("defaultPath") && StringUtils.isNotEmpty(userProps.get("defaultPath").get().getString())) {
				resourcePath = userProps.get("defaultPath").get().getString();
				
				// If user is authenticated and have default path redirect it there
				//response.sendRedirect("/"+resourcePath);
			}
		}
		//PrivilegesInfo privilegesInfo = new PrivilegesInfo();
		//privilegesInfo.canModifyProperties(currentNode.getSession(), rscUrl);
	}
	pageContext.setAttribute("resource", resourcePath);
}
%>
