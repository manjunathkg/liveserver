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
%><sling:defineObjects /><%
%><!DOCTYPE html>
<html>
	<sling:include resource="<%=resource%>" resourceType="rested/components/head"/>
	<body>
		<sling:include resource="<%=resource%>" resourceType="rested/components/navbar"/>
		<div class="subnavbar">
			<sling:include resource="<%=resource%>" resourceType="rested/components/breadcrumb"/>
		</div>
    <div class="container-fluid">
<%
	String query = request.getParameter("q");
	if (query == null) query = "";

	String error = request.getParameter("error");
	if (error != null) {
		%>
		<div class="alert alert-error">
		<a href="<%=slingRequest.getRequestURL()%>" class="close">&times;</a>
		<strong>Error while saving data!</strong>
		<%= error %>
		<p>
		You may have to <a href=#">login</a> before making any changes.
		</div>
		<%
	}
%>
			<div class="btn-toolbar">
				<form class="form-search form-inline">
					<div class="btn-group">
						<a class="btn" href="<%=resource.getPath() + ".create.html"%>"><i class=" icon-plus"></i> new</a>
						<a class="btn" href="<%=resource.getPath() + ".properties.html"%>"><i class="icon-edit"></i> properties</a>
					</div>
					<span class="pull-right">
					<div class="input-append">
						<input type="text" name="q" class="input-small search-query" value="<%=query%>">
						<button class="btn" type="submit"><i class="icon-search"></i></button>
					</div>
					</span>
				</form>
			</div>

			<% if (currentNode.hasNodes()) { %>
			<sling:include resource="<%=resource%>" resourceType="rested/components/pathlist"/>
			<% } else { %>
			<div class="alert alert-info">
				<a href="#" class="close"><i class="icon-circle-arrow-left"></i></a>
				<p>
				<h4 class="text-center">this node has no children</h4>
				<p>
			</div>
			<% } %>
		</div>
	</body>
</html>
