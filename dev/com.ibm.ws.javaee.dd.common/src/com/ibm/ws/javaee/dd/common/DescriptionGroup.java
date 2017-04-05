/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.dd.common;

import java.util.List;

/**
 * Represents the descriptionGroup type from the javaee XSD.
 */
public interface DescriptionGroup
                extends Describable
{
    /**
     * @return &lt;display-name> as a read-only list
     */
    List<DisplayName> getDisplayNames();

    /**
     * @return &lt;icon> as a read-only list
     */
    List<Icon> getIcons();
}
