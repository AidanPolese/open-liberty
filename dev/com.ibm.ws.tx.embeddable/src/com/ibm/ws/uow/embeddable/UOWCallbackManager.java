/* ********************************************************************************* */
/* COMPONENT_NAME: WAS.transactions                                                  */
/*                                                                                   */
/* ORIGINS: 27                                                                       */
/*                                                                                   */
/* IBM Confidential OCO Source Material                                              */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2006,2011 */
/* The source code for this program is not published or otherwise divested           */
/* of its trade secrets, irrespective of what has been deposited with the            */
/* U.S. Copyright Office.                                                            */
/*                                                                                   */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                                     */
/*                                                                                   */
/*  DESCRIPTION:                                                                     */
/*                                                                                   */
/*  Change History:                                                                  */
/*                                                                                   */
/*  YY-MM-DD  Developer  Defect  Description                                         */
/*  --------  ---------  ------  -----------                                         */
/*  06-10-17  johawkes   LIDB4548-1.1 Creation                                       */
/*  08-05-24  johawkes   522569  Perf trace                                          */
/*  09-11-09  johawkes   F743-305.1 EJB 3.1                                          */
/*  11-11-24  johawkes   723423  Repackaging                                         */
/* ********************************************************************************* */

package com.ibm.ws.uow.embeddable;

import java.util.ArrayList;
import java.util.Iterator;

import com.ibm.ejs.ras.Tr;
import com.ibm.ejs.ras.TraceComponent;
import com.ibm.tx.TranConstants;
import com.ibm.ws.Transaction.UOWCallback;
import com.ibm.ws.Transaction.UOWCoordinator;

public class UOWCallbackManager
{
    private static final TraceComponent tc = Tr.register(UOWCallbackManager.class, TranConstants.TRACE_GROUP, null);
    
    // A list of callbacks that are interested in
    // UOW context changes 
    private ArrayList<UOWCallback> _callbacks;
    
    public void addCallback(UOWCallback callback)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) Tr.entry(tc, "addCallback", callback);

        if (_callbacks == null)
        {
            _callbacks = new ArrayList<UOWCallback>();
        }
        
        _callbacks.add(callback);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) Tr.exit(tc, "addCallback");
    }
       
    public void notifyCallbacks(int contextChangeType, UOWCoordinator coord)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) Tr.entry(tc, "notifyCallbacks", new Object[]{contextChangeType, coord});       
        
        if (_callbacks != null)
        {
            final Iterator callbacks = _callbacks.iterator();
            
            while (callbacks.hasNext())
            {
                ((UOWCallback)callbacks.next()).contextChange(contextChangeType, coord);
            }       
        }
        
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) Tr.exit(tc, "notifyCallbacks");
    }
}
