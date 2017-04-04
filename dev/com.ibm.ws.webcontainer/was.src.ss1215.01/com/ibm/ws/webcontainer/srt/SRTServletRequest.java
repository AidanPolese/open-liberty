// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2008
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
//  CHANGE HISTORY
//Defect        Date        Modified By         Description
//--------------------------------------------------------------------------------------
//268366                                        PERF: 3% regression in PingServlet, create paramStack only one since parameters are popped off after usage
//269932                                        WebAppRequestDispatcher cleanup changes
//293696        07/27/05    mmolden             ServletRequest.getPathInfo() fails
//PK09940       08/11/05    todkap              HTTP CLIENT CLASS SENDING REQUESTS WITH AN EMPTY HOST HEADER
//309663.2      10/10/05    todkap              309663 pathInfo problem exists for InvokerExtensionProcessor
//321485        11/07/05    ekoonce             Improve trace
//306736.4      11/28/05    todkap              handle duplicate JSESSIONID in the request    WASCC.web.webcontainer    
//PK17095       01/04/06    todkap              WEBAPPDISPATCHERCONTEXT CORRUPTION DUE TO MULTITHREAD USAGE OF    WAS.webcontainer
//306998.15     01/09/06    ekoonce             PERF: WAS tracing performance improvement
//PK18815       02/21/06    todkap              NULLPOINTEREXCEPTION WITH CLONED REQUEST AND STORED RESPONSE    WAS.webcontainer
//LIDB4408-1    02/22/06    todkap              LIDB4408-1 web container changes to limit pooling
//346539        02/10/06    mmolden             61FVT:Access Control Exception in SRTServletRequest
//348603.1      03/03/06    todkap              Changes in security SPI affect webcontainer    WASCC.web.webcontainer
//353142        03/07/06    todkap              CodeReview: discarding of pooled objects and WCCRequest channel    WASCC.web.webcontainer
//PK22688       04/18/06    cjhoward            getRequestURL does not return the jsessionid along with the url.
//262147        04/25/06    mmolden             POST PARAMETERS NOT PRESERVED IN FORM LOGION    WAS.security
//382943        08/09/06    todkap              remove SUN dependencies from core webcontainer
//PK28078       08/14/06    ekoonce             getPathInfo returns empty string when url rewriting is enabled
//LIDB4395-1    10/13/06    dettlaff            added get/setSessionAffinityContext
//LIDB4317-1    10/17/06    mmolden             Servlet 2.5 changes
//454430        08/19/07    mmolden             Clear obj refs from pooled objs before returning back to pool
// 461383       09/28/07    mmolden             70FVT: Async should still work when ARD is disabled  
//506430        03/19/08    mmolden             UTF-8 encoding issues with admin console
//PK57679       04/14/08    mmolden (mmulholl)  add support for getInputStreamData() and setInputStreamData()
//519995        05/14/08    mmolden             SRTServletRequest getCharacterEncoding issues    
//531038        06/19/08    mmolden             PERF: Reduce duplicate method calls in Web Container classes
//516233        07/09/08    jebergma (mmulholl) update PK57679 to allow for empty post dat
//PK80362       03/19/09    anupag          	Suppress headers provided in the custom property list from the request to the application
//F1179-17167.9 11/03/09    utle          	Add removePrivateAttribute() method
//PM35450       04/25/11    anupag              Provide an option to allow query parameter with no "="
//724365        12/12/11    anupag              PM53950, Add limit to multipart-parameters
//724365.2      2/2/12      anupag              PM53930, Add limit.
//PM58495       02/27/12    anupag              add limit on duplicate hash parameters , 728397
//PM57418      	02/28/12    anupag          	Translate messages added by PM53930 and PM58495 (724365.4)
//


package com.ibm.ws.webcontainer.srt;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.cert.X509Certificate;
import java.text.MessageFormat; //724365.4
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet; // 728397
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncListener;
import javax.servlet.DispatcherType;
import javax.servlet.MultipartConfigElement;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.apache.commons.fileupload.FileUploadBase.FileSizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.ibm.ejs.ras.TraceNLS;
import com.ibm.servlet.util.EmptyEnumeration;
import com.ibm.websphere.servlet.request.IRequest;
import com.ibm.websphere.servlet.response.IResponse;
import com.ibm.ws.util.IDGeneratorImpl;
import com.ibm.ws.webcontainer.async.AsyncContextFactory;
import com.ibm.ws.webcontainer.async.AsyncIllegalStateException;
import com.ibm.ws.webcontainer.async.AsyncListenerEntry;
import com.ibm.ws.webcontainer.servlet.RequestUtils;
import com.ibm.ws.webcontainer.servlet.ServletConfig;
import com.ibm.ws.webcontainer.session.SessionManagerConfigBase;
import com.ibm.ws.webcontainer.srt.http.HttpInputStream;
import com.ibm.ws.webcontainer.util.UnsynchronizedStack;
import com.ibm.ws.webcontainer.webapp.WebApp;
import com.ibm.ws.webcontainer.webapp.WebAppConfiguration;
import com.ibm.ws.webcontainer.webapp.WebAppDispatcherContext;
import com.ibm.ws.webcontainer.webapp.WebGroup;
import com.ibm.wsspi.webcontainer.IPoolable;
import com.ibm.wsspi.webcontainer.WCCustomProperties;
import com.ibm.wsspi.webcontainer.WebContainer;
import com.ibm.wsspi.webcontainer.WebContainerConstants;
import com.ibm.wsspi.webcontainer.WebContainerRequestState;
import com.ibm.wsspi.webcontainer.logging.LoggerFactory;
import com.ibm.wsspi.webcontainer.servlet.IExtendedRequest;
import com.ibm.wsspi.webcontainer.servlet.IExtendedResponse;
import com.ibm.wsspi.webcontainer.servlet.IServletConfig;
import com.ibm.wsspi.webcontainer.servlet.IServletRequest;
import com.ibm.wsspi.webcontainer.util.EncodingUtils;
import com.ibm.wsspi.webcontainer.util.IInputStreamObserver;
import com.ibm.wsspi.webcontainer.util.WSServletInputStream;
import com.ibm.wsspi.webcontainer.webapp.IWebAppDispatcherContext;

@SuppressWarnings("unchecked")
public class SRTServletRequest implements HttpServletRequest, IExtendedRequest, IServletRequest, IPrivateRequestAttributes, IInputStreamObserver
{
    //Class level objects
    //=========================
protected static Logger logger = LoggerFactory.getInstance().getLogger("com.ibm.ws.webcontainer.srt");
	private static final String CLASS_NAME="com.ibm.ws.webcontainer.srt.SRTServletRequest";
    protected static TraceNLS nls = TraceNLS.getTraceNLS(SRTServletRequest.class, "com.ibm.ws.webcontainer.resources.Messages");
	private static String CLIENT_ENCODING_OVERRIDE;
	private static String DEFAULT_CLIENT_ENCODING;
	private static String suppressHeadersInRequest = null;  //PK80362
    private static ArrayList suppressheadersList = null; //PK80362
	static {
		// 115780 - begin - get static references to the default and override encodings
		CLIENT_ENCODING_OVERRIDE = System.getProperty("client.encoding.override");
		DEFAULT_CLIENT_ENCODING = System.getProperty("default.client.encoding");
		// 115780 - end
		//PK80362 Start
		suppressHeadersInRequest = WCCustomProperties.SUPPRESS_HEADERS_IN_REQUEST;
		if (suppressHeadersInRequest != null){
	    	String delimiter = ",";
	    	suppressheadersList = new ArrayList();
	    	StringTokenizer st = new StringTokenizer(suppressHeadersInRequest, delimiter);
	        while(st.hasMoreTokens()) {
	        	suppressheadersList.add(st.nextToken().trim());
	        }
    	}//PK80362 End 
	}
	private static final String JAVAX_NET_SSL_PEER_CERTS    = "javax.net.ssl.peer_certificates";
    private static final String JAVAX_NET_SSL_CIPHER_SUITE  = "javax.net.ssl.cipher_suite";
    private static final String JAVAX_SERVLET_REQUEST_X509CERTIFICATE = "javax.servlet.request.X509Certificate";
    
    private static final String DIRECT_CONNECTION_PEER_CERTS  = "com.ibm.websphere.ssl.direct_connection_peer_certificates";
    private static final String DIRECT_CONNECTION_CIPHER_SUITE = "com.ibm.websphere.ssl.direct_connection_cipher_suite";
    private static final String IS_DIRECT_CONNECTION = "com.ibm.websphere.webcontainer.is_direct_connection";                           //F001872

    
    private static final String INPUT_STREAM_CONTENT_TYPE = "ContentType";  // PK57679
    private static final String INPUT_STREAM_CONTENT_DATA = "ContentData";  // PK57679 
   
    private static final String INPUT_STREAM_CONTENT_DATA_LENGTH = "ContentDataLength"; //516233
    //=========================
    
    //Objects requiring Cloning
    //=========================
    //  268366, PERF: 3% regression in PingServlet, create paramStack only one since parameters are popped off after usage
    private UnsynchronizedStack _paramStack = new UnsynchronizedStack(); 
	// Created once in Constructor of SRTServletRequest object and not recreated for each request.
    private SRTConnectionContext _connContext;
    private SRTRequestContext _requestContext = null;    
    // set prior to handle the current dispatch to resource
    private WebAppDispatcherContext _dispatchContext = null;
    private SRTServletRequestHelper _srtRequestHelper;  // keep all per request objects in this helper class since SRTServletRequest objects are reused.
    //=========================

    //Objects not requiring Cloning
    //=========================
    // reset on each call to initForNextRequest 
    protected IRequest _request = null;

    //protected HttpInputStream _in = new HttpInputStream();
    protected WSServletInputStream _in;
    private boolean _runningCollaborators = false; // PK01801
    private String _setInputStreamContentType; // PK57679
    private int _setInputStreamContentLength;  // PK57679
    private boolean _setInputDataStreamCalled; // 5166233
    //=========================

