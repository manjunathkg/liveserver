/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package org.liveSense.service.apacheds;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.directory.api.ldap.model.constants.SchemaConstants;
import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.DefaultAttribute;
import org.apache.directory.api.ldap.model.entry.DefaultModification;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.entry.Modification;
import org.apache.directory.api.ldap.model.entry.ModificationOperation;
import org.apache.directory.api.ldap.model.filter.ExprNode;
import org.apache.directory.api.ldap.model.filter.PresenceNode;
import org.apache.directory.api.ldap.model.message.AliasDerefMode;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.schema.AttributeType;
import org.apache.directory.api.ldap.model.schema.SchemaManager;
import org.apache.directory.api.ldap.model.schema.registries.SchemaLoader;
import org.apache.directory.api.ldap.model.schema.syntaxCheckers.CsnSyntaxChecker;
import org.apache.directory.api.ldap.model.schema.syntaxCheckers.GeneralizedTimeSyntaxChecker;
import org.apache.directory.api.ldap.model.schema.syntaxCheckers.UuidSyntaxChecker;
import org.apache.directory.api.ldap.schemaextractor.SchemaLdifExtractor;
import org.apache.directory.api.ldap.schemaextractor.impl.DefaultSchemaLdifExtractor;
import org.apache.directory.api.ldap.schemaloader.LdifSchemaLoader;
import org.apache.directory.api.ldap.schemamanager.impl.DefaultSchemaManager;
import org.apache.directory.api.util.DateUtils;
import org.apache.directory.api.util.exception.Exceptions;
import org.apache.directory.server.config.ConfigPartitionReader;
import org.apache.directory.server.config.LdifConfigExtractor;
import org.apache.directory.server.config.beans.ConfigBean;
import org.apache.directory.server.config.beans.DirectoryServiceBean;
import org.apache.directory.server.config.beans.KdcServerBean;
import org.apache.directory.server.config.beans.LdapServerBean;
import org.apache.directory.server.config.beans.NtpServerBean;
import org.apache.directory.server.config.builder.ServiceBuilder;
import org.apache.directory.server.core.api.CacheService;
import org.apache.directory.server.core.api.CoreSession;
import org.apache.directory.server.core.api.DirectoryService;
import org.apache.directory.server.core.api.InstanceLayout;
import org.apache.directory.server.core.api.filtering.EntryFilteringCursor;
import org.apache.directory.server.core.api.interceptor.context.ModifyOperationContext;
import org.apache.directory.server.core.api.partition.Partition;
import org.apache.directory.server.core.api.schema.SchemaPartition;
import org.apache.directory.server.core.partition.ldif.LdifPartition;
import org.apache.directory.server.core.partition.ldif.SingleFileLdifPartition;
import org.apache.directory.server.i18n.I18n;
import org.apache.directory.server.integration.http.HttpServer;
import org.apache.directory.server.kerberos.kdc.KdcServer;
import org.apache.directory.server.ldap.LdapServer;
import org.apache.directory.server.ntp.NtpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A class used to start various servers in a given {@link InstanceLayout}.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ApacheDsService
{
	/** A logger for this class */
	private static final Logger LOG = LoggerFactory.getLogger( ApacheDsService.class );

	/** The LDAP server instance */
	private LdapServer ldapServer;

	/** The NTP server instance */
	private NtpServer ntpServer;

	/** The DNS server instance */
	//    private DnsServer dnsServer;

	/** The Change Password server instance *
    private ChangePasswordServer changePwdServer;/

    /** The Kerberos server instance */
	private KdcServer kdcServer;

	/** The started HttpServer */
	private HttpServer httpServer;

	/** The Schema partition */
	private LdifPartition schemaLdifPartition;

	/** The SchemaManager instance */
	private SchemaManager schemaManager;

	/** The configuration partition */
	private SingleFileLdifPartition configPartition;

	/** The configuration reader instance */
	private ConfigPartitionReader cpReader;

	// variables used during the initial startup to update the mandatory operational
	// attributes
	/** The UUID syntax checker instance */
	private final UuidSyntaxChecker uuidChecker = new UuidSyntaxChecker();

	/** The CSN syntax checker instance */
	private final CsnSyntaxChecker csnChecker = new CsnSyntaxChecker();

	private final GeneralizedTimeSyntaxChecker timeChecker = new GeneralizedTimeSyntaxChecker();

	// private static final Map<String, AttributeTypeOptions> MANDATORY_ENTRY_ATOP_MAP = new HashMap<String, AttributeTypeOptions>();

	private boolean isConfigPartitionFirstExtraction = false;

	private boolean isSchemaPartitionFirstExtraction = false;


	/**
	 * starts various services configured according to the 
	 * configuration present in the given instance's layout
	 *
	 * @param instanceLayout the on disk location's layout of the intance to be started
	 * @throws Exception
	 */
	public void start( InstanceLayout instanceLayout, CacheService cacheService ) throws Exception
	{
		File partitionsDir = instanceLayout.getPartitionsDirectory();

		if ( !partitionsDir.exists() )
		{
			LOG.info( "partition directory doesn't exist, creating {}", partitionsDir.getAbsolutePath() );
			if ( !partitionsDir.mkdirs() )
			{
				throw new IOException( I18n.err( I18n.ERR_112_COULD_NOT_CREATE_DIRECORY, partitionsDir ) );
			}
		}

		LOG.info( "using partition dir {}", partitionsDir.getAbsolutePath() );

		initSchemaManager( instanceLayout );
		initSchemaLdifPartition( instanceLayout );
		initConfigPartition( instanceLayout );

		// Read the configuration
		cpReader = new ConfigPartitionReader( configPartition );

		ConfigBean configBean = cpReader.readConfig();

		DirectoryServiceBean directoryServiceBean = configBean.getDirectoryServiceBean();

		// Initialize the DirectoryService now
		DirectoryService directoryService = initDirectoryService( instanceLayout, directoryServiceBean, cacheService );


		// start the LDAP server
		startLdap( directoryServiceBean.getLdapServerBean(), directoryService );

		// start the NTP server
		startNtp( directoryServiceBean.getNtpServerBean(), directoryService );

		// Initialize the DNS server (Not ready yet)
		// initDns( configBean );

		// Initialize the DHCP server (Not ready yet)
		// initDhcp( configBean );

		// start the ChangePwd server (Not ready yet)
		//startChangePwd( directoryServiceBean.getChangePasswordServerBean(), directoryService );

		// start the Kerberos server
		startKerberos( directoryServiceBean.getKdcServerBean(), directoryService );

		// start the jetty http server
		//startHttpServer( directoryServiceBean.getHttpServerBean(), directoryService );
	}


	/**
	 * Initialize the schema Manager by loading the schema LDIF files
	 * 
	 * @param instanceLayout the instance layout
	 * @throws Exception in case of any problems while extracting and writing the schema files
	 */
	private void initSchemaManager( InstanceLayout instanceLayout ) throws Exception
	{
		File schemaPartitionDirectory = new File( instanceLayout.getPartitionsDirectory(), "schema" );

		// Extract the schema on disk (a brand new one) and load the registries
		if ( schemaPartitionDirectory.exists() )
		{
			LOG.info( "schema partition already exists, skipping schema extraction" );
		}
		else
		{
			SchemaLdifExtractor extractor = new DefaultSchemaLdifExtractor( instanceLayout.getPartitionsDirectory() );
			extractor.extractOrCopy();
			isSchemaPartitionFirstExtraction = true;
		}

		SchemaLoader loader = new LdifSchemaLoader( schemaPartitionDirectory );
		schemaManager = new DefaultSchemaManager( loader );

		// We have to load the schema now, otherwise we won't be able
		// to initialize the Partitions, as we won't be able to parse 
		// and normalize their suffix Dn
		schemaManager.loadAllEnabled();

		List<Throwable> errors = schemaManager.getErrors();

		if ( errors.size() != 0 )
		{
			throw new Exception( I18n.err( I18n.ERR_317, Exceptions.printErrors( errors ) ) );
		}
	}


	/**
	 * Initialize the schema partition
	 * 
	 * @param instanceLayout the instance layout
	 * @throws Exception in case of any problems while initializing the SchemaPartition
	 */
	private void initSchemaLdifPartition( InstanceLayout instanceLayout ) throws Exception
	{
		File schemaPartitionDirectory = new File( instanceLayout.getPartitionsDirectory(), "schema" );

		// Init the LdifPartition
		schemaLdifPartition = new LdifPartition( schemaManager );
		schemaLdifPartition.setPartitionPath( schemaPartitionDirectory.toURI() );
	}


	/**
	 * 
	 * initializes a LDIF partition for configuration
	 * 
	 * @param instanceLayout the instance layout
	 * @throws Exception in case of any issues while extracting the schema
	 */
	private void initConfigPartition( InstanceLayout instanceLayout ) throws Exception
	{
		File confFile = new File( instanceLayout.getConfDirectory(), LdifConfigExtractor.LDIF_CONFIG_FILE );

		if ( confFile.exists() )
		{
			LOG.info( "config partition already exists, skipping default config extraction" );
		}
		else
		{
			LdifConfigExtractor.extractSingleFileConfig( instanceLayout.getConfDirectory(),
					LdifConfigExtractor.LDIF_CONFIG_FILE, true );
			isConfigPartitionFirstExtraction = true;
		}

		configPartition = new SingleFileLdifPartition( schemaManager );
		configPartition.setId( "config" );
		configPartition.setPartitionPath( confFile.toURI() );
		configPartition.setSuffixDn( new Dn( schemaManager, "ou=config" ) );
		configPartition.setSchemaManager( schemaManager );

		configPartition.initialize();
	}


	private DirectoryService initDirectoryService( InstanceLayout instanceLayout,
			DirectoryServiceBean directoryServiceBean, CacheService cacheService ) throws Exception
			{
		LOG.info( "Initializing the DirectoryService..." );

		long startTime = System.currentTimeMillis();

		DirectoryService directoryService = ServiceBuilder.createDirectoryService( directoryServiceBean,
				instanceLayout, schemaManager );

		// The schema partition
		SchemaPartition schemaPartition = new SchemaPartition( schemaManager );
		schemaPartition.setWrappedPartition( schemaLdifPartition );
		directoryService.setSchemaPartition( schemaPartition );

		directoryService.addPartition( configPartition );

		// Store the default directories
		directoryService.setInstanceLayout( instanceLayout );
		directoryService.setCacheService(cacheService);
		directoryService.startup();

		if ( isConfigPartitionFirstExtraction )
		{
			LOG.info( "begining to update config partition LDIF files after modifying manadatory attributes" );

			// disable writes to the disk upon every modification to improve performance
			configPartition.setEnableRewriting( false );

			// perform updates
			updateMandatoryOpAttributes( configPartition, directoryService );

			// enable writes to disk, this will save the partition data first if found dirty
			configPartition.setEnableRewriting( true );

			LOG.info( "config partition data was successfully updated" );
		}

		if ( isSchemaPartitionFirstExtraction )
		{
			LOG.info( "begining to update schema partition LDIF files after modifying manadatory attributes" );

			updateMandatoryOpAttributes( schemaLdifPartition, directoryService );

			LOG.info( "schema partition data was successfully updated" );
		}

		LOG.info( "DirectoryService initialized in {} milliseconds", ( System.currentTimeMillis() - startTime ) );

		return directoryService;
			}


	/**
	 * start the LDAP server
	 */
	private void startLdap( LdapServerBean ldapServerBean, DirectoryService directoryService ) throws Exception
	{
		LOG.info( "Starting the LDAP server" );
		long startTime = System.currentTimeMillis();

		ldapServer = ServiceBuilder.createLdapServer( ldapServerBean, directoryService );

		if ( ldapServer == null )
		{
			LOG.info( "Cannot find any reference to the LDAP Server in the configuration : the server won't be started" );
			return;
		}

		printBanner( BANNER_LDAP );

		ldapServer.setDirectoryService( directoryService );

		// And start the server now
		try
		{
			ldapServer.start();
		}
		catch ( Exception e )
		{
			LOG.error( "Cannot start the server : " + e.getMessage() );
		}

		LOG.info( "LDAP server: started in {} milliseconds", ( System.currentTimeMillis() - startTime ) + "" );
	}


	/**
	 * start the NTP server
	 */
	private void startNtp( NtpServerBean ntpServerBean, DirectoryService directoryService ) throws Exception
	{
		LOG.info( "Starting the NTP server" );
		long startTime = System.currentTimeMillis();

		ntpServer = ServiceBuilder.createNtpServer( ntpServerBean, directoryService );

		if ( ntpServer == null )
		{
			LOG.info( "Cannot find any reference to the NTP Server in the configuration : the server won't be started" );
			return;
		}

		printBanner( BANNER_NTP );

		ntpServer.start();

		if ( LOG.isInfoEnabled() )
		{
			LOG.info( "NTP server: started in {} milliseconds", ( System.currentTimeMillis() - startTime ) + "" );
		}
	}


	/**
	 * Initialize the DNS server
	 */
	//    private void initDns( InstanceLayout layout ) throws Exception
	//    {
	//        if ( factory == null )
	//        {
	//            return;
	//        }
	//
	//        try
	//        {
	//            dnsServer = ( DnsServer ) factory.getBean( "dnsServer" );
	//        }
	//        catch ( Exception e )
	//        {
	//            LOG.info( "Cannot find any reference to the DNS Server in the configuration : the server won't be started" );
	//            return;
	//        }
	//        
	//        System.out.println( "Starting the DNS server" );
	//        LOG.info( "Starting the DNS server" );
	//        
	//        printBanner( BANNER_DNS );
	//        long startTime = System.currentTimeMillis();
	//
	//        dnsServer.start();
	//        System.out.println( "DNS Server started" );
	//
	//        if ( LOG.isInfoEnabled() )
	//        {
	//            LOG.info( "DNS server: started in {} milliseconds", ( System.currentTimeMillis() - startTime ) + "" );
	//        }
	//    }

	/**
	 * start the KERBEROS server
	 */
	private void startKerberos( KdcServerBean kdcServerBean, DirectoryService directoryService ) throws Exception
	{
		LOG.info( "Starting the Kerberos server" );
		long startTime = System.currentTimeMillis();

		kdcServer = ServiceBuilder.createKdcServer( kdcServerBean, directoryService );

		if ( kdcServer == null )
		{
			LOG.info( "Cannot find any reference to the Kerberos Server in the configuration : the server won't be started" );
			return;
		}

		getDirectoryService().startup();
		kdcServer.setDirectoryService( getDirectoryService() );

		printBanner( BANNER_KERBEROS );

		kdcServer.start();

		if ( LOG.isInfoEnabled() )
		{
			LOG.info( "Kerberos server: started in {} milliseconds", ( System.currentTimeMillis() - startTime ) + "" );
		}
	}


	/**
	 * start the Change Password server
	 *
    private void startChangePwd( ChangePasswordServerBean changePwdServerBean, DirectoryService directoryService ) throws Exception
    {
        changePwdServer = ServiceBuilder.createChangePasswordServer( changePwdServerBean, directoryService );

        if ( changePwdServer == null )
        {
            LOG.info( "Cannot find any reference to the Change Password Server in the configuration : the server won't be started" );
            return;
        }

        LOG.info( "Starting the Change Password server" );
        long startTime = System.currentTimeMillis();

        getDirectoryService().startup();
        changePwdServer.setDirectoryService( getDirectoryService() );

        LOG.info( "Starting the Change Password server" );

        printBanner( BANNER_CHANGE_PWD );

        changePwdServer.start();

        if ( LOG.isInfoEnabled() )
        {
            LOG.info( "Change Password server: started in {} milliseconds", ( System.currentTimeMillis() - startTime )
                + "" );
        }
    }
	 */

	/**
	 * start the embedded HTTP server
	 */
	/*
	private void startHttpServer( HttpServerBean httpServerBean, DirectoryService directoryService ) throws Exception
	{
		httpServer = ServiceBuilder.createHttpServer( httpServerBean, directoryService );

		if ( httpServer == null )
		{
			LOG.info( "Cannot find any reference to the HTTP Server in the configuration : the server won't be started" );
			return;
		}

		LOG.info( "Starting the Http server" );
		long startTime = System.currentTimeMillis();

		httpServer.start( getDirectoryService() );

		if ( LOG.isInfoEnabled() )
		{
			LOG.info( "Http server: started in {} milliseconds", ( System.currentTimeMillis() - startTime )
					+ "" );
		}
	}
	*/

	public DirectoryService getDirectoryService()
	{
		return ldapServer.getDirectoryService();
	}


	public void synch() throws Exception
	{
		ldapServer.getDirectoryService().sync();
	}


	public void stop() throws Exception
	{
		// Stops the server
		if ( ldapServer != null )
		{
			ldapServer.stop();
		}

		if ( kdcServer != null )
		{
			kdcServer.stop();
		}

		/*if ( changePwdServer != null )
        {
            changePwdServer.stop();
        }*/

		if ( ntpServer != null )
		{
			ntpServer.stop();
		}

		if ( httpServer != null )
		{
			httpServer.stop();
		}

		// We now have to stop the underlaying DirectoryService
		ldapServer.getDirectoryService().shutdown();
	}

	private static final String BANNER_LDAP = "           _                     _          ____  ____   \n"
			+ "          / \\   _ __    ___  ___| |__   ___|  _ \\/ ___|  \n"
			+ "         / _ \\ | '_ \\ / _` |/ __| '_ \\ / _ \\ | | \\___ \\  \n"
			+ "        / ___ \\| |_) | (_| | (__| | | |  __/ |_| |___) | \n"
			+ "       /_/   \\_\\ .__/ \\__,_|\\___|_| |_|\\___|____/|____/  \n"
			+ "               |_|                                       \n";

	private static final String BANNER_NTP = "           _                     _          _   _ _____ _ __    \n"
			+ "          / \\   _ __    ___  ___| |__   ___| \\ | |_  __| '_ \\   \n"
			+ "         / _ \\ | '_ \\ / _` |/ __| '_ \\ / _ \\ .\\| | | | | |_) |  \n"
			+ "        / ___ \\| |_) | (_| | (__| | | |  __/ |\\  | | | | .__/   \n"
			+ "       /_/   \\_\\ .__/ \\__,_|\\___|_| |_|\\___|_| \\_| |_| |_|      \n"
			+ "               |_|                                              \n";

	private static final String BANNER_KERBEROS = "           _                     _          _  __ ____   ___    \n"
			+ "          / \\   _ __    ___  ___| |__   ___| |/ /|  _ \\ / __|   \n"
			+ "         / _ \\ | '_ \\ / _` |/ __| '_ \\ / _ \\ ' / | | | / /      \n"
			+ "        / ___ \\| |_) | (_| | (__| | | |  __/ . \\ | |_| \\ \\__    \n"
			+ "       /_/   \\_\\ .__/ \\__,_|\\___|_| |_|\\___|_|\\_\\|____/ \\___|   \n"
			+ "               |_|                                              \n";

	//    private static final String BANNER_DNS =
	//          "           _                     _          ____  _   _ ____    \n"
	//        + "          / \\   _ __    ___  ___| |__   ___|  _ \\| \\ | / ___|   \n"
	//        + "         / _ \\ | '_ \\ / _` |/ __| '_ \\ / _ \\ | | |  \\| \\__  \\   \n"
	//        + "        / ___ \\| |_) | (_| | (__| | | |  __/ |_| | . ' |___) |  \n"
	//        + "       /_/   \\_\\ .__/ \\__,_|\\___|_| |_|\\___|____/|_|\\__|____/   \n"
	//        + "               |_|                                              \n";
	//
	//    
	//    private static final String BANNER_DHCP =
	//          "           _                     _          ____  _   _  ___ ____  \n"
	//        + "          / \\   _ __    ___  ___| |__   ___|  _ \\| | | |/ __|  _ \\ \n"
	//        + "         / _ \\ | '_ \\ / _` |/ __| '_ \\ / _ \\ | | | |_| / /  | |_) )\n"
	//        + "        / ___ \\| |_) | (_| | (__| | | |  __/ |_| |  _  \\ \\__|  __/ \n"
	//        + "       /_/   \\_\\ .__/ \\__,_|\\___|_| |_|\\___|____/|_| |_|\\___|_|    \n"
	//        + "               |_|                                                 \n";

	private static final String BANNER_CHANGE_PWD = "         ___                              ___ __  __ __  ______    \n"
			+ "        / __|_       ___ _ __   ____  ___|  _ \\ \\ \\ / / / |  _ \\   \n"
			+ "       / /  | |__  / _` | '  \\ / ___\\/ _ \\ |_) \\ \\ / /\\/ /| | | |  \n"
			+ "       \\ \\__| '_  \\ (_| | |\\  | |___ | __/  __/ \\ ' /   / | |_| |  \n"
			+ "        \\___|_| |_|\\__,_|_| |_|\\__. |\\___| |     \\_/ \\_/  |____/   \n"
			+ "                                  |_|    |_|                       \n";


	/**
	 * Print the banner for a server
	 */
	public static void printBanner( String bannerConstant )
	{
		System.out.println( bannerConstant );
	}


	/**
	 * 
	 * adds mandatory operational attributes {@link #MANDATORY_ENTRY_ATOP_MAP} and updates all the LDIF files.
	 * WARN: this method is only called for the first time when schema and config files are bootstrapped
	 *       afterwards it is the responsibility of the user to ensure correctness of LDIF files if modified
	 *       by hand 
	 * 
	 * Note: we do these modifications explicitly cause we have no idea if each entry's LDIF file has the
	 *       correct values for all these mandatory attributes
	 *       
	 * @param partition instance of the partition Note: should only be those which are loaded before starting the DirectoryService
	 * @param dirService the DirectoryService instance
	 * @throws Exception
	 */
	public void updateMandatoryOpAttributes( Partition partition, DirectoryService dirService ) throws Exception
	{
		CoreSession session = dirService.getAdminSession();

		String adminDn = session.getEffectivePrincipal().getName();

		ExprNode filter = new PresenceNode( SchemaConstants.OBJECT_CLASS_AT );

		AttributeType uuidAtT = schemaManager.lookupAttributeTypeRegistry( SchemaConstants.ENTRY_UUID_AT );
		AttributeType atTypeT = schemaManager.lookupAttributeTypeRegistry( SchemaConstants.ENTRY_CSN_AT );
		AttributeType creatorAtT = schemaManager.lookupAttributeTypeRegistry( SchemaConstants.CREATORS_NAME_AT );
		AttributeType createdTimeAtT = schemaManager.lookupAttributeTypeRegistry( SchemaConstants.CREATE_TIMESTAMP_AT );


		//        HashSet<AttributeTypeOptions> options = new HashSet<AttributeTypeOptions>( MANDATORY_ENTRY_ATOP_MAP.values());
		/*


        AttributeType at;
        at
        //
        String id = SchemaUtils.stripOptions( returnAttribute );
        Set<String> options = SchemaUtils.getOptions( returnAttribute );

        AttributeType attributeType = session.getDirectoryService()
            .getSchemaManager().lookupAttributeTypeRegistry( id );
        AttributeTypeOptions attrOptions = new AttributeTypeOptions( attributeType, options );
//        

        EntryFilteringCursor cursor = session.search( partition.getSuffixDn(), SearchScope.SUBTREE, filter,
            AliasDerefMode.NEVER_DEREF_ALIASES, new HashSet<AttributeTypeOptions>( MANDATORY_ENTRY_ATOP_MAP.values() ) );
        cursor.beforeFirst();
		 */
		EntryFilteringCursor cursor = session.search( partition.getSuffixDn(), SearchScope.SUBTREE, filter,
				AliasDerefMode.NEVER_DEREF_ALIASES, SchemaConstants.OBJECT_CLASS_AT_OID, SchemaConstants.ENTRY_UUID_AT_OID, SchemaConstants.ENTRY_CSN_AT_OID, SchemaConstants.CREATORS_NAME_AT_OID, SchemaConstants.CREATORS_NAME_AT_OID);
		cursor.beforeFirst();


		List<Modification> mods = new ArrayList<Modification>();

		while ( cursor.next() )
		{
			Entry entry = cursor.get();

			Attribute uuidAt = entry.get( uuidAtT );
			String uuid = ( uuidAt == null ? null : uuidAt.getString() );

			if ( !uuidChecker.isValidSyntax( uuid ) )
			{
				uuidAt = new DefaultAttribute( uuidAtT, UUID.randomUUID().toString() );
			}

			Modification uuidMod = new DefaultModification( ModificationOperation.REPLACE_ATTRIBUTE, uuidAt );
			mods.add( uuidMod );

			Attribute csnAt = entry.get( atTypeT );
			String csn = ( csnAt == null ? null : csnAt.getString() );

			if ( !csnChecker.isValidSyntax( csn ) )
			{
				csnAt = new DefaultAttribute( atTypeT, dirService.getCSN().toString() );
			}

			Modification csnMod = new DefaultModification( ModificationOperation.REPLACE_ATTRIBUTE, csnAt );
			mods.add( csnMod );

			Attribute creatorAt = entry.get( creatorAtT );
			String creator = ( creatorAt == null ? "" : creatorAt.getString().trim() );

			if ( ( creator.length() == 0 ) || ( !Dn.isValid( creator ) ) )
			{
				creatorAt = new DefaultAttribute( creatorAtT, adminDn );
			}

			Modification creatorMod = new DefaultModification( ModificationOperation.REPLACE_ATTRIBUTE, creatorAt );
			mods.add( creatorMod );

			Attribute createdTimeAt = entry.get( createdTimeAtT );
			String createdTime = ( createdTimeAt == null ? null : createdTimeAt.getString() );

			if ( !timeChecker.isValidSyntax( createdTime ) )
			{
				createdTimeAt = new DefaultAttribute( createdTimeAtT, DateUtils.getGeneralizedTime() );
			}

			Modification createdMod = new DefaultModification( ModificationOperation.REPLACE_ATTRIBUTE, createdTimeAt );
			mods.add( createdMod );

			if ( !mods.isEmpty() )
			{
				LOG.debug( "modifying the entry {} after adding missing manadatory operational attributes",
						entry.getDn() );
				ModifyOperationContext modifyContext = new ModifyOperationContext( session );
				modifyContext.setEntry( entry );
				modifyContext.setDn( entry.getDn() );
				modifyContext.setModItems( mods );
				partition.modify( modifyContext );
			}

			mods.clear();
		}

		cursor.close();
	}

}
