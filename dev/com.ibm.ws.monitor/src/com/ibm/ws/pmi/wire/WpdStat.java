// IBM Confidential OCO Source Material
// 5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997, 2002
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

/** 
 * History:
 * CMVC 86523: create the file - wenjian, 09/18/2000
 * 
 * The class holds a Stat value for Stat data type.
 *  
 */

package com.ibm.ws.pmi.wire;

import com.ibm.websphere.pmi.*;

public class WpdStat extends WpdDataImpl {
    private static final long serialVersionUID = 8796793171074807073L;
    private int count;
    private double total;
    private double sumOfSquares;

    // constructor:
    public WpdStat(int id, long time, int count, double total, double sumOfSquares) {
        super(id, time);
        this.count = count;
        this.total = total;
        this.sumOfSquares = sumOfSquares;
    }

    public int getCount() {
        return count;
    }

    public double getTotal() {
        return total;
    }

    public double getSumOfSquares() {
        return sumOfSquares;
    }

    public String toXML() {
        String res = PmiConstants.XML_INT + PmiConstants.XML_ID + id
                     + PmiConstants.XML_TIME + time + PmiConstants.XML_COUNT + count
                     + PmiConstants.XML_TOTAL + total
                     + PmiConstants.XML_SUMOFSQUARES + sumOfSquares
                     + PmiConstants.XML_ENDTAG;
        return res;
    }

    public String toString() {
        return "Data Id=" + id + " time=" + time + " count=" + count
                + " total=" + total + " sumOfSquares=" + sumOfSquares;
    }

    public void combine(WpdData other) {
        if (other == null)
            return;
        if (!(other instanceof WpdStat)) {
            System.err.println("WpdStat.combine: wrong type. WpdStat is needed!");
        } else {
            WpdStat otherStat = (WpdStat) other;
            count += otherStat.getCount();
            total += otherStat.getTotal();
            sumOfSquares += otherStat.getSumOfSquares();
        }
    }
}
