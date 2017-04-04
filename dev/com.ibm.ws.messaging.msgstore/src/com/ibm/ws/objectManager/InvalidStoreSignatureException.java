package com.ibm.ws.objectManager;

/*
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-H88, 5724-J08, 5724-I63, 5655-W65, 5724-H89, 5722-WE2   Copyright IBM Corp., 2013
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 *
 *
 * Change activity:
 *
 *  Reason           Date    Origin     Description
 * --------------- -------- ---------- ----------------------------------------
 * ============================================================================
 */

/**
 * Thrown when a file contains an ObjectStorean attempt is made to store an invalid object.
 * 
 * @param ObjectStore throwing the exception.
 * @param String the signature found.
 * @param String the signature expected.
 */
public final class InvalidStoreSignatureException
                extends ObjectManagerException
{
    private static final long serialVersionUID = 5386326610925319343L;

    protected InvalidStoreSignatureException(ObjectStore source,
                                             String signatureFound,
                                             String signatureExpected)
    {
        super(source,
              InvalidStoreSignatureException.class,
              new Object[] { source.getName(),
                            signatureFound, signatureExpected });
    } // InvalidStoreSignatureException(). 
} // class InvalidStoreSignatureException.
