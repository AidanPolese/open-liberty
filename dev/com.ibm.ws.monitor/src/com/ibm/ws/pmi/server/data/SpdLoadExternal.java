// IBM Confidential OCO Source Material
// 5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997, 2002
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
/** 
 * History:
 * CMVC 86523: create the file - wenjian, 09/18/2000
 * 
 * The class implements SpdLoad interface and holds a SpdLoadExternalValue proxy and
 * a Load value.
 *  
 */

package com.ibm.ws.pmi.server.data;

import com.ibm.websphere.pmi.*;
import com.ibm.websphere.pmi.server.*;
import com.ibm.ws.pmi.stat.*;

public class SpdLoadExternal extends SpdDataImpl
                                 implements SpdLoad {
    private static final long serialVersionUID = 7079768640124356041L;
    SpdLoadExternalValue proxy;
    StatisticImpl stat = null;

    public SpdLoadExternal(PmiModuleConfig moduleConfig, String name,
                           SpdLoadExternalValue proxy) {
        super(moduleConfig, name);
        this.proxy = proxy;
        stat = new BoundedRangeStatisticImpl(dataId);
    }

    public SpdLoadExternal(int dataId, SpdLoadExternalValue proxy) {
        super(dataId);
        this.proxy = proxy;
        stat = new BoundedRangeStatisticImpl(dataId);
    }

    public SpdLoadExternal(int dataId, int type, SpdLoadExternalValue proxy) {
        super(dataId);
        this.proxy = proxy;
        if (type == TYPE_RANGE)
            stat = new RangeStatisticImpl(dataId);
        else
            stat = new BoundedRangeStatisticImpl(dataId);
    }

    // null methods in order to implement SpdLoad
    public void add(double val) {}

    public void increment(double incVal) {}

    public void increment() {}

    public void decrement(double incVal) {}

    public void decrement() {}

    public void setConfig(long minSize, long maxSize) {}

    public void reset(boolean resetAll) {
        return;
    }

    public void setDataInfo(PmiModuleConfig moduleConfig) {
        stat.setDataInfo(moduleConfig);
    }

    // return a wire level data using given time as snapshotTime
    public StatisticImpl getStatistic() {
        if (enabled)
            return (StatisticImpl) proxy.getLoadValue();
        else
            return null;
    }

    public void combine(SpdLoad other) {
        //System.out.println ("[PMI.SpdLoadExternal] combine(). shouldn't be here");
        /*
         * if (other == null) return;
         * if (enabled)
         * stat.combine((BoundedRangeStatisticImpl)other.getStatistic());
         */
    }

    public void cleanup() {}

    public boolean isExternal() {
        return true;
    }

    public void updateExternal() {
        proxy.updateStatistic();
    }
}
