package org.liveSense.service.solr.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.CoreDescriptor;
import org.apache.solr.core.SolrConfig;
import org.apache.solr.core.SolrCore;
import org.apache.solr.core.SolrResourceLoader;
import org.apache.solr.schema.IndexSchema;
import org.liveSense.core.BundleProxyClassLoader;
import org.liveSense.core.ClosableInputSource;
import org.liveSense.core.service.OSGIClassLoaderManager;
import org.liveSense.service.solr.api.EmbeddedOSGiClientResourceLoader;
import org.liveSense.service.solr.api.EmbeddedOSGiClientSolrConfig;
import org.liveSense.service.solr.api.SolrClient;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.packageadmin.PackageAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import au.com.bytecode.opencsv.CSVReader;

@Component(label="%embedSolrClient.name",
	description="%embedSolrClient.description",
	immediate=false,
	metatype=true,
	configurationFactory=true,
	policy=ConfigurationPolicy.REQUIRE,
	createPid=true)
@Properties(value = {
		@Property(
				name=EmbeddedSolrClient.PROP_SOLR_SERVER_NAME,
				value=EmbeddedSolrClient.DEFAULT_SOLR_SERVER_NAME,
				description="%solrServerName"),
		@Property(
				name=EmbeddedSolrClient.PROP_SOLR_CONFIG_FILENAME,
				value=EmbeddedSolrClient.DEFAULT_SOLR_CONFIG_FILENAME,
				description="%solrConfigLocation"),
		@Property(
				name=EmbeddedSolrClient.PROP_SOLR_SCHEMA_FIlNAME,
				value=EmbeddedSolrClient.DEFAULT_SOLR_SCHEMA_FILENAME,
				description="%solrSchemaLocation"),
		@Property(
				name=EmbeddedSolrClient.PROP_SOLR_IMPORT_ROOT,
				value=EmbeddedSolrClient.DEFAULT_SOLR_IMPORT_ROOT,
				description="%solrImportFiles"),
		@Property(
				name=EmbeddedSolrClient.PROP_SOLR_IMPORT_FILES,
				value=EmbeddedSolrClient.DEFAULT_SOLR_IMPORT_FILES,
				description="%solrImportFiles"),
		@Property(
				name=EmbeddedSolrClient.PROP_SOLR_IMPORT_ON_STARTUP,
				boolValue=EmbeddedSolrClient.DEFAULT_SOLR_IMPORT_ON_STARTUP,
				description="%solrImportOnStartup"),
		@Property(
				name=EmbeddedSolrClient.PROP_SOLR_IMPORT_FROM_BUNDLE,
				value= {
						"lucene-analyzers-common", 
						"lucene-analyzers-icu", 
						"lucene-analyzers-phonetic", 
						"lucene-analyzers-stempel", 
						"lucene-core", 
						"lucene-grouping", 
						"lucene-highlighter", 
						"lucene-memory", 
						"lucene-misc", 
						"lucene-queries", 
						"lucene-queryparser", 
						"lucene-spatial", 
						"lucene-suggest",
						"org.liveSense.framework.solr"
						},
				description="%solrImportFromBundle"

				)
})
@Service(value=SolrClient.class, serviceFactory=true)
public class EmbeddedSolrClient implements SolrClient {

	private static final Logger log = LoggerFactory.getLogger(EmbeddedSolrClient.class);

	public static final String SOLR_EMBEDDED_SERVICE_KEY = "org.liveSense.service.solr.embeddedserver";
	private String solrHome;

	private SolrServer server = null;
	private CoreContainer coreContainer = null;
	private	EmbeddedOSGiClientResourceLoader loader = null;
	private SolrCore solrCore = null;
	private String instanceName = null;
	private SolrClientListener listener = null;
	private final String solrBundle = null;

	public static final String PROP_SOLR_SERVER_NAME = "solrServerName";
	public static final String DEFAULT_SOLR_SERVER_NAME = "default";

	public static final String PROP_SOLR_SCHEMA_FIlNAME = "solrSchemaFilename";
	public static final String DEFAULT_SOLR_SCHEMA_FILENAME = "conf/schema.xml";

