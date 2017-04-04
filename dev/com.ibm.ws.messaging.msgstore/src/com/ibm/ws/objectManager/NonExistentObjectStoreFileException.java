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
 */
/**
 * Thrown when an attempt is made to locate an ObjectStore file which should already exist.
 * 
 * @version @(#) 1/25/13
 * @author Andrew_Banks
 */
public final class NonExistentObjectStoreFileException
                extends ObjectManagerException
{
    private static final long serialVersionUID = 1L;

    /**
     * @param objectStore that is unable to locate the file.
     * @param fileName the ObjectStore cannot find.
     */
    protected NonExistentObjectStoreFileException(ObjectStore objectStore,
                                                  String fileName) {
        super(objectStore,
              NonExistentObjectStoreFileException.class,
              new Object[] { objectStore,
                            null,
                            fileName });
    } // NonExistentObjectStoreFileException().

    /**
     * @param objectStore that is unable to locate the file.
     * @param exception caught by the ObjectStore.
     * @param fileName the ObjectStore cannot find.
     */
    protected NonExistentObjectStoreFileException(ObjectStore objectStore,
                                                  Exception exception,
                                                  String fileName) {
        super(objectStore,
              NonExistentObjectStoreFileException.class,
              exception, new Object[] { objectStore,
                                       exception,
                                       fileName });
    } // NonExistentObjectStoreFileException().
} // class NonExistentObjectStoreFileException.