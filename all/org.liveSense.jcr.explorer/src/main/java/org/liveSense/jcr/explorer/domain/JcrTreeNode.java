package org.liveSense.jcr.explorer.domain;

import java.io.Serializable;
import java.util.Map;

import org.liveSense.jcr.explorer.client.JcrExplorer;

import com.smartgwt.client.widgets.tree.TreeNode;

public class JcrTreeNode extends TreeNode implements Serializable {
	private static final long serialVersionUID = 2162736740069966873L;
	private Map<String, JcrProperty> properties;
	private String[] mixinNodeTypes;
	public JcrTreeNode() {
		super();
	}

	public JcrTreeNode(String name, String path, String primaryNodeType, String[] mixinNodeTypes,
			Map<String, JcrProperty> properties, JcrTreeNode... children) {
		setTitle(name);
		setAttribute("path", path);
		setAttribute("primaryNodeType", primaryNodeType);
//		setAttribute("mixinNodeTypes", mixinNodeTypes);
		this.mixinNodeTypes = mixinNodeTypes;
		this.properties = properties;
		setAttribute("treeGridIcon", JcrExplorer.defaultIcon);
		setChildren(children);
	}

	public JcrTreeNode(String name, String path, String primaryNodeType, String[] mixinNodeTypes,
			Map<String, JcrProperty> properties, String treeGridIcon, JcrTreeNode... children) {
		setTitle(name);
		setAttribute("path", path);
		setAttribute("primaryNodeType", primaryNodeType);
//		setAttribute("mixinNodeTypes", mixinNodeTypes);
		this.mixinNodeTypes = mixinNodeTypes;
		this.properties = properties;
		//setAttribute("treeGridIcon", JackrabbitExplorer.defaultIcon);
		setAttribute("treeGridIcon", treeGridIcon);
		setChildren(children);
	}

	public Map<String, JcrProperty> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, JcrProperty> properties) {
		this.properties = properties;
	}

	public String[] getMixinNodeTypes() {
		return mixinNodeTypes;
	}

	public void setMixinNodeTypes(String[] mixinNodeTypes) {
		this.mixinNodeTypes = mixinNodeTypes;
	}
	
}
