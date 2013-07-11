/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.liveSense.service.solr.configurationLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Deactivate;
import org.liveSense.service.solr.impl.EmbeddedSolrClient;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * The <code>ContfigurationLoader</code> is the service
 * providing the following functionality:
 * <ul>
 * <li>Bundle listener to load initial solr configurations for bundles.
 * <li>Fires OSGi EventAdmin events on behalf of internal helper objects
 * </ul>
 **/


@Component(label="%solrLoader.name",
	description="%solrLoader.description",
	immediate=true,
	metatype=true,
	configurationFactory=true,
	policy=ConfigurationPolicy.OPTIONAL,
	createPid=false)
public class Loader implements SynchronousBundleListener {

    private final static String CONFIGURATION_PROPERTY_NAME = "solrConfigLoaderName";

    ConfigurationAdmin configurationAdmin;
    ServiceMediator services;
    
    // ---------- BundleListener -----------------------------------------------
    /**
     * Loads and unloads any configuration provided by the bundle whose state
     * changed. If the bundle has been started, the configuration is loaded. If
     * the bundle is about to stop, the configurations are unloaded.
     *
     * @param event The <code>BundleEvent</code> representing the bundle state
     *            change.
     */
    public void bundleChanged(BundleEvent event) {

        //
        // NOTE:
        // This is synchronous - take care to not block the system !!
        //

        switch (event.getType()) {
            case BundleEvent.STARTING:
                try {
                    registerBundle(event.getBundle());
                } catch (Throwable t) {
                    services.error(
                            "bundleChanged: Problem loading solr configuration of bundle "
                            + event.getBundle().getSymbolicName() + " ("
                            + event.getBundle().getBundleId() + ")", t);
                } finally {
                }
                break;
            case BundleEvent.STOPPED:
                try {
                    unregisterBundle(event.getBundle());
                } catch (Throwable t) {
                    services.error(
                            "bundleChanged: Problem unloading solr configuration of bundle "
                            + event.getBundle().getSymbolicName() + " ("
                            + event.getBundle().getBundleId() + ")", t);
                } finally {
                }
                break;
        }
    }


    @Activate
    public void activate(BundleContext context) throws Exception {

        services = new ServiceMediator(context);
        configurationAdmin = services.getConfigurationAdminService(ServiceMediator.NO_WAIT);
        context.addBundleListener(this);

        int ignored = 0;
        try {
            Bundle[] bundles = context.getBundles();
            for (int i=0;i<bundles.length; i++) {
                Bundle bundle = (Bundle)bundles[i];

                if ((bundle.getState() & (Bundle.ACTIVE)) != 0) {
                    // load configurations from bundles which are ACTIVE
                    try {
                        registerBundle(bundle);
                    } catch (Throwable t) {
                        services.error(
                                "Problem loading solr configuration of bundle "
                                + bundle.getSymbolicName() + " ("
                                + bundle.getBundleId() + ")", t);
                    } finally {
                    }
                } else {
                    ignored++;
                }

                if ((bundle.getState() & (Bundle.ACTIVE)) == 0) {
                    // remove configurations from bundles which are not ACTIVE
                    try {
                        unregisterBundle(bundle);
                    } catch (Throwable t) {
                        services.error(
                                "Problem loading solr configuration of bundle "
                                + bundle.getSymbolicName() + " ("
                                + bundle.getBundleId() + ")", t);
                    } finally {
                    }
                } else {
                    ignored++;
                }


            }
            services.info(
                    "Out of "+bundles.length+" bundles, "+ignored+" were not in a suitable state for Solr configuration loading");
        } catch (Throwable t) {
            services.error("activate: Problem while loading Solr configuration", t);
        } finally {
        }


    }

