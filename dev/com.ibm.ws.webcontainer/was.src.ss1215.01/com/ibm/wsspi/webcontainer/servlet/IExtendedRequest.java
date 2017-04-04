// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
//CHANGE HISTORY
//Defect        Date        Modified By         Description
//--------------------------------------------------------------------------------------
// 306736.4      11/28/05    todkap              handle duplicate JSESSIONID in the request    WASCC.web.webcontainer
// 335400        01/03/06    todkap              Servlet Spec Violation:javax.servlet.include.query_string fails    WAS.rrd    
// 262147        04/25/06    mmolden             POST PARAMETERS NOT PRESERVED IN FORM LOGION    WAS.security
//

package com.ibm.wsspi.webcontainer.servlet;

import java.util.List;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import com.ibm.websphere.servlet.request.IRequest;
import com.ibm.ws.webcontainer.async.AsyncListenerEntry;
import com.ibm.wsspi.webcontainer.webapp.IWebAppDispatcherContext;

/**
 * 
 * 
 * IExtendedRequest is an spi for websphere additions to the standard
 * ServletRequest methods
 * 
 * @ibm-private-in-use
 * 
 * @since WAS7.0
 * 
 */
public interface IExtendedRequest extends HttpServletRequest {
    // public IProtocolHeader getRawHeaders();
    // public Hashtable getRawParameters();
    // public void setRawHeaders(IProtocolHeader h);
    // public void setRawParameters(Hashtable h);
    // public void setMethod(String method);

    // Httpsession helper methods
    /**
     * Returns incoming SSL session id of the request. Applicable only for
     * requests over ssl
     */
    public byte[] getSSLId();

    /**
     * Returns a cookie value as bytes
     */
    byte[] getCookieValueAsBytes(String cookieName);

    /**
     * Get the values for the cookie specified.
     * 
     * @param name
     *            the cookie name
     * @return List of values associated with this cookie name.
     */
    @SuppressWarnings("unchecked")
    List getAllCookieValues(String cookieName);

    /**
     * Sets sessionId that is being generated for this request
     */
    void setSessionId(String id);

    /**
     * returns sessionId that is being generated for this request
     */
    String getUpdatedSessionId();

    /**
     * Sets SessionAffinityContext for this request
     * 
     * @param SessionAffinityContext
     *            object
     */
    void setSessionAffinityContext(Object sac); // cmd LIDB4395

    /**
     * Get the SessionAffinityContext for this request
     * 
     * @return SessionAffinityContext object
     */
    Object getSessionAffinityContext(); // cmd LIDB4395

    /**
     * returns url with encoded session information of the incoming request
     */
    String getEncodedRequestURI();

    public void pushParameterStack();

    public void aggregateQueryStringParams(String additionalQueryString, boolean setQS);

    public void removeQSFromList();

    /**
     * @return
     */
    public String getQueryString();

    public void setQueryString(String qs);

    /**
     * Sets boolean used to indicate to session manager if collaborators are
     * running.
     */
    void setRunningCollaborators(boolean runningCollaborators); // PK01801

    /**
     * Returns boolean that indicates if collaborators are running. Used by
     * session manager when session security integration is enabled.
     */
    boolean getRunningCollaborators(); // PK01801

    public String getReaderEncoding();

    public IRequest getIRequest();

    public void attributeAdded(String key, Object newVal);

    public void attributeRemoved(String key, Object oldVal);

    public void attributeReplaced(String key, Object oldVal);

    // PQ94384
    public void addParameter(String name, String[] values);

    // PQ94384

    public void setMethod(String method);

    public void setWebAppDispatcherContext(IWebAppDispatcherContext ctx);

    public IWebAppDispatcherContext getWebAppDispatcherContext();

    public IExtendedResponse getResponse();

    public void setResponse(IExtendedResponse extResp);

    public void initForNextRequest(IRequest req);

    public void start();

    public void finish() throws ServletException;

    public void destroy();

    public String getPathInfo();

    public String getRequestURI();

    public void removeHeader(String header);

    public AsyncContext getAsyncContext();

    public void closeResponseOutput();

    public void setAsyncSupported(boolean asyncSupported);

    void finishAndDestroyConnectionContext();

	public void setDispatcherType(DispatcherType dispatcherType);

	public void setAsyncStarted(boolean b);

}
