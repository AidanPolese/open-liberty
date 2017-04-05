// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2008
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
//
//  CHANGE HISTORY
//  Flag    Defect         Date         Modified By         Description
//--------------------------------------------------------------------------------------
//	    298927    	    08/17/05	todkap		    improve filter handling for core    WASCC.web.webcontainer
//          PK10057         09/14/05    todkap              WSAS 6.0.2 WEB APPLICATION WELCOME PAGES NOT SECURED.    WAS.webcontainer
//	    PK10057.1       10/03/05    todkap              WSAS 6.0.2 WEB APPLICATION WELCOME PAGES NOT SECURED.    WAS.webcontainer
//	    309663          10/04/05    todkap              V61FVT: getWebConstraints has intermittent failures    WAS.webcontainer
//          309663.1        10/07/05    todkap              309663 needed to skip cache on forwards and includes   WAS.webcontainer
//          PK13570         01/03/06    mmolden             WITH SYNCTOOSTHREAD ENABLED, SIMPLE FILES (.GIF, .HTML, ETC...)    WAS.webcontainer
//          PK15276         01/03/06    mmolden             INCORRECT FILTER MAPPING: WAS 6.0.2 VIOLATES THE SERVLET    WAS.webcontainer
//          336082          01/10/05    mmolden             61FVT:RTF: All the images cannot display if security enabled
//          338876          01/17/06    todkap              61FVT:RRD-error request attrs not set    WAS.webcontainer    
//          PK22928         04/25/06    cjhoward            SOURCE CODE OF JSP MIGHT BE DISPLAYED FOR SOME SPECIAL URI
//	    364580	    04/28/06	todkap		    SSC: SVT:NMSV0308W: javaURLContextFactory cannot create
//          PK25868         06/14/06    mmolden             WHEN CLIENT USES SERVLET FILTER FUNCTION THEY ARE RECEIVING AN
//          PK23475	    06/26/06	ekoonce             Add checking of request allowability for extended document root files 
//          PK24615	    05/26/06    mmulholl            Only reject requests which start with "WEB-INF/" or "META-INF/"
//          PK27620         08/23/06    cjhoward            SERVLET FILTER IS NOT CALLED IN V6 FOR URL RESOURCES WHEN THESE ARE NOT FOUND.  IN V5, THE FILTER IS ALWAYS CALLED
//	    395087          10/24/06    mmulholl            Improve Trace  
//          PK36447         01/15/07    ekoonce             Source exposure of files under WEB-INF & META-INF on dispatch
//          PK31377         04/13/07    sartoris            Servlet filter is not called for URL resources
//          PK45107         08/30/07    mmolden            EXCEPTION FROM SERVLET FILTER IS NOT PROPAGATED TO CLIENT
//       489973          12/31/07      mmolden             70FVT:ServletRequestListener not firing when registered in tld
//       508566          04/09/08      mmolden             PERF: File Serving Performance improvement
//       PK64290         05/20/08      mmolden             SESSION LOSS WHEN USING ONLY URLREWRITING
//          PK64302        04/28/08     mmulholl            Return 403 in prefernce to 404 for forbidden requests.
//          PK65408        08/01/08     mmolden             ABSTRACT: "SRVE0190E: FILE NOT FOUND:" FROM A JSP WHICH USES
//          542155         08/07/08     mmolden              Modify PK6430 - 404 to 403 only if directory is traversed.                                                                                                                                                                                                                
//			PK78371      	01/13/09    pmdinh              Option to append queryString when forwarding to Welcome File
//			PK85015		   05/07/09		anupag				commit response if less then 4096 bytes
//          PM17845         07/13/10    mmulholl            allow for a request to a directory in an extended document root to be a partial URL
//          PM21451         03/25/11    mmulholl            Use new getRealPath and getStaticDocumentRootUtils methods
//          PM36303         04/19/11    pmdinh              404 returns for webcome_file after 7.0.0.13 with synToOSThread enabled.

package com.ibm.ws.webcontainer.extension;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.ejs.ras.TraceNLS;
import com.ibm.ws.webcontainer.WebContainer;
import com.ibm.ws.webcontainer.exception.IncludeFileNotFoundException;
import com.ibm.ws.webcontainer.servlet.FileServletWrapper;
import com.ibm.ws.webcontainer.servlet.ZipFileServletWrapper;
import com.ibm.ws.webcontainer.util.DocumentRootUtils;
import com.ibm.ws.webcontainer.util.ExtendedDocumentRootUtils;
import com.ibm.ws.webcontainer.util.MetaInfResourcesFileUtils;
import com.ibm.ws.webcontainer.util.ZipFileResource;
import com.ibm.ws.webcontainer.webapp.WebApp;
import com.ibm.ws.webcontainer.webapp.WebAppDispatcherContext;
import com.ibm.ws.webcontainer.webapp.WebAppErrorReport;
import com.ibm.ws.webcontainer.webapp.WebAppRequestDispatcher;
import com.ibm.wsspi.webcontainer.IPlatformHelper;
import com.ibm.wsspi.webcontainer.RequestProcessor;
import com.ibm.wsspi.webcontainer.WCCustomProperties;
import com.ibm.wsspi.webcontainer.collaborator.ICollaboratorHelper;
import com.ibm.wsspi.webcontainer.collaborator.IWebAppNameSpaceCollaborator;
import com.ibm.wsspi.webcontainer.collaborator.IWebAppSecurityCollaborator;
import com.ibm.wsspi.webcontainer.logging.LoggerFactory;
import com.ibm.wsspi.webcontainer.metadata.WebComponentMetaData;
import com.ibm.wsspi.webcontainer.security.SecurityViolationException;
import com.ibm.wsspi.webcontainer.servlet.IExtendedRequest;
import com.ibm.wsspi.webcontainer.servlet.IServletConfig;
import com.ibm.wsspi.webcontainer.servlet.IServletContext;
import com.ibm.wsspi.webcontainer.servlet.IServletWrapper;
import com.ibm.wsspi.webcontainer.util.ServletUtil;
import com.ibm.wsspi.webcontainer.util.URIMatcher;
import com.ibm.wsspi.webcontainer.webapp.NamespaceInvoker;

