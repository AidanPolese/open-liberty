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
/*  09-08-19  mallam      602532.3/6 ltc bundle                                      */
/* ********************************************************************************* */
package com.ibm.ws.uow;

/** 
 * An interface for components which support sending callback EVENTS to interested listeners.
 * Listeners can register using the registerCallback method.  Maybe this should be called
 * addListener ????
 */
public interface UOWScopeCallbackAgent
{
    void registerCallback(UOWScopeCallback callback);
    void unregisterCallback(UOWScopeCallback callback);
}
