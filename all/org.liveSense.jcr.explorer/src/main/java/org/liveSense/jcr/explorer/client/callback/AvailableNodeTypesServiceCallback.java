package org.liveSense.jcr.explorer.client.callback;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.liveSense.jcr.explorer.client.JcrExplorer;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.util.SC;

/**
 * 
 * @author James Pickup
 *
 */
public class AvailableNodeTypesServiceCallback implements AsyncCallback<List<String>> {
	public static String DEFAULT_NODE_TYPE = "nt:unstructured";
	public static LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();

	@Override
	public void onSuccess(List<String> result) {
		List<String> returnList = result;
		if (null == returnList || returnList.size() < 1) {
			SC.say("No available Node Types found.");
			JcrExplorer.hideLoadingImg();
			return;
		}
		
		for (Iterator<String> iterator = returnList.iterator(); iterator.hasNext();) {
			String nodeType = iterator.next();
			valueMap.put(nodeType, nodeType);
		}
		JcrExplorer.hideLoadingImg();
	}

	@Override
	public void onFailure(Throwable caught) {
		SC.warn("There was an error: " + caught.toString(), new NewBooleanCallback());
		JcrExplorer.hideLoadingImg();
	}
}

