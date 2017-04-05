/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2006
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.metadata;

/**
 * This exception is thrown to indicate a severe error has occurred
 * during metadata processing
 */

public class MetaDataException
                extends RuntimeException
{
    private static final long serialVersionUID = 6561900330766430495L;

    /**
     * Create a new <code>MetaDataException</code> instance. <p>
     */

    public MetaDataException(String message) {
        super(message);
    } // MetaDataException

    public MetaDataException(String message, Throwable cause) {
        super(message, cause);
    } // MetaDataException

    public MetaDataException(Throwable cause) {
        super(cause);
    } // MetaDataException

} // MetaDataException
