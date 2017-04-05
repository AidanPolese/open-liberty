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
 * Thrown when a request is made for a set of statistics that is unknown.
 */
public final class StatisticsNameNotFoundException
                extends ObjectManagerException
{
    private static final long serialVersionUID = -356341809168269499L;

    protected StatisticsNameNotFoundException(Object source,
                                              String name)
    {
        super(source,
              StatisticsNameNotFoundException.class,
              new Object[] { name });
    } // StatisticsNameNotFoundException().
} // class StatisticsNameNotFoundException.
