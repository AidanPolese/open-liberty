/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2017
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.websphere.rest.api.discovery;

/**
 * This MBean exposes the URLs related to the RESTful API Discovery, such as the documentation URL and the UI explorer URL.
 * These values are exposed as attributes of this MBean.
 *
 */
public interface APIDiscoveryMBean {

    /**
     * A string representing the {@link javax.management.ObjectName} that this MBean maps to.
     */
    String OBJECT_NAME = "WebSphere:feature=apiDiscovery,name=APIDiscovery";

    String getDocumentationURL();

    String getProtectedDocumentationURL();

    String getPrivateDocumentationURL();

    String getExplorerURL();

    String getProtectedExplorerURL();

    String getPrivateExplorerURL();

    String getCorsConfig();
}
