package org.liveSense.jcr.explorer.client.callback;

import org.liveSense.jcr.explorer.client.JcrExplorer;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.util.SC;

/**
 * 
 * @author James Pickup
 *
 */
public class AddNodeTypesServiceCallback implements AsyncCallback<Boolean>
{
	@Override
	public void onSuccess(Boolean result) {
//		SC.say("Added CND node types successfully.");
		SC.warn("Custom node type registering not available over RMI yet.");
		JcrExplorer.hideLoadingImg();		
	}
	
	@Override
	public void onFailure(Throwable caught) {
		SC.warn(caught.toString(), new NewBooleanCallback());
		JcrExplorer.hideLoadingImg();
	}
}
