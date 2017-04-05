package com.ibm.ws.Transaction.JTA;
/* ************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                           */
/*                                                                            */
/* IBM Confidential OCO Source Material                                       */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2005 */
/* The source code for this program is not published or otherwise divested    */
/* of its trade secrets, irrespective of what has been deposited with the     */
/* U.S. Copyright Office.                                                     */
/*                                                                            */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                              */
/*                                                                            */
/*  Change History:                                                           */
/*                                                                            */
/*  Date      Programmer    Defect       Description                          */
/*  --------  ----------    ------       -----------                          */
/*  DD/MM/YY                                                                  */
/*  28/08/03   johawkes     173214    Replace RegisteredResource vectors      */
/*  23/02/04   johawkes     190337    Preserve heuristic outcome              */
/*  06/01/06   johawkes     306998.12 Use TraceComponent.isAnyTracingEnabled()*/
/*  18/05/07   johawkes     438575    Further componentization                */
/* ************************************************************************** */

import com.ibm.tx.util.logging.Tr;
import com.ibm.tx.util.logging.TraceComponent;
import com.ibm.ws.Transaction.TranConstants;

public class ResourceWrapper implements StatefulResource
{
    private static final TraceComponent tc =
        Tr.register(
            ResourceWrapper.class,
            TranConstants.TRACE_GROUP,
            TranConstants.NLS_FILE);

    // StatefulResource.NONE has to be 0
    private int _resourceStatus;// = StatefulResource.NONE;

    /**
     * @return
     */
    public int getResourceStatus()
    {
        return _resourceStatus;
    }

    /**
     * @param status
     */
    public void setResourceStatus(int status)
    {
        if (tc.isDebugEnabled())
            Tr.debug(tc, "setResourceStatus",
                    "from " +
                    printResourceStatus(_resourceStatus) +
                    " to " +
                    printResourceStatus(status));

        _resourceStatus = status;
    }

    public static String printResourceStatus(int status)
	{
    	switch(status)
		{
        case StatefulResource.NONE:
            return "NONE";
        case StatefulResource.REGISTERED:
            return "REGISTERED";
        case StatefulResource.PREPARED:
            return "PREPARED";
        case StatefulResource.COMPLETING:
            return "COMPLETING";
        case StatefulResource.COMPLETED:
            return "COMPLETED";
        case StatefulResource.COMMITTED:
            return "COMMITTED";
        case StatefulResource.ROLLEDBACK:
            return "ROLLEDBACK";
        case StatefulResource.HEURISTIC_COMMIT:
            return "HEURISTIC_COMMIT";
        case StatefulResource.HEURISTIC_ROLLBACK:
            return "HEURISTIC_ROLLBACK";
        case StatefulResource.HEURISTIC_MIXED:
            return "HEURISTIC_MIXED";
        case StatefulResource.HEURISTIC_HAZARD:
            return "HEURISTIC_HAZARD";
        case StatefulResource.COMPLETING_ONE_PHASE:
            return "COMPLETING_ONE_PHASE";
        default:
            return "ILLEGAL STATE";
    	}
    }
}