    @Deactivate
    public void deactivate(BundleContext context) throws Exception {
        context.removeBundleListener(this);
        int ignored = 0;
        try {
            Bundle[] bundles = context.getBundles();
            for (int i=0;i<bundles.length; i++) {
                Bundle bundle = (Bundle)bundles[i];

                if ((bundle.getState()) == 0) {
                    // remove configurations from bundles which are not ACTIVE
                    try {
                        unregisterBundle(bundle);
                    } catch (Throwable t) {
                        services.error(
                                "Problem loading solr configuration of bundle "
                                + bundle.getSymbolicName() + " ("
                                + bundle.getBundleId() + ")", t);
                    } finally {
                    }
                } else {
                    ignored++;
                }


            }
            services.info(
                    "Out of "+bundles.length+" bundles, "+ignored+" were not in a suitable state for Solr configuration loading");
        } catch (Throwable t) {
            services.error("activate: Problem while loading Solr configuration", t);
        } finally {
        }

        if (services != null) {
            services.deactivate();
            services = null;
        }
    }



    // ---------- Implementation helpers --------------------------------------
    /**
     * Register a bundle and install the configurations included them.
     *
     * @param bundle
     */
    public void registerBundle(final Bundle bundle) throws Exception {
        // if this is an update, we have to uninstall the old content first

        services.debug("Registering bundle "+bundle.getSymbolicName()+" for Solr configuration loading.");
        registerBundleInternal(bundle);
        /*
        if (registerBundleInternal(bundle)) {

            // handle delayed bundles, might help now
            int currentSize = -1;
            for (int i = delayedBundles.size(); i > 0
                    && currentSize != delayedBundles.size()
                    && !delayedBundles.isEmpty(); i--) {

                Iterator di = delayedBundles.iterator();
                while (di.hasNext()) {
                    Bundle delayed = (Bundle)di.next();
                    if (registerBundleInternal(delayed)) {
                        di.remove();
                    }
                }
                currentSize = delayedBundles.size();
            }

        } else {
            // add to delayed bundles - if this is not an update!
            delayedBundles.add(bundle);
        }*/
    }

    

    private boolean registerBundleInternal(
            final Bundle bundle) throws Exception {


        // check if bundle has initial configuration
        final Iterator<?> pathIter = PathEntry.getContentPaths(bundle);
        if (pathIter == null) {
            services.debug("Bundle "+bundle.getSymbolicName()+" has no Solr configuration(s)");
            return true;
        }
        services.info("Bundle "+bundle.getSymbolicName()+" has Solr configuration(s)");

        while (pathIter.hasNext()) {
            PathEntry path = (PathEntry)pathIter.next();
            Enumeration<?> entries = bundle.getEntryPaths(path.getPath());

            if (entries != null) {
                while (entries.hasMoreElements()) {
                	String confPath = (String)entries.nextElement();
                    if (canHandle(bundle, confPath)) {
                        install(bundle, confPath);
                     }
                }
            }
        }

        return false;
    }

    /**
     * Unregister a bundle. Remove installed content.
     *
     * @param bundle The bundle.
     */
    public void unregisterBundle(final Bundle bundle) throws Exception {


        final Iterator<?> pathIter = PathEntry.getContentPaths(bundle);
        if (pathIter == null) {
            services.debug("Bundle "+bundle.getSymbolicName()+" has no Solr configuration(s)");
            return;
        }
        services.info("Bundle "+bundle.getSymbolicName()+" has Solr configuration(s)");

        while (pathIter.hasNext()) {
            PathEntry path = (PathEntry)pathIter.next();
            Enumeration<?> entries = bundle.getEntryPaths(path.getPath());

            if (entries != null) {
                while (entries.hasMoreElements()) {
                	String confPath = (String)entries.nextElement();
                    if (canHandle(bundle, confPath)) {
                        uninstall(bundle, confPath);
                    }
                }
            }
        }

    }


    public boolean canHandle(Bundle bundle, String name) {
    	InputStream st = null;
    	try {
    		st = bundle.getEntry(name+EmbeddedSolrClient.DEFAULT_SOLR_CONFIG_FILENAME).openStream();
    		if (st != null) {
    			try {
    				st.close();
    			} catch (Throwable th) {
    			}
    			st = bundle.getEntry(name+EmbeddedSolrClient.DEFAULT_SOLR_SCHEMA_FILENAME).openStream();
    		}
    		return st != null;
    	} catch (Throwable th) {
    		return false;
    	} finally {
    		if (st != null)
				try {
					st.close();
				} catch (IOException e) {
				}
    	}
    }

    public void install(Bundle bundle, String path) throws Exception
    {
        setConfig(bundle, path);
    }

