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

import com.ibm.ws.javaee.dd.common.Describable;

/**
 * Represents &lt;cmr-field>.
 */
public interface CMRField
                extends Describable
{
    /**
     * Represents an unspecified value for {@link #getTypeValue}.
     */
    int TYPE_UNSPECIFIED = -1;

    /**
     * Represents "java.util.Collection" for {@link #getTypeValue}.
     */
    int TYPE_JAVA_UTIL_COLLECTION = 0;

    /**
     * Represents "java.util.Set" for {@link #getTypeValue}.
     */
    int TYPE_JAVA_UTIL_SET = 1;

    /**
     * @return &lt;cmr-field-name>
     */
    String getName();

    /**
     * @return &lt;cmr-field-type>
     *         <ul>
     *         <li>{@link #TYPE_UNSPECIFIED} if unspecified
     *         <li>{@link #TYPE_JAVA_UTIL_COLLECTION} - java.util.Collection
     *         <li>{@link #TYPE_JAVA_UTIL_SET} - java.util.Set
     *         </ul>
     */
    int getTypeValue();
}
