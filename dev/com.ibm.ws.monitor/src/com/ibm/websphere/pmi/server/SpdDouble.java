// IBM Confidential OCO Source Material
// 5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997, 2002
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

/** 
 * History:
 * CMVC 86523: create the file - wenjian
 * 
 * SpdDouble: used for a long numeric value.
 */

package com.ibm.websphere.pmi.server;

public interface SpdDouble extends SpdData {

    // set the value
    public void set(double val);

    // increment the value by 1
    public void increment();

    // increment the value by val
    public void increment(double val);

    // decrement the value by 1
    public void decrement();

    // decrement the value by val
    public void decrement(double val);

    // combine the value of this data and other data
    public void combine(SpdDouble other);

}
