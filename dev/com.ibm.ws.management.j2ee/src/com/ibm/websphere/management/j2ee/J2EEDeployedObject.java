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
package com.ibm.websphere.management.j2ee;

import javax.management.ObjectName;

public abstract class J2EEDeployedObject extends J2EEManagedObject implements J2EEDeployedObjectMBean {
    private final ObjectName serverObjectName;

    J2EEDeployedObject(ObjectName objectName, ObjectName serverObjectName) {
        super(objectName);
        this.serverObjectName = serverObjectName;
    }

    @Override
    public abstract String getdeploymentDescriptor();

    @Override
    public String getserver() {
        return serverObjectName.toString();
    }
}
