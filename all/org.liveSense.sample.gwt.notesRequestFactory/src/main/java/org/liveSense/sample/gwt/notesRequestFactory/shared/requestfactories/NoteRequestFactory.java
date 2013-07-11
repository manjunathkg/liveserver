package org.liveSense.sample.gwt.notesRequestFactory.shared.requestfactories;

import java.util.List;

import org.liveSense.sample.gwt.notesRequestFactory.shared.proxies.NoteValueProxy;
import org.liveSense.sample.gwt.notesRequestFactory.shared.service.NoteDao;
import org.liveSense.service.gwt.OsgiServiceLocator;

import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.RequestFactory;
import com.google.web.bindery.requestfactory.shared.Service;

public interface NoteRequestFactory extends RequestFactory {

	@Service(value = NoteDao.class, locator = OsgiServiceLocator.class)
	public interface NoteRequestContext extends RequestContext {
		Request<List<NoteValueProxy>> getNotes();
	    Request<Void> createNote(NoteValueProxy note);
	    Request<Void> deleteNote(String path);
	}
	NoteRequestContext context();
}
