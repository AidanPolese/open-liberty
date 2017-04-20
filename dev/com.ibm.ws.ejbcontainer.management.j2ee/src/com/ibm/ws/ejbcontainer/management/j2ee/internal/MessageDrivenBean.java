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

import com.ibm.websphere.management.j2ee.J2EEManagedObject;
import com.ibm.websphere.management.j2ee.MessageDrivenBeanMBean;

public class MessageDrivenBean extends J2EEManagedObject implements MessageDrivenBeanMBean {
    public MessageDrivenBean(ObjectName objectName) {
        super(objectName);
    }
}
