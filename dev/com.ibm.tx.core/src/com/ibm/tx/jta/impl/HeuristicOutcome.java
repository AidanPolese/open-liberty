package com.ibm.tx.jta.impl;
/* ************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                           */
/*                                                                            */
/*  ORIGINS: 27                                                               */
/*                                                                            */
/* IBM Confidential OCO Source Material                                       */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2009 */
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
/*  Date      Programmer    Defect    Description                             */
/*  --------  ----------    ------    -----------                             */
/*  12/03/04  johawkes      194110    Fix heuristic on RMFAIL, RETRY & NOTA   */
/*  17/03/04  johawkes      192653    Cancel timeouts on RA uninstall         */
/*  13/09/04  mallam        231085    distributeForget                        */
/*  16/08/05  johawkes      290913.6  add commit/rollback                     */
/*  06/06/07  johawkes      443467    Moved                                   */
/*  20/07/07  hursdlg       452800    Method access                           */
/*  02/06/09  mallam        596067    package move                            */
/* ************************************************************************** */

import com.ibm.tx.util.logging.Tr;
import com.ibm.tx.util.logging.TraceComponent;
import com.ibm.tx.TranConstants;
import com.ibm.ws.Transaction.JTA.ResourceWrapper;
import com.ibm.ws.Transaction.JTA.StatefulResource;

// This is a table of states used for combining heuristic outcomes.
// Assumes Statefulesource.NONE = 0!!!!!
public final class HeuristicOutcome
{
    private static final TraceComponent tc =
        Tr.register(
                HeuristicOutcome.class,
                TranConstants.TRACE_GROUP,
                TranConstants.NLS_FILE);
    
    private static final int states[][] = new int[StatefulResource.numStates][StatefulResource.numStates];

