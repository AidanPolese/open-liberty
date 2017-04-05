// IBM Confidential OCO Source Material
// 5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997, 2002
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

/** 
 * History:
 * CMVC 86523: create the file - wenjian, 09/18/2000
 * 
 * The class holds a int value for int data type.
 *  
 */

package com.ibm.ws.pmi.wire;

import com.ibm.websphere.pmi.*;

public class WpdInt extends WpdDataImpl {
    private static final long serialVersionUID = -557634123546071193L;
    private int value;

    // constructor:
    public WpdInt(int id, long time, int value) {
        super(id, time);
        this.value = value;
    }

    public int getIntValue() {
        return value;
    }

    public String toXML() {
        String res = PmiConstants.XML_INT + PmiConstants.XML_ID + id
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
        if (!(other instanceof WpdInt)) {
            System.err.println("WpdInt.combine: wrong type. WpdInt is needed!");
        } else {
            WpdInt otherInt = (WpdInt) other;
            value += otherInt.getIntValue();
        }
    }
}
