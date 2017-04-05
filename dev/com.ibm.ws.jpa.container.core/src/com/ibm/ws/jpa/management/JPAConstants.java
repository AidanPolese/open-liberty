// IBM Confidential OCO Source Material
// Copyright IBM Corp. 2006, 2013
//
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change Activity:
//
// Reason    Version   Date     Userid    Change Description
// --------- --------- -------- --------- -----------------------------------------
// d392996   EJB3      20060930 leealber : Initial Release
// d392996.3 EJB3      20061020 leealber : Add and change system property names for provider & data sources
// d403070   EJB3      20061105 leealber : set Websphere version as default persistence provider to
// d406735   EJB3      20061120 leealber : Correct <exclude-unlisted-classes> semantics interpretation.
// d406994   EJB3      20071122 leealber : CI:change package to com.ibm.ws.jpa.pxml10
// d406994.4 EJB3      20061120 leealber : CI: miscellaneous clean up
// d407846   EJB3      20061123 leealber : Integrate openJPA logging to WAS Tr
// d408321   EJB3      20061128 leealber : Safe-guarding substring results exception
// d416151   EJB3      20070120 leealber : Container manager persistence context part 1
// d416151.2 EJB3      20070220 leealber : Container managed persistence context part 2
// d416151.3.11 EJB3   20070504 leealber : Code review clean up.
// d473432.1 EJB3      20071011 leealber : Add Versions class name
// d472866.1 EJB3      20071015 tkb      : register non-transactional with local tran
// d496032.1 WAS70     20080220 jckrueg  : Add EJB 3.0 module version constant
// d510184   WAS70     20080505 tkb      : Create separate EMF for each java:comp
// F1879-16302
//           WAS80     20091112 tkb      : support 2.0 and 1.0 xml separately
// F743-8155 WAS80     20100305 bkail    : Use SchemaHelper
// PM20625   WAS70     20100816 jckrueg  : Add property to exclude apps from JPA processing
// d706751   WAS80     20110609 bkail    : Add ALWAYS_USE_EMF_PROXY and EMF constants
// F61057    WAS85     20120224 bkail    : Support JPAEMPoolHelper
// 734773    WAS855    20120524 daboshe  : Removed JNDI_NAMESPACE_GLOBAL 
// d731877   WAS8501   20120518 jrbauer  : Enable pool access to WsJpa provider via lightweight EMF wrapper
// F84119    WAS855    20121010 jrbauer  : Moved loggers and logger factories into their own pkg
// RTC113511 RWAS90    20131009 bkail    : Use public visibility for constants
// RTC109631 RWAS90    20140128 rgcurtis : Automatically set eclipselink.target-server
// --------- --------- -------- --------- -----------------------------------------
package com.ibm.ws.jpa.management;

import java.security.AccessController;

import com.ibm.ejs.util.dopriv.SystemGetPropertyPrivileged;

/**
 * Manifested Constants used by the JPA Service implementation.
 */
public final class JPAConstants
{
    // ********** JPA general, trace and bundling constants
    public static final String JPA_TRACE_GROUP = "JPA";

    public static final String JPA_RESOURCE_BUNDLE_NAME = "com.ibm.ws.jpa.jpa";

    static final String PROVIDER_EXTENSION_NAME_SPACE = "com.ibm.ws.jpa";

    static final String PROVIDER_EXTENSION_POINT_ID = "com.ibm.ws.jpa.jpaextension";

    static final String EAR_SCOPE_MODULE_NAME = "EAR_Scope_Module";

    // ********** Java EE component context and namespace constants
    static final String JNDI_NAMESPACE_JAVA_COMP_ENV = "java:comp/env/"; // d408321
    static final String JNDI_NAMESPACE_JAVA_APP_ENV = "java:app/env/";

    static final String JNDI_TX_SYNC_REGISTRY = "java:comp/TransactionSynchronizationRegistry"; // d416151

    static final String JNDI_UOW_SYNC_REGISTRY = "java:comp/websphere/UOWSynchronizationRegistry"; // d472866.1

