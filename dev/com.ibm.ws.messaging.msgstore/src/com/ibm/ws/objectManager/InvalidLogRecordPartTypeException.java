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
 * Thrown when a Log Record part with an unrecognised type is read.
 * 
 * @param LogInput throwing the exception.
 * @param byte the invalid part type.
 */
public final class InvalidLogRecordPartTypeException
                extends ObjectManagerException
{
    private static final long serialVersionUID = 3919536104502610744L;

    protected InvalidLogRecordPartTypeException(LogInput source,
                                                byte partType)
    {
        super(source,
              InvalidLogRecordPartTypeException.class,
              new Byte(partType));

    } // InvalidLogRecordPartTypeException(). 
} // class InvalidLogRecordTypeException.
