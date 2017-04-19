/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.dd.ws;

import java.util.List;

import com.ibm.ws.javaee.dd.DeploymentDescriptor;
import com.ibm.ws.javaee.dd.common.DescriptionGroup;

public interface Webservices extends DeploymentDescriptor, DescriptionGroup {

    static final String WEB_DD_NAME = "WEB-INF/webservices.xml";
    static final String EJB_DD_NAME = "META-INF/webservices.xml";

    public String getVersion();

    public List<WebserviceDescription> getWebServiceDescriptions();
}
