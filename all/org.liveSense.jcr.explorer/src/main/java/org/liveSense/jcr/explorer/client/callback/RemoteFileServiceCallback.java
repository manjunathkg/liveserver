package org.liveSense.jcr.explorer.client.callback;

import java.util.List;

import org.liveSense.jcr.explorer.client.JcrExplorer;
import org.liveSense.jcr.explorer.domain.RemoteFile;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.grid.ListGridRecord;

/**
 * @author chrisjennings
 *
 */
public class RemoteFileServiceCallback implements AsyncCallback<List<RemoteFile>> {

	private JcrExplorer jackrabbitExplorer;
	public RemoteFileServiceCallback(JcrExplorer jackrabbitExplorer) {
		this.jackrabbitExplorer = jackrabbitExplorer;
	}

	@Override
	public void onSuccess(List<RemoteFile> results) {
		if (null == results || results.size() < 1) {
			SC.say("No possible icon files found.");
			JcrExplorer.hideLoadingImg();
			return;
		}
		ListGridRecord[] iconFilesListGridRecords = new ListGridRecord[results.size()];
		int i = 0;
		for (RemoteFile remoteFile : results) {
			ListGridRecord listGridRecord = new ListGridRecord();
			listGridRecord.setAttribute("imagePath", remoteFile.getImagePath());
			listGridRecord.setAttribute("path", remoteFile.getPath());
			listGridRecord.setAttribute("isDir", remoteFile.isDirectory());
			iconFilesListGridRecords[i] = listGridRecord;
			i++;
		}
		jackrabbitExplorer.getRemoteIconFilesListGrid().setData(iconFilesListGridRecords);
		jackrabbitExplorer.showPossibleIconsWindow();
		JcrExplorer.hideLoadingImg();		
	}

	@Override
	public void onFailure(Throwable caught) {
		SC.warn("There was an error: " + caught.toString(), new NewBooleanCallback());
		JcrExplorer.hideLoadingImg();
	}
}
