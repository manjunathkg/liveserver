package org.liveSense.jcr.explorer.client.callback;

import java.util.List;

import org.liveSense.jcr.explorer.client.JcrExplorer;
import org.liveSense.jcr.explorer.domain.JcrNode;
import org.liveSense.jcr.explorer.domain.JcrTreeNode;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.util.SC;

/**
 * 
 * @author James Pickup
 *
 */
public class GetNodeServiceCallback implements AsyncCallback<List<JcrNode>> {
	private final JcrExplorer jackrabbitExplorer;
	private final String parentPath;
	public GetNodeServiceCallback(JcrExplorer jackrabbitExplorer, String parentPath) {
		this.jackrabbitExplorer = jackrabbitExplorer;
		this.parentPath = parentPath;
	}
	@Override
	public void onSuccess(List<JcrNode> result) {
		List<JcrNode> jcrNodeList = result;
		JcrTreeNode[] jcrTreeNodes = new JcrTreeNode[jcrNodeList
				.size()];
		int x = 0;
		for (JcrNode jcrNode : jcrNodeList) {
			jcrTreeNodes[x] = new JcrTreeNode(jcrNode.getName(), jcrNode.getPath(), jcrNode.getPrimaryNodeType(), jcrNode.getMixinTypes(), jcrNode.getProperties());
			JcrExplorer.setCustomTreeIcon(jcrTreeNodes[x], jcrNode.getPrimaryNodeType());
			x++;
		}
		JcrTreeNode parentAnimateTreeNode;
		if (parentPath != null) {
			parentAnimateTreeNode = (JcrTreeNode) jackrabbitExplorer.jcrTree.find("/root" + parentPath);
			jackrabbitExplorer.jcrTreeGrid.setData(jackrabbitExplorer.jcrTree);
		} else { 
			parentAnimateTreeNode = (JcrTreeNode) jackrabbitExplorer.jcrTreeGrid.getSelectedRecord();
		}
		jackrabbitExplorer.jcrTree.addList(jcrTreeNodes, parentAnimateTreeNode);
		jackrabbitExplorer.jcrTreeGrid.setData(jackrabbitExplorer.jcrTree);

		JcrExplorer.hideLoadingImg();
	}

	@Override
	public void onFailure(Throwable caught) {
		SC.warn(caught.toString(), new NewBooleanCallback());
		JcrExplorer.hideLoadingImg();
	}
}

