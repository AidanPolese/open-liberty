/* ********************************************************************************* */
/* COMPONENT_NAME: WAS.transactions                                                  */
/*                                                                                   */
/* ORIGINS: 27                                                                       */
/*                                                                                   */
/* IBM Confidential OCO Source Material                                              */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2004, 2007 */
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
/*  04-05-07  awilkins   202175  Creation                                            */
/*  05-06-30  awilkins   280952  Perf: use iterator in notifyCallbacks               */
/*  06/01/06  johawkes   306998.12 Use TraceComponent.isAnyTracingEnabled()          */
/*  06/02/14  johawkes   347212  New ras & ffdc                                      */
/*  06/02/23  johawkes   349301  Old ras & ffdc                                      */
/*  08/05/22  johawkes   522569  Perf trace                                          */
/*  09/08/26  johawkes   602532.3 LTC bundle                                         */
/*  09/09/16  mallam     602532.6 LTC bundle                                         */
/* ********************************************************************************* */

package com.ibm.ws.uow;

import java.util.ArrayList;
import java.util.Iterator;

import com.ibm.tx.TranConstants;
import com.ibm.tx.util.logging.Tr;
import com.ibm.tx.util.logging.TraceComponent;

public class UOWScopeCallbackManager
{
    private static final TraceComponent tc = Tr.register(UOWScopeCallbackManager.class, TranConstants.TRACE_GROUP, null);
    
    // A list of callbacks that are interested in
    // UOW scope context changes 
    private ArrayList<UOWScopeCallback> _callbacks;
    
    public void addCallback(UOWScopeCallback callback)
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "addCallback", callback);

        if (_callbacks == null)
        {
            _callbacks = new ArrayList<UOWScopeCallback>();
        }
        
        _callbacks.add(callback);

        if (tc.isEntryEnabled()) Tr.exit(tc, "addCallback");
    }
       
    public void removeCallback(UOWScopeCallback callback)
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "removeCallback", callback);

        if (_callbacks != null)
        {
            final boolean result = _callbacks.remove(callback);
            if (tc.isDebugEnabled()) Tr.debug(tc, "callback found/removed: " + result);
        }
        
        if (tc.isEntryEnabled()) Tr.exit(tc, "removeCallback");
    }
       
    public void notifyCallbacks(int contextChangeType, UOWScope scope)
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "notifyCallbacks", new Object[]{contextChangeType, scope, this});       
        
        if (_callbacks != null)
        {
            final Iterator callbacks = _callbacks.iterator();
            
            while (callbacks.hasNext())
            {
                ((UOWScopeCallback)callbacks.next()).contextChange(contextChangeType, scope);
            }       
        }
        
        if (tc.isEntryEnabled()) Tr.exit(tc, "notifyCallbacks");
    }
}
