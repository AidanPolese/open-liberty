// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.websphere.servlet.event;


import javax.servlet.*;
import java.util.Enumeration;

/**
 * @ibm-api
 * 
 * This is the event class that is furnished to the listeners that register to 
 * listen to Application related events.
 * 
 * @see ApplicationListener
 */
@SuppressWarnings("unchecked")
public class ApplicationEvent extends java.util.EventObject
{
    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3546927969739289392L;
	private ServletContext _context;
    private Enumeration    _servletNames;

    /**
     * ApplicationEvent contructor.
     * @param source the object that triggered this event.
     * @param context the application's ServletContext
     * @param servletNames an enumeration of the names of all of the servlets in the application
     */
    public ApplicationEvent(Object source, ServletContext context, Enumeration servletNames)
    {
        super(source);
        _context = context;
        _servletNames = servletNames;
    }

    /**
     * Return the ServletContext that this event is associated with.
     */
    public ServletContext getServletContext()
    {
        return _context;
    }

    /**
     * Return the list of servlet names associated with this application
     **/
    public Enumeration getServletNames()
    {
        return _servletNames;
    }
}
