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

import com.ibm.ws.container.service.app.deploy.ApplicationInfo;
import com.ibm.ws.runtime.metadata.ApplicationMetaData;

/**
 *
 */
public interface ExtendedApplicationInfo extends ApplicationInfo {

    ApplicationMetaData getMetaData();

}
