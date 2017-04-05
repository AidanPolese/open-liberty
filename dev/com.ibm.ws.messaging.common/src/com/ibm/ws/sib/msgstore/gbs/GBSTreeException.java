/*
 * 
 * 
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * Copyright IBM Corp. 2012
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * 
 *
 * Change activity :
 *
 * Reason          Date      Origin    Description
 * --------------  --------  --------  ----------------------------------------
 *   176001        090903    corrigk   Original
 * LIDB3706-5.241  19/01/05  gareth    Add Serialization support
 * ============================================================================
 */
package com.ibm.ws.sib.msgstore.gbs;

/**
 * An exception that is thrown during a GBSTree operation when a
 * Kprogramming error is detected.
 *
 * @author Stewart L. Palmer
 */

public class GBSTreeException extends RuntimeException
{
    private static final long serialVersionUID = -8662457232975171206L;

    /**
     * Construct
     */
    GBSTreeException()
    {
        super();
    }

    /**
     * Construct with an error message
     */
    GBSTreeException(
                    String      msg)
    {
        super(msg);
    }

    /**
     * Construct with a String and a causing Exception
     */
    GBSTreeException(
                    String      msg,
                    Throwable   cause)
    {
        super(msg, cause);
    }
}
