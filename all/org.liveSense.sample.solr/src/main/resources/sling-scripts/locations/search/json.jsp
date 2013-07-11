<%@page import="org.apache.solr.client.solrj.response.QueryResponse"%>
<%@page import="org.apache.solr.client.solrj.SolrServer"%>
<%@page import="org.apache.solr.client.solrj.SolrQuery"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="org.liveSense.service.solr.api.SolrServerService"%>
<%@page contentType="application/json; charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0"%>
<%@taglib prefix="json" uri="http://www.atg.com/taglibs/json" %>
<sling:defineObjects/>
<%

SolrServerService solr = sling.getService(SolrServerService.class);
String q = request.getParameter("q");
if (StringUtils.isEmpty(q)) q = "*:*";

String f = request.getParameter("f");
if (StringUtils.isEmpty(f)) f = "city";


pageContext.setAttribute("q", q);
SolrQuery query = new SolrQuery();
query.setQuery( q)
	.setRows(0)
	.setFacet(true)
    .setFacetMinCount(1)
    .setFacetLimit(10)
    .addFacetField(f);

SolrServer server = solr.getServer("locations"); //.toString();

long startTime = System.currentTimeMillis();
QueryResponse rsp = server.query(query);

pageContext.setAttribute("queryTime", System.currentTimeMillis()-startTime);
pageContext.setAttribute("records", rsp.getFacetField(f).getValues());
pageContext.setAttribute("f", f);
%>
<json:object>
	<json:property name="querytime" value="${queryTime}"/>
	<json:array name="records" var="fld" items="${records}">
    	<json:object>
			<json:property name="name" value="${fld.name}"/>
		</json:object>
	</json:array>
</json:object>