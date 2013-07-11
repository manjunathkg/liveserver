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
			<form method="post" action="/j_security_check" enctype="MULTIPART/FORM-DATA" accept-charset="UTF-8" class="form-horizontal">
				<div class="modal">
					<div class="modal-header">
						<h3>Login <a href="#"><%= resource.getPath() %></a></h3>
						<a href="<%= resource.getPath() + ".edit.html" %>" class="btn">back</a>
					</div>

					<div class="modal-body">
							<input type="hidden" name="_charset_" value="UTF-8">
							<input type="hidden" name="resource" value="<%= resource.getPath() %>.edit.html">
							<input type="hidden" name="selectedAuthType" value="form">

							<div class="control-group">
								<label class="control-label" for="j_username">name</label>
								<div class="controls">
									<input id="j_username" name="j_username" type="text" accesskey="u">
								</div>
							</div>
							<div class="control-group">
								<label class="control-label" for="j_password">password</label>
								<div class="controls">
									<input id="j_password" name="j_password" type="password" accesskey="p">
								</div>
							</div>
					</div>

					<div class="modal-footer">
						<a class="btn" href="system/sling/logout.html">Logout</a>
						<button class="btn btn-primary" type="submit">Login</button>
					</div>
				</div>
			</form>
		</div>
	</body>
</html>
