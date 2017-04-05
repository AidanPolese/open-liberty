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
 *   251161        07/04/05   gareth    Add ObjectManager code to CMVC
 * ============================================================================
 */

/**
 * Thrown when the object manager tries to read a class that it cannot load.
 */
public final class ClassNotFoundException
                extends ObjectManagerException {

    private static final long serialVersionUID = 4500305405705255053L;

    /**
     * ClassNotFoundException.
     * 
     * @param Class of static which throws this ClassNotFoundException.
     * @param java.lang.ClassNotFoundException which was caught.
     */
    protected ClassNotFoundException(Class sourceClass,
                                     java.lang.ClassNotFoundException classNotFoundException)
    {
        super(sourceClass,
              ClassNotFoundException.class,
              classNotFoundException,
              classNotFoundException);
    } // ClassNotFoundException().

    /**
     * ClassNotFoundException.
     * 
     * @param Object which is throwing this ClassNotFoundIOException.
     * @param java.io.ClassNotFoundException which was caught.
     */
    protected ClassNotFoundException(Object source,
                                     java.lang.ClassNotFoundException classNotFoundException)
    {
        super(source,
              ClassNotFoundException.class,
              classNotFoundException,
              classNotFoundException);
    } // ClassNotFoundException(). 
} // End of class ClassNotFoundException.
