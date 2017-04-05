// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 2006, 2007
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// %Z% %I% %W% %G% %U% [%H% %T%]
//
// Change History:
//
// YY/MM/DD Developer CMVC ID      Description
// -------- --------- -------      -----------
// 06/10/03 johawkes  LIDB4548-1.1 Created
// 07-01-22 awilkins  414406.1     Update run method's javadoc
// 07-06-14 awilkins  445363       Javadoc tag as IBM SPI
//
package com.ibm.wsspi.uow;

/**
 * A piece of logic to be performed under a particular type of unit of work. The work
 * is performed by invoking <code>UOWManager.runUnderUOW</code> and providing this
 * action as a parameter.
 * 
 * @see UOWManager#runUnderUOW(int, boolean, ExtendedUOWAction, Class[], Class[])
 * 
 * @ibm-spi
 */
public interface ExtendedUOWAction
{
    /**
     * Invoked as a result of an invocation of <code>UOWManager.runUnderUOW</code>
     * once the requested UOW has been established on the thread.
     * 
     * @throws Exception
     * 
     * @see UOWManager#runUnderUOW(int, boolean, ExtendedUOWAction, Class[], Class[])
     */
    public Object run() throws Exception;
}
