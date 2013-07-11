<%@page import="javax.jcr.Node"%>
<%@page import="org.apache.sling.api.scripting.SlingScriptHelper"%>
<%@page import="org.apache.jackrabbit.api.security.user.User"%>
<%@page import="org.apache.jackrabbit.api.security.user.Group"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="org.liveSense.service.securityManager.SecurityManagerService"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="org.apache.sling.jcr.jackrabbit.accessmanager.PrivilegesInfo"%>
<%@page import="org.apache.sling.api.resource.ValueMap"%>
<%@page import="org.liveSense.core.wrapper.JcrNodeWrapper"%>
<%
{
	SlingScriptHelper sling = (SlingScriptHelper)pageContext.getAttribute("slingScriptHelper");
	Node currentNode = (Node)pageContext.getAttribute("currentNode");
		
	pageContext.setAttribute("canEdit", (new PrivilegesInfo()).canModifyProperties(currentNode));
	pageContext.setAttribute("userName",  request.getRemoteUser());
		

	if (request.getRemoteUser() != null && !request.getRemoteUser().equals("") && !request.getRemoteUser().equals("anonymous")) {
		pageContext.setAttribute("authenticated", true);
		try {
			User user = ((SecurityManagerService)sling.getService(SecurityManagerService.class)).getUserByName(currentNode.getSession(), request.getRemoteUser());
			pageContext.setAttribute("userNode", user);
		} catch (Throwable th) {
		}
	} else {
		pageContext.setAttribute("authenticated", false);
	}
		
	Boolean isAdmin = false;
	if ((SecurityManagerService)sling.getService(SecurityManagerService.class) != null && StringUtils.isNotEmpty(request.getRemoteUser())) {
		List<Group> groups = new ArrayList<Group>();
		try {
			groups = ((SecurityManagerService)sling.getService(SecurityManagerService.class)).getEffectiveMemberOfByName(currentNode.getSession(), request.getRemoteUser());
			
			for (Group grp : groups) {
				if (grp.getID().equals("administrators")) {
					isAdmin = true;
					pageContext.setAttribute("isAdmin", new Boolean(true));
				}
				// Other group specific settings
			}
		} catch (Exception e) {
		}
	}
}
%>
