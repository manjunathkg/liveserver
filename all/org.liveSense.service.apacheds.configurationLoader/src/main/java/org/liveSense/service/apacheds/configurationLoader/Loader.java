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
package org.liveSense.service.apacheds.configurationLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.directory.server.protocol.shared.store.LdifFileLoader;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.liveSense.service.apacheds.ApacheDsService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The <code>ContfigurationLoader</code> is the service
 * providing the following functionality:
 * <ul>
 * <li>Bundle listener to load initial apacheDS configurations for bundles.
 * <li>Fires OSGi EventAdmin events on behalf of internal helper objects
 * </ul>
 **/


@Component(label="%apacheDSLoader.name",
description="%apacheDSLoader.description",
immediate=true,
metatype=true,
configurationFactory=true,
policy=ConfigurationPolicy.OPTIONAL,
createPid=false)
public class Loader implements SynchronousBundleListener {

	Logger log = LoggerFactory.getLogger(Loader.class);
	ConfigurationAdmin configurationAdmin;
	ServiceMediator services;
	
	@Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.MANDATORY_UNARY)
	private ApacheDsService apacheDsService;

	// ---------- BundleListener -----------------------------------------------
	/**
	 * Loads and unloads any configuration provided by the bundle whose state
	 * changed. If the bundle has been started, the configuration is loaded. If
	 * the bundle is about to stop, the configurations are unloaded.
	 *
	 * @param event The <code>BundleEvent</code> representing the bundle state
	 *            change.
	 */
	@Override
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
						"bundleChanged: Problem loading apacheDS configuration of bundle "
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
						"bundleChanged: Problem unloading apacheDS configuration of bundle "
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
				Bundle bundle = bundles[i];

				if ((bundle.getState() & (Bundle.ACTIVE)) != 0) {
					// load configurations from bundles which are ACTIVE
					try {
						registerBundle(bundle);
					} catch (Throwable t) {
						services.error(
								"Problem loading apacheDS configuration of bundle "
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
								"Problem loading apacheDS configuration of bundle "
										+ bundle.getSymbolicName() + " ("
										+ bundle.getBundleId() + ")", t);
					} finally {
					}
				} else {
					ignored++;
				}


			}
			services.info(
					"Out of "+bundles.length+" bundles, "+ignored+" were not in a suitable state for apacheDS configuration loading");
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
				Bundle bundle = bundles[i];

				if ((bundle.getState()) == 0) {
					// remove configurations from bundles which are not ACTIVE
					try {
						unregisterBundle(bundle);
					} catch (Throwable t) {
						services.error(
								"Problem loading apacheDS configuration of bundle "
										+ bundle.getSymbolicName() + " ("
										+ bundle.getBundleId() + ")", t);
					} finally {
					}
				} else {
					ignored++;
				}


			}
			services.info(
					"Out of "+bundles.length+" bundles, "+ignored+" were not in a suitable state for apacheDS configuration loading");
		} catch (Throwable t) {
			services.error("activate: Problem while loading apacheDS configuration", t);
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

		services.debug("Registering bundle "+bundle.getSymbolicName()+" for apacheDS configuration loading.");
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
			services.debug("Bundle "+bundle.getSymbolicName()+" has no ApacheDS configuration(s)");
			return true;
		}
		services.info("Bundle "+bundle.getSymbolicName()+" has ApacheDS configuration(s)");

		while (pathIter.hasNext()) {
			PathEntry path = (PathEntry)pathIter.next();
			install(bundle, path.getPath());

			/*Enumeration<?> entries = bundle.getEntryPaths(path.getPath());

			if (entries != null) {
				while (entries.hasMoreElements()) {
					String confPath = (String)entries.nextElement();
					//if (canHandle(bundle, confPath)) {
						install(bundle, confPath);
					//}
				}
			}*/
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
			services.debug("Bundle "+bundle.getSymbolicName()+" has no ApacheDS configuration(s)");
			return;
		}
		services.info("Bundle "+bundle.getSymbolicName()+" has ApacheDS configuration(s)");

		while (pathIter.hasNext()) {
			PathEntry path = (PathEntry)pathIter.next();
			uninstall(bundle, path.getPath());
			/*
			Enumeration<?> entries = bundle.getEntryPaths(path.getPath());

			if (entries != null) {
				while (entries.hasMoreElements()) {
					String confPath = (String)entries.nextElement();
					//if (canHandle(bundle, confPath)) {
						uninstall(bundle, confPath);
					//}
				}
			}
			*/
		}

	}

	public boolean canHandle(Bundle bundle, String name) {
		return bundle.findEntries(name, "*.ldif", false).hasMoreElements();
	}

	private static String getDsHome(BundleContext bundleContext)
			throws IOException {
		String slingHomePath = bundleContext.getProperty("sling.home");
		File dsHome = new File(slingHomePath, "ds");
		if (!dsHome.isDirectory()) {
			return null;
		}
		return dsHome.getAbsolutePath();
	}

	public void install(Bundle bundle, String path) throws Exception
	{
		String apacheDSConfigurationName =  new File(bundle.getEntry(path).getFile()).getName();
		String apacheDSRelativePath =  path;
		String bundleRoot = bundle.getEntry(apacheDSRelativePath).toString();

		String apacheDSHome = getDsHome(bundle.getBundleContext());
		File apacheDSConfigurationHome = new File(apacheDSHome, bundle.getSymbolicName());
		Enumeration<?> entries = bundle.findEntries(apacheDSRelativePath, "*", true);
		boolean firstEntry = true;
		while (entries != null && entries.hasMoreElements()) {
			URL confPath = (URL)entries.nextElement();
			if (!confPath.toString().endsWith("/") && confPath.toString().endsWith(".ldif")) {
				// Make the path realative
				// Put the file to DS instance 
				if (!apacheDSConfigurationHome.exists()) {
					apacheDSConfigurationHome.mkdirs();
				}

				File con = new File(apacheDSConfigurationHome, confPath.toString().substring(bundleRoot.length()));
				if (!con.exists()) {
					FileOutputStream output = null; 
					try {
						output = FileUtils.openOutputStream(con);
						IOUtils.copy(confPath.openStream(), output);
						output.close(); // don't swallow close Exception if copy completes normally
					} catch (Throwable th) {
						log.error("Could not write ldif: "+con.getAbsolutePath());						
					} finally {
						if (output != null) IOUtils.closeQuietly(output);
					}
					try {
						LdifFileLoader ldifLoader = new LdifFileLoader(apacheDsService.getDirectoryService().getAdminSession(), con.getAbsolutePath());
						ldifLoader.execute();
					} catch (Throwable th) {
						log.error("Could not load ldif: "+con.getAbsolutePath(), th);
					}
				}
			}
		}
	}

	public void uninstall(Bundle bundle, String path) throws Exception{
	}

}