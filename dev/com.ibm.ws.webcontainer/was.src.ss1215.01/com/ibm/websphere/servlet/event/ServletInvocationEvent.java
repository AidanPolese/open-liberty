// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
//  CHANGE HISTORY
//  Flag    Defect         Date         Modified By         Description
//--------------------------------------------------------------------------------------
//          329141         12/06/05     mmolden             61FVT SIP...Caught RuntimeException unwrapping the request 
//          330703         12/09/05     mmolden             61FVT: Check in SIP changes for unwrapping SIP request
//          
package com.ibm.websphere.servlet.event;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.logging.Logger;
import java.util.logging.Level;
import com.ibm.wsspi.webcontainer.logging.LoggerFactory;
import com.ibm.wsspi.webcontainer.util.ServletUtil;

/**
 * Event that reports information about a servlet invocation. IBM
 * 
 * @ibm-api
 */

public abstract class ServletInvocationEvent extends ServletEvent
{
protected static Logger logger = LoggerFactory.getInstance().getLogger("com.ibm.websphere.servlet.event");
	private static final String CLASS_NAME="com.ibm.websphere.servlet.event.ServletInvocationEvent";
    private ServletRequest _req;
    private ServletResponse _resp;

    /**
     * ServletInvocationEvent constructor
     */
    public ServletInvocationEvent(Object source, ServletContext context, String servletName, String servletClassName, ServletRequest req, ServletResponse resp)
    {
        super(source, context, servletName, servletClassName);
        _req = req;
        _resp = resp;
    }

    /**
     * Get the URL of this invocation.
     */
    public String getRequestURL()
    {
    	HttpServletRequest httpReq = getRequest();
    	if (httpReq==null)
    		return null;
    	else
    		return httpReq.getRequestURL().toString();
    }

    /**
     * Get the amount of time it took the servlet generate its response.
     * This time is based on the the difference between the start and finish
     * time of the service method.
     *
     * This property is only useful after the service method of the servlet has
     * finished execution, otherwise it returns -1.
     *
     * @returns the length of the service() method execution time in milliseconds.
     */
    public abstract long getResponseTime();

    /**
     * Get the request used for the servlet invocation.
     */
    public HttpServletRequest getRequest()
    {
    	//    	moved as part of 	LIDB-3598 to ServletUtil
    	/*
        ServletRequest r = _req;
        
        while (!(r instanceof HttpServletRequest))
        {
            if (r instanceof ServletRequestWrapper)
            {
                r = ((ServletRequestWrapper) r).getRequest();
            }
        }
			    
        return (HttpServletRequest) r;
        */
    	//begin 311003, 61FVT:Simple SIP request generating exception
    	ServletRequest sReq = null;
    	if (_req==null)
    		return null;
    	try {
    		sReq = ServletUtil.unwrapRequest(_req);
    	}
    	catch (RuntimeException re){
    		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
    			logger.logp(Level.FINE, CLASS_NAME,"getRequest","Caught RuntimeException unwrapping the request",re);
    		return null;
    	}
    	//end 311003, 61FVT:Simple SIP request generating exception
    	if (sReq instanceof HttpServletRequest)
    		return (HttpServletRequest) sReq;
    	else 
    		return null;
    }

    /**
     * Get the response used for the servlet invocation.
     */
    public HttpServletResponse getResponse()
    {
    	// moved as part of 	LIDB-3598 to ServletUtil
    	/*
        ServletResponse r = _resp;

        while (!(r instanceof HttpServletResponse))
        {
            if (r instanceof ServletResponseWrapper)
            {
                r = ((ServletResponseWrapper) r).getResponse();
            }
        }
			    
        return (HttpServletResponse) r;
        */
    	//begin 311003, 61FVT:Simple SIP request generating exception
    	ServletResponse sRes = null;
		if (_resp==null)
    		return null;
    	try {
    		sRes = ServletUtil.unwrapResponse(_resp);
    	}
    	catch (RuntimeException re){
    		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
    			logger.logp(Level.FINE, CLASS_NAME,"getResponse","Caught RuntimeException unwrapping the response",re);
    		return null;
    	}
    	//end 311003, 61FVT:Simple SIP request generating exception
    	if (sRes instanceof HttpServletResponse)
    		return (HttpServletResponse) sRes;
    	else
    		return null;
    }
    
    public void setRequest(HttpServletRequest req)
    {
           this._req = req;
    }
    
    public void setResponse(HttpServletResponse res)
    {
           this._resp = res;
    }

}
