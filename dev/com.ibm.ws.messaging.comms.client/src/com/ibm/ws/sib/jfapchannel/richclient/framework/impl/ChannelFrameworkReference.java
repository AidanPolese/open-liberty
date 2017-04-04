/*
 * @start_prolog@
 * Version: @(#) 1.56.1.13 SIB/ws/code/sib.jfapchannel.client.rich.impl/src/com/ibm/ws/sib/jfapchannel/framework/impl/ChannelFrameworkReference.java, SIB.comms, WASX.SIB, uu1215.01 09/08/14 10:19:33 [4/12/12 22:14:17]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 *
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 Copyright IBM Corp. 2003, 2009
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * @end_prolog@
 *
 * Change activity:
 *
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * F177053         030922 prestona Rebase JFAP Channel on pre-M4 CF + TCP
 * F178002         030929 prestona Discover CF + TCP Channel as services
 * F178499         031002 prestona CF not being started in Client Container workaround.
 * D181601         031031 prestona Improve quality of JFAP Channel RAS
 * F188491         030128 prestona Migrate to M6 CF + TCP Channel
 * F189351         040203 prestona CF admin support
 * F196678.10      040413 prestona JS Client Administration
 * D202512         040511 prestona EXCP_DURING_INIT_SICJ0002 at client connect
 * D200811         040608 mattheg  Use WsChannelFrameworkImpl instead of ChannelFrameworkImpl
 * D219752         040728 prestona SSL channel properties not picked up correctly.
 * D221079         040803 prestona Find service using services namespace.
 * D197042         040811 prestona FFDC entries
 * D223632         040812 prestona Defend against errors during initialisiation
 * D224844         040817 mattheg  Ensure thread pool name is added correctly to property bag
 * D226214         040823 prestona Retry listen on initial bind failure.
 * D229522         040906 prestona Remove programatic chain creation
 * D232185         041007 mattheg  Serviceability improvements
 * D243616         041109 prestona Z4FVT: TCP Channel SIB_TCP_JFAP initialization failed
 * D244677         041122 prestona ZOS: Client applications cannot find messaging engine.
 * F244595         041129 prestona z/OS: TCP Proxy Bridge Support
 * D247547         041214 prestona z/OS: Attempted to create outbound bridge chains before CF lookup
 * D249911         050113 prestona z/OS: Wrong insert in (wrong) message output.
 * D255410         050217 prestona z/OS: TCP Proxy Bridge Support needs additional time to start
 * D256624         050220 mattheg  z/OS: Start right chain in z/OS startup thread.
 * F255130         050225 prestona Allow SSL properties to be overridden.
 * LIDB3187-28     050610 prestona Switched over the threadpool implementation used.
 * D249918         050721 mattheg  Add new NLS messages
 * D303323         050907 prestona SSL channel changes in SERV1
 * D311987         051101 prestona Delay "no SSL properties file" error until SSL chain usage
 * D244448         051114 prestona Allow client container to run without access to server JNDI
 * D323350         051114 prestona Failure loading properties when Java 2 security enabled
 * D329817         051207 mattheg  FFDC Failures
 * D336816         060127 prestona Distinguish between thin client and client container on z/OS
 * D350007         060228 prestona Application client doesn't pick up SSL properties
 * D360048         060404 mattheg  Use ThreadPoolRepository to create SIBFapThreadPool
 * D363760         060419 mattheg  Add trace suppression to the SSL property file missing warning
 * D341600         060810 prestona Fix Java 5 compiler warnings
 * SIB0048b.com.5  060913 mattheg  JFap channel for Portly client rework
 * SIB0048b.uti.1  060922 mattheg  Modifications to isThinClient()
 * SIB0134.com.1   070302 prestona Security tidy-up (start only permitted chains)
 * D435006         070502 prestona Handle encoded passwords
 * D453961         070725 mleming  Throw an SIErrorException if we fail to locate CFW in client
 * SIB0100.comms.1 070801 mleming  Allow TCP Proxy Bridge Service to be started separately
 * D458453         070810 sibcopyr Automatic update of trace guards
 * SIB0100.wmq.3   070816 mleming  Allow WMQRA to make use of TCP Proxy Bridge
 * 488180          071208 prestona SSL configuration not properly loaded in client container.
 * PK62789         080318 pbroad   Use ChannelFrameworkFactory.getChannelFramework() in standalone environment
 * 513302          080418 mleming  Pass in the correct alias key to SSLChannel
 * 500584.1        080418 vaughton Start/Stop channels when channel policy changes
 * 528455          080610 djvines  Fix up FFDC calls
 * 531881          080625 mleming  Retry starting chains on retryable failures
 * 581917          090415 mleming  Start up chains when bus added dynamically
 * 604938          090812 djvines  Don't add passwords to trace (or ffdc)
 * ============================================================================
 */
