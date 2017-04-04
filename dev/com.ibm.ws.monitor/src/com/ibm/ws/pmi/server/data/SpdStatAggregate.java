// IBM Confidential OCO Source Material
// 5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997, 2002
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

/** 
 * History:
 * CMVC 86523: create the file - wenjian, 09/18/2000
 * 
 * The aggregate class for SpdStat data. 
 *  
 */

package com.ibm.ws.pmi.server.data;

import com.ibm.websphere.pmi.PmiModuleConfig;
import com.ibm.websphere.pmi.server.SpdData;
import com.ibm.websphere.pmi.server.SpdStat;
import com.ibm.ws.pmi.stat.StatisticImpl;

public class SpdStatAggregate extends SpdGroupBase {
    private static final long serialVersionUID = -6529091247440770134L;

    SpdStat myValue = null;

    // Constructor
    public SpdStatAggregate(PmiModuleConfig moduleConfig, String name) {
        super(moduleConfig, name);
        myValue = new SpdStatImpl(dataId);
    }

    public SpdStatAggregate(int dataId) {
        super(dataId);
        myValue = new SpdStatImpl(dataId);
    }

    public SpdStatAggregate(int type, int dataId) {
        super(dataId);
        myValue = new SpdStatImpl(type, dataId);
    }

    // Check data type and call super.add to add data - synchronized in super
    public boolean add(SpdData data) {
        if (data == null)
            return false;
        if (data instanceof SpdStat) {
            return super.add(data);
        } else {
            return false;
        }
    }

    // Check data type and call super to remove data - synchronized in super
    public boolean remove(SpdData data) {
        if (data == null)
            return false;
        if (data instanceof SpdStat) {
            return super.remove(data);
        } else {
            return false;
        }
    }

    // reset - do nothing for aggregate data
    public void reset(boolean resetAll) {}

    // Return a wire level data using given time
    public StatisticImpl getStatistic() {
        if (enabled)
            return getSpdStat().getStatistic();
        else
            return null;
    }

    // Return a SpdStat that combines all the members of the aggregate data
    // Recursive.
    private SpdStat getSpdStat() {
        myValue.reset(false);
        for (int i = 0; i < members.size(); i++) {
            Object member = members.get(i);
            if (member == null)
                continue;
            if (member instanceof SpdStat) {
                myValue.combine((SpdStat) member);
            } else { // SpdStatAggregate
                myValue.combine(((SpdStatAggregate) member).getSpdStat());
            }
        }
        return myValue;
    }

    public void updateAggregate() {
        myValue.reset(false);
        for (int i = 0; i < members.size(); i++) {
            Object member = members.get(i);
            if (member == null)
                continue;
            if (member instanceof SpdStat) {
                myValue.combine((SpdStat) member);
            } else { // SpdStatAggregate
                myValue.combine(((SpdStatAggregate) member).getSpdStat());
            }
        }
    }
}