/**
 * @author asisin
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
@SuppressWarnings("unchecked")
public abstract class DefaultExtensionProcessor extends WebExtensionProcessor implements NamespaceInvoker, javax.servlet.Servlet
{
	
	public static final String PARAM_DEFAULT_PAGE = "default.page";
	public static final String PARAM_BUFFER_SIZE = "bufferSize";
	// PK24615 add trailing "/" to WEB-INF_DIR and META-INF-DIR to ensure matching directories only
	public static final String WEB_INF_DIR = "WEB-INF/";
	public static final String META_INF_DIR = "META-INF/";

    private static TraceNLS nls = TraceNLS.getTraceNLS(DefaultExtensionProcessor.class, "com.ibm.ws.webcontainer.resources.Messages");
        protected static Logger logger = LoggerFactory.getInstance().getLogger("com.ibm.ws.webcontainer.extension");
	private static final String CLASS_NAME="com.ibm.ws.webcontainer.extension.DefaultExtensionProcessor";

	private int defaultBufferSize = 4096;
	private String esiControl = null;
	
	//	 defect 220552: begin add patternList vars used to serve or deny requests related to DefaultExtensionProcessor
	private static final String DEFAULT_MAPPING = "/*";

	protected List patternList = new ArrayList();

	private static int optimizeFileServingSizeGlobal=-1;
//	private static int mappedByteBufferSizeGlobal;
	
	private int optimizeFileServingSize=1000000;
//	private int mappedByteBufferSize=-1;
	
	private static final List DEFAULT_DENY_EXTENSIONS = new ArrayList();
	static {
		DEFAULT_DENY_EXTENSIONS.add("*.jsp");
		DEFAULT_DENY_EXTENSIONS.add("*.jsv");
		DEFAULT_DENY_EXTENSIONS.add("*.jsw");
		DEFAULT_DENY_EXTENSIONS.add("*.jspx");
		try {
			String sizeStr = WCCustomProperties.OPTIMIZE_FILE_SERVING_SIZE_GLOBAL;
			if (sizeStr!=null)
				optimizeFileServingSizeGlobal = Integer.valueOf(WCCustomProperties.OPTIMIZE_FILE_SERVING_SIZE_GLOBAL);
		}catch (NumberFormatException nfe){
            com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(nfe, "com.ibm.ws.webcontainer.extension.DefaultExtensionProcessor.<init>", "65");
			
		}
		
	}
	protected URIMatcher denyPatterns = null;
	//	 defect 220552: end add patternList vars used to serve or deny requests related to DefaultExtensionProcessor

	
	
	// begin pq70834
	protected boolean redirectToWelcomeFile = false;
	// end pq70834
	protected WebComponentMetaData cmd;
	protected IPlatformHelper platformHelper;
	
	private Map params;
	
	protected List welcomeFileList;
        
        protected WebApp _webapp;
private ICollaboratorHelper collabHelper;
	private IWebAppNameSpaceCollaborator webAppNameSpaceCollab;
	private IWebAppSecurityCollaborator secCollab;
        private boolean exposeWebInfOnDispatch;  //PK36447
    
	/**
	 * 
	 */
	public DefaultExtensionProcessor(IServletContext webapp, HashMap params)
	{
		super (webapp);
                _webapp = (WebApp) webapp;
		this.params = params;
		collabHelper = _webapp.getCollaboratorHelper();
		webAppNameSpaceCollab =  collabHelper.getWebAppNameSpaceCollaborator();
	    secCollab = collabHelper.getSecurityCollaborator();
		platformHelper = WebContainer.getWebContainer().getPlatformHelper();
		welcomeFileList = (List) webapp.getAttribute(WebApp.WELCOME_FILE_LIST);
		init();
	}

	private void init()
	{
		// If there is a "bufferSize" Init Parameter set, use it as the new
		// read/write buffer size.
		
		
		//PK36447
		exposeWebInfOnDispatch = WCCustomProperties.EXPOSE_WEB_INF_ON_DISPATCH;
                if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
                    logger.logp(Level.FINE, CLASS_NAME,"init", "exposeWebInfOnDispatch ---> true");
		String stringBufferSize = getInitParameter(PARAM_BUFFER_SIZE);
		if (stringBufferSize != null)
		{
			try
			{
				int tempBufferSize = (Integer.parseInt(stringBufferSize));
				defaultBufferSize = tempBufferSize;
			}
			catch (NumberFormatException ex)
			{
                com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(ex, "com.ibm.ws.webcontainer.servlet.SimpleFileServlet.init", "65", this);
			}
		}
		
		//	 defect 220552: begin look up and parse init attributed related to file serving patterns and process
		String fileServingExtensions = getInitParameter("file.serving.patterns.allow");
		if (fileServingExtensions != null) {
			patternList = parseFileServingExtensions(fileServingExtensions);
			if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){
				logger.logp(Level.FINE, CLASS_NAME,"init", "URI patterns for FileServing =[" + patternList +"]");
			}
		} else {
			this.patternList.add(DEFAULT_MAPPING);
			if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){
				logger.logp(Level.FINE, CLASS_NAME,"init", "Default URI pattern for FileServing =[" + patternList +"]");
			}
		}

		String fileServingExtensionsDenied = getInitParameter("file.serving.patterns.deny");
		if (fileServingExtensionsDenied != null) {
			List list = parseFileServingExtensions(fileServingExtensionsDenied);
			list.addAll(DEFAULT_DENY_EXTENSIONS);
			if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){
				logger.logp(Level.FINE, CLASS_NAME,"init", "Denied URI patterns for FileServing =[" + list +"]");
			}
			denyPatterns = createURIMatcher(list);
		} else {
			List list = DEFAULT_DENY_EXTENSIONS;
			denyPatterns = createURIMatcher(list);
			if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){
				logger.logp(Level.FINE, CLASS_NAME,"init", "Default denied patterns for FileServing =[" + list +"]");
			}
		}
		// defect 220552: end look up and parse init attributed related to file serving patterns and process

		String esiTimeout = (String) AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				return System.getProperties().getProperty ("com.ibm.servlet.file.esi.timeOut","300");
			}
		});
                
		if (!esiTimeout.equals("0"))
		{
			esiControl = "max-age=" + esiTimeout + "," +
						 "cacheid=\"URL\"," +
						 "content=\"ESI/1.0+\"";
		}
		esiControl = (String) AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				return System.getProperties().getProperty ("com.ibm.servlet.file.esi.control",esiControl);
			}
		});
		// begin pq70834
		String redirectToWelcomeFileStr = getInitParameter("redirectToWelcomeFile");
		if( redirectToWelcomeFileStr != null){
			redirectToWelcomeFile =  redirectToWelcomeFileStr.equalsIgnoreCase("true");	
		}
		optimizeFileServingSize=getFileServingIntegerAttribute("com.ibm.ws.webcontainer.optimizefileservingsize",optimizeFileServingSizeGlobal);
