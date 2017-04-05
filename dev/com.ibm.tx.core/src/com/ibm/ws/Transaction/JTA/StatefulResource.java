package com.ibm.ws.Transaction.JTA;
/* ************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                           */
/*                                                                            */
/* IBM Confidential OCO Source Material                                       */
/* 5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997, 2004    */
/* The source code for this program is not published or otherwise divested    */
/* of its trade secrets, irrespective of what has been deposited with the     */
/* U.S. Copyright Office.                                                     */
/*                                                                            */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                              */
/*                                                                            */
/*  Change History:                                                           */
/*                                                                            */
/*  Date      Programmer    Defect    Description                             */
/*  --------  ----------    ------    -----------                             */
/*  DD/MM/YY                                                                  */
/*  28/08/03   johawkes     173214    Replace RegisteredResource vectors      */
/*  30/01/04   johawkes     187239    Handle HeuristicHazard responses        */
/*  23/02/04   johawkes     190337    Preserve heuristic outcome              */
/*  18/05/07   johawkes     438575    Further componentization                */
/* ************************************************************************** */

public interface StatefulResource
{
    // Be careful if you want to change these!
    // HeuristicOutcome assumes NONE is zero.
	public static final int NONE                 = 0;
	public static final int REGISTERED           = 1;
	public static final int PREPARED             = 2;                                      // Defect 1412.1
	public static final int COMPLETING           = 3;
	public static final int COMPLETED            = 4;
    public static final int COMPLETING_ONE_PHASE = 5;
    public static final int ROLLEDBACK           = 6;
    public static final int COMMITTED            = 7;
    public static final int HEURISTIC_COMMIT     = 8;
    public static final int HEURISTIC_ROLLBACK   = 9;
    public static final int HEURISTIC_MIXED      = 10;
    public static final int HEURISTIC_HAZARD     = 11;
    
    // If you add another state you've got to change this
    public static final int numStates = 12;

	public int getResourceStatus();

	public void setResourceStatus(int status);
}