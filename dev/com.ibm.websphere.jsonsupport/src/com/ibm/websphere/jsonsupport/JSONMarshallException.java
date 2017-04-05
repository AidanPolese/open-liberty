/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.jsonsupport;

/**
 * The JSONMarshallException is thrown when an error is encountered when
 * marshalling or unmarshalling a POJO to or from JSON.
 */
public class JSONMarshallException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Creates a JSONMarshallException with an explanation as to why the JSON
     * parse failed.
     * 
     * @param msg Explanation of parse exception
     */
    public JSONMarshallException(final String msg) {
        super(msg);
    }

    /**
     * Creates a JSONMarshallException with an explanation and cause as to why
     * the JSON parse failed.
     * 
     * @param msg Explanation of parse exception
     * @param t The cause of the Exception
     */
    public JSONMarshallException(final String msg, final Throwable t) {
        super(msg, t);
    }

}
