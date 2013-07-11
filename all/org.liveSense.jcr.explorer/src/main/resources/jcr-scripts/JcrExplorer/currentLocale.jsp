<%@page import="org.apache.sling.api.scripting.SlingScriptHelper"%>
<%@page import="javax.jcr.Node"%>
<%@page import="org.apache.commons.lang.LocaleUtils"%>
<%@page import="org.apache.sling.commons.osgi.PropertiesUtil"%>
<%@page import="org.apache.jackrabbit.api.security.user.User"%>
<%@page import="javax.servlet.jsp.jstl.fmt.LocalizationContext"%>
<%@page import="java.util.ResourceBundle"%>
<%@page import="org.apache.sling.api.SlingHttpServletRequest"%>
<%@page import="java.util.Locale"%>
<%@page import="org.liveSense.service.languageselector.LanguageSelectorService"%>
<%@page import="org.liveSense.core.wrapper.JcrNodeWrapper"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%

{
	Node currentNode = (Node)pageContext.getAttribute("currentNode");
	SlingScriptHelper sling = (SlingScriptHelper)pageContext.getAttribute("slingScriptHelper");

	LanguageSelectorService langSelectorService = (LanguageSelectorService)sling.getService(LanguageSelectorService.class);
	Locale locale = Locale.getDefault();
	if (langSelectorService != null) {
		locale = langSelectorService.getLocaleByRequest(request);
	}	
	if (pageContext.getAttribute("userNode") != null) {
		User user = (User)pageContext.getAttribute("userNode");
		if (user.hasProperty("locale")) {
			locale = LocaleUtils.toLocale(user.getProperty("locale")[0].getString());
		}
	}
	if (locale == null) locale = LocaleUtils.toLocale("hu_HU");
	
	pageContext.setAttribute("locale", locale);
	pageContext.setAttribute("resourceBundle", new LocalizationContext(((SlingHttpServletRequest)request).getResourceBundle("hu.libra.libraweb.remoteinvoice.i18n.RemoteInvoiceMessages", locale), locale));
}
%>
<fmt:setLocale value="${locale}" />
