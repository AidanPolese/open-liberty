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
 * <p>
 * This class provides a generic indication of failure of the completion of a
 * unit of work (UOW) begun by a call to the runUnderUOW method on the UOWManager
 * interface. The UOW-specific cause of the failure can be obtained via the
 * getCause() method.
 * </p>
 * @see UOWManager#runUnderUOW(int, boolean, UOWAction)
 * @ibm-spi
 */
public class UOWException extends Exception
{
    private static final long serialVersionUID = 48189790854141828L;

    /**
     * <p>
     * Creates a new UOWException with the given UOW-specific exception
     * as the cause.
     * </p>
     * @param cause The UOW-specific cause
     */
    public UOWException(Throwable cause)
    {
        super(cause);
    }

    /**
     * <p>
     * Returns the UOW-specific exception that describes the nature of
     * the completion failure.
     * </p>
     * 
     * @return The UOW-specific exception associated with the failure.
     */
    public Throwable getCause()
    {
        return super.getCause();
    }
}
