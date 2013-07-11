package org.liveSense.jcr.explorer.client.callback;

import org.liveSense.jcr.explorer.client.JcrExplorer;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.util.SC;

/**
 * 
 * @author James Pickup
 *
 */
public class GetBrowsableContentFilterRegexsServiceCallback implements AsyncCallback<String> {
	public void onSuccess(String result) {
		JcrExplorer.browsableContentFilterRegex = result;
		JcrExplorer.hideLoadingImg();
	}

	public void onFailure(Throwable caught) {
		SC.warn("There was an error: " + caught.toString(), new NewBooleanCallback());
		JcrExplorer.hideLoadingImg();
	}
}

