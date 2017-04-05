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

public abstract class J2EEModule extends J2EEDeployedObject implements J2EEModuleMBean {
    private final ObjectName javaVMObjectName;

    public J2EEModule(ObjectName objectName, ObjectName serverObjectName, ObjectName javaVMObjectName) {
        super(objectName, serverObjectName);
        this.javaVMObjectName = javaVMObjectName;
    }

    @Override
    public String[] getjavaVMs() {
        return new String[] { javaVMObjectName.toString() };
    }
}
