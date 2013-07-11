package org.liveSense.jcr.explorer.service;

import java.util.List;
import java.util.Map;

import org.liveSense.jcr.explorer.domain.JcrNode;
import org.liveSense.jcr.explorer.domain.JcrProperty;
import org.liveSense.jcr.explorer.domain.RemoteFile;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("JcrService")
public interface JcrService extends RemoteService {
	List<Map<String, List<JcrNode>>> getNodeTree(String path) throws SerializedException;
	List<JcrNode> getNode(String path) throws SerializedException;
	List<String> getAvailableNodeTypes() throws SerializedException;
	List<String> getMixinNodeTypes() throws SerializedException;
	String addNewNode(String path, String newNodeName, String primaryNodeType, String[] mixinTypes, String jcrContentFileName, boolean cancel) throws SerializedException;
	String addMixinType(String path, String mixinType) throws SerializedException;
	String removeMixinType(String path, String mixinType) throws SerializedException;
	String moveNode(String sourcePath, String destinationPath) throws SerializedException;
	String renameNode(String sourcePath, String newName) throws SerializedException;
	String copyNode(String sourcePath, String destinationPath) throws SerializedException;
	String cutAndPasteNode(String sourcePath, String destinationPath) throws SerializedException;
	String moveNodes(Map<String, String> nodeMap) throws SerializedException;
	String copyNodes(Map<String, String> nodeMap) throws SerializedException;
	String deleteNode(String sourcePath) throws SerializedException;
	String saveNodeDetails(String sourcePath, JcrNode jcrNode) throws SerializedException;
	String addNewProperty(String sourcePath, String name, JcrProperty value) throws SerializedException;
	String deleteProperty(String sourcePath, String name) throws SerializedException;
	String saveProperties(String sourcePath, JcrNode jcrNode) throws SerializedException;
	String saveProperty(String sourcePath, String property, JcrProperty value) throws SerializedException;
	//String savePropertyBinaryValue(String sourcePath, String property, InputStream value) throws SerializedException;
	List<String> fullTextSearch(String query) throws SerializedException;
	List<String> xpathSearch(String query) throws SerializedException;
	List<String> sqlSearch(String query) throws SerializedException;
	Boolean addNodeTypes(String cnd) throws SerializedException;
	List<Map<String, String>> getNodeTypeIcons() throws SerializedException;
	String getBrowsableContentFilterRegex() throws SerializedException;
	List<RemoteFile> getPossibleIconPaths(String path) throws SerializedException;
	Boolean changeNodeTypeIconAssociation(String nodeType, String iconPath) throws SerializedException;
}