    //WARNING! This custom property has not been officially exposed in an APAR
    //This was added as a way to revert back to pre-Servlet 2.5 changes.
    //If level 2 wishes to devulge this info, level 3 should be informed. Thanks!
    private static final boolean enableSetCharacterEncodingAfterGetReader = (Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("enablesetcharacterencodingaftergetreader"))).booleanValue();
    
    //PM03928
    private static boolean disableSetCharacterEncodingAfterParametersRead = WCCustomProperties.DISABLE_SET_CHARACTER_ENCODING_AFTER_PARAMETERS_READ;

    private static boolean allowQueryParamWithNoEqual = WCCustomProperties.ALLOW_QUERY_PARAM_WITH_NO_EQUAL;       //PM35450
    private static int maxParamPerRequest = WCCustomProperties.MAX_PARAM_PER_REQUEST; //724365
    private static boolean logMultiPartExceptionsOnParseParameter = WCCustomProperties.LOG_MULTIPART_EXCEPTIONS_ON_PARSEPARAMETER; //724365.2
    private static final int maxDuplicateHashKeyParams = WCCustomProperties.MAX_DUPLICATE_HASHKEY_PARAMS; // PM58495 (728397)
    		
    
	public SRTServletRequest(SRTConnectionContext context)
	{
        this._connContext = context;
        this._requestContext = new SRTRequestContext(this);
        this._in = createInputStream();
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"SRTServletRequest", "this->"+this+": " + "inputStream is of type --> " + this._in);
        }
	}
    
    public Object getPrivateAttribute(String name) {
        //321485
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"getPrivateAttribute", "this->"+this+": "+" name --> " + name);
        }
        
        if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        
    	if (_srtRequestHelper._privateAttributes == null)
    		return null;
        return _srtRequestHelper._privateAttributes.get(name);
    }

    private void checkRequestObjectInUse() {
    	WebContainerRequestState reqState = WebContainerRequestState.getInstance(true);
    	IExtendedRequest curThreadIExtendedReq = reqState.getCurrentThreadsIExtendedRequest();
		if (curThreadIExtendedReq!=null&&curThreadIExtendedReq!=this){
            
			try
			{
			    throw new IllegalStateException("Wrong request object in use on Thread");
			} catch (IllegalStateException e) {
				//No need to NLS these since they're only printed out for IBM service use when a custom property is on.
				logger.logp(Level.SEVERE,CLASS_NAME,"checkRequestObjectInUse","ERROR: Wrong request object in use on Thread. Object Expected: "
      		      +curThreadIExtendedReq+",  Found: "+this);
				logger.logp(Level.SEVERE,CLASS_NAME,"checkRequestObjectInUse","Wrong request object in use on Thread. \n", e); 
		    }    
		}
        
	}

	public Enumeration getPrivateAttributeNames() {
        //321485
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"getPrivateAttributeNames", "this->"+this+": ");
        }
        
        if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        
    	if (_srtRequestHelper._privateAttributes == null)
    		return EmptyEnumeration.instance();
        return _srtRequestHelper._privateAttributes.keys();
    }

    public void setPrivateAttribute(String name, Object value) {
        //321485
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"setPrivateAttribute", "this->"+this+": "+" name --> " + name + " value --> " + value.toString());
        }
        
        if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        
    	if (_srtRequestHelper._privateAttributes == null)
    		_srtRequestHelper._privateAttributes = new Hashtable();
    	_srtRequestHelper._privateAttributes.put(name, value);
    }

    public void removePrivateAttribute(String name) {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  
            logger.logp(Level.FINE, CLASS_NAME,"removePrivateAttribute", "this->"+this+": "+" name --> " + name);
        }
        
        if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        
    	if (_srtRequestHelper._privateAttributes != null)
    	    _srtRequestHelper._privateAttributes.remove(name);
    }

    /**
     * @param conn com.ibm.servlet.engine.srp.ISRPConnection
     */
    public void initForNextRequest(IRequest req) {
        //321485
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"initForNextRequest", "this->"+this+": ");
        }
        
        if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        
        try {

            if (req == null) {
                _in.init(null);
                return;
            }

            _setInputStreamContentType = null;  // PK57679
            _setInputStreamContentLength = -1;  // PK57679
            _setInputDataStreamCalled = false;  // 516233

            this._request = req;
            _srtRequestHelper = new SRTServletRequestHelper();
            _in.init(_request.getInputStream());
            // begin 280584.1    SVT: StackOverflowError when installing app larger than 2GB    WAS.webcontainer    
            if( this.getContentLength() > 0 ){            
            	_in.setContentLength(this.getContentLength());
            }
            //  end 280584.1    SVT: StackOverflowError when installing app larger than 2GB    WAS.webcontainer
            //F00349 Start - register with InputStream so that alertOPen and alertClosed
            // are notified when the InputStream is opened for re-read and closed.
            if (WCCustomProperties.ENABLE_MULTI_READ_OF_POST_DATA) {
                _in.setObserver(this);
            }
        } catch (IOException e) {
            //shouldn't happen.
            com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(
                e,
                "com.ibm.ws.webcontainer.srt.SRTServletRequest.initForNextRequest",
                "828",
                this);
            //logger.logp(Level.SEVERE, CLASS_NAME,"initForNextRequest", "Error.Initializing.for.Next.Request", e);
        }

    }
    
    /* (non-Javadoc)
     * @see javax.servlet.ServletRequest#getAttribute(java.lang.String)
     */
    public Object getAttribute(String arg0) {
        //321485
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"getAttribute", "this->"+this+": "+" name --> " + arg0);
        }
        
        if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        
    	Object obj = _srtRequestHelper._attributes.get(arg0);
        if (obj == null) {
            if (arg0.equalsIgnoreCase(JAVAX_NET_SSL_PEER_CERTS) ||
                arg0.equalsIgnoreCase(JAVAX_SERVLET_REQUEST_X509CERTIFICATE)) {
                obj = getPeerCertificates();
            } else if (arg0.equalsIgnoreCase(JAVAX_NET_SSL_CIPHER_SUITE)) {
                obj = getCipherSuite();
            } else if (arg0.equalsIgnoreCase(DIRECT_CONNECTION_PEER_CERTS)){
            	obj = getDirectConnectionPeerCertificates();
            } else if (arg0.equalsIgnoreCase(DIRECT_CONNECTION_CIPHER_SUITE)){
            	obj = getDirectConnectionCipherSuite();
            } else if (arg0.equalsIgnoreCase(IS_DIRECT_CONNECTION)){
                obj = isDirectConnection();
            } else if (arg0.equals(WebContainerConstants.JAVAX_SERVLET_REQUEST_SSL_SESSION_ID)){
            	obj = this.getSSLSessionId();
            }
            
    	}
        return obj;
    }



	public Enumeration getAttributeNames() {
        //321485
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"getAttributeNames", "this->"+this+": ");
        }
        
        if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }

        //PK81452- start
        if (WCCustomProperties.COPY_ATTRIBUTES_KEY_SET){
        	return new Enumeration(){
        		private java.util.Iterator iter = new ArrayList(_srtRequestHelper._attributes.keySet()).iterator();
        		public boolean hasMoreElements() {
                    return iter.hasNext();
                }

                public Object nextElement() {
            		return iter.next();
                }
        	};
        }
        else{
	        return new Enumeration() {
	            private java.util.Iterator iter = _srtRequestHelper._attributes.keySet().iterator();
	            public boolean hasMoreElements() {
	                return iter.hasNext();
	            }
	
	            public Object nextElement() {
	        		return iter.next();
	            }
	        };
        }
    }
    
    /* (non-Javadoc)
     * @see javax.servlet.ServletRequest#setCharacterEncoding(java.lang.String)
     */
    public void setCharacterEncoding(String arg0)
        throws UnsupportedEncodingException {
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
    	
    	//321485
    	if (!enableSetCharacterEncodingAfterGetReader){
	    	if (_srtRequestHelper._gotReader){//Servlet 2.5
	    		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {
	                logger.logp(Level.FINE, CLASS_NAME,"setCharacterEncoding", "this->"+this+": "+" call ignored, already got reader");
	            }
	    		return;
	    	}
    	}
    	
        //PM03928
        if (disableSetCharacterEncodingAfterParametersRead && _srtRequestHelper._parametersRead){
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {
                logger.logp(Level.FINE, CLASS_NAME,"setCharacterEncoding", "this->"+this+": "+" name --> " + arg0 + " is ignored, already parsed data");
            }
            return;
        }
        //PM03928

    	boolean isSupported = EncodingUtils.isCharsetSupported(arg0);
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"setCharacterEncoding", "this->"+this+": "+" name --> " + arg0 + " isSupported --> " + String.valueOf(isSupported));
        }
    	if(isSupported){
    		_srtRequestHelper._characterEncoding = arg0;
    	}
    	else{
    		String msg = nls.getFormattedMessage("unsupported.request.encoding.[{0}]", new Object[] { arg0}, "Unsupported encoding specified --> "+ arg0);
    		throw new UnsupportedEncodingException (msg);
    	}

    }

	//PK80362 Start
    // Check if the header name needs to be suppressed provided in the header name list in custom property.
    // Returns true if header can be suppressed, or false if not.
    private boolean isHeaderinSuppressedHeadersList(String headername)
    {
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
    	
		boolean suppressHeader = false;		
		if(headername != null){
			Iterator itList = suppressheadersList.iterator();
			while (itList.hasNext() && !(suppressHeader)) {
				String s = (String)itList.next();       		  		
				if (headername.startsWith(s)) {    	
					suppressHeader= true;
					if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable (Level.FINE))   
						logger.logp(Level.FINE, CLASS_NAME, "isHeaderinSuppressedHeadersList", " suppressHeadersInRequest is set and headername --> "+ headername +" begins with --> " + s);
				}    		
			}
		}		      	      		
    	return suppressHeader;
	}//PK80362 End  

    /**
     * Returns the value of a date header field, or -1 if not found.
     * The case of the header field name is ignored.
     * @param name the case-insensitive header field name
     */
    public long getDateHeader(String name) {
    	
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        //321485
        //PK80362 Start
        // If the getDateHeader method cannot translate the header to a Date object, an IllegalArgumentException is thrown
        //long header = _request.getDateHeader(name);
        long header = -1;
        if ( (suppressHeadersInRequest == null) || !(isHeaderinSuppressedHeadersList(name))){       				
        	header = _request.getDateHeader(name); 
        }//PK80362 End
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"getDateHeader", "this->"+this+": "+" name --> " + name + " header --> " + String.valueOf(header));
        }
        return header;
    }

    /**
     * Returns the value of a header field, or null if not known.
     * The case of the header field name is ignored.
     * @param name the case-insensitive header field name
     */
    public String getHeader(String name) {
    	
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        //321485
        //PK80362 Start
        //String header = _request.getHeader(name);    
        String header = null;
        if ( (suppressHeadersInRequest == null) ||  !(isHeaderinSuppressedHeadersList(name))){       				
        	header = _request.getHeader(name); 
        }//PK80362 End 
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"getHeader", "this->"+this+": "+" name --> " + name + " header --> " + header);
        }
        return header;
    }

    /**
     * 108037 - method added
     * Returns the value of a header field, or null if not known.
     * This method gets the header by going directly to the original
     * request object rather than init'ing the local headers.  Created
     * for security performance
     *
     * @param name the case-sensitive header field name
     */
    public String getHeaderDirect(String name)
    {
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        //321485
        //PK80362 Start
        //String header = _request.getHeader(name);   
        String header = null;
        if ( (suppressHeadersInRequest == null) ||  !(isHeaderinSuppressedHeadersList(name))){       				
        	header = _request.getHeader(name); 
        }//PK80362 End 
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"getHeaderDirect", "this->"+this+": "+" name --> " + name + " header --> " + header);
        }
        return header;
    }
    
    /**
         * Returns an enumeration of strings representing the header names
         * for this request. Some server implementations do not allow headers
         * to be accessed in this way, in which case this method will return null.
         */
    public Enumeration getHeaderNames() {
        //321485
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"getHeaderNames", "this->"+this+": ");
        }
        
        if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        
        //PK80362 Start              
        if( suppressHeadersInRequest == null){        	            
        return _request.getHeaderNames();
    }
        else {// step through the existing enumeration and create a new one without header names listed in custom property     	
        	ArrayList alHeaderNames = new ArrayList();
        	Enumeration enumHeaderNames = _request.getHeaderNames();         	
     		while( enumHeaderNames.hasMoreElements() ){
	     			String headerNameParam = (String)enumHeaderNames.nextElement();
	     			if( !(isHeaderinSuppressedHeadersList(headerNameParam)))	     				     				     				
	     				alHeaderNames.add(headerNameParam);	     			
     		}
	     	return Collections.enumeration(alHeaderNames);        	//create new Enumeration
        }                     
        //return _request.getHeaderNames();
		//PK80362 End  
    }
    /**
     * Returns the value of an integer header field, or -1 if not found.
     * The case of the header field name is ignored.
     * @param name the case-insensitive header field name
     */
    public int getIntHeader(String name) {
    	
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
    	
        //321485
        //PK80362 Start
        //int header = _request.getIntHeader(name);  
        int header = -1;
        if ( (suppressHeadersInRequest == null) ||  !(isHeaderinSuppressedHeadersList(name))){       				
        	header = _request.getIntHeader(name); 
        }
        //PK80362 End
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"getIntHeader", "this->"+this+": "+" name --> " + name + " header --> " + String.valueOf(header));
        }
        return header;
    }

    public Enumeration getHeaders(String name) {
        //321485
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"getHeaders", "this->"+this+": "+" name --> " + name);
        }
        
        if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        
        //PK80362 Start
        Enumeration header = null;
        if ( (suppressHeadersInRequest == null) ||  !(isHeaderinSuppressedHeadersList(name))){       				
        	header = _request.getHeaders(name); 
        }             
        //return (_request.getHeaders(name));
		return header;
		//PK80362 End   
    }

    public String getMethod() {
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        //321485
    	if (_srtRequestHelper._method ==null)
    		_srtRequestHelper._method = _request.getMethod();
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"getMethod", "this->"+this+": "+" method --> " + _srtRequestHelper._method);
        }
        return _srtRequestHelper._method;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRequest#getContentLength()
     */
    public int getContentLength() {
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        //321485
        // PK57679 Start
        int contentLength;
        if (!_setInputDataStreamCalled)   // 516233
            contentLength = this._request.getContentLength();
   	    else 
   	    	contentLength = _setInputStreamContentLength;
   	    // PK57679 End
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"getContentLength", "this->"+this+": "+" length --> " + String.valueOf(contentLength));
        }
        return contentLength;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRequest#getContentType()
     */
    public String getContentType() {
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        //321485
        // PK57679 Start
        String contentType;
        if (!_setInputDataStreamCalled)   // 516233
            contentType = _request.getContentType();
   	    else 
   	    	contentType =  _setInputStreamContentType;
        //	PK57679 End
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"getContentType", "this->"+this+": "+" type --> " + contentType);
        }
        return contentType;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRequest#getProtocol()
     */
    public String getProtocol() {
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        //321485
        String protocol = this._request.getProtocol();
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"getProtocol", "this->"+this+": "+" protocol --> " + protocol);
        }
        return protocol;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRequest#getScheme()
     */
    public String getScheme() {
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        //321485
        String scheme = this._request.getScheme();
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"getScheme", "this->"+this+": "+" scheme --> " + scheme);
        }
        return scheme;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRequest#getServerPort()
     */
    public int getServerPort() {
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        //321485
        int port = this._request.getServerPort();
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"getServerPort", "this->"+this+": "+" port --> " + String.valueOf(port));
        }
        return port;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRequest#getRemoteAddr()
     */
    public String getRemoteAddr() {
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        //321485
        String addr = this._request.getRemoteAddr();
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"getRemoteAddr", "this->"+this+": "+" address --> " + addr);
        }
        return addr;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRequest#getRemoteHost()
     */
    public String getRemoteHost() {
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        //321485
        String host = this._request.getRemoteHost();
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"getRemoteHost", "this->"+this+": "+" host --> " + host);
        }
        return host;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRequest#setAttribute(java.lang.String, java.lang.Object)
     */
    public void setAttribute(String arg0, Object arg1) {
        //321485
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"setAttribute", "this->"+this+": "+" name --> " + arg0 + " value --> " + (arg1!=null?arg1.toString():""));
        }
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
    	Object oldValue = _srtRequestHelper._attributes.put(arg0, arg1);

    	if (oldValue != null) {
            this.attributeReplaced(arg0, oldValue);
        } else {
            this.attributeAdded(arg0, arg1);
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRequest#removeAttribute(java.lang.String)
     */
    public void removeAttribute(String arg0) {
        //321485
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"removeAttribute", "this->"+this+": "+" name --> " + arg0);
        }
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        Object oldValue=_srtRequestHelper._attributes.remove(arg0);
   		if (oldValue != null) {
            this.attributeRemoved(arg0, oldValue);
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRequest#getLocale()
     */
    public Locale getLocale() {
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        processLocales();
        //321485
        Locale locale = (Locale) (_srtRequestHelper._locales.size() > 0 ? _srtRequestHelper._locales.get(0) : null);
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"getLocale", "this->"+this+": "+" locale --> " + (locale!=null?locale.toString():""));
        }
        return locale;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRequest#getLocales()
     */
    public Enumeration getLocales() {
        //321485
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"getLocales", "this->"+this+": ");
        }
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        processLocales();
        return new Enumeration() {
            java.util.Iterator iter = _srtRequestHelper._locales.iterator();
            public boolean hasMoreElements() {
                return iter.hasNext();
            }

            public Object nextElement() {
                return iter.next();
            }
        };

    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRequest#isSecure()
     */
    public boolean isSecure() {
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        //321485
        boolean secure = this._request.isSSL();
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"isSecure", "this->"+this+": "+" value --> " + String.valueOf(secure));
        }
        return secure;
    }

    public String getCipherSuite() {
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        //321485
        String cipherSuite = _request.getCipherSuite();
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"getCipherSuite", "this->"+this+": "+" value --> " + cipherSuite);
        }
        return cipherSuite;
    }
    
    public String getDirectConnectionCipherSuite() {
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
       Class requestClass = _request.getClass();
		String cipherSuite = null;
    	if (requestClass.getName().equals("com.ibm.ws.webcontainer.channel.WCCRequestImpl")){
    		Method method;
			try {
				method = requestClass.getMethod("getConnectionCipherSuite", null);
			
				cipherSuite = (String) method.invoke(_request, null);
			} catch (Exception e) {
				com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(e, "com.ibm.ws.webcontainer.srt.SRTServletRequest.getDirectConnectionCipherSuite", "587", this);
				if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
		            logger.logp(Level.FINE, CLASS_NAME,"getDirectConnectionCipherSuite", "failed to retrieve direction connection cipher suite",e);
		        }
			}
    	}
        
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"getDirectConnectionCipherSuite", "this->"+this+": "+" value --> " + cipherSuite);
        }
        return cipherSuite;
    }
    
    /**
     * Return the peer (i.e. the client) certificates.
     * This must be an SSL connection with mutual authentication;
     * else, null is returned.
     * @return the peer certificates, or null if none.
     **/
    public X509Certificate[] getPeerCertificates()
    {
        //321485
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"getPeerCertificates", "this->"+this+": ");
        }
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) logger.logp(Level.FINE, CLASS_NAME,"getPeerCertificates", "this->"+this+": ");  //306998.15
        X509Certificate[] certs = _request.getPeerCertificates();
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) logger.logp(Level.FINE, CLASS_NAME,"getPeerCertificates", "this->"+this+": "+"certificates", certs);  //306998.15
        return certs;
    }

    /**
     * Return the peer (i.e. the client) certificates.
     * This must be an SSL connection with mutual authentication;
     * else, null is returned.
     * @return the peer certificates, or null if none.
     **/
    public X509Certificate[] getDirectConnectionPeerCertificates()
    {
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
    	Class requestClass = _request.getClass();
    	X509Certificate[] certs = null;
    	if (requestClass.getName().equals("com.ibm.ws.webcontainer.channel.WCCRequestImpl")){
    		Method method;
			try {
				method = requestClass.getMethod("getConnectionPeerCertificates", null);
			
				certs = (X509Certificate[]) method.invoke(_request, null);
			} catch (Exception e) {
				com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(e, "com.ibm.ws.webcontainer.srt.SRTServletRequest.getDirectConnectionPeerCertificates", "635", this);
				if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
		            logger.logp(Level.FINE, CLASS_NAME,"getDirectConnectionPeerCertificates", "failed to retrieve direction connection cipher suite",e);
		        }
			}
    	}
        
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"getDirectConnectionPeerCertificates", "this->"+this+": "+" value --> " + certs);
        }
        return certs;
    }
    

    //F001872
    private Boolean isDirectConnection()
    {
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        Boolean direct = Boolean.valueOf("false");
        Class requestClass = _request.getClass();
        if (requestClass.getName().equals("com.ibm.ws.webcontainer.channel.WCCRequestImpl")){
            Method method;
            try{
                method = requestClass.getMethod("checkForDirectConnection", null);
                direct = (Boolean) method.invoke(_request, null);
            }
            catch (Exception e) {
                if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
                    logger.logp(Level.FINE, CLASS_NAME,"isDirectConnection", "failed to retrieve direct connection check",e);
                }
            }
        }
        
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"isDirectConnection", "return -> "+ direct.booleanValue());
        }
        return direct;
    }
    //F001872 - end

    /**
     * Sets the _request.
     * @param _request The _request to set
     */
    public void setRequest(IRequest _request) {
        //321485
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"setRequest", "this->"+this+": ");
        }
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        this._request = _request;
    }

    public void attributeAdded(String key, Object newVal) 
    {
        //321485
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"attributeAdded", "this->"+this+": "+" key --> " + key + " value --> " + newVal);
        }
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
    	WebApp webapp =_dispatchContext.getWebApp();
    	if (webapp != null) 
        	webapp.notifyServletRequestAttrAdded(this, key, newVal);
    }

    public void attributeRemoved(String key, Object oldVal) {
        //321485
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"attributeRemoved", "this->"+this+": "+" key --> " + key + " value --> " + oldVal);
        }
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
		WebApp webapp =_dispatchContext.getWebApp();
		if (webapp != null) 
        	webapp.notifyServletRequestAttrRemoved(this, key, oldVal);
    }

    public void attributeReplaced(String key, Object oldVal) {
        //321485
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"attributeReplaced", "this->"+this+": "+" key --> " + key + " value --> " + oldVal);
        }
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
		WebApp webapp =_dispatchContext.getWebApp();
		if (webapp != null) 
	        webapp.notifyServletRequestAttrReplaced(this, key, oldVal);
    }

    protected void setLocales(Iterator iter) {
        //321485
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"setLocales", "this->"+this+": ");
        }
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        while (iter.hasNext()) {
        	_srtRequestHelper._locales.add(iter.next());
        }
    }

    /**
          * Returns an input stream for reading binary request data.
          * 
	      * Note for multi-read:
	      *  If this is the first call - register an observer to get a notifiction (alertClose()) when input stream is closed.
	      *  If the is first call after alerClose, restart the input stream. This will cause a notification (alertOpen()) that the input stream has been opened.  
	      *  Any other call - no special processing.
          */
    public ServletInputStream getInputStream() throws IOException {
    	
    	if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"getInputStream", "this->"+this+": gotReader = " + _srtRequestHelper._gotReader);
        }
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
    	
        if (_srtRequestHelper._gotReader)
            throw new IllegalStateException(
                nls.getString(
                    "Reader.already.obtained",
                    "Reader already obtained"));
        
        // F003449 Start
        // if stream is currently closed, allow re-read.
        // otherwise this must be thr first read so register as an 
        // observer to be notified when close occurs.
        if (WCCustomProperties.ENABLE_MULTI_READ_OF_POST_DATA) {
            if (_srtRequestHelper._InputStreamClosed) {
                _in.restart();
            } 
        }    
        // F003449 End
        _srtRequestHelper._gotInputStream = true;
        return _in;
    }
    
    
    /*
     * Added for F003449 for use by ParseParameters
     * For multi read returns a ServletInputStream input stream irrespective of whether a 
     * reader has been obtained. The behavior if a reader has been obtained but not closed
     * is the same as normal behavior (no multi-read) if parseParameters gets the input stream
     * when a servletInputStream has been previously obtained but not closed. A failue will 
     * result - insufficient post data.   
     */
    protected ServletInputStream getInputStreamInternal() throws IOException {
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        if (WCCustomProperties.ENABLE_MULTI_READ_OF_POST_DATA) {
            if (_srtRequestHelper._InputStreamClosed) {
        	    _in.restart();
            } 
            _srtRequestHelper._gotInputStream = true;
            return _in;
        } else {
           return getInputStream();
        }
    }
    
    public IRequest getIRequest(){
        //321485
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"getIRequest", "this->"+this+": ");
        }
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
    	return _request;
    }

    /*
     * Note for multi-read:
     *  If this is the first call - register an observer to get a notifiction (alertClose()) when input stream is closed.
     *  If the is first call after alerClose, restart the input stream. This will cause a notification (alertOpen()) that the input stream has been opened.  
     *  Any other call - no special processing.
     */
    public synchronized BufferedReader getReader()
        throws UnsupportedEncodingException, IOException {
    	
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"getReader", "this->"+this+": gotReader = " + _srtRequestHelper._gotInputStream);
        }
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        
        if (_srtRequestHelper._gotInputStream)
            throw new IllegalStateException(
                nls.getString(
                    "InputStream.already.obtained",
                    "Input Stream already obtained"));

        if (_srtRequestHelper._reader == null) {
        	_srtRequestHelper._gotReader = true;
        	
        	
            // F003449 Start
        	// Even if reader is null the input stream may already have
        	// been read by parseParameters.
            if (WCCustomProperties.ENABLE_MULTI_READ_OF_POST_DATA) {
            	if (_srtRequestHelper._InputStreamClosed) {
            		_in.restart();
            	} 
            }
            // F003449 End
            
        	
        	_srtRequestHelper._reader =
                new BufferedReader(
                    new InputStreamReader(_in, getReaderEncoding()));
            // F003449 Start
            // if input stream has already been read and closed allow re-read	
        } else if (WCCustomProperties.ENABLE_MULTI_READ_OF_POST_DATA) {
        	if (_srtRequestHelper._InputStreamClosed){                      
                _in.restart();
            
        	    _srtRequestHelper._reader =
                    new BufferedReader(
                        new InputStreamReader(_in, getReaderEncoding()));
        	}    
        }
            // F003449 End
        

        return _srtRequestHelper._reader;
    }

    /*
     * Releases the InputStream once the obtainer has no more need for it.
     */
    protected void releaseInputStream() {
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        if (_srtRequestHelper._gotInputStream) {
        	_srtRequestHelper._gotInputStream = false;
        }
    }

    /*
     * Releases the Reader once the obtainer has no more need for it.
     */
    protected void releaseReader() {
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        if (_srtRequestHelper._gotReader) {
        	_srtRequestHelper._gotReader = false;
        }
    }

    // F003449 Start
    /* Indicates that the input stream, obtained using either getInputStream
     * or getReader has been closed.
     */
    public void alertOpen() {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  
            logger.logp(Level.FINE, CLASS_NAME,"alertOpen()", "");
        }
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
    	_srtRequestHelper._InputStreamClosed=false;
    }

    
    /* Indicates that the input stream, obtained using either getInputStream
     * or getReader has been closed.
     */
    public void alertClose() {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  
            logger.logp(Level.FINE, CLASS_NAME,"alertClose()", "");
        }
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
    	_srtRequestHelper._InputStreamClosed=true;
    }

    /**
     * @return SRTConnectionContext
     */
    protected SRTConnectionContext getConnectionContext() {
        //321485
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"getConnectionContext", "");
        }
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        return _connContext;
    }

    /* (non-Javadoc)
     * @see com.ibm.ws.webcontainer.core.Request#getWebAppContext()
     */
    public IWebAppDispatcherContext getWebAppDispatcherContext() {
        //321485
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"getWebAppDispatcherContext", "");
        }
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        return this._dispatchContext;
    }

    /* (non-Javadoc)
     * @see com.ibm.ws.webcontainer.core.Request#setWebAppContext(com.ibm.ws.webcontainer.webapp.WebAppContext)
     */
    public void setWebAppDispatcherContext(IWebAppDispatcherContext ctx) {
        //321485
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"setWebAppDispatcherContext", " old context [" + this._dispatchContext + "] new context [" + ctx +"]");
        }
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        this._dispatchContext = (WebAppDispatcherContext)ctx;
        resetPathElements();
    }
    
    public void resetPathElements (){
        //321485
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"resetPathElements", "");
        }
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        // reset path info and request uri
        if (_srtRequestHelper!=null){
            _srtRequestHelper._requestURI = null;
            _srtRequestHelper._pathInfo = null;
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRequest#getLocalAddr()
     */
    public String getLocalAddr() {
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        //321485
        String addr = this._request.getLocalAddr();
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"getLocalAddr", " address --> " + addr);
        }
        return addr;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRequest#getLocalName()
     */
    public String getLocalName() {
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        //321485
        String name = this._request.getLocalName();
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"getLocalName", " name --> " + name);
        }
        return name;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRequest#getLocalPort()
     */
    public int getLocalPort() {
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        //321485
        int port = this._request.getLocalPort();
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"getLocalPort", " port --> " + String.valueOf(port));
        }
        return port;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRequest#getRemotePort()
     */
    public int getRemotePort() {
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        //321485
        int port = this._request.getRemotePort();
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"getRemotePort", " port --> " + String.valueOf(port));
        }
        return port;
    }

    public IExtendedResponse getResponse() {
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        return this._connContext.getResponse();
    }
    
    public void setResponse(IExtendedResponse extResp) {
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        this._connContext.setResponse(extResp);
    }

    public void start() {
    }

    public Object clone() throws CloneNotSupportedException {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  
            logger.logp(Level.FINE, CLASS_NAME,"clone", " entry");
        }
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        SRTServletRequest clonedSRTServletRequest = (SRTServletRequest) super.clone();
        SRTRequestContext clonedRequestContext = null;
        if (_requestContext != null) {
            clonedRequestContext=(SRTRequestContext)_requestContext.clone(clonedSRTServletRequest);
            clonedSRTServletRequest._requestContext=clonedRequestContext;
        }

        WebAppDispatcherContext clonedDispatchContext = null;
        if (_dispatchContext != null) {
            clonedDispatchContext=(WebAppDispatcherContext)_dispatchContext.clone(clonedSRTServletRequest,clonedRequestContext);
            clonedSRTServletRequest._dispatchContext=clonedDispatchContext;
        }
        
        SRTConnectionContext clonedConnContext=null;
        if(_connContext!=null){
            clonedConnContext=(SRTConnectionContext)_connContext.clone(clonedSRTServletRequest,clonedDispatchContext);
            clonedSRTServletRequest._connContext=clonedConnContext;
        }

        SRTServletRequestHelper _clonedsrtRequestHelper = null;
        if (_srtRequestHelper != null) {
            _clonedsrtRequestHelper = (SRTServletRequestHelper) _srtRequestHelper.clone();
            clonedSRTServletRequest._srtRequestHelper = _clonedsrtRequestHelper;
        }
        if (_paramStack != null) {
            clonedSRTServletRequest._paramStack = (UnsynchronizedStack) _paramStack.clone();
        }
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {
            logger.logp(Level.FINE, CLASS_NAME,"clone", " exit original -->" + this + " cloned -->" + clonedSRTServletRequest);
        }

        return clonedSRTServletRequest;
    }
    
	// LIDB1234.5 - modify method below to return request object static variables
	/**
	 * Returns the authentication scheme of the request, or null if none.
	 * Same as the CGI variable AUTH_TYPE.
	 */
	public String getAuthType()
	{   
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
		String authType = null;

		//if (com.ibm.ws.security.core.SecurityContext.isSecurityEnabled())
		if(((WebAppDispatcherContext)this._dispatchContext).isSecurityEnabledForApplication())
		{
			authType = (String) getPrivateAttribute("AUTH_TYPE");
		}
		else
		{
			authType = _request.getAuthType();
		}
                //321485
                if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
                    logger.logp(Level.FINE, CLASS_NAME,"getAuthType", " authType --> " + authType);
                }
		// return one of the static vars defined in HttpServletRequest
		if (authType != null)
		{
			if (authType.equals("BASIC"))
				return HttpServletRequest.BASIC_AUTH;
			else if (authType.equals("CLIENT_CERT"))
				return HttpServletRequest.CLIENT_CERT_AUTH;
			else if (authType.equals("DIGEST"))
				return HttpServletRequest.DIGEST_AUTH;
			else if (authType.equals("FORM"))
				return HttpServletRequest.FORM_AUTH;
		}

		return authType;
	}

	/**
	* This method was created in VisualAge.
	* @return java.lang.String
	*/
	public String getCharacterEncoding()
	{
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
		if (_srtRequestHelper._characterEncoding != null)
			return _srtRequestHelper._characterEncoding;

		// first see if it's been specified in the content type header
        // Begin 287829, Fix setContentType when qoutes are a part of the charset
        
		String type = getContentType();
		
		int index = -1;
		if (type != null)
			index = type.indexOf("charset=");
		String _encoding = getEncodingFromContentType(type,index);
		
		if (_encoding!=null)
		{
				try
				{
					setCharacterEncoding(_encoding);
				}
				catch (UnsupportedEncodingException e)
				{
					logger.logp(Level.INFO, CLASS_NAME,"getCharacterEncoding", "Unable to set request character encoding based upon request header ", e);
	
				}
                       
		}
                  
		return _encoding;
		
	}
	
	public static String getEncodingFromContentType(String type,int index) {
		String _encoding=null;
		if (index > -1)
		{
            int startIndex = index+8;
            int endIndex;
            int semicolonIndex = type.indexOf(';', startIndex+1);
            if (semicolonIndex==-1){
            	endIndex = type.length()-1;
            }
            else {
            	endIndex = semicolonIndex-1;
            }
            if (startIndex<=endIndex){
	            boolean startsWithQoute = type.charAt(startIndex)=='"'||type.charAt(startIndex)=='\'';
	            boolean endsWithQoute = type.charAt(endIndex)=='"'||type.charAt(endIndex)=='\'';
	           if (startsWithQoute && endsWithQoute)
	           		_encoding = type.substring(startIndex+1,endIndex);
	           else {
	        	   if (semicolonIndex==-1)
	        		   _encoding = type.substring(startIndex);
	        	   else
	        		   _encoding = type.substring(startIndex,semicolonIndex);
	           }
	           
				
               
				
            }
		}
		 //321485
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"getEncodingFromContentType", "type->"+type+", encoding --> " + _encoding);
        }
		return _encoding;
	}

	public String getReaderEncoding()
	{
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
		// see if it's already set
		if (_srtRequestHelper._readerEncoding != null)
		{
			return _srtRequestHelper._readerEncoding;
		}

		// 115780 - set encoding to the static override if the user has set it
		String encoding = CLIENT_ENCODING_OVERRIDE;

		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))  //306998.15
			logger.logp(Level.FINE, CLASS_NAME,"getReaderEncoding", "client encoding override --> " + encoding);
		
		// try getting from the char set var (basically looking for it in content type header or from setCharacterEncoding())
		if (encoding == null)
		{
			encoding = getCharacterEncoding();
			if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))  //306998.15
				logger.logp(Level.FINE, CLASS_NAME,"getReaderEncoding", "content-type header --> " + encoding);
		}

		WebAppConfiguration webAppCfg = ((WebAppDispatcherContext)this._dispatchContext).getWebApp().getConfiguration();
		
		// not specifyed by character encoding...if autoRequestEncoding is on, try to determine
		// from the accepted languages
		if (encoding == null && webAppCfg.isAutoRequestEncoding())
		{
			String acceptLanguage = getHeader("Accept-Language");
			if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))  //306998.15
				logger.logp(Level.FINE, CLASS_NAME,"getReaderEncoding", "accept-language --> " + acceptLanguage);
			
			if (acceptLanguage != null && (!acceptLanguage.equals("*")))
			{
	            Locale _locale = getLocale();
		            /**
		             * Check the DD locale-endoding mappings to see if there is a specified mapping
		             * @since Servlet 2.4
		             */
	            	encoding = webAppCfg.getLocaleEncoding(_locale);
		
		            if (encoding == null) {
		            	encoding = EncodingUtils.getEncodingFromLocale(_locale);
						if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))  //306998.15
							logger.logp(Level.FINE, CLASS_NAME,"getReaderEncoding", "encoding from locale --> " + encoding);
		            }
				
			}
		}

		if (encoding == null)
		{
			// 115780 - set encoding to the static default if the user has set it
			encoding = DEFAULT_CLIENT_ENCODING;
			if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))  //306998.15
				logger.logp(Level.FINE, CLASS_NAME,"getReaderEncoding", "default client encoding -->" + encoding);
		}

		if (encoding == null)
		{
			// no choice but to default to the standard
			encoding = "ISO-8859-1";
			if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))  //306998.15
				logger.logp(Level.FINE, CLASS_NAME,"getReaderEncoding", "default encoding --> " + encoding);
		}

		_srtRequestHelper._readerEncoding = EncodingUtils.getJvmConverter(encoding);
		
		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))  //306998.15
			logger.logp(Level.FINE, CLASS_NAME,"getReaderEncoding", " encoding  converted --> " + _srtRequestHelper._readerEncoding);
		
		return (_srtRequestHelper._readerEncoding);
	}

	public Cookie[] getCookies()
	{
            //321485
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
                logger.logp(Level.FINE, CLASS_NAME,"getCookies", "");
            }
        	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
            	checkRequestObjectInUse();
            }
		if (!_srtRequestHelper._cookiesParsed)
		{
			_srtRequestHelper._cookies = _request.getCookies();
			_srtRequestHelper._cookiesParsed = true;
		}
		
		return _srtRequestHelper._cookies;
	}
	
    // PQ94384
    public void addParameter(String name, String[] values) {
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
		Hashtable aParam = new Hashtable(3);
		aParam.put(name, values);
		mergeQueryParams(aParam);
    }
    //PQ94384

    public void setMethod(String method)
    {
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        _srtRequestHelper._method = method;
    }
    
	// PK57679 Start - add methods setInputStreamData() and getInputStreamData() to be used by security code
	public void setInputStreamData(HashMap inStreamInfo) throws IOException
	{

    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }	
		if (_srtRequestHelper._gotReader)
		{
			if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
	            logger.logp(Level.FINE, CLASS_NAME,"setInputStreamData","attempt to setInputStreamData after it has been read");
            throw new IllegalStateException(
		             nls.getString(
		                 "Reader.already.obtained",
		                 "Reader already obtained"));
		} else {
			_setInputDataStreamCalled = true;   // 516233
			
			if (inStreamInfo != null)
		    {	
			
			    // 516233 - the input map should have a contentLength, if not we were given the wring map 
			    Integer contentLength = (Integer)inStreamInfo.get(INPUT_STREAM_CONTENT_DATA_LENGTH);
			
			    if (contentLength==null) {
			
			    	// security passed in a map that we did not create - should not happen
			    	if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
					   logger.logp(Level.FINE, CLASS_NAME,"setInputStreamData", "No content length in passed map. Throw IOException");
                    throw new IllegalStateException();
                
			    } else {
				
				    _setInputStreamContentLength = contentLength.intValue();
				
		            _setInputStreamContentType = (String)inStreamInfo.get(INPUT_STREAM_CONTENT_TYPE);
		   		        
			        byte[] inStreamContentData = (byte[])inStreamInfo.get(INPUT_STREAM_CONTENT_DATA);
			
			        // 516233 allow for no post data
		            if (inStreamContentData != null)
			        {				
			       
		            	if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
			    	        logger.logp(Level.FINE, CLASS_NAME, "setInputStreamData", "SetInputStreamData Content Type = " + _setInputStreamContentType + ", " +
						    "contentLength = " + _setInputStreamContentLength+ ", data length = " + inStreamContentData.length + " : this = " + this );
				    		    	    	
		                ByteArrayInputStream inDataInputStream = new ByteArrayInputStream(inStreamContentData);
		                try
		                {
			                _in.init(inDataInputStream);
			                if (inStreamContentData.length > 0)
			                {
				                _in.setContentLength(inStreamContentData.length);
			                }
		                }    
		                catch (IOException exc)
		                {
		                	if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
			 	                logger.logp(Level.FINE, CLASS_NAME, "setInputStreamData", "Exception caught : " + exc );
			                throw exc;
		                }    
		            }
			    }        
		    } else {
		    	// securty passed didn't provide a map - should not happen
		    	if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
		    		logger.logp(Level.FINE, CLASS_NAME, "setInputStreamData","No map passed as input");
                throw new IllegalArgumentException();
		    }    
		}
	}
	
	public HashMap getInputStreamData() throws IOException
	{
		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){
			logger.entering(CLASS_NAME, "getInputStreamData");
            logger.logp(Level.FINE, CLASS_NAME,"getInputStreamData","[" + this + "]");
		}
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
		
		ServletInputStream in = this.getInputStream();
		
		HashMap inStreamInfo = new HashMap();
        // 516233 allow for getContentType() returning null
		if (this.getContentType()!=null)
		{
		    inStreamInfo.put(INPUT_STREAM_CONTENT_TYPE,new String(this.getContentType()));
		} else {
			inStreamInfo.put(INPUT_STREAM_CONTENT_TYPE,null);
		}
		
		int offset = 0, inputLen = 0, len = getContentLength();
        
        // 516233 add content length
		inStreamInfo.put(INPUT_STREAM_CONTENT_DATA_LENGTH,new Integer(len));
		
		// 516133 allow for no post data
		if (len>0)
		{	
            byte [] postedBytes = new byte[len];
 
            do
            {
                inputLen = in.read(postedBytes, offset, len - offset);
                if (inputLen <= 0)
                {
                    String msg = nls.getString("post.body.contains.less.bytes.than.specified", "post body contains less bytes than specified by content-length");
                    throw new IOException(msg);
                }
                offset += inputLen;
            }
            while ((len - offset) > 0);
        
            inStreamInfo.put(INPUT_STREAM_CONTENT_DATA,postedBytes);
		} else {
			inStreamInfo.put(INPUT_STREAM_CONTENT_DATA,null);
		}
        

        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){
            logger.logp(Level.FINE, CLASS_NAME,"getInputStreamData","ContentType = " + this.getContentType() + ", data length = " + len);
            logger.exiting(CLASS_NAME, "getInputStreamData");
        }
        
        return inStreamInfo;
		
	}
    // PK57679 End

	
	public void setRawParameters(Hashtable params)
	{
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
            //321485
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
                logger.logp(Level.FINE, CLASS_NAME,"setRawParameters", "");
            }
		_srtRequestHelper._parameters = params;
	}

	public Hashtable getRawParameters()
	{
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
            //321485
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
                logger.logp(Level.FINE, CLASS_NAME,"getRawParameters", "");
            }
		parseParameters();
		return (Hashtable)_srtRequestHelper._parameters;
	}
	/**
	 * Returns the value of the specified parameter for the request. For
	 * example, in an HTTP servlet this would return the value of the
	 * specified query string parameter. This must be used when the 
	 * application is sure that there is only one value for the parameter.
	 * For multiple valued parameters, use getParameterValues. 
	 * @param name the parameter name
	 * @return the value for the parameter. For multiple values, a comma 
	 * separated string of values is returned. Preferred way is to call
	 * getParameterValues for multiple valued parameters.
	 * @see getParameterValues
	 */
	public String getParameter(String name)
	{
            //321485
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
                logger.logp(Level.FINE, CLASS_NAME,"getParameter", " name --> " + name);
            }
        	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
            	checkRequestObjectInUse();
            }
		parseParameters();
		String[] values = (String[]) _srtRequestHelper._parameters.get(name);
		String value=null;
		if (values != null && values.length > 0)
		{
		    value = values[0];
		}
		 if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
             logger.logp(Level.FINE, CLASS_NAME,"getParameter", " value --> " + name);
         }
		 return value;
	}
	/**
	 * Returns an enumeration of strings representing the parameter names
	 * for this request.
	 */
	public Enumeration getParameterNames()
	{
            //321485
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
                logger.logp(Level.FINE, CLASS_NAME,"getParameterNames", "");
            }
        	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
            	checkRequestObjectInUse();
            }
		parseParameters();
		return ((Hashtable)_srtRequestHelper._parameters).keys();
	}
	/**
	 * Returns the value of the specified parameter for the request. For
	 * example, in an HTTP servlet this would return the value of the
	 * specified query string parameter.
	 * @param name the parameter name
	 * @return an array of values for the passed parameter name. If there are
	 * no values then return null.
	 */
	public String[] getParameterValues(String name)
	{
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
            parseParameters();
            //321485
            String[] values = (String[]) _srtRequestHelper._parameters.get(name);
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
                logger.logp(Level.FINE, CLASS_NAME,"getParameterValues", " name --> " + name);
            }
		return values;
	}
	/**
	 * Returns null since this request has no concept of servlet mappings.
	 * This method will be overidden by the webapp layer.
	 */
	public String getPathInfo()
	{
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
           // Begin PK06988, strip session id of when url rewriting is enabled
		if (_srtRequestHelper._pathInfo==null){
			String aPathInfo =  ((WebAppDispatcherContext)this._dispatchContext).getPathInfo();
			if (aPathInfo == null)
				return null;
			else // Do not strip based on ? again, it was already done and we don't want to strip '%3f's that have since been decoded to ?'s
				_srtRequestHelper._pathInfo = WebGroup.stripURL(aPathInfo,false); //293696    ServletRequest.getPathInfo() fails    WASCC.web.webcontainer
		}
                //321485
                String path = _srtRequestHelper._pathInfo;
                //PK28078
                if(path.equals("")) {
                    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) 
                        logger.logp(Level.FINE, CLASS_NAME,"getPathInfo", " path is \"\", returning null");
                    return null;
                }
                else {
                    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) 
                        logger.logp(Level.FINE, CLASS_NAME,"getPathInfo", " path --> [" + path + "]");
                    return path;
                }
		//return path;
		// End PK06988, strip session id of when url rewriting is enabled
	}
	
	
	public String getServletPath() {
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
            //321485
            String path = ((WebAppDispatcherContext)this._dispatchContext).getServletPath();
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
                logger.logp(Level.FINE, CLASS_NAME,"getServletPath", "path --> " + path);
            }
		return path;
	}

	/**
	 * Returns extra path information translated to a real file system path.
	 * Returns null if no extra path information was specified or translated
	 * path was unavailable. Same as the CGI variable PATH_TRANSLATED.
	 */
	public String getPathTranslated()
	{
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
            //321485
            String path = ((WebAppDispatcherContext)this._dispatchContext).getPathTranslated();
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
                logger.logp(Level.FINE, CLASS_NAME,"getPathTranslated", " --> path " + path);
            }
		return path;
	}

	/**
	 * Returns the query string part of the servlet URI, or null if none.
	 * Same as the CGI variable QUERY_STRING.
	 */
	public String getQueryString()
	{
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
		if (_srtRequestHelper._queryString == null && !_srtRequestHelper._qsSetExplicit)
			_srtRequestHelper._queryString = _request.getQueryString();
                //321485
                String queryString = _srtRequestHelper._queryString;
                if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
                    logger.logp(Level.FINE, CLASS_NAME,"getQueryString", " queryString --> " + queryString);
                }	
		return queryString;
	}
	
	public void setQueryString(String qs)
	{
            //321485
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
                logger.logp(Level.FINE, CLASS_NAME,"setQueryString", " queryString --> " + qs);
            }
        	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
            	checkRequestObjectInUse();
            }
		_srtRequestHelper._qsSetExplicit=true;
		_srtRequestHelper._queryString = qs;
	}

	/**
	 * Applies alias rules to the specified virtual path and returns the
	 * corresponding real path.
	 */
	public String getRealPath(String path)
	{
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
            //321485
            String realPath = ((WebAppDispatcherContext)this._dispatchContext).getRealPath(path);
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
                logger.logp(Level.FINE, CLASS_NAME,"getRealPath", "path -->  " + path + " realPath --> " + realPath);
            }
		return realPath;
	}

	/**
	 * Returns the name of the user making this request, or null if not
	 * known. Same as the CGI variable REMOTE_USER.
	 */
	public String getRemoteUser()
	{
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        //if (com.ibm.ws.security.core.SecurityContext.isSecurityEnabled())
        if(((WebAppDispatcherContext)this._dispatchContext).isSecurityEnabledForApplication())
        {
            //321485
            String user = com.ibm.ws.security.core.SecurityContext.getName();
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
                logger.logp(Level.FINE, CLASS_NAME,"getRemoteUser", " (security enabled)--> user " + user);
            }
			return user;
		}
		else
		{
            //321485
            String user = _request.getRemoteUser();
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
                logger.logp(Level.FINE, CLASS_NAME,"getRemoteUser", " user -->  " + user);
            }
			return user;
		}
	}

	public String getRequestedSessionId()
	{
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
            //321485
            String id = ((WebAppDispatcherContext)this._dispatchContext).getRequestedSessionId();
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
                logger.logp(Level.FINE, CLASS_NAME,"getRequestedSessionId", "id --> " + id);
            }
		//return _connContext.getSessionAPISupport().getRequestedSessionId();
		return id;
	}

	/**
	 * Returns the request URI as string.
	 */
	public String getRequestURI()
	{
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
		// Begin PK06988, strip session id of when url rewriting is enabled
		if (this._srtRequestHelper._requestURI ==null)
		{
			String aURI= getEncodedRequestURI();
			if (aURI == null)
					return null;
			else
				this._srtRequestHelper._requestURI = WebGroup.stripURL(aURI);
		}
            //321485
                String uri = this._srtRequestHelper._requestURI;
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
                logger.logp(Level.FINE, CLASS_NAME,"getRequestURI", " uri --> " + uri);
            }
		return uri;
		// End PK06988, strip session id of when url rewriting is enabled
	}

	/**
	 * Returns the host name of the server that received the request.
	 * Same as the CGI variable SERVER_NAME.
	 */
	public String getServerName()
	{
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
		String sName = this._request.getServerName();
		
		//if (sName == null || sName.equalsIgnoreCase("localhost"))
		if (sName == null || sName.length() == 0)
		{
			try
			{
				// get a real name for the local machine
				sName = InetAddress.getLocalHost().getHostName();
				if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){  //306998.15
					logger.logp(Level.FINE, CLASS_NAME,"getServerName", "using InetAddress --> " + sName);
				}
			}
			catch (Throwable th)
			{
				// not much we can do here...just return what we have
			}
		}
		//Begin 255189, Part 2
		if (sName.charAt(0)!= '[' && sName.indexOf(':')!=-1)
			sName = "[" + sName + "]";
		//End 255189, Part 2
		
		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){  //306998.15
			logger.logp(Level.FINE, CLASS_NAME,"getServerName", "serverName --> " + sName);
		}
		return sName;
	}

	/**
	 * This method was created in VisualAge.
	 * @return javax.servlet.http.HttpSession
	 */
	public HttpSession getSession()
	{
		return getSession(true);
	}
	/**
	 * Returns the session as an HttpSession. This does all of the "magic"
	 * to create the session if it doesn't already exist.
	 */
	public HttpSession getSession(boolean create)
	{
            //321485
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
                logger.logp(Level.FINE, CLASS_NAME,"getSession", "create " + String.valueOf(create));
            }
        	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
            	checkRequestObjectInUse();
            }
		//return _connContext.getSessionAPISupport().getSession(create);
		return _requestContext.getSession(create, ((WebAppDispatcherContext)this._dispatchContext).getWebApp());
	}
	public boolean isRequestedSessionIdFromCookie()
	{   //321485
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
            boolean idFromCookie = ((WebAppDispatcherContext)this._dispatchContext).isRequestedSessionIdFromCookie();
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
                logger.logp(Level.FINE, CLASS_NAME,"isRequestedSessionIdFromCookie", " " + String.valueOf(idFromCookie));
            }
		//return _connContext.getSessionAPISupport().isRequestedSessionIdFromCookie();
		return idFromCookie;
	}

	public boolean isRequestedSessionIdFromUrl()
	{
		return isRequestedSessionIdFromURL();
	}
	/**
	 * This method was created in VisualAge.
	 * @return boolean
	 */
	public boolean isRequestedSessionIdFromURL()
	{
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
            //321485
            boolean idFromURL = ((WebAppDispatcherContext)this._dispatchContext).isRequestedSessionIdFromURL();
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
                logger.logp(Level.FINE, CLASS_NAME,"isRequestedSessionIdFromURL", " " + String.valueOf(idFromURL));
            }
		//return _connContext.getSessionAPISupport().isRequestedSessionIdFromURL();
		return idFromURL;
	}
	/**
	 * If the session doesn't exist, then the Id that came
	 * in is invalid. If there is no sessionID in the request, then
	 * it's not valid.
	 */
	public boolean isRequestedSessionIdValid()
	{
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
            //321485
            boolean sessionInvalid = _requestContext.isRequestedSessionIdValid(((WebAppDispatcherContext)this._dispatchContext).getWebApp());
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
                logger.logp(Level.FINE, CLASS_NAME,"isRequestedSessionIdValid", " " + String.valueOf(sessionInvalid));
            }
		return sessionInvalid;
	}
	
