// IBM Confidential OCO Source Material
// 5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997, 2002
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

/** 
 * History:
 * CMVC 86523: create the file - wenjian
 * 
 * The interface for server side performance data.
 * Each data will have an ID, a create time, an enable tag, and a value.
 *  
 * Note: when a data is turned off, it could be either destroyed or disabled.
 *        Destroying an object saves space but may need synchronization between
 *        threads. Disabling object does not need synchronization but uses
 *        more memory.
 */

package com.ibm.websphere.pmi.server;

import com.ibm.websphere.pmi.PmiModuleConfig;
import com.ibm.ws.pmi.stat.*;

public interface SpdData extends java.io.Serializable {

    // return the data id
    public int getId();

    // mark the data enabled and reset the value and createTime
    public void enable(int level);

    // mark the data disabled
    public void disable();

    // return if the data is enabled
    public boolean isEnabled();

    // reset the value and create time
    public void reset();

    public void reset(boolean resetAll);

    public void setDataInfo(PmiModuleConfig moduleConfig);

    // return a wire level data
    public StatisticImpl getStatistic();

    // compare the dataId and return -1, 0, 1 for less, equal, and greater
    public int compareTo(SpdData other);

    public boolean isExternal();

    public void updateExternal();

    public boolean isAggregate();
}
