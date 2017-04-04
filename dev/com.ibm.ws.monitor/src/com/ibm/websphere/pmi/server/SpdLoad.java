// IBM Confidential OCO Source Material
// 5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997, 2002
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

/**
 * History:
 * CMVC 86523: create the file - wenjian
 */

package com.ibm.websphere.pmi.server;

public interface SpdLoad extends SpdData {

    // Add a value
    public void add(double val);

    // increment the lastValue by incVal (default is 1)
    public void increment(double incVal);

    public void increment();

    // decrement the lastValue by incVal (default is 1)
    public void decrement(double incVal);

    public void decrement();

    // set lower and upper bound for the config data
    public void setConfig(long minSize, long maxSize);

    // combine the value of this data and other data
    public void combine(SpdLoad other);

    // clean up lastValue and integral but leave createTime unchanged
    public void cleanup();
}