	public static final String PROP_SOLR_CONFIG_FILENAME = "solrConfigFilename";
	public static final String DEFAULT_SOLR_CONFIG_FILENAME = "conf/solrconfig.xml";

	public static final String PROP_SOLR_IMPORT_FILES = "solrImportFiles";
	public static final String DEFAULT_SOLR_IMPORT_FILES = "";

	public static final String PROP_SOLR_IMPORT_ROOT = "solrImportRoot";
	public static final String DEFAULT_SOLR_IMPORT_ROOT = "";

	public static final String PROP_SOLR_BUNDLE = "solrConfigBundle";
	public static final String DEFAULT_SOLR_BUNDLE = "";

	public static final String PROP_SOLR_IMPORT_ON_STARTUP = "solrImportInStartup";
	public static final boolean DEFAULT_SOLR_IMPORT_ON_STARTUP = true;

	public static final String PROP_SOLR_IMPORT_FROM_BUNDLE = "solrImportFromBundle";
	public static final String[] DEFAULT_SOLR_IMPORT_FROM_BUNDLE = new String[]{
		"lucene-analyzers-common", 
		"lucene-analyzers-icu", 
		"lucene-analyzers-phonetic", 
		"lucene-analyzers-stempel", 
		"lucene-core", 
		"lucene-grouping", 
		"lucene-highlighter", 
		"lucene-memory", 
		"lucene-misc", 
		"lucene-queries", 
		"lucene-queryparser", 
		"lucene-spatial", 
		"lucene-suggest",
		"org.liveSense.framework.solr"
		};

	@Reference(cardinality=ReferenceCardinality.MANDATORY_UNARY, policy=ReferencePolicy.DYNAMIC)
	PackageAdmin packageAdmin;

	@Reference(cardinality=ReferenceCardinality.MANDATORY_UNARY, policy=ReferencePolicy.DYNAMIC)
	OSGIClassLoaderManager dynamicClassLoaderManager;
	
	private boolean enabled;

	private Dictionary<String, Object> properties;
	
	private String[] importBundles = DEFAULT_SOLR_IMPORT_FROM_BUNDLE;

	@SuppressWarnings("unchecked")

	ClassLoader bundleClassLoader = this.getClass().getClassLoader();
	
	private ClassLoader getOSGiCompositeClassLoader(BundleContext bundleContext) {
//		CompositeClassLoader ccl = new CompositeClassLoader(this.getClass().getClassLoader().getParent());

		// Adding all lucene package to classloader
//		if (bundleContext != null && bundleContext.getBundles() != null) {
//			for (Bundle bundle : bundleContext.getBundles()) {
//				if (bundle.getHeaders() != null && bundle.getHeaders().get("Bundle-Category") != null && bundle.getHeaders().get("Bundle-Category").equals("Lucene")) {
//					log.info("Adding classloader for: "+bundle.getSymbolicName()+" - "+bundle.getBundleId());
//					ccl.add(new BundleProxyClassLoader(bundle));
//				} 
				//else if (bundle.getSymbolicName().equals("org.liveSense.framework.solr")) {
				//	log.info("Adding classloader for: "+bundle.getSymbolicName()+" - "+bundle.getBundleId());
				//	ccl.add(new BundleProxyClassLoader(bundle));					
				//}
//			}
//		}// else {
		//	JarClassLoader jarcl = new JarClassLoader(this.getClass().getClassLoader());
		//	ccl.add(jarcl);
		//}

//		if (dynamicClassLoaderManager != null) ccl.add(dynamicClassLoaderManager.getBundleClassLoaderByCategory(bundleContext, "Lucene"));
//		ccl.add(this.getClass().getClassLoader());
//		return ccl;
//		return dynamicClassLoaderManager.getBundleClassLoaderByCategory(bundleContext, "Lucene");
		if (dynamicClassLoaderManager == null) return this.getClass().getClassLoader(); else
		return dynamicClassLoaderManager.getBundleClassLoader(bundleContext, importBundles);
	}
	

