//IBM Confidential OCO Source Material
//5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997-2007
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
//
//Revisions:
//Defect 212433  2004/07/20  Stepping into _jspService lands at a unexpected location
//Defect 217758  2005/07/20  trackDependencies should require reloadEnabled to be true
//APAR   PK01617 2005/03/11  JspConfiguration object being cached within the JSPExtensionServletWrapper class causing problems - slm
//Defect 268176.1  2005/05/20  Welcome file wrappers are not checked for resource existence
//Defect 315405  2005/10/21  jsp container fails to call JSPErrorReport.setTargetServletName
//APAR   PK27620 2006/08/24  SERVLET FILTER IS NOT CALLED IN V6 FOR URL RESOURCES WHEN THESE ARE NOT FOUND.  IN V5, THE FILTER IS ALWAYS CALLED
//APAR   PK31377 2007/04/13  Servlet filter is not called for URL resources - Jay Sartoris
//APAR   PK45107 2007/08/07  EXCEPTION FROM SERVLET FILTER IS NOT PROPAGATED TO CLIENT - Maxim Moldenhauer
//Defect 651265  2010/05/12  Trace Entry and Exit improvement - Anup Aggarwal 
//APAR   PM22919 09/29/10    Rethrow exception back to the dispatch caller when dispatched JSP failed to compile  - Phu Dinh

package com.ibm.ws.jsp.webcontainerext;

import java.io.File;
import java.io.FilePermission;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.PrivilegedActionException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Properties;											//PK76810

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.ws.jsp.Constants;
import com.ibm.ws.jsp.JspCoreException;
import com.ibm.ws.jsp.JspOptions;
import com.ibm.ws.jsp.translator.utils.FileLocker; 						//PK76810
import com.ibm.ws.jsp.configuration.JspConfigurationManager;//PK01617
import com.ibm.ws.jsp.runtime.JspClassInformation;
import com.ibm.ws.jsp.taglib.TagLibraryCache;
import com.ibm.ws.jsp.translator.utils.JspTranslatorUtil;
import com.ibm.wsspi.jsp.compiler.JspCompilerResult;
import com.ibm.wsspi.jsp.context.translation.JspTranslationContext;
import com.ibm.wsspi.jsp.resource.JspInputSource;
import com.ibm.wsspi.jsp.resource.translation.JspResources;
import com.ibm.wsspi.webcontainer.WebContainerRequestState;
import com.ibm.wsspi.webcontainer.servlet.GenericServletWrapper;
import com.ibm.wsspi.webcontainer.servlet.IServletConfig;
import com.ibm.wsspi.webcontainer.servlet.IServletContext;
import com.ibm.wsspi.webcontainer.WCCustomProperties;                   //PM22919
import com.ibm.wsspi.webcontainer.WebContainer;							//PK76810
import com.ibm.wsspi.jsp.resource.JspClassFactory;						//PK76810

public abstract class AbstractJSPExtensionServletWrapper extends GenericServletWrapper {
    static protected Logger logger;

    private static final String CLASS_NAME = "com.ibm.ws.jsp.webcontainerext.JSPExtensionServletWrapper";
    static {
        logger = Logger.getLogger("com.ibm.ws.jsp");
    }

    // begin 220676: add iVar for wrapper to know when translation occured.
    private static final String JSP_TRANSLATION_TIME_STAMP = "jsp_translation_ts";
    private static final String JSP_TRANSLATION_CHECKED_THIS_REQUEST = "jsp_translation_checked";
    
    protected Long lastTranslationTime = null;

    // end 220676: add iVar for wrapper to know when translation occured and
    // request attr for looking this up.

    private static String separatorString = System.getProperty("line.separator"); // Defect
                                                                                    // 211450

    protected JspOptions options = null;
    protected JspConfigurationManager configManager = null;// PK01617
    protected TagLibraryCache tlc = null;
    protected JspTranslationContext tcontext = null;
    protected CodeSource codeSource = null;
    protected JspInputSource inputSource = null;
    protected JspResources jspResources = null;
    protected List dependentsList = null;
    protected String versionNumber = null;
    protected boolean classloaderCreated = false;
    protected long lastCheck = 0;
    protected boolean debugClassFile = true; // defect 272935
    
