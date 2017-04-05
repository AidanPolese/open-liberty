package com.ibm.ws.objectManager.utils;

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
 * ============================================================================
 */
/**
 * @author Andrew_Banks
 * 
 *         Wrapper for Exception.
 */
public abstract class UtilsException
                extends Exception
{
    /**
     * @param message NLS message.
     */
    public UtilsException(String message)
    {
        super(message);
    }

    /**
     * @param message NLS message.
     * @param cause undelying exception causing this one.
     */
    public UtilsException(String message,
                          Throwable cause) {
        super(message,
              cause);
    }

    /**
     * @param cause undelying exception causing this one.
     */
    public UtilsException(Throwable cause)
    {
        super(cause);
    }

} // class UtilsException.
