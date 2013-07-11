package org.liveSense.jcr.explorer.domain;

import com.smartgwt.client.widgets.grid.ListGridRecord;

public class JcrPropertyListGridRecord extends ListGridRecord {

	private JcrProperty property;

	public JcrProperty getProperty() {
		return property;
	}

	public void setProperty(JcrProperty property) {
		this.property = property;
	}
}
