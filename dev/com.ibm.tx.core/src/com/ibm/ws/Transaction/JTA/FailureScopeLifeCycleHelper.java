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
/* DESCRIPTION:                                                                      */
/*                                                                                   */
/* Change History:                                                                   */
/*                                                                                   */
/* Date      Programmer    Defect      Description                                   */
/* --------  ----------    ------      -----------                                   */
/* 07/01/05  dmatthew      PK37117     Create - Fix HA                               */
/* 07/05/17  johawkes      438575      Further componentization                      */
/* 07/10/03  hursdlg       471680      Temporary fix for z/os                        */
/* 07/10/04  hursdlg       471680.1    Apply 438695.3/PK37117                        */
/* 08/09/30  maples        543125      Change HashMap to Hashtable                   */
/* ********************************************************************************* */

package com.ibm.ws.Transaction.JTA;

import java.util.Hashtable;

import com.ibm.tx.util.logging.Tr;
import com.ibm.tx.util.logging.TraceComponent;
import com.ibm.tx.TranConstants;

import com.ibm.ws.recoverylog.spi.FailureScope;

//------------------------------------------------------------------------------
// Class: FailureScopeLifeCycleHelper
//------------------------------------------------------------------------------
/**
* 
*/
public class FailureScopeLifeCycleHelper
{
    private static final TraceComponent tc = Tr.register(FailureScopeLifeCycleHelper.class,TranConstants.TRACE_GROUP, TranConstants.NLS_FILE);

    // Dummy string which will be overwritten by WAS implementation.
    protected static String _non_null_identityString = "_local_transaction_service_identityString";
    protected static final Hashtable<String, FailureScopeLifeCycle> _activeFSLC = new Hashtable<String, FailureScopeLifeCycle>();

    public static FailureScopeLifeCycle addToActiveList(FailureScope fs, boolean isLocal)
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "addToActiveList",new Object[]{fs, new Boolean(isLocal)});

        if(fs == null)
        {
            if (tc.isEntryEnabled()) Tr.exit(tc, "addToActiveList", null);
            return null;
        }

        // We can skip explicit synchronization between activation and deactivation threads
        // since removeFromActiveList() is only called AFTER prepareToShutdown().
        final FailureScopeLifeCycle fslc = new FailureScopeLifeCycle(_non_null_identityString, isLocal);
        _activeFSLC.put(_non_null_identityString, fslc);

        if (tc.isEntryEnabled()) Tr.exit(tc, "addToActiveList", fslc);
        return fslc;
    }

    public static void removeFromActiveList(FailureScopeLifeCycle fslc)
    {
        /*
         * Caller must ensure that this method is not called before addToActiveList for the same failureScope
         */
        if (tc.isEntryEnabled()) Tr.entry(tc, "removeFromActiveList", fslc);        

        if(fslc!=null)
        {
            synchronized(fslc)
            {
                fslc.stopAcceptingWork();  
                while(fslc.getActivityCount() >0)
                {
                    try
                    {
                        fslc.wait();
                    }
                    catch(InterruptedException ie)
                    {
                    }
                }

                _activeFSLC.remove(fslc.getIdentityString());    
            }
        }

        if (tc.isEntryEnabled()) Tr.exit(tc, "removeFromActiveList");
    }
}
