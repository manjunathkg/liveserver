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
	String text = currentNode.getProperty("jcr:content/jcr:data").getString();
%>
<FORM METHOD="POST" ACTION="<%=currentNode.getPath()+"/_jcr_content" %>" ENCTYPE="MULTIPART/FORM-DATA">
	<INPUT TYPE="HIDDEN" NAME=":redirect" VALUE="<%=slingRequest.getRequestURL()%>" />
	<TEXTAREA name="jcr:data" style="width:100%" rows="20"><%=text%></TEXTAREA>
	<BUTTON type="submit">save</BUTTON>
</FORM>
