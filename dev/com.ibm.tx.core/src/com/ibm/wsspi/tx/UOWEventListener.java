/* ********************************************************************************* */
/* COMPONENT_NAME: WAS.transactions                                                  */
/*                                                                                   */
/* ORIGINS: 27                                                                       */
/*                                                                                   */
/* IBM Confidential OCO Source Material                                              */
/* 5724-i63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 2011       */
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
/*  11-10-10  johawkes    719396  Creation                                           */
/*                                                                                   */
/* ********************************************************************************* */
package com.ibm.wsspi.tx;

import com.ibm.ws.Transaction.UOWCallback;

/**
 * A service providing this interface will have the UOWEvent method called at various points during the lifecycle of a unit of work.
 */
public interface UOWEventListener
{
    public static final int POST_BEGIN = UOWCallback.POST_BEGIN;
    public static final int POST_END = UOWCallback.POST_END;
    public static final int SUSPEND = 100;
    public static final int RESUME = 110;
    public static final int REGISTER_SYNC = 120;

    /**
     * @param uow The Unit of work to which this event applies
     * @param event The event code
     * @param data Data associated with the event
     */
    public void UOWEvent(UOWEventEmitter uow, int event, Object data);
}
