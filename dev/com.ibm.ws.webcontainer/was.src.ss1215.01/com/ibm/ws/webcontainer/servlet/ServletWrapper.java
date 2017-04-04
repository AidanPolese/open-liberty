// IBM Confidential OCO Source Material
// 5639-D57,5630-A36,5630-A37,5724-D18 (C) COPYRIGHT International Business Machines Corp. 1997, 2008
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
//  CHANGE HISTORY
//Defect        Date        Modified By         Description
//--------------------------------------------------------------------------------------
//296440        08/05/05    todkap              cleanup ServletWrapper loadServlet call    WASCC.web.webcontainer    
//298927        08/17/05    todkap              improve filter handling for core    WASCC.web.webcontainer    
//299508        08/22/05    todkap              onServletStartService should be before onFilterStartDoFilter    WAS.webcontainer
//307739        09/23/05    todkap              excessive logging for errors during servlet.service    WASCC.web.webcontainer    
//324991        11/21/05    mmolden             61FVT: NullPtrExpn when logging out after admin console addNode
//329118        12/05/05    todkap              IServletWrapper use of com.ibm.ws.webcontainer.servlet.ServletC    WASCC.web.webcontainer    
//331617        12/12/05    todkap              61FVT: local/remote session IDs do not always match using RRD    WAS.httpsession    
//331802        12/15/05    mmolden             Destroy not called after UnavailableException
//306998.15     01/06/06    ekoonce             PERF: WAS tracing performance improvement 
//PK26183       08/21/06    mmulholl            ENSURE correct classloader is used to desearialize a class
//PK27620       08/23/06    cjhoward            SERVLET FILTER IS NOT CALLED IN V6 FOR URL RESOURCES WHEN THESE ARE NOT FOUND.  IN V5, THE FILTER IS ALWAYS CALLED
//399878        10/23/06    mmolden             70FVT: ExampleTestGroup.testPermanentUnavailableJsp() fails                                                                                                                                                    
//437503        06/07/07    mmolden             70FVT: Regression J2ee
//PK54805       10/25/07    ekoonce             USERWORKAREA SERVICE FAILS IN A SERVLET FORWARD SCENARIO
//PK50133       12/05/07    mmolden             JSPExtensionClassLoader objects causing OOM
//489973        12/31/07    mmolden             70FVT:ServletRequestListener not firing when registered in tld
//LIDB4293-1    01/07/08    ekoonce             Lightweight test environment
//511670        04/09/08    mmolden             70FVT: servlet destroy is not called                                                                                        
//PK64290       05/20/08    mmolden             SESSION LOSS WHEN USING ONLY URLREWRITING
//531478        06/20/08    mmolden             Null checks for Rahul in ServletWrapper
//521677.2      06/23/08    mmolden             70FVT: MyFaces: PreDestroy not called in TestBean
//PK66012       05/22/08    mmulholl            On destroy wait for a request to finish for ServletWrapper.destroyWaitTime 
//PK74129       11/18/08    jebergma            REQUESTDISPATCHER FORWARD TO STATIC FILE SERVING SERVLET DOES
//PK76117       12/10/08    mmulholl            Don't make servlet unavailable if UnavialableException is from a disptached resource
//569469        01/04/08    pmdinh              Add entry and exit trace to V7
//PK80340       02/35/09    mmulholl            Add isDefaultServlet() method
//PK82657	03/17/09    mconcini		JSPClassLoaderLimit does not include forwards and includes
//PK83258       06/09/09    mmulholl            Set attribute for security if default head or trace is about to be called.
//721610        11/02/11    anupag              PM51389:: set the correct value of javax.servlet.request.key_size
//PM50111       12/12/11    anupag              Only flush after service if filters are invoked and response not committed.
//
package com.ibm.ws.webcontainer.servlet;

import java.beans.Beans;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.reflect.Method;
import java.lang.NoSuchMethodException;
import java.lang.ClassNotFoundException;

import javax.servlet.GenericServlet;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.SingleThreadModel;
import javax.servlet.UnavailableException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.ejs.j2c.HandleList;
import com.ibm.ejs.ras.TraceNLS;
import com.ibm.websphere.csi.CSIException;
import com.ibm.websphere.servlet.error.ServletErrorReport;
import com.ibm.websphere.servlet.event.ServletErrorEvent;
import com.ibm.websphere.servlet.event.ServletEvent;
import com.ibm.websphere.servlet.filter.ChainedResponse;
import com.ibm.ws.container.Configuration;
import com.ibm.ws.container.Container;
import com.ibm.ws.security.util.AccessController;
import com.ibm.ws.webcontainer.WebContainer;
import com.ibm.ws.webcontainer.core.Command;
import com.ibm.ws.webcontainer.core.Request;
import com.ibm.ws.webcontainer.core.Response;
import com.ibm.ws.webcontainer.filter.WebAppFilterManager;
import com.ibm.ws.webcontainer.spiadapter.collaborator.IInvocationCollaborator;
import com.ibm.ws.webcontainer.srt.SRTServletRequest;
import com.ibm.ws.webcontainer.srt.WriteBeyondContentLengthException;
import com.ibm.ws.webcontainer.webapp.WebApp;
import com.ibm.ws.webcontainer.webapp.WebAppDispatcherContext;
import com.ibm.ws.webcontainer.webapp.WebAppErrorReport;
import com.ibm.ws.webcontainer.webapp.WebAppEventSource;
import com.ibm.ws.webcontainer.webapp.WebAppRequestDispatcher;
import com.ibm.ws.webcontainer.webapp.WebAppServletInvocationEvent;
import com.ibm.wsspi.injectionengine.InjectionException;
import com.ibm.wsspi.webcontainer.ClosedConnectionException;
import com.ibm.wsspi.webcontainer.IPlatformHelper;
import com.ibm.wsspi.webcontainer.RequestProcessor;
import com.ibm.wsspi.webcontainer.WCCustomProperties;
import com.ibm.wsspi.webcontainer.WebContainerConstants;
import com.ibm.wsspi.webcontainer.WebContainerRequestState;
import com.ibm.wsspi.webcontainer.collaborator.CollaboratorInvocationEnum;
import com.ibm.wsspi.webcontainer.collaborator.ICollaboratorHelper;
import com.ibm.wsspi.webcontainer.collaborator.IConnectionCollaborator;
import com.ibm.wsspi.webcontainer.collaborator.IWebAppNameSpaceCollaborator;
import com.ibm.wsspi.webcontainer.collaborator.IWebAppSecurityCollaborator;
import com.ibm.wsspi.webcontainer.collaborator.IWebAppTransactionCollaborator;
import com.ibm.wsspi.webcontainer.collaborator.TxCollaboratorConfig;
import com.ibm.wsspi.webcontainer.logging.LoggerFactory;
import com.ibm.wsspi.webcontainer.metadata.WebComponentMetaData;
import com.ibm.wsspi.webcontainer.security.SecurityViolationException;
import com.ibm.wsspi.webcontainer.servlet.IExtendedRequest;
import com.ibm.wsspi.webcontainer.servlet.IExtendedResponse;
import com.ibm.wsspi.webcontainer.servlet.IServletConfig;
import com.ibm.wsspi.webcontainer.servlet.IServletContext;
import com.ibm.wsspi.webcontainer.servlet.IServletWrapper;
import com.ibm.wsspi.webcontainer.servlet.ServletReferenceListener;
import com.ibm.wsspi.webcontainer.util.IResponseOutput;
import com.ibm.wsspi.webcontainer.util.ServletUtil;
import com.ibm.wsspi.webcontainer.util.ThreadContextHelper;
import com.ibm.ws.webcontainer.exception.WebContainerUnavailableException; //PK76117

/**
 * @author asisin
 * 
 *         Base class for all targets that eventually get compiled into
 *         servlets. For Servlets, this class will get created by the
 *         WebExtensionProcessor, and for other targets it will really be a
 *         subclass of this wrapper which will get instantiated but the
 *         corresponding extension processor.
 */
@SuppressWarnings("unchecked")
public abstract class ServletWrapper extends GenericServlet implements RequestProcessor, Container, IServletWrapper {
    private static final long serialVersionUID = -4479626085298397598L;
    private boolean notifyInvocationListeners;
    // states that a servlet can be in
    protected final byte UNINITIALIZED_STATE = -1;
    protected final byte AVAILABLE_STATE = 0;
    protected final byte UNAVAILABLE_STATE = 1;
    protected final byte UNAVAILABLE_PERMANENTLY_STATE = 2;

    // PK01801 BEGIN
    private boolean sessionSecurityIntegrationEnabled = false;
    // PK01801 END

    protected ICollaboratorHelper collabHelper;

    // number of threads currently executing the service method
    // this is primarily used for the check before the destroy() is called
    // ALPINE Changed to an AtomicInteger
    private AtomicInteger nServicing = new AtomicInteger(0);

    private long lastAccessTime = 0;

    protected byte state = UNINITIALIZED_STATE;

    protected static Logger logger = LoggerFactory.getInstance().getLogger("com.ibm.ws.webcontainer.servlet");
    private static final String CLASS_NAME = "com.ibm.ws.webcontainer.servlet.ServletWrapper";

    private static TraceNLS nls = TraceNLS.getTraceNLS(ServletWrapper.class, "com.ibm.ws.webcontainer.resources.Messages");

    protected com.ibm.wsspi.webcontainer.servlet.IServletConfig servletConfig;
    protected WebApp context;

    // the actual Servlet instance which will get loaded by the loadServlet()
    // method
    // 
    protected Servlet target;

    private List cacheWrappers = null;

    protected ClassLoader targetLoader;
    protected WebAppEventSource evtSource;

    private ServletEvent event;

    protected String unavailableMessage;
    private long unavailableUntil = -1;

    protected boolean isSTM = false;

    protected boolean internalServlet = false;

    protected IInvocationCollaborator[] webAppInvocationCollaborators;

    protected IPlatformHelper platformHelper;
    private IWebAppNameSpaceCollaborator webAppNameSpaceCollab;
    private IWebAppSecurityCollaborator secCollab;
    private IWebAppTransactionCollaborator txCollab;
    private IConnectionCollaborator connCollab;

    // PK58806
    private static boolean suppressServletExceptionLogging = WCCustomProperties.SUPPRESS_SERVLET_EXCEPTION_LOGGING;
    private static EnumSet<CollaboratorInvocationEnum> throwExceptionEnum = EnumSet.of(CollaboratorInvocationEnum.EXCEPTION);
    // PK76117
    private static boolean discernUnavailableServlet = WCCustomProperties.DISCERN_UNAVAILABLE_SERVLET;
    private static boolean reInitServletonInitUnavailableException = WCCustomProperties.REINIT_SERVLET_ON_INIT_UNAVAILABLE_EXCEPTION; //PM01373

