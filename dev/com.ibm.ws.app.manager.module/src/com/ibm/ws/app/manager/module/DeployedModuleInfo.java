/*
* IBM Confidential
*
* OCO Source Materials
*
* WLP Copyright IBM Corp. 2016
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.ws.app.manager.module;

import com.ibm.ws.container.service.app.deploy.extended.ExtendedModuleInfo;

/**
 *
 */
public interface DeployedModuleInfo {
    ExtendedModuleInfo getModuleInfo();

    void setIsStarting();

    void setIsStarted();

    boolean isStarted();

    boolean isStarting();
}
