<%@page import="javax.jcr.Node"%>
<%@page import="org.apache.sling.api.resource.Resource"%>
<%@page import="org.apache.sling.api.scripting.SlingScriptHelper"%>
<%@page import="org.apache.sling.api.scripting.SlingBindings"%>
<%@page import="org.liveSense.core.wrapper.JcrNodeWrapper"%>


<%
{
	final String DEFAULT_REQUEST_NAME = "slingRequest";
	final String DEFAULT_RESPONSE_NAME = "slingResponse";
	final String DEFAULT_RESOURCE_NAME = "resource";
	final String DEFAULT_NODE_NAME = "currentNode";
	final String DEFAULT_BINDINGS_NAME = "bindings";
	final String DEFAULT_LOG_NAME = "log";
	final String DEFAULT_SLING_NAME = "slingScriptHelper";
	final String DEFAULT_RESOURCE_RESOLVER_NAME = "resourceResolver";

	final SlingBindings bindings = (SlingBindings)pageContext.getRequest().getAttribute(SlingBindings.class.getName());
	final SlingScriptHelper sling = bindings.getSling();

	pageContext.setAttribute(DEFAULT_REQUEST_NAME, sling.getRequest());
	pageContext.setAttribute(DEFAULT_RESPONSE_NAME, sling.getResponse());
	final Resource resource = sling.getRequest().getResource();
	pageContext.setAttribute(DEFAULT_RESOURCE_NAME, resource);
	pageContext.setAttribute(DEFAULT_RESOURCE_RESOLVER_NAME, sling.getRequest().getResourceResolver());
	pageContext.setAttribute(DEFAULT_SLING_NAME, sling);
	pageContext.setAttribute(DEFAULT_LOG_NAME, bindings.getLog());
	pageContext.setAttribute(DEFAULT_BINDINGS_NAME, bindings);
	final Object node = resource.adaptTo(Node.class);
	if (node != null) {
		pageContext.setAttribute(DEFAULT_NODE_NAME, node);
	}
}
%>
