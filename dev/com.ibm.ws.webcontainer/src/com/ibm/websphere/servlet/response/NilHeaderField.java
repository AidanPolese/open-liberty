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

import java.util.NoSuchElementException;

import javax.servlet.http.HttpServletResponse;

class NilHeaderField extends HeaderField
{
    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3545796567979079990L;
	private static NilHeaderField _instance;
    private NilHeaderField()
    {
    }

    public static synchronized NilHeaderField instance()
    {
        if (_instance == null)
        {
            _instance = new NilHeaderField();
        }
        return _instance;
    }

    public int getIntValue()
    {
        throw new IllegalStateException("Nil Header Field");
    }

    public long getDateValue()
    {
        throw new IllegalStateException("Nil Header Field");
    }

    public String getStringValue()
    {
        throw new IllegalStateException("Nil Header Field");
    }

    public void setIntValue(int val)
    {
        throw new IllegalStateException("Nil Header Field");
    }

    public void setDateValue(long date)
    {
        throw new IllegalStateException("Nil Header Field");
    }

    public void setStringValue(String s)
    {
        throw new IllegalStateException("Nil Header Field");
    }

    public String getName()
    {
        throw new IllegalStateException("Nil Header Field");
    }

    public void transferHeader(HttpServletResponse resp)
    {
        //do nothing, this is the end of the transferHeader propogation.
    }

    public HeaderField getNextField()
    {
        throw new NoSuchElementException();
    }

    public boolean hasMoreFields()
    {
        return false;
    }

    public boolean isNil()
    {
        return true;
    }
}