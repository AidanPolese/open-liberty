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
package com.ibm.ws.ejbcontainer.management.j2ee.internal;

import javax.management.ObjectName;

import com.ibm.websphere.management.j2ee.EJBModuleMBean;
import com.ibm.websphere.management.j2ee.J2EEModule;
import com.ibm.ws.javaee.ddmodel.DDReader;
import com.ibm.wsspi.adaptable.module.Container;

public class EJBModule extends J2EEModule implements EJBModuleMBean {
    private final ObjectName[] ejbObjectNames;
    private final Container container;
    private final String ddPath;

    public EJBModule(ObjectName objectName,
                     ObjectName serverObjectName,
                     ObjectName jvmObjectName,
                     Container container,
                     String ddPath,
                     ObjectName[] ejbObjectNames) {
        super(objectName, serverObjectName, jvmObjectName);
        this.container = container;
        this.ddPath = ddPath;
        this.ejbObjectNames = ejbObjectNames;
    }

    @Override
    public String getdeploymentDescriptor() {
        return DDReader.read(container, ddPath);
    }

    @Override
    public String[] getejbs() {
        String[] ejbs = new String[ejbObjectNames.length];
        for (int i = 0; i < ejbObjectNames.length; i++) {
            ejbs[i] = ejbObjectNames[i].toString();
        }
        return ejbs;
    }
}
