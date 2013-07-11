package org.liveSense.jcr.explorer.client.callback;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.liveSense.jcr.explorer.client.JcrExplorer;
import org.liveSense.jcr.explorer.domain.JcrNode;
import org.liveSense.jcr.explorer.domain.JcrTreeNode;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.tree.TreeNode;

/**
 * 
 * @author James Pickup
 *
 */
public class GetNodeTreeServiceCallback implements AsyncCallback<List<Map<String, List<JcrNode>>>> {
	private final JcrExplorer jackrabbitExplorer;
	public GetNodeTreeServiceCallback(JcrExplorer jackrabbitExplorer) {
		this.jackrabbitExplorer = jackrabbitExplorer;
	}
	@Override
	public void onSuccess(List<Map<String, List<JcrNode>>> result) {
		List<Map<String, List<JcrNode>>> jcrNodesList = result;
		JcrTreeNode[] jcrTreeNodes = null;
		for (Map<String, List<JcrNode>> treeAssociationMap : jcrNodesList) {
			for (Iterator<Map.Entry<String, List<JcrNode>>> iterator = treeAssociationMap.entrySet().iterator(); iterator.hasNext();) {
				Map.Entry<String, List<JcrNode>> pairs = iterator.next();
				jcrTreeNodes = new JcrTreeNode[pairs.getValue().size()];
				int x = 0;
				for(JcrNode jcrNode : pairs.getValue()) {
					jcrTreeNodes[x] = new JcrTreeNode(jcrNode.getName(), jcrNode.getPath(), jcrNode.getPrimaryNodeType(), jcrNode.getMixinTypes(), jcrNode.getProperties());
					JcrExplorer.setCustomTreeIcon(jcrTreeNodes[x], jcrNode.getPrimaryNodeType());
					x++;
				}
				
				TreeNode tempTreeNode = jackrabbitExplorer.jcrTree.find("/root" + pairs.getKey().toString());
				if (null != tempTreeNode) {
					if (!jackrabbitExplorer.jcrTree.hasChildren(tempTreeNode)) {
						jackrabbitExplorer.jcrTree.addList(jcrTreeNodes, "/root" + pairs.getKey().toString());
					}
				}
				jackrabbitExplorer.jcrTree.openFolder(tempTreeNode);
			}
		}
		jackrabbitExplorer.jcrTreeGrid.setData(jackrabbitExplorer.jcrTree);
		JcrExplorer.hideLoadingImg();
	}

	@Override
	public void onFailure(Throwable caught) {
		SC.warn(caught.toString(), new NewBooleanCallback());
		JcrExplorer.hideLoadingImg();
	}
}

