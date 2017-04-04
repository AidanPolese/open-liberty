/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.container.service.app.deploy;

import java.util.List;

/**
 *
 */
public interface ModuleClassesContainerInfo {
    /**
     * Get the ContainerInfo for all of the classes directly or indirectly available
     * through this module.
     * 
     * @return The classes container infos for this module.
     */
    public List<ContainerInfo> getClassesContainerInfo();
}
