package org.liveSense.jcr.explorer.client.handler;

import org.liveSense.jcr.explorer.client.JcrExplorer;

import com.smartgwt.client.widgets.grid.events.CellClickEvent;
import com.smartgwt.client.widgets.grid.events.CellClickHandler;

/**
 * @author chrisjennings
 * 
 */
public class IconGridHandler implements CellClickHandler {

	private JcrExplorer jackrabbitExplorer;

	public IconGridHandler(JcrExplorer jackrabbitExplorer) {
		this.jackrabbitExplorer = jackrabbitExplorer;
	}

	@Override
	public void onCellClick(CellClickEvent event) {
		jackrabbitExplorer.changeCurrentNodeTypeAssociation(event.getRecord()
				.getAttribute("path"));
		jackrabbitExplorer.hidePossibleIconsWindow();
	}
}
