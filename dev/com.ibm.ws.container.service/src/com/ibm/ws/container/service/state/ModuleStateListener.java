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

import com.ibm.ws.container.service.app.deploy.ModuleInfo;

public interface ModuleStateListener {

    /**
     * Notification that a module is starting.
     * 
     * @param moduleInfo The ModuleInfo of the module
     */
    void moduleStarting(ModuleInfo moduleInfo) throws StateChangeException;

    /**
     * Notification that a module has started.
     * 
     * @param moduleInfo The ModuleInfo of the module
     */
    void moduleStarted(ModuleInfo moduleInfo) throws StateChangeException;

    /**
     * Notification that a module is stopping.
     * 
     * @param moduleInfo The ModuleInfo of the module
     */
    void moduleStopping(ModuleInfo moduleInfo);

    /**
     * Notification that a module has stopped.
     * 
     * @param moduleInfo The ModuleInfo of the module
     */
    void moduleStopped(ModuleInfo moduleInfo);
}
