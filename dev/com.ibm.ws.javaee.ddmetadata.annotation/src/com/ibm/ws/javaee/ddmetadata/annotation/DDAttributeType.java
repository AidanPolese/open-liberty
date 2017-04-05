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
package com.ibm.ws.javaee.ddmetadata.annotation;

public enum DDAttributeType {
    /**
     * This type can only be used with methods that return {@link Enum}. If a
     * value is not specified in XML, null is the default value.
     */
    Enum,

    /**
     * This type can only be used with methods that return boolean. false is
     * the default value.
     */
    Boolean,

    /**
     * This type can only be used with methods that return int. 0 is the
     * default value.
     */
    Int,

    /**
     * This type can only be used with methods that return long. 0 is the
     * default value.
     */
    Long,

    /**
     * This type can only be used with methods that return {@link String},
     * null is the default value.
     */
    String,

    /**
     * This type can only be used with methods that return {@link String}.
     * null is the default value.
     */
    ProtectedString,
}
