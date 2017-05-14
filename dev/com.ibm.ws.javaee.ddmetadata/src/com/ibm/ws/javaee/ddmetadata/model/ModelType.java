/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.ddmetadata.model;

/**
 * The modeled return type of an interface method.
 */
public interface ModelType {
    /**
     * The fully qualified method return type (e.g., "java.lang.String" or "int").
     */
    String getJavaTypeName();

    /**
     * The fully qualified implementation class name. This will be distinct
     * from {@link #getJavaTypeName} if this type represents an interface.
     */
    String getJavaImplTypeName();

    /**
     * The fully qualified implementation class name for a list.
     */
    String getJavaListImplTypeName();

    /**
     * The default value if the element or attribute is not specified.
     *
     * @param string the default value specified for the specific method
     */
    String getDefaultValue(String string);
}
