// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2008
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
//
//  CHANGE HISTORY
// Defect       Date        Modified By     Description
//--------------------------------------------------------------------------------------
//PK15276       01/03/06    mmolden         INCORRECT FILTER MAPPING: WAS 6.0.2 VIOLATES THE SERVLET    WAS.webcontainer
//PK16370       01/04/06    mmolden         Log error statements when there is a failure to load listeners
//PK16542       01/05/06    mmolden         SERVLET ERROR - AXIXSERVLET WHEN RUNNING DIFFERENT AXIS VERSION
//337819        01/12/06    todkap          static request not invoking Dynacache when policy is defined    WAS.webcontainer
//340680        01/24/06    mmolden         content type fails when static resource has path element
//341303        01/25/06    mmolden         Change WebContainer APIs to allow modification of ServletConfig
//PK17371       02/03/06    ekoonce         CLASSLOADER REFERENCE OBTAINED FROM THE WEBMODULE IS NOT NULLED
//PK19394       02/28/06    todkap          WEBAPP DOES NOT RELEASE SESSION CONTEXT AFTER WEBAPP DESTROY.    WAS.webcontainer 
//PK18713       03/17/06    ekoonce         STARTED THE APPLICATION AND I SEE THE FOLLOWING ERROR MESSAGE    
//PK18917       04/13/06    ekoonce         CHANGE OUTPUT LEVEL FOR ADDDYNAMICSERVLET MESSAGES
//PK21127	04/17/06    mmolden         response.sendError(404) call after request.setCharacterEncoding causes UnsupportedEncodingException: JISAutoDetect
//PK23428       04/19/06    ekoonce         sendError() throws IllegalStateException
//370167        06/23/06    mmolden         61FVT:Unable to active Mbean, InstanceAlreadyExistsException    WAS.webcontainer
//PK27660       07/14/06    ekoonce         SERVLETCONTEXTLISTENER.CONTEXTDESTROYED(SERVLETCONTEXTEVENT)
//377689.1      07/19/06    mmolden         6101FVT: ServletConfigGetServletContextTest failing in watchDog
//PK27974       08/21/06    cjhoward        WAS V6 HAS DIFFERENT BEHAVIOUR FOR TRAILING "/" IN URI
//PK27620       08/21/06    cjhoward        SERVLET FILTER IS NOT CALLED IN V6 FOR URL RESOURCES WHEN THESE ARE NOT FOUND.  IN V5, THE FILTER IS ALWAYS CALLED
//PK27027       08/22/06    ekoonce         ConcurrentModificationException thrown from attributes HashMap
// 390332       09/14/06    mmolden         CTS:<jsp-property-gruop> tag not resolved for a particular URL    
//398349        10/17/06    ekoonce         CTS: Incorrect Minor Version returned by server
//PK34418       11/30/06    goff1           THE HTTPSESSIONATTRIBUTELISTENER CLASS DOES NOT LOAD WHEN THE
//406426        12/04/06    ekoonce         untranslated error message logging in to admin console
//PK33511       01/11/07    mmolden  	    Upgrade error message info in intializeTargetMappings
//LIDB3292-32.2 03/20/2007  cjhoward        WASX injection engine integration
//428887        03/27/07    cjhoward        Need to create some ext. procs. before populateJavaNameSpace()
//430016        04/09/07    cjhoward        BVT:RTF: AdminConsoleSSL --  500 Internal Error
//PK31450       04/20/07    mmolden         WHEN THERE ARE MULTIPLE SERVANT REGIONS PER CONTROL REGION
//PK37449       04/26/07    ekoonce         A THREAD DEADLOCK MAY OCCUR
//PK37608       04/27/07    mmolden         Suppress/Include $WSEP header in error reposne based on custom property and header in request
//434577        05/04/07    cjhoward        70FVT: PostConstruct and PreDestroy methods not called
//LIDB4336-35   09/25/07    mmolden         Remove mime filtering
// 461383       09/28/07    mmolden         70FVT: Async should still work when ARD is disabled 
//PK50133       10/05/07    sartoris        JSPExtensionClassLoader objects causing OOM
//486204        12/07/07    mmolden         Check version number before executing new getSession path
//PK55149       12/07/07    mmolden         Add custom property to allow error exception-type preceding status-code
//PK55330       12/07/07    mmolden         SOAP ADDRESS LOCATION ATTRIBUTE OF THE WSDL FOR THE PARTICULAR 
//501767        02/29/08    mmolden         FVT70:Static objects are not getting cached codelevl: DD808.25                                                                                        
//505048        03/17/08    cjhoward        70FVT RTFb: Destroy not happening for RI
//519410        05/14/08    mmolden         SVT:unexpected InvalidPortletWindowIdentifierException
//PK63920       05/19/08    mmolden(srpeters)        SRVE0058E SEEN WHEN CUSTOMER TRIES TO STOP PORTLET APPLICATION
//PK64290       05/20/08    mmolden         SESSION LOSS WHEN USING ONLY URLREWRITING
//521208        05/27/08    cjhoward        PERF: Optimizations to notifyServlet methods in WebAppImpl
//PK64421       05/28/08    mmolden         MESSAGES SRVE0180I/SRVE0181I WITH STACK TRACES SHOULD NOT APPEA
//PK67022       06/09/08    mmulholl        Suppress stack from message added by PK57136
//PK74092       11/10/08    mmolden         LOAD-ON-STARTUP INDICATION FOR SIPLETS IS NOT LOADING THE                                                                                                                                                                                              
//PK76142       12/05/08    mmulholl        getMimeType throws StringIndexOutOfBoundsException if passed empty string
//PK77421       12/12/08    anupag          Add "!" to SUPPRESS_HTML_RECURSIVE_ERROR_OUTPUT custom property
//PK79894       03/18/09    anupag          Add path elements(servletPath and pathInfo) to j_security_check and ibm_security_logout
// 569469       03/19.09    mmulholl        Improve trace
//PK79143       02/25/09    mmulholl        sendRedirect always causes chunked response
//PK82657		03/25/09	mconcini		JSPClassLoaderLimit does not include forwards and includes
//PK83345		04/19/09	anupag			Load-on-startup for servlet cause NPE on injected objects
//PK97815       10/06/09    anupag          Do not put Servlet,Filter in Service After Null Injection, defect 596191
//PM03788       01/12/10    anupag          Provide option for sending HTML formatted message in sendError.
//PM18512	    11/02/10    mmulholl(anupag) Encode the servletName while prinitng Error info.
//PM21451       11/03/11    mmulholl        getRealPath changes +
//F011107  		05/18/11	pmdinh			FIS not "always" trigger login process for URL that contains j_security_check
//

package com.ibm.ws.webcontainer.webapp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RunAs;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.MultipartConfigElement;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestAttributeEvent;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.ServletResponse;
import javax.servlet.ServletSecurityElement;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.descriptor.JspConfigDescriptor;
import javax.servlet.descriptor.JspPropertyGroupDescriptor;
import javax.servlet.descriptor.TaglibDescriptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.ibm.ejs.ras.TraceNLS;
import com.ibm.websphere.servlet.error.ServletErrorReport;
import com.ibm.websphere.servlet.event.ApplicationEvent;
import com.ibm.websphere.servlet.event.ApplicationListener;
import com.ibm.websphere.servlet.event.FilterErrorListener;
import com.ibm.websphere.servlet.event.FilterInvocationListener;
import com.ibm.websphere.servlet.event.FilterListener;
import com.ibm.websphere.servlet.event.ServletContextEventSource;
import com.ibm.websphere.servlet.event.ServletErrorEvent;
import com.ibm.websphere.servlet.event.ServletErrorListener;
import com.ibm.websphere.servlet.event.ServletInvocationListener;
import com.ibm.websphere.servlet.event.ServletListener;
import com.ibm.websphere.webcontainer.async.AsyncRequestDispatcher;
import com.ibm.ws.container.Container;
import com.ibm.ws.container.DeployedModule;
import com.ibm.ws.container.ErrorPage;
import com.ibm.ws.container.MimeFilter;
import com.ibm.ws.security.core.SecurityContext;
import com.ibm.ws.util.WSThreadLocal;
import com.ibm.ws.webcontainer.WebContainer;
import com.ibm.ws.webcontainer.async.AsyncIllegalStateException;
import com.ibm.ws.webcontainer.async.AsyncListenerEnum;
import com.ibm.ws.webcontainer.async.ListenerHelper;
import com.ibm.ws.webcontainer.async.ListenerHelper.CheckDispatching;
import com.ibm.ws.webcontainer.async.ListenerHelper.ExecuteNextRunnable;
import com.ibm.ws.webcontainer.core.BaseContainer;
import com.ibm.ws.webcontainer.core.Response;
import com.ibm.ws.webcontainer.exception.WebAppNotLoadedException;
import com.ibm.ws.webcontainer.exception.WebContainerException;
import com.ibm.ws.webcontainer.extension.DefaultExtensionProcessor;
import com.ibm.ws.webcontainer.extension.InvokerExtensionProcessor;
import com.ibm.ws.webcontainer.extension.WebExtensionProcessor;
import com.ibm.ws.webcontainer.filter.FilterConfig;
import com.ibm.ws.webcontainer.filter.FilterMapping;
import com.ibm.ws.webcontainer.filter.WebAppFilterManager;
import com.ibm.ws.webcontainer.metadata.JspConfigDescriptorImpl;
import com.ibm.ws.webcontainer.servlet.DefaultErrorReporter;
import com.ibm.ws.webcontainer.servlet.ServletConfig;
import com.ibm.ws.webcontainer.servlet.ServletWrapper;
import com.ibm.ws.webcontainer.servlet.exception.NoTargetForURIException;
import com.ibm.ws.webcontainer.session.IHttpSessionContext;
import com.ibm.ws.webcontainer.session.SessionManagerConfigBase;
import com.ibm.ws.webcontainer.spiadapter.collaborator.IInvocationCollaborator;
import com.ibm.ws.webcontainer.util.DocumentRootUtils;
import com.ibm.ws.webcontainer.util.EmptyEnumeration;
import com.ibm.ws.webcontainer.util.IteratorEnumerator;
import com.ibm.ws.webcontainer.util.MetaInfResourcesFileUtils;
import com.ibm.ws.webcontainer.util.UnsynchronizedStack;
import com.ibm.ws.webcontainer.util.ZipFileResource;
import com.ibm.wsspi.injectionengine.InjectionException;
import com.ibm.wsspi.webcontainer.ClosedConnectionException;
import com.ibm.wsspi.webcontainer.RequestProcessor;
import com.ibm.wsspi.webcontainer.WCCustomProperties;
import com.ibm.wsspi.webcontainer.WebContainerConstants;
import com.ibm.wsspi.webcontainer.WebContainerRequestState;
import com.ibm.wsspi.webcontainer.collaborator.CollaboratorHelper;
import com.ibm.wsspi.webcontainer.collaborator.CollaboratorInvocationEnum;
import com.ibm.wsspi.webcontainer.collaborator.ICollaboratorHelper;
import com.ibm.wsspi.webcontainer.collaborator.IWebAppNameSpaceCollaborator;
import com.ibm.wsspi.webcontainer.collaborator.IWebAppTransactionCollaborator;
import com.ibm.wsspi.webcontainer.collaborator.TxCollaboratorConfig;
import com.ibm.wsspi.webcontainer.extension.ExtensionFactory;
import com.ibm.wsspi.webcontainer.extension.ExtensionProcessor;
import com.ibm.wsspi.webcontainer.facade.ServletContextFacade;
import com.ibm.wsspi.webcontainer.filter.IFilterConfig;
import com.ibm.wsspi.webcontainer.filter.IFilterMapping;
import com.ibm.wsspi.webcontainer.logging.LoggerFactory;
import com.ibm.wsspi.webcontainer.logging.LoggerHelper;
import com.ibm.wsspi.webcontainer.metadata.WebModuleMetaData;
import com.ibm.wsspi.webcontainer.servlet.IExtendedRequest;
import com.ibm.wsspi.webcontainer.servlet.IServletConfig;
import com.ibm.wsspi.webcontainer.servlet.IServletContext;
import com.ibm.wsspi.webcontainer.servlet.IServletWrapper;
import com.ibm.wsspi.webcontainer.util.EncodingUtils;
import com.ibm.wsspi.webcontainer.util.ServletUtil;
import com.ibm.wsspi.webcontainer.util.ThreadContextHelper;
import com.ibm.wsspi.webcontainer.util.URIMapper;
import com.ibm.wsspi.webcontainer.webapp.WebAppConfig;

/**
 * @author mmolden
 */
@SuppressWarnings("unchecked")
public abstract class WebApp extends BaseContainer implements ServletContext, IServletContext {

    protected ExtensionProcessor loginProcessor = null;
    protected ExtensionProcessor logoutProcessor = null;
    protected ICollaboratorHelper collabHelper;
    private static WSThreadLocal envObject = new WSThreadLocal();
    protected List<String> orderedLibPaths = null;

    private final static com.ibm.websphere.security.WebSphereRuntimePermission perm = new com.ibm.websphere.security.WebSphereRuntimePermission(
            "accessServletContext");

    public static String WELCOME_FILE_LIST = "com.ibm.ws.webcontainer.config.WelcomeFileList";

    public static final String DIR_BROWSING_MAPPING = "__dirBrowsing__" + System.currentTimeMillis();

    public static final String FILTER_PROXY_MAPPING = "/__filterProxy__" + System.currentTimeMillis(); // PK15276

    protected static Logger logger = LoggerFactory.getInstance().getLogger("com.ibm.ws.webcontainer.webapp");
    private static final String CLASS_NAME = "com.ibm.ws.webcontainer.webapp.WebApp";

    protected ClassLoader loader;

    // begin defect 293789: add ability for components to register
    // ServletContextFactories
    protected ServletContext facade = null;
    protected List<WebContainerConstants.Feature> features = new ArrayList<WebContainerConstants.Feature>();
    // private boolean isWarnedOfAsyncDisablement=false;
    // end defect 293789: add ability for components to register
    // ServletContextFactories

    // need to call preInvoke() on this before any control
    // goes to the user application.
    //
    protected String applicationName;
    protected WebAppConfiguration config;
    protected WebExtensionProcessor webExtensionProcessor;

    protected boolean production = true;

    protected boolean isServlet23;

    protected String contextPath;

    protected String[][] internalServletList = { { "DirectoryBrowsingServlet", "com.ibm.ws.webcontainer.servlet.DirectoryBrowsingServlet" },
            { "SimpleFileServlet", "com.ibm.ws.webcontainer.servlet.SimpleFileServlet" },

    };

    protected final String BY_NAME_ONLY = "/_" + System.currentTimeMillis() + "_/";

    protected WebAppFilterManager filterManager;

    protected String documentRoot;
    
    public static final String SERVLET_API_VERSION = "Servlet 2.5";
    	
    protected static int disableServletAuditLogging = -1;

    protected String serverInfo = null;

    protected IHttpSessionContext sessionCtx = null;

    protected WebAppEventSource eventSource = new WebAppEventSource();

    protected static TraceNLS nls = TraceNLS.getTraceNLS(WebApp.class, "com.ibm.ws.webcontainer.resources.Messages");
    protected static TraceNLS error_nls = TraceNLS.getTraceNLS(WebApp.class, "com.ibm.ws.webcontainer.resources.ErrorPage");

    protected ArrayList sessionListeners = new ArrayList(); // cmd PQ81253
    protected ArrayList sessionAttrListeners = new ArrayList(); // cmd PQ81253

    protected ArrayList addedSessionListeners = new ArrayList(); // 434577
    protected ArrayList addedSessionAttrListeners = new ArrayList(); // 434577

    protected ArrayList servletContextListeners = new ArrayList();
    protected ArrayList servletContextLAttrListeners = new ArrayList();
    protected ArrayList servletRequestListeners = new ArrayList();
    protected ArrayList servletRequestLAttrListeners = new ArrayList();
    private static boolean prependSlashToResource = false; // 263020
    private Boolean destroyed = Boolean.FALSE;// 325429
    protected IWebAppNameSpaceCollaborator webAppNameSpaceCollab;
    private IWebAppTransactionCollaborator txCollab;

    protected ArrayList sessionActivationListeners = new ArrayList();
    protected ArrayList sessionBindingListeners = new ArrayList();

    private String scratchdir = null;

    private int jspClassLoaderLimit = 0; // PK50133
    // PK82657 - protected LinkedList jspClassLoaders = new LinkedList();
    // //PK50133
    protected ArrayList jspClassLoaderExclusionList = null; // PK50133
    protected JSPClassLoadersMap jspClassLoadersMap = null; // PK82657
    protected boolean jspClassLoaderLimitTrackIF = false; // PK82657

    private int versionID;
    protected DefaultExtensionProcessor defaultExtProc = null;

    private Object lock = new Object();
    private boolean initialized = false;

    private static boolean redirectContextRoot = WCCustomProperties.REDIRECT_CONTEXT_ROOT;

    private static boolean errorExceptionTypeFirst = WCCustomProperties.ERROR_EXCEPTION_TYPE_FIRST;
    private List<IServletConfig> sortedServletConfigs;
    private int effectiveMajorVersion;
    private int effectiveMinorVersion;

    private boolean canAddServletContextListener=true; //Servlet30 addListenerbehavior
    protected boolean withinContextInitOfProgAddListener=false;
	private ClassLoader webInfLibClassloader;
	private Map<String, URL> metaInfCache;
	
	private final static boolean useMetaInfCache = (WCCustomProperties.META_INF_RESOURCES_CACHE_SIZE>0);
    
	//The following two JSF listener classes are used to make sure that the JSF ServletContextListener 
	//is fired before the CDI listener when we are using the JSF implementation shipped with WAS.
	private static final String SUN_CONFIGURE_LISTENER_CLASSNAME = "com.sun.faces.config.ConfigureListener";
	private static final String MYFACES_LIFECYCLE_LISTENER_CLASSNAME = "org.apache.myfaces.webapp.StartupServletContextListener";
	private static final String JSF_IMPL_ENABLED_PARAM = "com.ibm.ws.jsf.JSF_IMPL_ENABLED";
	private static final String JSF_IMPL_ENABLED_CUSTOM = "Custom";
	private static final String JSF_IMPL_ENABLED_NONE = "None";
	
	DocumentRootUtils staticDocRoot = null;
	DocumentRootUtils jspDocRoot = null;
    
