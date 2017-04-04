// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2008
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
//
//  CHANGE HISTORY
//D efect       Date        Modified By (Original By)   Description
//--------------------------------------------------------------------------------------
// 341303       01/25/06    mmolden                     Change WebContainer APIs to allow modification of ServletConfig
// 442812       06/07/06    mmolden                     allowed to access servlet by classname
// LIDB3518-1.2 06/26/07    mmolden                     ARD
// 465095       09/06/07    ekoonce                     add isServlet2_5 method
// 465675       09/09/07    ekoonce                     web apps fail to start with trace enabled
// LIDB4336-35  09/25/07    mmolden                     Remove mime filtering
// PK52168      10/24/07    mmolden (pmdinh)            Error Page doesn't handle subclass exception correctly.
// PK54499      10/24/07    mmolden (srpeters)          WEBCONTAINER OPTION TO OVERRIDE FILESERVINGENABLED="TRUE"
// PK52059      10/24/07    mmolden (srpeters)          POTENTIAL SECURITY EXPOSURE WITH SERVESERVLETSBYCLASSNAMEENABLE
// PK66137      05/19/08    mmolden (jebergma)          GLOBAL LISTENERS ARE NOT INVOKED
//
//
package com.ibm.ws.webcontainer.webapp;

import java.lang.Enum;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.ServletRegistration.Dynamic;

import com.ibm.ws.container.BaseConfiguration;
import com.ibm.ws.container.ErrorPage;
import com.ibm.ws.webcontainer.session.SessionCookieConfigImpl;
import com.ibm.ws.webcontainer.session.SessionManagerConfigBase;
import com.ibm.wsspi.webcontainer.WCCustomProperties;
import com.ibm.wsspi.webcontainer.filter.IFilterConfig;
import com.ibm.wsspi.webcontainer.filter.IFilterMapping;
import com.ibm.wsspi.webcontainer.logging.LoggerFactory;
import com.ibm.wsspi.webcontainer.servlet.IServletConfig;
import com.ibm.wsspi.webcontainer.webapp.WebAppConfig;

/**
 * @author asisin
 */
@SuppressWarnings("unchecked")
public abstract class WebAppConfiguration extends BaseConfiguration implements WebAppConfig {
    private static final String CLASS_NAME = WebAppConfiguration.class.getName();
    private static final Logger logger = LoggerFactory.getInstance().getLogger("com.ibm.ws.webcontainer.webapp");

    private int version;
    private String contextRoot;
    private int sessionTimeout;
    private boolean moduleSessionTimeoutSet=false;
    private boolean moduleSessionTrackingModeSet=false;
    private SessionCookieConfigImpl sessionCookieConfig;
    private boolean hasProgrammaticCookieConfig=false;
    private EnumSet<SessionTrackingMode> sessionDefaultTrackingModeSet;
    private SessionManagerConfigBase sessionManagerConfigBase;
    private String displayName;
    private String description;
    private int reloadInterval;
    private boolean distributable;
    private boolean reloadingEnabled;
    private Boolean serveServletsByClassnameEnabled;
    private String defaultErrorPage;
    private String additionalClassPath;
    private Boolean fileServingEnabled;
    private Boolean directoryBrowsingEnabled;
    private boolean autoRequestEncoding;
    private boolean autoResponseEncoding;
    private boolean autoLoadFilters;
    private Map requestListeners;
    private Map requestAttributeListeners;
    private Map sessionListeners;
    private HashMap localeMap;
    private String moduleName;
    private String moduleId;
    private boolean isSyncToThreadEnabled;

    private boolean isSystemApp;

    // LIDB3518-1.2 06/26/07 mmolden ARD
    private boolean ardEnabled;
    private String ardDispatchType;
    // LIDB3518-1.2 06/26/07 mmolden ARD

    // List of listener classes
    private ArrayList listeners = new ArrayList();

    // Welcome files (String filenames/servlet URIs)
    private List<String> welcomeFileList = new ArrayList<String>();

    // Might turn out that we will store it in a different datastructure
    // (servletName, mappings} redundancy
    private Map<String, List<String>> servletMappings;

    private int lastIndexBeforeDeclaredFilters=0;
    // ordered list of filterMappings
    private List<IFilterMapping> filterMappings;

    // {servletName, ServletConfig}
    private Map<String, IServletConfig> servletInfo;

    // {filterName, FilterConfig}
    private Map<String, IFilterConfig> filterInfo;

    // format for storage {String extension, String type}
    private HashMap mimeMappings = new HashMap();

    // MimeFilter objects {String mimeType, MimeFilter}
    private HashMap mimeFilters;
    private boolean isMimeFilteringEnabled = false;

