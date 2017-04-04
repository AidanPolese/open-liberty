/* ********************************************************************************* */
/* COMPONENT_NAME: WAS.transactions                                                  */
/*                                                                                   */
/* ORIGINS: 27                                                                       */
/*                                                                                   */
/* IBM Confidential OCO Source Material                                              */
/* 5724-i63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 2009       */
/* The source code for this program is not published or otherwise divested           */
/* of its trade secrets, irrespective of what has been deposited with the            */
/* U.S. Copyright Office.                                                            */
/*                                                                                   */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                                     */
/*                                                                                   */
/*  DESCRIPTION:                                                                     */
/*                                                                                   */
/*  Change History:                                                                  */
/*                                                                                   */
/*  YY-MM-DD  Programmer  Defect  Description                                        */
/*  --------  ----------  ------  -----------                                        */
/*  09-08-27  mallam      602532.3  Creation                                         */
/*                                                                                   */
/* ********************************************************************************* */
package com.ibm.ws.uow;

/** 
 * A abstract representation of an object that is responsible for
 * storing information about LocalTransactions across its lifetime - namely an ActivitySession
 * or global transaction.
 */
public interface UOWScopeLTCAware extends UOWScope
{
    public void setCompletedLTCBoundary(Byte boundary);
    
    public Byte getCompletedLTCBoundary();
}
