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
 * Represents &lt;stateful-timeout>.
 */
public interface StatefulTimeout
                extends Timeout
{
    /**
     * @return &lt;unit>, or TimeUnit.MINUTES if unspecified and the
     *         implementation does not require XSD validation
     */
    @Override
    TimeUnit getUnitValue();
}
