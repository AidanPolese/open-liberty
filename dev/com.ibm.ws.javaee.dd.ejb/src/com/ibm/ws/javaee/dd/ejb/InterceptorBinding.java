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
 * Represents &lt;interceptor-binding>.
 */
public interface InterceptorBinding
                extends Describable
{
    /**
     * @return &lt;ejb-name>
     */
    String getEjbName();

    /**
     * @return &lt;interceptor-class> as a read-only list; empty if {@link #getInterceptorOrder} is non-null
     */
    List<String> getInterceptorClassNames();

    /**
     * @return &lt;interceptor-order>, or null if unspecified or {@link #getInterceptorClassNames} is non-empty
     */
    InterceptorOrder getInterceptorOrder();

    /**
     * @return true if &lt;exclude-default-interceptors> is specified
     * @see #isExcludeDefaultInterceptors
     */
    boolean isSetExcludeDefaultInterceptors();

    /**
     * @return &lt;exclude-default-interceptors> if specified
     * @see #isSetExcludeDefaultInterceptors
     */
    boolean isExcludeDefaultInterceptors();

    /**
     * @return true if &lt;exclude-class-interceptors> is specified
     * @see #isExcludeClassInterceptors
     */
    boolean isSetExcludeClassInterceptors();

    /**
     * @return &lt;exclude-class-interceptors> if specified
     * @see #isSetExcludeClassInterceptors
     */
    boolean isExcludeClassInterceptors();

    /**
     * @return &lt;method>, or null if unspecified
     */
    NamedMethod getMethod();
}
