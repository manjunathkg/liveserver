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
									javax.jcr.query.QueryResult,
                  javax.jcr.query.QueryManager,
                  javax.jcr.query.Query,
									org.apache.sling.api.*,
                  utils.*"
%><%@ include file="/apps/rested/components/utils.jsp" %>
<%@ taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %><%
%><%!

String buildQuery (String query) {
	if (query.indexOf("/") > 1) { //assume this is relative path
		return "SELECT * FROM [nt:file] as N WHERE ISDESCENDANTNODE('/apps/"+query+"') or ISDESCENDANTNODE('/libs/"+query+"')";
	}
	else if (query.indexOf("/") == 0) { //assume this is absolute path
		return "SELECT * FROM [nt:base] as N WHERE ISDESCENDANTNODE('"+query+"')";
	}
	else {
		return "SELECT * FROM [nt:base] as N WHERE contains(N.*, '"+query+"')";
	}
}

NodeIterator listNodes (Node currentNode) throws Exception {
	NodeIterator children = currentNode.getNodes();
	return children;
}

NodeIterator searchNodes (SlingHttpServletRequest req, String q) throws Exception {
	String queryType = "JCR-SQL2";
	String statement = buildQuery (q);
	Session session = req.getResourceResolver ().adaptTo (Session.class);
	QueryManager queryManager = session.getWorkspace().getQueryManager ();
	Query query = queryManager.createQuery (statement, queryType);

	QueryResult result = query.execute ();
	NodeIterator nodeIter = result.getNodes ();
	return nodeIter;
}

%><%
%><sling:defineObjects /><%

String requestPath = getRequestPath(slingRequest);
String requestSelector = getRequestSelectorExtension(slingRequest);
String suffix = slingRequest.getRequestPathInfo().getSuffix();

%>

	<table class="table table-condensed pathlist">
		<thead>
			<th class="nodetype"></th>
			<th>name</th>
			<th>type</th>
			<th>rtype</th>
			<th></th>
		</thead>
		<tbody>
	<%
		NodeIterator children = null;
		String q = request.getParameter("q");

		if (q != null) children = searchNodes((SlingHttpServletRequest)request, q);
		else children = listNodes(currentNode);
		
		while (children.hasNext ()) {
			Node node = children.nextNode();
			String type = node.getProperty("jcr:primaryType").getString();
			String name = node.getName();
			String path = node.getPath();
			Node parent = node.getParent();

			String ppath = "/";
			if (parent != null) ppath = parent.getPath();

			String rtype = null;
			if (node.hasProperty("sling:resourceType")) rtype = node.getProperty("sling:resourceType").getString();

			if (suffix != null) path = requestPath + path;
			else path = path + "." + requestSelector;
	%>
			<tr>
				<td><i class="<%=iconForType(type)%>"></i></td>
				<td><a href="<%=path%>"><%=name + (isFolder(type)?"/":"")%></a>
					<% if (q != null) { %>
						<br/><%=ppath%> <a href="<%=ppath%>.edit.html"><i class="icon-circle-arrow-right"></i></a>
					<% } %>
				</td>
				<td><%=type%></td>
				<td>
					<% if (rtype != null) { %>
						<%=rtype%> <a href="/.edit.html?q=<%=rtype%>"><i class="icon-search"></i></a>
					<% } %>
				</td>
				<td>
					<div class="btn-group pull-right">
						<a class="btn btn-mini" href="<%=node.getPath()+".properties.html/"%>"><i class="icon-edit icon-white"></i></a>
						<a class="btn btn-mini" href="<%=node.getPath()+".moveto.html/"%>"><i class="icon-arrow-right icon-white"></i></a>
						<a class="btn btn-mini" href="<%=node.getPath()+".copyto.html/"%>"><i class="icon-plus icon-white"></i></a>
						<a class="btn btn-mini" href="<%=node.getPath()+".remove.html"%>"><i class="icon-trash icon-white"></i></a>
					</div>
				</td>
			</tr>
	<%
		}
		String parentPath = "/";
		if (currentNode.getDepth() > 0) parentPath = currentNode.getParent().getPath();

		String newContent = currentNode.getPath();
		if (newContent.equals("/")) newContent = "/*";
		else newContent += "/*";
	%>
		</tbody>
	</table>