    // ********** JAXB persistence.xml v1.0 parsing constant          F1879-16302
    public static final String PERSISTENCE_10_XML_JAXB_PACKAGE_NAME = "com.ibm.ws.jpa.pxml10";

    // ********** JAXB persistence.xml v2.0 parsing constant          F1879-16302
    public static final String PERSISTENCE_20_XML_JAXB_PACKAGE_NAME = "com.ibm.ws.jpa.pxml20";

    // ********** System properties keys
    // Uuser-defined default JPA provider class name.
    static final String JPA_PROVIDER_SYSTEM_PROPERTY_NAME = "com.ibm.websphere.jpa.default.provider";

    // User-defined default JTA and non-JTA data sources.
    static final String JPA_JTA_DATASOURCE_SYSTEM_PROPERTY_NAME = "com.ibm.websphere.jpa.default.jta.datasource";

    static final String JPA_NONJTA_DATASOURCE_SYSTEM_PROPERTY_NAME = "com.ibm.websphere.jpa.default.nonjta.datasource";

    // Override all <exclude-unlisted-classes> semantics to mean
    // <exclude-unlisted-classes>true</exclude-unlisted-classes>
    // Applies only to persistence.xml files at the 1.0 level of the spec. F1879-16302
    static final boolean JPA_OVERRIDE_EXCLUDE_UNLISTED_CLASSES = Boolean.getBoolean
                    (((String) AccessController.doPrivileged
                                    (new SystemGetPropertyPrivileged
                                    ("com.ibm.websphere.jpa.override.exclude.unlisted.classes", "false"))
                    ).toLowerCase());

    // EntityManager pool capacity per PersistenceContext reference.      d510184
    static final String JPA_ENTITY_MANAGER_POOL_CAPACITY =
                    "com.ibm.websphere.jpa.entitymanager.poolcapacity";

    // ********** Persistence provider related constants
    static final String PROVIDER_SPI_META_INF_RESOURCE_NAME = "META-INF/services/javax.persistence.spi.PersistenceProvider";

    public static final String PERSISTENCE_XML_RESOURCE_NAME = "META-INF/persistence.xml";

    // Default JPA EntityManager pool capacity
    static final int DEFAULT_EM_POOL_CAPACITY = 10;

    // ********** MetaData constants             //d496032.1
    public static final int EJB_MODULE_VERSION_3_0 = 30;
    // wsjpa properties
    static final String WSOPENJPA_PREFIX = "wsjpa";
    static final String WSOPENJPA_EMF_POOL_PROPERTY_NAME = WSOPENJPA_PREFIX + "." + "PooledFactory";

    /**
     * This property allows the user to explicitly exclude applications from JPA processing. This
     * is useful to legacy applications using hibernate. Currently there is a performance issue
     * in hibernate that affects application start time when hibernate code is triggered by jeeruntime
     * code to create an EntityManagerFactory when the application contains a persistence.xml
     * file. This code is not needed to run for legacy Hibernate-related applications that do not
     * use openJPA or other JPA provider. <p> //PM20625
     * 
     * <B>Value:</B> com.ibm.websphere.persistence.ApplicationsExcludedFromJpaProcessing <p>
     * 
     * <B>Usage:</B> Optional JEERuntime Property <p>
     * 
     * <B>Property values:</B>
     * appName1:appName2:appName3...or * for all applications <p>
     **/
    public static final String EXCLUDE_JPA_PROCESSING_FOR = "com.ibm.websphere.persistence.ApplicationsExcludedFromJpaProcessing"; //PM20625

    /**
     * This property allows the user to disable EntityManagerFactory proxies for
     * non stateful session beans. This is useful for applications that need to
     * directly cast to vendor-specific EntityManagerFactory interfaces and that
     * cannot be changed to use JPAEMFactory.unwrap. <p>
     * 
     * <B>Value:</B> com.ibm.websphere.persistence.useEntityManagerFactoryProxy <p>
     * 
     * <B>Property values:</B> "true" (default) and "false"
     */
//   public static final String USE_EMF_PROXY = JPAJndiLookupObjectFactory.USE_EMF_PROXY; // d706751

}
