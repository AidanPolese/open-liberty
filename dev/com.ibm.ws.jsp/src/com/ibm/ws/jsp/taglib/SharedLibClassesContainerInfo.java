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
package com.ibm.ws.jsp.taglib;

import java.util.List;

import com.ibm.ws.container.service.app.deploy.ContainerInfo;

/**
 *
 */
public interface SharedLibClassesContainerInfo {
    public List<ContainerInfo> getSharedLibraryClassesContainerInfo();
    public List<ContainerInfo> getCommonLibraryClassesContainerInfo();
}
