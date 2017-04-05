// /I/ /W/ /G/ /U/   <-- CMVC Keywords, replace / with %
// %I% %W% %G% %U%
//
// IBM Confidential OCO Source Material
//5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N01, 5733-W61 (C) COPYRIGHT International Business Machines Corp. 2006, 2007
//
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Module  :  GlobalDataSourceNotFoundException.java
//
// Source File Description:
//
//     See class description.
//
// Change Activity:
//
// Reason    Version   Date     Userid    Change Description
// --------- --------- -------- --------- -----------------------------------------
// d463444   EJB3      20070910 jckrueg  : New part
// --------- --------- -------- --------- -----------------------------------------

package com.ibm.ws.jpa.management;

import java.sql.SQLException;

/**
 * Indicates that a datasource was not found within the java:comp/env
 * namespace. It can also indicate that that there is no component
 * context on the thread.
 */
public class GlobalDataSourceNotFoundException extends SQLException
{
    private static final long serialVersionUID = 1816214284123404414L;

    public GlobalDataSourceNotFoundException(String reason)
    {
        super(reason);
    }

    public GlobalDataSourceNotFoundException()
    {
        super();
    }

}
