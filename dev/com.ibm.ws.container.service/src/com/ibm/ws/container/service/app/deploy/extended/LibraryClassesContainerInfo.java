/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.container.service.app.deploy.extended;

import java.util.List;

import com.ibm.ws.container.service.app.deploy.ContainerInfo;

/**
 *
 */
public interface LibraryClassesContainerInfo extends LibraryContainerInfo {
    /**
     * Get the ContainerInfo for all of the classes directly or indirectly available
     * through this library.
     * 
     * @return The classes container infos for this module.
     */
    public List<ContainerInfo> getClassesContainerInfo();
}
