
/* ********************************************************************************* */
/* COMPONENT_NAME: WAS.transactions                                                  */
/*                                                                                   */
/* ORIGINS: 27                                                                       */
/*                                                                                   */
/* IBM Confidential OCO Source Material                                              */
/* 5724-J08, 5724-I63, 5724-H88, 5655-N01, 5733-W61 (C) COPYRIGHT International Business Machines Corp. 2004, 2007 */
/* The source code for this program is not published or otherwise divested           */
/* of its trade secrets, irrespective of what has been deposited with the            */
/* U.S. Copyright Office.                                                            */
/*                                                                                   */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                                     */
/*                                                                                   */
/* DESCRIPTION:                                                                      */
/*                                                                                   */
/* Change History:                                                                   */
/*                                                                                   */
/* Date      Programmer    Defect      Description                                   */
/* --------  ----------    ------      -----------                                   */
/* 07/01/05 dmatthew       PK37117     Create - Fix HA                               */
/*                                                                                   */
/* ********************************************************************************* */

package com.ibm.ws.Transaction.JTA;

import com.ibm.tx.util.logging.Tr;
import com.ibm.tx.util.logging.TraceComponent;
import com.ibm.ws.Transaction.TranConstants;

//------------------------------------------------------------------------------
// Class: FailureScopeLifeCycle
//------------------------------------------------------------------------------
/**
* <p>
* 
* </p>
*/
public class FailureScopeLifeCycle
{
    // The count of requests currently on server for this FailureScope
    private int _activityCount;

    private boolean _disabled;

    private boolean _isLocal;

    private String _idStr;

    private static final TraceComponent tc = Tr.register(FailureScopeLifeCycle.class,TranConstants.TRACE_GROUP, TranConstants.NLS_FILE);

    public FailureScopeLifeCycle(String idStr, boolean isLocal)
    {
        _isLocal = isLocal;
        _idStr = new String(idStr);
    }

    public boolean isLocal()
    {
        return _isLocal;
    }

    // This method determines whether the failureScope is currently processing new requests and returns
    // whether it is or not.  
    // If the failureScope is accepting requests it increments the count of transactions currently
    // processing 2PC requests on this server for this RM's failureScope.
    // If the failureScope is not accepting requests the caller should act upon the false boolean return value
    // to cease processing of the current request and take the necessary action for this condition.
    // NOTE this does not include active transactions.
    public synchronized boolean ifAcceptingWorkIncrementActivityCount()
    {
        if (tc.isDebugEnabled()) Tr.debug(tc, "isAcceptingWork", new Boolean(!_disabled));
        if(!_disabled)
        {   
            _activityCount++;
            if (tc.isDebugEnabled()) Tr.debug(tc, "_activityCount", new Object[]{this, new Integer(_activityCount)});
            return true;
        } 
        return false;
    }

    // This decrements the count of transactions currently processing 2PC requests
    // on this server for this RM's failureScope.
    // NOTE this does not include active transactions.
    public synchronized void decrementActivityCount()
    {
        _activityCount--;
        if(_activityCount == 0)
        {
            this.notifyAll();
        }
        if (tc.isDebugEnabled()) Tr.debug(tc, "decrementActivityCount", new Object[]{this, new Integer(_activityCount)});
    }

    public int getActivityCount()
    {
        if (tc.isDebugEnabled()) Tr.debug(tc, "getActivityCount", new Object[]{this, new Integer(_activityCount)});
        return _activityCount;
    }

    public void stopAcceptingWork()
    {
        if (tc.isDebugEnabled()) Tr.debug(tc, "stopAcceptingWork", this);
        _disabled = true;
    }

    public String getIdentityString()
    {
        if (tc.isDebugEnabled()) Tr.debug(tc, "getIdentityString", new Object[]{this,_idStr});
        return _idStr;
    }

}
