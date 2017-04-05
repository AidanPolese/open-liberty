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

import com.ibm.ws.javaee.dd.common.Describable;
import com.ibm.ws.javaee.dd.common.RunAs;

/**
 * Represents &lt;security-identity>.
 */
public interface SecurityIdentity
                extends Describable
{
    /**
     * @return true if &lt;use-caller-identity> is specified; false if {@link #getRunAs} returns non-null
     */
    boolean isUseCallerIdentity();

    /**
     * @return &lt;run-as>, or null if unspecified or {@link #isUseCallerIdentity} returns true
     */
    RunAs getRunAs();
}