	@Activate
	public void activate(ComponentContext componentContext) throws IOException,
	ParserConfigurationException, SAXException {
		BundleContext bundleContext = componentContext.getBundleContext();
		
		solrHome = Utils.getSolrHome(bundleContext);

		log.info("ACTIVATE SOLR CORE: "+componentContext.getProperties().get(PROP_SOLR_SERVER_NAME)+" Home: "+solrHome);

		importBundles = Utils.toStringArray(componentContext.getProperties().get(PROP_SOLR_IMPORT_FROM_BUNDLE), DEFAULT_SOLR_IMPORT_FROM_BUNDLE);

		bundleClassLoader = getOSGiCompositeClassLoader(bundleContext);

		//setServiceContextClassLoader();
		
		
		/*
		ServiceReference ref = getSolrServiceReference(EmbeddedOSGiClientResourceLoader.class, bundleContext);
		if (ref == null) {
			// ClassLoader have to null to use the thread's context classloader
			//loader = new LiveSenseResourceLoader(solrHome, null);
			ClassLoader oldContextClassLoader = Thread.currentThread().getContextClassLoader();
			
			Thread.currentThread().setContextClassLoader(bundleClassLoader);
			loader = new EmbeddedOSGiClientResourceLoader(solrHome, bundleClassLoader);
			Thread.currentThread().setContextClassLoader(oldContextClassLoader);
			registerSolrServiceReference(EmbeddedOSGiClientResourceLoader.class, bundleContext, loader);
		} else {
			loader = (EmbeddedOSGiClientResourceLoader)bundleContext.getService(ref);
		}
		*/
		// CoreContainer
		ServiceReference ref = getSolrServiceReference(CoreContainer.class, bundleContext);
		if (ref == null) {
			// ClassLoader have to null to use the thread's context classloader
			//loader = new LiveSenseResourceLoader(solrHome, null);
			ClassLoader oldContextClassLoader = Thread.currentThread().getContextClassLoader();
			
			Thread.currentThread().setContextClassLoader(bundleClassLoader);
			loader = new EmbeddedOSGiClientResourceLoader(solrHome, bundleClassLoader);
			Thread.currentThread().setContextClassLoader(oldContextClassLoader);

			coreContainer = new CoreContainer(loader);
			registerSolrServiceReference(CoreContainer.class, bundleContext, coreContainer);
		} else {
			coreContainer = (CoreContainer)bundleContext.getService(ref);
		}
		properties = componentContext.getProperties();

		enable(null);

	}

	private String makePathRelative(String path) {
		path = path.replaceAll("//", "/");
		if (path.startsWith("/")) 
			return path.substring(1);
		else
			return path;
	}

