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

import java.util.Enumeration;

@SuppressWarnings("unchecked")
class HeaderFieldNames implements Enumeration
{
    HeaderField _curr;
    HeaderFieldNames(HeaderField field)
    {
        _curr = field;
    }

    public boolean hasMoreElements()
    {
        return _curr.hasMoreFields();
    }

    public Object nextElement()
    {
        HeaderField _ret = _curr;
        _curr = _curr.getNextField();
        return _ret.getName();
    }
}