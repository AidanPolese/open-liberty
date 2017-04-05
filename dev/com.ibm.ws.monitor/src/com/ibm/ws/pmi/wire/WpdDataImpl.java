// IBM Confidential OCO Source Material
// 5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997, 2002
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

/** 
 * History:
 * CMVC 86523: create the file - wenjian, 09/18/2000
 * 
 * The class implementing WpdData interface. It holds two members: id and time.
 *  
 */

package com.ibm.ws.pmi.wire;

public abstract class WpdDataImpl implements WpdData {
    protected int id;
    protected long time;

    // constructor:
    public WpdDataImpl(int id, long time) {
        this.id = id;
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public int getId() {
        return id;
    }

    public abstract String toXML();

    public abstract void combine(WpdData other);
}
