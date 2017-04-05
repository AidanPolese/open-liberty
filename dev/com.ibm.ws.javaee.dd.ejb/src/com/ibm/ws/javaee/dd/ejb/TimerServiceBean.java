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

/**
 * Represents the group of elements common to beans that support the timer
 * service.
 */
public interface TimerServiceBean
                extends EnterpriseBean
{
    /**
     * @return &lt;timeout-method>, or null if unspecified
     */
    NamedMethod getTimeoutMethod();

    /**
     * @return &lt;timer> as a read-only list
     */
    List<Timer> getTimers();
}
