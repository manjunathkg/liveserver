package org.liveSense.jcr.explorer.client.callback;

import org.liveSense.jcr.explorer.client.JcrExplorer;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;

/**
 * 
 * @author James Pickup
 *
 */
public class LoginServiceCallback implements AsyncCallback<Boolean> {
	private final JcrExplorer jackrabbitExplorer;
	public LoginServiceCallback(JcrExplorer jackrabbitExplorer) {
		this.jackrabbitExplorer = jackrabbitExplorer;
	}
	@Override
	public void onSuccess(Boolean result) {
		JcrExplorer.hideLoadingImg();
		JcrExplorer.service.getAvailableNodeTypes(new AvailableNodeTypesServiceCallback());
		jackrabbitExplorer.drawMainLayout();
	}

	@Override
	public void onFailure(Throwable caught) {
		SC.warn("There was an error logging in: " + caught.toString(), new LoginErrorCallback());
		JcrExplorer.hideLoadingImg();
	}
	
	public class LoginErrorCallback implements BooleanCallback {
		@Override
		public void execute(Boolean value) {
			JcrExplorer.loginWindow.show();
			return;
		}
	}
}

