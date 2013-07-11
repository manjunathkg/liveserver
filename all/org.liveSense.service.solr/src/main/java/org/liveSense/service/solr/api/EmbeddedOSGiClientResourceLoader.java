package org.liveSense.service.solr.api;

import java.io.IOException;
import java.io.InputStream;

import org.apache.solr.core.SolrResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmbeddedOSGiClientResourceLoader extends SolrResourceLoader {

	public static Logger log = LoggerFactory.getLogger(EmbeddedOSGiClientResourceLoader.class);

	public EmbeddedOSGiClientResourceLoader( String instanceDir, final ClassLoader parent ) {
		super(instanceDir,parent);
		classLoader = new URLClassLoaderWrapper(parent);
	}


	@Override
	public InputStream openResource(String resource) {
		InputStream in = classLoader.getResourceAsStream(resource);
		if ( in == null ) {
			try {
				in = super.openResource(resource);
			} catch (IOException e) {
				log.error("openResource",e);
			}
		}
		return in;
	}
}