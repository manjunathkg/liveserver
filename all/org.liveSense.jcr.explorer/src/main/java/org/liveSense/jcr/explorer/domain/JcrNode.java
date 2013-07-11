package org.liveSense.jcr.explorer.domain;

import java.io.Serializable;
import java.util.Map;

/**
 * 
 * @author James Pickup
 *
 */
public class JcrNode implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public String name;
	public String path;
	public String primaryNodeType;
	public String mixinTypes[];
	
	
	public Map<String, JcrProperty> properties;

	public JcrNode() {
		this(null, null, null, null);
	}
	
	public JcrNode(String name, String path, String primaryNodeType, Map<String, JcrProperty> properties) {
		super();
		this.name = name;
		this.path = path;
		this.primaryNodeType = primaryNodeType;
		this.properties = properties;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPrimaryNodeType() {
		return primaryNodeType;
	}

	public void setPrimaryNodeType(String primaryNodeType) {
		this.primaryNodeType = primaryNodeType;
	}

	public Map<String, JcrProperty> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, JcrProperty> properties) {
		this.properties = properties;
	}

	public String[] getMixinTypes() {
		return mixinTypes;
	}

	public void setMixinTypes(String[] mixinTypes) {
		this.mixinTypes = mixinTypes;
	}
	
}
