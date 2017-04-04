// IBM Confidential OCO Source Material
// 5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997, 2002
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

/**
 * History:
 * CMVC 86523: create the file - wenjian
 * 
 */
package com.ibm.websphere.pmi.server;

public interface SpdStat extends SpdData {

    // Add a value
    public void add(double val);

    // combine the value of this data and other data
    public void combine(SpdStat other);

}
