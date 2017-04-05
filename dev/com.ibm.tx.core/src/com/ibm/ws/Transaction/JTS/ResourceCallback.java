package com.ibm.ws.Transaction.JTS;
/* ************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                           */
/*                                                                            */
/*  ORIGINS: 27                                                               */
/*                                                                            */
/* IBM Confidential OCO Source Material                                       */
/* 5630-A36. (C) COPYRIGHT International Business Machines Corp. 2003         */
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
/*  Date      Programmer    Defect   Description                              */
/*  --------  ----------    ------   -----------                              */
/*  17/01/03  awilkins      158466   Subord tx completion - destory timing    */
/* ************************************************************************** */

/** This interface should be implemented by all classes that require to
 *  receive resource callbacks.
 */
public interface ResourceCallback
{
    /** The resource is being destroyed. Any necessary cleanup
     *  should now be performed.
     */
    void destroy();
}
