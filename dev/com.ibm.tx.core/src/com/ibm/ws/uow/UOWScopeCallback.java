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

import com.ibm.ws.Transaction.UOWCallback;

public interface UOWScopeCallback
{
    /** 
     * A context change type indicating that a new
     * UOWScope is about to begin. <code>contextChange</code>
     * will receive a <code>null</code> UOWScope reference.
     */
    public static final int PRE_BEGIN  = UOWCallback.PRE_BEGIN;
    
    /**
     * A context change type indicating that a new
     * UOWScope has begun. <code>contextChange</code>
     * will receive a reference to the new scope.
     */
    public static final int POST_BEGIN = UOWCallback.POST_BEGIN;
   
    /**
     * A context change type indicating that a 
     * UOWScope is about to end. <code>contextChange</code>
     * will receive a reference to the ending scope.
     */   
    public static final int PRE_END    = UOWCallback.PRE_END;
    
    /** 
     * A context change type indicating that a
     * UOWScope has ended. <code>contextChange</code>
     * will receive a <code>null<code> UOWScope reference.
     */
    public static final int POST_END   = UOWCallback.POST_END;
    
    /** 
     * Invoked when a unit of work context change is occuring.
     * 
     * @param changeType The type of change that is occuring
     * @param uowScope The UOWScope to which the change relates
     * @throws IllegalStateException
     */
    public void contextChange(int changeType, UOWScope uowScope) throws IllegalStateException;
}
