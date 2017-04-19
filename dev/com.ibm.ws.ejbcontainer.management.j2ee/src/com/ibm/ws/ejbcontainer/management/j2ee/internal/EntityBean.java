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

import com.ibm.websphere.management.j2ee.EntityBeanMBean;
import com.ibm.websphere.management.j2ee.J2EEManagedObject;

public class EntityBean extends J2EEManagedObject implements EntityBeanMBean {
    public EntityBean(ObjectName objectName) {
        super(objectName);
    }
}
