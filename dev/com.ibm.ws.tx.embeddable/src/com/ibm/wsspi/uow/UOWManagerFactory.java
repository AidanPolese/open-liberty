// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2006, 2007
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
// 07-06-14 awilkins  445363       Javadoc tag as IBM SPI
// 11-11-24 johawkes  723423       Repackaging
//
package com.ibm.wsspi.uow;

import com.ibm.tx.TranConstants;
import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

/**
 * This class is a factory providing access to a stateless thread-safe UOWManager
 * instance when executing within a server but outside of a container managed
 * environment such that a JNDI lookup is not possible.
 * 
 * @ibm-spi
 */
public class UOWManagerFactory
{
    private static final TraceComponent tc = Tr.register(UOWManagerFactory.class, TranConstants.TRACE_GROUP, TranConstants.NLS_FILE);

    /**
     * Returns a stateless thread-safe UOWManager instance.
     * 
     * @return UOWManager instance
     */
    public static UOWManager getUOWManager()
    {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "getUOWManager");

        final UOWManager uowm = com.ibm.ws.uow.embeddable.UOWManagerFactory.getUOWManager();

        if (tc.isEntryEnabled())
            Tr.exit(tc, "getUOWManager", uowm);
        return uowm;
    }
}