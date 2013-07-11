package org.liveSense.jcr.explorer.service;

import java.util.List;
import java.util.Map;

import org.liveSense.jcr.explorer.domain.JcrNode;
import org.liveSense.jcr.explorer.domain.JcrProperty;
import org.liveSense.jcr.explorer.domain.RemoteFile;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface JcrServiceAsync {
/*
	void getDefaultLoginDetails(AsyncCallback<LoginDetails> callback);
	void loginLocal(String configFilePath, String homeDirPath, String workSpace, String userName, String password, AsyncCallback<Boolean> callback);
	void loginViaJndi(String jndiName, String jndiContext, String workSpace, String userName, String password, AsyncCallback<Boolean> callback);
	void loginViaRmi(String rmiUrl, String workSpace, String userName, String password, AsyncCallback<Boolean> callback);
*/
	void getNodeTree(String path, AsyncCallback<List<Map<String, List<JcrNode>>>> callback);
	void getNode(String path, AsyncCallback<List<JcrNode>> callback);
	void getAvailableNodeTypes(AsyncCallback<List<String>> callback);
	void getMixinNodeTypes(AsyncCallback<List<String>> callback);
	void addNewNode(String path, String newNodeName, String primaryNodeType, String[] mixinNodeTypes, String jcrContentFileName, boolean cancel, AsyncCallback<String> callback);
	void addMixinType(String path, String mixinType, AsyncCallback<String> callback);
	void removeMixinType(String path, String mixinType, AsyncCallback<String> callback);
	void moveNode(String sourcePath, String destinationPath, AsyncCallback<String> callback);
	void renameNode(String sourcePath, String newName, AsyncCallback<String> callback);
	void copyNode(String sourcePath, String destinationPath, AsyncCallback<String> callback);
	void cutAndPasteNode(String sourcePath, String destinationPath, AsyncCallback<String> callback);
	void moveNodes(Map<String, String> nodeMap, AsyncCallback<String> callback);
	void copyNodes(Map<String, String> nodeMap, AsyncCallback<String> callback);
	void deleteNode(String sourcePath, AsyncCallback<String> callback);
	void saveNodeDetails(String sourcePath, JcrNode jcrNode, AsyncCallback<String> callback);
	void addNewProperty(String sourcePath, String name, JcrProperty value, AsyncCallback<String> callback);
	void deleteProperty(String sourcePath, String name, AsyncCallback<String> callback);
	void saveProperties(String sourcePath, JcrNode jcrNode, AsyncCallback<String> callback);
	void saveProperty(String sourcePath, String property, JcrProperty value, AsyncCallback<String> callback);
	//String savePropertyBinaryValue(String sourcePath, String property, InputStream value) throws Exception;
	void fullTextSearch(String query, AsyncCallback<List<String>> callback);
	void xpathSearch(String query, AsyncCallback<List<String>> callback);
	void sqlSearch(String query, AsyncCallback<List<String>> callback);
	void addNodeTypes(String cnd, AsyncCallback<Boolean> callback);
	void getNodeTypeIcons(AsyncCallback<List<Map<String, String>>> callback);
	void getBrowsableContentFilterRegex(AsyncCallback<String> callback);
	void getPossibleIconPaths(String path, AsyncCallback<List<RemoteFile>> callback);
	void changeNodeTypeIconAssociation(String nodeType, String iconPath, AsyncCallback<Boolean> callback);
}

