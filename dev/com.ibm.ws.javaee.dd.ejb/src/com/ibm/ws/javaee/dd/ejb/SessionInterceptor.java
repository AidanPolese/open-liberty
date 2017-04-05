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

import com.ibm.ws.javaee.dd.common.LifecycleCallback;

/**
 * Represents the group of methods to intercept a session bean.
 */
public interface SessionInterceptor
                extends MethodInterceptor
{
    /**
     * @return &lt;post-activate> as a read-only list
     */
    List<LifecycleCallback> getPostActivate();

    /**
     * @return &lt;pre-passivate> as a read-only list
     */
    List<LifecycleCallback> getPrePassivate();
}
