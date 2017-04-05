// IBM Confidential OCO Source Material
// 5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997, 2002
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

/** 
 * History:
 * CMVC 86523: create the file - wenjian, 09/18/2000
 * 
 * The class implements SpdLong interface and holds a long value.
 *  
 */
package com.ibm.ws.pmi.server.data;

import com.ibm.websphere.pmi.PmiModuleConfig;
import com.ibm.websphere.pmi.server.SpdLong;
import com.ibm.ws.pmi.stat.CountStatisticImpl;
import com.ibm.ws.pmi.stat.StatisticImpl;

/**
 * SpdLong: contains a long numeric value.
 */
public class SpdLongImpl extends SpdDataImpl
                        implements SpdLong {
    private static final long serialVersionUID = 6081293933019198051L;
    protected CountStatisticImpl stat = null;

    public SpdLongImpl(PmiModuleConfig moduleConfig, String name) {
        super(moduleConfig, name);
        stat = new CountStatisticImpl(dataId);
    }

    public SpdLongImpl(int id) {
        super(id);
        stat = new CountStatisticImpl(dataId);
    }

    // set the value
    public void set(long val) {
        stat.setCount(val);
    }

    // increment the value by 1
    public void increment() {
        stat.increment();
    }

    // increment the value by val
    public void increment(long val) {
        stat.increment(val);
    }

    // decrement the value by 1
    public void decrement() {
        stat.decrement();
    }

    // decrement the value by val
    public void decrement(long val) {
        stat.decrement(val);
    }

    // reset the value and create time
    public void reset(boolean resetAll) {
        stat.reset(resetAll);
    }

    public StatisticImpl getStatistic() {
        return stat;
    }

    // combine the value of this data and other data
    public void combine(SpdLong other) {
        if (other == null)
            return;
        if (stat.isEnabled() && other.isEnabled())
            stat.combine((CountStatisticImpl) other.getStatistic());
    }

    // mark the data enabled and reset the value and createTime
    public void enable(int level) {
        super.enable(level);
        stat.enable(level);
    }

    // mark the data disabled
    public void disable() {
        super.disable();
        stat.disable();
    }

    // return if the data is enabled
    public boolean isEnabled() {
        return stat.isEnabled();
    }

}
