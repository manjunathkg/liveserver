<%@page import="org.apache.sling.api.scripting.SlingScriptHelper"%>
<%@page import="javax.jcr.Node"%>
<%@page import="org.liveSense.core.wrapper.JcrNodeWrapper"%>
<%
{
	Node currentNode = (Node)pageContext.getAttribute("currentNode");
	SlingScriptHelper sling = (SlingScriptHelper)pageContext.getAttribute("slingScriptHelper");

	// Get Node wrapper
	pageContext.setAttribute("node", new JcrNodeWrapper(currentNode));
	pageContext.setAttribute("currentNode", currentNode);
}
%>
