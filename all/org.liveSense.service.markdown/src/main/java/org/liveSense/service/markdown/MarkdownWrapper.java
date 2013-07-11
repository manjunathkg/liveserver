package org.liveSense.service.markdown;

import java.util.HashMap;

import org.liveSense.core.wrapper.JcrPropertyWrapper;
import org.liveSense.core.wrapper.JcrValueWrapper;


/**
 * Service wrapper for JSTL
 * 
 * @author robson
 *
 */
public class MarkdownWrapper extends HashMap<String, String> {

	MarkdownService service;
	
	public MarkdownWrapper(MarkdownService service) {
		this.service = service;
	}
	
	@Override
	public String get(Object markdown) {
		String str = "";
		if (markdown instanceof JcrPropertyWrapper) {
			str = ((JcrPropertyWrapper)markdown).toString();
		} else if (markdown instanceof JcrValueWrapper) {
			str = ((JcrValueWrapper)markdown).toString();
		} else if (markdown instanceof String) {
			str = (String)markdown;
		} else
			str = markdown.toString();
		
		return service.markdownToHtml(str);
	}
}
