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
package com.ibm.ws.jmx_test.mbeans;

import javax.management.StandardEmitterMBean;

/**
 *
 */
public class StandardStringHolderMBean extends StandardEmitterMBean {

    public StandardStringHolderMBean() {
        this(new StringHolder());
    }

    private StandardStringHolderMBean(StringHolder holder) {
        super(holder, StringHolderMBean.class, false, holder);
    }

}
