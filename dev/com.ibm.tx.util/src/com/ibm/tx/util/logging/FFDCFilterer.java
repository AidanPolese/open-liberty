/* **************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                             */
/*                                                                              */
/* IBM Confidential OCO Source Material                                         */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2012 */
/* The source code for this program is not published or otherwise divested      */
/* of its trade secrets, irrespective of what has been deposited with the       */
/* U.S. Copyright Office.                                                       */
/*                                                                              */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                                */
/*                                                                              */
/*  Change History:                                                             */
/*                                                                              */
/*  Date      Programmer  Defect      Description                               */
/*  --------  ----------  ------      -----------                               */
/*  12-05-24  timmccor    734766      Enhance FFDC error reporting              */
/* **************************************************************************** */
package com.ibm.tx.util.logging;

public interface FFDCFilterer
{
    void processException(Throwable e, String s1, String s2, Object o);

    void processException(Throwable e, String s1, String s2);
    
    void processException(Throwable th, String sourceId, String probeId, Object[] objectArray);
    
    void processException(Throwable th, String sourceId, String probeId, Object callerThis, Object[] objectArray);
}
