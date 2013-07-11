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
	<sling:include resource="<%=resource%>" resourceType="rested/components/head" replaceSelectors="edit"/>
	<body style="background-color:gray">
		<div class="container-fluid">
			<div class="modal">
				<div class="modal-header">
    			<a class="close" href="<%= resource.getPath() + ".edit.html" %>">&times;</a>
					<h3>Create Node</h3>
 				</div>

  			<div class="modal-body">
					<div class="alert alert-info"><i class="icon-folder-open"></i> <%= resource.getPath() %></div>
					<sling:include resource="<%=resource%>" resourceType="rested/components/createnode" replaceSelectors="edit"/>
				</div>
			</div>
		</div>
	</body>
</html>