package com.ibm.ws.sib.jfapchannel.richclient.framework.impl;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import com.ibm.ejs.ras.TraceNLS;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.ws.sib.jfapchannel.JFapChannelConstants;
import com.ibm.ws.sib.jfapchannel.impl.CommsClientServiceFacade;
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.wsspi.channelfw.ChannelFramework;
import com.ibm.wsspi.channelfw.exception.ChannelException;

/**
 * Encapsulates obtaining and holding a reference to the Channel Framework.
 */
public class ChannelFrameworkReference
{
   private static final TraceComponent tc = SibTr.register(ChannelFrameworkReference.class, JFapChannelConstants.MSG_GROUP, JFapChannelConstants.MSG_BUNDLE);
   private static final TraceNLS nls = TraceNLS.getTraceNLS(JFapChannelConstants.MSG_BUNDLE);

   //Used for outputting a comms message as a result of 581917.
   //Need to be careful about circular dependencies, hence using string version of the resource bundle name.
   private static final TraceComponent commsTc = SibTr.register(CommsTr.class, com.ibm.ws.sib.utils.TraceGroups.TRGRP_COMMS, "com.ibm.ws.sib.comms.CWSICMessages");
   private static final class CommsTr {};

   //@start_class_string_prolog@
   public static final String $sccsid = "@(#) 1.56.1.13 SIB/ws/code/sib.jfapchannel.client.rich.impl/src/com/ibm/ws/sib/jfapchannel/framework/impl/ChannelFrameworkReference.java, SIB.comms, WASX.SIB, uu1215.01 09/08/14 10:19:33 [4/12/12 22:14:17]";
   //@end_class_string_prolog@

