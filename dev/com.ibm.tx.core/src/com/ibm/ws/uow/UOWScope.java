/* ********************************************************************************* */
/* COMPONENT_NAME: WAS.transactions                                                  */
/*                                                                                   */
/* ORIGINS: 27                                                                       */
/*                                                                                   */
/* IBM Confidential OCO Source Material                                              */
/* 5724-i63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 2004       */
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
/*  04-05-07  awilkins    202175  Creation                                           */
/*                                                                                   */
/* ********************************************************************************* */
package com.ibm.ws.uow;

/** 
 * A abstract representation of an object that is responsible for
 * scoping units of work namely an ActivitySession, local transaction,
 * or global transaction.
 */
public interface UOWScope
{
    public void setTaskId(String taskId);
    
    public String getTaskId();
}
