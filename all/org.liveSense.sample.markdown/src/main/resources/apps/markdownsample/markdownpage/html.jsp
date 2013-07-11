<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page import="org.liveSense.service.languageselector.LanguageSelectorService"%>
<%@page import="javax.security.auth.callback.LanguageCallback"%>
<%@page import="org.liveSense.core.wrapper.RequestWrapper"%>
<%@page import="java.util.Locale"%>
<%@page import="org.liveSense.service.markdown.MarkdownWrapper"%>
<%@page import="org.liveSense.core.wrapper.JcrNodeWrapper"%>
<%@page import="javax.jcr.NodeIterator"%>
<%@page import="javax.jcr.query.Query"%>
<%@page import="javax.jcr.query.QueryManager"%>
<%@page import="javax.jcr.Node"%>
<%@page import="org.liveSense.service.markdown.MarkdownService"%>

<%@page session="false"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.1"%>
<%@taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>
<sling:defineObjects />
<%
		// Get Node wrapper
		LanguageSelectorService languageSelectorService = sling.getService(LanguageSelectorService.class);
		Locale locale =  languageSelectorService.getLocaleByRequest(request);
		JcrNodeWrapper node = new JcrNodeWrapper(currentNode, locale, true);

		pageContext.setAttribute("markdown", new MarkdownWrapper(sling.getService(MarkdownService.class)));
		pageContext.setAttribute("node", node);
%>

<html xmlns="http://www.w3.org/1999/xhtml" dir="ltr" lang="en-US"
	xml:lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7" />
<meta name="viewport" content="width=device-width, initial-scale=1.0">

	<title>${node.properties['title']}</title> <!-- Le styles -->
	<link href="css/bootstrap.css" rel="stylesheet">
		<style>
body {
	padding-top: 60px;
	/* 60px to make the container go all the way to the bottom of the topbar */
}
</style>
<link href="css/bootstrap-responsive.css" rel="stylesheet" />

<!-- The HTML5 shim, for IE6-8 support of HTML5 elements -->
<!--[if lt IE 9]>
      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
<![endif]-->

<!-- Le fav and touch icons -->
<link rel="shortcut icon" href="../assets/ico/favicon.ico" />
<link rel="apple-touch-icon-precomposed" sizes="144x144"
	href="../assets/ico/apple-touch-icon-144-precomposed.png" />
<link rel="apple-touch-icon-precomposed" sizes="114x114"
	href="../assets/ico/apple-touch-icon-114-precomposed.png" />
<link rel="apple-touch-icon-precomposed" sizes="72x72"
	href="../assets/ico/apple-touch-icon-72-precomposed.png" />
<link rel="apple-touch-icon-precomposed"
	href="../assets/ico/apple-touch-icon-57-precomposed.png"/>
</head>

