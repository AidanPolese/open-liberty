/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.webcontainer.management.j2ee.internal;

import javax.management.ObjectName;

import com.ibm.websphere.management.j2ee.J2EEModule;
import com.ibm.websphere.management.j2ee.WebModuleMBean;
import com.ibm.ws.javaee.ddmodel.DDReader;
import com.ibm.wsspi.adaptable.module.Container;

public class WebModule extends J2EEModule implements WebModuleMBean {

    private final ObjectName[] servletObjectNames;
    private final Container container;
    private final String ddPath;

    /**
     * @param objectName
     * @param serverObjectName
     * @param javaVMObjectName
     * @param container
     * @param ddPath
     * @param servletObjectNames
     */
    public WebModule(ObjectName objectName, ObjectName serverObjectName, ObjectName javaVMObjectName, Container container, String ddPath, ObjectName[] servletObjectNames) {
        super(objectName, serverObjectName, javaVMObjectName);
        this.container = container;
        this.ddPath = ddPath;
        this.servletObjectNames = servletObjectNames;
    }

    /** {@inheritDoc} */
    @Override
    public String[] getservlets() {
        int servletSize = servletObjectNames.length;
        String[] servlets = new String[servletSize];
        for (int i = 0; i < servletSize; i++) {
            servlets[i] = servletObjectNames[i].toString();
        }
        return servlets;
    }

    /** {@inheritDoc} */
    @Override
    public String getdeploymentDescriptor() {
        return DDReader.read(container, ddPath);
    }

}
