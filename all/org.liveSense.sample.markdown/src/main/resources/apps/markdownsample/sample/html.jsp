<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page import="org.liveSense.core.wrapper.JcrNodeWrapper"%>
<%@page import="javax.jcr.NodeIterator"%>
<%@page import="javax.jcr.query.Query"%>
<%@page import="javax.jcr.query.QueryManager"%>
<%@page import="javax.jcr.Node"%>
<%@page import="org.liveSense.service.markdown.MarkdownService"%>
<%@page import="org.pegdown.PegDownProcessor"%>

<%@page session="false"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0"%>
<%@taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>
<sling:defineObjects />
<%
		// Get Node wrapper
		JcrNodeWrapper node = new JcrNodeWrapper(currentNode);
		pageContext.setAttribute("node", node);

		MarkdownService markdown_service = sling.getService(MarkdownService.class);

		String orig_text = currentNode.getProperty("content").getString();
		String html_text = markdown_service.markdownToHtml(orig_text);
		orig_text = orig_text.replace("\n", "<br />");
		String code_text = html_text.replace("<","&lt;");
		code_text = code_text.replace(">","&gt;");
		
		pageContext.setAttribute("originalText", orig_text);
		pageContext.setAttribute("htmlText", html_text);
		pageContext.setAttribute("codeText", code_text);
		
%>

					<div class="span6">
						<div class="row-fluid">
							<div class="span12">
								<h4>${node['title']}</h4>
								<p>${node['description']}</p>
							</div>
						</div>
						</br>

						<h5>Markdown text</h5>
						<div class="row-fluid">
							<div class="alert alert span12">
								${originalText}
							</div>
						</div>

						<h5>HTML output</h5>
						<div class="row-fluid">
							<div class="alert alert-info span12">
								${codeText}
							</div>
						</div>

						<h5>Output view</h5>
						<div class="row-fluid">
							<div class="span12 well">
								${htmlText}
							</div>
						</div>
					</div>
