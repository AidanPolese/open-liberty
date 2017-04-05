/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.dd.ejb;

import java.util.List;

import com.ibm.ws.javaee.dd.common.Describable;
import com.ibm.ws.javaee.dd.common.JNDIEnvironmentRefsGroup;
import com.ibm.ws.javaee.dd.common.LifecycleCallback;

/**
 * Represents &lt;interceptor>.
 */
public interface Interceptor
                extends Describable,
                JNDIEnvironmentRefsGroup,
                SessionInterceptor
{
    /**
     * @return &lt;interceptor-class>
     */
    String getInterceptorClassName();

    /**
     * @return &lt;around-construct> as a read-only list
     */
    List<LifecycleCallback> getAroundConstruct();
}
