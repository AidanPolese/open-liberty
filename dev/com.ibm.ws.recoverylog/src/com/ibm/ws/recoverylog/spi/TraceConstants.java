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
/*  Date      Programmer    Defect    Description                             */
/*  --------  ----------    ------    -----------                             */
/* 06/06/03  beavenj        LIDB2472.2  Create                                 */
/* 04/07/04  kaczyns        MD19484     Add NLS file for tran component       */
/* 20/01/05  mdobbie        LI3603      New Recovery Log specific NLS file    */
/* ************************************************************************** */

package com.ibm.ws.recoverylog.spi;

public class TraceConstants
{
    public static final String TRACE_GROUP = "Transaction";
    public static final String NLS_FILE = "com.ibm.ws.recoverylog.resources.RecoveryLogMsgs"; 
}
