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
import com.ibm.ws.container.service.app.deploy.ModuleInfo;

/**
 * Service for firing deployed info events to listeners.
 */
public interface StateChangeService {

    void fireApplicationStarting(ApplicationInfo info) throws StateChangeException;

    void fireApplicationStarted(ApplicationInfo info) throws StateChangeException;

    void fireApplicationStopping(ApplicationInfo info);

    void fireApplicationStopped(ApplicationInfo info);

    void fireModuleStarting(ModuleInfo info) throws StateChangeException;

    void fireModuleStarted(ModuleInfo info) throws StateChangeException;

    void fireModuleStopping(ModuleInfo info);

    void fireModuleStopped(ModuleInfo info);
}
