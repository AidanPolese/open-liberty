/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.clientcontainer.metadata;

import com.ibm.ws.container.service.app.deploy.ModuleInfo;
import com.ibm.ws.javaee.dd.client.ApplicationClient;
import com.ibm.ws.runtime.metadata.ModuleMetaData;

public interface ClientModuleMetaData extends ModuleMetaData {
    public ModuleInfo getModuleInfo();

    public ApplicationClient getAppClient();

}
