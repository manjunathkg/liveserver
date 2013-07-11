<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page import="org.apache.commons.lang.StringUtils"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=9" />
<title>voszkvbsz</title>
<link rel="shortcut icon" href="favicon.ico" />
</head>

<%@page import="org.liveSense.service.solr.api.SolrServerService"%>
<%@page import="org.apache.solr.client.solrj.SolrServer"%>
<%@page import="org.apache.solr.common.SolrDocument"%>
<%@page import="org.apache.solr.common.SolrDocumentList"%>
<%@page import="org.apache.solr.client.solrj.response.QueryResponse"%>
<%@page import="org.apache.solr.client.solrj.SolrQuery"%>
<%@page import="org.liveSense.service.solr.impl.SolrServerServiceImpl"%>
<%@page import="org.apache.sling.jcr.jackrabbit.accessmanager.PrivilegesInfo"%>
<%@page import="org.apache.sling.api.resource.ValueMap"%>
<%@page import="org.liveSense.core.wrapper.JcrNodeWrapper"%>
<%@page contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0"%>
<sling:defineObjects/>
<%
response.setCharacterEncoding("utf-8");

SolrServerService solr = sling.getService(SolrServerService.class);

String q = request.getParameter("q");
if (StringUtils.isEmpty(q)) q = "*:*";
pageContext.setAttribute("q", q);

String ff = request.getParameter("ff");
if (StringUtils.isEmpty(ff)) ff = "city";
pageContext.setAttribute("ff", ff);

SolrQuery query = new SolrQuery();
query.setQuery( q)
	.setRows(10)
	.setFacet(true)
    .setFacetMinCount(1)
    .setFacetLimit(10)
    .addFacetField(ff);

SolrServer server = solr.getServer("locations"); //.toString();

long startTime = System.currentTimeMillis();
QueryResponse rsp = server.query(query);
pageContext.setAttribute("queryTime", System.currentTimeMillis()-startTime);

pageContext.setAttribute("res", rsp.getResults());
pageContext.setAttribute("facetfield", rsp.getFacetField(ff).getValues());
//pageContext.setAttribute("spell", rsp.getSpellCheckResponse().getSuggestions().get(0));

//pageContext.setAttribute("cities", rsp.getFacetFields());


%>
<body>
<form enctype="application/x-www-form-urlencoded" method="get">
	<input name="q" type="text" value="${q}"/>
	<input name="ff" type="hidden" value="${ff}"/>
</form>

<table>
<c:forEach items="${res}" var="doc">
        <tr>
          <td><c:out value="${doc.id}" /><td>
          <td><c:out value="${doc.city}" /><td>
        </tr>
</c:forEach>
</table>

<table>
<c:forEach items="${facetfield}" var="fff">
        <tr>
          <td><c:out value="${fff.name}" /><td>
          <td><c:out value="${fff.count}" /><td>
        </tr>
</c:forEach>
</table>

<table>
<tr><td>${spells}</td></tr>
<c:forEach items="${spells.alternatives}" var="spell">
        <tr>
          <td><c:out value="${spell}" /><td>
        </tr>
</c:forEach>
</table>

<p>${queryTime} ms</p>

</body>
</html>