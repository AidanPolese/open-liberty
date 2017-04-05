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

import com.ibm.ejs.ras.TraceNLS;

class StringHeaderField extends HeaderField
{
    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3906084568368034868L;
	private HeaderField _next;
    private String _name;
    private String _value;
    private static TraceNLS nls = TraceNLS.getTraceNLS(StringHeaderField.class, "com.ibm.ws.webcontainer.resources.Messages");

    public StringHeaderField(String name, String value, HeaderField next)
    {
        _name = name;
        _value = value;
        _next = next;
    }

    public int getIntValue()
    {
        return new Integer(getStringValue()).intValue();
    }

    public long getDateValue()
    {
        // pq41904 return the long representation of the date string...disable the exception
        //throw new IllegalStateException(nls.getString("Unsupported.conversion","Unsupported conversion"));
        return new Long(getStringValue()).longValue();
    }

    public String getStringValue()
    {
        return _value;
    }

    public void setIntValue(int val)
    {
        _value = (new Integer(val)).toString();
    }

    public void setDateValue(long date)
    {
        // begin pq54562: part 2
        // throw new IllegalStateException(nls.getString("Unsupported.conversion","Unsupported conversion"));
        _value = (new Long (date)).toString();
        // end pq54562: part 2
    }

    public void setStringValue(String s)
    {
        _value = s;
    }

    public String getName()
    {
        return _name;
    }


    public void transferHeader(HttpServletResponse resp)
    {
        _next.transferHeader(resp);
        resp.setHeader(getName(), getStringValue());
    }

    public HeaderField getNextField()
    {
        if (hasMoreFields())
        {
            return _next;
        }
        else
        {
            throw new NoSuchElementException();
        }
    }

    public boolean hasMoreFields()
    {
        return true;
//        return !_next.isNil();
    }

    public boolean isNil()
    {
        return false;
    }
}