// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997,2011
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// %Z% %I% %W% %G% %U% [%H% %T%]
//
// Change History:
//
// YY/MM/DD Developer CMVC ID      Description
// -------- --------- -------      -----------
// 06/10/04 johawkes  LIDB4548-1.1 Created
// 08/05/24 johawkes  522569       Perf trace
// 10/06/09 johawkes  655890       Moved for use in embeddable container
// 11/11/24 johawkes  723423       Repackaging
//
package com.ibm.ws.uow.embeddable.jndi;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;

import com.ibm.ejs.ras.Tr;
import com.ibm.ejs.ras.TraceComponent;
import com.ibm.tx.TranConstants;
import com.ibm.wsspi.uow.UOWManagerFactory;

public class UOWManagerJNDIFactory implements ObjectFactory
{
    private static final TraceComponent tc = Tr.register(UOWManagerJNDIFactory.class, TranConstants.TRACE_GROUP, TranConstants.NLS_FILE);

    public Object getObjectInstance(Object referenceObject, Name name, Context context, Hashtable env) throws Exception
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) Tr.entry(tc, "getObjectInstance", new Object[]{referenceObject, name, context, env});

        final Object o = UOWManagerFactory.getUOWManager();

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) Tr.exit(tc, "getObjectInstance", o);
        return o;
    }
}