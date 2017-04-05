/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011, 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.dd.common;

import java.util.List;

/**
 * Represents elements that contain 0-to-many &lt;description> elements.
 */
public interface Describable
{
    /**
     * @return &lt;description> as a read-only list
     */
    List<Description> getDescriptions();
}
