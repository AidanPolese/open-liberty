/*
* IBM Confidential
*
* OCO Source Materials
*
* WLP Copyright IBM Corp. 2014
*
* The source code for this program is not published or otherwise divested 
* of its trade secrets, irrespective of what has been deposited with the 
* U.S. Copyright Office.
*/
package com.ibm.ws.session;

/**
 *
 */
public interface SessionManagerMBean {

    /**
     * A String representing the {@link javax.management.ObjectName} that this MBean maps to.
     */
    String OBJECT_NAME = "WebSphere:name=com.ibm.ws.jmx.mbeans.sessionManagerMBean";

    /**
     * The unique identifier for this server among a group of other servers.
     * 
     * @return a unique server identifier
     */
    public String getCloneID();
    
    /**
     * Gets the character used to delimit clone IDs in session cookies.
     * Usually either ':' or '+' will be returned.
     * 
     * @return the character used to delimit clone IDs in session cookies
     */
    public String getCloneSeparator();

    /**
     * Gets the default name of session cookies. Note that this value can be
     * overridden by individual applications at run time. Whenever possible,
     * the exact cookie name should be read from the ServletContext instead
     * of this value.
     * 
     * @return the default name of session cookies
     */
    public String getCookieName();  
}
