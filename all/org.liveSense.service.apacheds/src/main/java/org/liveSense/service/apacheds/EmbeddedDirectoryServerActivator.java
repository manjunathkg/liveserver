package org.liveSense.service.apacheds;

import java.io.File;
import java.io.IOException;

import org.apache.directory.server.core.api.CacheService;
import org.apache.directory.server.core.api.InstanceLayout;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(label="%ds.service.name", description="%ds.service.description", metatype=true)
public class EmbeddedDirectoryServerActivator {

	/** The directory service */
	public CacheService cacheService;
	public ApacheDsService service;
	ServiceRegistration serviceReg;

	static Logger log = LoggerFactory.getLogger(EmbeddedDirectoryServerActivator.class);


	public static String getDsHome(BundleContext bundleContext)
			throws IOException {
		String slingHomePath = bundleContext.getProperty("sling.home");
		File dsHome = new File(slingHomePath, "ds");
		if (!dsHome.isDirectory()) {
			if (!dsHome.mkdirs()) {
				log.info(
						"verifyConfiguration: Cannot create ApacheDS home {}, failed creating default configuration ",
						dsHome.getAbsolutePath());
				return null;
			}
		}
		return dsHome.getAbsolutePath();
	}
	/**
	 * Creates a new instance of ApacheDsService. It initializes the directory
	 * service.
	 * 
	 * @throws Exception
	 *             If something went wrong
	 */
	@Activate
	public void activate(BundleContext context) {

		File workDir;
		try {
			workDir = new File(getDsHome(context));
			log.info("ApacheDsService startup: schema.resource.location ="+context.getBundle().getSymbolicName()+"("+context.getBundle().getBundleId()+") - working directory: "+workDir.getAbsolutePath());

			cacheService = new CacheService();
			service = new ApacheDsService();
			service.start(new InstanceLayout(workDir), cacheService);
			serviceReg = context.registerService(ApacheDsService.class.getName(), service, null);

		} catch (IOException e) {
			log.error("Error activating Apache Directory Service", e);
		} catch (Exception e) {
			log.error("Error activating Apache Directory Service", e);
		}

	}


	@Deactivate
	public void deactivate(BundleContext context) {
		try {
			service.stop();
			try {
				service.stop();
			} catch(Exception e) {
				log.error("ApacheDsService shutdown" ,e);
			}

			if (cacheService != null) {
				try {
					cacheService.destroy();
				} catch(Exception e) {
					log.error("Cache Service shutdown" ,e);
				}
			}
			if (serviceReg != null) {
				try {
					context.ungetService(serviceReg.getReference());
				} catch (Throwable th) {
					log.error("Error on unget service", serviceReg);
				}
			}

		} catch (Exception e) {
			log.error("Error deactivating Apache Directory Service", e);
		}

	}

}
