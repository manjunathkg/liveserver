package org.liveSense.jcr.explorer.client.callback;

import java.util.List;
import java.util.Map;

import org.liveSense.jcr.explorer.client.JcrExplorer;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.util.SC;

/**
 * 
 * @author James Pickup
 *
 */
public class GetNodeTypeIconsServiceCallback implements AsyncCallback<List<Map<String, String>>> {
	public void onSuccess(List<Map<String, String>> result) {
		JcrExplorer.customNodeList = result;
		JcrExplorer.hideLoadingImg();
	}

	public void onFailure(Throwable caught) {
		SC.warn("There was an error: " + caught.toString(), new NewBooleanCallback());
		JcrExplorer.hideLoadingImg();
	}
}

