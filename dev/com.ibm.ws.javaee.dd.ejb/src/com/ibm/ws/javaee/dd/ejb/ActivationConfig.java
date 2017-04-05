/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.dd.ejb;

import java.util.List;

import com.ibm.ws.javaee.dd.common.Describable;

/**
 * Represents &lt;activation-config>.
 */
public interface ActivationConfig
                extends Describable
{
    /**
     * @return &lt;activation-config-property> as a read-only list
     */
    List<ActivationConfigProperty> getConfigProperties();
}
