// IBM Confidential OCO Source Material
// 5639-D57,5630-A36,5630-A37,5724-D18 (C) COPYRIGHT International Business Machines Corp. 1997, 2008
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
//CHANGE HISTORY
//Flag    Defect         Date         Modified By         Description
//--------------------------------------------------------------------------------------
//	298927    	08/17/05	todkap		  improve filter handling for core    WASCC.web.webcontainer
//	299508    	08/22/05	todkap		  onServletStartService should be before onFilterStartDoFilter    WAS.webcontainer
//      PK27620         08/23/06        cjhoward          SERVLET FILTER IS NOT CALLED IN V6 FOR URL RESOURCES WHEN THESE ARE NOT FOUND.  IN V5, THE FILTER IS ALWAYS CALLED
//  415593     01/19/07    mmolden      70FVT: Error page test fail in web container bucket
//       489973          12/31/07      mmolden            70FVT:ServletRequestListener not firing when registered in tld
//       508566          04/09/08      mmolden            PERF: File Serving Performance improvement
//      PK55965          04/14/08      mmolden (pmdinh)   SMF data generated less in V6.x 
//       519701          05/14/08      cjhoward           CTS: JSP positiveIncludeCtxRelativeHtmlTest failed
//       PK64290         05/20/08      mmolden             SESSION LOSS WHEN USING ONLY URLREWRITING
//       PK70152         08/07/08      mmolden(pmdinh)     Static content (.html, .gifs) shows up after PK55965 but stats are showing zero.
//		 569469			 01/04/08	   pmdinh              Add entry and exit trace
//       PK80333         05/06/08      mmulholl            503 SERVLET TEMPORARILY UNAVAILABLE
//       PK99400         01/06/10      pmdinh              Option to display SMF record for static files with less detail (i.e 5.1's behavior)
//       PM34028         04/08/11      pmdinh              Correct the default MIME type "text/html" when compatibility flag is set
//       PM36341         04/19/11      anupag              Add case for IfModifiedSince Newer Than FileModified TimeStamp
//		 709533			 09/19/11	   pmdinh			   Portal migration to V8. fails

package com.ibm.ws.webcontainer.servlet;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import com.ibm.ejs.ras.TraceNLS;
import com.ibm.websphere.servlet.error.ServletErrorReport;
import com.ibm.websphere.servlet.event.ServletErrorEvent;
import com.ibm.websphere.servlet.event.ServletEvent;
import com.ibm.websphere.servlet.filter.ChainedResponse;
import com.ibm.ws.buffermgmt.impl.FCWsByteBufferImpl;
import com.ibm.ws.webcontainer.extension.DefaultExtensionProcessor;
import com.ibm.ws.webcontainer.filter.WebAppFilterManager;
import com.ibm.ws.webcontainer.srt.SRTServletRequest;
import com.ibm.ws.webcontainer.srt.SRTServletResponse;
import com.ibm.ws.webcontainer.srt.WriteBeyondContentLengthException;
import com.ibm.ws.webcontainer.webapp.WebApp;
import com.ibm.ws.webcontainer.webapp.WebAppDispatcherContext;
import com.ibm.ws.webcontainer.webapp.WebAppErrorReport;
import com.ibm.ws.webcontainer.webapp.WebAppEventSource;
import com.ibm.ws.webcontainer.webapp.WebAppRequestDispatcher;
import com.ibm.ws.webcontainer.webapp.WebAppServletInvocationEvent;
import com.ibm.wsspi.buffermgmt.WsByteBuffer;
import com.ibm.wsspi.webcontainer.IPlatformHelper;
import com.ibm.wsspi.webcontainer.RequestProcessor;
import com.ibm.wsspi.webcontainer.WCCustomProperties;
import com.ibm.wsspi.webcontainer.WebContainer;
import com.ibm.wsspi.webcontainer.collaborator.CollaboratorInvocationEnum;
import com.ibm.wsspi.webcontainer.collaborator.ICollaboratorHelper;
import com.ibm.wsspi.webcontainer.collaborator.IWebAppNameSpaceCollaborator;
import com.ibm.wsspi.webcontainer.collaborator.IWebAppSecurityCollaborator;
import com.ibm.wsspi.webcontainer.logging.LoggerFactory;
import com.ibm.wsspi.webcontainer.security.SecurityViolationException;
import com.ibm.wsspi.webcontainer.servlet.IExtendedRequest;
import com.ibm.wsspi.webcontainer.servlet.IExtendedResponse;
import com.ibm.wsspi.webcontainer.servlet.IServletConfig;
import com.ibm.wsspi.webcontainer.servlet.IServletContext;
import com.ibm.wsspi.webcontainer.servlet.IServletWrapper;
import com.ibm.wsspi.webcontainer.servlet.ServletReferenceListener;
import com.ibm.wsspi.webcontainer.util.ByteBufferWriter;
import com.ibm.wsspi.webcontainer.util.EncodingUtils;
import com.ibm.wsspi.webcontainer.util.IResponseOutput;
import com.ibm.wsspi.webcontainer.util.ServletUtil;
import com.ibm.wsspi.webcontainer.util.ThreadContextHelper;
import com.ibm.wsspi.webcontainer.util.WSServletOutputStream;

