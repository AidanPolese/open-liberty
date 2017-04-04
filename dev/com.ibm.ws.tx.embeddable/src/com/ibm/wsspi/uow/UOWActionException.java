// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2006, 2007
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// %Z% %I% %W% %G% %U% [%H% %T%]
//
// Change History:
//
// YY-MM-DD Developer CMVC ID      Description
// -------- --------- -------      -----------
// 06-10-03 johawkes  LIDB4548-1.1 Created
// 07-06-14 awilkins  445363       Javadoc tag as IBM SPI
//
package com.ibm.wsspi.uow;

/**
 * This exception is thrown by UOWManager.runUnderUOW() to indicate that the
 * action being performed threw a checked exception. The exception thrown by
 * the action can be obtained by calling the getCause() method.
 * 
 * @see UOWManager#runUnderUOW(int, boolean, UOWAction)
 * 
 * @ibm-spi
 */
public class UOWActionException extends Exception
{
    private static final long serialVersionUID = 44035455915950660L;

    /** 
     * Constructs a new UOWActionException wrapping the given
     * Exception.
     */ 
    public UOWActionException(Exception cause)
    {
        super(cause);
    }

    /**
     * Returns the exception thrown by UOWManager.runUnderUOW() that
     * caused this exception to be thrown.
     * 
     * @see UOWManager#runUnderUOW(int, boolean, UOWAction)
     */
    public Throwable getCause()
    {
        return super.getCause();
    }
}
