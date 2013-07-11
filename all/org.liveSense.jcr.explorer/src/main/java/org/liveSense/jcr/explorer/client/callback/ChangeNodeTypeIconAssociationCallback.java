package org.liveSense.jcr.explorer.client.callback;

import org.liveSense.jcr.explorer.client.JcrExplorer;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author chrisjennings
 *
 */
public class ChangeNodeTypeIconAssociationCallback implements AsyncCallback<Boolean> {
	private JcrExplorer jackrabbitExplorer;

	public ChangeNodeTypeIconAssociationCallback(JcrExplorer jackrabbitExplorer) {
		this.jackrabbitExplorer = jackrabbitExplorer;
	}

	@Override
	public void onFailure(Throwable throwable) {
		//SC.warn("There was an error: " + throwable.toString(), new NewBooleanCallback());
		JcrExplorer.hideLoadingImg();
	}

	@Override
	public void onSuccess(Boolean arg0) {
		jackrabbitExplorer.getNodeTypeIcons();
		jackrabbitExplorer.refreshFromRoot();
	}
}
