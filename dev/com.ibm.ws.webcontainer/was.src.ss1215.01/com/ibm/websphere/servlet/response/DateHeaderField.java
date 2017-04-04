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


class DateHeaderField extends HeaderField
{
    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3257848775039922233L;
	private HeaderField _next;
    private String _name;
    private long _value;
    private static TraceNLS nls = TraceNLS.getTraceNLS(DateHeaderField.class, "com.ibm.ws.webcontainer.resources.Messages");

    public DateHeaderField(String name, long value, HeaderField next)
    {
        _name = name;
        _value = value;
        _next = next;
    }

    public int getIntValue()
    {
        throw new IllegalStateException(nls.getString("Unsupported.conversion","Unsupported conversion"));
    }

    public long getDateValue()
    {
        return _value;
    }

    public String getStringValue()
    {
        return new Long(_value).toString();
    }

    public String getName()
    {
        return _name;
    }

    public void setIntValue(int val)
    {
        throw new IllegalStateException(nls.getString("Unsupported.conversion","Unsupported conversion"));
    }

    public void setDateValue(long date)
    {
        _value = date;
    }

    public void setStringValue(String s)
    {
        // begin pq54562: part 1
        //throw new IllegalStateException(nls.getString("Unsupported.conversion","Unsupported conversion"));
        _value = new Long(s).longValue();
        // end pq54562: part 1
    }

    public void transferHeader(HttpServletResponse resp)
    {
        _next.transferHeader(resp);
        resp.setDateHeader(getName(), getDateValue());
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