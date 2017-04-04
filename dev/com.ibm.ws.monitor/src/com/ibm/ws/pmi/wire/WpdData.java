// IBM Confidential OCO Source Material
// 5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997, 2002
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

/** 
 * History:
 * CMVC 86523: create the file - wenjian, 09/18/2000
 * 
 * The interface including common methods for wire level data.
 *  
 */

package com.ibm.ws.pmi.wire;

public interface WpdData extends java.io.Serializable {
    public static final long serialVersionUID = -8267987626962974625L;

    public long getTime();

    public int getId();

    public String toXML();

    public void combine(WpdData other);
}
