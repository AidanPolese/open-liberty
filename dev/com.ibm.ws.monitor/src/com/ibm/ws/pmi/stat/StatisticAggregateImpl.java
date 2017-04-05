// IBM Confidential OCO Source Material
// 5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997, 2003
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

package com.ibm.ws.pmi.stat;

import com.ibm.websphere.pmi.*;
import java.util.*;
import com.ibm.websphere.pmi.stat.WSStatistic;

public class StatisticAggregateImpl extends StatisticImpl implements PmiConstants {
    private static final long serialVersionUID = 8625154880422057621L;
    private ArrayList members = new ArrayList();
    private StatisticImpl aggregateValue = null;
    private int _type = TYPE_INVALID;

    public StatisticAggregateImpl(PmiDataInfo dataInfo) {
        super(dataInfo.getId());
        _type = dataInfo.getType();

        switch (_type) {
            case TYPE_LONG:
                aggregateValue = new CountStatisticImpl(id);
                break;

            case TYPE_DOUBLE:
                aggregateValue = new DoubleStatisticImpl(id);
                break;

            case TYPE_STAT:
                aggregateValue = new TimeStatisticImpl(id);
                break;

            case TYPE_LOAD:
                aggregateValue = new BoundedRangeStatisticImpl(id);
                break;
            default:
                _type = TYPE_INVALID;
                System.err.println("[StatisticAggregateImpl] Invalid statistic type");
        }
    }

    public WSStatistic copy() {
        // TODO: Implement this function
        return null;
    }

    public synchronized boolean add(StatisticImpl statistic) {
        if (statistic == null) {
            return false;
        } else if (members.contains(statistic)) {
            return false;
        } else {
            return members.add(statistic);
        }
    }

    public synchronized boolean remove(StatisticImpl statistic) {
        if (statistic == null)
            return false;
        else
            return members.remove(statistic);
    }

    // Return a wire level data using given time
    public StatisticImpl getStatistic() {
        StatisticImpl aggStat = _getAggregate();
        aggStat.setLastSampleTime(System.currentTimeMillis());
        return aggStat;
    }

    private StatisticImpl _getAggregate() {
        /*
         * // **FIXME: should be reset(false). add to statistic, statisticimpl
         * // this requires the code cleanup and adding reset or cleanup method to statistic interface
         * // if we add now it will be exposed so not the hard way now.
         * switch (_type)
         * {
         * case TYPE_LONG:
         * ((CountStatisticImpl)aggregateValue).reset(false);
         * break;
         * 
         * case TYPE_DOUBLE:
         * ((DoubleStatisticImpl)aggregateValue).reset(false);
         * break;
         * 
         * case TYPE_STAT:
         * ((TimeStatisticImpl)aggregateValue).reset(false);
         * break;
         * 
         * case TYPE_LOAD:
         * ((BoundedRangeStatisticImpl)aggregateValue).cleanup();
         * break;
         * default:
         * _type = TYPE_INVALID;
         * System.out.println("[StatisticAggregateImpl] Invalid statistic type");
         * }
         */
        aggregateValue.reset(false);

        for (int i = 0; i < members.size(); i++) {
            Object member = members.get(i);
            if (member == null)
                continue;

            if (member instanceof StatisticAggregateImpl) {
                aggregateValue.combine(((StatisticAggregateImpl) member)._getAggregate());
            } else {
                aggregateValue.combine((StatisticImpl) member);
            }

        }
        return aggregateValue;
    }

    // dummy impl
    // these methods will not be called on this class
    // these methods will be called on the "aggregateValue"
    public void update(WSStatistic data) {}

    public WSStatistic delta(WSStatistic data) {
        return null;
    }

    public WSStatistic rateOfChange(WSStatistic otherStat) {
        // TODO: Implement this function
        return null;
    }

    public void combine(WSStatistic data) {}

    public void resetOnClient(WSStatistic data) {}

    public void reset(boolean resetAll) {}

    public String toXML() {
        return null;
    }
}
