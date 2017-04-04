package com.ibm.tx.remote;

/* ********************************************************************************* */
/* COMPONENT_NAME: WAS.transactions                                                  */
/*                                                                                   */
/*  ORIGINS: 27                                                                      */
/*                                                                                   */
/* IBM Confidential OCO Source Material                                              */
/* 5724-i63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 2002, 2004 */
/* The source code for this program is not published or otherwise divested           */
/* of its trade secrets, irrespective of what has been deposited with the            */
/* U.S. Copyright Office.                                                            */
/*                                                                                   */
/* @(#) 1.6 SERV1/ws/code/was.transaction.impl/src/com/ibm/ws/Transaction/JTS/MinorCode.java, WAS.transactions, WAS855.SERV1, cf061521.02 2/20/07 12:09:33 [6/12/15 06:30:59]                                                     */
/*                                                                                   */
/*  DESCRIPTION:                                                                     */
/*                                                                                   */
/*  Change History:                                                                  */
/*                                                                                   */
/*  Date      Programmer    Defect   Description                                     */
/*  --------  ----------    ------   -----------                                     */
/*  31/10/02   hursdlg      ------   Move to JTA implementation                      */
/*  17/12/02   mallam    LIDB1673.xx Further changes for passive timeout             */
/*  07/07/03   hursdlg      171049   Reference ras minor codes                       */
/*  14/06/04   johawkes     209345   Organise imports                                */
/* ********************************************************************************* */

/**
 * This class simply contains minor code values for standard exceptions thrown
 * by the JTS.
 */

public interface MinorCode
{
    // 171049 - ras has 16 minor codes defined for transactions
    public static final int TRANSACTION_MINORCODE_BASE = 0x494210d0;// com.ibm.ejs.ras.WsCorbaMinor.transactionBase;  // 0x494210d0

    // minor codes for org.omg.CORBA.INTERNAL();
    public static final int NO_COORDINATOR = TRANSACTION_MINORCODE_BASE + 0x0;
    public static final int NO_GLOBAL_TID = TRANSACTION_MINORCODE_BASE + 0x1;
    public static final int LOGIC_ERROR = TRANSACTION_MINORCODE_BASE + 0x2;
    public static final int SERVER_BUSY = TRANSACTION_MINORCODE_BASE + 0x3;
}