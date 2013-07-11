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
%><sling:defineObjects /><%
		NodeIterator children = currentNode.getNodes();
		while (children.hasNext ()) {
			Node node = children.nextNode();
%><div class="well component" draggable="true" data-node="<%= node%>"><%= node.getName()%></div><%
		}
%>
<script>
	function handleDragStart(e) {
		this.style.opacity = '0.4';
		e.dataTransfer.effectAllowed = 'move';
		e.dataTransfer.setData('text/plain', this.dataset["node"]);
	}

function handleDragEnd(e) {
		this.style.opacity = '1.0';
}

	var components = document.querySelectorAll('.component');
	[].forEach.call(components, function(com) {
		com.addEventListener('dragstart', handleDragStart, false);
		com.addEventListener('dragend', handleDragEnd, false);
	});
</script>



