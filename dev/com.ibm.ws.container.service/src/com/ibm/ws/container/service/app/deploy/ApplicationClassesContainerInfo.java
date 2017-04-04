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
public interface ApplicationClassesContainerInfo {
    /**
     * Get the ContainerInfo for all of the classes directly or indirectly available
     * through the application library directory jars.
     * 
     * Note that this does not include the container infos for any of the EJB modules
     * in the application. That information is in the ModuleClassesContainerInfo
     * for those individual modules and is used when creating the Classloader for
     * the application.
     * 
     * @return The application library directory classes container infos
     */
    public List<ContainerInfo> getLibraryClassesContainerInfo();

    /**
     * Get the classes container info for each of the modules defined in this application.
     * 
     * @return
     */
    public List<ModuleClassesContainerInfo> getModuleClassesContainerInfo();
}