    protected Boolean recompiledJspOnRestart = null;//used with recompileJspOnRestart param

    public static boolean dispatcherRethrowSERROR = WCCustomProperties.DISPATCHER_RETHROW_SERROR;       //PM22919

    public AbstractJSPExtensionServletWrapper(IServletContext parent, 
                                      JspOptions options, 
                                      JspConfigurationManager configManager, 
                                      TagLibraryCache tlc,
                                      JspTranslationContext context, 
                                      CodeSource codeSource) throws Exception {// PK01617
        super(parent);
        this.options = options;
        this.configManager = configManager;// PK01617
        this.tlc = tlc;
        this.tcontext = context;
        this.codeSource = codeSource;
        if (options.isTrackDependencies())
            dependentsList = new ArrayList();
    }

    public void initialize(IServletConfig config) throws Exception {
        if (config.getFileName() == null || config.getFileName().equals("")) {
            throw new UnavailableException(JspCoreException.getMsg("jsp.error.failed.to.find.resource", new Object[] { config.getFileName() }));
        }
        inputSource = tcontext.getJspInputSourceFactory().createJspInputSource(config.getFileName());
        
        super.initialize(config);
    }

    public void loadOnStartupCheck ()throws Exception  {
	    if (servletConfig.isLoadOnStartup()) {
            checkForTranslation(null);
	        servletConfig.setClassName(jspResources.getPackageName() + "." + jspResources.getClassName());
        }
    }

