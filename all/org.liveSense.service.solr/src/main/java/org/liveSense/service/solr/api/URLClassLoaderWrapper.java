package org.liveSense.service.solr.api;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;

public class URLClassLoaderWrapper extends URLClassLoader {

	ClassLoader inh = null;
	public URLClassLoaderWrapper(ClassLoader parent) {
		super(new URL[]{});
		inh = parent;
	}
	
	@Override
	public URL findResource(String name) {
		return inh.getResource(name);
	}
	
	@Override
	public URL getResource(String name) {
		return inh.getResource(name);
	}
	
	@Override
	public Enumeration<URL> findResources(String name) throws IOException {
		return inh.getResources(name);
	}
	
	@Override
	public InputStream getResourceAsStream(String name) {
		return inh.getResourceAsStream(name);
	}

	@Override
	public URL[] getURLs() {
		return super.getURLs();
	}
	
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		return inh.loadClass(name);
	}
	
	@Override
	public Enumeration<URL> getResources(String name) throws IOException {
		return inh.getResources(name);
	}
}
