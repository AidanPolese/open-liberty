package com.ibm.ws.Transaction.JTA;
/* ***************************************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                                                      */
/*                                                                                                       */
/*  ORIGINS: 27                                                                                          */
/*                                                                                                       */
/* IBM Confidential OCO Source Material                                                                  */
/* 5724-I63, 5724-H88, 5655-N01, 5733-W60 (C) COPYRIGHT International Business Machines Corp. 2003, 2005 */
/* The source code for this program is not published or otherwise divested                               */
/* of its trade secrets, irrespective of what has been deposited with the                                */
/* U.S. Copyright Office.                                                                                */
/*                                                                                                       */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                                                         */
/*                                                                                                       */
/*  DESCRIPTION:                                                                                         */
/*                                                                                                       */
/*  Change History:                                                                                      */
/*                                                                                                       */
/*  Date      Programmer    Defect    Description                                                        */
/*  --------  ----------    ------    -----------                                                        */
/*  30/01/04   johawkes     187239    Handle HeuristicHazard responses                                   */
/*  16/05/05   hursdlg      274187    Add SUID for serialscan                                            */
/* ***************************************************************************************************** */

import javax.transaction.xa.XAException;

public final class HeuristicHazardException extends XAException
{
    // This exception is only used internally by the Tx service and is not serialized
    private static final long serialVersionUID = 491157435531839611L; /* @274187A*/
}