    public void handleRequest(ServletRequest req, ServletResponse res) throws Exception {
        if (req instanceof HttpServletRequest) {
            HttpServletRequest hreq = (HttpServletRequest) req;
            try {
                WebContainerRequestState reqState = WebContainerRequestState.getInstance(true);
                //if we already called checkForTranslation, the reqState attribute will have already been set and we won't try and re-translate
                if (reqState.getAttribute(AbstractJSPExtensionServletWrapper.JSP_TRANSLATION_CHECKED_THIS_REQUEST)==null) {
                    if (System.getSecurityManager() != null) {
                        try {
                            final HttpServletRequest finalReq = (HttpServletRequest) req;
                            AccessController.doPrivileged(new java.security.PrivilegedExceptionAction() {
                                public Object run() throws JspCoreException {
                                    checkForTranslation((HttpServletRequest) finalReq);
                                    return null;
                                }
                            });
                        } catch (PrivilegedActionException pae) {
                            com.ibm.ws.ffdc.FFDCFilter
                                    .processException(pae, "com.ibm.ws.jsp.webcontainerext.JSPExtensionServletWrapper.handleRequest", "143", this);
                            throw (JspCoreException) pae.getException();
                        }
                    } else {
                        checkForTranslation((HttpServletRequest) req);
                    }
                }
                
                //need to remove this attribute so that we call checkForTranslation on included jsps
                reqState.removeAttribute(AbstractJSPExtensionServletWrapper.JSP_TRANSLATION_CHECKED_THIS_REQUEST);

                if (preCompile(hreq)) {
                    // begin 220676: set request attribute indicating
                    // translation occured with timestamp.
                    if (hreq != null) {
                        hreq.setAttribute(JSP_TRANSLATION_TIME_STAMP, this.lastTranslationTime);
                    }
                    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)) {
                        logger.logp(Level.FINE, CLASS_NAME, "handleRequest", "precompile was requested for [" + this.getJspUri() + "] last translation time =["
                                + this.lastTranslationTime + "]");
                    }
                    // end 220676: set request attribute indicating translation
                    // occured with timestamp.
                    return;
                }

            } catch (JspCoreException e) {
                com.ibm.ws.ffdc.FFDCFilter.processException(e, "com.ibm.ws.jsp.webcontainerext.JSPExtensionServletWrapper.translateJsp", "259", this);
                int code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                if (e.getCause() != null && e.getCause() instanceof java.io.FileNotFoundException) {
                    code = HttpServletResponse.SC_NOT_FOUND;
                }
                Throwable t = null;
                Throwable rootCause = e;
                while ((t = rootCause.getCause()) != null) {
                    rootCause = t;
                }
                // Defect 211450
                JSPErrorReport jser = new JSPErrorReport(rootCause.getLocalizedMessage(), rootCause);
                jser.setStackTrace(rootCause.getStackTrace());
                jser.setErrorCode(code);
                jser.setTargetServletName(super.getServletName());  // Defect 315405
                
                //PM22919
                boolean isIncluded = req.getAttribute("javax.servlet.include.servlet_path") != null;            //PM22919
                boolean isForwarded = req.getAttribute("javax.servlet.forward.servlet_path") != null;           //PM22919

                if (dispatcherRethrowSERROR && (isIncluded || isForwarded))                     //PM22919   
                    throw jser;                                                                 //PM22919
                //end PM22919
                
                context.sendError(hreq, (HttpServletResponse) res, jser);
                return;
            }
            super.handleRequest(req, res);
            loadClassInformation();
        }
    }

    public String getJspUri() {
        return inputSource.getRelativeURL();
    }

    protected void loadClassInformation() {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)) {
            logger.logp(Level.FINE,CLASS_NAME,"loadClassInformation", "classloaderCreated: " + classloaderCreated + " getTarget(): " + getTarget());
        }
        if (classloaderCreated) {
            JspClassInformation jspClassInformation = null;
            synchronized (this) {
                if (getTarget() instanceof JspClassInformation) {    
                    jspClassInformation = (JspClassInformation) getTarget();
                } else {
                    try {
                        jspClassInformation = (JspClassInformation)Class.forName(jspResources.getPackageName() + "." + jspResources.getClassName(), true, getTargetClassLoader()).newInstance();
                        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)) {
                            logger.logp(Level.FINE,CLASS_NAME,"loadClassInformation", "created a temporary JspClassInformation object to get dependencies: " + jspClassInformation);
                        }
                    } catch (Throwable t){
                        if (logger!=null) {
                            logger.logp(Level.INFO,CLASS_NAME,"loadClassInformation","Exception caught checking JSP dependencies:",t);
                        }
                    }        			
                }

                if (jspClassInformation != null) {
                    if (options.isTrackDependencies()) {
                        dependentsList.clear();
                        String[] dependents = jspClassInformation.getDependants();
                        if (dependents != null) {
                            for (int i = 0; i < dependents.length; i++) {
                                JspDependent jspDependent = new JspDependent(dependents[i], tcontext);
                                dependentsList.add(jspDependent);
                            }
                        }
                    }

                    versionNumber = jspClassInformation.getVersionInformation();
                    // begin 228118: JSP container should recompile if debug enabled
                    // and jsp was not compiled in debug.
                    //if (options.isDebugEnabled()) {
                    debugClassFile = jspClassInformation.isDebugClassFile(); // defect 272935 
                    //}
                    // end 228118: JSP container should recompile if debug enabled
                    // and jsp was not compiled in debug.
                }
            }       
        }
    }

    // 247773 syncToOsThread changes, add sync around checkForTranslation
    protected void checkForTranslation(HttpServletRequest req) throws JspCoreException {
        if (options.isZOS()) {
            preinvokeCheckForTranslation(req);
/*            
            Object credToken = null;
            WSLoginLocalOSExtension localSecurity = null;
            try {
                localSecurity = WSLoginLocalOSExtensionFactory.getInstance();
                if (localSecurity.isThreadLocalApplicationSyncEnabled()) {
                    credToken = ServerIdentityHelper.getServerIdentityHelper().push();
                }
                _checkForTranslation(req);
            } finally {
                if (localSecurity != null) {
                    if (localSecurity.isThreadLocalApplicationSyncEnabled()) {
                        ServerIdentityHelper.getServerIdentityHelper().pop(credToken);
                    }
                }
            }
*/            
        } else {
            _checkForTranslation(req);
        }
        //create reqState and set attribute to say that we've already translated this request
        WebContainerRequestState reqState = WebContainerRequestState.getInstance(true);
        reqState.setAttribute(AbstractJSPExtensionServletWrapper.JSP_TRANSLATION_CHECKED_THIS_REQUEST, Boolean.TRUE);
    }

    protected void _checkForTranslation(HttpServletRequest req) throws JspCoreException {
        final boolean isAnyTraceEnabled=com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled(); 
        if (isAnyTraceEnabled&&logger.isLoggable(Level.FINER)) {
            logger.entering(CLASS_NAME, "_checkForTranslation", "enter checkForTranslation sync block for " + inputSource.getRelativeURL());
        }
        synchronized (this) {
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)) {
                logger.logp(Level.FINE, CLASS_NAME, "checkForTranslation", "Entered checkForTranslation sync block for " + inputSource.getRelativeURL());
            }
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE) && versionNumber != null && jspResources != null) {
                logger.logp(Level.FINE, CLASS_NAME, "checkForTranslation", "Classfile: [" + jspResources.getClassName() + "] version: [" + versionNumber + "]");
            }
            classloaderCreated = false;
            // if (versionNumber !=null && jspResources != null) {
            // System.out.println("Classfile: ["+jspResources.getClassName()+"]
            // version: [" + versionNumber+"]");
            // }
            if (options.isDisableJspRuntimeCompilation() == false) {
                if ((System.currentTimeMillis() - lastCheck) > options.getReloadInterval()) {
                    boolean translationRequired = false;

                    if (jspResources == null) {
                        jspResources = tcontext.getJspResourcesFactory().createJspResources(inputSource);
                        if (options.isReloadEnabled() == false) {
                            translationRequired = jspResources.isOutdated();
                            // begin 228118: JSP container should recompile if
                            // debug enabled and jsp was not compiled in debug.
                            if (translationRequired == false && options.isDebugEnabled()) {
                                translationRequired = (this.debugClassFile == false);
                                // defect 272935 begin
                                if (translationRequired && jspResources.getGeneratedSourceFile().getParentFile().exists() == false){
                                    boolean rc = jspResources.getGeneratedSourceFile().getParentFile().mkdirs();
                                    if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINEST)){
                                        logger.logp(Level.FINEST, CLASS_NAME, "_checkForTranslation", (rc?"Created":"Unable to create") +" directory for generated source file ["+jspResources.getGeneratedSourceFile().getParentFile() +"]");
                                    }
                                }
                                //  defect 272935 end                                
                            }
                            // end 228118: JSP container should recompile if
                            // debug enabled and jsp was not compiled in debug.
                        }
                        if (servletConfig != null) {
                            servletConfig.setClassName(jspResources.getPackageName() + "." + jspResources.getClassName());
                        }
                    }

                    jspResources.setCurrentRequest(req);

                    if (options.isReloadEnabled()) {
                        translationRequired = jspResources.isOutdated();
                        /*
                         * No longer needed as config is created for each new
                         * translation, PK01617
                         * 
                         * if (translationRequired && servletConfig != null) {
                         * //defect 200435 config =
                         * config.getConfigManager().getConfigurationForUrl(servletConfig.getFileName()); }
                         */
                        // begin 228118: JSP container should recompile if debug
                        // enabled and jsp was not compiled in debug.
                        if (translationRequired == false && options.isDebugEnabled()) {
                            translationRequired = (this.debugClassFile == false);
                            // defect 272935 begin
                            if (translationRequired && jspResources.getGeneratedSourceFile().getParentFile().exists() == false){
                                boolean rc = jspResources.getGeneratedSourceFile().getParentFile().mkdirs();
                                if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINEST)){
                                    logger.logp(Level.FINEST, CLASS_NAME, "_checkForTranslation", (rc?"Created":"Unable to create") +" directory for generated source file ["+jspResources.getGeneratedSourceFile().getParentFile() +"]");
                                }
                            }
                            // defect 272935 end
                        }
                        // end 228118: JSP container should recompile if debug
                        // enabled and jsp was not compiled in debug.
                        if (options.isTrackDependencies() && translationRequired == false) {
                            //PK57724 01/29/2008  Reloading of dependencies does not work on first request after a webapp start.  - Jay Sartoris
                            //PK57724 start
                            boolean needToReset = false;
                            JspClassInformation tmpJCI = null;

                            //if getTarget is null, we can assume it's the first request since starting the app.
                            if (getTarget() == null && (dependentsList == null || dependentsList.isEmpty())) {
                                if (getTargetClassLoader() == null) {
                                    try {
                                        createClassLoader();
                                    } catch (UnsupportedClassVersionError e) {
                                        if (logger.isLoggable(Level.FINE)) {
                                            logger.logp(Level.FINE,CLASS_NAME,"_checkForTranslation", "classloader error - recompile");
                                        }       
                                        translationRequired=true;
                                    }
                                    needToReset = true;
                                        
                                }
                                if (logger.isLoggable(Level.FINE)) {
                                    logger.logp(Level.FINE,CLASS_NAME,"_checkForTranslation", "getTargetClassLoader(): " + getTargetClassLoader());
                                }

                                try {
                                    tmpJCI = (JspClassInformation)Class.forName(jspResources.getPackageName() + "." + jspResources.getClassName(), true, getTargetClassLoader()).newInstance();
                                    if (logger.isLoggable(Level.FINE)) {
                                        logger.logp(Level.FINE,CLASS_NAME,"_checkForTranslation", "created a temporary JspClassInformation object to get dependencies: " + tmpJCI);
                                    }
                                } catch (Throwable t){
                                    if (logger!=null)
                                        logger.logp(Level.INFO,CLASS_NAME,"_checkForTranslation","Exception caught checking JSP dependencies:",t);
                                }
                                
                                if (tmpJCI != null) {
                                    dependentsList.clear();
                                    String[] dependents = tmpJCI.getDependants();
                                    if (dependents != null) {
                                        for (int i = 0; i < dependents.length; i++) {
                                            JspDependent jspDependent = new JspDependent(dependents[i], tcontext);
                                            dependentsList.add(jspDependent);
                                        }
                                    }
                                    tmpJCI = null;
                                }                               
                            } //PK57724 end
                            translationRequired = isDependentOutdated();
                            //PK57724 start
                            if (needToReset) {
                                setTargetClassLoader(null);
                                classloaderCreated = false;
                            } //PK57724 end
                        }
                        if (translationRequired == false && jspResources.isExternallyTranslated()) {
                            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)) {
                                logger.logp(Level.FINE, CLASS_NAME, "checkForTranslation", inputSource.getRelativeURL() + " has been externally translated");
                            }
                            setTargetClassLoader(null);
                            jspResources.sync();
                        }
                    }

                    //recompiledJspOnRestart will be null the first time this ServletWrapper is executed after a restart
                    if (options!=null && options.isRecompileJspOnRestart() &&
                            recompiledJspOnRestart==null) {
                        translationRequired = true;
                        recompiledJspOnRestart=true;
                    }
                    
                    if (!translationRequired) {
                        if (getTargetClassLoader() == null) {
                            if (getTarget() != null) {
                                prepareForReload();
                            }
                            try {
                                createClassLoader();
                            } catch (UnsupportedClassVersionError e) {
                                translationRequired=true;
                                if (logger.isLoggable(Level.FINE)) {
                                    logger.logp(Level.FINE,CLASS_NAME,"_checkForTranslation", "UnsupportedClassVersionError - recompile jsp");
                                }       
                            }
                        }
                    }
                    if (translationRequired) {
                        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)) {
                            logger.logp(Level.FINE, CLASS_NAME, "checkForTranslation", "Translation required for " + inputSource.getRelativeURL());
                        }
                      //PK76810 - Starts
                        boolean compiled = translateJsp();
                        if (options.isZOS()){			
                        	if (!compiled && jspResources.isOutdated()){              
                      		  int reCompile = 5;
                      		  Properties webContainerProperties = WebContainer.getWebContainerProperties();
                      		  if (webContainerProperties != null){
                      			  try{
                      				  reCompile = Integer.parseInt(webContainerProperties.getProperty("com.ibm.ws.jsp.zosrecompile", "5"));
                      			  }
                      			  catch (NumberFormatException e){
                      				  if (logger.isLoggable(Level.FINE)) {
                      					  logger.logp(Level.FINE,CLASS_NAME,"translateJsp", "NumberFormatException in the com.ibm.ws.jsp.zosReCompile property...default to 5 times");
                      				  }
                      				  reCompile = 5;
                      			  }
                      		  }
                      		  if (logger.isLoggable(Level.FINE)) {
                      			  logger.logp(Level.FINE,CLASS_NAME,"checkForTranslation", "Compile fails and isOutDated, retrying up to " + reCompile +" times");
                      		  }
                      	  
                      		  for (int i = 0; i < reCompile;i++){
                      			  compiled = translateJsp();
                      			  if (compiled || !(jspResources.isOutdated())){
                      				  if (logger.isLoggable(Level.FINE)) {
                      					  logger.logp(Level.FINE,CLASS_NAME,"checkForTranslation", (compiled?"Recompiled":"Resource up to date"));
                      				  }
                      				  break;
                      			  }
                      			  if (reCompile == i+1){						//last try but still fail... so give up
                      				  if (logger.isLoggable(Level.FINE)) {
                      					  logger.logp(Level.FINE,CLASS_NAME,"checkForTranslation", "Compile still fails after ["+reCompile+"] attempts");
                      				  }
                      				  JspCoreException e= new JspCoreException("jsp.error.compile.failed");
                                      if (isAnyTraceEnabled&&logger.isLoggable (Level.FINE)){
                                          logger.exiting(CLASS_NAME,"_checkForTranslation", " Compile fail for "+ inputSource.getRelativeURL());
                                      } //d651265
                      				  throw e;
                      			  }
                      		  }
                      	  }
                      	  if (logger.isLoggable(Level.FINE)) {
                      		  logger.logp(Level.FINE,CLASS_NAME,"checkForTranslation",(compiled?"Compiled":"Resource is updated") + " successfully");
                      	  }
                        }
                        //PK76810 - Ends
                        setTargetClassLoader(null);
                        this.lastTranslationTime = new Long(System.currentTimeMillis());// add
                                                                                        // 220676:
                                                                                        // add
                                                                                        // iVar
                                                                                        // for
                                                                                        // wrapper
                                                                                        // to
                                                                                        // know
                                                                                        // when
                                                                                        // translation
                                                                                        // occured.
                    }

                    lastCheck = System.currentTimeMillis();
                }
            }
            if (getTargetClassLoader() == null) {
                if (getTarget() != null) {
                    prepareForReload();
                }
                try {
                    createClassLoader();
                } catch (UnsupportedClassVersionError e) {
                    if (logger.isLoggable(Level.FINE)) {
                        //classloader should have already been created for a class that would have caused this error
                        logger.logp(Level.FINE,CLASS_NAME,"_checkForTranslation", "UnsupportedClassVersionError");
                    }       
                }
            }
        }
        if (isAnyTraceEnabled&&logger.isLoggable(Level.FINER)) {
            logger.exiting(CLASS_NAME, "_checkForTranslation", "Exiting checkForTranslation sync block for " + inputSource.getRelativeURL());
        }
    }

    protected boolean translateJsp() throws JspCoreException {			//PK76810
        // 247773: Move app syncToOsThread to checkForTranslation
        JspCompilerResult compilerResult = JspTranslatorUtil.translateJspAndCompile(jspResources, tcontext, configManager.getConfigurationForUrl(inputSource
                .getRelativeURL()), options, tlc, false, Collections.EMPTY_LIST);
        
      //PK76810 - Starts  
        if (compilerResult == null && options.isZOS()){
        	int fileLockRetrying = 240;
        	Properties webContainerProperties = WebContainer.getWebContainerProperties();
        	if (webContainerProperties != null){
        		try{
        			fileLockRetrying = Integer.parseInt(webContainerProperties.getProperty("com.ibm.ws.jsp.zosfilelockretrying", "240"));
        		}
        		catch (NumberFormatException e){
        			if (logger.isLoggable(Level.FINE)) {
                        logger.logp(Level.FINE,CLASS_NAME,"translateJsp", "NumberFormatException in the zOSFileLockRetrying property...default to 240 seconds");
                    }
        			fileLockRetrying = 240;
        		}
        	}
        	
        	if (logger.isLoggable(Level.FINE)) {
                logger.logp(Level.FINE,CLASS_NAME,"translateJsp", "FileLock failed in translateJspAndCompile...retrying up to " + 
                							fileLockRetrying + " seconds");  												
            }
        	boolean fileLock = false;
        	FileLocker zosFileLocker = (FileLocker) new JspClassFactory().getInstanceOf("FileLocker");
        	if (zosFileLocker == null){
        		JspCoreException exception = new JspCoreException("jsp.error.file.locker.failed"); 
                throw exception;
        	}

        	String fileLockString = jspResources.getInputSource().getRelativeURL();
        	for (int i = 0; i < fileLockRetrying; i++ ){
        		try{
        			fileLock = zosFileLocker.obtainFileLock(fileLockString);
        			if (fileLock){
        				zosFileLocker.releaseFileLock(fileLockString);
        				if (logger.isLoggable(Level.FINE))																		
                            logger.logp(Level.FINE,CLASS_NAME,"translateJsp", "FileLock retrying succeeded, releaseFileLock");  
        				return false;
        			}
        			Thread.sleep(1000);
        		}
        		catch (InterruptedException e){
        			JspCoreException exception = new JspCoreException("jsp.error.file.lock.retrying.failed"); 
                    throw exception;
        		}
        	}
        	if (logger.isLoggable(Level.FINE)) {
                logger.logp(Level.FINE,CLASS_NAME,"translateJsp", "FileLock failed in translateJspAndCompile...retrying has expired");
            }
        	JspCoreException e= new JspCoreException("jsp.error.compile.failed");
            throw e;
        }
        //PK76810 - Ends 
        
        if (compilerResult.getCompilerReturnValue() != 0) {
            JspCoreException e = new JspCoreException("jsp.error.compile.failed", new Object[] { inputSource.getRelativeURL(),
                    separatorString + compilerResult.getCompilerMessage() }); // Defect
                                                                                // 211450
            throw e;
        }
        return true;										//PK76810
    }

    protected void createClassLoader() {
        if (jspResources == null) {
            jspResources = tcontext.getJspResourcesFactory().createJspResources(inputSource);
            if (servletConfig != null) {
                servletConfig.setClassName(jspResources.getPackageName() + "." + jspResources.getClassName());
            }
        }
        if (options.isDisableJspRuntimeCompilation() && options.isUseFullPackageNames()) {
            classloaderCreated = true;
            return;
        }
        URL[] urls = null;
        try {
            PermissionCollection permissionCollection = createPermissionCollection();
            /*
            PermissionCollection permissionCollection = Policy.getPolicy().getPermissions(codeSource);

            ClassLoader loader = tcontext.getJspClassloaderContext().getClassLoader();
            if (loader instanceof ReloadableClassLoader || loader instanceof CompoundClassLoader) {
                Map csPerms = null;
                if (loader instanceof ReloadableClassLoader)
                    csPerms = ((ReloadableClassLoader) loader).getCodeSourcePermissions();
                else
                    csPerms = ((CompoundClassLoader) loader).getCodeSourcePermissions();
                DynamicPolicy policy = DynamicPolicyFactory.getInstance();
                if (policy != null) {
                    URL webinfURL = new URL(codeSource.getLocation() + "/WEB-INF/classes/*");
                    CodeSource webinfCS = new CodeSource(webinfURL, null);
                    permissionCollection = ((DynamicPolicy) policy).getPermissions(webinfCS, csPerms);
                }
            }
            */
            
            String sourceDir = jspResources.getGeneratedSourceFile().getParentFile().toString() + File.separator + "*";
            permissionCollection.add(new FilePermission(sourceDir, "read"));

            if (options.isUseFullPackageNames() == false) {
                urls = new URL[4];
                urls[0] = jspResources.getGeneratedSourceFile().getParentFile().toURL();
                urls[1] = options.getOutputDir().toURL();
                urls[2] = new File(tcontext.getRealPath("/WEB-INF/classes")
                        + inputSource.getRelativeURL().substring(0, inputSource.getRelativeURL().lastIndexOf("/") + 1)).toURL();
                urls[3] = new File(tcontext.getRealPath("/WEB-INF/classes")).toURL();
            } else {
                urls = new URL[2];
                urls[0] = options.getOutputDir().toURL();
                urls[1] = new File(tcontext.getRealPath("/WEB-INF/classes")).toURL();
            }
            JSPExtensionClassLoader jspLoader = new JSPExtensionClassLoader(urls, 
                                                                            tcontext.getJspClassloaderContext(), 
                                                                            jspResources.getClassName(), 
                                                                            codeSource, 
                                                                            permissionCollection);
            if (servletConfig != null && jspResources.getPackageName().equals(Constants.JSP_FIXED_PACKAGE_NAME)) {
                try {
                    jspLoader.loadClass(jspResources.getPackageName() + "." + jspResources.getClassName(), true);
                } catch (UnsupportedClassVersionError e) {
                   throw e;  
                } catch (Throwable e1) {
                    servletConfig.setClassName(Constants.OLD_JSP_PACKAGE_NAME + "." + jspResources.getClassName());
                }
            }

            setTargetClassLoader(jspLoader);
            classloaderCreated = true;
        } catch (MalformedURLException e) {
            com.ibm.ws.ffdc.FFDCFilter.processException(e, "com.ibm.ws.jsp.webcontainerext.JSPExtensionProcessor.createClassLoader", "312", this);
            logger.logp(Level.WARNING, CLASS_NAME, "createClassLoader", "failed to create JSP class loader", e);
        }
    }

    /* A request to a JSP page that has a request parameter with name jsp_precompile
     * is a precompilation request. This method determines if it is this type of request.*/
    boolean preCompile(HttpServletRequest request) throws ServletException {
        String queryString = request.getQueryString();
        if (queryString == null)
            return (false);
        int start = queryString.indexOf(Constants.PRECOMPILE);
        if (start < 0)
            return (false);
        queryString = queryString.substring(start + Constants.PRECOMPILE.length());
        if (queryString.length() == 0)
            return (true); // ?jsp_precompile
        if (queryString.startsWith("&"))
            return (true); // ?jsp_precompile&foo=bar...
        if (!queryString.startsWith("="))
            return (false); // part of some other name or value
        int limit = queryString.length();
        int ampersand = queryString.indexOf("&");
        if (ampersand > 0)
            limit = ampersand;
        String value = queryString.substring(1, limit);
        if (value.equals("true"))
            return (true); // ?jsp_precompile=true
        else if (value.equals("false"))
            //The spec makes it clear that even if the value is set to false, we should behave as if it is set.
            return (true); // ?jsp_precompile=false
        else
            throw new ServletException("Cannot have request parameter " + Constants.PRECOMPILE + " set to " + value);

    }

    protected boolean isDependentOutdated() throws JspCoreException {
        boolean outdated = false;
        for (Iterator itr = dependentsList.iterator(); itr.hasNext();) {
            JspDependent jspDependent = (JspDependent) itr.next();

            if (jspDependent.isOutdated()) {
                if (jspResources.getGeneratedSourceFile().exists() == false) {
                    jspResources.getGeneratedSourceFile().getParentFile().mkdirs();
                }
                if (jspDependent.getDependentFilePath().endsWith(".tld")) {
                    tlc.reloadTld(jspDependent.getDependentFilePath(), jspDependent.getTimestamp());
                }
                outdated = true;
                break;
            }
        }
        return outdated;
    }
    
    //Defect 268176.1 
    public boolean isAvailable() {
        boolean available = false;
        String relativeURL = inputSource.getRelativeURL();
        String realPath = tcontext.getRealPath(relativeURL);
        if (options.isDisableJspRuntimeCompilation() == false) {
            available = new File(realPath).exists();
        } else {
            available = true;
        }
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINER)) {
            logger.logp(Level.FINER, CLASS_NAME, "isAvailable", "relativeURL ["+ relativeURL + "]  " +
                    "realPath ["+ realPath + "] is available [" + available + "]");
        }
        return available;

    }
    
    public JspResources getJspResources() {
        return jspResources;
    }
    
    protected abstract void preinvokeCheckForTranslation(HttpServletRequest req) throws JspCoreException;
    protected abstract PermissionCollection createPermissionCollection() throws MalformedURLException;
    
    public void load() throws Exception {
    	 if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINER)) {
             logger.logp(Level.FINER, CLASS_NAME, "load", "no op");
         }
         
        // do nothing, jsps don't have an init method and they do their own classloading.
    }
}