    public WebGroup theWebGroup;

    private Map<String, String> jspAttributes = null;

    private HashMap fileServingAttributes = new HashMap();

    private HashMap invokerAttributes = new HashMap();

    private HashMap contextParams = new HashMap();

    private String virtualHost;
    private HashMap exceptionErrorPages = new HashMap();
    private HashMap codeErrorPages = new HashMap();
    private List tagLibList;
    private boolean precompileJsps;
    private WebApp webApp;

    // begin LIDB2356.1: WebContainer work for incorporating SIP
    private List virtualHostList = new ArrayList();
    // end LIDB2356.1: WebContainer work for incorporating SIP
    private int appStartupWeight;
    private int moduleStartupWeight;
    private boolean metaDataComplete;
    private List<Class<?>> classesToScan;
    private List<IFilterMapping> uriFilterMappingInfos;
    private List<IFilterMapping> servletFilterMappingInfos;
	private Map<String, Dynamic> dynamicServletRegistrationMap;
	private String applicationName;
	private List<String> libBinPathList;
	private Set<String> webXmlDefinedListeners;



	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	private static String disallowAllFileServingProp; // PK54499
    private static String disallowServeServletsByClassnameProp; // PK52059

    /**
     * Constructor.
     * 
     * @param id
     */
    public WebAppConfiguration(String id) {
        super(id);
        this.servletInfo = new HashMap<String, IServletConfig>();
        this.servletMappings = new HashMap<String, List<String>>();
        this.filterInfo = new HashMap<String, IFilterConfig>();
        this.filterMappings = new ArrayList();
        this.classesToScan = new ArrayList<Class<?>>();
    }

    /**
     * Returns the additionalClassPath.
     * 
     * @return String
     */
    public String getAdditionalClassPath() {
        return this.additionalClassPath;
    }

    // begin LIDB2356.1: WebContainer work for incorporating SIP
    public List getVirtualHostList() {
        return this.virtualHostList;
    }

    // end LIDB2356.1: WebContainer work for incorporating SIP

    /**
     * Returns the servlet name as obtained from the <servlet> definition in
     * web.xml
     */
    public Iterator getServletNames() {
        return this.servletInfo.keySet().iterator();
    }

    public void addServletInfo(String name, IServletConfig info) {
        this.servletInfo.put(name, info);
    }

    public void removeServletInfo(String name) {
        this.servletInfo.remove(name);
    }

    public Iterator<IServletConfig> getServletInfos() {
        return this.servletInfo.values().iterator();
    }

    public Iterator<IFilterConfig> getFilterInfos() {
        return this.filterInfo.values().iterator();
    }

    /**
     * Returns the autoRequestEncoding.
     * 
     * @return boolean
     */
    public boolean isAutoRequestEncoding() {
        return this.autoRequestEncoding;
    }

    /**
     * Returns the autoResponseEncoding.
     * 
     * @return boolean
     */
    public boolean isAutoResponseEncoding() {
        return this.autoResponseEncoding;
    }

    /**
     * Returns the defaultErrorPage.
     * 
     * @return String
     */
    public String getDefaultErrorPage() {
        return this.defaultErrorPage;
    }

    /**
     * Returns the directoryBrowsingEnabled.
     * 
     * @return boolean
     */
    public boolean isDirectoryBrowsingEnabled() {
        if (this.directoryBrowsingEnabled != null)
            return this.directoryBrowsingEnabled.booleanValue();

        directoryBrowsingEnabled = WCCustomProperties.DIRECTORY_BROWSING_ENABLED;

        return directoryBrowsingEnabled;
    }

    /**
     * Returns the fileServingEnabled.
     * 
     * @return boolean
     */
    public boolean isFileServingEnabled() {
        // PK54499 START
        disallowAllFileServingProp = WCCustomProperties.DISALLOW_ALL_FILE_SERVING;
        if (disallowAllFileServingProp != null && !this.getApplicationName().equalsIgnoreCase("isclite")) {
            if (Boolean.valueOf(disallowAllFileServingProp).booleanValue()) {
                this.fileServingEnabled = Boolean.FALSE;
            }
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
                logger.logp(Level.FINE, CLASS_NAME, "isFileServing", "PK54499: disallowAllFileServingProp set to " + disallowAllFileServingProp
                        + " for application: " + this.getApplicationName());
            }
        }
        // PK54499 END

        if (this.fileServingEnabled != null)
            return this.fileServingEnabled.booleanValue();

