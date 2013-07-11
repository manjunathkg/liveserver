package org.liveSense.jcr.explorer.client.callback;

import org.liveSense.jcr.explorer.client.JcrExplorer;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.util.SC;

/**
 * 
 * @author James Pickup
 *
 */
public class CRUDServiceCallback implements AsyncCallback<String> {
	private JcrExplorer jackrabbitExplorer;
	String newNodePath;
	String deletedNodePath;
	public CRUDServiceCallback(JcrExplorer jackrabbitExplorer, String newNodePath, String deletedNodePath) {
		this.jackrabbitExplorer = jackrabbitExplorer;
		this.newNodePath = newNodePath;
		this.deletedNodePath = deletedNodePath;
	}
	public void onSuccess(String result) {
		String returnMessage = result;
		//if returMessage says the operation failed. 
			//return
		
		if (null != newNodePath && !newNodePath.equals("")) {
			jackrabbitExplorer.treeRecordClick(jackrabbitExplorer.jcrTreeGrid, true, newNodePath);
		}
		if (null != deletedNodePath && !deletedNodePath.equals("")) {
			jackrabbitExplorer.treeDeleteUpdate(getParentPath(deletedNodePath));
		}
		
		SC.say(returnMessage);
		JcrExplorer.hideLoadingImg();
	}

	public void onFailure(Throwable caught) {
		SC.warn(caught.toString(), new NewBooleanCallback());
		JcrExplorer.hideLoadingImg();
	}
	
	private static String getParentPath(String path) {
		String parentPath = path.substring(0, path.lastIndexOf('/'));
		if (null != parentPath && parentPath.equals("")) {
			return "/";
		}
		return parentPath;
	}

}