    static
    {
        states[StatefulResource.REGISTERED][StatefulResource.HEURISTIC_COMMIT] = StatefulResource.HEURISTIC_COMMIT;
        states[StatefulResource.REGISTERED][StatefulResource.HEURISTIC_ROLLBACK] = StatefulResource.HEURISTIC_ROLLBACK;
        states[StatefulResource.REGISTERED][StatefulResource.HEURISTIC_MIXED] = StatefulResource.HEURISTIC_MIXED;
        states[StatefulResource.REGISTERED][StatefulResource.HEURISTIC_HAZARD] = StatefulResource.HEURISTIC_HAZARD;

        states[StatefulResource.PREPARED][StatefulResource.HEURISTIC_COMMIT] = StatefulResource.HEURISTIC_COMMIT;
        states[StatefulResource.PREPARED][StatefulResource.HEURISTIC_ROLLBACK] = StatefulResource.HEURISTIC_ROLLBACK;
        states[StatefulResource.PREPARED][StatefulResource.HEURISTIC_MIXED] = StatefulResource.HEURISTIC_MIXED;
        states[StatefulResource.PREPARED][StatefulResource.HEURISTIC_HAZARD] = StatefulResource.HEURISTIC_HAZARD;

        states[StatefulResource.COMPLETING][StatefulResource.HEURISTIC_COMMIT] = StatefulResource.HEURISTIC_COMMIT;
        states[StatefulResource.COMPLETING][StatefulResource.HEURISTIC_ROLLBACK] = StatefulResource.HEURISTIC_ROLLBACK;
        states[StatefulResource.COMPLETING][StatefulResource.HEURISTIC_MIXED] = StatefulResource.HEURISTIC_MIXED;
        states[StatefulResource.COMPLETING][StatefulResource.HEURISTIC_HAZARD] = StatefulResource.HEURISTIC_HAZARD;

        states[StatefulResource.COMPLETED][StatefulResource.HEURISTIC_COMMIT] = StatefulResource.HEURISTIC_COMMIT;
        states[StatefulResource.COMPLETED][StatefulResource.HEURISTIC_ROLLBACK] = StatefulResource.HEURISTIC_ROLLBACK;
        states[StatefulResource.COMPLETED][StatefulResource.HEURISTIC_MIXED] = StatefulResource.HEURISTIC_MIXED;
        states[StatefulResource.COMPLETED][StatefulResource.HEURISTIC_HAZARD] = StatefulResource.HEURISTIC_HAZARD;

        states[StatefulResource.COMPLETING_ONE_PHASE][StatefulResource.HEURISTIC_COMMIT] = StatefulResource.HEURISTIC_COMMIT;
        states[StatefulResource.COMPLETING_ONE_PHASE][StatefulResource.HEURISTIC_ROLLBACK] = StatefulResource.HEURISTIC_ROLLBACK;
        states[StatefulResource.COMPLETING_ONE_PHASE][StatefulResource.HEURISTIC_MIXED] = StatefulResource.HEURISTIC_MIXED;
        states[StatefulResource.COMPLETING_ONE_PHASE][StatefulResource.HEURISTIC_HAZARD] = StatefulResource.HEURISTIC_HAZARD;

        states[StatefulResource.ROLLEDBACK][StatefulResource.HEURISTIC_COMMIT] = StatefulResource.HEURISTIC_MIXED;
        states[StatefulResource.ROLLEDBACK][StatefulResource.HEURISTIC_ROLLBACK] = StatefulResource.HEURISTIC_ROLLBACK;
        states[StatefulResource.ROLLEDBACK][StatefulResource.HEURISTIC_MIXED] = StatefulResource.HEURISTIC_MIXED;
        states[StatefulResource.ROLLEDBACK][StatefulResource.HEURISTIC_HAZARD] = StatefulResource.HEURISTIC_HAZARD;
        states[StatefulResource.ROLLEDBACK][StatefulResource.COMMITTED] = StatefulResource.HEURISTIC_MIXED;

        states[StatefulResource.COMMITTED][StatefulResource.HEURISTIC_COMMIT] = StatefulResource.HEURISTIC_COMMIT;
        states[StatefulResource.COMMITTED][StatefulResource.HEURISTIC_ROLLBACK] = StatefulResource.HEURISTIC_MIXED;
        states[StatefulResource.COMMITTED][StatefulResource.HEURISTIC_MIXED] = StatefulResource.HEURISTIC_MIXED;
        states[StatefulResource.COMMITTED][StatefulResource.HEURISTIC_HAZARD] = StatefulResource.HEURISTIC_HAZARD;

        states[StatefulResource.HEURISTIC_COMMIT][StatefulResource.HEURISTIC_ROLLBACK] = StatefulResource.HEURISTIC_MIXED;
        states[StatefulResource.HEURISTIC_COMMIT][StatefulResource.HEURISTIC_MIXED] = StatefulResource.HEURISTIC_MIXED;
        states[StatefulResource.HEURISTIC_COMMIT][StatefulResource.HEURISTIC_HAZARD] = StatefulResource.HEURISTIC_HAZARD;

        states[StatefulResource.HEURISTIC_ROLLBACK][StatefulResource.HEURISTIC_MIXED] = StatefulResource.HEURISTIC_MIXED;
        states[StatefulResource.HEURISTIC_ROLLBACK][StatefulResource.HEURISTIC_HAZARD] = StatefulResource.HEURISTIC_HAZARD;

        states[StatefulResource.HEURISTIC_MIXED][StatefulResource.HEURISTIC_HAZARD] = StatefulResource.HEURISTIC_MIXED;
    }

    public static int combineStates(int left, int right)
    {
        int result;

        // Assumes StatefulResource.NONE == 0
        if(left == StatefulResource.NONE || left == StatefulResource.COMPLETED)
        {
        	result = right;
        }
        else if(right == StatefulResource.NONE || right == StatefulResource.COMPLETED)
        {
        	result = left;
        }
        else if(left == right)
        {
            result = left;
        }
        else if(left < right) // We only filled in one half of the matrix
        {
            result = states[left][right];
        }
        else 
        {    
            result = states[right][left];
        }

        if(tc.isDebugEnabled())
        {
            Tr.debug(tc, ResourceWrapper.printResourceStatus(left) + " + " + ResourceWrapper.printResourceStatus(right) + " = " + ResourceWrapper.printResourceStatus(result));
        }
        
        return result;
    }

	/**
	 * @param outcome
	 * @return
	 */
	public static boolean isHeuristic(int outcome)
    {
        switch(outcome)
        {
        case StatefulResource.HEURISTIC_COMMIT:
        case StatefulResource.HEURISTIC_MIXED:
        case StatefulResource.HEURISTIC_HAZARD:
        case StatefulResource.HEURISTIC_ROLLBACK:
            
            return true;
            
        default:
            
            return false;
        }
	}
}