	public void enable(SolrClientListener listener) throws IOException,
	ParserConfigurationException, SAXException {
		if (enabled) {
			return;
		}

		String solrBundle = Utils.toString(properties.get(PROP_SOLR_BUNDLE), DEFAULT_SOLR_BUNDLE);
		// Backup classloader
		//ClassLoader contextClassloader = Thread.currentThread().getContextClassLoader();

		String bundleRoot;

		String configFilename = makePathRelative(Utils.toString(properties.get(PROP_SOLR_CONFIG_FILENAME), DEFAULT_SOLR_CONFIG_FILENAME));
		String schemaFilename = makePathRelative(Utils.toString(properties.get(PROP_SOLR_SCHEMA_FIlNAME), DEFAULT_SOLR_SCHEMA_FILENAME));

		instanceName = Utils.toString(properties.get(PROP_SOLR_SERVER_NAME), DEFAULT_SOLR_SERVER_NAME);
		String importFiles = Utils.toString(properties.get(PROP_SOLR_IMPORT_FILES), DEFAULT_SOLR_IMPORT_FILES);
		String importRoot = Utils.toString(properties.get(PROP_SOLR_IMPORT_ROOT), DEFAULT_SOLR_IMPORT_ROOT);
		Boolean importOnStartup = Utils.toBoolean(properties.get(PROP_SOLR_IMPORT_ON_STARTUP), DEFAULT_SOLR_IMPORT_ON_STARTUP);
	
		if (!importRoot.endsWith("/")) importRoot += "/";
		File solrHomeFile = new File(solrHome);
		//deployFile(solrHomeFile, "solr.xml");

		ClosableInputSource schemaSource = null;
		ClosableInputSource configSource = null;
		try {

//			// set the host bundle's classloader
//			if (StringUtils.isNotEmpty(solrBundle)) {
//				try {
//					Thread.currentThread().setContextClassLoader(getClassLoaderByBundle(solrBundle));
//				} catch (Throwable e) {
//					log.error("Could not bundle classloader: "+solrBundle, e);
//				}
//			} else {
//				Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
//			}

			boolean isConfig = true;
			try {
				configSource = getSource(importRoot+configFilename);
				schemaSource = getSource(importRoot+schemaFilename);
			} catch (Exception e) {
				isConfig = false;
			}

			
			if (isConfig) {
				File coreDir = new File(solrHomeFile, instanceName);

				ArrayList<InputStream> importCsvStreams = new ArrayList<InputStream>(); 
				// Copying files to the core's directory
				if (StringUtils.isNotEmpty(importRoot)) {
					String files[] = importFiles.split(",");
					for (int i=0; i<files.length; i++) {
						String fullFileName = makePathRelative(files[i]);
						File file = new File(coreDir+"/"+fullFileName);
						try {
							// If it is CSV we would like to import
							if (fullFileName.endsWith(".csv") && (!file.exists() || importOnStartup)) {
								importCsvStreams.add(getSource(importRoot+fullFileName).getByteStream());
								copyFile(file, getSource(importRoot+fullFileName).getByteStream());
							} else {
								copyFile(file, getSource(importRoot+fullFileName).getByteStream());
							}
						} catch (Throwable th) {
							log.error("Could not copy file: "+file, th);
						}
					}
				}

				log.info("Configuring with Config {} schema {} ",configFilename, schemaFilename);

				SolrResourceLoader confloader = new EmbeddedOSGiClientResourceLoader(instanceName, bundleClassLoader);
				SolrConfig config = new EmbeddedOSGiClientSolrConfig(confloader, configFilename, configSource);
				IndexSchema schema = new IndexSchema(config, schemaFilename, schemaSource);
				CoreDescriptor coreDescriptor = new CoreDescriptor(coreContainer, instanceName, coreDir.getAbsolutePath());
				
				solrCore = new SolrCore(instanceName, coreDir.getAbsolutePath()+"/data", config, schema, coreDescriptor);
				coreContainer.register(instanceName, solrCore, false);
				server = new EmbeddedSolrServer(coreContainer, instanceName);
				LoggerFactory.getLogger(this.getClass()).info("Contains cores {} ", coreContainer.getCoreNames());

				/*  TODO - More flexible way */
				// Importing CSV documents
				String[] header = null;
				for (InputStream stream : importCsvStreams) {

					CSVReader csvReader = new CSVReader(new InputStreamReader(stream, "UTF-8"));
					for (String[] entries : csvReader.readAll()) {
						if (header == null) {
							header = entries;
						} else {
							SolrInputDocument doc1 = new SolrInputDocument();
							for (int i = 0; i < entries.length; i++) {
								doc1.addField(header[i], entries[i]);
							}
							server.add(doc1);
						}
					}
				}
				server.commit();
				server.optimize();

				this.enabled = true;
			}
			this.listener = listener;
		} catch (Throwable e) {
			log.error("Error on solrCreate",e);
			
		} finally {
			// Restore classloader
//			Thread.currentThread().setContextClassLoader(contextClassloader);
			safeClose(schemaSource);
			safeClose(configSource);
		}
	}

	@Deactivate
	public void deactivate(ComponentContext componentContext) {
		log.info("DEACTIVATE SOLR CORE: "+componentContext.getProperties().get(PROP_SOLR_SERVER_NAME));
		disable();

		// If there is no more cores we shutdown the corecontainer and removes cores
		if (coreContainer.getCores().size() == 0) {
			coreContainer.shutdown();

			ServiceReference ref = getSolrServiceReference(CoreContainer.class, componentContext.getBundleContext());
			if  (ref != null)
				log.info("Unregistering CoreContainer: "+((CoreContainer)componentContext.getBundleContext().getService(ref)).getSolrHome());
			try {
				componentContext.getBundleContext().ungetService(ref);
			} catch (Throwable e) {
				log.error("Cannot unregister CoreContainer", e);
			}
/*
			ref = getSolrServiceReference(EmbeddedOSGiClientResourceLoader.class, componentContext.getBundleContext());
			if  (ref != null)
				log.info("Unregistering EmbeddedOSGiClientResourceLoader: "+((CoreContainer)componentContext.getBundleContext().getService(ref)).getSolrHome());

			try {
				componentContext.getBundleContext().ungetService(ref);
			} catch (Throwable e) {
				log.error("Cannot unregister EmbeddedOSGiClientResourceLoader", e);
			} 
			*/
		}

	}

