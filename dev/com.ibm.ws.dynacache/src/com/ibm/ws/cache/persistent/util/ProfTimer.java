// 1.4, 9/30/04
// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997, 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.cache.persistent.util;

import java.util.concurrent.TimeUnit;

/**************************************************************************
 * Simple class for use in timing operations.
 *************************************************************************/
public class ProfTimer
{
    long start = 0;
    public ProfTimer()
    {
        reset();
    }

    public long elapsed()
    {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
    }

    public void reset()
    {
        start = System.nanoTime();
    }

}

