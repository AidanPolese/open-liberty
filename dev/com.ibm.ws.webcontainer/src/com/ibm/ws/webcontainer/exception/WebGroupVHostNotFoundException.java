// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.ws.webcontainer.exception;

import java.text.MessageFormat;

import com.ibm.ejs.ras.TraceNLS;

//Liberty - Change import
//import com.ibm.ejs.sm.client.ui.NLS;


public class WebGroupVHostNotFoundException extends WebContainerException {
    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 4120848863067584569L;
    // Liberty - Changed to TraceNLS
    //private static NLS nls = new NLS("com.ibm.ws.webcontainer.resources.Messages");
    private static TraceNLS nls = TraceNLS.getTraceNLS(WebGroupVHostNotFoundException.class, "com.ibm.ws.webcontainer.resources.Messages");

    public WebGroupVHostNotFoundException(String s)
    {     
        super(MessageFormat.format(nls.getString("Web.Group.VHost.Not.Found", "A WebGroup/Virtual Host to handle {0} has not been defined."), new Object[]{s}));
    }
}
