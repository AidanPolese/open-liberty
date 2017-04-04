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

import java.util.concurrent.TimeUnit;

/**
 * Represents the subelements of specifying a timeout.
 */
public interface Timeout
{
    /**
     * @return &lt;timeout>
     */
    long getTimeout();

    /**
     * @return &lt;unit>
     */
    TimeUnit getUnitValue();
}
