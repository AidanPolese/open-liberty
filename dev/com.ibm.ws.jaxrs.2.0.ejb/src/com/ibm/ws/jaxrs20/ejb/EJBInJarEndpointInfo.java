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
package com.ibm.ws.jaxrs20.ejb;

import java.util.Set;

import com.ibm.ws.jaxrs20.metadata.EndpointInfo;

/**
 *
 */
public class EJBInJarEndpointInfo extends EndpointInfo {

    /**  */
    private static final long serialVersionUID = 7350696518624254292L;

    private String ejbModuleName = null;

    /**
     * @param servletName
     * @param servletClassName
     * @param servletMappingUrl
     * @param appClassName
     * @param appPath
     * @param providerAndPathClassNames
     */
    public EJBInJarEndpointInfo(String servletName, String servletClassName, String servletMappingUrl, String appClassName, String appPath, Set<String> providerAndPathClassNames) {
        super(servletName, servletClassName, servletMappingUrl, appClassName, appPath, providerAndPathClassNames);

    }

    public String getEJBModuleName() {
        return this.ejbModuleName;
    }

    public void setEJBModuleName(String eJBModuleName) {
        this.ejbModuleName = eJBModuleName;
    }

}
