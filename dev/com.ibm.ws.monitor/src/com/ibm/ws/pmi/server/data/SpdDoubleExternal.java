// IBM Confidential OCO Source Material
// 5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997, 2002
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

/** 
 * History:
 * CMVC 86523: create the file - wenjian, 09/18/2000
 * 
 * The class implements SpdDouble interface and holds a SpdDoubleExternalValue proxy and
 * a double value.
 *  
 */

package com.ibm.ws.pmi.server.data;

import com.ibm.websphere.pmi.*;
import com.ibm.websphere.pmi.server.*;
import com.ibm.ws.pmi.stat.*;

public class SpdDoubleExternal extends SpdDataImpl
                             implements SpdDouble {
    private static final long serialVersionUID = -7694571935974380747L;
    protected SpdDoubleExternalValue proxy;
    protected DoubleStatisticImpl stat;

    public SpdDoubleExternal(PmiModuleConfig moduleConfig, String name,
                             SpdDoubleExternalValue proxy) {
        super(moduleConfig, name);
        this.proxy = proxy;
        stat = new DoubleStatisticImpl(dataId);
    }

    public SpdDoubleExternal(int dataId, SpdDoubleExternalValue proxy) {
        super(dataId);
        this.proxy = proxy;
        stat = new DoubleStatisticImpl(dataId);
    }

    // null methods in order to implement SpdDouble
    public void set(double val) {}

    public void increment() {}

    public void increment(double val) {}

    public void decrement() {}

    public void decrement(double val) {}

    public void reset(boolean resetAll) {
        return;
    }

    public void setDataInfo(PmiModuleConfig moduleConfig) {
        stat.setDataInfo(moduleConfig);
    }

    // return a wire level data using given time as snapshotTime
    public StatisticImpl getStatistic() {
        if (enabled) {
            return (StatisticImpl) proxy.getDoubleValue();
            //stat.setDouble(proxy.getDoubleValue());
            //return stat;
        } else {
            return null;
        }
    }

    public void combine(SpdDouble other) {
        //System.out.println ("[PMI.SpdDoubleExternal] combine(). shouldn't be here");
        return;
        /*
         * if (other == null) return;
         * if (enabled)
         * stat.combine((DoubleStatisticImpl)other.getStatistic());
         */
    }

    public boolean isExternal() {
        return true;
    }

    public void updateExternal() {
        proxy.updateStatistic();
    }
}