    private static boolean keySizeFromCipherMap = 
    		Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.keysizefromciphermap", "true")).booleanValue();  //not exposed in Infocenter
    // 721610 (PM51389)
    private static boolean invokeFlushAfterService = WCCustomProperties.INVOKE_FLUSH_AFTER_SERVICE; //PM50111	
	
    private boolean servlet23 = false;
    
    // PK83258 Start
    private static Class[] PARAMS_HEAD_TRACE={HttpServletRequest.class,HttpServletResponse.class};
   
    private boolean defaultHeadMethodInUse=false;
    private boolean defaultTraceMethodInUse=false;
    private Boolean checkedForDefaultMethods=null;
    
    private static boolean defaultTraceRequestBehavior = WCCustomProperties.DEFAULT_TRACE_REQUEST_BEHAVIOR;
    
    private static boolean defaultHeadRequestBehavior = WCCustomProperties.DEFAULT_HEAD_REQUEST_BEHAVIOR;  
	// PK83258 End
 
    /*
     * The base lifecycle processing built into the BaseContainer will not apply
     * to this Container.
     */
    public ServletWrapper(IServletContext parent) {
        this.context = (WebApp) parent;
        servlet23 = context.isServlet23();
        this.evtSource = (WebAppEventSource) context.getServletContextEventSource();
        notifyInvocationListeners = evtSource.hasServletInvocationListeners();
        lastAccessTime = System.currentTimeMillis();
        collabHelper = context.getCollaboratorHelper();
        webAppNameSpaceCollab = collabHelper.getWebAppNameSpaceCollaborator();
        secCollab = collabHelper.getSecurityCollaborator();
        txCollab = collabHelper.getWebAppTransactionCollaborator();
        connCollab = collabHelper.getWebAppConnectionCollaborator();
        // PK01801 BEGIN
        sessionSecurityIntegrationEnabled = context.getSessionContext().getIntegrateWASSecurity();
        // PK01801 END
        platformHelper = WebContainer.getWebContainer().getPlatformHelper();
    }

    public void setParent(IServletContext parent) {
        this.context = (WebApp) parent;
    }

