// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//

package com.ibm.websphere.servlet.event;

import javax.servlet.ServletContext;

import com.ibm.ws.webcontainer.util.EmptyEnumeration;

/**
 * Generic servlet event.
 * 
 * @ibm-api
 */
public class ServletEvent extends ApplicationEvent{
    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3906650803920713522L;
	private String _servletName;
    private String _servletClassName;

    /**
     * ServletEvent contructor.
     * @param source the object that triggered this event.
     * @param servletName the name of the servlet that triggered the event.
     */
    public ServletEvent(Object source, ServletContext context, String servletName, String servletClassName){
        super(source, context, EmptyEnumeration.instance());
        _servletName = servletName;
        _servletClassName = servletClassName;
    }

    /**
     * Get the name of the servlet that triggered this event.
     */
    public String getServletName(){
        return _servletName;
    }

    /**
     * Get the name of the servlet class that triggered this event.
     */
    public String getServletClassName(){
        return _servletClassName;
    }
}
