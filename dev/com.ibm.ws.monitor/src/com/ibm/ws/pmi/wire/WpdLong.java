// IBM Confidential OCO Source Material
// 5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997, 2002
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

/** 
 * History:
 * CMVC 86523: create the file - wenjian, 09/18/2000
 * 
 * The class holds a long value for long data type.
 *  
 */

package com.ibm.ws.pmi.wire;

import com.ibm.websphere.pmi.*;

public class WpdLong extends WpdDataImpl {
    private static final long serialVersionUID = -669579073691504835L;
    private long value;

    // constructor:
    public WpdLong(int id, long time, long value) {
        super(id, time);
        this.value = value;
    }

    public long getLongValue() {
        return value;
    }

    public String toXML() {
        String res = PmiConstants.XML_LONG + PmiConstants.XML_ID + id
                     + PmiConstants.XML_TIME + time + PmiConstants.XML_VALUE + value
                     + PmiConstants.XML_ENDTAG;
        return res;
    }

    public String toString() {
        return "Data Id=" + id + " time=" + time + " value=" + value;
    }

    public void combine(WpdData other) {
        if (other == null)
            return;
        if (!(other instanceof WpdLong)) {
            System.err.println("WpdLong.combine: wrong type. WpdLong is needed!");
        } else {
            WpdLong otherLong = (WpdLong) other;
            value += otherLong.getLongValue();
        }
    }
}