    public void uninstall(Bundle bundle, String path) throws Exception
    {
        deleteConfig(bundle, path);
    }

    /**
     * Set the configuration based on the config file.
     * 
     * @param bundle - The OSGi Bundle
     * @param path - The path of the configuration entry found
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
	boolean setConfig(Bundle bundle, String path) throws Exception {    	
    	String solrConfigurationName =  new File(bundle.getEntry(path).getFile()).getName();
    	String solrRelativePath =  path;
    	
        Properties p = new Properties();
        p.setProperty(EmbeddedSolrClient.PROP_SOLR_SERVER_NAME, solrConfigurationName);
        p.setProperty(EmbeddedSolrClient.PROP_SOLR_CONFIG_FILENAME, EmbeddedSolrClient.DEFAULT_SOLR_CONFIG_FILENAME);
       	p.setProperty(EmbeddedSolrClient.PROP_SOLR_SCHEMA_FIlNAME,  EmbeddedSolrClient.DEFAULT_SOLR_SCHEMA_FILENAME);
       	p.setProperty(EmbeddedSolrClient.PROP_SOLR_BUNDLE, bundle.getSymbolicName());
       	String bundleRoot = bundle.getEntry(solrRelativePath).toString();
        // List all files belongs to this configuration separated by comma. 
        StringBuffer sb = new StringBuffer();

        Enumeration<?> entries = bundle.findEntries(solrRelativePath, "*", true);
        boolean firstEntry = true;
        while (entries.hasMoreElements()) {
        	URL confPath = (URL)entries.nextElement();
        	if (!confPath.toString().endsWith("/") && !confPath.toString().endsWith("solrconfig.xml") && !confPath.toString().endsWith("solrschema.xml") ) {
        		if (firstEntry) {
	            	firstEntry = false;
	            } else {
	            	sb.append(",");
	            }
	            // Make the path realative            
	            sb.append(confPath.toString().substring(bundleRoot.length()));
        	}
        }

        p.setProperty(EmbeddedSolrClient.PROP_SOLR_IMPORT_FILES, sb.toString());
        p.setProperty(EmbeddedSolrClient.PROP_SOLR_IMPORT_ROOT, bundleRoot);
        
        Util.performSubstitution(p);
        @SuppressWarnings("rawtypes")
		Hashtable ht = new Hashtable();
        ht.putAll(p);
        ht.put(CONFIGURATION_PROPERTY_NAME, EmbeddedSolrClient.class.getName()+"-"+solrConfigurationName);
   	
        Configuration config = findExistingConfiguration(EmbeddedSolrClient.class.getName(), solrConfigurationName);
        if (config  == null) {
        	config = configurationAdmin.createFactoryConfiguration(EmbeddedSolrClient.class.getName(), null);
            services.info("Install solr instance: "+solrConfigurationName+" "+path);
            config.update(ht);
        } else {
        	services.info("Service already installed, skip installation: "+solrConfigurationName+" "+path);
        }
        return true;
    }

    /**
     * Remove the configuration.
     *
     * @param f
     *            File where the configuration in whas defined.
     * @return
     * @throws Exception
     */
    boolean deleteConfig(Bundle bundle, String path) throws Exception
    {
    	String solrConfigurationName =  new File(bundle.getEntry(path).getFile()).getName();

    	Configuration config = findExistingConfiguration(EmbeddedSolrClient.class.getName(), solrConfigurationName);
    	if (config != null) {
            services.info("Uninstall solr instance: "+solrConfigurationName+" "+path);
    		config.delete();
    	} else {
    		services.info("Service is not installed, skip uninstallation"+solrConfigurationName+" "+path);
    	}

        return true;
    }


    Configuration findExistingConfiguration(String className, String instanceName) throws Exception
    {
        String suffix = instanceName == null ? "" : "-" + instanceName + "";

        String filter = "(" + CONFIGURATION_PROPERTY_NAME + "=" + className + suffix + ")";
        Configuration[] configurations = configurationAdmin.listConfigurations(filter);
        if (configurations != null && configurations.length > 0)
        {
            return configurations[0];
        }
        else
        {
            return null;
        }
    }
}