/* ************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                           */
/*                                                                            */
/*  ORIGINS: 27                                                               */
/*                                                                            */
/* IBM Confidential OCO Source Material                                       */
/* 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2002, 2003    */
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
/*  Date      Programmer    Defect      Description                           */
/*  --------  ----------    ------      -----------                           */
/* 06/06/03  beavenj        LIDB2472.2  Create                                */
/* 08/07/03  amulholl       171270      Correct string name for Activity svc  */
/* 09/06/03  kaczyns        LIDB2561.1  Add ZTransactionManager               */
/* 12/11/03  hursdlg        LIDB2775    Merge zOS and distributed code        */
/* ************************************************************************** */

package com.ibm.ws.recoverylog.spi;

//------------------------------------------------------------------------------
// Interface: ClientId
//------------------------------------------------------------------------------
/**
* This interface contains static definitions of client service identifiers, names
* and RecoveryAgent sequence values. These are owned by the RLS to ensure
* uniqueness and compatibility between client services and are required for
* client service registration.
*/                                                                          
public interface ClientId
{                                                     
   /**
   * Recovery Log Client Identifiers (RLCI)
   */
   public static final int RLCI_TRANSACTIONSERVICE = 1;
   public static final int RLCI_ACTIVITYSERVICE = 2;
   public static final int RLCI_CSCOPESERVICE = 3;
   public static final int RLCI_ZTRANSACTIONSERVICE = 4;

   /**
   * Recovery Log Client Names (RLCN)
   */
   public static final String RLCN_TRANSACTIONSERVICE = "transaction";
   public static final String RLCN_ACTIVITYSERVICE = "activity";
   public static final String RLCN_CSCOPESERVICE = "compensation";
   public static final String RLCN_ZTRANSACTIONSERVICE = "Transaction.ws390";

   /**
   * Recovery Agent Sequence Values
   */
   public static final int RASEQ_TRANSACTIONSERVICE =  2;
   public static final int RASEQ_ACTIVITYSERVICE = 1;
   public static final int RASEQ_CSCOPESERVICE = 3;
   public static final int RASEQ_ZTRANSACTIONSERVICE = 2;
}
