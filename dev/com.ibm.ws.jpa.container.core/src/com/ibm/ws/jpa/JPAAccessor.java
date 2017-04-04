// /I/ /W/ /G/ /U/   <-- CMVC Keywords, replace / with %
// %I% %W% %G% %U%
//
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N01, 5733-W61 (C) COPYRIGHT International Business Machines Corp. 2006, 2007
//
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Module  :  JPAAccessor.java
//
// Change Activity:
//
// Reason          Version   Date     Userid    Change Description
// --------------- --------- -------- --------- -----------------------------------------
// d392996         EJB3      20060930 leealber : Initial Release
// d406994.2       EJB3      20061120 leealber : CI: exception handling rework
// d416151.3.5     EJB3      20070501 leealber : Rename JPAService to JPAComponent
// d416151.3.7     EJB3      20070501 leealber : Add isAnyTraceEnabled() test
// d416151.3.11    EJB3      20070504 leealber : Code review clean up.
// --------------- --------- -------- --------- -----------------------------------------
package com.ibm.ws.jpa;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

/**
 * Provide accessor function to retrieve the JPAComponent object.
 */
public abstract class JPAAccessor
{
    private static final TraceComponent tc = Tr.register(JPAAccessor.class, "JPA", null); // d406994.2 d416151.3.11

    private static JPAComponent jpaComponent;

    /**
     * Return the default JPAComponent object in the application server.
     */
    public static JPAComponent getJPAComponent()
    {
        return jpaComponent;
    }

    // 416151.3.5 Begins
    /**
     * Return the default JPAComponent object in the application server.
     */
    public static void setJPAComponent(JPAComponent instance)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.entry(tc, "setJPAComponent", instance);

        jpaComponent = instance;

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.exit(tc, "setJPAComponent");
    }
    // 416151.3.5 Ends
}