        return WCCustomProperties.FILE_SERVING_ENABLED;
    }

    /**
     * Returns the mimeMappings.
     * 
     * @return Map
     */
    public HashMap getMimeMappings() {
        return this.mimeMappings;
    }

    /**
     * Returns the reloadingEnabled.
     * 
     * @return boolean
     */
    public boolean isReloadingEnabled() {
        return this.reloadingEnabled;
    }

    /**
     * Returns the reloadInterval.
     * 
     * @return int
     */
    public int getReloadInterval() {
        return this.reloadInterval;
    }

    /**
     * Returns the requestAttributeListeners.
     * 
     * @return Map
     */
    public Map getRequestAttributeListeners() {
        return this.requestAttributeListeners;
    }

    /**
     * Returns the requestListeners.
     * 
     * @return Map
     */
    public Map getRequestListeners() {
        return this.requestListeners;
    }

    /**
     * Returns the sessionListeners.
     * 
     * @return Map
     */
    public Map getSessionListeners() {
        return this.sessionListeners;
    }

    /**
     * Sets the additionalClassPath.
     * 
     * @param additionalClassPath
     *            The additionalClassPath to set
     */
    public void setAdditionalClassPath(String additionalClassPath) {
        this.additionalClassPath = additionalClassPath;
    }

    /**
     * Sets the autoRequestEncoding.
     * 
     * @param autoRequestEncoding
     *            The autoRequestEncoding to set
     */
    public void setAutoRequestEncoding(boolean autoRequestEncoding) {
        this.autoRequestEncoding = autoRequestEncoding;
    }

    /**
     * Sets the autoResponseEncoding.
     * 
     * @param autoResponseEncoding
     *            The autoResponseEncoding to set
     */
    public void setAutoResponseEncoding(boolean autoResponseEncoding) {
        this.autoResponseEncoding = autoResponseEncoding;
    }

    /**
     * Sets the defaultErrorPage.
     * 
     * @param defaultErrorPage
     *            The defaultErrorPage to set
     */
    public void setDefaultErrorPage(String defaultErrorPage) {
        this.defaultErrorPage = defaultErrorPage;
    }

    /**
     * Sets the directoryBrowsingEnabled.
     * 
     * @param directoryBrowsingEnabled
     *            The directoryBrowsingEnabled to set
     */
    public void setDirectoryBrowsingEnabled(Boolean directoryBrowsingEnabled) {
        this.directoryBrowsingEnabled = directoryBrowsingEnabled;
    }

    /**
     * Sets the fileServingEnabled.
     * 
     * @param fileServingEnabled
     *            The fileServingEnabled to set
     */
    public void setFileServingEnabled(Boolean fileServingEnabled) {
        this.fileServingEnabled = fileServingEnabled;
    }

    /**
     * Sets the mimeMappings.
     * 
     * @param mimeMappings
     *            The mimeMappings to set
     */
    public void setMimeMappings(HashMap mimeMappings) {
        this.mimeMappings = mimeMappings;
    }

    /**
     * Sets the reloadingEnabled.
     * 
     * @param reloadingEnabled
     *            The reloadingEnabled to set
     */
    public void setReloadingEnabled(boolean reloadingEnabled) {
        this.reloadingEnabled = reloadingEnabled;
    }

    /**
     * Sets the reloadInterval.
     * 
     * @param reloadInterval
     *            The reloadInterval to set
     */
    public void setReloadInterval(int reloadInterval) {
        this.reloadInterval = reloadInterval;
    }

    /**
     * Sets the requestAttributeListeners.
     * 
     * @param requestAttributeListeners
     *            The requestAttributeListeners to set
     */
    public void setRequestAttributeListeners(Map requestAttributeListeners) {
        this.requestAttributeListeners = requestAttributeListeners;
    }

    /**
     * Sets the requestListeners.
     * 
     * @param requestListeners
     *            The requestListeners to set
     */
    public void setRequestListeners(Map requestListeners) {
        this.requestListeners = requestListeners;
    }

    /**
     * Sets the sessionListeners.
     * 
     * @param sessionListeners
     *            The sessionListeners to set
     */
    public void setSessionListeners(Map sessionListeners) {
        this.sessionListeners = sessionListeners;
    }

    /**
     * Returns the displayName.
     * 
     * @return String
     */
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * Sets the displayName.
     * 
     * @param displayName
     *            The displayName to set
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the jspAttributes.
     * 
     * @return Map<String,String>
     */
    public Map<String, String> getJspAttributes() {
        if (null == this.jspAttributes) {
            this.jspAttributes = new HashMap<String, String>();
        }
        return this.jspAttributes;
    }

    /**
     * Sets the jspAttributes.
     * 
     * @param jspAttributes
     *            The jspAttributes to set
     */
    public void setJspAttributes(Map<String, String> jspAttributes) {
        this.jspAttributes = jspAttributes;
    }

    public String toString() {
        return this.displayName;
    }

    /**
     * Returns the contextParams.
     * 
     * @return List
     */
    public java.util.HashMap getContextParams() {
        return this.contextParams;
    }

    /**
     * Sets the contextParams.
     * 
     * @param contextParams
     *            The contextParams to set
     */
    public void setContextParams(java.util.HashMap contextParams) {
        this.contextParams = contextParams;
    }

    /**
     * Method getServletInfo.
     * 
     * @param string
     * @return ServletConfig
     */
    public IServletConfig getServletInfo(String string) {
        return (IServletConfig) this.servletInfo.get(string);
    }

    /**
     * Returns the fileServingAttributes.
     * 
     * @return List
     */
    public HashMap getFileServingAttributes() {
        return this.fileServingAttributes;
    }

    /**
     * Sets the fileServingAttributes.
     * 
     * @param fileServingAttributes
     *            The fileServingAttributes to set
     */
    public void setFileServingAttributes(HashMap fileServingAttributes) {
        this.fileServingAttributes = fileServingAttributes;
    }

    /**
     * Returns the serveServletsByClassname.
     * 
     * @return boolean
     */
    public boolean isServeServletsByClassnameEnabled() {
        // PK52059 START
        disallowServeServletsByClassnameProp = WCCustomProperties.DISALLOW_SERVE_SERVLETS_BY_CLASSNAME_PROP;
        if (disallowServeServletsByClassnameProp != null) {
            if (Boolean.valueOf(disallowServeServletsByClassnameProp).booleanValue()) {
                this.serveServletsByClassnameEnabled = Boolean.FALSE;
            }
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
                logger.logp(Level.FINE, CLASS_NAME, "isServeServletsByClassnameEnabled", "PK52059: disallowServeServletsByClassnameProp set to "
                        + disallowServeServletsByClassnameProp + " for application: " + this.getApplicationName());
            }
        }
        // PK52059 END

        if (this.serveServletsByClassnameEnabled != null)
            return this.serveServletsByClassnameEnabled.booleanValue();

        return WCCustomProperties.SERVE_SERVLETS_BY_CLASSNAME_ENABLED;
    }

    /**
     * Sets the serveServletsByClassname.
     * 
     * @param serveServletsByClassname
     *            The serveServletsByClassname to set
     */
    public void setServeServletsByClassnameEnabled(Boolean serveServletsByClassname) {
        this.serveServletsByClassnameEnabled = serveServletsByClassname;
    }

    /**
     * Returns the invokerAttributes.
     * 
     * @return List
     */
    public HashMap getInvokerAttributes() {
        return this.invokerAttributes;
    }

    /**
     * Sets the invokerAttributes.
     * 
     * @param invokerAttributes
     *            The invokerAttributes to set
     */
    public void setInvokerAttributes(HashMap invokerAttributes) {
        this.invokerAttributes = invokerAttributes;
    }

    public int getLastIndexBeforeDeclaredFilters() {
        return lastIndexBeforeDeclaredFilters;
    }

    public void setLastIndexBeforeDeclaredFilters(int lastIndexBeforeDeclaredFilters) {
        this.lastIndexBeforeDeclaredFilters = lastIndexBeforeDeclaredFilters;
    }

    /**
     * Returns the filterMappings.
     * 
     * @return List
     */
    public List getFilterMappings() {
        return this.filterMappings;
    }

    /**
     * Sets the filterMappings.
     * 
     * @param filterMappings
     *            The filterMappings to set
     */
    public void setFilterMappings(List<IFilterMapping> filterMappings) {
        this.filterMappings = filterMappings;
    }

    /**
     * Method getFilterInfo.
     * 
     * @param filterName
     * @return FilterConfig
     */
    public IFilterConfig getFilterInfo(String filterName) {
        return this.filterInfo.get(filterName);
    }

    // Begin 202490f_4
    /**
     * Method addFilterInfo.
     * 
     * @param config
     */
    public void addFilterInfo(IFilterConfig config) {
        this.filterInfo.put(config.getFilterName(), config);
    }

    // End 202490f_4

    /**
     * Method getVirtualHostName.
     * 
     * @return String
     */
    public String getVirtualHostName() {
        return this.virtualHost;
    }

    /**
     * Returns the servletMappings.
     * 
     * @return HashMap
     */
    public Map<String, List<String>> getServletMappings() {
        return this.servletMappings;
    }

    /**
     * Sets the servletMappings.
     * 
     * @param servletMappings
     *            The servletMappings to set
     */
    public void setServletMappings(Map<String, List<String>> servletMappings) {
        this.servletMappings = servletMappings;
    }

    /**
     * Method getServletMapping.
     * 
     * @param servletName
     * @return ServletMapping
     */
    public List<String> getServletMappings(String servletName) {
        return this.servletMappings.get(servletName);
    }

    /**
     * Returns the mimeFilters.
     * 
     * @return List
     */
    public HashMap getMimeFilters() {
        return this.mimeFilters;
    }

    /**
     * Sets the mimeFilters.
     * 
     * @param mimeFilters
     *            The mimeFilters to set
     */
    public void setMimeFilters(HashMap mimeFilters) {
        if (mimeFilters != null && mimeFilters.size() > 0) {
            this.isMimeFilteringEnabled = true;
        }
        this.mimeFilters = mimeFilters;
    }

    public String getMimeType(String extension) {
        return (String) this.mimeMappings.get(extension);
    }

    /**
     * Method getErrorPageByExceptionType.
     * 
     * @param th
     * @return ErrorPage
     */
    public ErrorPage getErrorPageTraverseRootCause(Throwable th) {
        while (th != null && th instanceof ServletException) { // defect 155880
                                                               // - Check
                                                               // rootcause !=
                                                               // null
            Throwable rootCause = ((ServletException) th).getRootCause();
            if (rootCause == null) {
                break;
            }
            ErrorPage er = getErrorPageByExceptionType(th);
            if (er != null)
                return er;

            th = rootCause;
        }
        if (th != null)
            return getErrorPageByExceptionType(th);
        return null;
    }

    /**
     * Method getErrorPageByExceptionType.
     * 
     * @param rootException
     * @return ErrorPage
     */
    public ErrorPage getErrorPageByExceptionType(Throwable rootException) {
        // Begin 256281
        // Check for a perfect match first
        String exceptionName = rootException.getClass().getName();
        Object obj = exceptionErrorPages.get(exceptionName);
        if (obj != null)
            return (ErrorPage) obj;
        // Check to see if its a child of another exception type in the list
        Iterator i = exceptionErrorPages.values().iterator();
        ErrorPage curEP = null;
        ClassLoader warClassLoader = getWebApp().getClassLoader();
        while (i.hasNext()) {
            ErrorPage ep = (ErrorPage) i.next();
            // Class exceptionClass = ep.getException();
            Class exceptionClass = ep.getException(warClassLoader);
            if (exceptionClass != null && exceptionClass.isInstance(rootException)) {
                if (curEP == null)
                    curEP = ep;
                // else if
                // (curEP.getException().isAssignableFrom(exceptionClass))
                // //PK52168
                else if (curEP.getException(warClassLoader).isAssignableFrom(exceptionClass)) // PK52168
                    curEP = ep;
            }
        }
        return curEP;
        // End 256281
    }

    /**
     * Get an error page based on the error code.
     * 
     * @param code
     * @return ErrorPage
     */
    public ErrorPage getErrorPageByErrorCode(Integer code) {
        return (ErrorPage) this.codeErrorPages.get(code);
    }

    /**
     * Method getWelcomeFileList.
     * 
     * @return List<String>
     */
    public List<String> getWelcomeFileList() {
        return this.welcomeFileList;
    }

    /**
     * Method setWelcomeFileList.
     */
    public void setWelcomeFileList(List<String> list) {
        this.welcomeFileList = list;
    }

    /**
     * Method getTagLibs.
     * 
     * @return List
     */
    public List getTagLibs() {
        return this.tagLibList;
    }

    public void setTagLibs(java.util.List l) {
        this.tagLibList = l;
    }

    /**
     * Returns the listeners.
     * 
     * @return List
     */
    public List getListeners() {
        return this.listeners;
    }

    public void addListener(String listenerClass) {
        this.listeners.add(listenerClass);
    }

    // public void addGlobalListeners(ArrayList globalListeners)
    // {
    // listeners.addAll(0, globalListeners);
    //
    
    /**
     * Sets the listeners.
     * 
     * @param listeners
     *            The listeners to set
     */
    public void setListeners(ArrayList listeners) {
        this.listeners = listeners;
    }
    

	public void setWebXmlDefinedListeners(Set<String> setOfWebXmlDefinedListeners) {
		this.webXmlDefinedListeners = setOfWebXmlDefinedListeners;
	}

    public Set<String> getWebXmlDefinedListeners() {
		return webXmlDefinedListeners;
	}

	/**
     * Returns the moduleId.
     * 
     * @return String
     */
    public String getModuleId() {
        return this.moduleId;
    }

    /**
     * Returns the moduleName.
     * 
     * @return String
     */
    public String getModuleName() {
        return this.moduleName;
    }

    /**
     * Return the applicationName.
     * 
     * @return String
     */
    public String getApplicationName() {
    	if (this.applicationName!=null)
    		return this.applicationName;
    	else if (webApp!=null)
    		return this.webApp.getApplicationName();
    	else
    		return null;
    		
    }

    /**
     * Sets the moduleId.
     * 
     * @param moduleId
     *            The moduleId to set
     */
    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    /**
     * Sets the moduleName.
     * 
     * @param moduleName
     *            The moduleName to set
     */
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    /**
     * Returns the description.
     * 
     * @return String
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the description.
     * 
     * @param description
     *            The description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the distributable.
     * 
     * @return boolean
     */
    public boolean isDistributable() {
        return this.distributable;
    }

    /**
     * Sets the distributable.
     * 
     * @param distributable
     *            The distributable to set
     */
    public void setDistributable(boolean distributable) {
        this.distributable = distributable;
    }

    /**
     * Sets the codeErrorPages.
     * 
     * @param codeErrorPages
     *            The codeErrorPages to set
     */
    public void setCodeErrorPages(HashMap codeErrorPages) {
        this.codeErrorPages = codeErrorPages;
    }

    /**
     * Sets the exceptionErrorPages.
     * 
     * @param exceptionErrorPages
     *            The exceptionErrorPages to set
     */
    public void setExceptionErrorPages(HashMap exceptionErrorPages) {
        this.exceptionErrorPages = exceptionErrorPages;
    }

    /**
     * Returns the sessionTimeout.
     * 
     * @return int
     */
    public int getSessionTimeout() {
        return this.sessionTimeout;
    }

    public boolean isModuleSessionTimeoutSet() {
        return this.moduleSessionTimeoutSet;
    }
    
    public boolean isModuleSessionTrackingModeSet() {
        return this.moduleSessionTrackingModeSet;
    }
    
    /**
     * Sets the sessionTimeout.
     * 
     * @param sessionTimeout
     *            The sessionTimeout to set
     */
    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }
    
    public void setModuleSessionTimeoutSet(boolean b) {
        this.moduleSessionTimeoutSet=b;
    }
    
    public void setModuleSessionTrackingModeSet(boolean b) {
        this.moduleSessionTrackingModeSet=b;
    }
    
    public SessionManagerConfigBase getSessionManagerConfigBase() {
        return this.sessionManagerConfigBase;
    }

    public void setSessionManagerConfigBase(SessionManagerConfigBase smcBase) {
        this.sessionManagerConfigBase = smcBase;
    }
    
    public SessionCookieConfigImpl getSessionCookieConfig() {
        return this.sessionCookieConfig;
    }
    
    public void setSessionCookieConfig(SessionCookieConfigImpl scc) {
        this.sessionCookieConfig = scc;
    }
    
    public void setHasProgrammaticCookieConfig() {
        hasProgrammaticCookieConfig = true;
    }
    
    public boolean hasProgrammaticCookieConfig() {
        return hasProgrammaticCookieConfig;
    }
    
    public EnumSet<SessionTrackingMode> getSessionTrackingMode() {
        if (this.sessionManagerConfigBase==null) {
            return getDefaultSessionTrackingMode();
        } else {
            return EnumSet.copyOf(this.sessionManagerConfigBase.getSessionTrackingMode());
        }
    }
    
    public EnumSet<SessionTrackingMode> getDefaultSessionTrackingMode() {
        if (this.sessionDefaultTrackingModeSet==null) {
            return EnumSet.noneOf(SessionTrackingMode.class);
        } else {
            return EnumSet.copyOf(this.sessionDefaultTrackingModeSet);
        }
    }
    
    public EnumSet<SessionTrackingMode> getInternalDefaultSessionTrackingMode() {
        return this.sessionDefaultTrackingModeSet;
    }
    
    //stm can't be null
    public void setSessionTrackingMode(Set<SessionTrackingMode> stm) {
        sessionManagerConfigBase.setEffectiveTrackingModes(EnumSet.copyOf(stm));
    }
    
    //only called internally during startup
    public void setDefaultSessionTrackingMode(EnumSet<SessionTrackingMode> stm) {
        this.sessionDefaultTrackingModeSet = stm;
    }

    public void setServletInfos(Map<String, IServletConfig> sInfos) {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
            logger.logp(Level.FINE, CLASS_NAME, "setServletInfos", "servletInfo --> " + sInfos);
        }
        this.servletInfo = sInfos;
    }

    public void setAppStartupWeight(int appStartupWeight) {
        this.appStartupWeight = appStartupWeight;
    }

    public int getAppStartupWeight() {
        return this.appStartupWeight;
    }

    public void setFilterInfos(Map<String, IFilterConfig> fInfos) {
        this.filterInfo = fInfos;
    }

    public void addServletMapping(String servletName, String urlPattern) {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
            logger.logp(Level.FINE, CLASS_NAME, "addServletMapping", "adding servletName->" + servletName + ", urlPattern->" + urlPattern);
        List<String> mappings = this.servletMappings.get(servletName);

        if (mappings == null) {
            IServletConfig scon = getServletInfo(servletName);
            mappings = new ArrayList<String>();
            mappings.add(urlPattern);

            // add to web group mappings
            servletMappings.put(servletName, mappings);

            // add to servlet's config mapping

            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
                logger.logp(Level.FINE, CLASS_NAME, "addServletMapping", "servletInfo --> " + servletInfo + " servletName --> " + servletName);
            }
            scon.setMappings(mappings);
        } else {
            boolean found = false;
            for (String curPattern : mappings) {
                if (urlPattern.equals(curPattern)) {
                    found = true;
                    break; // we know we don't need to add it now
                }
            }

            if (!found) {
                mappings.add(urlPattern);

                // no need to set the mappings in the ServletConfig
                // because it already points to the mappings List
                // and all we need to to is update the list.
            }
        }
    }

    /**
     * Method setVirtualHostName.
     * 
     * @param string
     */
    public void setVirtualHostName(String string) {
        this.virtualHost = string;
    }

    /**
     * Method setPrecompileJSPs.
     * 
     * @param b
     */
    public void setPrecompileJSPs(boolean b) {
        this.precompileJsps = b;
    }

    public boolean getPreCompileJSPs() {
        return this.precompileJsps;
    }

    /**
     * Returns the autoLoadFilters.
     * 
     * @return boolean
     */
    public boolean isAutoLoadFilters() {
        return this.autoLoadFilters;
    }

    /**
     * Sets the autoLoadFilters.
     * 
     * @param autoLoadFilters
     *            The autoLoadFilters to set
     */
    public void setAutoLoadFilters(boolean autoLoadFilters) {
        this.autoLoadFilters = autoLoadFilters;
    }

    /**
     * @param locale
     * @return String
     */
    public String getLocaleEncoding(Locale locale) {
        if (localeMap == null)
            return null;

        String encoding = (String) localeMap.get(locale.toString());
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
            logger.logp(Level.FINE, CLASS_NAME, "getLocaleEncoding", "locale->" + locale);
        }

        if (encoding == null) {
            encoding = (String) localeMap.get(locale.getLanguage() + "_" + locale.getCountry());
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
                logger.logp(Level.FINE, CLASS_NAME, "getLocaleEncoding", "locale->" + locale + ", language->" + locale.getLanguage() + ", country->"
                        + locale.getCountry());
            }
            if (encoding == null) {
                if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
                    logger.logp(Level.FINE, CLASS_NAME, "getLocaleEncoding", "locale->" + locale + ", language->" + locale.getLanguage());
                }
                encoding = (String) localeMap.get(locale.getLanguage());
            }
        }

        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
            logger.logp(Level.FINE, CLASS_NAME, "getLocaleEncoding", "encoding->" + encoding);
        }
        return encoding;
    }

    public void addLocaleEncodingMap(String locale, String encoding) {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
            logger.logp(Level.FINE, CLASS_NAME, "addLocaleEncodingMap", "locale->" + locale + ", encoding->" + encoding);
        }
        if (localeMap == null)
            localeMap = new HashMap();

        localeMap.put(locale, encoding);
    }

    /**
     * @return
     */
    public boolean isMimeFilteringEnabled() {
        return this.isMimeFilteringEnabled;
    }

    /**
     * @return
     */
    public boolean isServlet2_4() {
        return (this.version == 24);
    }

    /**
     * @return
     */
    public boolean isServlet2_5() {
        return (this.version == 25);
    }

    /**
     * @return
     */
    public boolean isServlet2_4OrHigher() {
        return (this.version >= 24);
    }

    /**
     * @return
     */
    public com.ibm.ws.webcontainer.webapp.WebApp getWebApp() {
        return this.webApp;
    }

    /**
     * @param app
     */
    public void setWebApp(com.ibm.ws.webcontainer.webapp.WebApp app) {
        this.webApp = app;
    }

    /**
     * @param servletName
     */
    public void removeServletMappings(String servletName) {
        this.servletMappings.remove(servletName);
    }

    /**
     * @param contextRoot
     */
    public void setContextRoot(String contextRoot) {
        this.contextRoot = contextRoot;

    }

    /**
     * @return
     */
    public String getContextRoot() {
        return this.contextRoot;
    }

    /**
     * @param i
     */
    public void setVersion(int i) {
        this.version = i;
    }

    public int getVersion() {
        return this.version;
    }

    /**
     * @return
     */
    public boolean isSyncToThreadEnabled() {
        return this.isSyncToThreadEnabled;
    }

    /**
     * @param b
     */
    public void setSyncToThreadEnabled(boolean b) {
        this.isSyncToThreadEnabled = b;
    }

    public int getModuleStartupWeight() {
        return this.moduleStartupWeight;
    }

    public void setModuleStartupWeight(int moduleStartupWeight) {
        this.moduleStartupWeight = moduleStartupWeight;
    }

    // LIDB3816
    public HashMap getCodeErrorPages() {
        return this.codeErrorPages;

    }

    // LIDB3816
    public HashMap getExceptionErrorPages() {
        return this.exceptionErrorPages;
    }

    // LIDB3518-1.2 06/26/07 mmolden ARD
    /**
     * @return Returns the ardDispatchType.
     */
    public String getArdDispatchType() {
        return this.ardDispatchType;
    }

    /**
     * @param ardDispatchType
     *            The ardDispatchType to set.
     */
    public void setArdDispatchType(String ardDispatchType) {
        this.ardDispatchType = ardDispatchType;
        if ((this.ardDispatchType).equals("CLIENT_SIDE") || (this.ardDispatchType).equals("SERVER_SIDE"))
            this.ardEnabled = true;
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
            logger.logp(Level.FINE, CLASS_NAME, "setArdDispatchType", " type --> " + ardDispatchType);
        }
    }

    /**
     * @return Returns the ardEnabled.
     */
    public boolean isArdEnabled() {
        return this.ardEnabled;
    }

    // LIDB3518-1.2 06/26/07 mmolden ARD

    public void setMetadataComplete(boolean b) {
        this.metaDataComplete = b;
    }

    public boolean isMetadataComplete() {
        return this.metaDataComplete;
    }

    public void addClassesToScan(List<Class<?>> list) {
        this.classesToScan.addAll(list);
    }

    public List<Class<?>> getClassesToScan() {
        return this.classesToScan;
    }

    // Start PK66137
    /**
     * @return
     */
    public boolean isSystemApp() {
        return this.isSystemApp;
    }

    /**
     * @param b
     */
    public void setSystemApp(boolean b) {
        this.isSystemApp = b;
    }

    // End PK66137

    public void addUriMappedFilterInfo(IFilterMapping fmInfo) {
        if (uriFilterMappingInfos == null) {
            uriFilterMappingInfos = new ArrayList<IFilterMapping>();
        }
        uriFilterMappingInfos.add(fmInfo);
    }

    public void addServletMappedFilterInfo(IFilterMapping fmInfo) {
        if (servletFilterMappingInfos == null) {
            servletFilterMappingInfos = new ArrayList<IFilterMapping>();
        }
        servletFilterMappingInfos.add(fmInfo);
    }

    public List<IFilterMapping> getUriFilterMappings() {
        return this.uriFilterMappingInfos;
    }

    public List<IFilterMapping> getServletFilterMappings() {
        return this.servletFilterMappingInfos;
    }

    public Map<String, ? extends ServletRegistration> getServletInfoMap() {
        // TODO Auto-generated method stub
        return this.servletInfo;
    }
    
    public Map<String, ? extends FilterRegistration> getFilterInfoMap() {
        // TODO Auto-generated method stub
        return this.filterInfo;
    }

	public void addDynamicServletRegistration(String servletName,
			Dynamic dynamicServletRegistration) {
		if (this.dynamicServletRegistrationMap==null){
			this.dynamicServletRegistrationMap = new HashMap <String, Dynamic> ();
		}
		this.dynamicServletRegistrationMap.put(servletName,dynamicServletRegistration);
	}

	public Map<String, ? extends Dynamic> getDynamicServletRegistrations() {
		return this.dynamicServletRegistrationMap;
	}
	
	public void setLibBinPathList(List<String> libBinPathList) {
		this.libBinPathList = libBinPathList;
	}
	
	@Override
    public List<String> getLibBinPathList() {
		return libBinPathList;
	}

    @Override
    public boolean isJCDIEnabled() {
        return false;
    }

    @Override
    public void setJCDIEnabled(boolean b) {}
	
}
