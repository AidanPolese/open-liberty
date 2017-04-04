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
 * Represents &lt;method-permission>.
 */
public interface MethodPermission
                extends Describable
{
    /**
     * @return true if &lt;unchecked> is specified
     */
    boolean isUnchecked();

    /**
     * @return &lt;role-name> as a read-only list (empty if {@link #isUnchecked} is true)
     */
    List<String> getRoleNames();

    /**
     * @return &lt;method> as a read-only list
     */
    List<Method> getMethodElements();
}