    public synchronized void init(ServletConfig conf) throws ServletException {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) // 306998.15
            logger.entering(CLASS_NAME, "init", "ServletWrapper enter init for servletName--> [" + getServletName() + "] , state -->["
                    + state + "]");
        /*
         * Check to see if someone else initizlized this while we were waiting
         * to get in here....
         */
        if (state != UNINITIALIZED_STATE) {
        	if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))  //PM01373
				logger.exiting(CLASS_NAME,"init", "ServletWrapper exit init for servletName--> [" + getServletName() + "] , state -->[" + state +"]");
		
            return;
        }

        WebComponentMetaData cmd = null;
        cmd = ((com.ibm.wsspi.webcontainer.servlet.IServletConfig) conf).getMetaData();

        Object secObject;
        TxCollaboratorConfig txConfig;
        // IConnectionCollaboratorHelper connCollabHelper=
        // collabHelper.createConnectionCollaboratorHelper();
        
        //These preInvokes are nested inside other collaborator preInvokes when you are
        //not an init-on-startup servlet. This may or may not be bad, but this is how we have always done it.
        HandleList _connectionHandleList = new HandleList();
        try {
            webAppNameSpaceCollab.preInvoke(cmd);
            // 246216
            // This should be called after the nameSpaceCollaborator as is
            // done
            // in handleRequest so preInvoke can reference the component
            // meta data
            secObject = secCollab.preInvoke(servletConfig.getServletName());

            collabHelper.doInvocationCollaboratorsPreInvoke(webAppInvocationCollaborators, cmd);

            // end LIDB549.21
            txConfig = txCollab.preInvoke(null, servlet23);

            connCollab.preInvoke(_connectionHandleList, true);
        } catch (SecurityViolationException wse) {
            com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(wse, "com.ibm.ws.webcontainer.servlet.ServletInstance.init", "227", this);
            logger.logp(Level.SEVERE, CLASS_NAME, "init", "Uncaught.init.exception.thrown.by.servlet", new Object[] { getServletName(),
                    getWebApp().getApplicationName(), wse });
            ServletErrorEvent errorEvent = new ServletErrorEvent(this, getServletContext(), getServletName(), servletConfig.getClassName(), wse);
            evtSource.onServletInitError(errorEvent);
            // evtSource.onServletFinishInit(errorEvent);
            evtSource.onServletUnloaded(errorEvent);
            throw new ServletException(nls.getString("preInvoke.Security.Exception", "preInvoke Security Exception"), wse);
        } catch (IOException ioe) {
            com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(ioe, "com.ibm.ws.webcontainer.servlet.ServletInstance.init", "248", this);
            logger.logp(Level.SEVERE, CLASS_NAME, "init", "Uncaught.init.exception.thrown.by.servlet", new Object[] { getServletName(),
                    getWebApp().getApplicationName(), ioe });
            ServletErrorEvent errorEvent = new ServletErrorEvent(this, getServletContext(), getServletName(), servletConfig.getClassName(), ioe);
            evtSource.onServletInitError(errorEvent);
            // evtSource.onServletFinishInit(errorEvent);
            evtSource.onServletUnloaded(errorEvent);
            throw new ServletException(nls.getString("Uncaught.initialization.exception.thrown.by.servlet",
                    "Uncaught initialization exception thrown by servlet"), ioe);
        } catch (Exception e) {
            com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(e, "com.ibm.ws.webcontainer.servlet.ServletInstance.init", "181", this);
            logger.logp(Level.SEVERE, CLASS_NAME, "init", "Uncaught.init.exception.thrown.by.servlet", new Object[] { getServletName(),
                    getWebApp().getApplicationName(), e });
            ServletErrorEvent errorEvent = new ServletErrorEvent(this, getServletContext(), getServletName(), servletConfig.getClassName(), e);
            evtSource.onServletInitError(errorEvent);
            // evtSource.onServletFinishInit(errorEvent);
            evtSource.onServletUnloaded(errorEvent);
            throw new ServletException(nls.getString("Uncaught.initialization.exception.thrown.by.servlet",
                    "Uncaught initialization exception thrown by servlet"), e);
        }

        registerMBean();

        internalServlet = servletConfig.isInternal();

        ClassLoader origClassLoader = null;
        try {
            origClassLoader = ThreadContextHelper.getContextClassLoader();
            final ClassLoader warClassLoader = context.getClassLoader();
            if (warClassLoader != origClassLoader) {
                ThreadContextHelper.setClassLoader(warClassLoader);
            } else {
                origClassLoader = null;
            }

            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
                logger.logp(Level.FINE, CLASS_NAME, "init", "ClassLoader set to: " + warClassLoader.toString()); // PK26183

            evtSource.onServletStartInit(getServletEvent());
            target.init(conf);
            logger.logp(Level.INFO, CLASS_NAME, "init", "[{0}].Initialization.successful", new Object[] { getServletName(),
                    this.context.getContextPath(), this.context.getApplicationName() });
            evtSource.onServletFinishInit(getServletEvent());
            evtSource.onServletAvailableForService(getServletEvent());
            setAvailable();

        } catch (UnavailableException ue) {
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) // 306998.15
                logger.logp(Level.FINE, CLASS_NAME, "init", "unavailableException throw by --> [" + getServletName(), ue);
            com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(
            		ue, 
            		"com.ibm.ws.webcontainer.servlet.ServletInstance.init", 
            		"259", 
            		this);
            //PM01373 Start
			if(!reInitServletonInitUnavailableException){
				if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))  
					logger.logp(Level.FINE, CLASS_NAME,"init", " Custom property reInitServletonInitUnavailableException is not set");
				handleUnavailableException(ue, false); 
			}
			else{
				if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))  
					logger.logp(Level.FINE, CLASS_NAME,"init", " Custom property reInitServletonInitUnavailableException is set");
				handleUnavailableException(ue, true);			
			}
			//PM01373 End
            ServletErrorEvent errorEvent = new ServletErrorEvent(this, getServletContext(), getServletName(), servletConfig.getClassName(), ue);
            evtSource.onServletInitError(errorEvent);
            // Should not call this
            // evtSource.onServletFinishInit(errorEvent);
            //PM01373 Start
			if(reInitServletonInitUnavailableException){
				evtSource.onServletUnloaded(errorEvent); 
				this.deregisterMBean(); // Deregister
			}
			//PM01373 End
            // PK76117 Start
            if (discernUnavailableServlet) {
                if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) // 306998.15
                    logger.logp(Level.FINE, CLASS_NAME, "init", "Create WebContainerUnavailableException");
                throw WebContainerUnavailableException.create(ue);
            }
            // PK76117 End
            throw ue;
        } catch (ServletException e) {
            com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(e, "com.ibm.ws.webcontainer.servlet.ServletInstance.init", "172", this);
            logger.logp(Level.SEVERE, CLASS_NAME, "init", "Uncaught.init.exception.thrown.by.servlet", new Object[] { getServletName(),
                    getWebApp().getApplicationName(), e });
            ServletErrorEvent errorEvent = new ServletErrorEvent(this, getServletContext(), getServletName(), servletConfig.getClassName(), e);
            evtSource.onServletInitError(errorEvent);
            // evtSource.onServletFinishInit(errorEvent);
            evtSource.onServletUnloaded(errorEvent);
            throw e;
        } catch (Throwable e) {
            com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(e, "com.ibm.ws.webcontainer.servlet.ServletInstance.init", "181", this);
            logger.logp(Level.SEVERE, CLASS_NAME, "init", "Uncaught.init.exception.thrown.by.servlet", new Object[] { getServletName(),
                    getWebApp().getApplicationName(), e });
            ServletErrorEvent errorEvent = new ServletErrorEvent(this, getServletContext(), getServletName(), servletConfig.getClassName(), e);
            evtSource.onServletInitError(errorEvent);
            // evtSource.onServletFinishInit(errorEvent);
            evtSource.onServletUnloaded(errorEvent);
            throw new ServletException(nls.getString("Uncaught.initialization.exception.thrown.by.servlet",
                    "Uncaught initialization exception thrown by servlet"), e);
        } finally {

            if (origClassLoader != null) {
                final ClassLoader fOrigClassLoader = origClassLoader;

                ThreadContextHelper.setClassLoader(fOrigClassLoader);
            }
            try {
                connCollab.postInvoke(_connectionHandleList, true);
            } catch (CSIException e) {
                // It is already added to cache..do we need to throw the
                // exception back
                com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(e, "com.ibm.ws.webcontainer.webapp.WebAppServletManager.addServlet",
                        "260", this);
            }
            try {
                txCollab.postInvoke(null, txConfig, servlet23);
            } catch (Exception e) {
                com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(e, "com.ibm.ws.webcontainer.webapp.WebAppServletManager.addServlet",
                        "268", this);
            }

            // begin LIDB549.21
            collabHelper.doInvocationCollaboratorsPostInvoke(webAppInvocationCollaborators, cmd);
            // end LIDB549.21

            try {
                secCollab.postInvoke(secObject);
            } catch (Exception e) {
                com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(e, "com.ibm.ws.webcontainer.webapp.WebAppServletManager.addServlet",
                        "325", this);
            }

            webAppNameSpaceCollab.postInvoke();
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) // 306998.15
                logger.exiting(CLASS_NAME, "init", "ServletWrapper exit init for servletName--> [" + getServletName() + "] , state -->[" + state
                        + "]");
        }

    }

    protected void registerMBean() {
    	//Should never call this in WAS
        if (logger.isLoggable(Level.FINE))
            logger.logp(Level.FINE, CLASS_NAME, "registerMBean", "executing method stub");
    }

    protected void deregisterMBean() {
    	//Should never call this in WAS
        if (logger.isLoggable(Level.FINE))
            logger.logp(Level.FINE, CLASS_NAME, "deregisterMBean", "executing method stub");
    }

    public void handleRequest(ServletRequest req, ServletResponse res) throws Exception {
        IExtendedRequest wasreq = (IExtendedRequest) ServletUtil.unwrapRequest(req);
        WebAppDispatcherContext dispatchContext = (WebAppDispatcherContext) wasreq.getWebAppDispatcherContext();
        handleRequest(req, res, dispatchContext);
    }

    /**
     * @see com.ibm.ws.core.RequestProcessor#handleRequest(Request, Response)
     */
    public void handleRequest(ServletRequest req, ServletResponse res, WebAppDispatcherContext dispatchContext) throws Exception {
        final boolean isTraceOn = com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled();
        // 569469
        // if (isTraceOn&&logger.isLoggable (Level.FINE)) //306998.15
        // logger.logp(Level.FINE, CLASS_NAME,"handleRequest", "entry");
        if (isTraceOn && logger.isLoggable(Level.FINE)) {
        	logger.entering(CLASS_NAME,"handleRequest " + this.toString()+ " ,request-> " +req+ " ,response-> "+res); //PM01373

        }
        // 569469

        IExtendedRequest wasreq = (IExtendedRequest) ServletUtil.unwrapRequest(req);
        
        if (wasreq.isAsyncSupported())
            wasreq.setAsyncSupported(this.servletConfig.isAsyncSupported());
        
        HttpServletRequest httpRequest = (HttpServletRequest) ServletUtil.unwrapRequest(req, HttpServletRequest.class);
        HttpServletResponse httpResponse = (HttpServletResponse) ServletUtil.unwrapResponse(res, HttpServletResponse.class);
        com.ibm.wsspi.webcontainer.metadata.WebComponentMetaData cmd = servletConfig.getMetaData();
        Object secObject = null;
        com.ibm.wsspi.webcontainer.collaborator.TxCollaboratorConfig txConfig = null;

        boolean isInclude = dispatchContext.isInclude();
        boolean isForward = dispatchContext.isForward();
		
	//PM50111 Start
        boolean filtersInvokedForRequest = false;
        WebContainerRequestState reqFilterState = WebContainerRequestState.getInstance(false);
        if(reqFilterState!= null){
        	 filtersInvokedForRequest = reqFilterState.isInvokedFilters();
        	 if(filtersInvokedForRequest) reqFilterState.setInvokedFilters(false); //set the local filter var and reset the requestState var
        }
        //PM50111 End

        // PK54805 End
        if (state == UNAVAILABLE_PERMANENTLY_STATE) {
            UnavailableException ue = new UnavailableException(unavailableMessage);

            // PK56247 Start
            ServletErrorReport errorReport = WebAppErrorReport.constructErrorReport(ue, dispatchContext.getCurrentServletReference());

            if (isInclude || isForward) {

                /*
                 * Throw the UnavailableException if we are an include or
                 * forward so the calling servlet has a chance to handle it.
                 */
                // PK76117 Start
                if (discernUnavailableServlet) {
                    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) // 306998.15
                        logger.logp(Level.FINE, CLASS_NAME, "init", "Create WebContainerUnavailableException");
                    throw WebContainerUnavailableException.create(ue);
                }
                // PK76117 End
                if (isTraceOn && logger.isLoggable(Level.FINE)) {
                    logger.exiting(CLASS_NAME, "handleRequest", "throw exception : " + ue.toString());
                }
                throw ue;
            }
            // PK56247 Start
            // throw constructErrorReport(ue,
            // dispatchContext.getCurrentServletReference());
            if (isTraceOn && logger.isLoggable(Level.FINE)) {
                logger.exiting(CLASS_NAME, "handleRequest", "throw error report : " + errorReport.getExceptionType());
            }
            throw errorReport;
        }

        boolean servletCalled = false; // PK56247
        try {

            // begin 270421 SVT:Portal Automation ServletInvocationListener
            // testcase fails WAS.webcontainer
            notifyInvocationListeners = evtSource.hasServletInvocationListeners();
            // end 270421 SVT:Portal Automation ServletInvocationListener
            // testcase fails WAS.webcontainer

            // Changes required so we can leave the last servlet reference on
            // the stack
            String nested = (String) req.getAttribute(WebAppRequestDispatcher.DISPATCH_NESTED_ATTR);
            if (nested != null)
                dispatchContext.pushServletReference(this);
            else
                dispatchContext.clearAndPushServletReference(this);

            // begin 270421 SVT:Portal Automation ServletInvocationListener
            // testcase fails WAS.webcontainer
            if (isTraceOn && logger.isLoggable(Level.FINE)) { // 306998.15
                String includeReqURI = (String) httpRequest.getAttribute(WebAppRequestDispatcher.REQUEST_URI_INCLUDE_ATTR);
                String requestURI = httpRequest.getRequestURI();
                logger.logp(Level.FINE, CLASS_NAME, "handleRequest", "handling request for resource ["
                        + (includeReqURI == null ? requestURI : includeReqURI) + "]");
            }
            // end 270421 SVT:Portal Automation ServletInvocationListener
            // testcase fails WAS.webcontainer

            if (httpRequest.isSecure()) {

            	String cipherSuite = null;

            	// we have an SSL connection...set the attributes
            	ServletRequest implRequest = ServletUtil.unwrapRequest(httpRequest);                
            	cipherSuite = ((SRTServletRequest) implRequest).getCipherSuite();

            	if (cipherSuite != null) {
            		httpRequest.setAttribute("javax.servlet.request.cipher_suite", cipherSuite);

            		//Start 721610 (PM51389)	                
            		Integer keySize = null;	                                	

            		if (keySizeFromCipherMap) {
            			// check for the values in Map which
            			// has cipher to bit size map						

            			keySize = WebContainer.getWebContainer().getKeySizefromCipherMap(cipherSuite);

            			if (keySize == null) {
            				if (cipherSuite.contains("_AES_256_")) {
            					keySize = 256;
            				} else if (cipherSuite.contains("_3DES_")) {
            					keySize = 168;
            				} else if (cipherSuite.contains("_AES_128_")
            						|| cipherSuite.contains("_RC4_128_")) {
            					keySize = 128;
            				} else if (cipherSuite.contains("_DES_")) {
            					keySize = 56;
            				} else if (cipherSuite.contains("_RC4_40_")
            						|| cipherSuite.contains("_DES40_")) {
            					keySize = 40;
            				} else if (cipherSuite.contains("_NULL_")) {
            					keySize = 0;
            				}
            			}

            		} else {
            			// Now check the sslbitsize.properties file which can be modified by customer.
            			// see if there's a bit size
            			if (isTraceOn && logger.isLoggable(Level.FINE))
            				logger.logp(Level.FINE, CLASS_NAME,"handleRequest"," check for the size in properties file");
            			keySize = WebContainer.getWebContainer().getKeySize(cipherSuite);
            		}


            		if (keySize != null){
            			httpRequest.setAttribute("javax.servlet.request.key_size", keySize);                    
            		}
            		//End 721610 (PM51389)
            	}
            }

            WebAppServletInvocationEvent invocationEvent = null;

            if (notifyInvocationListeners)
                invocationEvent = new WebAppServletInvocationEvent(this, getServletContext(), getServletName(), servletConfig.getClassName(),
                        httpRequest, httpResponse);

            target = loadServlet();
            if (target==null&&!servletConfig.isClassDefined())
            	throw new FileNotFoundException();
            if (state == UNINITIALIZED_STATE)
    		{
    			// PM01373 Start	
    			if ( unavailableUntil == -1 ){ 
    				
    				if (isTraceOn&&logger.isLoggable (Level.FINE))
    					{logger.logp(Level.FINE, CLASS_NAME,"handleRequest", " state --> " + state+ " ,  init");}

                init(servletConfig);
    			}		 
    			else {
    				if (isTraceOn&&logger.isLoggable (Level.FINE))
    					{logger.logp(Level.FINE, CLASS_NAME,"handleRequest", " state --> " + state + "&& unavailableUntil --> "+ unavailableUntil);}
    				
    				lastAccessTime = System.currentTimeMillis();
    				long timeDiff = unavailableUntil - lastAccessTime;
    				if (timeDiff <= 0)
    				{		
    					if (isTraceOn&&logger.isLoggable (Level.FINE))
    						{logger.logp(Level.FINE, CLASS_NAME,"handleRequest", " unavailable time expired , init ");}

    					init(servletConfig);
    				}
    				else
    				{
    					int timeLeft = (int) (timeDiff) / 1000;
    					if (timeLeft == 0)
    					{
    						//caused by truncation of long to int
    						timeLeft = 1;
    					}
    					if (isTraceOn&&logger.isLoggable (Level.FINE))					
    						{logger.logp(Level.FINE, CLASS_NAME,"handleRequest", " remaining unavailable time --> " + timeLeft);}
    					//UnavailableException from init()is already handled then thrown, need to handle this one and throw.

    					UnavailableException ue = new UnavailableException(unavailableMessage, timeLeft);
    					if (isTraceOn&&logger.isLoggable (Level.FINE))
    						{logger.logp(Level.FINE, CLASS_NAME,"handleRequest", " handle UnavailableException for TempUE ");}

    					this.handleUnavailableException(ue, true);					
    					throw ue;						
    				}
    			}// PM01373 End
    		}

            if (isTraceOn && logger.isLoggable(Level.FINE)) {
                logger.logp(Level.FINE, CLASS_NAME, "handleRequest", "internal servlet --> " + servletConfig.isInternal());
            }

            if (context.isMimeFilteringEnabled()) {
                ChainedResponse chainedResp = new ChainedResponse(httpRequest, httpResponse);

                // set a default content type
                chainedResp.setContentType("text/html");

                // defect 55215 - automatically transfer client headers through
                // the chain
                Enumeration names = httpRequest.getHeaderNames();
                while (names.hasMoreElements()) {

                    String name = (String) names.nextElement();
                    String value = httpRequest.getHeader(name);
                    if (!name.toLowerCase().startsWith("content")) {

                        // don't transfer content headers
                        chainedResp.setAutoTransferringHeader(name, value);
                    }
                }

                this.service(httpRequest, chainedResp, invocationEvent);

                // BEGIN PQ47136
                // part 1: check to see if chainedResponse
                // contains a redirectURI; if so get the value.
                String _redirectURI = null;
                if (chainedResp.isRedirected()) {
                    _redirectURI = chainedResp.getRedirectURI();
                }
                // END PQ47136

                String mimeType = chainedResp.getHeader("content-type");

                IServletWrapper wrapper = getMimeFilterWrapper(mimeType);
                while (wrapper != null) {
                    httpRequest = chainedResp.getChainedRequest();

                    // begin pq50381: part 1 --> keep a copy of previous
                    // chainedResponse
                    // Purpose: Pass pertinent response information along chain.
                    ChainedResponse prevChainedResp = chainedResp;
                    // begin pq40381: part 1

                    chainedResp = new ChainedResponse(httpRequest, httpResponse);

                    // begin pq50381: part 2
                    // Purpose: Transcode Publishing checks StoredResponse for
                    // mime filtered servlet's contentType to decide how to
                    // filter
                    // ultimate response. Transfer client set headers/ cookies
                    // thru chain.
                    chainedResp.setContentType(mimeType);
                    transferHeadersFromPrevChainedResp(chainedResp, prevChainedResp);
                    // end pq50381: part 2

                    // BEGIN PQ47136
                    // part 2: if redirectURI was specified
                    // set the location header to the redirectURI.
                    // and set appropriate status code.
                    if (_redirectURI != null) {
                        // System.out.println("transferring header location = ["+
                        // _redirectURI +"]");
                        // chainedResp.setAutoTransferringHeader ("location",
                        // _redirectURI); // could not get this to work.
                        chainedResp.setHeader("location", _redirectURI); // works
                                                                         // but
                                                                         // should
                                                                         // be
                                                                         // replaced
                                                                         // with
                                                                         // above.
                        chainedResp.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
                    }
                    // END PQ47136

                    dispatchContext.pushServletReference(wrapper);

                    ((ServletWrapper) wrapper).service(httpRequest, chainedResp, invocationEvent); // optimized
                                                                                                   // dispatch

                    dispatchContext.popServletReference();

                    String newMimeType = chainedResp.getHeader("content-type");
                    String nMime = newMimeType.toLowerCase();
                    String oMime = mimeType.toLowerCase();
                    int icharset = nMime.indexOf(";");
                    if (icharset != -1)
                        nMime = nMime.substring(0, icharset);
                    icharset = oMime.indexOf(";");
                    if (icharset != -1)
                        oMime = oMime.substring(0, icharset);
                    if (nMime.equals(oMime)) {
                        // recursive filter break condition
                        wrapper = null;
                    } else {
                        mimeType = newMimeType;
                        wrapper = getMimeFilterWrapper(mimeType);
                    }
                }

                chainedResp.transferResponse(httpResponse);
            } else {

                servletCalled = true; // PK56247
                service(req, res, invocationEvent);

            //PM50111 Start            	              
                if (isTraceOn && logger.isLoggable(Level.FINE)){
                    logger.logp(Level.FINE, CLASS_NAME, "handleRequest", "isInclude->" + isInclude +" , isForward ->" + isForward +
                    		" ,filtersInvokedforRequest-->"+ filtersInvokedForRequest +" ,flushAfterService -->"+ invokeFlushAfterService );
                }
                     
                // Start PM50111.
                // flush any buffered data that has been written to the response as per spec 5.5 
                // for v7 compatibility we should not call flushbuffer here, since filters are not done yet. Customer need to set custom property to false.
                // flush if custom property invokeFlushAfterService is set (default) 
                // Don't flush the buffer if this servlet was included (see spec) or async is set.
                // Dont' flush if the response is committed. 
				// instanceof IExtendedResponse to make sure this is response object is ours and not wrappedresponse
                
                if (!isInclude && !httpResponse.isCommitted() && invokeFlushAfterService) {                 	        
                	//check for async now
	                WebContainerRequestState reqState = WebContainerRequestState.getInstance(false);
	                boolean isStartAsync=false;
	                boolean isComplete = false;
	                if (reqState!=null){
	                    if (reqState.isAsyncMode())
	                        isStartAsync = true;
	                    if (reqState.isCompleted())
	                        isComplete=true;
	                }
	                    
	                if (isTraceOn && logger.isLoggable(Level.FINE)){
	                    logger.logp(Level.FINE, CLASS_NAME, "handleRequest", "isComplete->" + isComplete + ", isStartAsync->"+isStartAsync);
	                }	
	                if (!isStartAsync && !isComplete ) {	                		                        
	                	if (!(httpResponse instanceof IExtendedResponse)) {
//                            		((IExtendedResponse) httpResponse).flushBuffer(false);	 
//	                	else
	                		httpResponse.flushBuffer();
	                		}
	                }                  
                }
              //PM50111 End
            }
        } catch (WriteBeyondContentLengthException clex) {
            com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(clex, "com.ibm.ws.webcontainer.servlet.ServletWrapper.handleRequest()",
                    "293");
            dispatchContext.pushException(clex);
            try {
                httpResponse.flushBuffer();
            } catch (IOException i) {
                ServletErrorReport errorReport = WebAppErrorReport.constructErrorReport(i, dispatchContext.getCurrentServletReference());
                com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(i, "com.ibm.ws.webcontainer.servlet.ServletWrapper.handleRequest()",
                        "298");
                throw errorReport;
            }
        } catch (IOException ioe) {
            if (isRethrowOriginalException(req, isInclude, isForward)) {
                dispatchContext.pushException(ioe);
                throw ioe;
            }
            ServletErrorReport errorReport = WebAppErrorReport.constructErrorReport(ioe, dispatchContext.getCurrentServletReference());
            dispatchContext.pushException(ioe);
            com.ibm.wsspi.webcontainer.util.FFDCWrapper
                    .processException(ioe, "com.ibm.ws.webcontainer.servlet.ServletWrapper.handleRequest()", "298");
            throw errorReport;
        } catch (UnavailableException ue) {
            ServletErrorReport errorReport = null;
            // PK76117 Start
            boolean caughtUEIsInstanceOfWUE = false;
            if (discernUnavailableServlet) {
                caughtUEIsInstanceOfWUE = (ue instanceof WebContainerUnavailableException);
            }
            if (isTraceOn && logger.isLoggable(Level.FINE))
                logger.logp(Level.FINE, CLASS_NAME, "handleRequest", "UnavailableException Caught : caughtUEIsInstanceOfWUE="
                        + caughtUEIsInstanceOfWUE + 
                        ", discernUnavailableServlet="+discernUnavailableServlet+ ", state = " + state ); //PM01373
            // PK76117 End
            try {
                errorReport = WebAppErrorReport.constructErrorReport(ue, dispatchContext.getCurrentServletReference());
                //PM01373 need to check state since UE already handled for init()
				if (!caughtUEIsInstanceOfWUE && state != UNINITIALIZED_STATE) {    // PK76117	
					handleUnavailableException(ue,false); //PM01373
                }

                if (isInclude || isForward) {
                    throw ue;
                }

                if (isRethrowOriginalException(req, isInclude, isForward)) {
                    throw ue;
                }
            } catch (UnavailableException une) {
                if (isRethrowOriginalException(req, isInclude, isForward)) {
                    // PK76117 Start
                    if (discernUnavailableServlet) {
                        if ((une instanceof WebContainerUnavailableException))
                            throw une;
                        else {
                            if (isTraceOn && logger.isLoggable(Level.FINE))
                                logger.logp(Level.FINE, CLASS_NAME, "handleRequest", "Create WebContainerUnavailableException");
                            throw WebContainerUnavailableException.create(ue);
                        }
                    }
                    // PK76117 End
                    throw une;
                }
                errorReport = WebAppErrorReport.constructErrorReport(une, dispatchContext.getCurrentServletReference());
            } finally {
                if (!caughtUEIsInstanceOfWUE || !servletCalled) { // PK76117
                    // 198256 - begin
                	if (!ue.isPermanent()&& (state == this.UNAVAILABLE_STATE || state == this.UNINITIALIZED_STATE)) //PM01373
                        httpResponse.setHeader("Retry-After", String.valueOf(ue.getUnavailableSeconds()));
                    // 198256 - end
                }
                dispatchContext.pushException(ue);
                com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(ue, "com.ibm.ws.webcontainer.servlet.ServletWrapper.handleRequest()",
                        "302");
            }
            throw errorReport;
        }

        finally {
            dispatchContext.popServletReference();
            // PK50133 - start
            if (context.getJSPClassLoaderLimit() > 0
                    && (context.getJSPClassLoaderExclusionList() == null || !context.getJSPClassLoaderExclusionList().contains(
                            httpRequest.getRequestURI()))) {
                // PK82657 - add check for whether to track forwards and
                // includes
                if (context.isJSPClassLoaderLimitTrackIF() || (!isInclude && !isForward)) {
                    ClassLoader cl = getTargetClassLoader();
                    Class jspClassLoaderClassName = context.getJSPClassLoaderClassName();
                    if (cl != null && cl.getClass().isAssignableFrom(jspClassLoaderClassName)) {
                        context.addAndCheckJSPClassLoaderLimit(this);
                    }
                }
            }
            // PK50133 - end
            

            if (isTraceOn && logger.isLoggable(Level.FINE)) {
                logger.exiting(CLASS_NAME, "handleRequest");
            }
            // 569469
        }
    }

    private boolean isRethrowOriginalException(ServletRequest req, boolean isInclude, boolean isForward) {
        return (isInclude || isForward) && req.getAttribute(WebContainerConstants.IGNORE_DISPATCH_STATE) == null;
    }

    protected Object getTransaction() throws Exception {
        return null;
    }

    protected void checkTransaction(Object transaction) {

    }

    protected void checkForRollback() {

    }

    /**
     * @see com.ibm.ws.core.CommandSequence#addCommand(Command)
     */
    public void addCommand(Command command) {
    }

    public String getName() {
        return servletConfig.getServletName();
    }

    /**
     * @see com.ibm.ws.core.CommandSequence#removeCommand(Command)
     */
    public void removeCommand(Command command) {
    }

    protected void doDestroy() {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) // 306998.15
            logger.entering(CLASS_NAME, "doDestroy " + this.toString()); // 569469
        // logger.logp(Level.FINE, CLASS_NAME,"doDestroy", "entry");
        if (state != this.UNINITIALIZED_STATE) {
            WebComponentMetaData cmd = null;
            cmd = ((com.ibm.wsspi.webcontainer.servlet.IServletConfig) servletConfig).getMetaData();

            Object secObject = null;
            TxCollaboratorConfig txConfig = null;
            // IConnectionCollaboratorHelper connCollabHelper=
            // collabHelper.createConnectionCollaboratorHelper();
            HandleList _connectionHandleList = new HandleList();
            try {
                webAppNameSpaceCollab.preInvoke(cmd);
                // 246216
                // This should be called after the nameSpaceCollaborator as is
                // done
                // in handleRequest so preInvoke can reference the component
                // meta data
                secObject = secCollab.preInvoke(servletConfig.getServletName());

                collabHelper.doInvocationCollaboratorsPreInvoke(webAppInvocationCollaborators, cmd);

                // end LIDB549.21
                txConfig = txCollab.preInvoke(null, servlet23);

                connCollab.preInvoke(_connectionHandleList, true);
                deregisterMBean();

                ClassLoader origClassLoader = null;
                try {
                    origClassLoader = ThreadContextHelper.getContextClassLoader();
                    final ClassLoader warClassLoader = context.getClassLoader();
                    if (warClassLoader != origClassLoader) {
                        ThreadContextHelper.setClassLoader(warClassLoader);
                    } else {
                        origClassLoader = null;
                    }
                    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) // 306998.15
                        logger.logp(Level.FINE, CLASS_NAME, "doDestroy", "Servlet.unload.initiated:.{0}", getServletName());

                    setUnavailable();
                    evtSource.onServletStartDestroy(getServletEvent());

                    for (int i = 0; (nServicing.get() > 0) && i < WCCustomProperties.SERVLET_DESTROY_WAIT_TIME; i++) {
                        try {
                            if (i == 0) {
                                if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) // 306998.15
                                    logger.logp(Level.FINE, CLASS_NAME, "doDestroy",
                                            "servlet is still servicing...will wait up to 60 seconds for servlet to become idle: {0}",
                                            getServletName());
                                if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) // 306998.15
                                    logger.logp(Level.FINE, CLASS_NAME, "doDestroy", "Waiting.servlet.to.finish.servicing.requests:.{0}",
                                            getServletName());
                            }
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(e,
                                    "com.ibm.ws.webcontainer.servlet.ServletInstance.destroy", "377", this);
                        }
                    }

                    if (nServicing.get() > 0) {
                        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) // 306998.15
                            logger.logp(Level.FINE, CLASS_NAME, "doDestroy",
                                    "Servlet.wait.for.destroy.timeout.has.expired,.destroy.will.be.forced:.{0}", getServletName());
                    }
                    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) { // 306998.15
                        logger.logp(Level.FINE, CLASS_NAME, "doDestroy", "enter Servlet.destroy(): {0}", getServletName());
                    }
                    if (target != null) {
                        target.destroy();
                        logger.logp(Level.INFO, CLASS_NAME, "doDestroy", "[{0}].Destroy.successful", new Object[] { getServletName(),
                                this.context.getContextPath(), this.context.getApplicationName() });
                    }

                    evtSource.onServletFinishDestroy(getServletEvent());
                    evtSource.onServletUnloaded(getServletEvent());

                    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) // 306998.15
                        logger.logp(Level.FINE, CLASS_NAME, "doDestroy", "Servlet.unloaded:.{0}", getServletName());
                } catch (Exception e) {
                    throw new ServletException(e);
                } finally {

                    if (origClassLoader != null) {
                        final ClassLoader fOrigClassLoader = origClassLoader;

                        ThreadContextHelper.setClassLoader(fOrigClassLoader);
                    }

                }
            } catch (Throwable e) {
                com.ibm.wsspi.webcontainer.util.FFDCWrapper
                        .processException(e, "com.ibm.ws.webcontainer.servlet.ServletWrapper.destroy", "403", this);
                logger.logp(Level.SEVERE, CLASS_NAME, "doDestroy", "Uncaught.destroy().exception.thrown.by.servlet", new Object[] {
                        getServletName(), getWebApp().getApplicationName(), e });
                evtSource.onServletDestroyError(new ServletErrorEvent(this, getServletContext(), getServletName(), servletConfig.getClassName(), e));
                context.log("Error occurred while destroying servlet", e);
            } finally {
                try {
                    connCollab.postInvoke(_connectionHandleList, true);
                } catch (CSIException e) {
                    // It is already added to cache..do we need to throw the
                    // exception back
                    com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(e, "com.ibm.ws.webcontainer.servlet.ServletWrapper.doDestroy",
                            "260", this);
                }
                try {
                    txCollab.postInvoke(null, txConfig, servlet23);
                } catch (Exception e) {
                    com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(e, "com.ibm.ws.webcontainer.servlet.ServletWrapper.doDestroy",
                            "268", this);
                }

                // begin LIDB549.21
                collabHelper.doInvocationCollaboratorsPostInvoke(webAppInvocationCollaborators, cmd);
                // end LIDB549.21

                try {
                    secCollab.postInvoke(secObject);
                } catch (Exception e) {
                    com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(e, "com.ibm.ws.webcontainer.servlet.ServletWrapper.doDestroy",
                            "325", this);
                }

                webAppNameSpaceCollab.postInvoke();
            }
        }
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) // 306998.15
            logger.logp(Level.FINE, CLASS_NAME, "doDestroy", "exit Servlet.destroy(): {0}", getServletName());
        // 569469
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
            logger.exiting(CLASS_NAME, "doDestroy");
        }
        // 569469

    }

    /**
     * This method is called by the subclass implementation (eg.,
     * JSPExtensionServletWrapper) when it realizes that a reload is required.
     * The difference between this method and a destroy() is that the
     * servletConfig object is not destroyed because it only contains config
     * information which remains unchanged.
     * 
     */
    public void prepareForReload() {
        doDestroy();
        target = null;
        targetLoader = null;
        state = this.UNINITIALIZED_STATE;
    }

    /**
     * @see javax.servlet.Servlet#destroy() will be called by the container to
     *      destroy the target and propagate the appropriate events. This will
     *      also nullify the servletConfig object, hence it should be called by
     *      the subclasses only if they no longer need the servletConfig.
     */
    public void destroy() {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) { // 306998.15
            logger.logp(Level.FINE, CLASS_NAME, "destroy", "servlet destroy for -->" + getServletName() + ", state is -->" + state);
        }
        try {
            if (state != UNINITIALIZED_STATE && state != UNAVAILABLE_PERMANENTLY_STATE) {
                try {
                    doDestroy();
                } catch (Throwable th) {
                    com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(th, "com.ibm.ws.webcontainer.servlet.ServletWrapper", "1152", this);
                    logger.logp(Level.SEVERE, CLASS_NAME, "destroy", "Exception.occured.during.servlet.destroy", th);
                } finally {
                    target = null;
                    servletConfig = null;
                    targetLoader = null;
                    state = this.UNAVAILABLE_PERMANENTLY_STATE;
                }
            }
        } finally {
            invalidateCacheWrappers();
        }
    }

    /**
     * @see javax.servlet.Servlet#getServletConfig()
     */
    public IServletConfig getServletConfig() {
        return servletConfig;
    }

    /**
     * @see javax.servlet.Servlet#getServletInfo()
     */
    public String getServletInfo() {
        return getName() + ":" + servletConfig.getClassName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.Servlet#service(javax.servlet.ServletRequest,
     * javax.servlet.ServletResponse)
     */
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        WebAppServletInvocationEvent evt = null;
        if (notifyInvocationListeners) {
            evt = new WebAppServletInvocationEvent(this, getServletContext(), getServletName(), servletConfig.getClassName(), req, res); // 263020
        }

        service(req, res, evt);
    }

    /**
     * @see javax.servlet.Servlet#service(ServletRequest, ServletResponse)
     */
    public void service(ServletRequest req, ServletResponse res, WebAppServletInvocationEvent evt) throws ServletException, IOException {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
            logger.entering(CLASS_NAME, "service " + this.toString()+ " ,req-->"+ req + " ,res-->"+ res); //PM50111 // 569469
        
        boolean notify = notifyInvocationListeners && (evt != null);
        if (unavailableUntil != -1) {
            lastAccessTime = System.currentTimeMillis();
            long timeDiff = unavailableUntil - lastAccessTime;
            if (timeDiff <= 0) {
                setAvailable();
            } else {
                int timeLeft = (int) (timeDiff) / 1000;
                if (timeLeft == 0) {
                    // caused by truncation of long to int
                    timeLeft = 1;
                }
                throw new UnavailableException(unavailableMessage, timeLeft);
            }
        }

        try {
            // PK02277: Load target if it is null
            if (target == null)
                load();
            try {
                if (notify)
                    evtSource.onServletStartService(evt);
            } catch (Throwable th) {
                // ALPINE
                nServicing.getAndIncrement();
                throw th;
            }
            // ALPINE
            nServicing.getAndIncrement();

            if (notify) {
                long curLastAccessTime = System.currentTimeMillis(); // 266936,
                                                                     // bad
                                                                     // webAppModule
                                                                     // response
                                                                     // time
                                                                     // from PMI
                                                                     // (use
                                                                     // local
                                                                     // method
                                                                     // variable)
                lastAccessTime = curLastAccessTime;
                target.service(req, res);
                long endTime = System.currentTimeMillis();
                evt.setResponseTime(endTime - curLastAccessTime);
                evtSource.onServletFinishService(evt);
            } else
                target.service(req, res);
        } catch (UnavailableException e) {
            throw e;
        } catch (IOException ioe) {

            // begin PK04668 IF THE CLIENT THAT MADE THE SERVLET REQUEST GOES
            // DOWN,THERE IS WAS.webcontainer
            if (ioe instanceof ClosedConnectionException) { // do not log as
                                                            // errors since this
                                                            // will fill logs
                                                            // too quickly.
                if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) { // 306998.15
                    logger.logp(Level.FINE, CLASS_NAME, "service", "Caught ClosedConnectionException for servlet [" + getServletName() + "]");
                }
            } else {
                logger.logp(Level.SEVERE, CLASS_NAME, "service", "Uncaught.service().exception.root.cause", new Object[] {
                        getServletName(), ioe });

            }
            // end PK04668 IF THE CLIENT THAT MADE THE SERVLET REQUEST GOES
            // DOWN,THERE IS WAS.webcontainer

            // defect 156072.1
            if (notify) {
                ServletErrorEvent errorEvent = new ServletErrorEvent(this, getServletContext(), getServletName(), servletConfig.getClassName(), ioe);
                evtSource.onServletServiceError(errorEvent);

                evtSource.onServletFinishService(evt);
            }

            throw ioe;
        } catch (ServletException e) {
            if (!suppressServletExceptionLogging) {
                logger.logp(Level.SEVERE, CLASS_NAME, "service", "Uncaught.service().exception.root.cause", new Object[] {
                        getServletName(), e });
            }

            // defect 156072.1
            if (notify) {
                ServletErrorEvent errorEvent = new ServletErrorEvent(this, getServletContext(), getServletName(), servletConfig.getClassName(), e);
                evtSource.onServletServiceError(errorEvent);

                evtSource.onServletFinishService(evt);
            }

            throw e;
        } catch (UnsatisfiedLinkError e) {
            logger.logp(Level.SEVERE, CLASS_NAME, "service", "Place.servlet.class.on.classpath.of.the.application.server",
                    new Object[] { getServletName(), getWebApp().getApplicationName(), e });

            // defect 156072.1
            if (notify) {
                ServletErrorEvent errorEvent = new ServletErrorEvent(this, getServletContext(), getServletName(), servletConfig.getClassName(), e);
                evtSource.onServletServiceError(errorEvent);

                evtSource.onServletFinishService(evt);
            }

            throw new ServletException(e);
        } catch (RuntimeException e) {
            logger.logp(Level.SEVERE, CLASS_NAME, "service", "Uncaught.service().exception.thrown.by.servlet", new Object[] {
                    getServletName(), getWebApp().getApplicationName(), e });

            if (notify) {
                ServletErrorEvent errorEvent = new ServletErrorEvent(this, getServletContext(), getServletName(), servletConfig.getClassName(), e);
                evtSource.onServletServiceError(errorEvent);

                evtSource.onServletFinishService(evt);
            }

            throw e;
        } catch (Throwable e) {
            logger.logp(Level.SEVERE, CLASS_NAME, "service", "Uncaught service() exception thrown by servlet {0}: {2}", new Object[] {
                    getServletName(), getWebApp().getApplicationName(), e });
            com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(e, "com.ibm.ws.webcontainer.servlet.ServletInstance.service", "290", this);

            if (notify) {
                evtSource.onServletFinishService(evt);
                ServletErrorEvent errorEvent = new ServletErrorEvent(this, getServletContext(), getServletName(), servletConfig.getClassName(), e);
                evtSource.onServletServiceError(errorEvent);
            }

            throw new ServletErrorReport(e);
        } finally {
            // ALPINE
            nServicing.getAndDecrement();
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) // 306998.15
                logger.exiting(CLASS_NAME, "service"+ " ,req-->"+ req + " ,res-->"+ res); //PM50111 // 569469
        }
    }

    /**
     * Method initialize() is called by the creator of the ServletWrapper
     * object. The creator constructs the object and immediately initializes it
     * with the ServletConfig object. This method is meant to be called only
     * from the *outside* and specifically by an ExtensionProcessor instance,
     * and never by this class, or its subclasses.
     * 
     * @param config
     */
    public void initialize(IServletConfig config) throws Exception {
        this.servletConfig = config;
        webAppInvocationCollaborators = context.getWebAppInvocationCollaborators();
    }

    public void loadOnStartupCheck() throws Exception {
        if (servletConfig.isLoadOnStartup()) {
            // System.out.println("Loading at startup
            // "+servletConfig.getServletName());
            try {
                loadServlet();
            } catch (UnavailableException ue) {
                handleUnavailableException(ue, false); //PM01373
                // PK76117 Start
                if (discernUnavailableServlet) {
                    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
                        logger.logp(Level.FINE, CLASS_NAME, "handleRequest", "Create WebContainerUnavailableException");
                    throw WebContainerUnavailableException.create(ue);
                }
                // PK76117 End
                throw ue;
            }
            init(servletConfig);
        }
    }

    public void load() throws Exception {
    	 if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
             logger.entering(CLASS_NAME, "load"); 
        loadServlet();
        if (target!=null) {
        init(servletConfig);
        } else if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)){
        	logger.logp(Level.FINE,CLASS_NAME, "load","unable to load servlet's target so skipping init"); 
        }
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
            logger.exiting(CLASS_NAME, "load"); 
    }

    /*
     * Method unload
     */
    public void unload() throws Exception {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) // 306998.15
            logger.entering(CLASS_NAME, "unload className-->[" + servletConfig.getClassName() + "], servletName[" + servletConfig.getServletName()
                    + "]"); // 569469
        // logger.logp(Level.FINE, CLASS_NAME,"unload",
        // "entry className-->["+servletConfig.getClassName()+"], servletName["+servletConfig.getServletName()+"]");
        // // PK26183

        if (state == UNINITIALIZED_STATE) {
            return;
        }

        try {
            doDestroy();
        } catch (Throwable th) {
            com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(th, "com.ibm.ws.webcontainer.servlet.ServletWrapper", "1511", this);
            logger.logp(Level.SEVERE, CLASS_NAME, "unload", "Exception.occured.during.servlet.unload", th);
        } finally {
            target = null;
            targetLoader = null;
            invalidateCacheWrappers();
            state = UNINITIALIZED_STATE;
        }

        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) // 306998.15
            logger.exiting(CLASS_NAME, "unload"); // 569469

    }

    /**
     * Method loadServlet.
     */
    // begin 280649 SERVICE: clean up separation of core and shell
    // WASCC.web.webcontainer : reuse with other ServletWrapper impls.
    // private synchronized Servlet loadServlet() throws Exception {
    protected synchronized Servlet loadServlet() throws Exception {
        // end 280649 SERVICE: clean up separation of core and shell
        // WASCC.web.webcontainer : reuse with other ServletWrapper impls.
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) // 306998.15
            logger.entering(CLASS_NAME, "loadServlet, className-->[" + servletConfig.getClassName() + "], servletName["
                    + servletConfig.getServletName() + "]"); // 569469
        // logger.logp(Level.FINE, CLASS_NAME,"loadServlet",
        // "entry className-->["+servletConfig.getClassName()+"], servletName["+servletConfig.getServletName()+"]");
        if (target != null) {
			if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))  
					logger.exiting(CLASS_NAME,"loadServlet, Found target for className-->["+servletConfig.getClassName()+"], servletName["+servletConfig.getServletName()+"]"); //PM01373
            return target;
        }

        Object cred = null;

        Servlet servlet = servletConfig.getServlet();
        if (servlet != null) {
        	target = servlet;
            return target;
        }

        // load the servlet
        final Class<? extends Servlet> servletClass = servletConfig.getServletClass();
        final String className = servletConfig.getClassName();
        final String servletName = servletConfig.getServletName();

        if (className == null) {
            logger.logp(Level.WARNING, CLASS_NAME, "run", "servlet.classname.is.null", new Object []{servletName});
            return null;
        }
        
        ClassLoader origClassLoader = null;

        try {
            if (platformHelper.isSyncToThreadPlatform() && ((WebApp) context).getConfiguration().isSyncToThreadEnabled())
                cred = platformHelper.securityIdentityPush();

            origClassLoader = ThreadContextHelper.getContextClassLoader();

            final ClassLoader loader;
            if (targetLoader == null) {
                loader = context.getClassLoader();
                setTargetClassLoader(loader);
            } else {
                loader = targetLoader;
            }

            if (loader != origClassLoader) {
                ThreadContextHelper.setClassLoader(loader);
            } else {
                origClassLoader = null;
            }

            AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws ServletException {
                    try {
                        Servlet s = null;
                       
                            if (className.equals("com.ibm.ws.webcontainer.servlet.SimpleFileServlet"))
                                s = context.getSimpleFileServlet();
                            else {
                                if (servletClass != null)
                                    s = servletClass.newInstance();
                                else
                                    s = (javax.servlet.Servlet) Beans.instantiate(loader, className);
                            }
                        
                        createTarget(s);
                    } catch (ClassNotFoundException e) {
                        com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(e,
                                "com.ibm.ws.webcontainer.webapp.WebAppServletManager.loadServlet", "208", this);
                        // log the error

                        logger.logp(Level.SEVERE, CLASS_NAME, "run", "classnotfoundexception.loading.servlet.class", e);
                        throw new UnavailableException(MessageFormat.format(nls.getString("Servlet.Could.not.find.required.servlet.class",
                                "Servlet [{0}]: Could not find required servlet - {1}"), new Object[] { className, e.getMessage() }));
                    } catch (ClassCastException e) {
                        com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(e,
                                "com.ibm.ws.webcontainer.webapp.WebAppServletManager.loadServlet", "213", this);
                        throw new UnavailableException(MessageFormat.format(nls.getString("Servlet.not.a.servlet.class",
                                "Servlet [{0}]: not a servlet class"), new Object[] { className }));
                    } catch (NoClassDefFoundError e) {
                        com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(e,
                                "com.ibm.ws.webcontainer.webapp.WebAppServletManager.loadServlet", "218", this);
                        throw new UnavailableException(
                                MessageFormat.format(nls.getString("Servlet.was.found.but.is.missing.another.required.class",
                                        "Servlet [{0}]: {1} was found, but is missing another required class.\n"), new Object[] { servletName,
                                        className })
                                        + nls
                                                .getString(
                                                        "This.error.implies.servlet.was.originally.compiled.with.classes.which.cannot.be.located.by.server",
                                                        "This error typically implies that the servlet was originally compiled with classes which cannot be located by the server.\n")
                                        + nls.getString("Check.your.classpath.ensure.all.classes.present",
                                                "Check your classpath to ensure that all classes required by the servlet are present.\n")
                                        + nls
                                                .getString(
                                                        "be.debugged.by.recompiling.the.servlet.using.only.the.classes.in.the.application's.runtime.classpath",
                                                        "\n  This problem can be debugged by recompiling the servlet using only the classes in the application's runtime classpath\n")
                                        + MessageFormat.format(nls.getString("Application.classpath", "Application classpath=[{0}]"),
                                                new Object[] { context.getClasspath() }));
                    } catch (ClassFormatError e) {
                        com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(e,
                                "com.ibm.ws.webcontainer.webapp.WebAppServletManager.loadServlet", "227", this);
                        throw new UnavailableException(
                                MessageFormat.format(nls.getString("Servlet.found.but.corrupt", "Servlet [{0}]: {1} was found, but is corrupt:\n"),
                                        new Object[] { servletName, className })
                                        + nls.getString("class.resides.in.proper.package.directory",
                                                "1. Check that the class resides in the proper package directory.\n")
                                        + nls
                                                .getString("classname.defined.in.server.using.proper.case.and.fully.qualified.package",
                                                        "2. Check that the classname has been defined in the server using the proper case and fully qualified package.\n")
                                        + nls.getString("class.transfered.using.binary.mode",
                                                "3. Check that the class was transfered to the filesystem using a binary tranfer mode.\n")
                                        + nls.getString("class.compiled.using.proper.case",
                                                "4. Check that the class was compiled using the proper case (as defined in the class definition).\n")
                                        + nls.getString("class.not.renamed.after.compiled",
                                                "5. Check that the class file was not renamed after it was compiled."));
                    } catch (IOException e) {
                        com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(e,
                                "com.ibm.ws.webcontainer.webapp.WebAppServletManager.loadServlet", "1349", this);
                        throw new ServletException(
                                MessageFormat.format(nls.getString("IOException.loading.servlet"),
                                        new Object[] { servletName, className }), e);
                    }  
                    catch (InjectionException ie) {	//596191 Start	
						com.ibm.ws.ffdc.FFDCFilter.processException(ie, "com.ibm.ws.webcontainer.webapp.WebAppServletManager.loadServlet", "228", this);
						throw new ServletException(MessageFormat.format(nls.getString("Servlet.found.but.injection.failure", "For the [{0}] servlet, {1} servlet class was found, but a resource injection failure has occurred:\n"),
								new Object[] { servletName, className }),ie);						
					}//596191 End 
                    catch (IllegalAccessException e) {
                    	com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(e,
                                "com.ibm.ws.webcontainer.webapp.WebAppServletManager.loadServlet", "1349", this);
                        throw new ServletException(
                                MessageFormat.format(nls.getString("IllegalAccessException.loading.servlet"),
                                        new Object[] { servletName, className }), e);
                    } catch (InstantiationException e) {
                    	com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(e,
                                "com.ibm.ws.webcontainer.webapp.WebAppServletManager.loadServlet", "1349", this);
                        throw new ServletException(
                                MessageFormat.format(nls.getString("InstantiationException.loading.servlet"),
                                        new Object[] { servletName, className }), e);
                    }

                    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
                        logger.exiting(CLASS_NAME, "loadServlet"); // 569469

                    return null;
                }
            });

        } catch (PrivilegedActionException e) {
            Throwable th = e.getCause();
            if (th instanceof Exception)
                throw ((Exception) th);
            throw new Exception(th);
        } catch (Throwable t) {
            throw new Exception(t);
        } finally {
            if (platformHelper.isSyncToThreadPlatform() && context.getConfiguration().isSyncToThreadEnabled())
                platformHelper.securityIdentityPop(cred);

            if (origClassLoader != null) {
                final ClassLoader fOrigClassLoader = origClassLoader;
                ThreadContextHelper.setClassLoader(fOrigClassLoader);
            }
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) // 306998.15
                logger.exiting(CLASS_NAME, "loadServlet"); // 569469

        }

        return target;
    }

    private ServletEvent getServletEvent() {
        if (event == null) {
            event = new ServletEvent(this, servletConfig.getServletContext(), servletConfig.getServletName(), servletConfig.getClassName());
        }

        return event;
    }

    //New parameter added for init() PM01373
	private synchronized void handleUnavailableException(UnavailableException e, boolean isInit) throws UnavailableException
	 {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) // 306998.15
            logger.entering(CLASS_NAME, "handleUnavailableException", e); // 569469
        // logger.logp(Level.FINE, CLASS_NAME,"handleUnavailableException", "",
        // e);
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) // 306998.15
            logger.logp(Level.FINE, CLASS_NAME, "handleUnavailableException", "UnavailableException was thrown by servlet: " + getServletName()
                    + " reason:" + e.getMessage());
        if (state == UNAVAILABLE_PERMANENTLY_STATE) {
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) // 306998.15
                logger.exiting(CLASS_NAME, "handleUnavailableException", "state is already permanently unavailable, throw ue"); // 569469
            // logger.logp(Level.FINE, CLASS_NAME,"handleUnavailableException",
            // "state is already permanently unavailable, throw ue");
            throw new UnavailableException(unavailableMessage);
        } else if (e.isPermanent()) {
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) // 306998.15
                logger.logp(Level.FINE, CLASS_NAME, "handleUnavailableException", "exception is permanent");
            if (state == AVAILABLE_STATE) {
                if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) // 306998.15
                    logger.logp(Level.FINE, CLASS_NAME, "handleUnavailableException", "state is available so destroy the servlet");
                /*
                 * This means the UnavailableException is thrown from the
                 * service method so we should destroy this servlet.
                 */
                // destroy();
                // destroy does not remove from the requestMapper which can lead
                // to a 500 error code on a subsequent request
                if (!context.removeServlet(getServletName()))
                    ;
                {
                    // In the case that there are no servlet mappings, we still
                    // need to destroy
                    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) // 306998.15
                        logger.logp(Level.FINE, CLASS_NAME, "handleUnavailableException",
                                "removeServlet didn't destroy. destroy from handleUnavailableException");
                    destroy();
                }
            }
            unavailableMessage = e.getMessage();
            if(isInit){											
				this.setUninitialize();  // PM01373
			}
			else{				
            setUnavailable();
			}
        } else {
            int secs = e.getUnavailableSeconds();
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) // 306998.15
                logger.logp(Level.FINE, CLASS_NAME, "handleUnavailableException", "ue is not permanent, unavailable secs -->[" + secs + "]");
            if (secs > 0) {
                long time = System.currentTimeMillis() + ((secs) * 1000);
                if (time > unavailableUntil) {
                    unavailableMessage = e.getMessage();
                    // only set the time if the new time is farther away than
                    // the old time.
                    setUnavailableUntil(time, isInit);		//PM01373
                }
            }
        }
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) // 306998.15
            logger.exiting(CLASS_NAME, "handleUnavailableException"); // 569469
    }

    /**
     * Method setUnavailableUntil.
     * 
     * @param time
	 * @param isInit
     */
	private void setUnavailableUntil(long time, boolean isInit) //PM01373
	{
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
            logger.logp(Level.FINE, CLASS_NAME, "setUnavailableUntil", "setUnavailableUntil() : " + time);
        if(isInit){
			state = UNINITIALIZED_STATE;  //PM01373
		}
		else {
        state = UNAVAILABLE_STATE;
		}
        unavailableUntil = time;
        evtSource.onServletUnavailableForService(getServletEvent());
    }
	
	// PM01373 Start
	/**
	 * Puts a servlet into uninitialize state.
	 * 
	 */
	protected void setUninitialize()
	{
    	if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
    		logger.logp(Level.FINE, CLASS_NAME,"setUninitialized ","" + this.toString());    

		state = UNINITIALIZED_STATE;
	}
	// PM01373 End

    /**
     * Puts a servlet out of service. This will destroy the servlet (if it was
     * initialized) and then mark it permanently unavailable
     */
    protected void setUnavailable() {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
            logger.logp(Level.FINE, CLASS_NAME, "setUnavailable", "" + this.toString()); // PK26183

        state = UNAVAILABLE_PERMANENTLY_STATE;
        evtSource.onServletUnavailableForService(getServletEvent());
    }

    private void setAvailable() {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) // 306998.15
            logger.entering(CLASS_NAME, "setAvailable " + this.toString()); // 569469
        evtSource.onServletAvailableForService(getServletEvent());
        // System.out.println("Setting as available");
        state = AVAILABLE_STATE;
        unavailableMessage = null;
        unavailableUntil = -1;
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) // 306998.15
            logger.exiting(CLASS_NAME, "setAvailable"); // 569469
    }

    // 263020 Make protected since we must override destroy now
    // which makes a call to invalidateCacheWrappers
    protected synchronized void invalidateCacheWrappers() {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) // 306998.15
            logger.entering(CLASS_NAME, "invalidateCacheWrappers"); // 569469

        if (cacheWrappers != null) {
            // invalidate all the cache wrappers that wrap this target.
            Iterator i = cacheWrappers.iterator();

            while (i.hasNext()) {
                ServletReferenceListener w = (ServletReferenceListener) i.next();
                if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) // 306998.15
                    logger.logp(Level.FINE, CLASS_NAME, "invalidateCacheWrappers", "servlet reference listener -->[" + w + "]");
                w.invalidate();
            }

            cacheWrappers = null;
        }
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) // 306998.15
            logger.exiting(CLASS_NAME, "invalidateCacheWrappers"); // 569469
    }

    /**
     * @see com.ibm.ws.container.Container#addSubContainer(Container)
     */
    public void addSubContainer(Container con) {

    }

    /**
     * @see com.ibm.ws.container.Container#getParent()
     */
    public Container getParent() {
        return context;
    }

    /**
     * @see com.ibm.ws.container.Container#getSubContainer(String)
     */
    public Container getSubContainer(String name) {
        return null;
    }

    /**
     * @see com.ibm.ws.container.Container#initialize(Configuration)
     */
    public void initialize(Configuration config) {
    }

    /**
     * @see com.ibm.ws.container.Container#isActive()
     */
    public boolean isActive() {
        return (state != UNINITIALIZED_STATE);
    }

    /**
     * @see com.ibm.ws.container.Container#isAlive()
     */
    public boolean isAlive() {
        return (state != this.UNAVAILABLE_PERMANENTLY_STATE && state != this.UNAVAILABLE_STATE);
    }

    public long getLastAccessTime() {
        return lastAccessTime;
    }

    /**
     * @see com.ibm.ws.container.Container#removeSubContainer(String)
     */
    public Container removeSubContainer(String name) {
        return null;
    }

    /**
     * @see com.ibm.ws.container.Container#start()
     */
    public void start() {
    }

    /**
     * @see com.ibm.ws.container.Container#stop()
     */
    public void stop() {
    }

    /**
     * @see com.ibm.ws.container.Container#subContainers()
     */
    public Iterator subContainers() {
        return null;
    }

    /**
     * @see com.ibm.ws.core.CommandSequence#execute(IWCCRequest, IWCCResponse)
     */
    public void execute(IExtendedRequest req, IExtendedResponse res) {
    }

    /**
     * @see com.ibm.ws.core.RequestProcessor#handleRequest(Request, Response)
     */
    /**
     * @see javax.servlet.ServletConfig#getServletContext()
     */
    public ServletContext getServletContext() {
        return servletConfig.getServletContext();
    }

    public WebApp getWebApp() {
        return context;
    }

    /**
     * @see javax.servlet.ServletConfig#getServletName()
     */
    public String getServletName() {
        if (servletConfig == null)
            return null;
        return servletConfig.getServletName();
    }

    private IServletWrapper getMimeFilterWrapper(String mimeType) {
        try {
            if (mimeType.indexOf(";") != -1)
                mimeType = mimeType.substring(0, mimeType.indexOf(";"));
            return context.getMimeFilterWrapper(mimeType);
        } catch (ServletException e) {
            com.ibm.ws.ffdc.FFDCFilter.processException(e, "com.ibm.ws.webcontainer.webapp.WebAppRequestDispatcher.getMimeFilterReference", "834",
                    this);
            context.logError("Failed to load filter for mime-type: " + mimeType, e);
            return null;
        }
    }

    // begin pq50381: part 3
    private void transferHeadersFromPrevChainedResp(ChainedResponse current, ChainedResponse previous) {

        Iterable<String> headers = previous.getHeaderNames();
        for (String name:headers) {
            String value = (String) previous.getHeader(name);
            current.setHeader(name, value);
        }
        Cookie[] theCookies = previous.getCookies();
        for (int i = 0; i < theCookies.length; i++) {
            Cookie currCookie = theCookies[i];
            current.addCookie(currCookie);
        }
    }

    // end pq50381: part 3
    /*
     * (non-Javadoc)
     * 
     * @seecom.ibm.ws.webcontainer.util.CacheTarget#addCacheWrapper(com.ibm.ws.
     * webcontainer.util.CacheWrapper)
     */
    public void addServletReferenceListener(ServletReferenceListener listener) {
        if (this.cacheWrappers == null) {
            cacheWrappers = new ArrayList();
        }
        this.cacheWrappers.add(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ibm.wsspi.webcontainer.servlet.IServletWrapper#setTargetClassLoader
     * (java.lang.ClassLoader)
     */
    public void setTargetClassLoader(ClassLoader loader) {
        this.targetLoader = loader;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.webcontainer.servlet.IServletWrapper#getTarget()
     */
    public Servlet getTarget() {
        return target;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ibm.wsspi.webcontainer.servlet.IServletWrapper#getTargetClassLoader()
     */
    public ClassLoader getTargetClassLoader() {
        // TODO Auto-generated method stub
        return targetLoader;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ibm.wsspi.webcontainer.servlet.IServletWrapper#setTarget(javax.servlet
     * .Servlet)
     */
    public void setTarget(Servlet target) {
        this.target = target;
    }

    public String toString() {
        Iterable<String> mappings = null;
        // PK76117 Start
        if (state == UNAVAILABLE_PERMANENTLY_STATE) {
            return "ServletWrapper[Servlet is permanently unavailable]";
        }
        // PK76117 End
        if (servletConfig != null)
            mappings = servletConfig.getMappings();
        return "ServletWrapper[" + getServletName() + ":" + mappings + "]";
    }

    // begin 268176 Welcome file wrappers are not checked for resource existence
    // WAS.webcontainer
    /**
     * Returns whether the requested wrapper resource exists.
     */
    public boolean isAvailable() {
        return true;
    }

    // end 268176 Welcome file wrappers are not checked for resource existence
    // WAS.webcontainer

    protected synchronized void createTarget(Servlet s) throws InjectionException{ //596191
        if (s instanceof SingleThreadModel) {
            isSTM = true;
            servletConfig.setSingleThreadModelServlet(isSTM);
            target = new SingleThreadModelServlet(s.getClass());
        } else {
            target = s;
        }
    }

    // PK80340 Start
    public boolean isDefaultServlet() {
        boolean result = false;
        if (servletConfig != null) {
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE))
                logger.logp(Level.FINE, CLASS_NAME, "isDefaultServlet", " : mappings:" + servletConfig.getMappings());
            ;
            for (String curMapping:servletConfig.getMappings()){
                if (curMapping.equals("/"))
                    return true;
            }
        }
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() & logger.isLoggable(Level.FINE))
            logger.logp(Level.FINE, CLASS_NAME, "isDefaultServlet", " result=" + result);

        return false;
    }

    // PK80340 End

    public boolean isInternal() {
        return servletConfig.isInternal();
    }
    
    //modifies the target for SingleThreadModel & caching
    public void modifyTarget(Servlet s) {
        //do nothing here
        //can be overridden
    }
    
    
    public void startRequest(ServletRequest request) {
    	
		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {
			logger.entering(CLASS_NAME,"startRequest","state = " + state);
		}
    	
        HttpServletRequest httpServletReq = (HttpServletRequest) ServletUtil.unwrapRequest(request,HttpServletRequest.class);
        
        String method = httpServletReq.getMethod();

		boolean checkHeadRequest = (!defaultHeadRequestBehavior && method.equals("HEAD"));
		boolean checkTraceRequest = (!defaultTraceRequestBehavior && method.equals("TRACE"));
		
    	// Only check for default implementation if request is doTrace or doHead.
    	// Don't synchronize to prevent a bottleneck - result should be the same 
    	// even if two threads run at the same time.
		if ((checkHeadRequest || checkTraceRequest)) {
			if ((checkedForDefaultMethods==null) && (state == AVAILABLE_STATE) ) {
			    checkForDefaultImplementation();
			    checkedForDefaultMethods = new Boolean(Boolean.TRUE);
			}
			if (checkHeadRequest && this.defaultHeadMethodInUse) {
				request.setAttribute("com.ibm.ws.webcontainer.security.checkdefaultmethod","HEAD");	
			} else if(checkTraceRequest && this.defaultTraceMethodInUse) {
				request.setAttribute("com.ibm.ws.webcontainer.security.checkdefaultmethod","TRACE");
			}
		}
				
		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {
			logger.exiting(CLASS_NAME,"startRequest"," method = " + method);
		}

		
    }
	
    public void finishRequest(ServletRequest request) {
    	// remove attribute, if it was ever added (easier to just try the remove than work
    	// out if it is set.
		request.removeAttribute("com.ibm.ws.webcontainer.security.checkdefaultmethod");				
	}
   
	public void checkForDefaultImplementation() {
				
		
		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {
			logger.entering(CLASS_NAME,"checkForDefaultMethods","already checked = " + this.checkedForDefaultMethods);
		}
			
	    if (checkedForDefaultMethods == null ) {
			    	
            final String targetClassName = servletConfig.getClassName();
            final ClassLoader targetClassLoader = context.getClassLoader();
		
            try {
		        AccessController.doPrivileged(new PrivilegedExceptionAction() {
			    public Object run()
			    {
		                Class targetClass;
		                try {
		                    targetClass = Class.forName(targetClassName, false, targetClassLoader);
		                } catch (java.lang.ClassNotFoundException exc ) {
		    			    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable (Level.FINE))  //306998.14
		    			        logger.logp(Level.FINE, CLASS_NAME,"checkForDefaultMethods","Class not found when checking for default implementations. Class = " + targetClassName);
			                return null;	
		                }
		
				        if (!defaultHeadRequestBehavior)
					        defaultHeadMethodInUse = checkForDefaultImplementation(targetClass, "doHead", PARAMS_HEAD_TRACE);
		                if (!defaultTraceRequestBehavior)
		                    defaultTraceMethodInUse = checkForDefaultImplementation(targetClass, "doTrace", PARAMS_HEAD_TRACE);
		                return null;
			        }   					
		        });
            } catch (PrivilegedActionException exc) {
			    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable (Level.FINE))  //306998.14
			        logger.logp(Level.FINE, CLASS_NAME,"checkForDefaultMethods","PrivelegedActionException when checking for default implementations. Class = " + targetClassName);
            }
		}
				
	    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable (Level.FINE))  //306998.14
	    	logger.exiting(CLASS_NAME,"checkForDefaultMethods","Default's in use? doHead = " + (defaultHeadRequestBehavior ? "not checked" : (defaultHeadMethodInUse ? "true" : "false")) + ", doTrace = " + (defaultTraceRequestBehavior ? "not checked" : (defaultTraceMethodInUse ? "true" : "false")));;
		
	    return;
	}
		
	// PK83258 Add method checkDefaultImplementation
	private boolean checkForDefaultImplementation(Class checkClass, String checkMethod, Class[] methodParams){
		
	    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable (Level.FINE))  //306998.14
	    	logger.exiting(CLASS_NAME,"checkForDefaultImplementation","Method : " + checkMethod + ", Class : " + checkClass.getName());
		
		
		boolean defaultMethodInUse=true;
		
		while (defaultMethodInUse && checkClass!=null && !checkClass.getName().equals("javax.servlet.http.HttpServlet")) {
		    try {  
		    						       
		        checkClass.getDeclaredMethod(checkMethod, methodParams);
			
			    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable (Level.FINE))  //306998.14
			    	logger.logp(Level.FINE, CLASS_NAME,"checkForDefaultImplementation","Class implementing " + checkMethod + " is " + checkClass.getName());
			
			    defaultMethodInUse=false;
			    break;
			     
		    } catch (java.lang.NoSuchMethodException exc) {    	
			    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable (Level.FINE))  //306998.14
			    	logger.logp(Level.FINE, CLASS_NAME,"checkForDefaultImplementation",checkMethod + " is not implemented by class " + checkClass.getName()); 
		    } catch (java.lang.SecurityException exc) {
			    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable (Level.FINE))  //306998.14
			    	logger.logp(Level.FINE, CLASS_NAME,"checkForDefaultImplementation","Cannot determine if " + checkMethod + " is implemented by class " + checkClass.getName());				    	
		    }
		    
		    checkClass = checkClass.getSuperclass();
	    }  
	    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable (Level.FINE))  //306998.14
	    	logger.exiting(CLASS_NAME,"checkForDefaultImplementation","Result : " + defaultMethodInUse);
	    
        return defaultMethodInUse;
	}


}
