// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5655-N01, 5733-W60 (C) COPYRIGHT International Business Machines Corp. 1997, 2005
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

package com.ibm.ws.pmi.server.data;

import com.ibm.websphere.pmi.PmiDataInfo;
import com.ibm.ws.pmi.stat.AverageStatisticImpl;
import com.ibm.ws.pmi.stat.BoundedRangeStatisticImpl;
import com.ibm.ws.pmi.stat.CountStatisticImpl;
import com.ibm.ws.pmi.stat.DoubleStatisticImpl;
import com.ibm.ws.pmi.stat.RangeStatisticImpl;
import com.ibm.ws.pmi.stat.StatisticImpl;
import com.ibm.ws.pmi.stat.TimeStatisticImpl;
import com.ibm.wsspi.pmi.factory.StatisticActions;

public class SpdStatisticExternal extends SpdDataImpl {
    private static final long serialVersionUID = -7029239686912201805L;
    protected StatisticActions proxy;
    protected StatisticImpl onReqStatistic;

    //private int _type;

    public SpdStatisticExternal(PmiDataInfo dataInfo, StatisticActions proxy) {
        super(dataInfo.getId());
        this.proxy = proxy;
        //System.out.println("Hi This is for UpdataStatistic");
        int type = dataInfo.getType();
        switch (type) {
            case TYPE_LONG:
                onReqStatistic = new CountStatisticImpl(dataId);
                break;

            case TYPE_DOUBLE:
                onReqStatistic = new DoubleStatisticImpl(dataId);
                break;

            case TYPE_STAT:
                onReqStatistic = new TimeStatisticImpl(dataId);
                break;

            case TYPE_AVGSTAT:
                onReqStatistic = new AverageStatisticImpl(dataId);
                break;

            case TYPE_RANGE:
                onReqStatistic = new RangeStatisticImpl(dataId);
                break;

            case TYPE_LOAD:
                onReqStatistic = new BoundedRangeStatisticImpl(dataId);
                break;

            default:
                System.out.println("[SpdStatisticExternal] Invalid statistic type");
        }
    }

    // return a wire level data using given time as snapshotTime
    public StatisticImpl getStatistic() {
        if (enabled) {
            if (proxy == null) {
                System.out.println("[SpdStatisticExternal] null proxy");
                return null;
            }

            proxy.updateStatisticOnRequest(dataId);

            // onReqStatistic will be updated by the component
            // the reference is kept in this class
            return onReqStatistic;
        } else
            return null;
    }

    public void reset(boolean resetAll) {
        // ** Nothing to do **
        return;
    }

    public StatisticImpl getStatisticRef() {
        return onReqStatistic;
    }

    public boolean isExternal() {
        return true;
    }

    public void updateExternal() {
        //System.out.println("Hi This is for UpdataStatistic");
        proxy.updateStatisticOnRequest(dataId);
    }
}