	public void disable() {
		if (!enabled) {
			return;
		}
		coreContainer.remove(instanceName);
		solrCore.close();		
	}

	public SolrServer getServer() {
		return server;
	}

	public SolrServer getUpdateServer() {
		return server;
	}

	public String getSolrHome() {
		return solrHome;
	}

	public String getName() {
		return Utils.toString(properties.get(PROP_SOLR_SERVER_NAME), DEFAULT_SOLR_SERVER_NAME);
	}

	private void safeClose(ClosableInputSource source) {
		if (source != null) {
			try {
				source.close();
			} catch (IOException e) {
				log.debug(e.getMessage(), e);
			}
		}
	}

	private ServiceReference getSolrServiceReference(Class clazz, BundleContext bundleContext) {
		ServiceReference refs[] = null;
		try {
			refs = bundleContext.getServiceReferences(clazz.getName(), "("+SOLR_EMBEDDED_SERVICE_KEY+"=true)");
		} catch (InvalidSyntaxException e) {
			return null;
		}
		if (refs != null && refs.length > 0) {
			return refs[0];
		}
		return null;
	}

	private ServiceRegistration registerSolrServiceReference(Class clazz, BundleContext bundleContext, Object service) {
		Dictionary<String, Object> loggingProperties = new Hashtable<String, Object>();
		loggingProperties.put(SOLR_EMBEDDED_SERVICE_KEY, "true");
		return bundleContext.registerService(clazz.getName(), service, loggingProperties);
	}


	private ClosableInputSource getSource(String name) throws IOException {
		if (name.contains(":")) {
			// try a URL
			try {
				URL u = new URL(name);
				InputStream in = u.openStream();
				if (in != null) {

					return new ClosableInputSource(in);
				}
			} catch (IOException e) {
				log.debug(e.getMessage(), e);
			}
		}
		// try a file
		File f = new File(name);
		if (f.exists()) {
			return new ClosableInputSource(new FileInputStream(f));
		} else {
			// try classpath
			InputStream in = null;
			try {
				in = this.getClass().getClassLoader()
						.getResourceAsStream(name);
			} catch (Exception e) {
			}
			if (in == null) {
				log.error(
						"Failed to locate stream {}, tried URL, filesystem ",
						name);
				throw new IOException("Failed to locate stream " + name
						+ ", tried URL, filesystem ");
			}
			return new ClosableInputSource(in);
		}
	}

	private void deployFile(File destDir, String target) throws IOException {
		if (!destDir.isDirectory()) {
			if (!destDir.mkdirs()) {
				log.warn(
						"Unable to create dest dir {} for {}, may cause later problems ",
						destDir, target);
			}
		}
		File destFile = new File(destDir, target);
		if (!destFile.exists()) {
			InputStream in = Utils.class.getClassLoader().getResourceAsStream(
					target);
			OutputStream out = new FileOutputStream(destFile);
			IOUtils.copy(in, out);
			out.close();
			in.close();
		}
	}

	private void copyFile(File destFile, InputStream in) throws IOException {
		File destDir = new File(destFile.getParent());
		if (!destDir.isDirectory()) {
			if (!destDir.mkdirs()) {
				log.warn(
						"Unable to create dest dir {} for {}, may cause later problems ",
						destDir, destFile.getName());
			}
		}
		if (!destFile.exists()) {
			OutputStream out = new FileOutputStream(destFile);
			IOUtils.copy(in, out);
			out.close();
			in.close();
		}
	}

	
	private ClassLoader getClassLoaderByBundle(String name) throws ClassNotFoundException {
		return new BundleProxyClassLoader(getBundleByName(name));
	}

	private Bundle getBundleByName(String name) {
		Bundle[] ret = packageAdmin.getBundles(name, null);
		if (ret != null && ret.length > 0) {
			return ret[0];
		}
		return null;
	}

}
