package com.ibm.tx;
/* ************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                           */
/*                                                                            */
/*  ORIGINS: 27                                                               */
/*                                                                            */
/* IBM Confidential OCO Source Material                                       */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2002, 2013 */
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
/*  Date      Developer  Defect       Description                             */
/*  --------  ---------  ------       -----------                             */
/*  02-11-18  irobins                 Creation                                */
/*  21/02/03  gareth     LIDB1673.19  Make any unextended code final          */
/*  23-03-04  mdobbie    LIDB3133-23  Added SPI classification                */
/*  07-04-27  awilkins   416102       Separate out WAS-specific constants     */
/*  07-06-18  johawkes   444613       Repackaging                             */
/*  09-08-19  mallam     602532.3     ltc bundle                              */
/*  13-08-13  slaterpa   752004       TRANSUMMARY trace                       */
/* ************************************************************************** */
public class TranConstants
{
    public static final String NLS_FILE = "com.ibm.ws.Transaction.resources.TransactionMsgs";    
    public static final String LTC_NLS_FILE = "com.ibm.ws.LocalTransaction.resources.LocalTranMsgs";
    public static final String TRACE_GROUP = "Transaction";
    public static final String SUMMARY_TRACE_GROUP = "TransactionSummary";
}
