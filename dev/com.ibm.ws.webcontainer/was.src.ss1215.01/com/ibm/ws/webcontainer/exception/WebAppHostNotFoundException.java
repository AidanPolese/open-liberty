// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.ws.webcontainer.exception;

import java.text.MessageFormat;

import com.ibm.ejs.ras.TraceNLS;


public class WebAppHostNotFoundException extends WebContainerException
{
    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3834595387381068080L;
	private static TraceNLS nls = TraceNLS.getTraceNLS(WebAppHostNotFoundException.class, "com.ibm.ws.webcontainer.resources.Messages");

    public WebAppHostNotFoundException(String s)
    {
        super(MessageFormat.format(nls.getString("host.has.not.been.defined","The host {0} has not been defined"), new Object[]{s}));
    }

    public WebAppHostNotFoundException(String s, String port)
    {
        super(MessageFormat.format(nls.getString("host.on.port.has.not.been.defined","The host {0} on port {1} has not been defined"), new Object[]{s, port}));
    }
    
    public WebAppHostNotFoundException (Throwable th, String s)
    {
    	super(s);
    	super.setStackTrace(th.getStackTrace());
    }
}
