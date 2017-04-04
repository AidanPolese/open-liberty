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

import com.ibm.ws.javaee.dd.common.InterceptorCallback;
import com.ibm.ws.javaee.dd.common.JNDIEnvironmentRefsGroup;

/**
 * Represents the group of &lt;post-construct>, &lt;pre-destroy,
 * &lt;around-invoke> and &lt;around-timeout> elements.
 */
public interface MethodInterceptor
                extends JNDIEnvironmentRefsGroup
{
    /**
     * @return &lt;around-invoke> as a read-only list
     */
    List<InterceptorCallback> getAroundInvoke();

    /**
     * @return &lt;around-timeout> as a read-only list
     */
    List<InterceptorCallback> getAroundTimeoutMethods();
}