    // PK37608 Start
    static {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
            logger.logp(Level.FINE, CLASS_NAME, "<init>", " : suppressWSEPHeader set to " + WCCustomProperties.SUPPRESS_WSEP_HEADER);
        }
        // Begin 263020
        if (WCCustomProperties.PREPEND_SLASH_TO_RESOURCE != null && WCCustomProperties.PREPEND_SLASH_TO_RESOURCE.equals("true")) {
            prependSlashToResource = true;
        }
        // End 263020
        
    }

    // PK37698 End
    public WebApp(String name, Container parent) {
        super(name, parent);
        // PK63920 Start
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
            logger.entering(CLASS_NAME, "<init> [ " + this + " ] with name -> [ " + name + " ] and parent [ " + parent + " ]");
        // PK63920 End
        this.requestMapper = new URIMapper(true);
        // attributes = new HashMap(); //PK27027
        this.attributes = Collections.synchronizedMap(new HashMap()); // PK27027
        
        if (useMetaInfCache){
        	//prevent rehash of the map by making initial capacity one greater than maximum size
        	//and loadFactor of 1. If the initial capacity is greater than the maximum number of 
        	//entries divided by the load factor, no rehash operations will ever occur.
        	//The removeEldestEntry will take effect when we're at the cache size + 1.
        	metaInfCache = new LinkedHashMap(WCCustomProperties.META_INF_RESOURCES_CACHE_SIZE+1, 1.0f, true) {
		        public boolean removeEldestEntry(Map.Entry eldest) {
		            return size() > WCCustomProperties.META_INF_RESOURCES_CACHE_SIZE;
		        }
		    };
        }

        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) { // 306998.15
            logger.exiting(CLASS_NAME, "<init> name --> " + name);
        }
    }

    // BEGIN: NEVER INVOKED BY WEBSPHERE APPLICATION SERVER (Common Component
    // Specific)
    public void initialize(WebAppConfiguration config, DeployedModule moduleConfig, // BEGIN:
            // NEVER
            // INVOKED
            // BY
            // WEBSPHERE
            // APPLICATION
            // SERVER
            // (Common
            // Component
            // Specific)
            List extensionFactories) throws ServletException, Throwable {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
            logger.entering(CLASS_NAME, "Initialize WebApp -> [ " + this + " ]");

        this.loader = moduleConfig.getClassLoader(); // NEVER INVOKED BY
        // WEBSPHERE APPLICATION
        // SERVER (Common Component
        // Specific)
        this.applicationName = moduleConfig.getName(); // NEVER INVOKED BY
        // WEBSPHERE APPLICATION
        // SERVER (Common
        // Component Specific)

        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
            logger.logp(Level.FINE, CLASS_NAME, "initialize", "Initializing application " + this.applicationName);
        // PK63920 End
        serverInfo = getServerInfo(); // NEVER INVOKED BY WEBSPHERE APPLICATION
        // SERVER (Common Component Specific)
        ClassLoader origClassLoader = null; // NEVER INVOKED BY WEBSPHERE
        // APPLICATION SERVER (Common
        // Component Specific)
        try {
            origClassLoader = ThreadContextHelper.getContextClassLoader(); // NEVER
            // INVOKED
            // BY
            // WEBSPHERE
            // APPLICATION
            // SERVER
            // (Common
            // Component
            // Specific)
            final ClassLoader warClassLoader = getClassLoader(); // NEVER
            // INVOKED BY
            // WEBSPHERE
            // APPLICATION
            // SERVER
            // (Common
            // Component
            // Specific)
            if (warClassLoader != origClassLoader) // NEVER INVOKED BY WEBSPHERE
            // APPLICATION SERVER (Common
            // Component Specific)
            {
                ThreadContextHelper.setClassLoader(warClassLoader); // NEVER
                // INVOKED
                // BY
                // WEBSPHERE
                // APPLICATION
                // SERVER
                // (Common
                // Component
                // Specific)
            } else {
                origClassLoader = null; // NEVER INVOKED BY WEBSPHERE
                // APPLICATION SERVER (Common Component
                // Specific)
            }

            commonInitializationStart(config, moduleConfig); // NEVER INVOKED BY
            // WEBSPHERE
            // APPLICATION
            // SERVER (Common
            // Component
            // Specific)
            webAppNameSpaceCollab.preInvoke(config.getMetaData().getCollaboratorComponentMetaData()); //added 661473
            commonInitializationFinish(extensionFactories); // NEVER INVOKED BY
            loadLifecycleListeners(); //added 661473
            // WEBSPHERE
            // APPLICATION
            // SERVER (Common
            // Component
            // Specific)
            try {
                //moved out of commonInitializationFinish
                notifyServletContextCreated();
            } catch (Throwable th) {
                // pk435011
                logger.logp(Level.SEVERE, CLASS_NAME, "initialize", "error.notifying.listeners.of.WebApp.start", new Object[] { th });
            }
            commonInitializationFinally(extensionFactories); // NEVER INVOKED BY
            webAppNameSpaceCollab.postInvoke(); //added 661473            
            // WEBSPHERE
            // APPLICATION
            // SERVER (Common
            // Component
            // Specific)
        } finally {
            if (origClassLoader != null) // NEVER INVOKED BY WEBSPHERE
            // APPLICATION SERVER (Common Component
            // Specific)
            {
                final ClassLoader fOrigClassLoader = origClassLoader; // NEVER
                // INVOKED
                // BY
                // WEBSPHERE
                // APPLICATION
                // SERVER
                // (Common
                // Component
                // Specific)

                ThreadContextHelper.setClassLoader(fOrigClassLoader); // NEVER
                // INVOKED
                // BY
                // WEBSPHERE
                // APPLICATION
                // SERVER
                // (Common
                // Component
                // Specific)
            }
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
                logger.exiting(CLASS_NAME, "initializeTargetMappings");
        }
        // PK63920 Start
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
            logger.exiting(CLASS_NAME, "Initialize WebApp -> [ " + this + " ] ApplicationName -> [ " + config.getApplicationName() + " ]");
        // PK63920 End
    }

    // END: NEVER INVOKED BY WEBSPHERE APPLICATION SERVER (Common Component
    // Specific)

    protected void commonInitializationFinish(List extensionFactories) {
        try {
            initializeExtensionProcessors(extensionFactories);
        } catch (Throwable th) {
            // pk435011
            logger.logp(Level.SEVERE, CLASS_NAME, "commonInitializationFinish", "error.initializing.extension.factories", new Object[] { th });
            
        }
        //We need to move this above createServletWrappers so that the correct facades are reference from getServletContext methods
        initializeServletContextFacades();
        try {
            createServletWrappers();
        } catch (Throwable th) {

            logger.logp(Level.SEVERE, CLASS_NAME, "commonInitializationFinish", "error.while.initializing.servlets", th);

        }
        
        initFilterConfigs();
        
        
        // End 309151, Undo Call-Order change of MetaDataListener and
        // ExtensionProcess
    }

    private void initFilterConfigs() {
		Iterator<IFilterConfig> filterInfos = this.config.getFilterInfos();
		while (filterInfos.hasNext()){
			filterInfos.next().setIServletContext(this);
		}
	}

	// PK83345 Start
    protected void commonInitializationFinally(List extensionFactories) {
        try {
            doLoadOnStartupActions();
        } catch (Throwable th) {
            // pk435011
            logger.logp(Level.SEVERE, CLASS_NAME, "commonInitializationFinally", "error.while.initializing.servlets", th);
            
        }
        try {
            initializeTargetMappings();
        } catch (Throwable th) {
            // pk435011
            logger.logp(Level.SEVERE, CLASS_NAME, "commonInitializationFinally", "error.while.initializing.target.mappings", th);
            
        }
        try {
            // initialize filter manager
            // keeping old implementation for now
            initializeFilterManager();
        } catch (Throwable th) {
            // pk435011
            logger.logp(Level.SEVERE, CLASS_NAME, "commonInitializationFinally", "error.initializing.filters", th);
            
        }

    }

    private void doLoadOnStartupActions() throws Exception {
        for (IServletConfig iServletConfig : this.sortedServletConfigs) {
            if (iServletConfig != null) {
                if (iServletConfig.getServletWrapper() != null) {
                    iServletConfig.getServletWrapper().loadOnStartupCheck();
                } else if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
                    logger.logp(Level.FINE, CLASS_NAME, "doLoadOnStartupActions", "servletWrapper for iServletConfig=>"
                            + iServletConfig.getServletName() + " is null");
                }

            } else if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
                logger.logp(Level.FINE, CLASS_NAME, "doLoadOnStartupActions", "iServletConfig is null");
            }
        }
    }

    // Begin 299205, Collaborator added in extension processor recieves no
    // events
    protected void commonInitializationStart(WebAppConfiguration config, DeployedModule moduleConfig) throws Throwable {
        // End 299205, Collaborator added in extension processor recieves no
        // events
        this.config = config;
        config.setWebApp(this);
        WebGroupConfiguration webGroupCfg = ((WebGroup) parent).getConfiguration();
        isServlet23 = webGroupCfg.isServlet2_3();
        versionID = webGroupCfg.getVersionID();
        effectiveMajorVersion = versionID / 10;
        effectiveMinorVersion = versionID % 10;
        
        collabHelper = createCollaboratorHelper(moduleConfig); // must happen
        // before
        // createSessionContext
        // which calls
        // startEnvSetup
        webAppNameSpaceCollab = collabHelper.getWebAppNameSpaceCollaborator();
       
        
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
            logger.logp(Level.FINE, CLASS_NAME, "commonInitializationStart", "servlet spec version -->" + versionID+
                    "effectiveMajorVersion->" + effectiveMajorVersion +
                    "effectiveMinorVersion->" + effectiveMinorVersion);
        }
        contextPath = ((WebGroup) parent).getConfiguration().getContextRoot();
        this.webExtensionProcessor = this.getWebExtensionProcessor();
        
        this.staticDocRoot = new DocumentRootUtils(this,this.config,DocumentRootUtils.STATIC_FILE);
        this.jspDocRoot = new DocumentRootUtils(this,this.config,DocumentRootUtils.JSP);
        
        webAppNameSpaceCollab.preInvoke(config.getMetaData().getCollaboratorComponentMetaData());
        loadWebAppAttributes();
        //loadLifecycleListeners();
        //since we have now removed clearing the listeners from within loadLifecycleListeners (due to when it is being called), 
        //we need to add this method to clear listeners now in case there was an error and the app gets updated
        clearLifecycleListeners();
        webAppNameSpaceCollab.postInvoke();

        registerGlobalWebAppListeners();
        txCollab = collabHelper.getWebAppTransactionCollaborator();
        createSessionContext(moduleConfig);
        eventSource
                .onApplicationStart(new ApplicationEvent(this, this, new com.ibm.ws.webcontainer.util.IteratorEnumerator(config.getServletNames())));
    }

    public abstract WebExtensionProcessor getWebExtensionProcessor();

    /**
     * Method createSessionContext.
     * 
     * @param moduleConfig
     */
    protected void createSessionContext(DeployedModule moduleConfig) throws Throwable {
        try {
            ArrayList sessionRelatedListeners[] = new ArrayList[] { sessionListeners, sessionAttrListeners }; // cmd
            // PQ81253
            this.sessionCtx = ((WebGroup) parent).getSessionContext(moduleConfig, this, sessionRelatedListeners); // cmd
            // PQ81253
        } catch (Throwable th) {
            // pk435011
            logger.logp(Level.SEVERE, CLASS_NAME, "createSessionContext", "error.obtaining.session.context", th);
            throw new WebAppNotLoadedException(th.getMessage());
        }

    }

    /**
     * Method initializeFilterManager.
     */
    protected void initializeFilterManager() {
        if (filterManager != null)
            return;

        filterManager = new WebAppFilterManager(config, this);

        filterManager.init();
    }

    /**
     * Method initializeTargetMappings.
     */
    protected void initializeTargetMappings() throws Exception {
        // NOTE: namespace preinvoke/postinvoke not necessary as the only
        // external
        // code being run is the servlet's init() and that is handled in the
        // ServletWrapper

        // check if an extensionFactory is present for *.jsp:
        // We do this by constructing an arbitrary mapping which
        // will only match the *.xxx extension pattern
        //
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
            logger.entering(CLASS_NAME, "initializeTargetMappings");

        initializeStaticFileHandler();

        initializeInvokerProcessor();

        if (config.isDirectoryBrowsingEnabled()) {
            try {
                IServletWrapper dirServlet = getServletWrapper("DirectoryBrowsingServlet");
                requestMapper.addMapping(DIR_BROWSING_MAPPING, dirServlet);
            } catch (WebContainerException wce) {
                // pk435011
                logger.logp(Level.WARNING, CLASS_NAME, "initializeTargetMappings", "mapping.for.directorybrowsingservlet.already.exists");

            } catch (Exception exc) {
                // pk435011
                logger.logp(Level.WARNING, CLASS_NAME, "initializeTargetMappings", "mapping.for.directorybrowsingservlet.already.exists");
            }
        }

    }

    /**
     * Method createServletWrappers.
     */
    protected void createServletWrappers() throws Exception {
        // NOTE: namespace preinvoke/postinvoke not necessary as the only
        // external
        // code being run is the servlet's init() and that is handled in the
        // ServletWrapper

        // check if an extensionFactory is present for *.jsp:
        // We do this by constructing an arbitrary mapping which
        // will only match the *.xxx extension pattern
        //
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
            logger.entering(CLASS_NAME, "createServletWrappers");

        WebExtensionProcessor jspProcessor = (WebExtensionProcessor) requestMapper.map("/dummyPath.jsp");

        if (jspProcessor == null) {
            // No extension processor present to handle this kind of
            // target. Hence warn, skip.
            // pk435011
            logger.logp(Level.WARNING, CLASS_NAME, "createServletWrappers", "no.jsp.extension.handler.found");
        }

        Iterator<IServletConfig> sortedServletConfigIterator = sortNamesByStartUpWeight(config.getServletInfos());
        Map<String, List<String>> mappings = config.getServletMappings();
        String path = null;
        IServletConfig servletConfig;
        IServletWrapper wrapper = null;
        while (sortedServletConfigIterator.hasNext()) {
            wrapper = null; // 248871: reset wrapper to null
            servletConfig = sortedServletConfigIterator.next();
            String servletName = servletConfig.getServletName();
            List<String> mapList = mappings.get(servletConfig.getServletName());
            servletConfig.setServletContext(this.getFacade());
            
            //Begin 650884
            //WARNING!!! We shouldn't map by name only as there is 
        	//no way to configure a security constraint
        	//for a dynamically added path.
            
            //Consolidate the code to setup a single entry map list when its mapped by name only
//            if (mapList==null){
//            	//WARNING!!! We shouldn't map by name only as there is 
//            	//no way to configure a security constraint
//            	//for a dynamically added path.
//				//Also, if there was nothing mapped to the servlet
//				//in web.xml, we would have never called WebAppConfiguration.addServletMapping
//				//which sets the mappings on sconfig. Adding the list directly to the hashMap short
//				//circuits that logic so future calls to addMapping on the ServletConfig wouldn't work
//				//unless there was at least one mapping in web.xml
//            	
//            	
//            	 // hardcode the path, since it had no mappings
//                String byNamePath = BY_NAME_ONLY + servletName;
//
//                // Add this to the config, because we will be looking at
//                // the mappings in order to get to the servlet through the
//                // mappings in the config.
//                mapList = new ArrayList<String>();
//                mapList.add(byNamePath);
//                mappings.put(servletName, mapList);
//            }
             //End 650884
            
            if (mapList==null||mapList.isEmpty()){
            	wrapper = jspAwareCreateServletWrapper(jspProcessor,
						servletConfig,  servletName);
            }
            else {
	            for (String urlPattern : mapList) {
	                path = urlPattern;
	
	                if (path == null) {
	                    // shouldn't happen since there is a mapping specified
	                    // but too bad the user can never hit the servlet
	                    // pk435011
	                	//Begin 650884
	                    logger.logp(Level.SEVERE, CLASS_NAME, "createServletWrappers", "illegal.servlet.mapping", servletName); // PK33511
	//                    path = "/" + BY_NAME_ONLY + "/" + servletName;
	                    //End 650884
	                } else if (path.equals("/")) {
	                    path = "/*";
	                }
	
	                if (wrapper == null) { // 248871: Check to see if we've
	                    // already found wrapper for
	                    // servletName
	
	                    
	                    wrapper = jspAwareCreateServletWrapper(jspProcessor,
								servletConfig,  servletName);
	                    
	                    if (wrapper==null)
	                    	continue;
	                    
	                }
	                try {
	                    // Begin:248871: Check to see if we found the wrapper
	                    // before adding
	                	if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
	                		logger.logp(Level.FINE,CLASS_NAME, "createServletWrappers"
	                        		,"determine whether to add mapping for path[{0}] wrapper[{1}] isEnabled[{2}]"
	                        		, new Object []{path,wrapper,servletConfig.isEnabled()});
	                    if (wrapper != null&&path!=null&&servletConfig.isEnabled()) {
	                        requestMapper.addMapping(path, wrapper);
	                    }
	                    // End:248871
	                } catch (Exception e) {
	                	//TODO: ???? extension processor used to call addMappingTarget after the wrappers had been added.
	                	//Now it is done before, and you can get this exception here in the case they call addMappingTarget
	                	//and add the mapping to the servletConfig because we'll try to add it again, but it will
	                	//already be mapped. You could add a list of paths to ignore and not try to add again. So any
	                	//path added via addMappingTarget will be recorded and addMapping can be skipped. It is preferrable
	                	//to just have them not call addMappingTarget any more instead of adding the extra check.
	                    // pk435011
	                    logger.logp(Level.WARNING, CLASS_NAME, "createServletWrappers", "error.while.adding.servlet.mapping.for.path", new Object[] {
	                            path, wrapper, getApplicationName() });
	                    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
	                        com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(e, CLASS_NAME + ".createServletWrappers", "455", this);
	                        // pk435011
	                        logger.logp(Level.SEVERE, CLASS_NAME, "createServletWrappers", "error.adding.servlet.mapping.for.servlet", new Object[] {
	                                servletName, getApplicationName(), e }); // PK33511
	                    }
	                }
	            }
            }
        
            
            servletConfig.setServletWrapper(wrapper); 
            
            this.initializeNonDDRepresentableAnnotation(servletConfig);
            // set the servlet wrapper on the
            // servlet config so
            // ServletConfig.addMapping
            // can put it in the
            // requestMapper
            
        }
    }

	private IServletWrapper jspAwareCreateServletWrapper(
			WebExtensionProcessor jspProcessor, IServletConfig servletConfig,String servletName) {
		IServletWrapper wrapper=null;
		if (!servletConfig.isJsp()) {
		    try {
		        wrapper = getServletWrapper(servletName);
		        // getServletWrapper does addWrapper itself so
		        // no need to invoke
		    } catch (Throwable t) {
		        // pk435011
		        logger.logp(Level.SEVERE, CLASS_NAME, "jspAwareCreateServletWrapper", "uncaught.init.exception.thrown.by.servlet",
		                new Object[] { servletName, getApplicationName(), t }); // PK33511
		        // t.printStackTrace(System.err); @283348D
		        // continue;
		    }
		} else {
		    try {
		        // its a JSP in Servlet clothing
		        if (jspProcessor != null) {
		            wrapper = jspProcessor.createServletWrapper(servletConfig);
		        } else {
		            // pk435011
		            logger.logp(Level.WARNING, CLASS_NAME, "jspAwareCreateServletWrapper", "jsp.processor.not.defined.skipping",
		                    servletConfig.getFileName());
		            
		        }
		    } catch (Throwable t) {
		        // pk435011
		        logger.logp(Level.SEVERE, CLASS_NAME, "jspAwareCreateServletWrapper", "error.while.initializing.jsp.as.servlet",
		                new Object[] { servletConfig.getFileName(), getApplicationName(), t }); // PK33511
		        // t.printStackTrace(System.err); @283348D
		    }
		}
		return wrapper;
	}

    private void initializeNonDDRepresentableAnnotation(IServletConfig servletConfig) {
		String className = servletConfig.getClassName();
		if (className!=null){
			try {
				Class clazz = Class.forName(className, false, this.getClassLoader());
				checkForServletSecurityAnnotation(clazz, servletConfig);
			} catch (ClassNotFoundException e) {
				if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
				{    
					logger.logp(Level.FINE,CLASS_NAME, "initializeNonDDRepresentableAnnotation"
                    		,"unable to load class [{0}] which is benign if the class is never used"
                    		, new Object []{className});
				}
			}
		}
	}

	protected void initializeStaticFileHandler() {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
            logger.entering(CLASS_NAME, "initializeStaticFileHandler");
        }
        // String nextPattern = null; // PK18713
        if (config.isFileServingEnabled()) {
            try {
                addStaticFilePatternMappings(null);
                // defect 220552: end defer URL mappings to
                // FileExtensionProcessor instead of hardcoding.
            } catch (Throwable exc) {
                com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(exc, CLASS_NAME + ".initializeStaticFileHandler", "542", this);
                // pk435011
                logger.logp(Level.SEVERE, CLASS_NAME, "initializeStaticFileHandler", "error.while.adding.static.file.processor", exc); /* 283348.1 */
            }
        }
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
            logger.exiting(CLASS_NAME, "initializeStaticFileHandler");
        }
    }

    protected void addStaticFilePatternMappings(RequestProcessor proxyReqProcessor) {
        String nextPattern;
        ExtensionProcessor fileExtensionProcessor = getDefaultExtensionProcessor(this, getConfiguration().getFileServingAttributes());

        List patternList = fileExtensionProcessor.getPatternList();
        Iterator patternIter = patternList.iterator();
        while (patternIter.hasNext()) {
            nextPattern = (String) patternIter.next(); // PK18713
            try {
                if (proxyReqProcessor == null)
                    requestMapper.addMapping(nextPattern, fileExtensionProcessor); // PK18713
                else
                    requestMapper.addMapping(nextPattern, proxyReqProcessor);
            } catch (Exception e) {
                // Mapping clash. Log warning
                // pk435011
                logger.logp(Level.WARNING, CLASS_NAME, "initializeStaticFileHandler", "error.adding.servlet.mapping.file.handler", nextPattern);
            }
        }
    }

    private void initializeInvokerProcessor() {
        if (config.isServeServletsByClassnameEnabled()) {
            // PK57136 - STARTS
            try {
                InvokerExtensionProcessor invokerExtensionProcessor = (InvokerExtensionProcessor) getInvokerExtensionProcessor(this);
                List patternList = invokerExtensionProcessor.getPatternList();
                Iterator patternIter = patternList.iterator();
                while (patternIter.hasNext()) {
                    try {
                        requestMapper.addMapping((String) patternIter.next(), invokerExtensionProcessor);
                    } catch (Throwable e) {
                        com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(e, CLASS_NAME + ".initializeInvokerProcessor", "671", this);
                        // PK67022 remove stack from error message
                        logger.logp(Level.SEVERE, CLASS_NAME, "initializeInvokerProcessor", nls.getString("error.initializing.extension.factories"));
                    }
                }
            } catch (Throwable th) {
                com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(th, CLASS_NAME + ".initializeInvokerProcessor", "678", this);
                logger.logp(Level.SEVERE, CLASS_NAME, "initializeInvokerProcessor", nls.getString("error.initializing.extension.factories"), th);
            }
            /*
             * try { requestMapper.addMapping("/servlet/*",
             * getInvokerExtensionProcessor(this)); } catch
             * (WebContainerException wce) { } catch (Exception exc) { //TODO:
             * // Mapping for /servlet/ already exists }
             */
            // PK57136 - ENDS
        }
    }

    /**
     * @param app
     * @return
     */
    protected abstract InvokerExtensionProcessor getInvokerExtensionProcessor(WebApp app);

    protected abstract ExtensionProcessor getDefaultExtensionProcessor(WebApp app, HashMap map);

    public IServletWrapper createServletWrapper(IServletConfig sc) throws Exception {
        return this.webExtensionProcessor.createServletWrapper(sc);
    }

    /**
     * Method getFacade.
     * 
     * @return ServletContext
     */
    public ServletContext getFacade() {
        if (this.facade == null)
            this.facade = new ServletContextFacade(this);

        return this.facade;
    }

    public String normalize(String path) {
        String URI = path;

        int qIndex;
        String qString = "";

        if ((qIndex = URI.indexOf("?")) != -1) {
            qString = URI.substring(qIndex);
            URI = URI.substring(0, qIndex);

        }

        while (true) {
            int index = URI.indexOf("/./");
            if (index < 0)
                break;
            URI = URI.substring(0, index) + URI.substring(index + 2);
        }

        while (true) {
            int index = URI.indexOf("/../");
            if (index < 0)
                break;
            if (index == 0)
                return (null); // Trying to go outside our context
            int index2 = URI.lastIndexOf('/', index - 1);
            URI = URI.substring(0, index2) + URI.substring(index + 3);
        }

        return URI + qString;
    }

    // PK61140 - Starts
    public IServletWrapper getServletWrapper(String servletName) throws Exception {
        return getServletWrapper(servletName, false);
    }

    // PK61140 - Ends
    /**
     * Method getServletWrapper.
     * 
     * @param string
     * @return Object
     */
    public IServletWrapper getServletWrapper(String servletName, boolean addMapping) throws Exception // PK61140
    {
        IServletWrapper targetWrapper = null;
        
        IServletConfig sconfig = config.getServletInfo(servletName);
        if (sconfig!=null){
        	IServletWrapper existingServletWrapper = sconfig.getServletWrapper();
        	if (existingServletWrapper!=null)
        		return existingServletWrapper;
        }

        // Retrieve the list of mappings associated with 'servletName'
        List<String> mappings = config.getServletMappings(servletName);

        if (mappings != null) {
            for (String mapping : mappings) {
                if (mapping.charAt(0) != '/' && mapping.charAt(0) != '*')
                    mapping = '/' + mapping;
                RequestProcessor p = requestMapper.map(mapping);
                if (p != null) {
                    if (p instanceof IServletWrapper) {
                        if (((IServletWrapper) p).getServletName().equals(servletName)) {
                            targetWrapper = (IServletWrapper) p;
                            break;
                        }
                    }
                }
            }
        }

        if (targetWrapper != null)
            return targetWrapper;

        //Begin 650884
        // PK61140 - Starts
//        String path = BY_NAME_ONLY + servletName;
//        RequestProcessor p = requestMapper.map(path);
//        // RequestProcessor p = requestMapper.map(BY_NAME_ONLY + servletName);
//
//        // PK61140 - Ends
//       
//
//        if (p != null)
//            if (p instanceof ServletWrapper) {
//                if (((ServletWrapper) p).getServletName().equals(servletName))
//                    targetWrapper = (ServletWrapper) p;
//            }
//
//        if (targetWrapper != null)
//            return targetWrapper;
        //End 650884

        

        if (sconfig == null) {
            if (isInternalServlet(servletName)) {
                sconfig = loadInternalConfig(servletName);
            } else {
                // Not found in DD, and not an Internal Servlet, stray??
                //
                return null;
            }

        }

        // return webExtensionProcessor.createServletWrapper(sconfig); //
        // PK61140
        // PK61140 - Starts
        IServletWrapper sw = webExtensionProcessor.createServletWrapper(sconfig);

        //Begin 650884
//        if ((sw != null)) {
//            if (addMapping) {
//                synchronized (sconfig) {
//                    if (!requestMapper.exists(path)) {
//                        requestMapper.addMapping(path, sw);
//                    }
//                }
//            }
//        }
        //End 650884
        return sw;
        // PK61140 - Ends
    }

    public IServletWrapper getMimeFilterWrapper(String mimeType) throws ServletException {
        IServletWrapper wrapper = null;
        MimeFilter mimeFilter = (MimeFilter) config.getMimeFilters().get(mimeType);
        if (mimeFilter != null) {
            String servletName = mimeFilter.getTarget();
            try {
                wrapper = getServletWrapper(servletName);
            } catch (Exception e) {
                wrapper = null;
            }
        }
        return wrapper;
    }

    protected void registerGlobalWebAppListeners() {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
            logger.entering(CLASS_NAME, "registerGlobalWebAppListeners");

        try {
            // PK66137
            boolean isSystemApp = WCCustomProperties.DISABLE_SYSTEM_APP_GLOBAL_LISTENER_LOADING && config.isSystemApp();
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
                logger.logp(Level.FINE, CLASS_NAME, "registerGlobalWebAppListeners", "systemApp--> " + config.isSystemApp()
                        + " DISABLE_SYSTEM_APP_GLOBAL_LISTENER_LOADING--> " + WCCustomProperties.DISABLE_SYSTEM_APP_GLOBAL_LISTENER_LOADING);
            // End PK66137

            List appListeners = WebContainer.getApplicationListeners(isSystemApp);
            try {
                for (int i = 0; i < appListeners.size(); i++)
                    eventSource.addApplicationListener((ApplicationListener) appListeners.get(i));
            } catch (Throwable th) {
                logError("Failed to add global application listener: " + th);
            }

            List serListeners = WebContainer.getServletListeners(isSystemApp);
            try {
                for (int i = 0; i < serListeners.size(); i++)
                    eventSource.addServletListener((ServletListener) serListeners.get(i));
            } catch (Throwable th) {
                logError("Failed to load global servlet listener: " + th);
            }

            List erListeners = WebContainer.getServletErrorListeners(isSystemApp);
            try {
                for (int i = 0; i < erListeners.size(); i++)
                    eventSource.addServletErrorListener((ServletErrorListener) erListeners.get(i));
            } catch (Throwable th) {
                logError("Failed to load global servlet error listener: " + th);
            }

            List invListeners = WebContainer.getServletInvocationListeners(isSystemApp);
            try {
                for (int i = 0; i < invListeners.size(); i++)
                    eventSource.addServletInvocationListener((ServletInvocationListener) invListeners.get(i));
            } catch (Throwable th) {
                logError("Failed to load global servlet invocation listener: " + th);
            }

            // LIDB-3598: begin
            List finvListeners = WebContainer.getFilterInvocationListeners(isSystemApp);
            try {
                for (int i = 0; i < finvListeners.size(); i++)
                    eventSource.addFilterInvocationListener((FilterInvocationListener) finvListeners.get(i));
            } catch (Throwable th) {
                logError("Failed to load global filter invocation listener: " + th);
            }
            // 292460: begin resolve issues concerning LIDB-3598
            // WASCC.web.webcontainer
            List fListeners = WebContainer.getFilterListeners(isSystemApp);
            try {
                for (int i = 0; i < fListeners.size(); i++)
                    eventSource.addFilterListener((FilterListener) fListeners.get(i));
            } catch (Throwable th) {
                logError("Failed to load global filter listener: " + th);
            }
            List ferrorListeners = WebContainer.getFilterErrorListeners(isSystemApp);
            try {
                for (int i = 0; i < ferrorListeners.size(); i++)
                    eventSource.addFilterErrorListener((FilterErrorListener) ferrorListeners.get(i));
            } catch (Throwable th) {
                logError("Failed to load global filter error listener: " + th);
            }
            // 292460: end resolve issues concerning LIDB-3598
            // WASCC.web.webcontainer

            // LIDB-3598: end

            // CODE REVIEW START
            List scaListeners = WebContainer.getServletContextAttributeListeners(isSystemApp);
            try {
                for (int i = 0; i < scaListeners.size(); i++)
                    servletContextLAttrListeners.add(i, scaListeners.get(i));
            } catch (Throwable th) {
                com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(th, CLASS_NAME + ".registerGlobalWebAppListeners", "817", this);
                logError("Failed to load global serfvlet context attribute listener: " + th);
            }

            List scListeners = WebContainer.getServletContextListeners(isSystemApp);
            try {
                for (int i = 0; i < scListeners.size(); i++)
                    servletContextListeners.add(i, scListeners.get(i));
            } catch (Throwable th) {
                com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(th, CLASS_NAME + ".registerGlobalWebAppListeners", "827", this);
                logError("Failed to load global serfvlet context listener: " + th);
            }

            List sraListeners = WebContainer.getServletRequestAttributeListeners(isSystemApp);
            try {
                for (int i = 0; i < sraListeners.size(); i++)
                    servletRequestLAttrListeners.add(i, sraListeners.get(i));
            } catch (Throwable th) {
                com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(th, CLASS_NAME + ".registerGlobalWebAppListeners", "837", this);
                logError("Failed to load global serfvlet request attribute listener: " + th);
            }

            List srListeners = WebContainer.getServletRequestListeners(isSystemApp);
            try {
                for (int i = 0; i < srListeners.size(); i++)
                    servletRequestListeners.add(i, srListeners.get(i));
            } catch (Throwable th) {
                com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(th, CLASS_NAME + ".registerGlobalWebAppListeners", "847", this);
                logError("Failed to load global servlet request listener: " + th);
            }

            List sListeners = WebContainer.getSessionListeners(isSystemApp);
            try {
                for (int i = 0; i < sListeners.size(); i++)
                    sessionListeners.add(i, sListeners.get(i));
            } catch (Throwable th) {
                com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(th, CLASS_NAME + ".registerGlobalWebAppListeners", "857", this);
                logError("Failed to load global session listener: " + th);
            }

            List saListeners = WebContainer.getSessionAttributeListeners(isSystemApp);
            try {
                for (int i = 0; i < saListeners.size(); i++)
                    sessionAttrListeners.add(i, saListeners.get(i));
            } catch (Throwable th) {
                com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(th, CLASS_NAME + ".registerGlobalWebAppListeners", "867", this);
                logError("Failed to load global session attribute listener: " + th);
            }
            // CODE REVIEW END
        } catch (Throwable th) {
            // pk435001
            logger.logp(Level.SEVERE, CLASS_NAME, "registerGlobalWebAppListeners", "error.processing.global.listeners.for.webapp", new Object[] {
                    getApplicationName(), th }); /* 283348.1 */
            
        }

    }

    // 275172, Remove registerWebAppListeners since appserver.properties is no
    // longer in the core
    // This functionality is still available in the shell.

    public boolean isMimeFilteringEnabled() {
        return this.config.isMimeFilteringEnabled();
    }

    /**
     * Method loadInternalConfig.
     * 
     * @param servletName
     */
    private ServletConfig loadInternalConfig(String servletName) throws ServletException {
        ServletConfig sconfig = (com.ibm.ws.webcontainer.servlet.ServletConfig) webExtensionProcessor.createConfig("InternalServlet_" + servletName);
        sconfig.setServletName(servletName);
        sconfig.setDisplayName(servletName);
        sconfig.setServletContext(this.getFacade());
        sconfig.setIsJsp(false);

        for (int i = 0; i < this.internalServletList.length; i++) {
            if (internalServletList[i][0].equals(servletName))
                sconfig.setClassName(internalServletList[i][1]);
        }

        return sconfig;
    }

    /**
     * Method isInternalServlet.
     * 
     * @param servletName
     * @return boolean
     */
    public boolean isInternalServlet(String servletName) {
        for (int i = 0; i < this.internalServletList.length; i++) {
            if (internalServletList[i][0].equals(servletName))
                return true;
        }

        return false;
    }

    /**
     * Method initializeExtensionProcessors.
     */
    protected void initializeExtensionProcessors(List extensionFactories) {
        // TODO: nameSpace preinvoke/postinvoke

        if (extensionFactories == null)
            return;

        // process the ExtensionFactories
        for (int i = 0; i < extensionFactories.size(); i++) {
            ExtensionFactory fac = (ExtensionFactory) extensionFactories.get(i);
            ExtensionProcessor processor = null;

            try {
                processor = fac.createExtensionProcessor(this);
                if (processor == null) {
                    // if the factory returns a null processor, it means
                    // that this factory doesn't want to be associated with
                    // this particular webapp.
                    continue;
                }

            } catch (Throwable e) {
                // Extension processor failed to initialize
                com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(e, CLASS_NAME + ".initializeExtensionFactories", "883", this);
                // e.printStackTrace(System.err); @283348D
                // 435011
                logger.logp(Level.SEVERE, CLASS_NAME, "initializeExtensionProcessors", "extension.processor.failed.to.initialize.in.factory",
                        new Object[] { fac, e }); /* 283348.1 */
                continue;
            }

            // Get the global patterns that this factory creates processors for
            List l = fac.getPatternList();

            Iterator it = l.iterator();

            StringBuffer mapStr = new StringBuffer(' ');

            while (it.hasNext()) {
                String mapping = (String) it.next();
                try {
                    requestMapper.addMapping(mapping, processor);
                    mapStr.append(mapping);
                    mapStr.append(' ');
                } catch (Exception exc) {
                    // TODO:
                    // processor already exists for specified pattern
                    // pk435011
                    logger.logp(Level.SEVERE, CLASS_NAME, "initializeExtensionProcessors", "request.processor.already.present.for.mapping", mapping);
                }
            }

            // Get the additional patterns that the specific extension processor
            // might want to be associated with
            l = processor.getPatternList();

            it = l.iterator();

            while (it.hasNext()) {
                String mapping = (String) it.next();

                try {
                    requestMapper.addMapping(mapping, processor);
                } catch (Exception exc) {
                    // TODO:
                    // processor already exists for specified pattern
                    // pk435011
                    logger.logp(Level.SEVERE, CLASS_NAME, "initializeExtensionProcessors", "error.adding.servlet.mapping.for.servlet", new Object[] {
                            mapping, getApplicationName(), exc });
                }
            }
        }

    }

    protected void loadWebAppAttributes() {
        // add ServletContextEventSource as an attribute
        setAttribute(ServletContextEventSource.ATTRIBUTE_NAME, getServletContextEventSource());
        try {
            setAttribute("com.ibm.websphere.servlet.application.classpath", getClasspath());
            setAttribute("com.ibm.websphere.servlet.application.name", config.getDisplayName());
            setAttribute("com.ibm.websphere.servlet.application.host", getServerName());
            setAttribute("com.ibm.websphere.servlet.enterprise.application.name", getApplicationName());
            if (orderedLibPaths!=null)
            	setAttribute(ServletContext.ORDERED_LIBS, orderedLibPaths);
            if (config.getWelcomeFileList() != null)
                setAttribute(WELCOME_FILE_LIST, config.getWelcomeFileList());

            // SDJ 104265 - allow user to define scratch dir

            Map attrs = config.getJspAttributes();
            Iterator i = attrs.keySet().iterator();
            while (i.hasNext()) {
                String name = (String) i.next();
                if (name.toLowerCase().equals("scratchdir")) {
                    scratchdir = attrs.get(name).toString();
                    // break; //PK50133 comment this out.
                }
                // PK50133 start
                else if (name.toLowerCase().equals("jspclassloaderlimit")) {
                    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
                        logger.logp(Level.FINE, CLASS_NAME, "loadWebAppAttributes", "JSPClassLoaderLimit: " + attrs.get(name));
                    }
                    setJSPClassLoaderLimit(new Integer((attrs.get(name)).toString()).intValue());
                } else if (name.toLowerCase().equals("jspclassloaderexclusionlist")) {
                    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
                        logger.logp(Level.FINE, CLASS_NAME, "loadWebAppAttributes", "JSPClassLoaderExclusionList: " + attrs.get(name));
                    }
                    this.jspClassLoaderExclusionList = new ArrayList();
                    setJSPClassLoaderExclusionList(attrs.get(name).toString());
                }
                // PK50133 end
                // PK82657 start
                else if (name.toLowerCase().equals("jspclassloaderlimit.trackincludesandforwards")) {
                    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
                        logger.logp(Level.FINE, CLASS_NAME, "loadWebAppAttributes", "JSPClassLoaderLimit.TrackIncludesAndForwards: "
                                + attrs.get(name));
                    }
                    setJSPClassLoaderLimitTrackIF(Boolean.parseBoolean(attrs.get(name).toString()));
                }
                // PK82657 end

            }
            // sdj 2001/07/30 110113 begin
            if (scratchdir == null) {
                String sDir = System.getProperty("com.ibm.websphere.servlet.temp.dir");
                if (sDir != null) {
                    scratchdir = sDir;
                }
                // if (scratchdir !=null) {
                // System.out.println(" WebApp, scratchdir from system: "+scratchdir);
                // }
            }
            // sdj 2001/07/30 110113 end

            if (scratchdir == null) {
                setAttribute("javax.servlet.context.tempdir", new File(getTempDirectory()));
            } else {
                setAttribute("javax.servlet.context.tempdir", new File(getTempDirectory(scratchdir, true, true)));
            }
        } catch (Exception e) {
            // pk435011
            logger.logp(Level.SEVERE, CLASS_NAME, "loadWebAppAttributes", "error.while.setting.WebAppAttributes", e);
            // e.printStackTrace(System.err); @283348D
        }
    }

    private void clearLifecycleListeners() {
        // clear the current arrays
        servletContextListeners.clear();
        servletContextLAttrListeners.clear();

        // 2.4 Listeners
        servletRequestListeners.clear();
        servletRequestLAttrListeners.clear();

        // cmd PQ81253 session listeners
        sessionListeners.clear();
        sessionAttrListeners.clear();

        sessionActivationListeners.clear();
        sessionBindingListeners.clear();
    }
    
    protected void loadLifecycleListeners() {
        logger.entering(CLASS_NAME, "loadLifecycleListeners");
        try {
            // get a list of the defined listeners
            java.util.List listeners = config.getListeners();

            // see if we have any listeners to process
            if (!listeners.isEmpty()) {
                // we do have listeners...process 'em
                Iterator iter = listeners.iterator();

                while (iter.hasNext()) {
                    // get the listener instance
                    String listenerClass = null;
                    Object curObj = iter.next();
                    listenerClass = getListenerClassName(curObj);

                    if (listenerClass != null) {
                        // determine listener type...first, instantiate it
						//596191 Start
						Object listener = null;
						try{ 
							listener = loadListener(listenerClass);
						}
	        	        catch(InjectionException ie){
	        		       com.ibm.ws.ffdc.FFDCFilter.processException(ie, "com.ibm.ws.webcontainer.webapp.WebApp.loadListener","672", this );
	        		       LoggerHelper.logParamsAndException(logger, Level.SEVERE, CLASS_NAME,"loadLifecycleListeners", "Listener.found.but.injection.failure", new Object[]{listenerClass} , ie );
                        } //596191 End

                        if (listener != null) {
                            if (listener instanceof javax.servlet.ServletContextListener) {
                                // add to the context listener list
                                servletContextListeners.add(listener);
                            }
                            if (listener instanceof javax.servlet.ServletContextAttributeListener) {
                                // add to the context attr listener list
                                servletContextLAttrListeners.add(listener);
                            }

                            // 2.4 Listeners
                            if (listener instanceof javax.servlet.ServletRequestListener) {
                                // add to the request listener list
                                servletRequestListeners.add(listener);
                            }
                            if (listener instanceof javax.servlet.ServletRequestAttributeListener) {
                                // add to the request attribute list
                                servletRequestLAttrListeners.add(listener);
                            }
                            // cmd PQ81253 BEGIN load session listeners here
                            if (listener instanceof javax.servlet.http.HttpSessionListener) {
                                // add to the session listener list
                                this.sessionCtx.addHttpSessionListener((javax.servlet.http.HttpSessionListener) listener, name);
                                this.sessionListeners.add(listener);
                            }
                            if (listener instanceof javax.servlet.http.HttpSessionAttributeListener) {
                                // add to the session attribute listener list
                                if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
                                    logger
                                            .logp(Level.FINE, CLASS_NAME, "addLifecycleListener",
                                                    "listener instanceof javax.servlet.http.HttpSessionAttributeListener");
                                    logger.logp(Level.FINE, CLASS_NAME, "addLifecycleListener", "name : " + name);
                                }
                                this.sessionCtx.addHttpSessionAttributeListener((HttpSessionAttributeListener) listener, name);

                                // 434577
                                // add to a mirror list because we can't get access to the list
                                // the
                                // session context is holding on to later on.

                                this.sessionAttrListeners.add(listener);
                            }
                            // cmd PQ81253 END

                            if (listener instanceof javax.servlet.http.HttpSessionActivationListener) {
                                sessionActivationListeners.add(listener);
                            }

                            if (listener instanceof javax.servlet.http.HttpSessionBindingListener) {
                                sessionBindingListeners.add(listener);
                            }
                        }
                    }
                }
            }
        } catch (Throwable th) {
            // pk435011
            logger.logp(Level.SEVERE, CLASS_NAME, "loadLifecycleListeners", "error.processing.global.listeners.for.webapp", th);
            
        }
        logger.exiting(CLASS_NAME, "loadLifecycleListeners");
    }

    /**
     * @return
     */
    protected String getListenerClassName(Object curObj) {
        if (curObj instanceof String) {
            return (String) curObj;
        }
        return null;
    }

    // LIDB1234.2 - added method below to load a listener class
    protected Object loadListener(String lClassName) throws InjectionException //596191 :: PK97815
    {
    	Object listener = null;

        try {
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
                logger.logp(Level.FINE, CLASS_NAME, "loadListener", "loadListener Classloader " + getClassLoader());
            // PK16542 end
            // instantiate the listener
            listener = java.beans.Beans.instantiate(getClassLoader(), lClassName);
        } catch (Throwable th) {
            // some exception, log error.
            logError("Failed to load listener: " + lClassName, th);
        }
        return listener;
    }

    public abstract Servlet getSimpleFileServlet();

    protected abstract void initializeServletContextFacades();

    // LIDB1234.2 - method added below to notify listeners of servlet context
    // creation
    public void notifyServletContextCreated() throws Throwable{

        TxCollaboratorConfig txConfig = null;
        try {
            WebModuleMetaData mm = getModuleMetaData();
            webAppNameSpaceCollab.preInvoke(mm.getCollaboratorComponentMetaData());

            txConfig = txCollab.preInvoke(null, this.isServlet23);
            if (txConfig != null)
                txConfig.setDispatchContext(null);

            if (!servletContextListeners.isEmpty()) {

                Iterator i = servletContextListeners.iterator();
                ServletContextEvent sEvent = new ServletContextEvent(this.getFacade());

            	//canAddServletContextListener used in sL.contextInitialized
                canAddServletContextListener=false;
                while (i.hasNext()) {
                    // get the listener
                    ServletContextListener sL = (ServletContextListener) i.next();

                    // invoke the listener's context initd method
                    // PK27660 - wrap contextInitialized in try/catch
                    try {
                    	Set<String> webXmlDefListeners = this.config.getWebXmlDefinedListeners();
                    	if (webXmlDefListeners!=null&&!webXmlDefListeners.contains(sL.getClass().getName())) {
                    		withinContextInitOfProgAddListener=true;
                    	}
                        sL.contextInitialized(sEvent);
                    } catch (Throwable th) {
                        com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(th, CLASS_NAME + ".notifyServletContextCreated", "1341", this);
                        // pk435011
                        logger.logp(Level.SEVERE, CLASS_NAME, "notifyServletContextCreated", "exception.while.initializing.context", th);
                        if (withinContextInitOfProgAddListener) {
                        	if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
                                logger.logp(Level.FINE, CLASS_NAME, "notifyServletContextCreated", "rethrowing exception since the scl was programmatically added");
                        	throw th;
                        }

                    } 
                    withinContextInitOfProgAddListener=false;
                }
            }
            setAttribute("com.ibm.ws.jsp.servletContextListeners.contextInitialized", "true");  //PM05903 
        } catch (Throwable e) {
        	if (withinContextInitOfProgAddListener)
        		throw e;
            com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(e, CLASS_NAME + ".notifyServletContextCreated", "1353", this);
            // e.printStackTrace(System.err); @283348D
            // pk435011
            logger.logp(Level.SEVERE, CLASS_NAME, "notifyServletContextCreated", "exception.caught.in.notifyServletContextCreated", e); // PK27660
        } finally {
        	canAddServletContextListener=true;
            try {
                txCollab.postInvoke(null, txConfig, this.isServlet23);
            } catch (Exception e) {
                com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(e, CLASS_NAME + ".notifyServletContextCreated", "1327", this);

            }
            webAppNameSpaceCollab.postInvoke();
        }
    }

    // LIDB1234.2 - method added below to notify listeners of servlet context
    // destruction
    public void notifyServletContextDestroyed() {
        TxCollaboratorConfig txConfig = null;
        try {

            WebModuleMetaData mm = getModuleMetaData();
            webAppNameSpaceCollab.preInvoke(mm.getCollaboratorComponentMetaData());

            txConfig = txCollab.preInvoke(null, this.isServlet23);
            if (txConfig != null)
                txConfig.setDispatchContext(null);
            // need to notify listeners registered in the
            // _servletContextListeners array
            if (!servletContextListeners.isEmpty()) {
                ServletContextEvent sEvent = new ServletContextEvent(this.getFacade());

                // listeners must be notified in reverse order of definition
                for (int listenerIndex = servletContextListeners.size() - 1; listenerIndex > -1; listenerIndex--) {
                    // get the listener
                    ServletContextListener sL = (ServletContextListener) servletContextListeners.get(listenerIndex);

                    // invoke the listener's context destroyed method
                    try {
                        sL.contextDestroyed(sEvent);
                    } catch (Throwable th) {
                        com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(th, CLASS_NAME + ".notifyServletContextDestroyed", "1405", this);
                        // pk435011
                        logger.logp(Level.SEVERE, CLASS_NAME, "notifyServletContextDestroyed", "exception.caught.destroying.context", th);
                    }
                }
            }
        } catch (Exception e) {
            com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(e, CLASS_NAME + ".notifyServletContextDestroyed", "1417", this);
            // pk435011
            logger.logp(Level.SEVERE, CLASS_NAME, "notifyServletContextDestroyed", "exception.caught.in.notifyServletContextDestroyed", e); // PK27660
        } finally {
            try {
                txCollab.postInvoke(null, txConfig, this.isServlet23);
            } catch (Exception e) {
                com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(e, CLASS_NAME + ".notifyServletContextDestroyed", "1557", this);
            }
            webAppNameSpaceCollab.postInvoke();
        }
    }

    // LIDB1234.2 - method added below to notify listeners of servlet context
    // attr creation
    public void notifyServletContextAttrAdded(String name, Object value) {

        // need to notify listeners registered in the
        // ServletContextAttributeListener array
        if (!servletContextLAttrListeners.isEmpty()) {
            // We run the risk of getting a concurrent modification
            Iterator i = servletContextLAttrListeners.iterator();
            ServletContextAttributeEvent sEvent = new ServletContextAttributeEvent(this.getFacade(), name, value);

            while (i.hasNext()) {
                // get the listener
                ServletContextAttributeListener sL = (ServletContextAttributeListener) i.next();

                // invoke the listener's attr added method
                sL.attributeAdded(sEvent);
            }
        }

    }

    // LIDB1234.2 - method added below to notify listeners of servlet context
    // attr replacement
    public void notifyServletContextAttrReplaced(String name, Object value) {
        // need to notify listeners registered in the
        // ServletContextAttributeListener array
        if (!servletContextLAttrListeners.isEmpty()) {
            Iterator i = servletContextLAttrListeners.iterator();
            ServletContextAttributeEvent sEvent = new ServletContextAttributeEvent(this.getFacade(), name, value);

            while (i.hasNext()) {
                // get the listener
                ServletContextAttributeListener sL = (ServletContextAttributeListener) i.next();

                // invoke the listener's attr added method
                sL.attributeReplaced(sEvent);
            }
        }
    }

    // LIDB1234.2 - method added below to notify listeners of servlet context
    // attr removal
    public void notifyServletContextAttrRemoved(String name, Object value) {
        // need to notify listeners registered in the
        // ServletContextAttributeListener array
        if (!servletContextLAttrListeners.isEmpty()) {
            Iterator i = servletContextLAttrListeners.iterator();
            ServletContextAttributeEvent sEvent = new ServletContextAttributeEvent(this.getFacade(), name, value);

            while (i.hasNext()) {
                // get the listener
                ServletContextAttributeListener sL = (ServletContextAttributeListener) i.next();

                // invoke the listener's attr added method
                sL.attributeRemoved(sEvent);
            }
        }
    }

    //PK91120 Start
    public boolean notifyServletRequestCreated(ServletRequest request)
    {        
        boolean servletRequestListenerCreated = false;                                              
        if (!servletRequestListeners.isEmpty())
        {
            WebContainerRequestState reqState = WebContainerRequestState.getInstance(true);
            if (reqState.getAttribute("com.ibm.ws.webcontainer.invokeListenerRequest") == null)
            {
                reqState.setAttribute("com.ibm.ws.webcontainer.invokeListenerRequest", false);                      
                    
                Iterator i = servletRequestListeners.iterator();
                ServletRequestEvent sEvent = new ServletRequestEvent(this.getFacade(), request);

                while (i.hasNext())
                {
                    // get the listener
                    ServletRequestListener sL = (ServletRequestListener) i.next();

                    // invoke the listener's request initd method
                    sL.requestInitialized(sEvent);
                }       
                servletRequestListenerCreated = true;
            }       
            else{
                if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) 
                    logger.logp(Level.FINE, CLASS_NAME,"notifyServletRequestCreated", 
                            " ServletListener already invoked for request, reqState --> "+ reqState);                               
            }
        }       
     return servletRequestListenerCreated;
    }
    //PK91120 End

    public void notifyServletRequestDestroyed(ServletRequest request) {
        if (!servletRequestListeners.isEmpty()) {
            ServletRequestEvent sEvent = new ServletRequestEvent(this.getFacade(), request);

            // listeners must be notified in reverse order of definition
            for (int listenerIndex = servletRequestListeners.size() - 1; listenerIndex > -1; listenerIndex--) {
                // get the listener
                ServletRequestListener sL = (ServletRequestListener) servletRequestListeners.get(listenerIndex);

                // invoke the listener's request destroyed method
                sL.requestDestroyed(sEvent);
            }
        }
    }

    public void notifyServletRequestAttrAdded(ServletRequest request, String name, Object value) {
        // need to notify listeners registered in the
        // ServletRequestAttributeListener array
        if (!servletRequestLAttrListeners.isEmpty()) {
            Iterator i = servletRequestLAttrListeners.iterator();
            ServletRequestAttributeEvent sEvent = new ServletRequestAttributeEvent(this.getFacade(), request, name, value);

            while (i.hasNext()) {
                // get the listener
                ServletRequestAttributeListener sL = (ServletRequestAttributeListener) i.next();

                // invoke the listener's attr added method
                sL.attributeAdded(sEvent);
            }
        }
    }

    public void notifyServletRequestAttrReplaced(ServletRequest request, String name, Object value) {
        // need to notify listeners registered in the
        // ServletRequestAttributeListener array
        if (!servletRequestLAttrListeners.isEmpty()) {
            Iterator i = servletRequestLAttrListeners.iterator();
            ServletRequestAttributeEvent sEvent = new ServletRequestAttributeEvent(this.getFacade(), request, name, value);

            while (i.hasNext()) {
                // get the listener
                ServletRequestAttributeListener sL = (ServletRequestAttributeListener) i.next();

                // invoke the listener's attr added method
                sL.attributeReplaced(sEvent);
            }
        }
    }

    public void notifyServletRequestAttrRemoved(ServletRequest request, String name, Object value) {
        // need to notify listeners registered in the
        // ServletRequestAttributeListener array
        if (!servletRequestLAttrListeners.isEmpty()) {
            Iterator i = servletRequestLAttrListeners.iterator();
            ServletRequestAttributeEvent sEvent = new ServletRequestAttributeEvent(this.getFacade(), request, name, value);

            while (i.hasNext()) {
                // get the listener
                ServletRequestAttributeListener sL = (ServletRequestAttributeListener) i.next();

                // invoke the listener's attr added method
                sL.attributeRemoved(sEvent);
            }
        }
    }

    // LIDB441.9.2
    // This method can only be called after loadLifecycleListeners which clears
    // the listener arrays.
    // It has been added with the intention of being called from
    // WebAppInitializationCollaborator impls
    public void addLifecycleListener(java.util.EventListener listener) {
    	String listenerClassName = listener==null?null:listener.getClass().getName();
    	if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
    		logger.entering(CLASS_NAME, "addLifecycleListener : listenerClass = " + listenerClassName);	
    	}    	
        
        if (listener != null) {
            if (listener instanceof javax.servlet.ServletContextListener) {
            	// add to the context listener list
            	boolean addFirst = false;
            	//if using WAS shipped JSF jar, register JSF listener first
            	//for performance reasons, treating non JSF apps as highest priority (will fail out of first check)
            	Object jsf_impl_enabled_param = this.getAttribute(JSF_IMPL_ENABLED_PARAM);
            	if (!JSF_IMPL_ENABLED_NONE.equals(jsf_impl_enabled_param) && !JSF_IMPL_ENABLED_CUSTOM.equals(jsf_impl_enabled_param)) {
            		if (SUN_CONFIGURE_LISTENER_CLASSNAME.equals(listenerClassName) ||
                    	MYFACES_LIFECYCLE_LISTENER_CLASSNAME.equals(listenerClassName)) {
            			addFirst=true;
            		}
            	}            	
            	if (addFirst) {
            		servletContextListeners.add(0, listener);
            	} else { 
            		servletContextListeners.add(listener);
            	}
            }
            if (listener instanceof javax.servlet.ServletContextAttributeListener) {
                // add to the context attr listener list
                servletContextLAttrListeners.add(listener);
            }

            if (listener instanceof javax.servlet.http.HttpSessionListener) {
                // add to the session listener list
                this.sessionCtx.addHttpSessionListener((javax.servlet.http.HttpSessionListener) listener, name);

                // 434577
                // add to a mirror list because we can't get access to the list
                // the
                // session context is holding on to later on.

                this.sessionListeners.add(listener);
                this.addedSessionListeners.add(listener);
            }

            // \PK34418 begins
            if (listener instanceof javax.servlet.http.HttpSessionAttributeListener) {
                // add to the session attribute listener list
                if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
                    logger
                            .logp(Level.FINE, CLASS_NAME, "addLifecycleListener",
                                    "listener instanceof javax.servlet.http.HttpSessionAttributeListener");
                    logger.logp(Level.FINE, CLASS_NAME, "addLifecycleListener", "name : " + name);
                }
                this.sessionCtx.addHttpSessionAttributeListener((HttpSessionAttributeListener) listener, name);

                // 434577
                // add to a mirror list because we can't get access to the list
                // the
                // session context is holding on to later on.

                this.sessionAttrListeners.add(listener);
                this.addedSessionAttrListeners.add(listener);

            }// PK34418

            // 2.4 Listeners
            if (listener instanceof javax.servlet.ServletRequestListener) {
                servletRequestListeners.add(listener);
            }
            if (listener instanceof javax.servlet.ServletRequestAttributeListener) {
                servletRequestLAttrListeners.add(listener);
            }

            if (listener instanceof javax.servlet.http.HttpSessionActivationListener) {
                sessionActivationListeners.add(listener);
            }

            if (listener instanceof javax.servlet.http.HttpSessionBindingListener) {
                sessionBindingListeners.add(listener);
            }
        }
        logger.exiting(CLASS_NAME, "addLifecycleListener");
    }

    /**
     * Method getServletContextEventSource.
     * 
     * @return Object
     */
    public ServletContextEventSource getServletContextEventSource() {
        return this.eventSource;
    }

    /**
     * @see com.ibm.ws.webcontainer.webapp.WebAppContext#getClassLoader()
     */
    public ClassLoader getClassLoader() {
        return this.loader;
    }

    /**
     * @see com.ibm.ws.webcontainer.webapp.WebAppContext#getDocumentRoot()
     */
    public String getDocumentRoot() {
        return this.documentRoot;
    }

    /**
     * @see com.ibm.ws.webcontainer.webapp.WebAppContext#getClasspath()
     */
    public String getClasspath() {
        return this.loader.toString();
    }

    /**
     * Method getNodeName.
     * 
     * @return int
     */
    public abstract String getNodeName();

    public abstract String getServerName();

    // begin PK31450
    public String getTempDirectory() {
        return getTempDirectory(true);
    }

    public String getCommonTempDirectory() {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINEST)) {
            logger.entering(CLASS_NAME, "getCommonTempDirectory");
        }
        String tempDir = null;
        if (scratchdir == null) {
            tempDir = getTempDirectory(false);
        } else {
            tempDir = getTempDirectory(scratchdir, true, false);
        }
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINEST)) {
            logger.exiting(CLASS_NAME, "getCommonTempDirectory");
        }
        return tempDir;
    }

    public String getTempDirectory(boolean checkZOSFlag) {
        String sr = System.getProperty("server.root"); /* MD15600 */
        if ((sr == null) || (sr.length() < 1)) { /* MD15600 */
            // pk435011
            logger.logp(Level.SEVERE, CLASS_NAME, "getTempDirectory", "server.root.is.null"); /* MD15600 */
            sr = null; /* MD15600 */
        } else { /* MD15600 */
            sr = sr.trim(); /* MD15600 */
            if ((sr == null) || (sr.length() < 1)) { /* MD15600 */
                // pk435011
                logger.logp(Level.SEVERE, CLASS_NAME, "getTempDirectory", "server.root.is.null"); /* MD15600 */
                sr = null; /* MD15600 */
            } /* MD15600 */
        }
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) /* MD15600 */
            logger.logp(Level.FINE, CLASS_NAME, "getTempDirectory", "Using.[{0}].as.server.root", sr); /* MD15600 */
        if (sr == null) { /* MD15600 */
            return sr; /* MD15600 */
        } /* MD15600 */

        StringBuilder path = new StringBuilder(sr);

        if (!(path.charAt(path.length() - 1) == java.io.File.separatorChar)) {
            path.append(java.io.File.separator);
        }

        path.append("temp").append(java.io.File.separator);

        return getTempDirectory(path.toString(), false, checkZOSFlag);
    }

    public String getTempDirectory(String dirRoot, boolean override, boolean checkZOSFlag) {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINEST)) {
            logger.logp(Level.FINEST, CLASS_NAME, "getTempDirectory", "dirRoot-->" + dirRoot + " override --> " + override + " checkZOSFlag --> "
                    + checkZOSFlag);
        }

        StringBuilder dir = new StringBuilder(dirRoot.toString());

        if (!(dir.charAt(dir.length() - 1) == java.io.File.separatorChar)) {
            dir.append(java.io.File.separator);
        }

        // begin 247392, part 2
        if (checkZOSFlag && !WebContainer.isDefaultTempDir()) {
            // END PK31450
            // Begin 257796, part 1
            dir.append(getNodeName()).append(java.io.File.separator).append(getServerName().replace(' ', '_')).append(
                    "_" + WebContainer.getWebContainer().getPlatformHelper().getServerID());
            if (WebContainer.getTempDir() == null) {
                WebContainer.setTempDir(dir.toString());
                if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
                    logger.logp(Level.FINE, CLASS_NAME, "getTempDirectory", "ZOS temp dir is:" + WebContainer.getTempDir());
            }
            dir.append(java.io.File.separator).append(getApplicationName().replace(' ', '_')).append(java.io.File.separator).append(
                    config.getModuleName().replace(' ', '_'));
            // End 257796, part 1
        } else
            dir.append(getTempDirChildren());

        // defect 112137 begin - don't replace spaces with underscores
        // java.io.File tmpDir = new java.io.File(dir.toString().replace(' ',
        // '_'));
        java.io.File tmpDir = new java.io.File(dir.toString());
        // defect 112137 end

        if (!tmpDir.exists()) {
            // 117050 OS/400 support for servers running under two different
            // profile
            if (System.getProperty("os.name").equals("OS/400")) {
                int nodeIndex = tmpDir.toString().indexOf(getNodeName());
                nodeIndex = nodeIndex + getNodeName().length();
                String nodeDir = tmpDir.toString().substring(0, nodeIndex);
                java.io.File tempNodeDir = new java.io.File(nodeDir);
                if (!tempNodeDir.exists()) {
                    tempNodeDir.mkdirs();
                    String cmd = "/usr/bin/chown QEJBSVR " + nodeDir;
                    try {
                        Runtime runtime = Runtime.getRuntime();
                        Process process = runtime.exec(cmd);
                        process.waitFor();
                        if (process.exitValue() != 0) {
                            // pk435011
                            logger.logp(Level.SEVERE, CLASS_NAME, "getTempDirectory", "chown.failed", new Object[] { cmd,
                                    new Integer(process.exitValue()).toString() });
                        }
                    } catch (Exception e) {
                        com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(e, CLASS_NAME + ".getTempDirectory", "991", this);
                        // e.printStackTrace(); @283348D
                    }
                }
            }
            boolean success = tmpDir.mkdirs();
            if (success == false) {
                // pk435011
                logger.logp(Level.SEVERE, CLASS_NAME, "getTempDirectory", "failed.to.create.temp.directory", tmpDir.toString());
            }
        }

        if (tmpDir.canRead() == false || tmpDir.canWrite() == false) {
            if (override) {
                // pk435011
                logger.logp(Level.SEVERE, CLASS_NAME, "getTempDirectory", "unable.to.use.specified.temp.directory", new Object[] { tmpDir.toString(),
                        tmpDir.canRead(), tmpDir.canWrite() });
            } else {
                // pk435011
                logger.logp(Level.SEVERE, CLASS_NAME, "getTempDirectory", "unable.to.use.default.temp.directory", new Object[] { tmpDir.toString(),
                        tmpDir.canRead(), tmpDir.canWrite() });
            }
        }
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
            logger.logp(Level.FINE, CLASS_NAME, "getTempDirectory", "directory --> " + tmpDir.getAbsolutePath());
        }
        return tmpDir.getAbsolutePath();
    }

    public String getTempDirChildren() {
        StringBuilder dir = new StringBuilder();

        // SDJ D99077 - use Uri of web module in constructing temp dir, not the
        // web module's display name
        // defect 113620 - replace spaces with underscores starting with
        // servername
        dir.append(getNodeName()).append(java.io.File.separator).append(getServerName().replace(' ', '_')).append(java.io.File.separator).append(
                getApplicationName().replace(' ', '_')).append(java.io.File.separator).append(config.getModuleName().replace(' ', '_'));

        return dir.toString();
    }

    public static boolean isDisableServletAuditLogging() {
        // 89638
        if (disableServletAuditLogging == -1) {
            String skipAudit = System.getProperty("com.ibm.servlet.engine.disableServletAuditLogging");

            if (skipAudit != null && skipAudit.toLowerCase().equals("true")) {
                disableServletAuditLogging = 1;
                // System.out.println(new java.util.Date() +
                // " [Servlet.Message]-[Servlet Logging to the Audit Facility has been disabled.]");
            } else {
                disableServletAuditLogging = 0;
            }
        }

        return disableServletAuditLogging == 1 ? true : false;
    }

    /**
     * @see com.ibm.ws.webcontainer.webapp.WebAppContext#getContextPath()
     */
    public String getContextPath() {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
            logger.logp(Level.FINE, CLASS_NAME, "getContextPath", "contextPath->" + contextPath);

        return this.contextPath;
    }

    /**
     * @see com.ibm.ws.webcontainer.webapp.WebAppContext#logServletMessage(String,
     *      String)
     */
    public void logServletMessage(String servletName, String message) {
        Object[] args = { servletName, message };

        if (isDisableServletAuditLogging())
            logger.logp(Level.FINE, CLASS_NAME, "logServletMessage", "[Servlet Message]-[{0}]:.{1}", args);
        else
            logger.logp(Level.INFO, CLASS_NAME, "logServletMessage", "log.servlet.message", args);
    }

    /**
     * @see com.ibm.ws.webcontainer.webapp.WebAppContext#logServletError(String,
     *      String, Throwable)
     */
    public void logServletError(String servletName, String message, Throwable th) {
        ServletException e = null;

        if (th instanceof ServletException) {
            e = (ServletException) th;

            while (e != null) {
                th = e.getRootCause();

                if (th == null) {
                    th = e;
                    break;
                }
                e = th instanceof ServletException ? (ServletException) th : null;
            }
        }

        // log the error
        if (message.equals(""))
            // pk435011
            logger.logp(Level.SEVERE, CLASS_NAME, "logServletError", "log.servlet.error", new Object[] { servletName, th });
        else
            // pk435011
            logger.logp(Level.SEVERE, CLASS_NAME, "logServletError", "log.servlet.error.and.message", new Object[] { servletName, message, th });

        // 105840 - end

    }

    /**
     * @see com.ibm.ws.webcontainer.webapp.WebAppContext#logServletError(String,
     *      String)
     */
    public void logServletError(String servletName, String message) {
        Object[] args = { servletName, message };
        // pk435011
        logger.logp(Level.SEVERE, CLASS_NAME, "logServletError", "log.servlet.error", args);

    }

    /**
     * @see com.ibm.ws.webcontainer.webapp.WebAppContext#logError(String)
     */
    public void logError(String message) {
        // pk435011
        logger.logp(Level.SEVERE, CLASS_NAME, "logError", "Error.reported.{0}", message);
    }

    /**
     * @see com.ibm.ws.webcontainer.webapp.WebAppContext#logError(String,
     *      Throwable)
     */
    public void logError(String message, Throwable th) {
        ServletException e = null;

        if (th instanceof ServletException) {
            e = (ServletException) th;

            while (e != null) {
                th = e.getRootCause();

                if (th == null) {
                    th = e;
                    break;
                }
                e = th instanceof ServletException ? (ServletException) th : null;
            }
        }

        // log the error
        Object[] args = { message, th };
        // pk435011
        logger.logp(Level.SEVERE, CLASS_NAME, "logError", "log.servlet.error", args);
    }

    /**
     * @see com.ibm.ws.webcontainer.webapp.WebAppContext#getServletContext(String)
     */
    public ServletContext getServletContext(String path) {
        return ((WebApp) ((WebGroup) parent).findContext(path)).getFacade();
    }

    /**
     * @see com.ibm.ws.webcontainer.webapp.WebAppContext#getResourceAsStream(String)
     */
    public InputStream getResourceAsStream(String path) {
        try {
            URL url = getResource(path);
            if (url == null)
                return null;
            URLConnection conn = url.openConnection();
            return conn.getInputStream();
        } catch (MalformedURLException e) {
            com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(e, CLASS_NAME + ".getResourceAsStream", "602", this);
            return null;
        } catch (IOException e) {
            com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(e, CLASS_NAME + ".getResourceAsStream", "606", this);
            return null;
        }
    }

    /**
     * @see com.ibm.ws.webcontainer.webapp.WebAppContext#getResource(String)
     */
    public URL getResource(String p) throws MalformedURLException {
        String rPath = null;
        URL returnURL = null;

        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
            logger.entering(CLASS_NAME, "getResource", "resource --> " + p);
        }
        /*
         * The spec states the resource must start with a / so if one isn't
         * there we prepend one.
         */
        // Begin 263020
        if (p.charAt(0) != '/' && p.charAt(0) != '\\') {
            if (prependSlashToResource) {
                logger.logp(Level.WARNING, CLASS_NAME, "getResource", "resource.path.has.to.start.with.slash");
                rPath = "/" + p;
            } else {
                throw new MalformedURLException(nls.getString("resource.path.has.to.start.with.slash"));
            }
        } else {
            rPath = p;
        }
        // End 263020

        // PM21451 Start
        String uri = getRealPath(rPath,"webapp");
        java.io.File checkFile = new java.io.File(uri);
        if (!checkFile.exists()) {
           	returnURL = getDocumentRootUtils(uri).getURL(rPath,metaInfCache);
        } else {
        	returnURL = checkFile.toURL();	        	
        } 
        // PM21451 End
        
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
            logger.exiting(CLASS_NAME, "getResource", "URL --> " + (returnURL == null ? "null." : returnURL.toString()));
        }

        return returnURL;
        
    }

    // PM21451 Add covenience method
    public DocumentRootUtils getStaticDocumentRootUtils() {
    	return new DocumentRootUtils(this,staticDocRoot.getedrSearchPath(),staticDocRoot.getpfedrSearchPath());
    }
    
    // PM21451 Add covenience method
    public DocumentRootUtils getJSPDocumentRootUtils() {
    	return new DocumentRootUtils(this,jspDocRoot.getedrSearchPath(),jspDocRoot.getpfedrSearchPath());
    }
        
    // PM21451 Add covenience method    
    // Return an appropriate DocumentRootUtils object (For static files or JSPs)
    // based on the resource being requested. If the requested resource would be 
    // processed by a JSp Extension Porcessor return a JSP based object otherwise 
    // return a static file based object. 
    private DocumentRootUtils getDocumentRootUtils(String uri) {
    	
        boolean useJSPRoot=false;
        DocumentRootUtils docRoot=null;
        
        // Only need to check if there is a Doc Root of one sort or the other.
        if (staticDocRoot.hasDocRoot() || jspDocRoot.hasDocRoot()) {
        	
        	// See if the resourcece would map to a jsp request processor.
        	RequestProcessor requestProcessor = requestMapper.map(uri);
        	
        	if (requestProcessor!=null) {
        		           		
        		try {
            	    
        			Class jspProcessorClass = Class.forName("com.ibm.ws.jsp.webcontainerext.AbstractJSPExtensionProcessor");                	    
        			useJSPRoot = jspProcessorClass.isInstance(requestProcessor);
        			
        			if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
        				logger.logp(Level.FINE, CLASS_NAME, "useJSPDocRoot", "useJSPRoot = " + useJSPRoot + ", request Processor is " + requestProcessor.getClass().getName());
                    }
            	    
        		} catch (ClassNotFoundException cnfe) {
        			
        			useJSPRoot=false;
        			
                    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
                        logger.logp(Level.FINE, CLASS_NAME, "useJSPDocRoot", "useJSPRoot = " + useJSPRoot + ", ClassNotFoundException.");
                    }
        		}
        	}    
        }
        if (useJSPRoot) {
            docRoot = getJSPDocumentRootUtils();           	
        } else {
            docRoot = getStaticDocumentRootUtils();            	
        }

        return docRoot;
    }
    /**
     * @see com.ibm.ws.webcontainer.webapp.WebAppContext#getRealPath(String)
     */

    // getRealPath checks for the absolute path of the uri or path specified.
	// Parameters:	path - a String specifying a virtual path 
	// Returns: a String specifying the real path, 
	// or null if the translation cannot be performed, null is returned only if Custom property is set
	public String getRealPath(String uri)
	{
    //PM21451 Start
		String realPath = null,docRootPath = null; 
		
		realPath = getRealPath(uri, "webapp");
			
		if ( new java.io.File(realPath).exists() == false ) {		
            // File does not exist so look in the document roots.
			docRootPath = getDocumentRootUtils(uri).getDocumentRealPath(uri,metaInfCache);
	        if (docRootPath != null) {
	        	realPath = docRootPath.replace('/', java.io.File.separatorChar);
	        } else if (WCCustomProperties.CHECK_EDR_IN_GET_REALPATH){
	        	realPath = null;
	        }
        } 
				
		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) 
			logger.logp(Level.FINE, CLASS_NAME,"getRealPath","realPath -->" + realPath); 
		        
		return realPath;

	}
	
	// This should be called by internal components only. If possible avoid it.
	// This does not return null and does not check for EDR. 
	public String getRealPath(String uri, String source)
	{
    //PM21451 End		
		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {
			logger.logp(Level.FINE, CLASS_NAME,"getRealPath", " uri -->" + uri +" , source --> "+ source ); //PM21451	
		}
        if (uri == null) {
            uri = "/";
        } else if (uri.equals("/")) {
            return getDocumentRoot().replace('/', java.io.File.separatorChar);
        }
        // SDJ 106155 2001/06/25 begin
        else if (!uri.startsWith("/") && !uri.startsWith("\\")) {
            uri = "/" + uri;
        }
        // uri = uri.replace('/', java.io.File.separatorChar);
        return ((getDocumentRoot() + uri).replace('/', java.io.File.separatorChar));
        // SDJ 106155 2001/06/25 end

    }
    
    /**
     * @see com.ibm.ws.webcontainer.webapp.WebAppContext#log(String)
     */
    public void log(String msg) {
        if (isDisableServletAuditLogging()) {
            // System.out.println(new java.util.Date() + " [" + getName() + "]["
            // + this.getContextPath() + "][Servlet.LOG]-[" + msg + "]:.");
        } else {
            logger.logp(Level.INFO, CLASS_NAME, "log", "log.servlet.message", new Object[] { getName(), msg });
        }
    }

    /**
     * @see com.ibm.ws.webcontainer.webapp.WebAppContext#log(String, Throwable)
     */
    public void log(String message, Throwable th) {
        Object[] args = { getName(), this.getContextPath(), message, th };
        // 89638
        if (isDisableServletAuditLogging()) {
            // System.out.println(new java.util.Date() + " [" + getName() + "]["
            // + this.getContextPath() + "][Servlet.LOG]-[" + message + "]:." +
            // th);
        } else {
            logger.logp(Level.INFO, CLASS_NAME, "log", "log.servlet.message.with.throwable", args);
        }
    }

    /**
     * @see com.ibm.ws.webcontainer.webapp.WebAppContext#getMimeType(String)
     */
    public String getMimeType(String file) {
        // PK76142 Start
        if (file == null || file.length() == 0) {
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
                logger.logp(Level.FINE, CLASS_NAME, "getMimeType", "returning empty string");
            }
            return null;
        }
        // PK76142 End

        int pathElementIndex = file.indexOf(';');
        int dot = -1;
        String extWithDot;
        if (pathElementIndex == -1) {
            dot = file.lastIndexOf('.');
            if (dot == -1)
                dot = 0;
            extWithDot = file.substring(dot);
        } else {
            dot = file.lastIndexOf('.', pathElementIndex);
            if (dot == -1)
                dot = 0;
            extWithDot = file.substring(dot, pathElementIndex);
        }

        String extWithoutDot = extWithDot.substring(1);

        String type = config.getMimeType(extWithoutDot);

        if (type == null)
            type = config.getMimeType(extWithDot);

        if (type != null)
            return type;
        return ((WebGroup) parent).getMimeType(extWithDot, extWithoutDot);
    }

    /**
     * @see com.ibm.ws.webcontainer.webapp.WebAppContext#getConfiguration()
     */
    public WebAppConfiguration getConfiguration() {
        return this.config;
    }

    public Set getResourcePaths(String path) {
    	return getResourcePaths(path,true);
    }
    
    /**
     * @see com.ibm.ws.webcontainer.webapp.WebAppContext#getResourcePaths(String)
     */
    public Set getResourcePaths(String path, boolean searchMetaInf) {
        
    	if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
            logger.entering(CLASS_NAME, "getResourcePaths", "path->[" + path + "] searchMetaInf = " + searchMetaInf);
        
        HashSet set = new HashSet();

        // get the root path
        java.io.File root = new java.io.File(getDocumentRoot() + path);

        if (root.exists()) {
            // list the files in the root
            java.io.File[] fileList = root.listFiles();

            if (fileList != null) {
                for (int i = 0; i < fileList.length; i++) {
                    String resourcePath = fileList[i].getPath();
                    resourcePath = resourcePath.substring(getDocumentRoot().length());
                    resourcePath = resourcePath.replace('\\', '/');
                    if (fileList[i].isDirectory()) {
                        if (resourcePath.endsWith("/") == false) {
                            resourcePath += "/";
                        }
                    }

                    set.add(resourcePath);
                }
            }
        }
        
        // PM21451 Start
        // search the static doc roots and include a search of meta-inf resources if boolean parameter is true
        set.addAll(getStaticDocumentRootUtils().getResourcePaths(path,searchMetaInf));
        
        // look at the JSP doc roots but don't search meta-inf resources this time (meta-inf resources are common
        // to both doc roots).
        set.addAll(getJSPDocumentRootUtils().getResourcePaths(path,false));
        // PM21451 End
        
    	if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
            logger.exiting(CLASS_NAME, "getResourcePaths", "size of set = " + set.size());
        
        return (set);
    }

    /**
     * @see com.ibm.ws.webcontainer.webapp.WebAppContext#getServletContextName()
     */
    public String getServletContextName() {
        return this.config.getDisplayName();
    }

    /**
     * @see com.ibm.ws.webcontainer.webapp.WebAppContext#getApplicationName()
     */
    public String getApplicationName() {
        return this.applicationName;
    }

    /**
     * @see com.ibm.ws.webcontainer.webapp.WebAppContext#getSessionContext()
     */
    public IHttpSessionContext getSessionContext() {
        return this.sessionCtx;
    }

    /**
     * @see javax.servlet.ServletContext#getAttribute(String)
     */
    public Object getAttribute(String name) {
        return this.attributes.get(name);
    }

    /**
     * @see javax.servlet.ServletContext#getAttributeNames()
     */
    public Enumeration getAttributeNames() {
        // return new
        // IteratorEnumerator(((HashMap)(((HashMap)attributes).clone())).keySet().iterator());
        // PK27027 - We have to create a new HashMap since the attributes
        // HashMap is synchronized and it cannot be cloned.
        HashMap tmpAttributes = new HashMap(attributes.size());
        tmpAttributes.putAll(attributes);

        return new IteratorEnumerator(tmpAttributes.keySet().iterator());
    }

    /**
     * @see javax.servlet.ServletContext#getContext(String)
     */
    public ServletContext getContext(String path) {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
            logger.logp(Level.FINE, CLASS_NAME, "getContext", "path->[" + path + "]");
        // Begin 377689.1 revert back to best match getContext
        WebApp s = (WebApp) ((WebGroup) parent).findContext(path);
        if (s != null) {
            return s.getFacade();
        }
        return null;
    }

    /**
     * @see javax.servlet.ServletContext#getInitParameter(String)
     */
    public String getInitParameter(String name) {
    	String value = (String) this.config.getContextParams().get(name);
    	if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE) == true)
        {
            logger.logp(Level.FINE, CLASS_NAME,"getInitParameter", "name->"+name+"value->"+value);
        }
        return value;
    }

    /**
     * @see javax.servlet.ServletContext#getInitParameterNames()
     */
    public Enumeration getInitParameterNames() {
        return new IteratorEnumerator(((HashMap) (config.getContextParams().clone())).keySet().iterator());
    }

    /**
     * @see javax.servlet.ServletContext#getMajorVersion()
     */
    public int getMajorVersion() {
        return 3;
    }

    /**
     * @see javax.servlet.ServletContext#getMinorVersion()
     */
    public int getMinorVersion() {
        return 0; // 398349
    }

    public abstract WebAppDispatcherContext createDispatchContext();

    /**
     * @see javax.servlet.ServletContext#getNamedDispatcher(String)
     */
    public RequestDispatcher getNamedDispatcher(String name) {
        IServletWrapper w;
        try {
            // w = getServletWrapper(name); // PK61140
            w = getServletWrapper(name, true); // PK61140
        } catch (Exception wce) {
            w = null;
        }

        if (w == null)
            return null;

        // begin PK07351 6021Request dispatcher could not be reused as it was in
        // V5. WAS.webcontainer
        // WebAppDispatcherContext dispatchContext = createDispatchContext();
        // RequestDispatcher ward = new WebAppRequestDispatcher(this, w,
        // dispatchContext);
        RequestDispatcher ward = new WebAppRequestDispatcher(this, w);
        // end PK07351 6021Request dispatcher could not be reused as it was in
        // V5. WAS.webcontainer

        return ward;
    }

    public WebModuleMetaData getModuleMetaData() {
        return this.config.getMetaData();
    }

    /**
     * @see javax.servlet.ServletContext#getRequestDispatcher(String)
     */
    public RequestDispatcher getRequestDispatcher(String path) {
        if (path != null) {
            if (!path.startsWith("/"))
                path = "/" + path;

            String uri = WebGroup.stripURL(path);

            RequestProcessor p = requestMapper.map(uri);

            if (p == null)
                return null;

            // begin PK07351 6021Request dispatcher could not be reused as it
            // was in V5. WAS.webcontainer
            /*
             * WebAppDispatcherContext dispatchContext =
             * createDispatchContext();
             * dispatchContext.setRequestURI((contextPath.equals("/")) ? path :
             * this.contextPath + path);
             * 
             * int qMark = path.indexOf('?'); if (qMark != -1) path =
             * path.substring(0, qMark);
             * 
             * return new WebAppRequestDispatcher(this, path, dispatchContext);
             */
            return new WebAppRequestDispatcher(this, path);
            // end PK07351 6021Request dispatcher could not be reused as it was
            // in V5. WAS.webcontainer

        } else
            return null;
    }

    /**
     * @see javax.servlet.ServletContext#getServlet(String)
     * @deprecated
     */
    public Servlet getServlet(String arg0) throws ServletException {
        // as per spec return null
        return null;
    }

    /**
     * @see javax.servlet.ServletContext#getServletNames()
     * @deprecated
     */
    public Enumeration getServletNames() {
        return EmptyEnumeration.instance();
    }

    /**
     * @see javax.servlet.ServletContext#getServlets()
     * @deprecated
     */
    public Enumeration getServlets() {
        return EmptyEnumeration.instance();
    }

    /**
     * @see javax.servlet.ServletContext#log(Exception, String)
     * @deprecated
     */
    public void log(Exception th, String message) {
        ServletException e = null;

        if (th instanceof ServletException) {
            e = (ServletException) th;

            while (e != null) {
                th = (Exception) e.getRootCause();

                if (th == null) {
                    th = e;
                    break;
                } else
                    e = th instanceof ServletException ? (ServletException) th : null;
            }
        }

        // log the error
        Object[] args = { message, th };

        // pk435011
        logger.logp(Level.SEVERE, CLASS_NAME, "log", "log.servlet.error", args);
    }

    /**
     * @see javax.servlet.ServletContext#removeAttribute(String)
     */
    public void removeAttribute(String arg0) {
    	if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
            logger.logp(Level.FINE, CLASS_NAME, "removeAttribute", "name [{0}]", new Object [] {name});
        }
        Object o = attributes.remove(arg0);

        this.notifyServletContextAttrRemoved(arg0, o);

        // TODO: check WebAppBean stuff
    }

    /**
     * @see javax.servlet.ServletContext#setAttribute(String, Object)
     */
    public void setAttribute(String name, Object value) {
        // TODO: check is WebAppBean stuff is needed
        // or add BeanContextChild elements..
    	
    	if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
            logger.logp(Level.FINE, CLASS_NAME, "setAttribute", "name [{0}], value [{1}]", new Object [] {name,value});
        }

        if (attributes.containsKey(name)) {
            Object oldValue = attributes.put(name, value);
            this.notifyServletContextAttrReplaced(name, oldValue);
        } else {
            attributes.put(name, value);
            this.notifyServletContextAttrAdded(name, value);
        }
    }

    /**
     * Method sortByStartUpWeight. Sorts the servlets in the order of their
     * startUp weight. This way, we can iterate the list, and call their
     * respective init() methods if loadAtStartUp() is true, without worrying
     * about the order.
     * 
     * @param iterator
     * @return Iterator
     */
    protected Iterator<IServletConfig> sortNamesByStartUpWeight(Iterator<IServletConfig> iterator) {

        // System.out.println("in sort");
        int min = Integer.MAX_VALUE;
        int maxpos = 0;

        sortedServletConfigs = new ArrayList<IServletConfig>();
        while (iterator.hasNext()) {
            IServletConfig sc = iterator.next();
            addToStartWeightList(sc);
        }

        return sortedServletConfigs.iterator();

    }

    /**
     * Method addToStartWeightList.
     */
    public void addToStartWeightList(IServletConfig sc) {
        // we haven't started sorting the startup weights yet so just ignore. It
        // will be added later.
        if (this.sortedServletConfigs == null)
            return;
        int size = this.sortedServletConfigs.size();
        int pos = 0;
        boolean added = false;

        if (size == 0 || !sc.isLoadOnStartup())
            sortedServletConfigs.add(sc);
        else {
            // remove the current entry if it was already added
            if (sc.isAddedToLoadOnStartup() && sc.isWeightChanged())
                sortedServletConfigs.remove(sc);

            int value = sc.getStartUpWeight();

            for (IServletConfig curServletConfig : sortedServletConfigs) {
                int curStartupWeight = curServletConfig.getStartUpWeight();
                if (value < curStartupWeight || !curServletConfig.isLoadOnStartup()) {
                    sortedServletConfigs.add(pos, sc);
                    added = true;
                    break;
                }
                pos++;
            }

            if (!added)
                sortedServletConfigs.add(sc);
        }
        sc.setAddedToLoadOnStartup(true);

    }

    public void destroy() {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
            logger.logp(Level.FINE, CLASS_NAME, "destroy", "entry");
        // 325429 BEGIN
        if (destroyed.booleanValue()) {
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
                logger.logp(Level.FINE, CLASS_NAME, "destroy", "WebApp {0} is already destroyed", applicationName);
            return;
        }
        destroyed = Boolean.TRUE;
        ClassLoader origClassLoader = null;
        // 325429 END
        try {
        	
        	origClassLoader = ThreadContextHelper.getContextClassLoader();
            final ClassLoader warClassLoader = getClassLoader();
            if (warClassLoader != origClassLoader)
            {
                ThreadContextHelper.setClassLoader(warClassLoader);
            }
            else
            {
                origClassLoader = null;
            }
        	
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
                logger.logp(Level.FINE, CLASS_NAME, "destroy", "WebApp {0} is destroying", applicationName);
            webAppNameSpaceCollab.preInvoke(((WebModuleMetaData)getModuleMetaData()).getCollaboratorComponentMetaData());
            notifyStop();

            Iterator targets = requestMapper.targetMappings();

            // The super class lifecycle handling will not work for
            // ServletWrappers
            // because it doesn't extend BaseContainer. Hence we will explictly
            // destroy each ServletWrapper

            while (targets.hasNext()) {
                RequestProcessor p = (RequestProcessor) targets.next();

                if (p instanceof IServletWrapper)
                    ((IServletWrapper) p).destroy();
            }

            super.destroy();

            if (filterManager!=null && filterManager.areFiltersDefined())
                filterManager.shutdown();

            Enumeration enumServletNames = new IteratorEnumerator(config.getServletNames());

            eventSource.onApplicationEnd(new ApplicationEvent(this, this, enumServletNames));
            if (sessionCtx!=null) {
                sessionCtx.stop(name);
            }

            eventSource.onApplicationUnavailableForService(new ApplicationEvent(this, this, enumServletNames));

            jspClassLoadersMap = null; // PK50133

            notifyServletContextDestroyed();

            config = null;

        } catch (Throwable th) {
            Object[] vals = { this.getName(), th };
            // pk435011
            logger.logp(Level.SEVERE, CLASS_NAME, "destroy", "WebApp.destroy.encountered.errors", vals);
            com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(th, CLASS_NAME + ".destroy", "2459", this);
        } finally {
        	
            webAppNameSpaceCollab.postInvoke();
            
            if (origClassLoader != null) 
            {
                final ClassLoader fOrigClassLoader = origClassLoader;
			
                ThreadContextHelper.setClassLoader(fOrigClassLoader);
            }
        }

        destroyListeners(new ArrayList[] { servletContextListeners, servletContextLAttrListeners, servletRequestListeners,
                servletRequestLAttrListeners, sessionListeners, sessionAttrListeners, sessionActivationListeners, sessionBindingListeners });

        // Begin 299205, Collaborator added in extension processor recieves no
        // events
        finishDestroyCleanup();
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
            logger.logp(Level.FINE, CLASS_NAME, "destroy", "exit");
    }

    protected void destroyListeners(ArrayList listeners[]) {
    }

    protected void finishDestroyCleanup() {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
            logger.logp(Level.FINE, CLASS_NAME, "finishDestroyCleanup", "core WebApp {finishDestroyCleanup()", applicationName);
        requestMapper = null;
        this.config = null;
        this.loader = null; // PK17371
        this.sessionCtx = null;
        this.defaultExtProc = null;
        // parent.removeSubContainer(name); //PK37449
    }

    // End 299205, Collaborator added in extension processor recieves no events

    /**
     * Method sendError.
     * 
     * @param req
     * @param res
     * @param error
     */
    public void sendError(HttpServletRequest req, HttpServletResponse res, ServletErrorReport error) {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
            logger.entering(CLASS_NAME, "sendError", "error :" + error.getMessage());

        req.setAttribute("javax.servlet.jsp.jspException", error);
        
        //PK82794
        if(WCCustomProperties.SUPPRESS_LAST_ZERO_BYTE_PACKAGE)
            WebContainerRequestState.getInstance(true).setAttribute("com.ibm.ws.webcontainer.suppresslastzerobytepackage", "true");  
        //PK82794

        if (!res.isCommitted())
            res.resetBuffer();

        String servletName = error.getTargetServletName();

        // begin PK04668 IF THE CLIENT THAT MADE THE SERVLET REQUEST GOES
        // DOWN,THERE IS WAS.webcontainer
        Throwable rootCause = error.getRootCause();
        if (rootCause instanceof ClosedConnectionException) {
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
                logger.logp(Level.FINE, CLASS_NAME, "sendError",
                        "sendError occured as a result of ClosedConnectionException. skip sending error page to client");
                if (rootCause.getCause() != null)
                    logger.logp(Level.FINE, CLASS_NAME, "sendError", "cause of closed connection", rootCause.getCause());
            }
            eventSource.onServletServiceError(new ServletErrorEvent(this, this, servletName, null, error));
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
                logger.exiting(CLASS_NAME, "sendError", "ClosedConnectionException");
            return;
        } else if (rootCause instanceof javax.servlet.UnavailableException) {
            // end PK04668 IF THE CLIENT THAT MADE THE SERVLET REQUEST GOES
            // DOWN,THERE IS WAS.webcontainer
            // 198256 - begin
            // if (error instanceof WebAppErrorReport)
            // {
            // ((WebAppErrorReport)
            // error).setErrorCode(HttpServletResponse.SC_NOT_FOUND);
            // }
            // 198256 - end
            this.eventSource.onServletServiceDenied(new ServletErrorEvent(this, this, servletName, null, error));
        }

        if (error.getErrorCode() >= 500) {
            if (servletName == null)
                // Defect 211450
                logError(error.getUnencodedMessage(), error);
            else {
                logServletError(servletName, "", error);
                eventSource.onServletServiceError(new ServletErrorEvent(this, this, servletName, null, error));
            }
        }

        if (req.getAttribute(ServletErrorReport.ATTRIBUTE_NAME) != null) {
            // we are in a recursive situation...report a recursive error
            ServletErrorReport oError = (ServletErrorReport) req.getAttribute(ServletErrorReport.ATTRIBUTE_NAME);
            reportRecursiveError(req, res, oError, error);
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
                logger.exiting(CLASS_NAME, "sendError", "recursive error");
            return;
        }

        // set the error code in the response
        try {
            // if the error code hasn't been set, set it to server error
            if (error.getErrorCode() < 100 || error.getErrorCode() > 599) {
                error.setErrorCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

            // d124698 - set the status code attribute in the request

            req.setAttribute(javax.servlet.RequestDispatcher.ERROR_STATUS_CODE, new Integer(error.getErrorCode()));

            // set the response status
            res.setStatus(error.getErrorCode());

            // We have to determine the charset to use with the error page
            String clientEncoding = req.getCharacterEncoding();
            // PK21127 start
            if (clientEncoding != null && !EncodingUtils.isCharsetSupported(clientEncoding)) {
                // charset not supported, continue with the logic to determine
                // the encoding
                clientEncoding = null;
            }
            // PK21127 end
            if (clientEncoding == null)
                clientEncoding = com.ibm.wsspi.webcontainer.util.EncodingUtils.getEncodingFromLocale(req.getLocale());
            if (clientEncoding == null)
                clientEncoding = System.getProperty("default.client.encoding");
            if (clientEncoding == null)
                clientEncoding = "ISO-8859-1";

            res.setContentType("text/html;charset=" + clientEncoding);
        } catch (IllegalStateException ise) {
            // failed to set status code.
            // This could be caused by:
            // 1. the servlet is being included.
            // 2. the stream response is already committed
            com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(ise, CLASS_NAME + ".handleError", "865", this);
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
                logger.logp(Level.FINE, CLASS_NAME, "sendError", "WebApp.sendError() failed to set status code.\n"
                        + "This may be caused by a servlet calling response.sendError() "
                        + "while being included or after the response has already been committed to the client.", ise);
        }

        // set the exception as an error bean in the response
        req.setAttribute(ServletErrorReport.ATTRIBUTE_NAME, error);

        // get this request's error page dispatcher
        RequestDispatcher rd = getErrorPageDispatcher(req, error);

        // PK37608 Start
        if (WCCustomProperties.SUPPRESS_WSEP_HEADER) {

            Enumeration ViaHeaderValues = req.getHeaders("Via");

            if (ViaHeaderValues != null) {
                boolean foundODRHeader = false;
                while (!foundODRHeader && ViaHeaderValues.hasMoreElements()) {

                    String ViaHeaderValue = (String) ViaHeaderValues.nextElement();

                    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
                        logger.logp(Level.FINE, CLASS_NAME, "sendError", "Via Header value : " + ViaHeaderValue);
                    }

                    if (ViaHeaderValue.indexOf("On-Demand Router") != -1) {

                        foundODRHeader = true;

                        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
                            logger.logp(Level.FINE, CLASS_NAME, "sendError", "Via Header with On-Demand Router found, add $WSEP header to response.");
                        }

                        res.addHeader("$WSEP", "");

                    }
                }

                if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE) && foundODRHeader == false) {
                    logger.logp(Level.FINE, CLASS_NAME, "sendError",
                            "No Via header found with On-Demand Router. Do not add $WSEP header to response.");
                }

            } else if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
                logger.logp(Level.FINE, CLASS_NAME, "sendError", "No Via Header, do not add $WSEP header to response.");
            }

        } else {

            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
                logger.logp(Level.FINE, CLASS_NAME, "sendError", "Add $WSEP header to response.");
            }

            res.addHeader("$WSEP", "");
        }
        // PK37608 End

        try {
            if (rd == null) {
                // PK23428 BEGIN
                try {

                    PrintWriter pw = res.getWriter();
            		//PM03788 Start                                                                	
            		if( WCCustomProperties.SET_UNENCODED_HTML_IN_SENDERROR){                                 		   	                                                pw.println(error.getUnencodedMessageAsHTML());
            		}
           			else {
                    pw.println(error.getMessageAsHTML()); // Defect 211450
            		}//PM03788 End
                } catch (IllegalStateException ise) {
                    ServletOutputStream os = res.getOutputStream();
            		//PM03788 Start   
            		if( WCCustomProperties.SET_UNENCODED_HTML_IN_SENDERROR){ 
                    		os.println(error.getUnencodedMessageAsHTML());
            		}
            		else {
                    os.println(error.getMessageAsHTML());
            		}//PM03788 End                             
                }
                // PK23428 END
            } else {
            	
            	
            	
                try {
                    // first attempt to forward (this permits setting of
                    // headers)
                    // res.setStatus(200);
                    rd.forward(req, res);
                    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
                        logger.exiting(CLASS_NAME, "sendError", "after forward");
                    return;
                } catch (IllegalStateException ise) {
                    // include the error handler
                    rd.include(req, res);
                    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
                        logger.exiting(CLASS_NAME, "sendError", "after include");
                    return;
                }

            }
        } catch (Throwable th) {
            com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(th, CLASS_NAME + ".handleError", "912", this);
            // logger.logp(Level.SEVERE, CLASS_NAME,"sendError",
            // "Error.occurred.while.invoking.error.reporter", new Object[] {
            // error, th });

            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
                logger.logp(Level.FINE, CLASS_NAME, "sendError", "Error occurred while invoking error reporter");
                logger.logp(Level.FINE, CLASS_NAME, "sendError", "URL: " + req.getRequestURL());
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);

                if (th instanceof ServletException) {
                    DefaultErrorReporter.printFullStackTrace(pw, (ServletException) th);
                } else {
                    th.printStackTrace(pw);
                }

                pw.flush();
                logger.logp(Level.FINE, CLASS_NAME, "sendError", "Full Exception dump of original error", sw.toString());
                sw = new StringWriter();
                pw = new PrintWriter(sw);
                if (th instanceof ServletException) {
                    DefaultErrorReporter.printFullStackTrace(pw, (ServletException) th);
                } else {
                    th.printStackTrace(pw);
                }

                pw.flush();
                logger.logp(Level.FINE, CLASS_NAME, "sendError", "Full Exception dump of recursive error", sw.toString());
            }

            reportRecursiveError(req, res, error, new WebAppErrorReport(th));
        }

        try {
            // reset the error bean object
            req.setAttribute(ServletErrorReport.ATTRIBUTE_NAME, null);

            // 101639 reset the jsp exception object
            req.setAttribute("javax.servlet.jsp.jspException", null);
        } catch (Throwable th) {
            /* ignore */
            com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(th, CLASS_NAME + ".handleError", "961", this);
        }
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
            logger.exiting(CLASS_NAME, "sendError");

    }

    // begin PK04177 CONTENTS UNDER WEBMODULE WEB-INF DIRECTORY ARE ACCESSIBLE
    // WAS.webcontainer: rewritten
    private boolean isForbidden(String uri) {
        String reqUri = uri.toUpperCase();
        reqUri = removeLeadingSlashes(reqUri);

        if (reqUri == null) {
            return false;
        }

        // As per spec (servlet 2.4), deny access to WEB-INF
        if (reqUri.startsWith("WEB-INF/") || reqUri.startsWith("META-INF/"))
            return true;
        else if ((reqUri.equals("WEB-INF") || reqUri.equals("META-INF"))) {
            return true;
        } else {
            return false;
        }
    }

    /*
     * removeLeadingSlashes -- Removes all slashes from the head of the input
     * String.
     */
    private String removeLeadingSlashes(String src) {
        String result = null;
        int i = 0;
        boolean done = false;

        if (src == null)
            return null;

        int len = src.length();
        while ((!done) && (i < len)) {
            if ((src.charAt(i) == '/') || (src.charAt(i) == ' ')) {
                i++;
            } else {
                done = true;
            }
        }

        // If all slashes were stripped off and there was no remainder, then
        // return null.
        if (done) {
            result = src.substring(i);
        }

        return result;
    }

    // end PK04177 CONTENTS UNDER WEBMODULE WEB-INF DIRECTORY ARE ACCESSIBLE
    // WAS.webcontainer: rewritten

    /**
     * @param req
     * @param ser
     * @return RequestDispatcher
     */
    public RequestDispatcher getErrorPageDispatcher(ServletRequest req, ServletErrorReport ser) {

        String errorURL = null;

        // Get and Set error request attributes.
        Integer errorCode = new Integer(ser.getErrorCode());
        Class errorException = ser.getExceptionClass();
        String errorMessage = ser.getMessage();

        // 114582 - begin - get down to the root servlet exception
        ServletException sx = (ServletException) ser;
        Throwable th = sx.getRootCause();
        boolean isPreV7 = false;

        if (WCCustomProperties.ERROR_PAGE_COMPATIBILITY != null && WCCustomProperties.ERROR_PAGE_COMPATIBILITY.equalsIgnoreCase("V6")) {
            isPreV7 = true;
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
                logger.logp(Level.FINE, CLASS_NAME, "getErrorPageDispatcher", "is PreV7");
            while (th != null && th instanceof ServletException && ((ServletException) th).getRootCause() != null) { // defect
                // 155880
                // -
                // Check
                // rootcause
                // !=
                // null
                sx = (ServletException) th;
                th = sx.getRootCause();
            }
            // 114582 - end
        } else {
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
                logger.logp(Level.FINE, CLASS_NAME, "getErrorPageDispatcher", "not PreV7");
            while (th != null && th instanceof ServletErrorReport && ((ServletErrorReport) th).getRootCause() != null) {
                sx = (ServletException) th;
                th = sx.getRootCause();
            }
        }

        // 114582 - only set exception type if root exception is not null
        if (th != null) {
            if (errorException != null) {
                req.setAttribute("javax.servlet.error.exception_type", errorException);
                // req.setAttribute("javax.servlet.error.exception_type",
                // Class.forName(errorException));
            }
        }

        if (errorMessage != null) {
            req.setAttribute("javax.servlet.error.message", errorMessage);
        }

        // LIDB1234.5 - begin - set new attributes for Servlet 2.3

        // 114582 - begin - add ser if root cause is null
        // add the exception to the request object
        if (th != null) {
            req.setAttribute("javax.servlet.error.exception", th);
        }
        // 114582 - end

        // add the request uri to the request object
        HttpServletRequest httpServletReq = (HttpServletRequest) ServletUtil.unwrapRequest(req,HttpServletRequest.class);
        req.setAttribute("javax.servlet.error.request_uri", httpServletReq.getRequestURI());

        WebContainerRequestState.getInstance(true).setAttribute("isErrorDispatcherType", "true");
        
        // add the target servlet name to the request object
        if (ser.getTargetServletName() != null) {
            req.setAttribute("javax.servlet.error.servlet_name", ser.getTargetServletName());
        } else {
            IExtendedRequest wsRequest = (IExtendedRequest) ServletUtil.unwrapRequest(req);
            WebAppDispatcherContext ctxt = (WebAppDispatcherContext) wsRequest.getWebAppDispatcherContext();
            RequestProcessor processor = ctxt.getCurrentServletReference();
            if (processor != null) {
                String name = processor.getName();
                req.setAttribute("javax.servlet.error.servlet_name", name);
            }
        }

        // LIDB1234.5 - end

        // Determine if the error that has occurred has a defined ErrorPage to
        // call. If not,
        // then call the DefaultErrorReporter.
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
            logger.logp(Level.FINE, CLASS_NAME, "getErrorPageDispatcher", "Looking for defined Error Page!");
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
            logger.logp(Level.FINE, CLASS_NAME, "getErrorPageDispatcher", "Exception errorCode=" + errorCode);
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
            logger.logp(Level.FINE, CLASS_NAME, "getErrorPageDispatcher", "Exception type=" + errorException);
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
            logger.logp(Level.FINE, CLASS_NAME, "getErrorPageDispatcher", "Exception message=" + errorMessage);

        ErrorPage ep = config.getErrorPageByErrorCode(errorCode);
        // PK55149 - STARTS
        if (errorExceptionTypeFirst) {
            ErrorPage exceptionErrorPage = null;
            if (th != null) {
                if (isPreV7)
                    exceptionErrorPage = config.getErrorPageByExceptionType(th);
                else
                    exceptionErrorPage = config.getErrorPageTraverseRootCause(th);
                if (exceptionErrorPage != null) {
                    errorURL = exceptionErrorPage.getLocation();
                    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
                        logger.logp(Level.FINE, CLASS_NAME, "getErrorPageDispatcher", "Found exception-type=" + errorException + " with location="
                                + errorURL);
                }
            }
            if (exceptionErrorPage == null && ep != null) {
                errorURL = ep.getLocation();
                if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
                    logger.logp(Level.FINE, CLASS_NAME, "getErrorPageDispatcher", "Found error-code=" + errorCode + " with location=" + errorURL);
            }
        } else {
            // PK55149 - ENDS
            if (ep != null) {
                errorURL = ep.getLocation();
                if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
                    logger.logp(Level.FINE, CLASS_NAME, "getErrorPageDispatcher", "Found error-code=" + errorCode + " with location=" + errorURL);
            } else if (th != null) {
                if (isPreV7)
                    ep = config.getErrorPageByExceptionType(th);
                else
                    ep = config.getErrorPageTraverseRootCause(th);
                if (ep != null) {
                    errorURL = ep.getLocation();
                    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
                        logger.logp(Level.FINE, CLASS_NAME, "getErrorPageDispatcher", "Found exception-type=" + errorException + " with location="
                                + errorURL);
                }
            }
        } // PK55149

        // If no Error Page was defined for the error, then use the
        // <default-error-page>
        // if one has been defined.
        if (errorURL == null) {
            if (ser.getErrorCode() != 403) {
                errorURL = config.getDefaultErrorPage();

                if (errorURL == null || errorURL.equals(""))
                    return null;
            } else {
                return null;
            }
        }

        if (!errorURL.startsWith("/")) {
            errorURL = "/" + errorURL;
        }

        // WebAppDispatcherContext dispatchContext = new
        // WebAppDispatcherContext(this);
        // dispatchContext.setRelativeUri(errorURL);

        return getRequestDispatcher(errorURL);
    }

    /**
     * @param req
     * @param res
     * @param oError
     * @param error
     */
    private void reportRecursiveError(ServletRequest req, ServletResponse res, ServletErrorReport originalErr, ServletErrorReport recurErr) {

        try {
            String message = error_nls.getString("error.page.exception", "Error Page Exception");

            // log("Original Error: ", originalErr);
            Object[] args = { getName(), this.getContextPath(), message, recurErr };
            logger.logp(Level.SEVERE, CLASS_NAME, "reportRecursiveError", message+":", args);
            
            PrintWriter out;
            try {
                out = res.getWriter();
            } catch (IllegalStateException e) {
                com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(e, CLASS_NAME + ".reportRecursiveError", "985", this);
                out = new PrintWriter(new OutputStreamWriter(res.getOutputStream(), res.getCharacterEncoding()));
            }
            if (!WCCustomProperties.SUPPRESS_HTML_RECURSIVE_ERROR_OUTPUT) { // PK77421
                out
                        .println("<H1>"
                                + message
                                + "</H1>\n<H4>"
                                + nls
                                        .getString("cannot.use.error.page",
                                                "The server cannot use the error page specified for your application to handle the Original Exception printed below.") // 406426
                                + "</H4>");
                out.println("<BR><H3>" + error_nls.getString("original.exception", "Original Exception") + ": </H3>"); // 406426
                printErrorInfo(out, originalErr);
                out.println("<BR><BR><H3>" + error_nls.getString("error.page.exception", "Error Page Exception") + ": </H3>"); // 406426
                printErrorInfo(out, recurErr);
                out.flush();
            }
        } catch (Throwable th) {
            com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(th, CLASS_NAME + ".reportRecursiveError", "998", this);
            log("Unable to report exception to client", th);
        }
    }

    /**
     * @param out
     * @param originalErr
     */
    private void printErrorInfo(PrintWriter out, ServletErrorReport e) throws IOException {
        out.println("<B>" + error_nls.getString("error.message", "Error Message") + ": </B>" + e.getMessage() + "<BR>"); // 406426
        out.println("<B>" + error_nls.getString("error.code", "Error Code") + ": </B>" + e.getErrorCode() + "<BR>"); // 406426
		out.println("<B>"+error_nls.getString("target.servlet", "Target Servlet")+": </B>" 
			     + DefaultErrorReporter.encodeChars(e.getTargetServletName()) + "<BR>"); //406426 //PM18512, encoded		
        out.println("<B>" + error_nls.getString("error.stack", "Error Stack") + ": </B><BR>"); // 406426

        DefaultErrorReporter.printShortStackTrace(out, e);
    }

    /**
     * @see com.ibm.ws.core.RequestProcessor#handleRequest(IWCCRequest,
     *      IWCCResponse)
     */
    public void handleRequest(ServletRequest request, ServletResponse response) throws Exception {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
            logger.entering(CLASS_NAME, "handleRequest");

        IExtendedRequest req = (IExtendedRequest) request;
        Response res = (Response) response;
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
            logger.logp(Level.FINE, CLASS_NAME, "handleRequest", "URI --> " + req.getRequestURI() + " handled by WebApp --> " + applicationName);
        }
        WebAppDispatcherContext dispatchContext = (WebAppDispatcherContext) req.getWebAppDispatcherContext();
        String fullUri = dispatchContext.getDecodedReqUri(); // 280335, do not
        // decode again
        // since done in
        // VirtualHost
        String partialUri = fullUri;

        if (!contextPath.equals("/")) {
            int index = 0;
            if (contextPath.endsWith("/*")) {
                index = contextPath.length() - 1;
            } else {
                index = contextPath.length();
            }

            partialUri = fullUri.substring(index); // .trim()

            // BEGIN PK27974

            if (WebApp.redirectContextRoot && (partialUri.length() == 0) && (req instanceof HttpServletRequest)) {
                // PK79143 Start
                // dispatchContext.sendRedirect (((HttpServletRequest)
                // req).getRequestURL() + "/");
                ((HttpServletResponse) res).sendRedirect(((HttpServletRequest) req).getRequestURL() + "/");
                // PK79143 End

                if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
                    logger.exiting(CLASS_NAME, "handleRequest");
                return;
            }

            // END PK27974
        }

        if (partialUri.length() == 0)
            partialUri = "/";
        dispatchContext.setWebApp(this);
        dispatchContext.setRelativeUri(partialUri);

        if (isForbidden(partialUri)) {
            WebAppErrorReport ser = new WebAppErrorReport(new ServletException(MessageFormat.format(nls.getString("File.not.found",
                    "File not found: {0}"), new Object[] { partialUri })));
            ser.setErrorCode(HttpServletResponse.SC_NOT_FOUND);
            if (req instanceof HttpServletRequest) {
                sendError((HttpServletRequest) req, (HttpServletResponse) res, (WebAppErrorReport) ser);
                if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
                    logger.exiting(CLASS_NAME, "handleRequest - sendError : Not allowed to access contents of WEB-INF/META-INF");
                return;
            } else {
                if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
                    logger.exiting(CLASS_NAME, "handleRequest - Not allowed to access contents of WEB-INF/META-INF");
                throw new ServletException("Not allowed to access contents of WEB-INF/META-INF");
            }
        }

        if (SecurityContext.isSecurityEnabled()) {
            // these form login related identifiers can occur anywhere in the
            // URI
            // hence this is the only place to check for them.
            String servletPath = null; // PK79894
            String pathInfo = null; // PK79894
            if (fullUri.indexOf("j_security_check") != -1 && (!WCCustomProperties.ENABLE_EXACT_MATCH_J_SECURITY_CHECK || fullUri.endsWith("/j_security_check") ))  //F011107
			{
                // PK79894 Start
                // Note: setting the servletPath to "/j_security check" is arguably wrong
                // for a request such aa /<context-root>/a/b/j_security_check which will be 
                // processed as a "j_ecurity_check". However such a request is not valid
                // according to the servlet specification (SRV 12.5.3.1).  Further the
                // customer who requested PK79894 did not complain about the servletPath
                // being incorrect. As a result it was decided not to change the servletPath 
                // setting in PK95461 but leave it hard-coded as "j_security_check" 
                // 
                servletPath = "/j_security_check"; // Get servletPath
                pathInfo = getPathInfoforSecureloginlogout(fullUri, "j_security_check"); // Get
                // PathInfo
                dispatchContext.setPathElements(servletPath, pathInfo);
                if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
                    logger.logp(Level.FINE, CLASS_NAME, "handleRequest", "security is enabled, URI = " + fullUri + " , servletPath = "
                            + ((HttpServletRequest) req).getServletPath() + " , pathInfo = " + req.getPathInfo());
                }// PK79894 End
                
                // PK95461 - setPathElements sets the relativeURI to servletPath+pathInfo
                // which will be wrong if the URI was something like /<context-root>/a/b/j_security check
                // This is problematic because the relativeURI is used for filter mappring, so
                // reset the requestURI
                dispatchContext.setRelativeUri(partialUri);
                
                ExtensionProcessor p = getLoginProcessor();
                if (p != null) {
                    if (isFiltersDefined()) {
                        EnumSet<CollaboratorInvocationEnum> filterCollabEnum = EnumSet.of(CollaboratorInvocationEnum.CLASSLOADER, CollaboratorInvocationEnum.SESSION, CollaboratorInvocationEnum.EXCEPTION);
                        filterManager.invokeFilters((HttpServletRequest) req, (HttpServletResponse) res, this, p, filterCollabEnum);
                    } else {
                        p.handleRequest(req, res);
                    }
                    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
                        logger.exiting(CLASS_NAME, "handleRequest");
                    return;
                }
            } else if (fullUri.indexOf("ibm_security_logout") != -1) {
                //PK79894 Start                             
                // Note: setting the servletPath to "ibm_security_logout" is arguably wrong
                // for a request such aa /<context-root>/a/b/ibm_security_logout which will be 
                // processed as a "ibm_security_logout". However, the customer who requested
                // PK79894 did not complain about the servletPath being incorrect. As a result
                // it was decided not to change the servletPath setting in PK95461 but leave
                // it hard-coded as "ibm_security_logout" 
                // 
                servletPath = "/ibm_security_logout"; // Get servletPath
                pathInfo = getPathInfoforSecureloginlogout(fullUri, "ibm_security_logout"); // Get
                // PathInfo
                dispatchContext.setPathElements(servletPath, pathInfo);
                if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
                    logger.logp(Level.FINE, CLASS_NAME, "handleRequest", "security is enabled,  URI = " + fullUri + " , servletPath = "
                            + ((HttpServletRequest) req).getServletPath() + " , pathInfo = " + req.getPathInfo());
                }// PK79894 End
                
                // 610571 - setPathElements sets the relativeURI to servletPath+pathInfo
                // which will be wrong if the URI was something like /<context-root/a/b/ibm_security_logout
                // This is problematic because the relativeURI is used for filter mappring, so
                // reset the requestURI.
                dispatchContext.setRelativeUri(partialUri);
                
                ExtensionProcessor p = getLogoutProcessor();
                if (p != null) {
                    if (isFiltersDefined()) {
                        EnumSet<CollaboratorInvocationEnum> filterCollabEnum = EnumSet.of(CollaboratorInvocationEnum.NAMESPACE, CollaboratorInvocationEnum.CLASSLOADER, CollaboratorInvocationEnum.SECURITY, CollaboratorInvocationEnum.SESSION, CollaboratorInvocationEnum.EXCEPTION);
                        filterManager.invokeFilters((HttpServletRequest) req, (HttpServletResponse) res, this, p, filterCollabEnum);
                    } else {
                        p.handleRequest(req, res);
                    }
                    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
                        logger.exiting(CLASS_NAME, "handleRequest");
                    return;
                }
            }
        }

        RequestProcessor requestProcessor = requestMapper.map(req);

        try {
            req.start(); // TODO:This can be removed if we had servlet wrapper
            // returned to webcontainer
            res.start();

           

            filterManager
                    .invokeFilters((HttpServletRequest) req, (HttpServletResponse) res, this, requestProcessor, CollaboratorHelper.allCollabEnum);

            if (requestProcessor != null) {
                if (requestProcessor instanceof IServletWrapper) {
                    // 271276, do not add to cache if we have error status code
                    if (req.getAttribute(javax.servlet.RequestDispatcher.ERROR_STATUS_CODE) == null)
                        WebContainer.addToCache((HttpServletRequest) req, requestProcessor, this);
                }
            }
        } catch (Throwable th) {
        	handleException(th,req,res,requestProcessor);
        }
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
            logger.exiting(CLASS_NAME, "handleRequest");
        return;

    }
    
    public void handleException(Throwable th, ServletRequest req, ServletResponse res,RequestProcessor requestProcessor){
    	WebContainerRequestState reqState = WebContainerRequestState.getInstance(false);
        if (reqState!=null&&reqState.isAsyncMode()){
        	//Don't call execute next runnable because that will be done by WebContainer or DispatchRunnable exiting
        	//Don't check dispatching because we know its true and want to invoke error handling anyway because we are in control
        	//of when it is invoked.
        	 ListenerHelper.invokeAsyncErrorHandling(reqState.getAsyncContext(), reqState, th, AsyncListenerEnum.ERROR, ExecuteNextRunnable.FALSE,CheckDispatching.FALSE);      
        }
        else{
	    	if  (th instanceof ServletErrorReport) {
	            // Almost all exceptions from the wrapper will be caught here
	            this.sendError((HttpServletRequest) req, (HttpServletResponse) res, (ServletErrorReport)th);
	        } else {
	            // Just in case something unforeseen happens
	            // But first log in FFDC
	            com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(th, CLASS_NAME + ".handleRequest", "985", this);
	            WebAppErrorReport r = new WebAppErrorReport(th);
	            if (requestProcessor != null && requestProcessor instanceof ServletWrapper)
	                r.setTargetServletName(((ServletWrapper) requestProcessor).getServletName());
	            r.setErrorCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	            this.sendError((HttpServletRequest) req, (HttpServletResponse) res, r);
	        }
        }
    }
    

    // PK79894 Start
    // find pathInfo if associated alongwith servletPath(/j_security_check or
    // /ibm_security_logout ) in URI
    private String getPathInfoforSecureloginlogout(String currentURI, String securityString) {
        String pathInfo = null;
        int lastIndex_Security = currentURI.lastIndexOf(securityString);
        String restURI = currentURI.substring(lastIndex_Security + securityString.length());

        if (restURI.equals(""))
            pathInfo = null;
        else
            pathInfo = restURI;
        return pathInfo;
    }

    // PK79894 End

    /**
     * Returns the nameSpaceCollaborator.
     * 
     * @return WebAppNameSpaceCollaborator
     */
    public ICollaboratorHelper getWebAppCollaboratorHelper() {
        return this.collabHelper;
    }

    public IInvocationCollaborator[] getWebAppInvocationCollaborators() {
        return null;
    }

    /**
     * Method isFiltersDefined.
     * 
     * @return boolean
     */
    public boolean isFiltersDefined() {
        return this.filterManager.areFiltersDefined();
    }

    /**
     * Method getFilterManager.
     */
    public com.ibm.wsspi.webcontainer.filter.WebAppFilterManager getFilterManager() {
        return this.filterManager;

    }

    /**
     * @param sc
     * @return
     */
    public boolean isErrorPageDefined(int sc) {
        ErrorPage ep = config.getErrorPageByErrorCode(new Integer(sc));

        if (ep == null) {
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
                logger.logp(Level.FINE, CLASS_NAME, "isErrorPageDefined", "Could not locate custom error page for error code =" + sc);
            }
            return false;
        }
        String errorURL = ep.getLocation();
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
            logger.logp(Level.FINE, CLASS_NAME, "isErrorPageDefined", "Found error-code=" + sc + " with location=" + errorURL);
        }
        return true;
    }

    /**
     * @return
     */
    public List getWelcomeFileList() {
        return this.config.getWelcomeFileList();
    }

    /**
     * @return
     */
    public String getWebAppName() {
        return this.config.getDisplayName();
    }

    /*
     * Used by the following components to add servlets to the Webcontainer
     * runtime: (1) Portal Server (2) WebServices
     */
    public void addDynamicServlet(String servletName, String servletClass, String mappingURI, Properties initParameters) throws ServletException,
            SecurityException {

        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
            logger.entering(CLASS_NAME, "addDyanamicServlet");
            logger.logp(Level.FINE, CLASS_NAME, "addDynamicServlet", " servletName[" + servletName + "] servletClass [" + servletClass
                    + "] mappingURI [" + mappingURI + "]");
        }

        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(perm);
        }
        // make sure the servlet doesn't already exist
        IServletConfig sconfig = config.getServletInfo(servletName);

        if (sconfig == null) {
            // Adding a brand new servlet to the container
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
                logger.logp(Level.FINE, CLASS_NAME, "addDynamicServlet",
                        "Servlet not found in the web application configuration. Creating new config.");
            }
            sconfig = this.webExtensionProcessor.createConfig("DYN_" + servletName + "_" + System.currentTimeMillis()); // PK63920
            sconfig.setServletName(servletName);
            sconfig.setDisplayName(servletName);
            sconfig.setDescription("dynamic servlet " + servletName);
            sconfig.setClassName(servletClass);
            sconfig.setStartUpWeight(new Integer(1));
            sconfig.setServletContext(getFacade());

            // check the parameters
            if (initParameters == null) {
                initParameters = new Properties();
            } else
                sconfig.setInitParams(initParameters);

            // add to the config
            config.addServletInfo(servletName, sconfig);
        }

        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
            logger.logp(Level.FINE, CLASS_NAME, "addDynamicServlet", " added servletMapping to config for [" + servletName + "] sconfig [" + sconfig
                    + "]");
        }

        IServletWrapper s = null;
        try {
            s = getServletWrapper(servletName);
        } catch (Exception e) {
            com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(e, CLASS_NAME + ".addDynamicServlet", "3084", this);
            // pk435011
            logger.logp(Level.SEVERE, CLASS_NAME, "addDynamicServlet", "exception.occured.while.creating.wrapper.for.servlet", new Object[] {
                    servletName, e }); /* 283348.1 */
            throw new ServletException(e);
        }

        if (s == null) {
            // pk435011
            logger.logp(Level.SEVERE, CLASS_NAME, "addDynamicServlet", "could.not.create.wrapper.for.servlet", servletName);
            throw new ServletException("Could not create wrapper for the dynamic servlet " + servletName);
        }

        try {
            if (s != null) {
                sconfig.setServletWrapper(s);
                sconfig.addMapping(ServletConfig.CheckContextInitialized.FALSE,mappingURI);
                config.addServletMapping(servletName, mappingURI);
            }
        } catch (Exception e) {
            com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(e, CLASS_NAME + ".addDynamicServlet", "3095", this);
            // pk435011
            logger
                    .logp(Level.SEVERE, CLASS_NAME, "addDynamicServlet", "mapping.already.exists",
                            new Object[] { mappingURI, getApplicationName(), e }); /* 283348.1 */
            throw new ServletException(e);
        }
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
            logger.exiting(CLASS_NAME, "addDyanamicServlet");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ibm.websphere.servlet.context.IBMServletContext#addHttpSessionListener
     * (javax.servlet.http.HttpSessionListener)
     */
    public void addHttpSessionListener(HttpSessionListener listener) throws SecurityException {
        this.addHttpSessionListener(listener, true);
    }
    
    private void addHttpSessionListener(HttpSessionListener listener, boolean securityCheckNeeded) throws SecurityException {
        if (securityCheckNeeded) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(perm);
        }
        }
        this.getSessionContext().addHttpSessionListener(listener, name);
    }

    private void addHttpSessionAttributeListener(HttpSessionAttributeListener listener) throws SecurityException {
    	this.getSessionContext().addHttpSessionAttributeListener(listener, name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ibm.websphere.servlet.context.IBMServletContext#fireSessionAttributeAdded
     * (javax.servlet.http.HttpSessionBindingEvent)
     */
    public void fireSessionAttributeAdded(HttpSessionBindingEvent event) {
        this.sessionCtx.sessionAttributeAddedEvent(event);

    }

    /*
     * (non-Javadoc)
     * 
     * @seecom.ibm.websphere.servlet.context.IBMServletContext#
     * fireSessionAttributeRemoved(javax.servlet.http.HttpSessionBindingEvent)
     */
    public void fireSessionAttributeRemoved(HttpSessionBindingEvent event) {
        this.sessionCtx.sessionAttributeRemovedEvent(event);
    }

    /*
     * (non-Javadoc)
     * 
     * @seecom.ibm.websphere.servlet.context.IBMServletContext#
     * fireSessionAttributeReplaced(javax.servlet.http.HttpSessionBindingEvent)
     */
    public void fireSessionAttributeReplaced(HttpSessionBindingEvent event) {
        this.sessionCtx.sessionAttributeReplacedEvent(event);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ibm.websphere.servlet.context.IBMServletContext#fireSessionCreated
     * (javax.servlet.http.HttpSessionEvent)
     */
    public void fireSessionCreated(HttpSessionEvent event) {
        this.sessionCtx.sessionCreatedEvent(event);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ibm.websphere.servlet.context.IBMServletContext#fireSessionDestroyed
     * (javax.servlet.http.HttpSessionEvent)
     */
    public void fireSessionDestroyed(HttpSessionEvent event) {
        this.sessionCtx.sessionDestroyedEvent(event);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ibm.websphere.servlet.context.IBMServletContext#getSessionTimeout()
     */
    public int getSessionTimeout() {
        return this.sessionCtx.getSessionTimeOut();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ibm.websphere.servlet.context.IBMServletContext#isSessionTimeoutSet()
     */
    public boolean isSessionTimeoutSet() {
        return this.sessionCtx.isSessionTimeoutSet();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ibm.websphere.servlet.context.IBMServletContext#loadServlet(java.
     * lang.String)
     */
    public void loadServlet(String servletName) throws ServletException, SecurityException {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(perm);
        }

        ServletWrapper s;

        try {
            s = (ServletWrapper) getServletWrapper(servletName);
            if (s != null) {
                s.load();
            }
        } catch (Exception e) {
            throw new ServletException("Servlet load failed: " + e.getMessage());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ibm.websphere.servlet.context.IBMServletContext#removeDynamicServlet
     * (java.lang.String)
     */
    public void removeDynamicServlet(String servletName) throws SecurityException {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
            logger.logp(Level.FINE, CLASS_NAME, "removeDynamicServlet", "remove dynamic servlet for -->" + servletName);
        }
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(perm);
        }
        removeServlet(servletName);
    }

    public boolean removeServlet(String servletName) {
        // make sure the servlet doesn't already exist
        boolean hasDestroyed = false;
        IServletConfig sconfig = config.getServletInfo(servletName);

        if (sconfig == null)
            return hasDestroyed;

        List l = config.getServletMappings(servletName);

        if (l != null) {
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
                logger.logp(Level.FINE, CLASS_NAME, "removeServlet", "number of servletMappings for -->[" + servletName + "] is -->[" + l.size()
                        + "]");
            }
            Iterator<String> mappings = l.iterator();
            while (mappings.hasNext()) {
                String mapping = mappings.next();
                if (mapping.charAt(0) != '/' && mapping.charAt(0) != '*')
                    mapping = '/' + mapping;
                RequestProcessor p = requestMapper.map(mapping);
                if (p != null) {
                    if (p instanceof ServletWrapper) {
                        // if
                        // (((ServletWrapper)p).getServletName().equals(servletName))
                        // {
                        try {
                            if (!hasDestroyed) {
                                ((ServletWrapper) p).destroy();
                                hasDestroyed = true;
                            }
                            requestMapper.removeMapping(mapping);
                        } catch (Throwable th) {
                            // pk435011
                            logger.logp(Level.WARNING, CLASS_NAME, "removeServlet", "encountered.problems.while.removing.servlet", new Object[] {
                                    servletName, th });
                        }
                    } else {
                        continue;
                    }
                }
            }

            config.removeServletMappings(servletName);
            config.removeServletInfo(servletName);
        } else {
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
                logger.logp(Level.FINE, CLASS_NAME, "removeServlet", "no servletMappings for -->[" + servletName + "]");
            }
        }
        return hasDestroyed;
    }

    public void started() {
        notifyStart();
    }

    /**
	 * 
	 */
    public void notifyStart() {
        try {
            eventSource.onApplicationAvailableForService(new ApplicationEvent(this, this, new com.ibm.ws.webcontainer.util.IteratorEnumerator(config
                    .getServletNames())));
            this.setInitialized(true);

        } catch (Exception e) {
            com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(e, CLASS_NAME + ".started", "3220", this);
            // pk435011
            logger.logp(Level.SEVERE, CLASS_NAME, "started", "error.on.collaborator.started.call");
        }
    }

    public ExtensionProcessor getLoginProcessor() {
        if (loginProcessor == null)
            loginProcessor = collabHelper.getSecurityCollaborator().getFormLoginExtensionProcessor(this);

        return loginProcessor;
    }

    public ExtensionProcessor getLogoutProcessor() {
        if (logoutProcessor == null)
            logoutProcessor = collabHelper.getSecurityCollaborator().getFormLogoutExtensionProcessor(this);

        return logoutProcessor;
    }

    /*
     * (non-Javadoc)
     * 
     * @seecom.ibm.wsspi.webcontainer.servlet.IServletContext#
     * registerRequestDispatcherFactory
     * (com.ibm.wsspi.webcontainer.servlet.RequestDispatcherFactory)
     */
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ibm.wsspi.webcontainer.servlet.IServletContext#removeLifeCycleListener
     * (java.util.EventListener)
     */
    public void removeLifeCycleListener(EventListener listener) {
        if (listener != null) {
            if (listener instanceof javax.servlet.ServletContextListener) {
                // add to the context listener list
                servletContextListeners.remove(listener);
            }
            if (listener instanceof javax.servlet.ServletContextAttributeListener) {
                // add to the context attr listener list
                servletContextLAttrListeners.remove(listener);
            }

            if (listener instanceof javax.servlet.http.HttpSessionListener) {
                // session context currently has no way to remove
                // lifecycle listeners
            }

            // 2.4 Listeners
            if (listener instanceof javax.servlet.ServletRequestListener) {
                servletRequestListeners.remove(listener);
            }
            if (listener instanceof javax.servlet.ServletRequestAttributeListener) {
                servletRequestLAttrListeners.remove(listener);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ibm.wsspi.webcontainer.servlet.IServletContext#addMappingTarget(java
     * .lang.String, com.ibm.ws.webcontainer.core.RequestProcessor)
     */
    public void addMappingTarget(String mapping, RequestProcessor target) throws Exception {
        this.requestMapper.addMapping(mapping, target);
    }

    public void addMappingFilter(String mapping, com.ibm.websphere.servlet.filter.IFilterConfig config) {
        addMappingFilter(mapping, (com.ibm.wsspi.webcontainer.filter.IFilterConfig) config);
    }

    public void addMappingFilter(String mapping, IFilterConfig config) {
        IFilterMapping fmapping = new FilterMapping(mapping, config, null);
        _addMapingFilter(config, fmapping);
    }

    /**
     * Adds a filter against a specified servlet config into this context
     * 
     * @param sConfig
     * @param config
     */
    public void addMappingFilter(IServletConfig sConfig, IFilterConfig config) {
        IFilterMapping fmapping = new FilterMapping(null, config, sConfig);
        _addMapingFilter(config, fmapping);
    }

    private void _addMapingFilter(IFilterConfig config, IFilterMapping fmapping) {
        fmapping.setDispatchMode(config.getDispatchType());
        // Begin 202490f_4
        this.config.addFilterInfo(fmapping.getFilterConfig());
        this.config.getFilterMappings().add(fmapping);
        // End 202490f_4 this.config
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ibm.wsspi.webcontainer.servlet.IServletContext#getMappingTarget(java
     * .lang.String)
     */
    public RequestProcessor getMappingTarget(String mapping) {
        return this.requestMapper.map(mapping);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.webcontainer.servlet.IServletContext#targets()
     */
    public Iterator targets() {
        return this.requestMapper.targetMappings();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.webcontainer.servlet.IServletContext#getWebAppConfig()
     */
    public WebAppConfig getWebAppConfig() {
        return this.config;
    }

    /**
	 * 
	 */
    public void failed() {
        eventSource.onApplicationUnavailableForService(new ApplicationEvent(this, this, new com.ibm.ws.webcontainer.util.IteratorEnumerator(config
                .getServletNames())));
    }

    public abstract String getServerInfo();

    public void replaceMappingTarget(String mapping, RequestProcessor target) throws Exception {
        this.requestMapper.replaceMapping(mapping, target);
    }

    public IFilterConfig createFilterConfig(String id) {
        FilterConfig fc = new FilterConfig(id, config);

        fc.setIServletContext(this);
        return fc;
    }

    public com.ibm.websphere.servlet.filter.IFilterConfig getFilterConfig(String id) {
        return createFilterConfig(id);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ibm.wsspi.webcontainer.servlet.IServletContext#finishEnvSetup(boolean
     * )
     */
    public void finishEnvSetup(boolean transactional) throws Exception {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
            logger.logp(Level.FINE, CLASS_NAME, "finishEnvSetup", "enter");
        }
        // unset the classloader
        UnsynchronizedStack envObjectStack = (UnsynchronizedStack) envObject.get();
        EnvObject env = null;
        if (envObjectStack != null) {
            env = (EnvObject) envObjectStack.pop();
            final ClassLoader origLoader = env.origClassLoader;
            if (origLoader != null) {
                ThreadContextHelper.setClassLoader(origLoader);
            }
            if (envObjectStack.isEmpty()) {
                envObject.set(null);
            }
        }

        // namespace postinvoke
        webAppNameSpaceCollab.postInvoke();

        if (transactional && env != null)
            txCollab.postInvoke(null, env.txConfig, this.isServlet23);
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
            logger.logp(Level.FINE, CLASS_NAME, "finishEnvSetup", "exit");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ibm.wsspi.webcontainer.servlet.IServletContext#startEnvSetup(boolean)
     */
    public void startEnvSetup(boolean transactional) throws Exception {
        // set classloader
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
            logger.logp(Level.FINE, CLASS_NAME, "startEnvSetup", "enter");
        }
        ClassLoader origCl = ThreadContextHelper.getContextClassLoader();
        final ClassLoader warClassLoader = getClassLoader();
        if (warClassLoader != origCl) {
            ThreadContextHelper.setClassLoader(warClassLoader);
        } else {
            origCl = null;
        }
        // createCollaboratorHelper();
        // nameSpace preinvoke
        webAppNameSpaceCollab.preInvoke(getModuleMetaData().getCollaboratorComponentMetaData());

        // transaction preinvoke
        Object tx = null;
        if (transactional) {
            tx = txCollab.preInvoke(null, this.isServlet23);
        }
        UnsynchronizedStack envObjectStack = (UnsynchronizedStack) envObject.get();
        if (envObjectStack == null) {
            envObjectStack = new UnsynchronizedStack();
            envObject.set(envObjectStack);
        }
        envObjectStack.push(new EnvObject(origCl, tx));
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
            logger.logp(Level.FINE, CLASS_NAME, "startEnvSetup", "exit");
        }
    }

    // begin defect 293789: add ability for components to register
    // ServletContextFactories
    public void addFeature(WebContainerConstants.Feature feature) {
        this.features.add(feature);
    }

    // end defect 293789: add ability for components to register
    // ServletContextFactories

    public boolean isFeatureEnabled(WebContainerConstants.Feature feature) {
        return this.features.contains(feature);
    }

    public ArrayList getServletContextAttrListeners() {
        return this.servletContextLAttrListeners;
    }

    public ArrayList getServletContextListeners() {
        return this.servletContextListeners;
    }

    public ArrayList getServletRequestAttrListeners() {
        return this.servletRequestLAttrListeners;
    }

    public ArrayList getServletRequestListeners() {
        return this.servletRequestListeners;
    }

    // 325429
    public Boolean getDestroyed() {
        return this.destroyed;
    }

    class EnvObject {
        ClassLoader origClassLoader;
        Object txConfig;

        EnvObject(ClassLoader cl, Object tx) {
            this.origClassLoader = cl;
            this.txConfig = tx;
        }
    }

    public ICollaboratorHelper getCollaboratorHelper() {
        return this.collabHelper;
    }

    protected void setCollaboratorHelper(ICollaboratorHelper collab) {
        this.collabHelper = collab;
    }

    protected abstract ICollaboratorHelper createCollaboratorHelper(DeployedModule moduleConfig);

    public boolean isServlet23() {
        return this.isServlet23;
    }

    public int getVersionID() {
        return this.versionID;
    }

    public abstract com.ibm.wsspi.webcontainer.metadata.WebComponentMetaData getWebAppCmd();

    protected void notifyStop() {
        // nothing
    }

    public AsyncRequestDispatcher getAsyncRequestDispatcher(String path) {
        logger.logp(Level.WARNING, CLASS_NAME, "getAsyncRequestDispatcher", "ARD.Not.Enabled");
        return (AsyncRequestDispatcher) getRequestDispatcher(path);
    }

    // PK50133 start
    public void addAndCheckJSPClassLoaderLimit(ServletWrapper sw) {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
            logger.entering(CLASS_NAME, "addAndCheckJSPClassLoaderLimit");
        }

        ServletWrapper swToUnload = null;
        synchronized (jspClassLoadersMap) {
            swToUnload = jspClassLoadersMap.putSW(sw.getServletName(), sw);
        }

        // PK82657 start - replace linked last code with call to LRU map
        /*
         * synchronized(jspClassLoaders) { if (jspClassLoaders.size() >
         * jspClassLoaderLimit) { swToUnload =
         * (ServletWrapper)jspClassLoaders.getFirst();
         * jspClassLoaders.removeFirst(); jspClassLoaders.addLast(sw); } else {
         * jspClassLoaders.addLast(sw); } }
         */

        if (swToUnload != null) {
            try {
                requestMapper.removeMapping(swToUnload.getServletName());
                swToUnload.unload();
            } catch (Exception e) {
                logger.logp(Level.SEVERE, CLASS_NAME, "addAndCheckJSPClassLoaderLimit", "Exception.occured.during.servlet.unload", e);
            }
        }

        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
            logger.exiting(CLASS_NAME, "addAndCheckJSPClassLoaderLimit");
        }
    }

    public void setJSPClassLoaderLimit(int i) {
        this.jspClassLoaderLimit = i;
        this.jspClassLoadersMap = new JSPClassLoadersMap(i); // PK82657
    }

    public int getJSPClassLoaderLimit() {
        return jspClassLoaderLimit;
    }

    public void setJSPClassLoaderExclusionList(String s) {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
            logger.logp(Level.FINE, CLASS_NAME, "setJSPClassLoaderExclusionList", "value: " + s);
        }

        StringTokenizer st = new StringTokenizer(s, ",");
        while (st.hasMoreElements()) {
            this.jspClassLoaderExclusionList.add(st.nextToken().trim());
        }
    }

    public List getJSPClassLoaderExclusionList() {
        return this.jspClassLoaderExclusionList;
    }

    public Class getJSPClassLoaderClassName() {
        try {
            return Class.forName("com.ibm.ws.jsp.webcontainerext.JSPExtensionClassLoader");
        } catch (java.lang.ClassNotFoundException cnf) {
            return null;
        }
    }

    // PK50133 end

    public boolean isCachingEnabled() {
        return false;
    }

    // PK82657 start
    public void setJSPClassLoaderLimitTrackIF(boolean trackIF) {
        this.jspClassLoaderLimitTrackIF = trackIF;
    }

    public boolean isJSPClassLoaderLimitTrackIF() {
        return jspClassLoaderLimitTrackIF;
    }

    private class JSPClassLoadersMap extends LinkedHashMap {

        // classloader limit as set by jsp attribute JSPClassLoaderLimit.
        int limit;
        ServletWrapper swToUnload;

        public JSPClassLoadersMap(int limit) {
            // call LinkedHashMap constructor and init the limit var.
            // set loadFactor param to 1.1 to ensure no resizing
            // set lastAccessed param to true so we remove the least recently
            // used instead of least recently added.
            super(limit + 1, 1.1f, true);
            this.limit = limit;
        }

        public ServletWrapper putSW(Object index, Object value) {
            this.put(index, value);
            return swToUnload;
        }

        protected boolean removeEldestEntry(Entry entry) {
            if (size() > limit) {
                // unload the servlet wrapper, then return true to remove the
                // entry from the map
                // unloadSW((ServletWrapper)entry.getValue());
                swToUnload = (ServletWrapper) entry.getValue();
                return true;
            }
            swToUnload = null;
            return false;
        }

    }

    // PK82657 end

    public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
        return commonAddFilter(filterName, null, null, filterClass);
    }

    public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
        return commonAddFilter(filterName, null, filter, null);
    }

    public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, String className) {
        return commonAddFilter(filterName, className, null, null);
    }

    private javax.servlet.FilterRegistration.Dynamic commonAddFilter(String filterName, String className, Filter filter,
            Class<? extends Filter> filterClass) {

    	if (initialized){
            throw new IllegalStateException(nls.getString("Not.in.servletContextCreated"));
    	}
    	if (withinContextInitOfProgAddListener) {
        	throw new UnsupportedOperationException(nls.getString("Unsupported.op.from.servlet.context.listener"));
        }
    	
        if (this.config.getFilterInfo(filterName) != null) {
            return null;
        }

        IFilterConfig filterConfig = createFilterConfig(filterName);

        if (className != null) {
            filterConfig.setFilterClassName(className);
        } else if (filter != null) {
            filterConfig.setFilter(filter);
        } else if (filterClass != null) {
            filterConfig.setFilterClass(filterClass);
        }

        this.config.addFilterInfo(filterConfig);
//Wait until the filter is actually initialized to the the injection if necessary.
        
//if filter instance is null, we need to add this class to the list of classes to pass to the injection engine since we will create the instance later

//        if (filter==null) {
//	        Class injectionClass = filterClass;
//        	if (className!=null) {
//        		try {
//					injectionClass = Class.forName(className, true, this.getClassLoader());
//				} catch (ClassNotFoundException e) {
//					if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
//						logger.logp(Level.FINE, CLASS_NAME, "commonAddFilter", "exception.occured.while.loading.class.for.filter", new Object[] { filterName, e });
//				}
//	        }
//            injectProgrammaticallyAddedClasses(injectionClass);
//        }

        return filterConfig;
    }

    public javax.servlet.ServletRegistration.Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
            logger.entering(CLASS_NAME, "addServlet(String, Class<? extends Servlet>)");

        javax.servlet.ServletRegistration.Dynamic dynamic = commonAddServlet(servletName, null, null, servletClass);

        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
            logger.exiting(CLASS_NAME, "addServlet(String, Class<? extends Servlet>)");
        return dynamic;
    }

    public javax.servlet.ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet) {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
            logger.entering(CLASS_NAME, "addServlet(String, Servlet)");

        javax.servlet.ServletRegistration.Dynamic dynamic = commonAddServlet(servletName, null, servlet, null);

        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
            logger.exiting(CLASS_NAME, "addServlet(String, Servlet)");
        return dynamic;
    }

    public javax.servlet.ServletRegistration.Dynamic addServlet(String servletName, String className) {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
            logger.entering(CLASS_NAME, "addServlet(String, String)");

        javax.servlet.ServletRegistration.Dynamic dynamic = commonAddServlet(servletName, className, null, null);

        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
            logger.exiting(CLASS_NAME, "addServlet(String, String)");
        return dynamic;
    }

    public javax.servlet.ServletRegistration.Dynamic commonAddServlet(String servletName, String className, Servlet servlet,
            Class<? extends Servlet> servletClass) {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
            logger.entering(CLASS_NAME, "commonAddServlet");
        // make sure the servlet doesn't already exist
        IServletConfig sconfig = config.getServletInfo(servletName);

        if (initialized){
            throw new IllegalStateException(nls.getString("Not.in.servletContextCreated"));
        }
        if (withinContextInitOfProgAddListener) {
        	throw new UnsupportedOperationException(nls.getString("Unsupported.op.from.servlet.context.listener"));
        }

        if (sconfig == null) {
            // Adding a brand new servlet to the container
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
                logger
                        .logp(Level.FINE, CLASS_NAME, "commonAddServlet",
                                "Servlet name not found in the web application configuration. Creating new config.");
            }

            if (servlet != null && isExistingServletWithSameInstance(servlet)) {
                logger.logp(Level.SEVERE, CLASS_NAME, "commonAddServlet", "servlet.with.same.object.instance.already.exists");
                if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
                    logger.exiting(CLASS_NAME, "commonAddServlet");
                return null;
            }

            try {
                sconfig = this.webExtensionProcessor.createConfig("DYN_" + servletName + "_" + System.currentTimeMillis());

                sconfig.setServletName(servletName);
                sconfig.setClassName(className);
                sconfig.setServletClass(servletClass);
                sconfig.setServlet(servlet);
                sconfig.setServletContext(getFacade());

                // add to the config
                config.addServletInfo(servletName, sconfig);
                config.addDynamicServletRegistration(servletName,sconfig);
                IServletWrapper s = null;
                try {
                    s = getServletWrapper(servletName);
                } catch (Exception e) {
                    com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(e, CLASS_NAME + ".addDynamicServlet", "3084", this);
                    // pk435011
                    
                    throw new ServletException(e);
                }

                s.setTarget(servlet);
                sconfig.setServletWrapper(s);
                //if servlet instance is null, we need to add this class to the list of classes to pass to the injection engine since we will create the instance later
                if (servlet==null) {
        	        Class clazz = servletClass;
                	if (clazz==null) {
                		if (className!=null){
	                		try {
								clazz = Class.forName(className, true, this.getClassLoader());
								loadAnnotationsForProgrammaticServlets(clazz,sconfig);
							} catch (ClassNotFoundException e) {
								if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
									logger.logp(Level.FINE, CLASS_NAME, "commonAddServlet", "exception.occured.while.loading.class.for.servlet", new Object[] { servletName, e });
							}
                		}
        	        } else {
        	        	loadAnnotationsForProgrammaticServlets(clazz,sconfig);
        	        }
                	//This isn't needed because we already scan all the classes in the web module
                	//and this is meant to add to that list of classes to scan.
					//injectProgrammaticallyAddedClasses(injectionClass);
                } else {
                    //handles caching for a servlet already created
                    s.modifyTarget(servlet); // to handle SingleThreadModel & caching
                }
            } catch (ServletException e) {
            	logger.logp(Level.SEVERE, CLASS_NAME, "commonAddServlet", "exception.occured.while.adding.servlet", new Object[] {
                        servletName, e }); /* 283348.1 */
            } // PK63920
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
                logger.exiting(CLASS_NAME, "commonAddServlet");
            return sconfig;
        } else {
        	if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
                logger.logp(Level.FINER,CLASS_NAME, "commonAddServlet", "named sconfig already exists->"+sconfig);
        	if (!sconfig.isClassDefined()) {
        		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
                    logger.logp(Level.FINER,CLASS_NAME, "commonAddServlet", "class not defined so setting class");
        		sconfig.setClassName(className);
                sconfig.setServletClass(servletClass);
                sconfig.setServlet(servlet);
        		config.addDynamicServletRegistration(servletName,sconfig);
        		return sconfig;
        	}
            logger.logp(Level.WARNING, CLASS_NAME, "commonAddServlet", "servlet.with.same.name.already.exists", new Object[] {servletName});
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
                logger.exiting(CLASS_NAME, "addServlet");
            return null;
        }

    }

    private void loadAnnotationsForProgrammaticServlets(Class clazz,
			IServletConfig sconfig) {
    	RunAs runAsAnnotation = (RunAs) clazz.getAnnotation(RunAs.class);
    	if (runAsAnnotation!=null)
    		sconfig.setRunAsRole(runAsAnnotation.value());
    	
    	checkForServletSecurityAnnotation(clazz, sconfig);
    	
    	
    	MultipartConfig multipartConfig = (MultipartConfig) clazz.getAnnotation(MultipartConfig.class);
    	if (multipartConfig!=null){
    		MultipartConfigElement multipartConfigElement = new MultipartConfigElement(multipartConfig);
    		sconfig.setMultipartConfig(multipartConfigElement);
    	}   
    	
    	
	}

	private void checkForServletSecurityAnnotation(Class clazz,
			IServletConfig sconfig) {
		ServletSecurity servletSecurityAnnotation = (ServletSecurity) clazz.getAnnotation(ServletSecurity.class);
    	if (servletSecurityAnnotation!=null)
    	{
    		ServletSecurityElement servletSecurity = new ServletSecurityElement(servletSecurityAnnotation);
    		sconfig.setServletSecurity(servletSecurity);
    	}
	}

	public void commonAddListener(String listenerClassName, EventListener listener,
            Class<? extends EventListener> listenerClass) {

        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
            logger.entering(CLASS_NAME, "addListener");
        if (initialized) {
            throw new IllegalStateException(nls.getString("Not.in.servletContextCreated"));
        }
        if (withinContextInitOfProgAddListener) {
        	throw new UnsupportedOperationException(nls.getString("Unsupported.op.from.servlet.context.listener"));
        }
        Class className = listenerClass;
        try {
	        if (listenerClassName!=null) {
	            className = Class.forName(listenerClassName, true, this.getClassLoader());
	        } else if (listener!=null) {
	            className = listener.getClass();
	        }
	        Class[] validListenerClasses=new Class[6];
	        validListenerClasses[0]=javax.servlet.ServletContextAttributeListener.class;
	        validListenerClasses[1]=javax.servlet.ServletRequestListener.class;
	        validListenerClasses[2]=javax.servlet.ServletRequestAttributeListener.class;
	        validListenerClasses[3]=javax.servlet.http.HttpSessionListener.class;
	        validListenerClasses[4]=javax.servlet.http.HttpSessionAttributeListener.class;
	        
	        //if this was called from ServletContainerInitializer#onStartup, then the listener can implement ServletContextListener
	        if (canAddServletContextListener) {
	            validListenerClasses[5]=javax.servlet.ServletContextListener.class;
	        } else {
	        	if ((javax.servlet.ServletContextListener.class).isAssignableFrom(className)) {
	        		throw new IllegalArgumentException(nls.getString("Error.adding.ServletContextListener"));
	        	}
	        }
	        boolean valid = false;
	        Set classesSet = new HashSet();
	        for (Class c: validListenerClasses) {
	            if (c!=null && c.isAssignableFrom(className)) {
	                valid=true;
	                classesSet.add(c);
	            }
	        }
	        if (!valid) {
	            throw new IllegalArgumentException(nls.getString("Invalid.Listener"));
	        }
	        
	        if (listener==null) {
	        	//Class or className was passed, need to create an instance
	            //Injection done here
	        	try {
	            	listener = createListener(className);
	            } catch (Exception e) {
	            	logger.logp(Level.SEVERE, CLASS_NAME, "commonAddListener", "exception.occurred.while.creating.listener.instance", new Object[] {
	                        className, e });
				} 
	        }
	        //add it to the end of the ordered list of listeners
	        if (classesSet.contains(javax.servlet.ServletContextAttributeListener.class)) {
	        	this.servletContextLAttrListeners.add(listener);
	        }
	        if (classesSet.contains(javax.servlet.ServletRequestListener.class)) {
	            this.servletRequestListeners.add(listener);
	        }
	        if (classesSet.contains(javax.servlet.ServletRequestAttributeListener.class)) {
	        	this.servletRequestLAttrListeners.add(listener);
	        }
	        if (classesSet.contains(javax.servlet.http.HttpSessionListener.class)) {
	            this.sessionListeners.add(listener);//add to this list in case we need to do a preDestroy
	        	this.addHttpSessionListener((HttpSessionListener)listener, false);
	        }
	        if (classesSet.contains(javax.servlet.http.HttpSessionAttributeListener.class)) {
	        	sessionAttrListeners.add(listener);//add to this list in case we need to do a preDestroy
	        	this.addHttpSessionAttributeListener((HttpSessionAttributeListener)listener);
	        }
	        if (classesSet.contains(javax.servlet.ServletContextListener.class)) {
	            this.servletContextListeners.add(listener);
	        }
        } catch (ClassNotFoundException e) {
        	logger.logp(Level.SEVERE, CLASS_NAME, "commonAddListener", "exception.occurred.while.adding.listener", new Object[] {
                    className.getName(), e });
        }
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
            logger.exiting(CLASS_NAME, "addListener");
    }
    
    private boolean isExistingServletWithSameInstance(Servlet servletToAdd) {
        Iterator<IServletConfig> servletInfoIterator = config.getServletInfos();
        while (servletInfoIterator.hasNext()) {
            IServletConfig curConfig = servletInfoIterator.next();
            if (curConfig.getServlet() == servletToAdd) {
                return true;
            }
        }
        return false;
    }

    @Override
    public <T extends Filter> T createFilter(Class<T> c) throws ServletException {
    	//injection done in WebAppImpl
    	if (withinContextInitOfProgAddListener) {
        	throw new UnsupportedOperationException(nls.getString("Unsupported.op.from.servlet.context.listener"));
        }
    	try {
            return c.newInstance();
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    public <T extends Servlet> T createServlet(Class<T> c) throws ServletException {
    	//injection done in WebAppImpl
    	if (withinContextInitOfProgAddListener) {
        	throw new UnsupportedOperationException(nls.getString("Unsupported.op.from.servlet.context.listener"));
        }
        try {
            return c.newInstance();
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
        if (withinContextInitOfProgAddListener) {
            throw new UnsupportedOperationException(nls.getString("Unsupported.op.from.servlet.context.listener"));
        }
        return config.getDefaultSessionTrackingMode();
    }

    @Override
    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
        if (withinContextInitOfProgAddListener) {
            throw new UnsupportedOperationException(nls.getString("Unsupported.op.from.servlet.context.listener"));
        }
        return config.getSessionTrackingMode();
    }

    @Override 
    public int getEffectiveMajorVersion() throws UnsupportedOperationException{
      if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE) == true)
      {
          logger.logp(Level.FINE, CLASS_NAME,"getEffectiveMajorVersion", "effectiveMajorVersion->"+effectiveMajorVersion);
      }
        return this.effectiveMajorVersion;
    }

    @Override
    public int getEffectiveMinorVersion() throws UnsupportedOperationException{
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE) == true)
        {
            logger.logp(Level.FINE, CLASS_NAME,"getEffectiveMinorVersion", "effectiveMinorVersion->"+effectiveMinorVersion);
        }
        return this.effectiveMinorVersion;
    }

    @Override
    public SessionCookieConfig getSessionCookieConfig() {
        if (withinContextInitOfProgAddListener) {
            throw new UnsupportedOperationException(nls.getString("Unsupported.op.from.servlet.context.listener"));
        }
        /*if (!config.getSessionManagerConfigBase().isAllowProgrammaticConfigurationSupport()) {
            //throw runtime exception 'cause you can't do programmatic config on the base object
            throw new RuntimeException(nls.getString("programmatic.sessions.disabled"));
        }*/
        return config.getSessionCookieConfig();
    }

    @Override
    public boolean setInitParameter(String name, String value) throws IllegalStateException, IllegalArgumentException {
    	 if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE) == true)
         {
             logger.logp(Level.FINE, CLASS_NAME,"setInitParameter", "name->"+name+"value->"+value);
             logger.logp(Level.FINE, CLASS_NAME,"setInitParameter", "initialized->"+initialized+"withinContextInitOfProgAddListener->"+withinContextInitOfProgAddListener);
         }
    	
    	if (initialized)
            throw new IllegalStateException(nls.getString("Not.in.servletContextCreated"));
    	
    	if (withinContextInitOfProgAddListener) {
        	throw new UnsupportedOperationException(nls.getString("Unsupported.op.from.servlet.context.listener"));
        }
    	
    	HashMap ctxParams = this.config.getContextParams();
    	if (ctxParams.containsKey(name)) {
    		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE) == true)
            {
                logger.logp(Level.FINE, CLASS_NAME,"setInitParameter", "ignoring init parameter with same key as another entry");
            }
    		return false;
    	}
    	else
    		ctxParams.put(name, value);
    	
        return true;
    }

    @Override
    public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) throws IllegalStateException {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
            logger.entering(CLASS_NAME, "setSessionTrackingModes(Set<SessionTrackingMode>)");
        }
        if (initialized) {
            throw new IllegalStateException(nls.getString("programmatic.sessions.already.been.initialized"));
        }
        if (withinContextInitOfProgAddListener) {
            throw new UnsupportedOperationException(nls.getString("Unsupported.op.from.servlet.context.listener"));
        }
        if (sessionTrackingModes==null) {
            sessionTrackingModes = EnumSet.noneOf(SessionTrackingMode.class);
        }
        //TODO: FIGURE OUT WHAT TO DO HERE.  SPEC DIFFERS FROM OUR IMPL
        if ((sessionTrackingModes.contains(SessionTrackingMode.SSL)) && (sessionTrackingModes.size()>1)) {
            //our default SSL implementation allows for SSL with either cookies or url rewriting and just stores the id SESSIONMANAGEMENTAFFINI
            throw new IllegalArgumentException("When setting the session tracking modes to SSL, you must not include another tracking mode.");
        }
        //I think we need to add the following WebSphere specific behavior
        //if (sessionTrackingModes.contains(SessionTrackingMode.SSL)) {
        //    sessionTrackingModes.add(SessionTrackingMode.COOKIE);
        //}
        
        //throw runtime exception 'cause you can't do programmatic config on the base object
        /*if (!config.getSessionManagerConfigBase().isAllowProgrammaticConfigurationSupport()) {
            throw new RuntimeException(nls.getString("programmatic.sessions.disabled"));
        }*/
        
        config.setSessionTrackingMode(sessionTrackingModes);
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
            logger.exiting(CLASS_NAME, "setSessionTrackingModes(Set<SessionTrackingMode>)");
        }
    }

    public boolean containsTargetMapping(String mapping) {
        return this.requestMapper.exists(mapping);
    }

    public void finishedFiltersWithNullTarget(ServletRequest request, ServletResponse response, RequestProcessor requestProcessor) throws ServletException {

        // a filter could potentially send on a ServletRequest instead of
        // HttpServletRequest
        IExtendedRequest iExtendedRequest = ServletUtil.unwrapRequest(request);
        HttpServletResponse httpResponse = (HttpServletResponse) ServletUtil.unwrapResponse(response, HttpServletResponse.class);

        String relativeURI = iExtendedRequest.getWebAppDispatcherContext().getRelativeUri();

        NoTargetForURIException noTargetForURIException = new NoTargetForURIException(relativeURI);
        WebAppErrorReport webAppErrorReport = new WebAppErrorReport(noTargetForURIException);
        if (requestProcessor != null && requestProcessor instanceof ServletWrapper) {
            webAppErrorReport.setTargetServletName(((ServletWrapper) requestProcessor).getServletName());
        } else {
            webAppErrorReport.setTargetServletName(iExtendedRequest.getRequestURI());
        }
        webAppErrorReport.setErrorCode(HttpServletResponse.SC_NOT_FOUND);

        // anything implementing iExtendedRequest should be implementing
        // HttpServletRequest as well.
		this.sendError(iExtendedRequest, httpResponse, webAppErrorReport);
        //throw webAppErrorReport;
    }

    // try {
    // return c.newInstance();
    // } catch (Exception e) {
    // throw new ServletException(e);
    // }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
        //make sure session config is up to date with session cookie & set session cookie initialized
        
        this.config.getSessionCookieConfig().setContextInitialized();
    }

    @Override
    public void addListener(Class<? extends EventListener> listenerClass) {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
            logger.entering(CLASS_NAME, "addListener(Class<? extends EventListener>)");

        commonAddListener(null, null, listenerClass);

        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
            logger.exiting(CLASS_NAME, "addListener(Class<? extends EventListener>)");
    }

    @Override
    public void addListener(String listenerClassName) {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
            logger.entering(CLASS_NAME, "addListener(String)");

        commonAddListener(listenerClassName, null, null);

        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
            logger.exiting(CLASS_NAME, "addListener(String)");
    }

    @Override
    public <T extends EventListener> void addListener(T listener) {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
            logger.entering(CLASS_NAME, "<T extends EventListener> addListener(T)");

        commonAddListener(null, listener, null);

        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
            logger.exiting(CLASS_NAME, "<T extends EventListener> addListener(T)");
    }

    @Override
    public <T extends EventListener> T createListener(Class<T> listener) throws ServletException {
        //injection done in WebAppImpl
    	if (withinContextInitOfProgAddListener) {
        	throw new UnsupportedOperationException(nls.getString("Unsupported.op.from.servlet.context.listener"));
        }
        try {
        	return listener.newInstance();
        } catch (Exception e){
            throw new ServletException(e);
        }
    }
    
    protected void injectProgrammaticallyAddedClasses(Class className) {
    	//injection done in WebAppImpl
    	return;
    }
    
    protected void injectProgrammaticallyAddedClasses(Object classToInject, Class className) throws ServletException  {
    	//injection done in WebAppImpl
    	return;
    }

    @Override
    public FilterRegistration getFilterRegistration(String arg0) {
        return config.getFilterInfo(arg0);
    }

    @Override
    public Map<String, FilterRegistration> getFilterRegistrations() {
        return Collections.unmodifiableMap(config.getFilterInfoMap());
    }

    @Override
    public ServletRegistration getServletRegistration(String servletName) {
        IServletWrapper servletWrapper;
        try {
            servletWrapper = getServletWrapper(servletName);
        } catch (Exception e) {
            return null;
        }

        if (servletWrapper != null)
            return servletWrapper.getServletConfig();
        else
            return null;
    }

    @Override
    public Map<String, ServletRegistration> getServletRegistrations() {
        return Collections.unmodifiableMap(config.getServletInfoMap());
    }

    /* No longer used ... have to use getSessionCookieConfig and set the values on that
    @Override
    public void setSessionCookieConfig(SessionCookieConfig arg0) {
        // TODO Auto-generated method stub

    }*/

    @Override
    public JspConfigDescriptor getJspConfigDescriptor() {
        if (withinContextInitOfProgAddListener) {
            throw new UnsupportedOperationException(nls.getString("Unsupported.op.from.servlet.context.listener"));
        }
        JspConfigDescriptorImpl jspConfigDescriptor = new JspConfigDescriptorImpl(this);
        if (jspConfigDescriptor.getJspPropertyGroups().isEmpty() && jspConfigDescriptor.getTaglibs().isEmpty()) {
            return null;
        }
        return jspConfigDescriptor;
    }
    
	@Override
	public void declareRoles(String... arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public Map<String, ? extends ServletRegistration.Dynamic> getDynamicServletRegistrations(){
		return this.config.getDynamicServletRegistrations();
	}
	
	/*private ClassLoader getWebInfLibClassloader() {
		ClassLoader tempWebInfLibClassloader = webInfLibClassloader;
	    if (tempWebInfLibClassloader == null) { 
	        synchronized(this) {
	        	tempWebInfLibClassloader = webInfLibClassloader;
	            if (tempWebInfLibClassloader == null) 
	            {
	                URL url;
					try {
						File libFile = new File(getRealPath("/WEB-INF/lib"));
						url = libFile.toURI().toURL();
						tempWebInfLibClassloader = webInfLibClassloader = new URLClassLoader(new URL [] {url});
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }
	        }
	    }
	     return tempWebInfLibClassloader;
	}*/

}
