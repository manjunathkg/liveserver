package org.liveSense.sample.gwt.notesRequestFactory.server;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.jcr.api.SlingRepository;
import org.liveSense.sample.gwt.notesRequestFactory.shared.domain.NoteBean;
import org.liveSense.sample.gwt.notesRequestFactory.shared.service.NoteDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.web.bindery.requestfactory.shared.ServiceLocator;


@Component(immediate=true, metatype=false)
@Service(value=NoteDao.class)
public class NoteDaoImpl implements NoteDao {

	Logger log = LoggerFactory.getLogger(NoteDaoImpl.class);

	private final String pn_notetitle = "noteTitle";
	private final String pn_notetext = "noteText";
	private final String path_democontent = "/samples/notesrequestfactory/notes";

	@Reference(cardinality=ReferenceCardinality.MANDATORY_UNARY, policy=ReferencePolicy.DYNAMIC)
	private SlingRepository repository;

	@Reference(cardinality=ReferenceCardinality.MANDATORY_UNARY, policy=ReferencePolicy.DYNAMIC)
	private ServiceLocator serviceLocator;

	private Session getAdministrativeSession() throws RepositoryException {
		Session session = repository.loginAdministrative(repository.getDefaultWorkspace());
		return session;

	}

	@Activate
	protected void activate() {
		Session session = null;
		try {
			session = getAdministrativeSession();
		} catch (RepositoryException e) {
			log.error("NoteDaoImpl create: default repository unavailable: ", e);
		}

		if (session != null) {
			try {
				Node root = (Node) session.getItem(path_democontent);
			} catch (RepositoryException e) {
				log.error("NoteDaoImpl create: error while getting demo content path " + path_democontent + ": "
						+ session.getWorkspace().getName() + ": ", e);
			} finally {
				try {
					if (session != null && session.isLive())
						session.logout();
				} catch (Throwable th) {
					log.warn("Could not close session");
				}
			}
		} else {
			log.error("No session");
		}
	}

	@Override
	public void createNote(NoteBean note) {
		Session session = null;
		log.info("createNote: creating note with title {}...", note.getTitle());
		try {
			session = getAdministrativeSession();
			Node root = (Node) session.getItem(path_democontent);

			// add a node to the root of the demo content and set the properties via the POJO's getters
			final Node node = root.addNode("note");
			node.setProperty(pn_notetitle, note.getTitle());
			node.setProperty(pn_notetext, note.getText());
			session.save();
			log.info("createNote: successfully saved note {} to repository.", node.getPath());

		} catch (RepositoryException e) {

			log.error("createNote: error while creating note in repository: ", e);
			try {
				// if the node creation failed, try to roll-back the repository changes accumulated
				if (session.hasPendingChanges()) {
					session.refresh(false);
				}
			} catch (RepositoryException e1) {
				log.error("createNote: error while reverting changes after trying to save note to repository: ", e1);
			}
		} finally {
			try {
				if (session != null && session.isLive())
					session.logout();
			} catch (Throwable th) {
				log.warn("Could not close session");
			}
		}
	}

	@Override
	public List<NoteBean> getNotes() throws Exception {
		Session session = null;

		final List<NoteBean> notes = new ArrayList<NoteBean>();

		try {
			session = getAdministrativeSession();
			Node root = (Node) session.getItem(path_democontent);

			// get all child nodes of the demo content root node
			final NodeIterator nodes = root.getNodes();
			while (nodes.hasNext()) {

				Node node = nodes.nextNode();
				// for every child node, that has the correct properties set, create a POJO and add it to the list
				if (node.hasProperty(pn_notetitle) && node.hasProperty(pn_notetext)) {

					final NoteBean note = new NoteBean();
					note.setTitle(node.getProperty(pn_notetitle).getString());
					note.setText(node.getProperty(pn_notetext).getString());
					// set the node's path, so that the GWT client app can use it as the parameter for the
					// deleteNote(String path) method.
					note.setPath(node.getPath());

					notes.add(note);
					log.info("getNotes: found note {}, adding to list...", node.getPath());
				}
			}

			if (session.hasPendingChanges())
				session.save();

		} catch (RepositoryException e) {
			log.error("getNotes: error while getting list of notes from " + path_democontent + ": ", e);
		} finally {
			try {
				if (session != null && session.isLive())
					session.logout();
			} catch (Throwable th) {
				log.warn("Could not close session");
			}
		}

		return notes;
	}

	@Override
	public void deleteNote(String path) {
		Session session = null;
		try {
			session = getAdministrativeSession();
			session.getItem(path).remove();
			session.save();
		} catch (RepositoryException e) {
			log.error("deleteNote: error while deleting note {}: ", e);
		} finally {
			try {
				if (session != null && session.isLive())
					session.logout();
			} catch (Throwable th) {
				log.warn("Could not close session");
			}
		}
	}


}
