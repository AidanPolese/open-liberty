package com.ibm.ws.tx.embeddable;
/* ********************************************************************************* */
/* COMPONENT_NAME: WAS.transactions                                                  */
/*                                                                                   */
/* ORIGINS: 27                                                                       */
/*                                                                                   */
/* IBM Confidential OCO Source Material                                              */
/* 5724-i63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 2009,2011  */
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
/*  Date      Programmer  Defect         Description                                 */
/*  --------  ----------  ------         -----------                                 */
/*  09-11-03  johawkes    F743-305.1     Creation                                    */
/*  11-11-24  johawkes    723423         Repackaging                                 */
/* ********************************************************************************* */

import javax.transaction.UserTransaction;

import com.ibm.ws.uow.UOWScopeCallback;

/**
 * <code>EmbeddableWebSphereUserTransaction</code> defines some Websphere extensions to
 * the UserTransaction interface.
 *
 * <p> This interface is private to WAS.
 * Any use of this interface outside the WAS Express/ND codebase 
 * is not supported.
 *
 */
public interface EmbeddableWebSphereUserTransaction extends UserTransaction
{
    /**
     *
     *  Register users who want notification on UserTransaction Begin and End
     *
     */

    public void registerCallback(UOWScopeCallback callback); // Defect 130321

    /**
     *
     *  Unregister users who want notification on UserTransaction Begin and End
     *
     */
    public void unregisterCallback(UOWScopeCallback callback);
}