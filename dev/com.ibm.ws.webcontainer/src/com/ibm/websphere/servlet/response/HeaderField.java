// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
//
//CHANGE HISTORY
//Flag    Defect         Date         Modified By         Description
//--------------------------------------------------------------------------------------
//       400921          01/16/07      mmolden            Limit to one class per java file
package com.ibm.websphere.servlet.response;

import java.io.Serializable;

import javax.servlet.http.HttpServletResponse;


abstract class HeaderField implements Serializable
{
    public abstract int getIntValue();
    public abstract long getDateValue();
    public abstract String getStringValue();
    public abstract void setIntValue(int val);
    public abstract void setDateValue(long date);
    public abstract void setStringValue(String s);
    public abstract String getName();
    public abstract void transferHeader(HttpServletResponse resp);
    public abstract HeaderField getNextField();
    public abstract boolean hasMoreFields();
    public abstract boolean isNil();
}