//    protected void removeHeader(String key) {
//        //311717
//        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
//            logger.logp(Level.FINE, CLASS_NAME,"removeHeader", " name --> " + key);
//        }
//        request.removeHeader(key);
//    }


	synchronized public void parseParameters()
	{
		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
			logger.logp(Level.FINE, CLASS_NAME,"parseParameters", "");
        }
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        
        if (_srtRequestHelper._parameters != null)
        	return;

        //PM03928 - start
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"parseParameters", "set _parametersRead");
        }
        _srtRequestHelper._parametersRead = true;                               
        
        //PM03928 - end

		try
		{
			_srtRequestHelper._parameters = new Hashtable();
			String ct = getContentType();

			if (ct != null)
			{
				ct = ct.toLowerCase();
				if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
					logger.logp(Level.FINE, CLASS_NAME,"parseParameters", "Content type -->" + ct);

				if (ct.startsWith("java-internal"))
				{
					String[] values = { ct };
					_srtRequestHelper._parameters.put(new String("Application specific data. Content-type "), values);
					return;
				}
			}
		}
		catch (Exception npe)
		{
            com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(npe, "com.ibm.ws.webcontainer.srt.SRTServletRequest.parseParameters", "667", this);
			logger.logp(Level.INFO, CLASS_NAME,"parseParameters", "Exception thrown during parsing of parameters", npe.toString());
			return;
		}

        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
		    logger.logp(Level.FINE, CLASS_NAME,"parseParameters", "Content type is not java-internal");
        }

		String method = getMethod();

		if (method.equalsIgnoreCase("post"))
			// end pq70055: part 1
		{
			String contentType = getContentType();
			if (contentType != null && contentType.startsWith("application/x-www-form-urlencoded"))
			{
				try
				{
				    // begin 231634    Support posts with query parms in chunked body    WAS.webcontainer
					if( getContentLength() > 0){
						if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))  //306998.15
							logger.logp(Level.FINE, CLASS_NAME,"parseParameters", "parsing post data based upon content length");
						_srtRequestHelper._parameters = RequestUtils.parsePostData(getContentLength(), getInputStreamInternal(), getReaderEncoding());  // F003449
					}
					else{
						if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))  //306998.15
							logger.logp(Level.FINE, CLASS_NAME,"parseParameters", "parsing post data based upon input stream (possibly chunked)");
						_srtRequestHelper._parameters = RequestUtils.parsePostData(getInputStreamInternal(), getReaderEncoding());   // F003449
					}
				    // end 231634    Support posts with query parms in chunked body    WAS.webcontainer
				}
				catch (IOException io)
				{
                    com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(io, "com.ibm.ws.webcontainer.srt.SRTServletRequest.parseParameters", "765", this);
					logger.logp(Level.SEVERE, CLASS_NAME,"parseParameters", "Error.Parsing.Parameters", io);
				}
				finally
				{
					releaseInputStream(); //Let go of the InputStream now that we're done with it.
				}
				if (_srtRequestHelper._parameters != null)
				{
					parseQueryStringList();	// 256836
				}
			}
			if (contentType != null && contentType.startsWith("multipart/form-data"))
			{
		        if (_srtRequestHelper._parameters != null)
	            {
		            parseQueryStringList();	//256836
		            try {
		                prepareMultipart();
		                StringBuffer value=new StringBuffer("");
		                for (String partName:_srtRequestHelper.multipartPartsHashMap.keySet()) {
		                    Part p = _srtRequestHelper.multipartPartsHashMap.get(partName);
		                    if (p!=null) {
		                        value.setLength(0);
		                        if (((SRTServletRequestPart)p).isFormField()) { //only add the parameter if it's not a file
		                            InputStream partInputStream = p.getInputStream();
		                            BufferedReader reader = new BufferedReader(new InputStreamReader(partInputStream));
		                            String line=null;
		                            try {
		                                while((line = reader.readLine())!=null) {
		                                    value.append(line);
		                                }
		                            } finally {
		                                partInputStream.close();
		                                reader.close();
		                            }
		                            String[] values = { value.toString() };
		                            _srtRequestHelper._parameters.put(partName, values);		                            
		                        }
		                    }	
		                }
		            } //724365.2 Start
		            catch (UnsupportedOperationException uoe){
		            	// take care of Portal or any other customer using own logic of handling Multipart form
		            	if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {
		                    logger.logp(Level.FINE, CLASS_NAME,"parseParameters", "Error parsing parameters, maybe no MutliPartConfig defined in servlet. ", uoe);
		                }		            	
		            }
		            catch (IllegalArgumentException iae) {		
		            	// take care of max number of parameters allowed		            		
		            		logger.logp(Level.SEVERE, CLASS_NAME,"parseParameters", "Error.Parsing.Parameters", iae);		            				                
		                	throw iae;          	
		            }
		            catch (Exception e) {
		            	
		                if(logMultiPartExceptionsOnParseParameter){ 		                		                	
		            		logger.logp(Level.SEVERE, CLASS_NAME,"parseParameters", "Error.Parsing.Parameters", e);		            				                		                	
		                }
		                else{		                	
		                	if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {
		                		logger.logp(Level.FINE, CLASS_NAME,"parseParameters", "Error.Parsing.Parameters", e);
		                	}
		                }
		            }
		            //724365.2 End
	            }
			}
			// begin pq70031: need to check if param list is empty in addition to null		
			if ((_srtRequestHelper._parameters == null || _srtRequestHelper._parameters.isEmpty()))
				// end pq70031
			{
				parseQueryStringList();	//256836
			}
		}
		// begin pq70055: part 2: failed to parse query string for head requests
		else
		{
			parseQueryStringList();	//256836	
		}
		// end pq70055: part 2
		if (_srtRequestHelper._parameters == null)
		{
			_srtRequestHelper._parameters = new Hashtable();
		}
	}

	//Begin 256836
	private void parseQueryStringList(){
            //321485
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
                logger.logp(Level.FINE, CLASS_NAME,"parseQueryStringList", "");
            }
        	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
            	checkRequestObjectInUse();
            }
		Hashtable tmpQueryParams=null;
		if (_srtRequestHelper._queryStringList ==null || _srtRequestHelper._queryStringList.isEmpty()){ //258025
			String queryString = getQueryString();
            if (queryString != null && ((queryString.indexOf('=') != -1) || allowQueryParamWithNoEqual))//PM35450
			{
				if (_srtRequestHelper._parameters==null||_srtRequestHelper._parameters.isEmpty())//258025
					_srtRequestHelper._parameters = RequestUtils.parseQueryString(getQueryString(), getReaderEncoding());
				else{
					tmpQueryParams = RequestUtils.parseQueryString(getQueryString(), getReaderEncoding());
					mergeQueryParams(tmpQueryParams);
				}
			}
		}
		else{
			Iterator i = _srtRequestHelper._queryStringList.iterator();
			QSListItem qsListItem=null;
			String queryString;
			while (i.hasNext()){
				qsListItem = ((QSListItem)i.next());
				queryString = qsListItem._qs;
                                //321485
                                if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
                                    logger.logp(Level.FINE, CLASS_NAME,"parseQueryStringList", "queryString --> " + queryString);
                                }
				if (qsListItem._qsHashtable!=null)
					mergeQueryParams(qsListItem._qsHashtable);
                else if (queryString != null && ((queryString.indexOf('=') != -1) || allowQueryParamWithNoEqual))//PM35450
				{
					if (_srtRequestHelper._parameters==null||_srtRequestHelper._parameters.isEmpty())//258025
					{
						qsListItem._qsHashtable = RequestUtils.parseQueryString(queryString, getReaderEncoding());
						_srtRequestHelper._parameters=qsListItem._qsHashtable;
						qsListItem._qs=null;
					}
					else{
						tmpQueryParams = RequestUtils.parseQueryString(queryString, getReaderEncoding());
						qsListItem._qsHashtable=tmpQueryParams;
						qsListItem._qs=null;
						mergeQueryParams(tmpQueryParams);
					}
				}
			}
		}
	}
	//End 256836
	private void mergeQueryParams(Hashtable tmpQueryParams)
	{
		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
			logger.logp(Level.FINE, CLASS_NAME,"mergeQueryParams", "");
        }
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
		if (tmpQueryParams != null)
		{
			if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))  //306998.15
			{
				logger.logp(Level.FINE, CLASS_NAME,"mergeQueryParams", "tmpQueryParams.size() " + tmpQueryParams.size());
				logger.logp(Level.FINE, CLASS_NAME,"mergeQueryParams", "tmpQueryParams " + tmpQueryParams);
			}
			Enumeration enumeration = tmpQueryParams.keys();
			while (enumeration.hasMoreElements())
			{
				Object key = enumeration.nextElement();
				// Check for QueryString parms with the same name
				// pre-append to postdata values if necessary
				if (_srtRequestHelper._parameters!=null&&_srtRequestHelper._parameters.containsKey(key))
				{
					if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))  //306998.15
					{
						logger.logp(Level.FINE, CLASS_NAME,"mergeQueryParams", "_paramaters contains key " + key);
					}
					String postVals[] = (String[]) _srtRequestHelper._parameters.get(key);
					String queryVals[] = (String[]) tmpQueryParams.get(key);
					String newVals[] = new String[postVals.length + queryVals.length];
					int newValsIndex = 0;
					for (int i = 0; i < queryVals.length; i++)
					{
						newVals[newValsIndex++] = queryVals[i];
					}
					for (int i = 0; i < postVals.length; i++)
					{
						newVals[newValsIndex++] = postVals[i];
					}
					_srtRequestHelper._parameters.put(key, newVals);
					if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))  //306998.15
					{
						logger.logp(Level.FINE, CLASS_NAME,"mergeQueryParams", "put key " + key + " into _parameters.");
					}
				}
				else
				{
					if(_srtRequestHelper._parameters == null) //PK14900
						_srtRequestHelper._parameters = new Hashtable();//PK14900
					_srtRequestHelper._parameters.put(key, tmpQueryParams.get(key));
					if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))  //306998.15
					{
						logger.logp(Level.FINE, CLASS_NAME,"mergeQueryParams", "put key " + key + " into _parameters. ");
					}
				}
			}
		}
	}
	//Begin 256836
	private void removeQueryParams(Hashtable tmpQueryParams)
	{
		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
			logger.logp(Level.FINE, CLASS_NAME,"removeQueryParams", "");
        }
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
		if (tmpQueryParams != null)
		{
			if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))  //306998.15
			{
				logger.logp(Level.FINE, CLASS_NAME,"removeQueryParams", "tmpQueryParams.size() " + tmpQueryParams.size());
				logger.logp(Level.FINE, CLASS_NAME,"removeQueryParams", "tmpQueryParams " + tmpQueryParams);
			}
			Enumeration enumeration = tmpQueryParams.keys();
			while (enumeration.hasMoreElements())
			{
				Object key = enumeration.nextElement();
				// Check for QueryString parms with the same name
				// pre-append to postdata values if necessary
				if (_srtRequestHelper._parameters.containsKey(key))
				{
					if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))  //306998.15
					{
						logger.logp(Level.FINE, CLASS_NAME,"removeQueryParams", "_paramaters contains key " + key);
					}
					String postVals[] = (String[]) _srtRequestHelper._parameters.get(key);
					String queryVals[] = (String[]) tmpQueryParams.get(key);
					if (postVals.length-queryVals.length>0){
						String newVals[] = new String[postVals.length - queryVals.length];
						int newValsIndex = 0;
						for (int i = queryVals.length; i < postVals.length; i++)
						{
							newVals[newValsIndex++] = postVals[i];
						}
						_srtRequestHelper._parameters.put(key, newVals);
						if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))  //306998.15
						{
							logger.logp(Level.FINE, CLASS_NAME,"removeQueryParams", "put key " + key + " into _parameters.");
						}
					}
					else
						_srtRequestHelper._parameters.remove(key);
				}
			}
		}
	}
	//End 256836
	/**
	 * Close this request.
	 * This method must be called after the request has been processed.
	 */
	public void finish()	//280584.3    6021: Cleanup of  defect 280584.2    WAS.webcontainer removed throws clause.
	{
		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
			logger.logp(Level.FINE, CLASS_NAME,"finish", "entry");
        }
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
		try
		{
				int length = getContentLength();
				if (length > 0)
				{
					_in.close();
				}
				// begin 280584.3    6021: Cleanup of  defect 280584.2    WAS.webcontainer: moved from finally block
				else
				{
					this._in.finish();
				}
				// end 280584.3    6021: Cleanup of  defect 280584.2    WAS.webcontainer
			}
		catch (IOException e)
		{
            com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(e, "com.ibm.ws.webcontainer.srt.SRTServletRequest.finish", "875", this);
            // begin 280584.3    6021: Cleanup of  defect 280584.2    WAS.webcontainer
			if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){  //306998.15
				logger.logp(Level.FINE, CLASS_NAME,"finish", "Error occurred while finishing request", e);
			}
			//logger.logp(Level.SEVERE, CLASS_NAME,"finish", "IO.Error", e);
			//se = new ServletException(nls.getString("Error.occured.while.finishing.request", "Error occurred while finishing request"), e);
            // end 280584.3    6021: Cleanup of  defect 280584.2    WAS.webcontainer
		}
		finally
		{
			this._srtRequestHelper = null;
            this._request.clearHeaders();
            this._request = null; // as SRTServletResponse.finish() does for _response
            this._requestContext.finish();
            if(this._paramStack.isEmpty() == false){
                this._paramStack.clear();
            }
		}
	}

	protected void processLocales()
	{
            //321485
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
                logger.logp(Level.FINE, CLASS_NAME,"processLocales", "entry");
            }
        	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
            	checkRequestObjectInUse();
            }
		if(_srtRequestHelper._localesProcessed)
		  return;
		if (_srtRequestHelper._locales == null)
			_srtRequestHelper._locales = new LinkedList();
		this.setLocales(EncodingUtils.getLocales(this).iterator());
		_srtRequestHelper._localesProcessed = true;
	}

	public RequestDispatcher getRequestDispatcher(String path)
	{
            //321485
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
                logger.logp(Level.FINE, CLASS_NAME,"getRequestDispatcher", " path --> " + path);
            }
        	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
            	checkRequestObjectInUse();
            }
		return ((WebAppDispatcherContext)this._dispatchContext).getRequestDispatcher(path);
	}

	public String getContextPath()
	{
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
            //321485
            String path = ((WebAppDispatcherContext)this._dispatchContext).getContextPath();
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
                logger.logp(Level.FINE, CLASS_NAME,"getContextPath", " path --> " + path);
            }
		return path;
	}

	public boolean isUserInRole(String role)
	{
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
            //321485
            boolean userInRole = ((WebAppDispatcherContext)this._dispatchContext).isUserInRole(role, this);
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
                logger.logp(Level.FINE, CLASS_NAME,"isUserInRole", " role --> " + role + " result --> " + String.valueOf(userInRole));
            }
		return userInRole;
	}

	public java.security.Principal getUserPrincipal()
	{
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
            //321485
            java.security.Principal principal = ((WebAppDispatcherContext)this._dispatchContext).getUserPrincipal();
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
                logger.logp(Level.FINE, CLASS_NAME,"getUserPrincipal", " principal --> " + (principal!=null?principal.getName():""));
            }
		return principal;
	}

	// begin pq71994    
	/**
	 * Save the state of the parameters before a call to include or forward.
	 */
	public void pushParameterStack()
	{
		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"pushParameterStack", "entry");
        }
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
		if (_srtRequestHelper._parameters ==null)
		{
			_paramStack.push(null);
		} else
		{
			_paramStack.push(((Hashtable)_srtRequestHelper._parameters).clone());
		}
		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE) && _srtRequestHelper._parameters !=null)  //306998.15
		{
			debugParams(_srtRequestHelper._parameters);
		}
	}
	
	/**
	 * Revert the state of the parameters which was saved before an include call
	 *
	 */
	public void popParameterStack()
	{
		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"popParameterStack", "entry");
        } 
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }

		try
		{
			_srtRequestHelper._parameters = (Hashtable) _paramStack.pop();
		} catch (java.util.EmptyStackException empty)
		{
			if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
                logger.logp(Level.FINE, CLASS_NAME,"popParameterStack", "Unable to remove item from stack", empty);
            } 
		}
		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE) && _srtRequestHelper._parameters !=null)  //306998.15
		{
			debugParams(_srtRequestHelper._parameters);
		}
	}
	
	private void debugParams(Map parameters)
	{
		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"debugParams", "entry");
        } 
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
		if (parameters !=null)
		{
			if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
                logger.logp(Level.FINE, CLASS_NAME,"debugParams", "Only displaying value retrieved by request.getParameter(). More parameters may exist for parameter name");
            } 
			Iterator enumeration = parameters.keySet().iterator();
			while (enumeration.hasNext())
			{
				String paramName = (String) enumeration.next(); 
				if(isSecure() || paramName.toUpperCase().indexOf("PASSWORD") > -1){
					if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
	                    logger.logp(Level.FINE, CLASS_NAME,"debugParams", "paramName --> " + paramName +" paramValue [**********]");
	                } 
				}
				else{
					String paramValue = getParameter(paramName);
					if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
	                    logger.logp(Level.FINE, CLASS_NAME,"debugParams", "paramName --> "+ paramName +" paramValue --> " + paramValue);
	                } 
				}
			}   
		}
	}
	// end pq71994

	
	//Begin 256836
	public void removeQSFromList(){
		//321485
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
                logger.logp(Level.FINE, CLASS_NAME,"removeQSFromList", "entry");
            }
        	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
            	checkRequestObjectInUse();
            }
		if (_srtRequestHelper._queryStringList!=null&&!_srtRequestHelper._queryStringList.isEmpty()){
			Map _tmpParameters = _srtRequestHelper._parameters;	// Save off reference to current parameters
			popParameterStack();
			if (_srtRequestHelper._parameters==null&&_tmpParameters!=null) // Parameters above current inluce/forward were never parsed
			{
				_srtRequestHelper._parameters=_tmpParameters;
				Hashtable tmpQueryParams = ((QSListItem)_srtRequestHelper._queryStringList.getLast())._qsHashtable;
				if (tmpQueryParams==null)
				{
					tmpQueryParams = RequestUtils.parseQueryString(((QSListItem)_srtRequestHelper._queryStringList.getLast())._qs, getReaderEncoding());
				}
				removeQueryParams(tmpQueryParams);
			}
			_srtRequestHelper._queryStringList.removeLast();
		}
		else{
			//We need to pop parameter stack regardless of whether queryStringList is null
			//because the queryString parameters could have been added directly to parameter list without
			//adding ot the queryStringList
			popParameterStack();
		}
	}
	
	//Begin 249841, 256836
	public void aggregateQueryStringParams(String additionalQueryString, boolean setQS)
	{
		//321485
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
                logger.logp(Level.FINE, CLASS_NAME,"aggregateQueryStringParams", "entry qs --> " + additionalQueryString + " set --> " + String.valueOf(setQS));
            }
        	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
            	checkRequestObjectInUse();
            }
		QSListItem tmpQS=null;
		if (_srtRequestHelper._parameters == null)
		{
			if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))   //306998.15
               			logger.logp(Level.FINE, CLASS_NAME,"aggregateQueryStringParams", "The paramater stack is currently null");
			//Begin 258025, Part 2
			if (_srtRequestHelper._queryStringList==null || _srtRequestHelper._queryStringList.isEmpty())
			{
				if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))  //306998.15
               				 logger.logp(Level.FINE, CLASS_NAME,"aggregateQueryStringParams", "The queryStringList is empty");
				if (_srtRequestHelper._queryStringList==null)
					_srtRequestHelper._queryStringList = new LinkedList();
				
				if (getQueryString()!=null){
					if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))  //306998.15
                				logger.logp(Level.FINE, CLASS_NAME,"aggregateQueryStringParams", "getQueryString will be added first in the QSList wih value->"+getQueryString());
					tmpQS = new QSListItem(getQueryString(),null);
					_srtRequestHelper._queryStringList.add(tmpQS);
				}
				
			}
			//End 258025, Part 2
			if (additionalQueryString !=null){
				tmpQS = new QSListItem(additionalQueryString,null);
				_srtRequestHelper._queryStringList.add(tmpQS);
			}
		}
	    if (setQS){
	    	setQueryString(additionalQueryString);
	    }

		// if _parameters is not null, then this is part of a forward or include...add the additional query parms
		// if _parameters is null, then the string will be parsed if needed
		if (_srtRequestHelper._parameters != null && additionalQueryString != null)
		{
			Hashtable parameters = RequestUtils.parseQueryString(additionalQueryString, getReaderEncoding());
			// end 249841, 256836
			String[] valArray;
			for (Enumeration e = parameters.keys(); e.hasMoreElements();)
			{
				String key = (String) e.nextElement();
				String[] newVals = (String[]) parameters.get(key);

				// Check to see if a parameter with the key already exists
				// and prepend the values since QueryString takes precedence
				//
				if (_srtRequestHelper._parameters.containsKey(key))
				{
					String[] oldVals = (String[]) _srtRequestHelper._parameters.get(key);
					Vector v = new Vector();

					for (int i = 0; i < newVals.length; i++)
					{
						v.add(newVals[i]);
					}
					
					for (int i = 0; i < oldVals.length; i++)
					{
							// 249841, do not check to see if values already exist
							v.add(oldVals[i]);
					}

					valArray = new String[v.size()];
					v.toArray(valArray);

					_srtRequestHelper._parameters.put(key, valArray);
				}
				else
				{
					_srtRequestHelper._parameters.put(key, newVals);
				}
			}
		}
	}

	// LIDB1234.4 - added method below
	/**
	* Returns a java.util.Map of the parameters of this request.  Request parameters are extra
	* information sent with the request.  For HTTP servlets, parameters are contained in the
	* query string or posted form data.
	*
	* @return an immutable Map object containing parameter names as keys and parameter values
	*         as map values.  The keys in the parameter map are of type String.  The values
	*         in the parameter map are of type String array.
	*
	* @since Servlet 2.3
	*/
	public java.util.Map getParameterMap()
	{
            //321485
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
                logger.logp(Level.FINE, CLASS_NAME,"getParameterMap", "");
            }
        	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
            	checkRequestObjectInUse();
            }
		// if the parameters haven't been parsed, parse them
		parseParameters();

		// return the map
		return (Map) _srtRequestHelper._parameters;
	}

	// LIDB1234.4 - added method below
	/**
	 * Reconstructs the URL the client used to make the request.  The returned URL contains
	 * a protocol, server name, port number, and server path, but it does not include query
	 * string parameters.
	 * 
	 * Because this method returns a StringBuffer, not a string, you can modify the URL
	 * easily, for example, to append query parameters.
	 *
	 * This method is useful for creating redirect messages and for reporting errors.
	 *
	 * @return a StringBuffer object containing the reconstructed URL
	 *
	 * @since Servlet 2.3
	 */
	public java.lang.StringBuffer getRequestURL()
	{
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
		StringBuffer fullURL = new StringBuffer();

		// get the scheme and port up front for later use
		String scheme = getScheme();
		int port = getServerPort();

		// append the scheme and server name
		fullURL.append(scheme);
		fullURL.append("://");
		fullURL.append(getServerName());

		// append the port if not the default one for the scheme
		if ((scheme.equals("http") && port != 80) || (scheme.equals("https") && port != 443))
		{
			fullURL.append(':');
			fullURL.append(getServerPort());
		}

		// append the uri
                // PK22688 start
		//fullURL.append(getRequestURI());
		fullURL.append(getEncodedRequestURI());
		// PK22688 end
		
                            //321485
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
                logger.logp(Level.FINE, CLASS_NAME,"getRequestURL", "url --> " + fullURL);
            }

		return fullURL;
	}

	// LIDB1234.6 - added method below
	/**
	 * Overrides the name of the character encoding used in the body of this request.  This
	 * method must be called prior to reading request parameters or reading input using
	 * getReader().
	 *
	 * @param encoding a String containing the name of the character encoding
	 *
	 * @throws java.io.UnsupportedEncodingException if this is not a valid encoding
	 */

	public byte[] getSSLId()
	{
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
            //321485
            byte[] id = _request.getSSLSessionID();
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
                logger.logp(Level.FINE, CLASS_NAME,"getSSLId", " id --> " + (id!=null?new String(id):""));
            }
		return id;
	}
	
    private String getSSLSessionId() {
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
    	byte[] id = getSSLId();
    	if (id!=null)
    		return IDGeneratorImpl.convertSessionIdBytesToSessionId(getSSLId(), SessionManagerConfigBase.getSessionIDLength());
    	else
    		return null;
	}


    
        public byte[] getCookieValueAsBytes(String cookieName) {
        	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
            	checkRequestObjectInUse();
            }
            //321485
            byte[] cookieValue = _request.getCookieValue(cookieName);
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
                logger.logp(Level.FINE, CLASS_NAME,"getCookieValueAsBytes", " name --> " + cookieName + " value --> " + (cookieValue!=null?new String(cookieValue):""));
            }
                return cookieValue;
        }

	/**
     * Get the values for the cookie specified.
     * @param name the cookie name
     * @return List of values associated with this cookie name.
     */
    public List getAllCookieValues(String cookieName){

    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        List cookieValues = _request.getAllCookieValues(cookieName);
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"getAllCookieValues", " name --> " + cookieName + " values --> " + cookieValues);
        }
        return cookieValues;
    }
    

	/**
	 * Returns the cookie that is been set in this request.
	 * Need this to handle the case where session is created
	 * in a request and forwarded to another or viceversa. Applies
	 * to includes to
	 */
	public String getUpdatedSessionId()
	{
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
            //321485
            String id = _srtRequestHelper._updatedSessionId;
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
                logger.logp(Level.FINE, CLASS_NAME,"getUpdatedSessionId", " id --> " + id);
            }
		return id;
	}

	public void setSessionId(String id)
	{
            //321485
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
                logger.logp(Level.FINE, CLASS_NAME,"setSessionId", " id --> " + id);
            }
        	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
            	checkRequestObjectInUse();
            }
		_srtRequestHelper._updatedSessionId = id;
	}

        // LIDB4395 cmd start
        public Object getSessionAffinityContext()
        {
        	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
            	checkRequestObjectInUse();
            }
            //321485
            Object sac = _srtRequestHelper._sessionAffinityContext;
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
                logger.logp(Level.FINE, CLASS_NAME,"getSessionAffinityContext", " sac --> " + sac);
            }
            return sac;
        }

        public void setSessionAffinityContext(Object sac)
        {
        	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
            	checkRequestObjectInUse();
            }
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
                logger.logp(Level.FINE, CLASS_NAME,"setSessionAffinityContext", " sac --> " + sac);
            }
            _srtRequestHelper._sessionAffinityContext = sac;
        }
        // LIDB4395 cmd end
    
	public String getEncodedRequestURI()
	{
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
            //321485
            String uri = null;
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
                logger.logp(Level.FINE, CLASS_NAME,"getEncodedRequestURI", "");
            }
		if (_dispatchContext == null) 
                    uri = _request.getRequestURI();
                else 
                    uri = _dispatchContext.getRequestURI();
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
                logger.logp(Level.FINE, CLASS_NAME,"getEncodedRequestURI", " uri --> " + uri);
            }	

		return uri;
	}
	/**
	 * @return
	 */
	public SRTRequestContext getRequestContext()
	{
            //321485
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
                logger.logp(Level.FINE, CLASS_NAME,"getRequestContext", "");
            }
        	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
            	checkRequestObjectInUse();
            }
		return _requestContext;
	}

    /**
     * Returns a boolean that indicates if collaborators are
     * running.  Session Manager needs this to suppress throwing
     * UnauthorizedSessionRequestExceptions when getSession is
     * called by collaborators, rather than the application.
     * PK01801
     */
    public boolean getRunningCollaborators()
    {
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        //321485
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"getRunningCollaborators", " value --> " + String.valueOf(_runningCollaborators));
        }
        return _runningCollaborators;
    }

    public void setRunningCollaborators(boolean runningCollaborators)
    {
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        //321485
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"setRunningCollaborators", " value --> " + String.valueOf(_runningCollaborators));
        }
        this._runningCollaborators = runningCollaborators;
    }
    
    public void destroy() {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  
            logger.logp(Level.FINE, CLASS_NAME,"destroy", " entry");
        }
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        this._requestContext.destroy();
        this._requestContext = null;
        this._connContext = null;
        this._dispatchContext = null;
        this._srtRequestHelper = null;
        this._paramStack = null;
        this._in = null;
        this._setInputStreamContentType = null;   // PK57679
        this._setInputStreamContentLength = -1;   // PK57679
        this._setInputDataStreamCalled = false;   // 516233
        
        if(this._request instanceof IPoolable){
        	((IPoolable)this._request).destroy();
        }
        this._request = null;
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  
            logger.logp(Level.FINE, CLASS_NAME,"destroy", " exit");
        }
    }

    
    protected WSServletInputStream createInputStream(){
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  //306998.15
            logger.logp(Level.FINE, CLASS_NAME,"createInputStream", " entry");
        }
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        return new HttpInputStream();
    }
    

	private class SRTServletRequestHelper implements Cloneable{
        // objects requiring cloning
        //==========================
        private Hashtable _privateAttributes = null; //268366, PERF: 3% regression in PingServlet
        private LinkedList _queryStringList = null; //256836
        private Map _attributes = new HashMap();
        private Map _parameters = null;
        //==========================

        // instance variables not needing cloning
        //==========================
        private String _queryString = null;
        private boolean _cookiesParsed = false;
        private String _updatedSessionId;
        private Object _sessionAffinityContext;  // cmd LIDB4395
        private Cookie[] _cookies;
        private boolean _localesProcessed = false;
        private String _readerEncoding = null;
        private String _characterEncoding = null;
        private boolean _qsSetExplicit = false;
        private boolean _gotReader = false;
        private boolean _gotInputStream = false;
        private boolean _InputStreamClosed = false; // F003449
        private String _requestURI = null;
        private String _pathInfo = null;
        private String _method = null;
        private boolean _parametersRead = false;                            //PM03928
        private DispatcherType dispatcherType = DispatcherType.REQUEST;
        
        //==========================

        //other objects not needing cloning
        //=================================
        private BufferedReader _reader = null;
        private LinkedList _locales = null;
        
        private boolean asyncSupported=true;
        private com.ibm.wsspi.webcontainer.servlet.AsyncContext asyncContext;
        private List<AsyncListenerEntry> asyncListenerEntryList;
        public long _asyncTimeout=0;
        public boolean multipartRequestInputStreamRead = false;
        public Exception multipartException=null;
        public boolean multipartISEException=false;
        public LinkedHashMap<String, Part> multipartPartsHashMap = null;
		private boolean asyncStarted=false;
        //=================================

        protected Object clone() throws CloneNotSupportedException {
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {
                logger.logp(Level.FINE, CLASS_NAME,"clone", "SRTRequestHelper.clone entry");
            }
            SRTServletRequestHelper _clonedHelper = null;
            _clonedHelper = (SRTServletRequestHelper) super.clone();
            if(this._privateAttributes != null )
                _clonedHelper._privateAttributes = (Hashtable)_privateAttributes.clone();
            if(this._queryStringList != null)
                _clonedHelper._queryStringList = (LinkedList)_queryStringList.clone();
            if(this._parameters != null)
                _clonedHelper._parameters = (Hashtable)((Hashtable)_parameters).clone();
            if(this._attributes !=null)
                _clonedHelper._attributes = (HashMap)((HashMap)_attributes).clone();
            
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {
                logger.logp(Level.FINE, CLASS_NAME,"clone", "SRTRequestHelper.clone original -->" + this +" cloned -->" + _clonedHelper);
            }
            return _clonedHelper;
        }
	}
	//Begin 256836
    class QSListItem
	{
    	String _qs=null;
    	Hashtable _qsHashtable=null;
    	QSListItem(String qs, Hashtable qsHashtable){
    		_qs=qs;
    		_qsHashtable=qsHashtable;
    	}
	}
    //End 256836

	public static void main (String []args){
		System.out.println(getEncodingFromContentType("text/html;charset='blah';action='dude'"
				,"text/html;charset='blah';action='dude'".indexOf("charset=")));
		System.out.println(getEncodingFromContentType("text/html;charset='blah';action=dude"
				,"text/html;charset='blah';action=dude".indexOf("charset=")));
		System.out.println(getEncodingFromContentType("text/html;charset='blah'"
				,"text/html;charset='blah'".indexOf("charset=")));
		System.out.println(getEncodingFromContentType("text/html;charset=b;action='dude'"
				,"text/html;charset=b;action='dude'".indexOf("charset=")));
		System.out.println(getEncodingFromContentType("text/html;action='dude';charset='blah'"
				,"text/html;action='dude';charset='blah'".indexOf("charset=")));
	}

	public void removeHeader(String header) {
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
		this._request.removeHeader(header);
	}

    
	public AsyncContext getAsyncContext() {
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
	    if (_srtRequestHelper.asyncContext==null)
	        throw new IllegalStateException();
		return _srtRequestHelper.asyncContext;
	}

	
	public void closeResponseOutput() {
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }

	        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {
	            logger.entering(CLASS_NAME,"closeResponseOutput");
	        }
	    
	        IResponse iResponse = getResponse().getIResponse();
	        
	        getResponse().closeResponseOutput(false);
	        
	        
    	    finishAndDestroyConnectionContext();
    	    
    	    //wait until after the finishConnection is called to release the channel link.
    	    //you may need to read in the remaining bits from the input stream
    	    iResponse.releaseChannel();
    	    
    	    WebContainerRequestState reqState = WebContainerRequestState.getInstance(true);
            if (reqState.getCurrentThreadsIExtendedRequest()==this)
                WebContainerRequestState.getInstance(true).setCompleted(true);
	        
	        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {
	            logger.exiting(CLASS_NAME,"closeResponseOutput");
	        }
	        
	}

    public void finishAndDestroyConnectionContext() {
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        this._connContext.finishConnection();       
        this._connContext.destroy();
    }
	
    //Defect 632800
    //we need to keep track of the error dispatch type separately from the 
    //include or forward dispatch type so that error dispatches still behave
    //the same based off of whether it was a forward or include, but also
    //the ServletRequest.getDispatcherType must return error regardless of
    //whether include or forward was used.
    public void setDispatcherType(DispatcherType dispatcherType) {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
            logger.logp(Level.FINE, CLASS_NAME,"setDispatcherType","dispatcherType->"+dispatcherType);
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        this._srtRequestHelper.dispatcherType = dispatcherType;
    }
    public DispatcherType getDispatcherType() {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
            logger.logp(Level.FINE, CLASS_NAME,"getDispatcherType","dispatcherType->"+this._srtRequestHelper);
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        return this._srtRequestHelper.dispatcherType;
    }
    //Defect 632800

	@Override
	public ServletContext getServletContext() {
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
		return this._dispatchContext.getWebApp().getFacade();
	}

	@Override
	public boolean isAsyncStarted() {
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
	    return this._srtRequestHelper.asyncStarted;
	}

	@Override
	public boolean isAsyncSupported() {
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
		return this._srtRequestHelper.asyncSupported ;
	}
	
	@Override
    public void setAsyncSupported(boolean asyncSupported) {
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        this._srtRequestHelper.asyncSupported = asyncSupported;
    }




	@Override
	public AsyncContext startAsync() {
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
	    final IExtendedResponse iExtendedResponse = this.getResponse();
		return startAsync(this,iExtendedResponse);
	}

    @Override
	public AsyncContext startAsync(ServletRequest servletRequest,
			ServletResponse servletResponse) {
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
    
    	WebContainerRequestState reqState = WebContainerRequestState.getInstance(true);
    	
    	//NOTE: We should actually be even more strict by determining if we've committed and closed the response,
    	//but we do not commit and close the response in all cases that we should today.
    	
        if (reqState.isCompleted()||(_srtRequestHelper.asyncContext!=null&&_srtRequestHelper.asyncContext.isCompletePending())){
            throw new AsyncIllegalStateException(nls.getString("trying.to.do.startAsync.after.a.complete"));
        } else if (reqState.getCurrentThreadsIExtendedRequest()!=this){
            throw new AsyncIllegalStateException(nls.getString("not.called.from.within.the.context.of.a.dispatch.for.this.request"));
        } else if (reqState.isAsyncMode()){
        	throw new AsyncIllegalStateException(nls.getString("cannot.call.startAsync.multiple.times.within.same.dispatch"));
        } else if (!isAsyncSupported()) {
            throw new AsyncIllegalStateException(nls.getString("request.does.not.support.async.servlet.processing"));
        }
       
        //The listeners will not be added at this point any more due to spec changes
        //or they will be about ready to get cleared out of the current list of listeners.
        //Either way, they don't need to be invoked.
//        if (_srtRequestHelper.asyncListenerEntryList!=null){
//            initializeAsyncListeners(servletRequest,servletResponse);
//        }
//        
        if (_srtRequestHelper.asyncContext!=null){
        	_srtRequestHelper.asyncContext.initialize();
        }
        else {
        	_srtRequestHelper.asyncContext = AsyncContextFactory.getAsyncContextFactory().getAsyncContext(this,this.getResponse(),this.getWebAppDispatcherContext());
        }
        
        _srtRequestHelper.asyncContext.setRequestAndResponse(servletRequest, servletResponse);
        
        
        _request.startAsync();
        this.setAsyncStarted(true);
        reqState.setAsyncMode(true);
        reqState.setAsyncContext(_srtRequestHelper.asyncContext);
        
        
        return _srtRequestHelper.asyncContext;
       
	}
