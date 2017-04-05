package com.ibm.tx.ltc.embeddable.impl;
/* ************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                           */
/*                                                                            */
/*  ORIGINS: 27                                                               */
/*                                                                            */
/* IBM Confidential OCO Source Material                                       */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70. (C) COPYRIGHT International Business Machines Corp. 2002, 2007, 2008   */
/* The source code for this program is not published or otherwise divested    */
/* of its trade secrets, irrespective of what has been deposited with the     */
/* U.S. Copyright Office.                                                     */
/*                                                                            */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                              */
/*                                                                            */
/*  DESCRIPTION:                                                              */
/*                                                                            */
/*  Change History:                                                           */
/*                                                                            */
/*  Date      Develoepr  Defect       Description                             */
/*  --------  ---------   ------      -----------                             */
/*  21/05/02  gareth     130321       Initial Creation                        */
/*  09/09/02  gareth     ------       Move to JTA implementation              */
/*  19/11/02  awilkins   1507         JTS -> JTA. Thread local restructuring  */
/*  21/02/03  gareth     LIDB1673.19  Make any unextended code final          */
/*  30/11/03  hursdlg    LIDB2775     z/OS distributed merge                  */
/*  07-04-27  awilkins   416102       LTC_NLS_FILE constant is WAS-specific   */  
/*  24/05/08  johawkes   522569       Perf trace                              */
/* ************************************************************************** */

import java.util.ArrayList;

import com.ibm.ejs.ras.Tr;
import com.ibm.ejs.ras.TraceComponent;
import com.ibm.tx.TranConstants;
import com.ibm.tx.jta.embeddable.EmbeddableTransactionManagerFactory;
import com.ibm.ws.Transaction.UOWCallback;
import com.ibm.ws.Transaction.UOWCoordinator;
import com.ibm.ws.Transaction.UOWCurrent;

public final class LTCCallbacks
{
    //
    // Static initialisation for
    // singleton instance.
    //
    static private LTCCallbacks _instance = new LTCCallbacks();

    /**
     * Collection of interested components.
     */
    private ArrayList<UOWCallback> _callbacks = new ArrayList<UOWCallback>();

    /**
     * Reference to the <code>UOWCurrent</code> implementation.
     */
    private final static UOWCurrent _uowCurrent = EmbeddableTransactionManagerFactory.getUOWCurrent();

    private static final TraceComponent tc = Tr.register(LTCCallbacks.class, TranConstants.TRACE_GROUP, TranConstants.LTC_NLS_FILE);

    private LTCCallbacks(){}

    static public LTCCallbacks instance()
    {
        return _instance;
    }

    /**
     * Register a <code>UOWCallback</code> for LTC notifications.
     * 
     * @param callback The UOWCallback object to register with the LocalTransaction service
     */
    public void registerCallback(UOWCallback callback)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) Tr.entry(tc, "registerCallback", callback);

        if (!_callbacks.contains(callback))
        {
            _callbacks.add(callback);

            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) Tr.debug(tc, "Number of registered Callbacks: "+_callbacks.size());
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) Tr.exit(tc, "registerCallback");
    }

    /**
     * Notify registered callbacks of context change.
     */
    public void contextChange(int typeOfChange) throws IllegalStateException
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
        {
            String type = "UNKNOWN";
            switch (typeOfChange)
            {
            case UOWCallback.PRE_BEGIN:
                type = "PRE_BEGIN";
                break;
            case UOWCallback.POST_BEGIN:
                type = "POST_BEGIN";
                break;
            case UOWCallback.PRE_END:
                type = "PRE_END";
                break;
            case UOWCallback.POST_END:
                type = "POST_END";
                break;
            }

            Tr.entry(tc, "contextChange", type);
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) Tr.debug(tc, "UOWCurrent", _uowCurrent);

        //
        // Need to get the current UOWCoordinator if 
        // we are in POST_BEGIN or PRE_END
        //
        UOWCoordinator coord = null;

        if ((typeOfChange == UOWCallback.POST_BEGIN) || (typeOfChange == UOWCallback.PRE_END))
        {
            coord = _uowCurrent.getUOWCoord();
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) Tr.debug(tc, "Coordinator="+coord);

        IllegalStateException ex = null;

        //
        // Inform the registered callbacks
        //
        for (int i = 0; i < _callbacks.size(); i++)
        {
            UOWCallback callback = _callbacks.get(i);

            try
            {
                callback.contextChange(typeOfChange, coord);
            }
            catch (IllegalStateException ise)
            {   
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) Tr.debug(tc, "Exception caught during UOW callback at context change", ise);
                ex = ise;
            }
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) Tr.exit(tc, "contextChange");

        //
        // If one of the callbacks threw an exception 
        // then rethrow here. In practice the last exception 
        // to be thrown will be re-thrown but as long as 
        // we throw it doesn't matter.
        //
        if (ex != null)
        {
            throw ex;
        }
    }
}
