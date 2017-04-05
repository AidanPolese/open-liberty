// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//

package com.ibm.websphere.servlet.event;


/**
 * Event listener interface used for notifications about the application.
 * @ibm-api
 */
public interface ApplicationListener extends java.util.EventListener{

    /**
     * Triggered when the application is started.
     * This event is triggered before any object initializations occur within the application
     * (including auto-start servlet initialization).  This method is the perfect place for
     * applications to register for other events and to setup the application before any other
     * objects are created by the application.
     * 
     */
    public void onApplicationStart(ApplicationEvent evt);

    /**
     * Final application event that occurs before the application is terminated by the server process.
     */
    public void onApplicationEnd(ApplicationEvent evt);

    /**
     * Triggered when the application is activated to receive external requests.
     */
    public void onApplicationAvailableForService(ApplicationEvent evt);

    /**
     * Triggered when the application is taken offline.
     * When an application is taken offline, all requests to the application
     * will be denied.
     */
    public void onApplicationUnavailableForService(ApplicationEvent evt);
}