//    
//    private void initializeAsyncListeners(ServletRequest servletRequest,
//            ServletResponse servletResponse) {
//        for (AsyncListenerEntry asyncListenerEntry:_srtRequestHelper.asyncListenerEntryList){
//            if (!asyncListenerEntry.isInitialized()){
//                asyncListenerEntry.init(servletRequest,servletResponse);
//            }
//        }
//    }

    @Override
    public boolean authenticate(HttpServletResponse arg0) throws ServletException, IOException{
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        WebApp webApp = this._dispatchContext.getWebApp();
        return webApp.getCollaboratorHelper().getSecurityCollaborator().authenticate(this,arg0);
    }

    @Override
    public Part getPart(String arg0) throws ServletException, IOException {
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        prepareMultipart();
		return _srtRequestHelper.multipartPartsHashMap.get(arg0);
    }

    @Override
    public Collection<Part> getParts() throws ServletException, IOException {
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
    	prepareMultipart();
    	return _srtRequestHelper.multipartPartsHashMap.values();
    }
    
    private void prepareMultipart() throws ServletException, IOException {
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        if (!_srtRequestHelper.multipartRequestInputStreamRead) {
            if (_srtRequestHelper.multipartPartsHashMap==null) {
                _srtRequestHelper.multipartPartsHashMap = new LinkedHashMap<String, Part>();
            }
            if (this.getContentType()!=null && this.getContentType().startsWith("multipart/form-data")) {
                int contentLength=this.getContentLength();
                IServletConfig multiPartServletConfig = this._dispatchContext.getCurrentServletReference().getServletConfig();
                MultipartConfigElement mce = multiPartServletConfig.getMultipartConfig();
                if (mce==null) {
                    throw new UnsupportedOperationException(nls.getString("multipart.no.multipart.config"));
                }
                parseMultipart(multiPartServletConfig, mce.getFileSizeThreshold(), mce.getLocation(), mce.getMaxFileSize());
                _srtRequestHelper.multipartRequestInputStreamRead=true;
            } else {
                //since getParameter wouldn't get into this method when the request isn't multipart, we don't need to store the exception.
                throw new ServletException(nls.getString("multipart.request.not.multipart"));
            }
        }
    }
    
    private void parseMultipart(IServletConfig multiPartServletConfig, int fileThreshold, String location, long maxFileSize) throws IOException {
	if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  
            logger.logp(Level.FINE, CLASS_NAME,"parseMultipart"," "+ multiPartServletConfig.getServletName()+" ["+ fileThreshold +", "+location+ ", " + maxFileSize+" ]");
        }

    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        File uploadFile = multiPartServletConfig.getMultipartBaseLocation();
        if (uploadFile==null) {
            //handle new requirement for location to be found relative to temp dir
    	    WebApp webapp =_dispatchContext.getWebApp();
    		File tmpLocation=(File)webapp.getAttribute("javax.servlet.context.tempdir");
    	    if (location==null || location.length()==0) {
    			uploadFile = tmpLocation;
    		} else {
    		    if (System.getSecurityManager() != null) {
                    final String finalLocation=location;
                    final File finalTmpLocation=tmpLocation;
                    uploadFile = AccessController.doPrivileged(new java.security.PrivilegedAction<File>() {
                        public File run() {
                            File innerUploadFile = new File(finalLocation);
                            if (!innerUploadFile.isAbsolute()) {
                                //if it is not an absolute path, make it relative to the tmp dir
                                innerUploadFile = new File(finalTmpLocation, finalLocation);
                                if (!innerUploadFile.exists()) {
                                    //create it
                                    innerUploadFile.mkdirs();
                                }
                            }
                            return innerUploadFile;
                        }
                    });
                } else {
                    uploadFile = new File(location);
                    if (!uploadFile.isAbsolute()) {
                        //if it is not an absolute path, make it relative to the tmp dir
                        uploadFile = new File(tmpLocation, location);
                        if (!uploadFile.exists()) {
                            //create it
                            uploadFile.mkdirs();
                        }
                    }
                }   	
    		}
 	    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  
                logger.logp(Level.FINE, CLASS_NAME,"parseMultipart", "uploadFile location --> " + uploadFile.getAbsolutePath());
            }
    	    multiPartServletConfig.setMultipartBaseLocation(uploadFile);
    	}
    	DiskFileItemFactory fact = new DiskFileItemFactory(fileThreshold, uploadFile);
    	ServletFileUpload sfu = new ServletFileUpload(fact);
    	sfu.setFileSizeMax(maxFileSize);
    	List list=null;
    	try {
    	    if (_srtRequestHelper.multipartException!=null) {
    	        //this means that we previously hit an exception when parsing.  An exception might not have been propogated to the client if this code was called for getParameter/getParameters
    	        if (_srtRequestHelper.multipartISEException) {
    	            throw (IllegalStateException) _srtRequestHelper.multipartException;
    	        } else {
    	            throw (IOException) _srtRequestHelper.multipartException;
    	        }
    	    }
    	    if (System.getSecurityManager() != null) {
    	        final ServletFileUpload finalSFU = sfu;
    	        final SRTServletRequest finalThis = this;
    	        try {
                    list = AccessController.doPrivileged(new java.security.PrivilegedExceptionAction<List>() {
                        public List run() throws FileUploadException {
                            List innerList = finalSFU.parseRequest(finalThis);
                            return innerList;
                        }
                    });
                } catch (PrivilegedActionException e) {
                    //should be either a FileSizeLimitExceededException, SizeLimitExceededException, or FileUploadException
                    throw e.getException();
                }
            } else {
                list = sfu.parseRequest(this);
            }

    	    if (list!= null) {
    		//724365.2 Start
    		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  
                    	logger.logp(Level.FINE, CLASS_NAME,"parseMultipart", "size after parsing request --> " + list.size());
                }
    		int totalPartSize = 0;
    		int dupSize = 0; // 728397
	    	HashSet<Integer> key_hset = new HashSet<Integer>(); // 728397
    		for (Object fileItem:list) {
    			Part p = morphIntoPart((DiskFileItem)fileItem);  				
    			// 728397 Start 
	    		if( !(_srtRequestHelper.multipartPartsHashMap.containsKey(p.getName()))){ 
	    			if(!(key_hset.add(p.getName().hashCode()))){ 
    	            	   		dupSize++;// if false then count as duplicate hashcodes for unique keys
    	            	   		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  
    	                    			logger.logp(Level.FINE, CLASS_NAME,"parseMultipart", "duplicate hashCode generated by part --> " + p.getName());
    	            	   		}
    	            	   		if( dupSize > maxDuplicateHashKeyParams){							    	            	   		
    	            	   			throw new IllegalArgumentException(MessageFormat.format(nls.getString("Exceeding.maximum.hash.collisions"), new Object[]{maxDuplicateHashKeyParams}));
    	            	   		}
    	               		} 
	    		}
	    		// 728397 End					    				 				   								    				
    			if((maxParamPerRequest == -1) || (totalPartSize < maxParamPerRequest)){
                    		_srtRequestHelper.multipartPartsHashMap.put(p.getName(), p);
                    		totalPartSize ++;
                    	}
                    	else{	                   	
                    		throw new IllegalArgumentException(MessageFormat.format(nls.getString("Exceeding.maximum.parameters"), new Object[]{maxParamPerRequest, totalPartSize})); //724365.4
                    	}// 724365.2 End
		}
    	    }
    	}
        catch(FileSizeLimitExceededException fileSizeException) {
            _srtRequestHelper.multipartException = new IllegalStateException(nls.getString("multipart.file.size.too.big"));
            _srtRequestHelper.multipartISEException = true;
            throw (IllegalStateException)_srtRequestHelper.multipartException;
        }
        catch (SizeLimitExceededException sizeException) {
            _srtRequestHelper.multipartException = new IllegalStateException(nls.getString("multipart.request.size.too.big"));
            _srtRequestHelper.multipartISEException = true;
            throw (IllegalStateException)_srtRequestHelper.multipartException;                
        }
        catch (FileUploadException e) {
            _srtRequestHelper.multipartException = new IOException(nls.getString("multipart.file.upload.exception"), e);
            throw (IOException)_srtRequestHelper.multipartException;
        } catch (RuntimeException e) {
            throw (RuntimeException) e;
        } catch (Exception e) {
            throw new IOException("", e);
        }
    }
    
    private Part morphIntoPart(DiskFileItem commonsFile) {
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
    	Part p = new SRTServletRequestPart(commonsFile);
    	return p;
    }

    @Override
    public void login(String username, String password) throws ServletException {
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        WebApp webApp = this._dispatchContext.getWebApp();

        webApp.getCollaboratorHelper().getSecurityCollaborator().login(this,(HttpServletResponse)this.getResponse(),username,password);
        
    }

    @Override
    public void logout() throws ServletException {
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
        WebApp webApp = this._dispatchContext.getWebApp();
        
        webApp.getCollaboratorHelper().getSecurityCollaborator().logout(this,(HttpServletResponse)this.getResponse());
    }

	@Override
	public void setAsyncStarted(boolean b) {
    	if (WCCustomProperties.CHECK_REQUEST_OBJECT_IN_USE){
        	checkRequestObjectInUse();
        }
		this._srtRequestHelper.asyncStarted = b;
	}

	
}
