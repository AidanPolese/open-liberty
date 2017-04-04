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
 * Thrown when an unexpected condition was found, indicating an internal Logic error.
 * 
 * @param Object
 *            discovering the invalid condition and throwing the exception.
 * @param String
 *            describing the variable containing the invalid value.
 * @param String
 *            describing the invalid value.
 */
public final class InvalidConditionException
                extends ObjectManagerException
{
    private static final long serialVersionUID = 6077051984304754182L;

    protected InvalidConditionException(Object source,
                                        String variableName,
                                        String value)
    {
        super(source,
              InvalidConditionException.class,
              new Object[] { source,
                            variableName,
                            value });
    } // InvalidConditionException().
} // class InvalidConditionException.
