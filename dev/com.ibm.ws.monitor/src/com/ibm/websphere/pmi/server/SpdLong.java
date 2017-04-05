// IBM Confidential OCO Source Material
// 5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997, 2002
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

/** 
 * History:
 * CMVC 86523: create the file - wenjian
 * 
 * SpdLong: used for a long numeric value.
 */

package com.ibm.websphere.pmi.server;

public interface SpdLong extends SpdData {

    // set the value
    public void set(long val);

    // increment the value by 1
    public void increment();

    // increment the value by val
    public void increment(long val);

    // decrement the value by 1
    public void decrement();

    // decrement the value by val
    public void decrement(long val);

    // combine the value of this data and other data
    public void combine(SpdLong other);

}