/**
 * @author asisin
 * 
 *         The ServletWrapper responsible for serving up static content.
 */
@SuppressWarnings("unchecked")
public abstract class FileServletWrapper implements IServletWrapper {
    protected static Logger logger = LoggerFactory.getInstance().getLogger("com.ibm.ws.webcontainer.servlet");
    private static final String CLASS_NAME = "com.ibm.ws.webcontainer.servlet.FileServletWrapper";
    protected static TraceNLS nls = TraceNLS.getTraceNLS(FileServletWrapper.class, "com.ibm.ws.webcontainer.resources.Messages");
    private boolean notifyInvocationListeners = false;
    private ArrayList listeners;
    private long lasAccessedTime = -1;
    protected WebApp context;
    protected DefaultExtensionProcessor parentProcessor;
    private ICollaboratorHelper collabHelper;
    private IWebAppNameSpaceCollaborator webAppNameSpaceCollab;
    private IWebAppSecurityCollaborator secCollab;
    private IPlatformHelper platformHelper;
    // PK44379 - number of threads currently executing the service method
    // this is used for the check before the destroy() is called
    private int nServicing = 0;
    protected boolean isZip;
    private int optimizeFileServingSize;

    // PK55965 Start
    protected WebAppEventSource evtSource;

    private ServletEvent event;
    private boolean wrapperInitialized;
    private StringBuffer servletAndFileName = new StringBuffer();
    private boolean sessionSecurityIntegrationEnabled = false;

    // PK55965 End
    
    // PK99400.  This property will also set fileWrapperEvents ...
    private static boolean fileWrapperEvents = WCCustomProperties.FILE_WRAPPER_EVENTS;
    private static boolean fileWrapperEventsLessDetail = WCCustomProperties.FILE_WRAPPER_EVENTS_LESS_DETAIL;  
    // PK99400
    private static boolean modifiedsincelaterthanfiletimestamp = WCCustomProperties.IFMODIFIEDSINCE_NEWER_THAN_FILEMODIFIED_TIMESTAMP; //PM36341

    // *** Uncomment for doing Mapped Byte Buffers
    // private int syncFileServingSize;
    // private int mappedByteBufferSize;
    // private static boolean useMappedByteBuffers =
    // Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.wsspi.webcontainer.usemappedbytebuffers"));

    /**
     * @param parent
     */
    public FileServletWrapper(IServletContext parent, DefaultExtensionProcessor parentProcessor) {
        this.context = (WebApp) parent;
        this.parentProcessor = parentProcessor;
        collabHelper = context.getCollaboratorHelper();
        webAppNameSpaceCollab = collabHelper.getWebAppNameSpaceCollaborator();
        secCollab = collabHelper.getSecurityCollaborator();

        sessionSecurityIntegrationEnabled = context.getSessionContext().getIntegrateWASSecurity();
        platformHelper = WebContainer.getWebContainer().getPlatformHelper();
        this.optimizeFileServingSize = parentProcessor.getOptimizeFileServingSize();
        // *** Uncomment for doing Mapped Byte Buffers
        // this.mappedByteBufferSize =
        // parentProcessor.getMappedByteBufferSize();

        // PK99400.. automatically set fileWrapperEvents to true
        if (fileWrapperEventsLessDetail)
            fileWrapperEvents = true;
        // PK99400

        // PK55965 Start
        this.evtSource = (WebAppEventSource) context.getServletContextEventSource();
        notifyInvocationListeners = evtSource.hasServletInvocationListeners()&& fileWrapperEvents;      //PK99400
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {
            logger.logp(Level.FINE, CLASS_NAME,"<init>", "FileServletWrapper created : " + evtSource.hasServletInvocationListeners() + 
                     ", do file servlet wrapper events->" + fileWrapperEvents + ", file servlet wrapper events with less detail ->"+ fileWrapperEventsLessDetail);  //PK99400
        }
        // PK55965 End

    }

    public void addServletReferenceListener(ServletReferenceListener wrapper) {
        if (listeners == null) {
            listeners = new ArrayList();
        }

        listeners.add(wrapper);
    }

