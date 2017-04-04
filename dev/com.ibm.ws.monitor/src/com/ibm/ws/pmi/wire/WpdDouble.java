// IBM Confidential OCO Source Material
// 5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997, 2002
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

/** 
 * History:
 * CMVC 86523: create the file - wenjian, 09/18/2000
 * 
 * The class holds a double value for double data type.
 *  
 */

package com.ibm.ws.pmi.wire;

import com.ibm.websphere.pmi.*;

public class WpdDouble extends WpdDataImpl {
    private static final long serialVersionUID = 2706590952848330822L;
    private double value;

    // constructor:
    public WpdDouble(int id, long time, double value) {
        super(id, time);
        this.value = value;
    }

    public double getDoubleValue() {
        return value;
    }

    public String toXML() {
        String res = PmiConstants.XML_DOUBLE + PmiConstants.XML_ID + id
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
        if (!(other instanceof WpdDouble)) {
            System.err.println("WpdDouble.combine: wrong type. WpdDouble is needed!");
        } else {
            WpdDouble otherDouble = (WpdDouble) other;
            value += otherDouble.getDoubleValue();
        }
    }
}
