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
package com.ibm.ws.container.service.metadata;

public class MetaDataException extends Exception {
    private static final long serialVersionUID = 2919646540712528838L;

    public MetaDataException(String s) {
        super(s);
    }

    public MetaDataException(String s, Throwable t) {
        super(s, t);
    }

    public MetaDataException(Throwable t) {
        super(t);
    }
}
