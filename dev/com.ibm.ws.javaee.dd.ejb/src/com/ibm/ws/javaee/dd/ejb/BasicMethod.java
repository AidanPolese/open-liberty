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
 * Represents common elements for referencing a method by name and parameters.
 */
public interface BasicMethod
{
    /**
     * @return &lt;method-name>
     */
    String getMethodName();

    /**
     * @return &lt;method-params>, or null if unspecified
     */
    List<String> getMethodParamList();
}
