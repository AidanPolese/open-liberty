// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.ws.webcontainer.webapp;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.ibm.websphere.servlet.event.ServletInvocationEvent;

/**
 * WebApp implmentation of the WebSphere ServletInvocationEvent.
 * This class provides the ability to set the response time of the servlet
 * via the setResponseTime() method.
 */
public class WebAppServletInvocationEvent extends ServletInvocationEvent
{
    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3257290223048995889L;
	long responseTime = -1;
    
    public WebAppServletInvocationEvent(Object source, ServletContext context, String servletName, String servletClassName, ServletRequest req, ServletResponse resp)
    {
        super(source, context, servletName, servletClassName, req, resp);
    }

    public long getResponseTime()
    {
        return responseTime;
    }
    
    /**
     * Set the response time of the request.
     */

// SHS 81242
// Changed from package scope to public to allow the JspServlet to setResponseTime
    public void setResponseTime(long time)
    {
        responseTime = time;
    }
    
}