    public void destroy() {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
            logger.entering(CLASS_NAME, "destroy " + this.toString());
        }
        // PK44379 START
        for (int i = 0; (nServicing > 0) && i < WCCustomProperties.SERVLET_DESTROY_WAIT_TIME; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(e, "com.ibm.ws.webcontainer.servlet.ServletInstance.destroy", "377",
                        this);
            }
        }

        // PK55965 Start
        if (notifyInvocationListeners) {
            evtSource.onServletStartDestroy(getServletEvent());
        }
        // PK55965 End

        // PK44379
        // Destroy this wrapper
        // nullify all fields

        // PK80333 add synchronization to prevent a listener being added by the
        // webcontainer
        // addTocache method during this invalidate process.
        synchronized (this) {
            if (listeners != null) {

                // fire invalidate events
                for (int i = 0; i < listeners.size(); i++) {
                    ServletReferenceListener listener = (ServletReferenceListener) listeners.get(i);
                    listener.invalidate();
                }

                listeners = null;
            }
        }

        // PK55965 Start
        if (notifyInvocationListeners) {
            evtSource.onServletFinishDestroy(getServletEvent());
            evtSource.onServletUnloaded(getServletEvent());
        }
        // PK55965 End

        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
            logger.exiting(CLASS_NAME, "destroy");
        }
    }

    public long getLastAccessTime() {
        return this.lasAccessedTime;
    }

    public IServletConfig getServletConfig() {
        return null;
    }

    public String getServletName() {
        return "File wrapper";
    }

    public Servlet getTarget() {
        // There is going to be no servlet target
        return null;
    }

    public ClassLoader getTargetClassLoader() {
        return null;
    }

    public void handleRequest(ServletRequest req, ServletResponse res) throws Exception {
        IExtendedRequest wasreq = (IExtendedRequest) ServletUtil.unwrapRequest(req);
        WebAppDispatcherContext dispatchContext = (WebAppDispatcherContext) wasreq.getWebAppDispatcherContext();
        handleRequest(req, res, dispatchContext);

    }

    public void handleRequest(ServletRequest req, ServletResponse res, WebAppDispatcherContext dispatchContext) throws Exception {
        // 569469
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
            logger.entering(CLASS_NAME, "handleRequest " + this.toString());
        }
        // 569469

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        // PK55965 Start
        if (notifyInvocationListeners && servletAndFileName.length() == 0) {
            String servletPath = null;
            String pathInfo = null;

            if (req.getAttribute(WebAppRequestDispatcher.REQUEST_URI_INCLUDE_ATTR) != null) {
                servletPath = (String) req.getAttribute(WebAppRequestDispatcher.SERVLET_PATH_INCLUDE_ATTR);
                pathInfo = (String) req.getAttribute(WebAppRequestDispatcher.PATH_INFO_INCLUDE_ATTR);
            } else {
                servletPath = request.getServletPath();
                pathInfo = request.getPathInfo();
            }
            this.servletAndFileName.append(servletPath);
            if (pathInfo != null) {
                int semicolon = pathInfo.indexOf(';');
                if (semicolon != -1)
                    pathInfo = pathInfo.substring(0, semicolon);
                this.servletAndFileName.append(pathInfo);
            }

            this.initialize(null);
        }
        // PK55965 End

        Object secObject = null;
        boolean securityEnforced = false; // PK70824

        Object cred = null;
        try {
            // PK01801 BEGIN
            if (sessionSecurityIntegrationEnabled && req instanceof IExtendedRequest) {
                ((IExtendedRequest) req).setRunningCollaborators(true); // PK01801
            }
            // PK01801 END

            // begin PK10057
            // get the security collaborator and call directly?
            try {
                // Don't invoke security since we already did it in
                // WebAppFilterManager.
                // We still have to call preInvoke though so it can do a
                // syncToThread check for whether the current user id on the
                // thread has access to the file on the z/OS file system
                securityEnforced = dispatchContext.isEnforceSecurity(); // PK70824
                secObject = secCollab.preInvoke(request, response, getFileName(), false);
            } catch (SecurityViolationException e) {
                secObject = collabHelper.processSecurityPreInvokeException(e, this, request, response, dispatchContext, context, getFileName());
                return;
            }
            if (platformHelper.isSyncToThreadPlatform() && context.getConfiguration().isSyncToThreadEnabled())
                cred = platformHelper.securityIdentityPush();

            if (sessionSecurityIntegrationEnabled && req instanceof IExtendedRequest) {
                ((IExtendedRequest) req).setRunningCollaborators(false); // PK01801
            }

            dispatchContext.pushServletReference(this);

            if (request.isSecure()) {
                String cipherSuite = null;

                // we have an SSL connection...set the attributes
                ServletRequest implRequest = ServletUtil.unwrapRequest(request);
                cipherSuite = ((SRTServletRequest) implRequest).getCipherSuite();

                if (cipherSuite != null) {
                    request.setAttribute("javax.servlet.request.cipher_suite", cipherSuite);

                    // see if there's a bit size
                    Integer keySize = WebContainer.getWebContainer().getKeySize(cipherSuite);

                    if (keySize != null)
                        request.setAttribute("javax.servlet.request.key_size", keySize);
                }
            }

            WebAppServletInvocationEvent invocationEvent = null;

            if (notifyInvocationListeners)
                invocationEvent = new WebAppServletInvocationEvent(this, getServletContext(), getServletAndFileName(), getFileName(), request,
                        response); // PK70152

            // PK0227: Define the local classloader variable
            ClassLoader origClassLoader = null;
            boolean isInclude = dispatchContext.isInclude();
            boolean isForward = dispatchContext.isForward();
            try {
                // 2.4 Listeners
                if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) 
                    logger.logp(Level.FINE, CLASS_NAME, "handleRequest" , "(isInclude,isForward)"+  "("+isInclude+","+isForward+")"); 

                if (context.isMimeFilteringEnabled()) {
                    // PK0227: Set classloader to CompoundClassLoader
                    origClassLoader = ThreadContextHelper.getContextClassLoader();
                    final ClassLoader warClassLoader = context.getClassLoader();
                    if (warClassLoader != origClassLoader)
                        ThreadContextHelper.setClassLoader(warClassLoader);
                    ChainedResponse chainedResp = new ChainedResponse(request, response);

                    // set a default content type
                    chainedResp.setContentType("text/html");

                    // defect 55215 - automatically transfer client headers
                    // through the chain
                    Enumeration names = request.getHeaderNames();
                    while (names.hasMoreElements()) {

                        String name = (String) names.nextElement();
                        String value = request.getHeader(name);
                        if (!name.toLowerCase().startsWith("content")) {

                            // don't transfer content headers
                            chainedResp.setAutoTransferringHeader(name, value);
                        }
                    }

                    this.service(request, chainedResp, invocationEvent);

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
                        request = chainedResp.getChainedRequest();

                        // begin pq50381: part 1 --> keep a copy of previous
                        // chainedResponse
                        // Purpose: Pass pertinent response information along
                        // chain.
                        ChainedResponse prevChainedResp = chainedResp;
                        // begin pq40381: part 1

                        chainedResp = new ChainedResponse(request, response);

                        // begin pq50381: part 2
                        // Purpose: Transcode Publishing checks StoredResponse
                        // for
                        // mime filtered servlet's contentType to decide how to
                        // filter
                        // ultimate response. Transfer client set headers/
                        // cookies thru chain.
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
                            // chainedResp.setAutoTransferringHeader
                            // ("location", _redirectURI); // could not get this
                            // to work.
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

                        ((ServletWrapper) wrapper).service(request, chainedResp, invocationEvent); // optimized
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

                    chainedResp.transferResponse(response);
                } else {

                    service(request, response, invocationEvent);

                    // flush any buffered data that has been written to the
                    // response.
                    // We don't flush the buffer if this servlet was included
                    // (see spec)
                    if (!isInclude) {
                        if (response instanceof IResponseOutput) {
                            ((IResponseOutput) response).flushBuffer(false);
                        } else {
                            response.flushBuffer();
                        }
                    }
                }
            } catch (WriteBeyondContentLengthException clex) {
                com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(clex, "com.ibm.ws.webcontainer.servlet.ServletWrapper.handleRequest()",
                        "293");
                dispatchContext.pushException(clex);
                try {
                    response.flushBuffer();
                } catch (IOException i) {
                    ServletErrorReport errorReport = WebAppErrorReport.constructErrorReport(i, dispatchContext.getCurrentServletReference());
                    com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(i, "com.ibm.ws.webcontainer.servlet.ServletWrapper.handleRequest()",
                            "298");
                    throw errorReport;
                }
            } catch (IOException ioe) {
                if (isInclude || isForward) {
                    dispatchContext.pushException(ioe);
                    throw ioe;
                }
                ServletErrorReport errorReport = WebAppErrorReport.constructErrorReport(ioe, dispatchContext.getCurrentServletReference());
                dispatchContext.pushException(ioe);
                com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(ioe, "com.ibm.ws.webcontainer.servlet.ServletWrapper.handleRequest()",
                        "298");
                throw errorReport;
            } catch (ServletErrorReport ser) {
                com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(ser, "com.ibm.ws.webcontainer.webapp.WebAppRequestDispatcher.dispatch",
                        "428", this);
                dispatchContext.pushException(ser);
                // PK70824 call error page with security context if security was
                // enforced
                if (securityEnforced && !dispatchContext.isInclude() && !dispatchContext.isForward() && (req instanceof HttpServletRequest)) {
                    WebApp app = (WebApp) dispatchContext.getWebApp();
                    app.sendError((HttpServletRequest) req, (HttpServletResponse) res, ser);
                } else {
                    throw ser;
                }
                // PK70824 End
            } catch (ServletException se) {
                if (isInclude || isForward) {
                    dispatchContext.pushException(se);
                    throw se;
                }
                ServletErrorReport errorReport = WebAppErrorReport.constructErrorReport(se, dispatchContext.getCurrentServletReference());
                com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(se, "com.ibm.ws.webcontainer.servlet.ServletWrapper.handleRequest()",
                        "309");
                dispatchContext.pushException(se);
                throw errorReport;
            } catch (RuntimeException re) {
                if (isInclude || isForward) {
                    dispatchContext.pushException(re);
                    throw re;
                }
                ServletErrorReport errorReport = WebAppErrorReport.constructErrorReport(re, dispatchContext.getCurrentServletReference());
                dispatchContext.pushException(re);
                com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(re, "com.ibm.ws.webcontainer.webapp.WebAppRequestDispatcher.dispatch",
                        "448", this);
                throw errorReport;
            } catch (Throwable th) {
                ServletErrorReport errorReport = WebAppErrorReport.constructErrorReport(th, dispatchContext.getCurrentServletReference());
                dispatchContext.pushException(th);
                com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(th, "com.ibm.ws.webcontainer.servlet.ServletWrapper.handleRequest()",
                        "313");
                throw errorReport;
            } finally {
                // PK02277: Reset thread contxt classloader to what it was
                if (origClassLoader != null)
                    ThreadContextHelper.setClassLoader(origClassLoader);

                dispatchContext.popServletReference();
            }
        } finally {
            if (platformHelper.isSyncToThreadPlatform() && context.getConfiguration().isSyncToThreadEnabled())
                platformHelper.securityIdentityPop(cred);

            secCollab.postInvoke(secObject);

            // 569469
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
                logger.exiting(CLASS_NAME, "handleRequest");
            }
            // 569469
        }

    }

    /**
	 * 
	 */
    protected String getFileName() {
        return null;
    }

    /**
	 * 
	 */

    // PK27620
    public ServletContext getServletContext() {
        return null;
    }

    // public abstract void service(ServletRequest req, ServletResponse res,
    // WebAppServletInvocationEvent evt) throws IOException, ServletException;

    public void service(ServletRequest req, ServletResponse res, WebAppServletInvocationEvent evt) throws IOException, ServletException {
        // 569469
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
            logger.entering(CLASS_NAME, "service " + this.toString());
        }
        // 569469

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        IExtendedRequest wasreq = (IExtendedRequest) ServletUtil.unwrapRequest(req);
        WebAppDispatcherContext dispatchContext = (WebAppDispatcherContext) wasreq.getWebAppDispatcherContext();

        boolean writeResponseBody = true;
        // PK55965 Start
        boolean notify = notifyInvocationListeners && (evt != null);
        long curLastAccessTime = 0;
        long endTime = 0;

        if (notify) {
            evtSource.onServletStartService(evt);
            curLastAccessTime = System.currentTimeMillis();
        }
        // PK55965 End

        try {
            synchronized (this) {
                nServicing++;
            }
            boolean isInclude = dispatchContext.isInclude();
            if (!isInclude) {
                writeResponseBody = setResponseHeaders(request, response);
            }
            if (writeResponseBody) {

                // begin pq65763
                // <!-- move response writing into separate method -->
                writeResponseToClient(request, response, wasreq);
                // end pq65763
            }

            // PK55965 Start
            if (notify) {
                endTime = System.currentTimeMillis();
                evt.setResponseTime(endTime - curLastAccessTime);
                evtSource.onServletFinishService(evt);
            }
        } catch (Throwable e) {

            if (notify) {
                if (endTime == 0) {
                    endTime = System.currentTimeMillis();
                    evt.setResponseTime(endTime - curLastAccessTime);
                    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
                        logger.logp(Level.FINE, CLASS_NAME, "service", "Notify onServletFinishService : response time "
                                + (endTime - curLastAccessTime) + " milliseconds");
                    }
                    evtSource.onServletFinishService(evt);
                }
                ServletErrorEvent errorEvent = new ServletErrorEvent(this, getServletContext(), getServletName(), "FileServletWrapper", e);
                if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
                    logger.logp(Level.FINE, CLASS_NAME, "service", "Notify onServletServiceError : " + errorEvent.toString());
                }
                evtSource.onServletServiceError(errorEvent);
            }
            throw new ServletException(e);

        } finally {
            synchronized (this) {
                nServicing--;
            }
            // 569469
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
                logger.exiting(CLASS_NAME, "service");
            }
            // 569469
        }
    }

    protected abstract InputStream getInputStream() throws IOException;

    protected abstract RandomAccessFile getRandomAccessFile() throws IOException;

    protected abstract long getLastModified();

    protected abstract int getContentLength();

    protected abstract int getContentLength(boolean update);

    private boolean setResponseHeaders(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    	if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
    		logger.entering(CLASS_NAME, "setResponseHeaders");
    	}
    	// Add date header as per RFC 2616 sec 14.18
    	resp.setDateHeader("Date", System.currentTimeMillis());

        // begin pq65763
        String matchString = req.getRequestURI();
        // String type =
        // getServletContext().getMimeType(file.getAbsolutePath());
        String type = context.getMimeType(matchString);
        // end pq65763
        
        //PM34028 begin
        if (type == null && (context.getConfiguration().isAutoResponseEncoding() != true)){
            String contentTypeCompatibility = WCCustomProperties.CONTENT_TYPE_COMPATIBILITY;
            if (contentTypeCompatibility != null
                    && (contentTypeCompatibility.equalsIgnoreCase("V4") || contentTypeCompatibility.equalsIgnoreCase("V5")) 
                    && !(matchString.endsWith(".html") || matchString.endsWith(".htm")))
            {
                type = resp.getContentType();
            }
        }
        //PM34028 end

        EncodingUtils.setContentTypeByCustomProperty(type, matchString, resp);

        String esiControl = parentProcessor.getEsiControl();

        // Add ESI control header, if
        // (1) ESI not disabled &
        // (2) capability header is in request &
        // (3) this is not a forward &
        // (4) control header wasn't already added (by dynacache)
        // (5) request is not authenticated
        if ((esiControl != null) && (req.getHeader("Surrogate-Capability") != null)
                && (req.getAttribute(WebAppRequestDispatcher.DISPATCH_NESTED_ATTR) == null) && (!resp.containsHeader("Surrogate-Control"))
                && (req.getAuthType() == null)) {
            resp.addHeader("Surrogate-Control", esiControl);
        }

        /*
         * A code change is being made for defect PQ42792. A 304 Response is
         * being sent back whenever the "If-Modified-Since" header matches the
         * Last Modified Date of the file. Else the file is read and returned to
         * the browser.
         */

        long ModifiedSince = -1;
        try {
            ModifiedSince = req.getDateHeader("If-Modified-Since");
        } catch (IllegalArgumentException iae) {
            ModifiedSince = -1;
        }
        
        long FileModified = 0;
        if ( System.getSecurityManager() != null){
        	FileModified = AccessController.doPrivileged(new PrivilegedAction<Long>()  {        
                public Long run() {
                	return getLastModified();
                }
             });
        } else {
        	FileModified = getLastModified();
        }

        // PK65384 start
        if (FileModified == 0 && !isAvailable()) {
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
                logger.logp(Level.FINE, CLASS_NAME, "setResponseHeaders", "isAvailable false, setting 404 status");
            }
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, MessageFormat.format(nls.getString("File.not.found", "File not found: {0}"),
                    new Object[] { servletAndFileName }));

    		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
    			logger.exiting(CLASS_NAME, "setResponseHeaders", "file not found");
    		}

    		return false;
    	}
    	// PK65384 end

        // set the last modified date
        resp.setDateHeader("last-modified", FileModified);

        // PK65384 check to ensure ModifiedSince is not -1 before comparing.
        long systemTime  =  System.currentTimeMillis();
        if (ModifiedSince != -1){
             if (ModifiedSince / 1000 == FileModified / 1000) {                  
                resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);

                if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
    			    logger.exiting(CLASS_NAME, "setResponseHeaders", "file not modified");
    		    }

                return false;
             } else if(modifiedsincelaterthanfiletimestamp && (systemTime >= ModifiedSince) && ( ModifiedSince > FileModified )) {
                //RFC2616 :: The requested variant has not been modified
                //   since the time specified in this field, an entity will not be
                //   returned from the server; instead, a 304 (not modified) response will
                //   be returned without any message-body.
                resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);                        
                if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) { 
                    logger.exiting(CLASS_NAME, "setResponseHeaders","The requested variant has not been modified");
                }  
                return false;
             }  
             else if(systemTime < ModifiedSince)
             {
                //RFC2616 :: A date which is later than the server's current time is invalid. 
                // Ignore this conditional and send 200 in response 
                if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) 
                    logger.logp(Level.FINE, CLASS_NAME, "setResponseHeaders" ,  
                        "The IfModifiedSince date is later than the server's current time so it is invalid, ignore. ");                 
             }                   
        }
        //PM36341 End
        else {
    		ServletResponse wasres = ServletUtil.unwrapResponse(resp);				//709533
    		if (!(wasres instanceof IExtendedResponse) || (!((IExtendedResponse) wasres).isOutputWritten())) { //709533
        		int contentLen=0;
                if ( System.getSecurityManager() != null){
        		    contentLen = AccessController.doPrivileged(new PrivilegedAction<Integer>()  {
                        public Integer run() {
                    	    return new Integer(getContentLength());
                        }
                    });
                } else {
                	contentLen = getContentLength();
                }
        		resp.setContentLength(contentLen);
        	}	
        }
    	if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
    		logger.exiting(CLASS_NAME, "setResponseHeaders");
    	}
        return true;
    }

    protected void writeResponseToClient(final HttpServletRequest request, final HttpServletResponse resp, final IExtendedRequest wasreq) throws ServletException,
         IOException {
		 if ( System.getSecurityManager() != null){
			 try {
	            AccessController.doPrivileged(new PrivilegedExceptionAction()  {
	                 public Object run() throws ServletException, IOException {
	                	 _writeResponseToClient(request,resp,wasreq);
	                   	 return null;
	                 }
	              });
	         } catch (PrivilegedActionException pae) {
	        	 throw new IOException(pae.getMessage()); 
	         }

		 } else {
			 _writeResponseToClient(request,resp,wasreq);
		 }

    }
   
    protected void _writeResponseToClient(HttpServletRequest request, HttpServletResponse resp, IExtendedRequest wasreq) throws ServletException,
            IOException {
        // ALPINE InputStream in = null
        boolean isWritten = false;
        boolean rethrowIOException = false;
                	 
        try {
            ServletOutputStream os = resp.getOutputStream();
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
                logger.logp(Level.FINE, CLASS_NAME, "writeResponseToClient", "optimizeFileServingSize->" + optimizeFileServingSize
                        + " contentLength->" + getContentLength(false) + ", isZip->" + isZip);
            }

            if (!isZip && os instanceof WSServletOutputStream) {
                WSServletOutputStream wsos = ((WSServletOutputStream) os);
                int bufferSize = wsos.getBufferSize();
                int totalWritten = ((WSServletOutputStream) os).getTotal();
                int fileSize = getContentLength(false);

                RandomAccessFile raf = null;
                FileChannel channel = null;

                try {

                    // remove check for filters. being a WSServletOutputStream
                    // is good enough.
                    // WebContainerRequestState reqState =
                    // WebContainerRequestState.getInstance(false);
                    // //The most likely reason to not execute this path is that
                    // filters have been invoked.
                    // //Therefore do this check first.
                    // if (reqState==null||!reqState.isInvokedFilters()||){
                    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
                        logger.logp(Level.FINE, CLASS_NAME, "writeResponseToClient", "totalWritten->" + totalWritten + " bufferSize->" + bufferSize
                                + ", actual file size->" + fileSize + ", isTransferToOS->" + platformHelper.isTransferToOS());
                    }
                    if (totalWritten == 0 // don't write out if somebody (a
                            // filter?) has written stuff
                            && (bufferSize < fileSize) // don't write out if the
                            // file size is less than
                            // the buffer size. They
                            // could technically
                            // still clear the buffer
                            // afterwards if this was
                            // the case.
                            && bufferSize == SRTServletResponse.DEFAULT_BUFFER_SIZE
                            && optimizeFileServingSize != -1
                            && (fileSize >= optimizeFileServingSize)
                            && platformHelper.isTransferToOS()
                            && ( request.getProtocol().toUpperCase().startsWith("HTTP/") || request.getProtocol().equalsIgnoreCase("http"))) {

                        WebAppDispatcherContext dispatchCtx = (WebAppDispatcherContext) wasreq.getWebAppDispatcherContext();
                        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
                            logger.logp(Level.FINE, CLASS_NAME, "writeResponseToClient", "isInclude->" + dispatchCtx.isInclude() + " isForward->"
                                    + dispatchCtx.isForward());
                        }
                        if (!dispatchCtx.isInclude()) {
                            raf = getRandomAccessFile();
                            channel = raf.getChannel();

                            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) { // 306998.15
                                logger.logp(Level.FINE, CLASS_NAME, "writeResponseToClient", "writing using File Channel Byte Buffer Wrapper");
                            }
                            isWritten = true;

                            WsByteBuffer[] wsBufArray = new WsByteBuffer[1];
                            FCWsByteBufferImpl fcw = new FCWsByteBufferImpl(channel);
                            fcw.setStatus(FCWsByteBufferImpl.STATUS_TRANSFER_TO);
                            // fcw.setBufferSize(maxBufferSize);
                            wsBufArray[0] = (WsByteBuffer) (fcw);
                            ((ByteBufferWriter) os).writeByteBuffer(wsBufArray);

                        }
                    }

                } finally {
                    if (channel != null)
                        channel.close();
                    if (raf != null)
                        raf.close();
                }

            }
            if (!isWritten) {
                isWritten = true;
                rethrowIOException = true;
                writeByBytes(resp, os);
            }

            // END ZHJ
        } catch (IllegalStateException isEx) {
            if (!isWritten) {
                if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
                    logger.logp(Level.FINE, CLASS_NAME, "writeResponseToClient", "going to try to use the response writer");
                }
            	isWritten = true;
                writeByBytes(resp, null);
            } else {
            	//this should not happen since I believe the IllegalStateException can only be thrown before isWritten is set
            	com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(isEx,
                        "com.ibm.ws.webcontainer.servlet.SimpleFileServlet.writeResponseToClient", "689", this);
            }
        } catch (IOException ioexp) {
            // ignore as browser will close stream on write back
            // if last modified date is the same, defect PQ41741
            if (rethrowIOException)
                throw ioexp;
            com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(ioexp,
                    "com.ibm.ws.webcontainer.servlet.SimpleFileServlet.writeResponseToClient", "304", this);
        }

    }

    private void writeByBytes(HttpServletResponse resp, ServletOutputStream os) throws IOException {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
            logger.logp(Level.FINE, CLASS_NAME, "writeByBytes", "resp->" + resp + " os->" + os);
        }
        InputStream in = null;
        try {
            in = getInputStream();
            if (os != null) {
                int bufferSize = parentProcessor.getDefaultBufferSize();
                if (in instanceof FileInputStream) { // PK90207
                    FileChannel channel = ((FileInputStream) in).getChannel();
                    ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
                    int readSize = channel.read(buffer);
                    while (readSize != -1) {
                        os.write(buffer.array(), 0, readSize);
                        readSize = channel.read(buffer);
                        buffer.flip();
                    }
                    channel.close();
                }// PK90207 Start
                else {
                    // e.g. if 'in' of type
                    // java.util.zip.ZipFile.ZipFileInputStream
                    byte[] buf = new byte[bufferSize];
                    int readSize = in.read(buf);

                    while (readSize != -1) {
                        os.write(buf, 0, readSize);
                        readSize = in.read(buf);
                    }
                } // PK90207 End
            } else {
                // parent servlet already had writer, try using the writer
                // instead
                PrintWriter writer = resp.getWriter();
                char[] buf = new char[parentProcessor.getDefaultBufferSize()];
                InputStreamReader isIn = new InputStreamReader(in, resp.getCharacterEncoding());
                int numRead = isIn.read(buf);
                try {
                    while (numRead != -1) {
                        writer.write(buf, 0, numRead);
                        numRead = isIn.read(buf);
                    }
                } catch (IOException ioex) {
                    // ignore as browser will close stream on write back
                    // if last modified date is the same, defect PQ41741
                    com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(ioex,
                            "com.ibm.ws.webcontainer.servlet.SimpleFileServlet.writeResponseToClient", "298", this);
                }
            }
        } finally {
            if (in != null)
                in.close();
        }
    }

    public void initialize(IServletConfig config) throws Exception {
        // PK55965 Start
        if (!wrapperInitialized && notifyInvocationListeners) {
            wrapperInitialized = true;
            evtSource.onServletStartInit(getServletEvent());
            evtSource.onServletFinishInit(getServletEvent());
            evtSource.onServletAvailableForService(getServletEvent());
        }
        // PK55965 End
    }

    public void prepareForReload() {
        // don't need to do anything for this
    }

    public void setTarget(Servlet target) {
        // do nothing
    }

    public void setTargetClassLoader(ClassLoader loader) {
        // do nothing
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

    private void transferHeadersFromPrevChainedResp(ChainedResponse current, ChainedResponse previous) {

        Iterable<String> headers = previous.getHeaderNames();
        for (String name : headers) {
            String value = (String) previous.getHeader(name);
            current.setHeader(name, value);
        }
        Cookie[] theCookies = previous.getCookies();
        for (int i = 0; i < theCookies.length; i++) {
            Cookie currCookie = theCookies[i];
            current.addCookie(currCookie);
        }
    }

    /*
     * @param request
     * 
     * @param response
     * 
     * @throws IOException
     */
    public void service(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        WebAppServletInvocationEvent evt = null;
        if (notifyInvocationListeners) {
            evt = new WebAppServletInvocationEvent(this, getServletContext(), getServletAndFileName(), getFileName(), (HttpServletRequest) request,
                    (HttpServletResponse) response); // PK70152
        }

        service(request, response, evt);
    }

    // begin 268176 Welcome file wrappers are not checked for resource existence
    // WAS.webcontainer
    public boolean isAvailable() {
        return true;
    }

    // end 268176 Welcome file wrappers are not checked for resource existence
    // WAS.webcontainer

    // Begin PK27620

    public void nameSpacePostInvoke() {
        this.webAppNameSpaceCollab.postInvoke();
    }

    public void nameSpacePreInvoke() {
        this.webAppNameSpaceCollab.preInvoke(this.parentProcessor.getMetaData());
    }

    // PK55965 Start
    private ServletEvent getServletEvent() {
        if (event == null) {
            event = new ServletEvent(this, getServletContext(), this.getServletAndFileName(), getFileName());
        }

        return event;
    }

    private String getServletAndFileName() {
        if (!fileWrapperEventsLessDetail && (this.servletAndFileName.length() > 0)){        //PK99400
            return this.servletAndFileName.toString();
        } else {
            return this.getServletName();
        }
    }

    // PK55965 End

    public boolean isInternal() {
        return false;
    }

    public void loadOnStartupCheck() throws Exception {
        // do nothing, static files don't load on startup
    }
    
    public void load() throws Exception {
        // do nothing, static files have anything to load from the classloader or have an init method.
    }

    @Override
    public void modifyTarget(Servlet s) {
        //do nothing
    }

}
