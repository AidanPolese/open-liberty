/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.tx.admin.internal;

import com.ibm.websphere.management.j2ee.JTAResourceMBean;
import com.ibm.ws.tx.admin.TransactionServiceMBean;

public class TransactionServiceMBeanImpl implements TransactionServiceMBean, JTAResourceMBean {

    private final String objectName;

    TransactionServiceMBeanImpl(String objectName) {
        this.objectName = objectName;
    }

    /** {@inheritDoc} */
    @Override
    public String getobjectName() {
        return objectName;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isstateManageable() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isstatisticsProvider() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean iseventProvider() {
        return false;
    }
}