<body>

	<div class="navbar navbar-inverse navbar-fixed-top">
		<div class="navbar-inner">
			<div class="container-fluid">
				<a class="btn btn-navbar" data-toggle="collapse"
					data-target=".nav-collapse"> <span class="icon-bar"></span> <span
					class="icon-bar"></span> <span class="icon-bar"></span>
				</a> <a class="brand" href="#">liveSense Samples</a>
				<div class="nav-collapse collapse">
					<ul class="nav">
						<c:set var="query" value="SELECT * FROM [markdownsample:markdownpage] WHERE [portalName] = 'markdownsample' ORDER BY [menuOrder]"/>
						<c:forEach var="n"
							items="${node.SQL2Query[query]}">
							<c:choose>
								<c:when test="${n.name == node.name}">
									<li class="active"><a href="${n.name}.html">${n.properties['menu']}</a></li>
								</c:when>
								<c:otherwise>
									<li><a href="${n.name}.html">${n.properties['menu']}</a></li>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</ul>
				</div>
				<!--/.nav-collapse -->
			</div>
		</div>
	</div>

	<div class="container-fluid">
		${node.properties['content']}

		<c:if test="${node.name == 'index'}">
			<div class="row-fluid">
				<div class="span12">				
					<div class="row-fluid">
						<div class="span7">
							<div class="hero-unit">
								<h3>liveSense Markdown Sample</h3>
								<p>The liveSense web framework integrates the Pegdown
									pure-Java Markdown processor. This sample application gives
									basic informations and shows how to use the Pegdown parser in
									the server-side of your liveSense-based web-application.</p>
								<p>
									<a href="http://www.github.com/sirthias/pegdown#readme"
										class="btn btn-primary btn-large">Learn more about Pegdown
										»</a>
								</p>
							</div>
						</div>

						<div class="span5">
							<div class="row-fluid">
								<div class="span12">
									<legend>Bundle informations</legend>
									<dl class="dl-horizontal">
										<dt>Name</dt>
										<dd>org.liveSense.service.markdown</dd>
										<dt>Bundle version</dt>
										<dd>1.0.0-SNAPSHOT</dd>
										<dt>Pegdown version</dt>
										<dd>1.1.0</dd>
									</dl>
								</div>
							</div>

							<div class="row-fluid">
								<div class="span12">
									<legend>Links and references</legend>
									<ul>
										<li><a href="https://github.com/sirthias/pegdown">Pegdown pure Java Markdown processor on GitHub</a></li>
										<li><a href="http://daringfireball.net/projects/markdown/syntax">Markdown syntax by John Gruber</a></li>
										<li><a href="http://github.github.com/github-flavored-markdown/">GitHub Flavored Markdown</a></li>
										<li><a href="http://michelf.ca/projects/php-markdown/">PHP Markdown Extra</a></li>
										<li><a href="http://twitter.github.com/bootstrap/index.html">Bootstrap Framework</a></li>
									</ul>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</c:if>

		<c:if test="${node.name != 'index'}">
			<!-- Example row of columns -->
			<div class="row-fluid">
				<div class='span12'>
				
					<c:set var="query" value = "SELECT * FROM [markdownsample:sample] WHERE [portalName] = \"markdownsample\" AND [parentMenu] = \"${node.name}\" ORDER BY [displayOrder]"/>
					<c:set var="iteratedElements" value = "0"/>
					<c:forEach var="n" items="${node.SQL2Query[query]}">
						<c:set var="iteratedElements" value = "${iteratedElements+1}"/>
						
						<c:if test="${iteratedElements % 2 == 1}">
							<div class='row-fluid'>
						</c:if>
						<div class="span6">
							<div class="row-fluid">
								<div class="span12">
									<h4>${n.properties['title']}</h4>
									<p>${n.properties['description']}</p>
								</div>
							</div>
							</br>
	
							<h5>Markdown text</h5>
							<div class="row-fluid">
								<div class="alert alert span12">
									<c:out value="${n.properties['content'] }"/>
								</div>
							</div>
							
							<h5>HTML output</h5>
							<div class="row-fluid">
								<div class="alert alert-info span12">
									<c:out value="${markdown[n.properties['content']]}"></c:out>
								</div>
							</div>
	
							<h5>Output view</h5>
							<div class="row-fluid">
								<div class="span12 well">
									${markdown[n.properties['content']]}
								</div>
							</div>
						</div>
						<c:if test="${iteratedElements % 2 == 0}">
							</div>
						</c:if>
						
					</c:forEach>
					
					<!-- If there is no pair of last block close the tag -->
					<c:if test="${iteratedElements % 2 == 1}">
						</div>
					</c:if>
					
					<c:if test="${iteratedElements == 0}">
						<div class='span12'><h4>No sample in the JCR repository!</h4></div>
					</c:if>
				</div>
			</div>
		</c:if>

	</div>
	<hr>
		<footer>
		<div class="container-fluid">

			<div class="row-fluid">
				<div class='span12'>
					<p>&copy; 2012 liveSense.org</p>
				</div>
			</div>
		</div>
		</footer>

	</div>
	<!-- /container -->
	<!-- Placed at the end of the document so the pages load faster -->
	<script src="js/jquery-1.8.2.min.js"></script>
	<script src="js/bootstrap.js"></script>
</body>

</html>
