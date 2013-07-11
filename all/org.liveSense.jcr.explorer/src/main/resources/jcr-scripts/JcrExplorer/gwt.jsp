<%
// Check if debug mode
String moduleName = "Ajanlom";
if (request.getParameter("gwt.codesvr") != null || request.getParameter("development") !=null) {
	moduleName = "AjanlomDevelopment";
}
pageContext.setAttribute("moduleName", moduleName);
%>
