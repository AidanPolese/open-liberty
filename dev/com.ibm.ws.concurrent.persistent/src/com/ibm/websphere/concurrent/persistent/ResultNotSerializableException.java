/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014,2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.concurrent.persistent;

/**
 * Thrown when an attempt is made to access a task result that does not implement
 * the <code>Serializable</code> interface.
 * The argument should be the name of the class.
 */
public class ResultNotSerializableException extends RuntimeException {
    private static final long serialVersionUID = 4860312564481081289L;

    /**
     * Constructs a ResultNotSerializableException for the specified class name.
     * 
     * @param className Class of the instance being serialized/deserialized.
     */
    public ResultNotSerializableException(String className) {
        super(className);
    }
}