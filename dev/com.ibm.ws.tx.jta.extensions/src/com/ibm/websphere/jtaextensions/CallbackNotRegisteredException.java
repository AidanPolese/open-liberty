package com.ibm.websphere.jtaextensions;
/* ************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                           */
/*                                                                            */
/*  ORIGINS: 27                                                               */
/*                                                                            */
/* IBM Confidential OCO Source Material                                       */
/* 5724-I63, 5724-H88, 5655-N01, 5733-W60                                     */
/* (C) COPYRIGHT International Business Machines Corp. 2002, 2005             */
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
/*  02-02-01  awilkins      LI850     Creation                                */
/*  21/02/03  gareth     LIDB1673.19  Make any unextended code final          */
/*  04-03-23  irobins       LIDB3133-23 Added API classification              */
/*  10-02-05   hursdlg    LIDB3706-5  Serialization                           */
/* ************************************************************************** */


/**
 * The exception is thrown by the transaction manager if an attempt is
 * made to unregister a <code>SynchronizationCallback</code> that is not
 * registered with the <code>ExtendedJTATransaction</code>.
 *
 * @ibm-api
 * @ibm-was-base
 * 
 */
public final class CallbackNotRegisteredException extends java.lang.Exception 
{
    private static final long serialVersionUID = -8757030444545754993L;
}
