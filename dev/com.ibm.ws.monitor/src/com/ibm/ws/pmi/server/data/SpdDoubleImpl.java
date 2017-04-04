// IBM Confidential OCO Source Material
// 5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997, 2002
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

/** 
 * History:
 * CMVC 86523: create the file - wenjian, 09/18/2000
 * 
 * The class implements SpdDouble interface and holds a double value.
 *  
 */
package com.ibm.ws.pmi.server.data;

import com.ibm.websphere.pmi.PmiModuleConfig;
import com.ibm.websphere.pmi.server.SpdDouble;
import com.ibm.ws.pmi.stat.DoubleStatisticImpl;
import com.ibm.ws.pmi.stat.StatisticImpl;

/**
 * SpdDouble: contains a long numeric value.
 */
public class SpdDoubleImpl extends SpdDataImpl
                        implements SpdDouble {
    private static final long serialVersionUID = -8540479668841981802L;
    protected DoubleStatisticImpl stat = null;

    public SpdDoubleImpl(PmiModuleConfig moduleConfig, String name) {
        super(moduleConfig, name);
        stat = new DoubleStatisticImpl(dataId);
    }

    public SpdDoubleImpl(int id) {
        super(id);
        stat = new DoubleStatisticImpl(dataId);
    }

    // set the value
    public void set(double val) {
        stat.setDouble(val);
    }

    // increment the value by 1
    public void increment() {
        stat.increment();
    }

    // increment the value by val
    public void increment(double val) {
        stat.increment(val);
    }

    // decrement the value by 1
    public void decrement() {
        stat.decrement();
    }

    // decrement the value by val
    public void decrement(double val) {
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
    public void combine(SpdDouble other) {
        if (other == null)
            return;
        if (stat.isEnabled() && other.isEnabled())
            stat.combine((DoubleStatisticImpl) other.getStatistic());
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
