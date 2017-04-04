// IBM Confidential OCO Source Material
// 5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997, 2002
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

/** 
 * History:
 * CMVC 86523: create the file - wenjian
 * 
 * The interface for all PMI performance module.
 */
package com.ibm.websphere.pmi.server;

public interface PmiModuleAggregate {
    /**
     * Add a child instance
     */
    public void add(PmiModule instance);

    public void remove(PmiModule instance);

    /**
     * Add a list of SpdData from child
     */
    //public void add(SpdData[] dataList);

    public void remove(SpdData[] dataList);

    public boolean remove(SpdData data);

    /**
     * May need other remove methods in the future
     */
}
