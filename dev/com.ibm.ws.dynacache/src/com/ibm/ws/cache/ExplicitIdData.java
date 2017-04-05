// 1.2, 7/3/08
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2008
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.cache;

public class ExplicitIdData {

    public Object id;               // cache id
    public byte   info;             // info

	public ExplicitIdData() {
	}

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("EID: id=");
        sb.append(id);
        sb.append(" info=");
        sb.append(info);
        return sb.toString();
    }
}
