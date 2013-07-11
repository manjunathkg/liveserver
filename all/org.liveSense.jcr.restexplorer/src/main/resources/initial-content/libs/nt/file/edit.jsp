<%--
/************************************************************************
 **     $Date: $
 **   $Source: $
 **   $Author: $
 ** $Revision: $
 ************************************************************************/
--%><%
%><%@page session="false" contentType="text/html; charset=utf-8" %><%
%><%@page import="java.io.*,
                  java.net.*,
									javax.jcr.*,
									org.apache.sling.api.resource.*,
                  utils.*" 
%><%
%><%@ taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %><%
%><%!
%><%
%><sling:defineObjects /><%
	String type = "unknown";
	if (currentNode.hasProperty("jcr:content/jcr:mimeType")) {
		String path = currentNode.getPath();
		String mimetype = currentNode.getProperty("jcr:content/jcr:mimeType").getString();

		if      (mimetype.equalsIgnoreCase("plain/text")) type = "text";
		else if (mimetype.equalsIgnoreCase("text/plain")) type = "text";
		else if (mimetype.equalsIgnoreCase("text/html")) type = "text";
		else if (path.endsWith(".jsp")) type = "text";
		else if (path.endsWith(".html"))type = "text";
		else if (path.endsWith(".css")) type = "text";
		else if (path.endsWith(".js"))  type = "text";
		else if (path.endsWith(".png")) type = "image";
	}
%><!DOCTYPE html>
<html>
	<sling:include resource="<%=resource%>" resourceType="rested/components/head" replaceSelectors="edit" />
	<body>
		<sling:include resource="<%=resource%>" resourceType="rested/components/navbar" replaceSelectors="edit"/>
		<div class="subnavbar">
			<sling:include resource="<%=resource%>" resourceType="rested/components/breadcrumb" replaceSelectors="edit"/>
		</div>
    <div class="container-fluid">

			<div class="btn-toolbar">
				<div class="btn-group">
					<a class="btn" href="<%=resource.getPath() + "/_jcr_content.edit.html"%>"><i class="icon-chevron-right"></i> view jcr:content</a>
					<a class="btn" href="<%=resource.getPath() + "/_jcr_content.properties.html"%>"><i class="icon-edit"></i> properties</a>
				</div>
			</div>

			<% if ("text".equalsIgnoreCase(type)) { %>
				<DIV style="width:100%">
					<sling:include resource="<%=resource%>" resourceType="rested/components/editor/text"/>
				</DIV>
			<% } else if ("image".equalsIgnoreCase(type)) { %>
				<DIV style="width:100%">
					<img src="<%=resource.getPath()%>"></img>
				</DIV>
			<% } else { %>
				<!-- IFRAME width="100%" src="<%=currentNode.getPath()%>"></IFRAME -->
			<% } %>
		</div>
	</body>
</html>