   static {
     if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "Source Info: " + $sccsid);
   }

   /** Flag to indicate if the outbound chains have been created without an SSL properties file */
   private static boolean outboundChainDefinitionsCreatedWithoutSSLPropertiesFile = false;

   /** The singleton instance of the actual channel framework */
   private static ChannelFramework framework;

   /** Flag to indicate whether the CFW ref is cached */
   private static boolean cached = false;

   /** sib.ssl.client.props property that controls how client properties are loaded */
   private final static String SIB_SSL_CONFIG_SOURCE = "com.ibm.ws.sib.configurationSource";

   /** Key for use when passing in an alias name to the SSLChannel. */
   private static final String ALIAS_KEY = "alias";

   // All valid SSL properties that the user may specify from the command line.
   private static final String SUPPORTED_SSL_PROPS[] =
   {
//      com.ibm.ws.ssl.core.Constants.SYSTEM_SSLPROP_KEY_STORE,
//      com.ibm.ws.ssl.core.Constants.SYSTEM_SSLPROP_KEY_STORE_PASSWORD,
//      com.ibm.ws.ssl.core.Constants.SYSTEM_SSLPROP_KEY_STORE_PROVIDER,
//      com.ibm.ws.ssl.core.Constants.SYSTEM_SSLPROP_KEY_STORE_TYPE,
//      com.ibm.ws.ssl.core.Constants.SYSTEM_SSLPROP_TRUST_STORE,
//      com.ibm.ws.ssl.core.Constants.SYSTEM_SSLPROP_TRUST_STORE_PASSWORD,
//      com.ibm.ws.ssl.core.Constants.SYSTEM_SSLPROP_TRUST_STORE_PROVIDER,
//      com.ibm.ws.ssl.core.Constants.SYSTEM_SSLPROP_TRUST_STORE_TYPE,
//
//      com.ibm.ws.ssl.core.Constants.SSLPROP_ALIAS,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_CLIENT_AUTHENTICATION,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_CLIENT_AUTHENTICATION_SUPPORTED,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_CONFIGURL_LOADED_FROM,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_CONTEXT_PROVIDER,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_CUSTOM_KEY_MANAGER,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_CUSTOM_TRUST_MANAGERS,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_DEFAULT_ALIAS,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_DYNAMIC_SELECTION_INFO,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_ENABLED_CIPHERS,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_EXCHANGE_SIGNER_PROMPT,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_EXPIRED_WARNING,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_KEY_MANAGER,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_KEY_STORE,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_KEY_STORE_CLIENT_ALIAS,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_KEY_STORE_CREATE_CMS_STASH,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_KEY_STORE_CUSTOM_CLASS,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_KEY_STORE_FILE_BASED,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_KEY_STORE_HOST_LIST,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_KEY_STORE_INITIALIZE_AT_STARTUP,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_KEY_STORE_MGMT_SCOPE,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_KEY_STORE_NAME,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_KEY_STORE_PASSWORD,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_KEY_STORE_PROVIDER,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_KEY_STORE_READ_ONLY,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_KEY_STORE_SERVER_ALIAS,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_KEY_STORE_SLOT,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_KEY_STORE_TYPE,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_KEY_STORE_USE_FOR_ACCELERATION,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_PROTOCOL,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_SECURITY_LEVEL,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_SSLTYPE,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_TOKEN_ENABLED,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_TOKEN_LIBRARY,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_TOKEN_PASSWORD,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_TOKEN_SLOT,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_TOKEN_TYPE,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_TRUST_MANAGER,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_TRUST_STORE,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_TRUST_STORE_CREATE_CMS_STASH,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_TRUST_STORE_CUSTOM_CLASS,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_TRUST_STORE_FILE_BASED,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_TRUST_STORE_HOST_LIST,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_TRUST_STORE_INITIALIZE_AT_STARTUP,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_TRUST_STORE_MGMT_SCOPE,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_TRUST_STORE_NAME,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_TRUST_STORE_PASSWORD,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_TRUST_STORE_PROVIDER,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_TRUST_STORE_READ_ONLY,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_TRUST_STORE_SLOT,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_TRUST_STORE_TYPE,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_URL_HOSTNAME_VERIFICATION,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_USE_INHERITABLE_THREAD_LOCAL,
//      com.ibm.ws.ssl.core.Constants.SSLPROP_VALIDATION_ENABLED,
//
//      com.ibm.ws.ssl.core.Constants.CONNECTION_INFO_DIRECTION,
//      com.ibm.ws.ssl.core.Constants.CONNECTION_INFO_ENDPOINT_NAME,
//      com.ibm.ws.ssl.core.Constants.CONNECTION_INFO_REMOTE_HOST,
//      com.ibm.ws.ssl.core.Constants.CONNECTION_INFO_REMOTE_PORT,
//
//      com.ibm.ws.ssl.core.Constants.IBMJCE,
//      com.ibm.ws.ssl.core.Constants.IBMJCE_NAME,
//      com.ibm.ws.ssl.core.Constants.IBMJCEFIPS,
//      com.ibm.ws.ssl.core.Constants.IBMJCEFIPS_NAME,
//      com.ibm.ws.ssl.core.Constants.IBMJSSE2,
//      com.ibm.ws.ssl.core.Constants.IBMJSSE2_NAME,
//
//      com.ibm.ws.ssl.core.Constants.USE_FIPS,
//      com.ibm.ws.ssl.core.Constants.FIPS_ENABLED,
//      com.ibm.ws.ssl.core.Constants.FIPS_JCEPROVIDERS,
//      com.ibm.ws.ssl.core.Constants.FIPS_JSSEPROVIDERS,

      SIB_SSL_CONFIG_SOURCE
   };

   /**
    * This flag is set to true if the TCP Proxy Bridge Service has been intialised.
    * Callers should only read/write this variable when they hold the ChannelFrameworkReference monitor.
    */
   private static boolean tcpBridgeServiceProxyInitialised = false;

   /**
    * Lock object, used to prevent multiple threads trying to start the TCP Proxy Bridge Service Inbound Chain at the same time.
    */
   private static final Object tcpProxyBridgeInboundLock = new Object();

   /**
    * Whether the TCP Proxy Bridge Service Inbound Chain has been started or not. This variable should only be read when holding the
    * tcpProxyBridgeInboundLock.
    */
   private static boolean tcpProxyBridgeInboundChainStarted = false;

   /**
    * Keep track of what chains have been started. We track started chains so that when we received notification of a change in the
    * permitted channel policy we can work out whether we now need to start or stop chains.
    */
   private static final Set<String> startedChains = Collections.synchronizedSet(new HashSet<String>());

   /**
    * The time interval in milliseconds between a chain failing to start with a RetryableChannelException, and the next attempt to start it.
    * Currently this is set to ten seconds.
    */
   private static final int CHAIN_RETRY_INTERVAL = 10000;

   /**
    * Whether we have output the SERVER_STARTED_SICO2001 message or not. Used so that we don't output it repeatedly.
    * Using AtomicBoolean as will need to do this on multiple threads and don't want to add in synchronization.
    */
   private static final AtomicBoolean hasListeningMessageBeenOutput = new AtomicBoolean(false);

   
   /*
   * @return Returns the singleton instance of the channel framework.
   */
    public static synchronized ChannelFramework getInstance() {
       return CommsClientServiceFacade.getChannelFramewrok();
    }
    
    public static boolean isOutboundSSLChainDefinedWithoutProperties()
    {
       return outboundChainDefinitionsCreatedWithoutSSLPropertiesFile;
    }
         

   /**
    * Wrapper for Class.forName(String).  Returns null rather than throwing a class not foun
    * exception.
    * @param name The name of the class to attempt to locate
    * @return The class or null if the class cannot be found.
    */
   private static Class getClassForName(String name)
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(tc, "getClassForName", name);
      Class returnClass = null;
      try
      {
         returnClass = Class.forName(name);
      }
      catch(ClassNotFoundException cnfe)
      {
         FFDCFilter.processException(cnfe, "com.ibm.ws.sib.jfapchannel.framework.impl.ChannelFrameworkReference.getClassForName",
                                     JFapChannelConstants.CHFWREF_GETCLASSFORNAME_01);
         if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) SibTr.exception(tc, cnfe);
      }
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(tc, "getClassForName", returnClass);
      return returnClass;
   }

   /**
    * Loads the SSL properties file, used when determining client SSL setup.
    * @return Properties The property map loaded from the SSL properties file.
    */
   public static Properties loadSSLProperties()                   // D219752
   {
      return null;
   }

   /**
    * Determines what properties to use for SSL settings based on the presence of
    * the com.ibm.ws.sib.configurationSource property.  If this property is present, it can
    * select between 'alias' and 'file'.  If the property is not present or is malformed
    * then a value of 'alias' is assumed.
    * <p>
    * When set to a value of 'alias' - the use of the com.ibm.ssl.alias properties is
    * assumed.  This property should name an SSL repertoire to use.
    * <p>
    * When set to a value of 'file' - the other contents of the properties file are
    * assumed to contain the settings to use for SSL.
    *
    * @param properties  the properties to perform reconciliation on.
    */
   private static void reconcileConfigurationSource(Properties properties)
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(tc, "reconcileConfigurationSource",properties);
      final String configurationSourceValue = properties.getProperty(SIB_SSL_CONFIG_SOURCE);
      final boolean useAlias;

      if (configurationSourceValue == null)
      {
         if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "No configurationSource property present, using alias");
         useAlias = true;
      }
      else if (configurationSourceValue.equalsIgnoreCase("alias"))
      {
         if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "configurationSource property specified as alias");
         useAlias = true;
      }
      else if (configurationSourceValue.equalsIgnoreCase("file"))
      {
         if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "configurationSource property specified as file");
         useAlias = false;
      }
      else
      {
         if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            SibTr.debug(tc, "An unknown configurationSource property value was specified ("+
                            configurationSourceValue+"), assuming a value of alias");
         useAlias = true;
      }

