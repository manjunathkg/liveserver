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
                  utils.*" 
%><%
%><%@ taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %><%
%><%!
%><%
%><sling:defineObjects /><%

	String parentPath = "/";
	if (currentNode.getDepth() > 0) parentPath = currentNode.getParent().getPath();

	String newContent = currentNode.getPath();
	if (newContent.equals("/")) newContent = "/";
	else newContent += "/";
%>

<form ID="CREATEFORM" class="form-horizontal" method="post" action="<%= newContent %>" enctype="multipart/form-data">
	<fieldset>
	<legend>Add New Node</legend>
		<div class="control-group">
			<label class="control-label" for="node_name">node name</label>
			<div class="controls">
				<input id="node_name" type="text" name=":name" value="" required placeholder="node name"/>
			</div>
		</div>
		<!-- div class="control-group">
			<label class="control-label" for="node_rtype">sling resource type</label>
			<div class="controls">
				<input id="node_rtype" type="text" name="sling:resourceType" value="" placeholder="resource type"/>
			</div>
		</div -->

		<input type="hidden" name=":operation" value="import" />
		<input type="hidden" name=":contentType" value="json" />
		<div class="control-group">
			<label class="control-label" for="node_type">node type</label>
			<div class="controls">
				<select name=":content" style="height:26px">
					<option value="{ 'jcr:primaryType':'sling:Folder' }">Sling Folder</option>
					<option value="{ 'jcr:primaryType':'nt:unstructured' }">Unstructured Node</option>
					<option value="{ 'jcr:primaryType':'nt:file','jcr:content':{'jcr:primaryType':'nt:resource','jcr:data':'','jcr:mimeType':'text/plain'} }">Empty Text File</option>
				</select>
			<button class="btn btn-success" type="submit"><i class="icon-ok icon-white"></i></button>
			</div>

			<input type="hidden" name=":redirect" value="<%=resource.getPath()%>.edit.html" />
			<input type="hidden" name=":errorpage" value="<%=slingRequest.getRequestURL()%>" />

		</div>
	</fieldset>
</form>

<form id="UPLOADFORM" method="post" action="<%= currentNode.getPath() %>" enctype="multipart/form-data">
	<fieldset>
	<legend>Upload File</legend>
		<br/>
		<i class="icon-upload"></i>
		<input class="btn" type="file" name="*"/>

		<button class="btn btn-success" type="submit"><i class="icon-ok icon-white"></i></button>
		<input type="hidden" name=":redirect" value="<%=resource.getPath()%>.edit.html" />
		<input type="hidden" name=":errorpage" value="<%=slingRequest.getRequestURL()%>" />
	</fieldset>
</form>

<%
	String error = request.getParameter("error");
	if (error != null) {
		%>
		<div class="alert alert-error">
		<a href="<%=slingRequest.getRequestURL()%>" class="close">&times;</a>
		<strong>Error while saving data!</strong>
		<%= error %>
		<p>
		You may have to <a href=#">login</a> before making any changes.
		</div>
		<%
	}
%>
