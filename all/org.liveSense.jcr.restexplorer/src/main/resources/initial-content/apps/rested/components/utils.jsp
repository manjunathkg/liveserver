<%!

String iconForType(String type) {
	if (type.equals("sling:Folder")) return "icon-folder-open";
	else if (type.equals("nt:file")) return "icon-file";
	else return "icon-minus";
}

boolean isFolder(String type) {
	if (type.equals("sling:Folder")) return true;
	else return false;
}

String getRequestPath(SlingHttpServletRequest slingRequest) throws Exception {
	URL u = new URL (slingRequest.getRequestURL().toString());
	String suffix = slingRequest.getRequestPathInfo().getSuffix();
	String path = u.getPath();

	if (suffix != null) return path.substring(0, path.length() - suffix.length());
	else return path;
}

String getRequestSelectorExtension(SlingHttpServletRequest slingRequest) throws Exception {
	String path = getRequestPath(slingRequest);
	int i = path.indexOf ('.');
	if (i == -1) return "";
	else return path.substring(i+1);
}

%>
