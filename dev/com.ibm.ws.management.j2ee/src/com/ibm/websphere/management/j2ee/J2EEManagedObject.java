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

public class J2EEManagedObject implements J2EEManagedObjectMBean {
    private final ObjectName objectName;

    public J2EEManagedObject(ObjectName objectName) {
        this.objectName = objectName;
    }

    @Override
    public String getobjectName() {
        return objectName.toString();
    }

    @Override
    public boolean isstateManageable() {
        return false;
    }

    @Override
    public boolean isstatisticsProvider() {
        return false;
    }

    @Override
    public boolean iseventProvider() {
        return false;
    }
}
