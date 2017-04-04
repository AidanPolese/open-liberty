/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.container.service.state;

import com.ibm.ws.container.service.app.deploy.ApplicationInfo;

public interface ApplicationStateListener {

    /**
     * Notification that an application is starting.
     * 
     * @param appInfo The ApplicationInfo of the app
     */
    void applicationStarting(ApplicationInfo appInfo) throws StateChangeException;

    /**
     * Notification that an application has started.
     * 
     * @param appInfo The ApplicationInfo of the app
     */
    void applicationStarted(ApplicationInfo appInfo) throws StateChangeException;

    /**
     * Notification that an application is stopping.
     * 
     * @param appInfo The ApplicationInfo of the app
     */
    void applicationStopping(ApplicationInfo appInfo);

    /**
     * Notification that an application has stopped.
     * 
     * @param appInfo The ApplicationInfo of the app
     */
    void applicationStopped(ApplicationInfo appInfo);
}
