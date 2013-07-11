<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page session="false"%>
<%@page import="java.util.Map"%>
<%@page import="org.liveSense.core.wrapper.GenericValue"%>
<%@page import="org.liveSense.service.securityManager.SecurityManagerService"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0"%>

<jsp:directive.include file="../sling.jsp" />
<jsp:directive.include file="../authentication.jsp" />
<jsp:directive.include file="../currentLocale.jsp" />
<jsp:directive.include file="../redirect.jsp" />
<%
	if (request.getParameter("errorMessage") != null) {
		String errorMessage = request.getParameter("errorMessage");
		if (errorMessage.equalsIgnoreCase("Username and Password do not match")) {
			pageContext.setAttribute("errorMessage", "${node.properties.errorUserNameAndPasswordDoesNotMatch}");
		} else if (errorMessage.equalsIgnoreCase("Session timed out, please login again")) {
			pageContext.setAttribute("errorMessage", "${node.properties.errorSessionTimedOut}");
		} else {
			pageContext.setAttribute("errorMessage", "${node.properties.errorUnknown}"+errorMessage);
		}
	}

	if (request.getParameter("resource") != null) {
		pageContext.setAttribute("resource", request.getParameter("resource"));
	}

%>
<c:if test="${isAdmin}">
	<c:redirect>/</c:redirect>
</c:if>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta charset="utf-8">
	<title>${node.properties.loginText}</title>
	<meta name="description" content="Libra SAAS"/>
	<jsp:directive.include file="../head-metatags.jsp" />
	<jsp:directive.include file="../head-bootstrap.jsp" />
	<link href="css/login.css" rel="stylesheet">
</head>
<body>
	<jsp:directive.include file="../currentNode.jsp" />

	<div class="container">
		<div class="content login-content" >
			<div class="row">
				<div class="login-form">
					<h2>${node.properties.loginText}</h2>
					<form id="loginform" name="loginform" method="post"
						action="/j_security_check" enctype="multipart/form-data"
						accept-charset="UTF-8">
						<fieldset>
							<c:if test="${!empty errorMessage}">
								<div class="clearfix alert alert-error">
									<div>
										<h4>${node.properties.loginText}</h4>
										<p>${errorMessage}</p>
									</div>
								</div>
							</c:if>
							<div class="input-prepend">
								<span class="add-on">
									<i class="icon-user"></i>
								</span>
								<input type="text" placeholder="${node.properties.userNameLabel}" name="j_username" id="j_username">
							</div>
							<div class="input-prepend">
								<span class="add-on">
									<i class="icon-lock"></i>
								</span>
								<input type="password" placeholder="${node.properties.passwordLabel}"  name="j_password" id="j_password">
							</div>
							<button class="btn btn-primary btn-large" type="submit">${node.properties.submitButton}</button>
							<input type="hidden" name="_charset_" value="UTF-8" /> 
							<input type="hidden"name="resource" value="${resource}" />
						</fieldset>
					</form>
					
				</div>
			</div>			
		</div>
		
	</div> <!-- /container -->
	
	<jsp:directive.include file="../javascript-jquery.jsp" />
	<jsp:directive.include file="../javascript-bootstrap.jsp" />
	
</body>

</html>