//      if (useAlias)
//      {
//         final String aliasValue = properties.getProperty(com.ibm.ws.ssl.core.Constants.SSLPROP_ALIAS);
//         properties.clear();
//         if (aliasValue != null)
//         {
//            //The SSLChannel expects the key to be alias NOT com.ibm.ssl.alias.
//            properties.put(ALIAS_KEY, aliasValue);
//         }
//      }
//      else
//      {
//         properties.remove(com.ibm.ws.ssl.core.Constants.SSLPROP_ALIAS);
//         properties.remove(SIB_SSL_CONFIG_SOURCE);
//      }

      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(tc, "reconcileConfigurationSource");
   }

   /**
    * Expands "WASROOT" tokens in property file values into the root directory
    * of the current profile.
    *
    * @param properties  the properties to expand "WASROOT" tokens in.
    */
   private static void expandWsRootVariables(Properties properties)
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(tc, "expandWsRootVariables", properties);
      String installRoot = null;

      final Enumeration keysEnum = properties.keys();
      while(keysEnum.hasMoreElements())
      {
         final String key = (String)keysEnum.nextElement();
         final String value = properties.getProperty(key);
         final String tokenToReplace;

         if (value.contains("$(WASROOT)")) tokenToReplace = "$(WASROOT)";
         else if (value.contains("${WASROOT}")) tokenToReplace = "${WASROOT}";
         else tokenToReplace = null;

         if (tokenToReplace != null)
         {
            if (installRoot == null)
            {
               installRoot =
                  AccessController.doPrivileged
                  (
                        new PrivilegedAction<String>()
                        {
                           public String run()
                           {
                              return System.getProperty("user.install.root");
                           }
                        }
                  );
            }

            if (installRoot != null)
            {
               final String newValue = value.replace(tokenToReplace, installRoot);
               if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "replacing: "+key+"->"+value+" with: "+key+"->"+newValue);
               properties.put(key, newValue);
            }
            else
            {
               if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "Couldn't get user.install.root property");
            }
         }
      }
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(tc, "expandWsRootVariables");
   }


   /**
    * Helper method that adds a channel definition to the Channel Framework.  It
    * returns any exception throw, rather than throwing it.  This makes the code which
    * programmatically creates outbound channel chains a bit neater.
    * @param name The name of the channel to add.
    * @param factory The factory class for the channel.
    * @param properties Properties for configuring the channel.
    * @return A channel exception if one is thrown, otherwise null.
    */
   private static ChannelException addChannel(String name, Class factory, Map properties)
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(tc, "addChannel", new Object[] {name, factory, properties});
      ChannelException channelException = null;
      try
      {
         framework.addChannel(name, factory, properties);
      }
      catch(ChannelException thrownException)
      {
         // No FFDC code needed
         channelException = thrownException;
      }
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(tc, "addChannel", channelException);
      return channelException;
   }

   /**
    * Creates outbound chains used for client environment bootstrap process.
    */
   private static void createOutboundChains()
   {
	   
   }

   /**
    * Start all permitted inbound chains.
    * <p>
    * Note: method is marked protected to permit unit test access.
    */
   protected static void startInboundChains () {}

   /**
    * This method reconciles the current started/stopped state of each inbound chain with the state of the chain
    * as allowed by the security isPermitted() method. Inbound chains that are currently stopped which are permitted
    * will be started, chains which are currently started which are not longer permitted will be stopped.
    */
   public static void reconcileInboundChains (final boolean update) {}

   
   
}