//		mappedByteBufferSize=getFileServingIntegerAttribute("mappedByteBufferSize",mappedByteBufferSizeGlobal);
		// end pq70834
	}

	private int getFileServingIntegerAttribute (String attributeKey, int defaultValue){
		int integerAttribute=defaultValue;
		try {
			 if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){
	            	logger.logp(Level.FINE, CLASS_NAME,"getFileServingIntegerAttribute", "attributeKey->"+attributeKey);
			}
			String integerAttributeStr = getInitParameter(attributeKey);
			if (integerAttributeStr!=null){
				 if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){
		            	logger.logp(Level.FINE, CLASS_NAME,"getFileServingIntegerAttribute", "integerAttributeStr->"+integerAttributeStr);
				}
				integerAttribute = Integer.valueOf(integerAttributeStr).intValue();
			}
		}
		catch (NumberFormatException nfe){
            com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(nfe, "com.ibm.ws.webcontainer.extension.DefaultExtensionProcessor.init", "65", this);
			 if(logger.isLoggable(Level.SEVERE))
	            	logger.logp(Level.SEVERE, CLASS_NAME,"getFileServingIntegerAttribute", "NumberFormatException.for.file.size.at.which.you.switch.to.optimized.file.serving");
		}
		return integerAttribute;
	}

	/**
	 * @param PARAM_BUFFER_SIZE
	 * @return
	 */
	private String getInitParameter(String param)
	{
		return (String) params.get(param);
	}

    public String getName (){
    	return CLASS_NAME;
    }
    

	/* (non-Javadoc)
	 * @see com.ibm.wsspi.webcontainer.RequestProcessor#handleRequest(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
	 */
    public void handleRequest(ServletRequest request, ServletResponse response) throws Exception {
        Object cred = null;
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        StringBuffer path =null;
        WebAppDispatcherContext dispatchContext =null;
        String pathInfo = null; // PK64302
        FileNotFoundException fnf = null;
        
    	
		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){
         	logger.entering(CLASS_NAME,"handleRequest : request--->" + req.getRequestURI()+ "<---");
		}
		
        try {
        	IExtendedRequest wasreq = (IExtendedRequest) ServletUtil.unwrapRequest(request);
			dispatchContext = (WebAppDispatcherContext)wasreq.getWebAppDispatcherContext();
        
            //ALPINE: Talk to Namespace
                //webAppNameSpaceCollab.preInvoke(cmd);
			
            //remove since the preInvoke is done in WebAppFilterManager
			//The preInvoke in WebAppFilterManager calls with doAuth/enforceSecurity=true which will setup
			//the metadata for sync to thread just as well as a call with doAuth=false would.
			//securityPreInvokes.push(secCollab.preInvoke(req, resp, null, false));
        
            if (platformHelper.isSyncToThreadPlatform() && ((WebApp)_webapp).getConfiguration().isSyncToThreadEnabled())
                cred = platformHelper.securityIdentityPush();
        
            boolean isInclude = false; // is this an include request?
            ServletContext context = _webapp;
            String fileSystemPath = null; // actual OS dependent path
            path = new StringBuffer(); // constructed path

            String servletPath = null; // PK64302

            if (req.getAttribute(WebAppRequestDispatcher.REQUEST_URI_INCLUDE_ATTR) != null) {
                isInclude = true;
                servletPath = (String) req.getAttribute(WebAppRequestDispatcher.SERVLET_PATH_INCLUDE_ATTR);
                pathInfo = (String) req.getAttribute(WebAppRequestDispatcher.PATH_INFO_INCLUDE_ATTR);
            }
            else {
                servletPath = req.getServletPath();
                pathInfo = req.getPathInfo();
            }

            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){
            	logger.logp(Level.FINE, CLASS_NAME,"handleRequest", "servletPath [" + servletPath +"] pathInfo [" + pathInfo +"]");
            }
            
            path.append(servletPath);

            if (pathInfo != null) {
            	int semicolon = pathInfo.indexOf(';');
                if (semicolon != -1)
                    pathInfo = pathInfo.substring(0, semicolon);
               
                path.append(pathInfo);

                //PK36447
                String tempPathInfo = removeLeadingSlashes(pathInfo);
                if(tempPathInfo != null) {
                    
                    if(!(exposeWebInfOnDispatch && tempPathInfo.startsWith(WEB_INF_DIR))) {
                        pathInfo = tempPathInfo;
                        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
                            logger.logp(Level.FINE, CLASS_NAME,"handleRequest"," stripping leading slashes pathInfo ---> " + pathInfo);
                    }
    
                    // PK24615 change conditionals from "indexOf() != -1" to "startsWith()"
                    if (pathInfo.startsWith(WEB_INF_DIR) || pathInfo.startsWith(META_INF_DIR)) {
                        resp.sendError(
                            HttpServletResponse.SC_FORBIDDEN,
                            MessageFormat.format(nls.getString("File.not.found", "File not found: {0}"), new Object[] { path.toString()}));
                        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
                            logger.exiting(CLASS_NAME,"handleRequest","Forbidden-WEB-INF/META-INF");
                        return;
                    }
                }
            }

            fileSystemPath = _webapp.getRealPath(path.toString(),"DEP");
            if (!fileSystemPath.endsWith(File.separator))
                fileSystemPath = fileSystemPath + File.separator;

            // PQ79698 part1 starts
            if (!isValidFilePath(fileSystemPath)) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, nls.getString("File.name.contains.illegal.character", "File path contains illegal character."));
                if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
                	logger.exiting(CLASS_NAME,"handleRequest","Forbidden-invalid file path");
                return;
            }
            // PQ79698 part1 ends
			// defect 220552: begin implement the deny feature for specified URI patterns
			else{
				Object matchedURI = denyPatterns.match(path.toString());
				if(matchedURI != null){	
					if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){
						logger.logp(Level.FINE, CLASS_NAME,"handleRequest", "Attempted to serve URI that matches denied URL pattern URI =[" + path.toString() + "] matched [" + matchedURI + "]");
					}
					resp.sendError(HttpServletResponse.SC_FORBIDDEN,							
							MessageFormat.format(nls.getString(
							"File.not.found", "File not found: {0}"),
							new Object[] { path.toString() }));
                    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
                    	logger.exiting(CLASS_NAME,"handleRequest","Forbidden-denied pattern");
						return;
				}
			}
			// defect 220552: end implement the deny feature for specified URI patterns

            boolean fileIsDirectoryInDocumentRoot = false;         // PM17845

            File file = new File(fileSystemPath);
            
            if (!checkFileExists(file)) {
            	
            	if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
    				logger.logp(Level.FINE, CLASS_NAME,"handleRequest","file does not exist --> " + fileSystemPath);
            	
                if (processEDR(path,req,resp,dispatchContext)) {
                	if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
        				logger.logp(Level.FINE, CLASS_NAME,"handleRequest","file processed from EDR");
                	return;
                } else {
                	fileIsDirectoryInDocumentRoot = true;
                }
                
            }    
            // PM17845 Add check for ileIsDirectoryInExtendedDocumnetRoot
            if (checkFileIsDirectory(file) || fileIsDirectoryInDocumentRoot) 
            {
            	
            	if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
    				logger.logp(Level.FINE, CLASS_NAME,"handleRequest","file is a directory --> " + fileSystemPath);
                
                /*
                 * The file is a directory so first see if we should append a trailing slash to the
                 * uri and redirect.  After that try and serve a welcome file or directory browse.
                 */
                String requestString = getURLWithRequestURIEncoded(req).toString();
                if (!requestString.endsWith("/")) {
                    if (isInclude == false) {
                        String tmpURL = requestString + "/"; // append a slash for redirect purposes.
                        String qString;
                        if ((qString = req.getQueryString()) != null) { // if query string exists; add it.
                            tmpURL += "?" + qString;
                        }
                        
                        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
                        	logger.logp(Level.FINE, CLASS_NAME,"handleRequest","sendRedirect -->" + tmpURL);
                        
                        // encode redirect url to keep session info.
                        resp.sendRedirect(resp.encodeRedirectURL(tmpURL));
                        
                        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
                        	logger.exiting(CLASS_NAME,"handleRequest","redirect to context root");
                                              
                        return;
                    }
                }
                //begin PK10057
                if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){
                    logger.logp(Level.FINE, CLASS_NAME,"handleRequest","calling security check for URI --> " + path.toString() );
                }
                
                //The preInvoke in WebAppFilterManager calls with doAuth/enforceSecurity=true
                //securityPreInvokes.push(secCollab.preInvoke ( req, resp, path.toString(), dispatchContext.isEnforceSecurity()));
             
                if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){
                    logger.logp(Level.FINE, CLASS_NAME,"handleRequest","returned from security check for URI --> " + path.toString() );
                }
                //end PK10057
                
                String welcomeFileRedirectUri = null;
                String welcomeFileForwardUri = null;
                
                if ((welcomeFileList != null) && (welcomeFileList.size() != 0)) {
                	if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){
                    	logger.logp(Level.FINE, CLASS_NAME,"handleRequest", "Check welcome file list");
                	}
                    /*
                     * Check the welcome files to see if we should be serving
                     * one of them up here.
                     */
                    Iterator e = welcomeFileList.iterator();
                    String page = null;
                    //PM36303
                    Object credToken = null;

                    if (platformHelper.isSyncToThreadPlatform() && ((WebApp)_webapp).getConfiguration().isSyncToThreadEnabled()){
                        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {
                            logger.logp(Level.FINE, CLASS_NAME,"handleRequest", "about to call securityIdentityPush() ");
                        }
                        credToken = platformHelper.securityIdentityPush();
                    }
                    //PM36303
                    while (e.hasNext()) {
                        page = (String) e.next();

                        if ((page.charAt(0) == '/'))
                            page = page.substring(1);

                        // begin 254491    [proxies BOTP] mis-handling of non-existent welcome-file's    WAS.webcontainer -- rewritten
                        RequestProcessor rp = _webapp.getRequestMapper().map(path.toString() + page);
                        if(rp == null){
                        	if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){
                            	logger.logp(Level.FINE, CLASS_NAME,"handleRequest", "No request processor found for welcome file:" + path.toString() + page);
                        	}
                        	continue;
                        }
                        else if (rp instanceof WebExtensionProcessor){
                        	boolean available = ((WebExtensionProcessor)rp).isAvailable(path.toString() + page);
                        	if(available == false){
                        		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){
                                	logger.logp(Level.FINE, CLASS_NAME,"handleRequest", "No web extension processor found for welcome file:" + path.toString() + page);
                            	}
                        		continue;
                        	}
                        }
                        else if (rp instanceof IServletWrapper){
                        	boolean available = ((IServletWrapper)rp).isAvailable();
                        	if(available == false){
                        		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){
                                	logger.logp(Level.FINE, CLASS_NAME,"handleRequest", "No servlet wrapper found for welcome file:" + path.toString() + page);
                            	}  
                        		continue;
                        	}
                        }

                        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){
                        	logger.logp(Level.FINE, CLASS_NAME,"handleRequest", "Process welcome file:" + path.toString() + page);
                    	} 
                        // end 254491    [proxies BOTP] mis-handling of non-existent welcome-file's    WAS.webcontainer -- rewritten
						if (redirectToWelcomeFile) {
							String qString = "";
							if ((req.getQueryString()) != null) { // if query string exists; add it.
								qString = "?" + req.getQueryString();
							}
							String rPath = removeLeadingSlashes(path.toString());
							if (rPath == null)
								rPath = "";
						//	BEGIN:PK15276
							String redirectURI=rPath + page + qString;
								req.setAttribute("com.ibm.ws.webcontainer.welcomefile.redirecturl", redirectURI);
                            welcomeFileRedirectUri = redirectURI;
                        	    
						}
						else {
                        	//begin 267395    SVT: can not get Admin Console on Win2000 -- m0514.18 build    WAS.webcontainer    
                            String uri = path.toString() + page;
                            //PK78371 - start
                        	if (WCCustomProperties.PROVIDE_QSTRING_TO_WELCOME_FILE){
                        		if ((req.getQueryString()) != null)
                        			uri = uri + "?" + req.getQueryString();
                        	}
                        	//PK78371 - end 
                        	req.setAttribute("com.ibm.ws.webcontainer.welcomefile.url", uri);
                        	welcomeFileForwardUri = uri;
                        }
                        //end 267395    SVT: can not get Admin Console on Win2000 -- m0514.18 build    WAS.webcontainer    
                        break; // Break out of the loop with the first match
                    }
                    //PM36303
                    if (platformHelper.isSyncToThreadPlatform() && ((WebApp)_webapp).getConfiguration().isSyncToThreadEnabled()){
                        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {
                            logger.logp(Level.FINE, CLASS_NAME,"handleRequest", "about to call securityIdentityPop() ");
                        }
                        platformHelper.securityIdentityPop(credToken);
                    }
                    //PM36303
                } 
                
                if (welcomeFileRedirectUri!=null){
                	 if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
              			logger.logp(Level.FINE,CLASS_NAME,"handleRequest", "sendRedirect to Welcome File:" + welcomeFileRedirectUri);
                 	    
                      resp.sendRedirect(resp.encodeRedirectURL(welcomeFileRedirectUri));
                      
                      if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
               			logger.exiting(CLASS_NAME,"handleRequest");
                   	return;
                      
                 } else if (welcomeFileForwardUri!=null){
                	 RequestDispatcher rd = context.getRequestDispatcher(welcomeFileForwardUri);
		                        
		                        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
               			logger.logp(Level.FINE,CLASS_NAME,"handleRequest", "forward :" + welcomeFileForwardUri);
		                        
		 		                rd.forward(req, resp);
                            
 					 if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
                			logger.exiting(CLASS_NAME,"handleRequest");
						return;
                    }
                //End PK64097

                if (_webapp.getConfiguration().isDirectoryBrowsingEnabled()){
					RequestDispatcher dirBrowse = context.getRequestDispatcher(WebApp.DIR_BROWSING_MAPPING);

					if (dirBrowse != null) {
						
						/*
						 * invoke the directory browsing servlet
						 */
						req.setAttribute("com.ibm.servlet.engine.webapp.dir.browsing.path", fileSystemPath);
						req.setAttribute("com.ibm.servlet.engine.webapp.dir.browsing.uri", req.getRequestURI());

						  
						if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
							logger.logp(Level.FINE, CLASS_NAME,"handleRequest","Directory Browse" + fileSystemPath);           		
						dirBrowse.forward(req, resp);
						if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
							logger.exiting(CLASS_NAME,"handleRequest");
						return;
					}
                }

                /*
				 * could not find a welcome file and dir browsing not on
				 */    
                // PK31377 - added if/else
                if (!resp.isCommitted()){  
                    resp.sendError(404, nls.getString("File.not.found", "File not found"));
       	   	  		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
                	logger.exiting(CLASS_NAME,"handleRequest","handleRequest ---> File not found");
                }else{		    	   
                    logger.exiting("handleRequest", "handleRequest");               	
            	}
                //PK31377 - end
                return;
            }

		// PK23475 code to check for invalid charcters in the request moved to new private method isRequestForbidden

            // String matchString = path.toString();

            // defect 220552: removed checks for URI resources ending with "/" for JSP resources: handled above.

            /**
            * Do not allow ".." to appear in a path as it allows one to serve files from anywhere within
            * the file system.
            * Also check to see if the path or URI ends with '/', '\', or '.' .
            * PQ44346 - Allow files on DFS to be served.
            */
            //if ((matchString.lastIndexOf("..") != -1 && (!matchString.startsWith("/...")))
            //    || matchString.endsWith("\\")
            //    || req.getRequestURI().endsWith(".")
            //    // PK22928
            //    || req.getRequestURI().endsWith("/")){
            //    //PK22928
		
		    // PK23475 call isRequestForbidden to check if request includes ".." etc.
            if (isRequestForbidden(path))   
            {
                resp.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    MessageFormat.format(nls.getString("File.not.found", "File not found: {0}"), new Object[] { pathInfo}));
                if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
                	logger.exiting(CLASS_NAME,"handleRequest","handleRequest --> Forbidden-endsWith");
                return;
            }

            // 94578, "Case Sensitive Security Matching bug":  On Windows and Netware only, filename of
            //         requested file must exactly match case or we will throw FNF exception.
            if (com.ibm.ws.util.FileSystem.isCaseInsensitive) {
            	File caseFile = new File(fileSystemPath);
                if (!caseCheck(caseFile, path.toString(),false,false)) {
                    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
                    	logger.logp(Level.FINE, CLASS_NAME,"handleRequest","handleRequest --> Case sensitivity check - throw FileNotFoundException");               	
                    throw new FileNotFoundException(path.toString());
                }
            }
            // end 94578

            // Create the FileServletWrapper to serve the file up.
            FileServletWrapper fwrapper = getStaticFileWrapper(_webapp, this, file);
			// 224858, must set pathElements for files mapped to staticFileServlet so getServletPath
			// is the same on the first request as on subsequent requests
        	//  begin 309663: should not be resetting path elements here (sitemesh issue since corrected).
			//dispatchContext.setPathElements(path.toString(),null);
        	//          end 309663
			// end 224858
            
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
            	logger.logp(Level.FINE, CLASS_NAME,"handleRequest","Use FileServletWrapper");
            
			fwrapper.handleRequest(req, resp);

			if (!_webapp.isCachingEnabled()){
			try
				{
					_webapp.addMappingTarget(path.toString(), fwrapper);
					// begin 309663
					// add to cache now versus wait until Webcontainer.handleRequest finds a IServletWrapper 
					// (allows request.getPathInfo and request.servletPath() to stay valid)
	                if (!dispatchContext.isInclude() && !dispatchContext.isForward()) {
	                    WebContainer.addToCache(req, fwrapper, this._webapp);
	                }
					// end 309663			
				}
				catch (Exception e)
				{
					// do nothing because its a race condition...won't happen again.
				}
			}
			
        }
        catch (FileNotFoundException e) 
		{
             // PK64302 Start
             if (!WCCustomProperties.THROW_404_IN_PREFERENCE_TO_403 && isDirectoryTraverse(path))   
             {
                 resp.sendError(
                     HttpServletResponse.SC_FORBIDDEN,
                     MessageFormat.format(nls.getString("File.not.found", "File not found: {0}"), new Object[] { pathInfo}));
                 if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable (Level.FINE))
                     logger.exiting(CLASS_NAME, "handleRequest", "Forbidden-file does not exist");
                 return;
             }
             // PK64302 End
        	// Begin PK27620
        	
        	WebAppErrorReport errorReport = new WebAppErrorReport(MessageFormat.format(nls.getString("File.not.found", "File not found: {0}"), new Object[] { e.getMessage()}), e);
			if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
        		logger.logp(Level.FINE,CLASS_NAME,"handleRequest","FileNotFoundException caught");
            
        	errorReport.setErrorCode(HttpServletResponse.SC_NOT_FOUND);
        	errorReport.setTargetServletName ("DefaultExtensionProcessor");
        	
       		// Note: In V8 by default WCCustomProperties.MODIFIED_FNF_BEHAVIOR is true.
            if(!WCCustomProperties.THROW_MISSING_JSP_EXCEPTION && !WCCustomProperties.MODIFIED_FNF_BEHAVIOR) { //PK65408
							
                com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(e, "com.ibm.ws.webcontainer.extension.DefaultExtensionProcessor.handleRequest", "573", this);
							try
							{
								_webapp.sendError(req, resp, errorReport);
							}
							catch (Exception ex)
							{
								// ignore
							
							}
							//PK65408 Start
					 }
					else {
						if (!WCCustomProperties.SERVLET_30_FNF_BEHAVIOR||request.getDispatcherType()!=DispatcherType.INCLUDE){
							if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
                				logger.logp(Level.FINE, CLASS_NAME,"handleRequest","dispatch type is not include, throw normal FNF");
                    
							fnf = e;
							resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
						}
						else {
//							Max: Must throw our own type of FNF to differentiate so we can support the following spec requirement
//							and not break backwards compatibility when the servlet itself throws FileNotFound or we're not in an include
//							Spec: If the default servlet is the target of a RequestDispatch.include() and the requested
//							resource does not exist, then the default servlet MUST throw
//							FileNotFoundException. If the exception isn't caught and handled, and the
//							response hasn’t been committed, the status code MUST be set to 500.

							if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
                				logger.logp(Level.FINE, CLASS_NAME,"handleRequest","throwing IncludeFileNotFoundException");
                    
							fnf = new IncludeFileNotFoundException(e.getMessage());
						}
						
					}
				
				
				// End PK27620
        }
	    catch (SecurityViolationException e){
	    	String strPath=null;
	    	if (path!=null){
	    		strPath=path.toString();
	    		collabHelper.processSecurityPreInvokeException (e, this, req, resp, dispatchContext, _webapp, strPath);
	    	}
	    	return;
	    }
        finally {
            //begin PK10057
        	if (platformHelper.isSyncToThreadPlatform() && ((WebApp)_webapp).getConfiguration().isSyncToThreadEnabled())
        		platformHelper.securityIdentityPop(cred);
        	
        	//The preInvoke in WebAppFilterManager calls with doAuth/enforceSecurity=true which will setup
//        	while (securityPreInvokes.size() != 0){ // may have null objects in list.
//        		secCollab.postInvoke(securityPreInvokes.pop());
//        	}
        	
        	//webAppNameSpaceCollab.postInvoke();
           //end PK10057
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
            	logger.logp(Level.FINE, CLASS_NAME,"handleRequest","handleRequest");
          //PK65408 Start
    	    if(fnf!=null){
    	    	throw fnf;
    	    }
    	    //PK65408 End
        }
        return;
    }

    private boolean checkFileExists(final File file) {
       	 boolean result;
		 if ( System.getSecurityManager() != null){
	         result = AccessController.doPrivileged(new PrivilegedAction<Boolean>()  {
	             public Boolean run() {
	                 return new Boolean(file.exists());
	             }
	         });
		 } else {
			 result = file.exists();
		 }
		 return result;
    }
    
    private boolean checkFileIsDirectory(final File file) {
      	 boolean result;
		 if ( System.getSecurityManager() != null){
	         result = AccessController.doPrivileged(new PrivilegedAction<Boolean>()  {
	             public Boolean run() {
	                 return new Boolean(file.isDirectory());
	             }
	         });
		 } else {
			 result = file.isDirectory();
		 }
		 return result;
   }
    
    
    // 
    // Run documentRoot processing as a priveleged action for Java@Security
    // 
    // 4 outcomes possible
    // - true : request was processed. not further work to do
    // - false : the request was for a directory so was not fully processed
    // - FileNotFoundException : the file could not be found or could not be accessed
    // - IOException : An error occurred searching trhe EDR
    // - Exception : An error occurred when creating a wrapper to process the file 
    //
    private boolean processEDR(final StringBuffer path, final HttpServletRequest req, final HttpServletResponse resp, final WebAppDispatcherContext dispatchContext) throws FileNotFoundException, IOException, Exception {
    	boolean result;
		 if ( System.getSecurityManager() != null){
		    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
			    logger.logp(Level.FINE, CLASS_NAME,"processEDR","Run _processEDR as a privileged action");
			 try {
	            result = AccessController.doPrivileged(new PrivilegedExceptionAction<Boolean>()  {
	                 public Boolean run() throws FileNotFoundException, IOException, Exception {
	                   	 return new Boolean(_processEDR(path,req,resp,dispatchContext));
	                 }
	              });
	         } catch (PrivilegedActionException pae) {
	 		     if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
				     logger.logp(Level.FINE, CLASS_NAME,"processEDR","PrivilgedActionException : " + pae.getMessage());
	 		     throw new FileNotFoundException(pae.getMessage()); 	        	
	         }

		 } else {
			 result = _processEDR(path,req,resp,dispatchContext);
		 }
		 return result;

    }
    
    private boolean _processEDR(StringBuffer path,HttpServletRequest req, HttpServletResponse resp, WebAppDispatcherContext dispatchContext) throws FileNotFoundException, IOException, Exception{
    	
    	if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
			logger.entering(CLASS_NAME,"_processEDR","path --> " + path);
    	
    	boolean requestProcessed = false;
    	
    	DocumentRootUtils docRoot = _webapp.getStaticDocumentRootUtils();   // PM21451
    	try {
    	    docRoot.handleDocumentRoots(path.toString());
    	} catch (FileNotFoundException fnfe) {
        	if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
    			logger.exiting(CLASS_NAME,"_processEDR","throw FileNotFoundException");
			throw new FileNotFoundException(MessageFormat.format(nls.getString("File.not.found","File not found: {0}"), new Object[]{path}));
    	}
    	if (docRoot.isDirectory()) {
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
            	logger.logp(Level.FINE,CLASS_NAME,"_processEDR","matched file is a directory");
	    	if (!docRoot.isMatchedFromEDR() || WCCustomProperties.ALLOW_PARTIAL_URL_TO_EDR ) {
	    		requestProcessed = false;
	    	} else {
	        	if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
	    			logger.exiting(CLASS_NAME,"_processEDR","throw FileNotFoundException");
				throw new FileNotFoundException(MessageFormat.format(nls.getString("File.not.found","File not found: {0}"), new Object[]{path}));
	    	}
    	} else {
    		
            if (isRequestForbidden(path))   
            {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, MessageFormat.format(nls.getString("File.not.found","File not found: {0}"), new Object[]{req.getPathInfo()}));
                if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
                	logger.exiting(CLASS_NAME,"_processEDR","Forbidden-Extended doc root endsWith");
                requestProcessed = true;
            } else {
    	
    	        ZipFileResource zipFileResource = docRoot.getMatchedZipFileResource();
    	        if (zipFileResource!=null)
    	        {            		
    		        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
    		         logger.logp(Level.FINE, CLASS_NAME,"_processEDR","zip file found: Use ZipFileServletWrapper");
    			
   		             handleZipFileWrapper(req, resp, path, dispatchContext,zipFileResource);
   		             requestProcessed = true;
    	        } else {
    		           		
    		        File matchedFile = docRoot.getMatchedFile();
    		            		
    		        if (matchedFile !=null) {
    		    	

        		        FileServletWrapper fwrapper = getStaticFileWrapper(_webapp, this, matchedFile);
				        fwrapper.handleRequest(req, resp);
				        if (!_webapp.isCachingEnabled()){
					        try
					        {
						        _webapp.addMappingTarget(path.toString(), fwrapper);
						        // begin 309663
						        // add to cache now versus wait until Webcontainer.handleRequest finds a IServletWrapper 
						        // (allows request.getPathInfo and request.servletPath() to stay valid)
                                if (!dispatchContext.isInclude() && !dispatchContext.isForward()) {
                                    WebContainer.addToCache(req, fwrapper, this._webapp);
                                }
						        // end 309663
					        }
					        catch (Exception e)
					        {
						       // do nothing because its a race condition...won't happen again.
					        }
				        } 
				        requestProcessed =  true;
    		        }    
				}
    		}
    	}
    	
    	if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
			logger.exiting(CLASS_NAME,"_processEDR","requestProcessed = " + requestProcessed);

    	return requestProcessed;
    }
    
	private void handleZipFileWrapper(HttpServletRequest req,
			HttpServletResponse resp, StringBuffer path,
			WebAppDispatcherContext dispatchContext,
			ZipFileResource metaInfResourceFile) throws Exception {
		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
        	logger.entering(CLASS_NAME, "handleZipFileWrapper");
		
		ZipFileServletWrapper zfwrapper = getZipFileWrapper(_webapp, this, metaInfResourceFile);
		zfwrapper.handleRequest(req, resp);
		if (!_webapp.isCachingEnabled()){
			try
			{
				_webapp.addMappingTarget(path.toString(), zfwrapper);
				if (!dispatchContext.isInclude() && !dispatchContext.isForward()) {
		            WebContainer.addToCache(req, zfwrapper, this._webapp);
		        }
			}
			catch (Exception e)
			{
				logger.logp(Level.WARNING, CLASS_NAME, "handleZipFileWrapper", "default.extension.exception.adding.mapping.target");
			}
		}
		
		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
        	logger.exiting(CLASS_NAME, "handleZipFileWrapper");
	}
	
	/**
	 * @param _webapp
	 * @param processor
	 * @param file
	 * @return
	 */
	protected abstract FileServletWrapper getStaticFileWrapper(IServletContext _webapp, DefaultExtensionProcessor processor, File file);
	/**
	 * @param _webapp
	 * @param processor
	 * @param file
	 * @param entry
	 * @return
	 */
	protected abstract ZipFileServletWrapper getZipFileWrapper(IServletContext _webapp, DefaultExtensionProcessor processor, ZipFileResource zipFileResource);

	// PQ79698 part2
	protected boolean isValidFilePath(String filePath) {
		   if(filePath == null) return false;
		   int len = filePath.length();
		   for (int i = 0; i < len; i++) {
			   if(filePath.charAt(i) < ' ') return false;
		   }
		   return true;
	}
	// PQ79698 part2 ends

	/*
	 * removeLeadingSlashes -- Removes all slashes from the head of the input String.
	 */
	public String removeLeadingSlashes(String src)
	{
		String result = null;
		int i = 0;
		boolean done = false;

		if (src == null)
			return null;

		int len = src.length();
		while ((!done) && (i < len))
		{
			if (src.charAt(i) == '/')
			{
				i++;
			}
			else
			{
				done = true;
			}
		}

		// If all slashes were stripped off and there was no remainder, then
		// return null.
		if (done)
		{
			result = src.substring(i);
		}

		return result;
	}

	protected StringBuffer getURLWithRequestURIEncoded(HttpServletRequest req)
	{
		StringBuffer url = new StringBuffer();
		String scheme = req.getScheme();
		int port = req.getServerPort();
		String urlPath = null;
		try
		{
			urlPath = new String(req.getRequestURI().getBytes("utf-8"), "iso-8859-1");
		}
		catch (UnsupportedEncodingException e)
		{
			urlPath = req.getRequestURI();
		}
		url.append(scheme);
		url.append("://");
		url.append(req.getServerName());
		if (scheme.equals("http") && port != 80 || scheme.equals("https") && port != 443)
		{
			url.append(':');
			url.append(req.getServerPort());
		}
		url.append(urlPath);
		return url;
	}

	/**
	 * @return
	 */
	public String getEsiControl()
	{
		return esiControl;
	}

	/**
	 * @return
	 */
	public int getDefaultBufferSize()
	{
		return defaultBufferSize;
	}
	
	//	 defect 220552: begin add ability to handle patternLists served or denied by DefaultExtensionProcessor
	public List getPatternList() {
		return patternList;
	}

	private List parseFileServingExtensions(String exts) {
		List list = new ArrayList();
		StringTokenizer st = new StringTokenizer(exts, ": ;");
		while (st.hasMoreTokens()) {
			String ext = st.nextToken();
			if (ext.equals("/")) {
				ext = "/*";
			}
			if (patternList.contains(ext) == false) {
				list.add(ext);
			}
		}
		return list;
	}
	
	public URIMatcher createURIMatcher (List list){
		URIMatcher uriMatcher = new URIMatcher();

		Iterator i = list.iterator();
		while (i.hasNext()){
			String currPattern = (String) i.next();
			if(currPattern.startsWith("*.")){
				try{
					uriMatcher.put( currPattern, currPattern + " _base pattern");
					uriMatcher.put( currPattern + "/", currPattern + " _base pattern 2");	//security check.
				}catch (Exception e){
					logger.logp(Level.SEVERE, CLASS_NAME,"createURIMatcher", "mapping.clash.occurred",new Object[]{currPattern});
					logger.throwing(CLASS_NAME, "createURIMatcher", e);
				}
			}
			else {
				try{
					uriMatcher.put( currPattern, currPattern + " _base pattern");
				}catch (Exception e){
					logger.logp(Level.SEVERE, CLASS_NAME,"createURIMatcher", "mapping.clash.occurred",new Object[]{currPattern});
					logger.throwing(CLASS_NAME, "createURIMatcher", e);
				}
			}
		}
		return uriMatcher;
	}
	//	 defect 220552: end add ability to handle patternLists served or denied by DefaultExtensionProcessor
	
	public boolean isAvailable(String resource) {
    	boolean available;
		 if ( System.getSecurityManager() != null){
			final String runResource = resource;
		    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
			    logger.logp(Level.FINE, CLASS_NAME,"isAvaialble","Run _isAvailable as a privileged action");
		    available = AccessController.doPrivileged(new PrivilegedAction<Boolean>()  {
	             public Boolean run() {
	                 return new Boolean(_isAvailable(runResource));
	             }
	        });
		 } else {
			 available = _isAvailable(resource);
		 }
		 return available;		
	}

	
	   // begin 254491    [proxies BOTP] mis-handling of non-existent welcome-file's    WAS.webcontainer
	private boolean _isAvailable (String resource){
		boolean available = false;
		File caseFile = new File(_webapp.getRealPath(resource,"DEP"));  // Pm21451
		if (caseFile.exists()) {
			available = _caseCheck(caseFile,resource,true,true);
        }
	    if (!available)
	    	available = _isAvailableInDocumentRoot(resource,WCCustomProperties.SERVE_WELCOME_FILE_FROM_EDR);			
		
        return available;
	}
    // end 254491    [proxies BOTP] mis-handling of non-existent welcome-file's    WAS.webcontainer
	
	private boolean caseCheck (final File caseFile,final String resource, final boolean checkWEBINF, final boolean checkMETAINF) {
    	boolean result;
		 if ( System.getSecurityManager() != null){
		    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
			    logger.logp(Level.FINE, CLASS_NAME,"processEDR","Run _caseCheck as a privileged action");
	        result = AccessController.doPrivileged(new PrivilegedAction<Boolean>()  {
	             public Boolean run() {
	                 return new Boolean(_caseCheck(caseFile,resource,checkWEBINF,checkMETAINF));
	             }
	        });
		 } else {
			 result = _caseCheck(caseFile,resource,checkWEBINF,checkMETAINF);
		 }
		 return result;		
	}
	
	private boolean _caseCheck (File caseFile, String resource, boolean checkWEBINF, boolean checkMETAINF){
		boolean available=true;
		if (com.ibm.ws.util.FileSystem.isCaseInsensitive) {
			try{
				available = com.ibm.wsspi.webcontainer.util.FileSystem.uriCaseCheck(caseFile,resource,false,false);
			}catch (IOException io){
				available = false;
			}
        }
		return available;
	}
	
	private boolean _isAvailableInDocumentRoot(String resource,boolean searchEDR) {
		boolean available = false;
		try { 
	    	if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable (Level.FINE))
	    		logger.logp(Level.FINE, CLASS_NAME,"isAvailable()","File not found in WAR directorr so check DocumetRoots");
            DocumentRootUtils docRoot = _webapp.getStaticDocumentRootUtils();
            docRoot.handleDocumentRoots(resource,true,!WCCustomProperties.SKIP_META_INF_RESOURCES_PROCESSING,searchEDR);
            String filePath = docRoot.getFilePath();
            if (filePath!=null) {
		    	if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable (Level.FINE))
		    		logger.logp(Level.FINE, CLASS_NAME,"isAvailable()","Match found in DocumetRootd");
		    	File caseFile = new File(filePath);
                available = caseFile.exists();
            }  
		} catch (FileNotFoundException fne) {
	    	if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable (Level.FINE))
	    		logger.logp(Level.FINE, CLASS_NAME,"isAvailable()","FileNotFoundException caught");
        	/* ignore */
        } catch (IOException ioe) {
        	if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable (Level.FINE))
        		logger.logp(Level.FINE, CLASS_NAME,"isAvailable()","IOException caught");
        	/* ignore */
        }                
        return available;
	}

	//271581    DefaultExtensionProcessor update    WASCC.web.webcontainer    
	/* (non-Javadoc)
	 * @see com.ibm.ws.webcontainer.extension.WebExtensionProcessor#createServletWrapper(com.ibm.ws.webcontainer.servlet.ServletConfig)
	 */
	public IServletWrapper createServletWrapper(IServletConfig config) throws Exception
	{
		String filename = _webapp.getRealPath(config.getFileName());
		if(filename != null){
			File wrapperedFile = new File (filename);
			if(wrapperedFile.exists()){
				return this.getStaticFileWrapper(_webapp, this, wrapperedFile);
			}
		}
		return null;
		
	}
	//271581    DefaultExtensionProcessor update    WASCC.web.webcontainer
	
    
    
	public WebComponentMetaData getMetaData() {
		return cmd;
	}

               //	PK23475 - Method added using code form doGet() so that it can be called from 2 places
	//  returns true if request is forbidden because it contains ".." etc.
	private boolean isRequestForbidden(StringBuffer path)
	{
			
		boolean requestIsForbidden = false;
			
		String matchString = path.toString();
			
		/**
            * Do not allow ".." to appear in a path as it allows one to serve files from anywhere within
            * the file system.
            * Also check to see if the path or URI ends with '/', '\', or '.' .
            * PQ44346 - Allow files on DFS to be served.
                */
		if ((matchString.lastIndexOf("..") != -1 && (!matchString.startsWith("/...")))
				|| matchString.endsWith("\\")
			// PK23475 use matchString instead of RequestURI because RequestURI is not decoded
			//	|| req.getRequestURI().endsWith(".")
				|| matchString.endsWith(".")
			// PK22928
			//	|| req.getRequestURI().endsWith("/")){
				|| matchString.endsWith("/"))
			//PK22928
		{
			requestIsForbidden = true;
		}
		
		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
			logger.logp(Level.FINE, CLASS_NAME,"isRequestForbidden","returning :" + requestIsForbidden + ", matchstring :" + matchString);
			
		return requestIsForbidden;
	}
	
	// 542155 Add isDirectoryTraverse method - reduced version of isRequestForbidden
	private boolean isDirectoryTraverse(StringBuffer path)
	{
			
		boolean directoryTraverse = false;
			
		String matchString = path.toString();
			
		/**
            * Do not allow ".." to appear in a path as it allows one to serve files from anywhere within
            * the file system.
            * Also check to see if the path or URI ends with '/', '\', or '.' .
            * PQ44346 - Allow files on DFS to be served.
                */
		if ( (matchString.lastIndexOf("..") != -1) && (!matchString.startsWith("/...") )) 
		{
			directoryTraverse = true;
		}
		
		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
			logger.logp(Level.FINE, CLASS_NAME,"isDirectoryTraverse", "returning" + directoryTraverse + " , matchstring :" + matchString);
			
		return directoryTraverse;
	}

	public void nameSpacePostInvoke () {
		this.webAppNameSpaceCollab.postInvoke();
	}
	
	public void nameSpacePreInvoke () {
		this.webAppNameSpaceCollab.preInvoke (getMetaData());
	}
	
	public int getOptimizeFileServingSize() {
		return optimizeFileServingSize;
	}


	public void destroy() {
		// TODO Auto-generated method stub
		
	}


	public ServletConfig getServletConfig() {
		// TODO Auto-generated method stub
		return null;
	}


	public String getServletInfo() {
		// TODO Auto-generated method stub
		return null;
	}


	public void init(ServletConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		
	}


	public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
		try {
			this.handleRequest(request, response);
		}
		catch (Exception e){
			throw new ServletException(e);
		}
	}

    public IServletWrapper getServletWrapper(ServletRequest request, ServletResponse response){
       return null;
    }

}
