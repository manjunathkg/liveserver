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
                  utils.*" 
%><%
%><%@ taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %><%
%><sling:defineObjects /><%
%>
<div class="navbar navbar-inverse navbar-static-top">
	<div class="navbar-inner">
		<div class="container-fluid">
			<a class="brand" href="/">RESTed</a>
			<div class="nav-collapse collapse">
				<ul class="nav">
					<li class="">
						<a href="/">Home</a>
					</li>
					<li class="">
						<a href="<%= resource.getPath()+".authenticate.html"%>">Login</a>
					</li>
					<li class="">
						<a href="/system/console">Console</a>
					</li>
					<li>
						<a href="#"><i class="icon-user icon-white"></i></a>
					</li>
				</ul>
			</div>
		</div>
	</div>
</div>
