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
//       LIDB3518-1.1    06-23-07      mmolden            ARD
package com.ibm.websphere.servlet31.response;

import com.ibm.websphere.servlet.response.DummyResponse;

public class DummyResponse31 extends DummyResponse
{

    /* (non-Javadoc)
     * @see javax.servlet.ServletResponse#setContentLengthLong(long)
     */
    @Override
    public void setContentLengthLong(long arg0) {
       // Added for Servlet 3.1.  No Implementation required

    }
}

