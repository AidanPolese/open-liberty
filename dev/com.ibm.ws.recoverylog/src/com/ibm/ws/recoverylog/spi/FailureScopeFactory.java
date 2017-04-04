/* ************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                           */
/*                                                                            */
/* ORIGINS: 27                                                                */
/*                                                                            */
/* IBM Confidential OCO Source Material                                       */
/* 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2004          */
/* The source code for this program is not published or otherwise divested    */
/* of its trade secrets, irrespective of what has been deposited with the     */
/* U.S. Copyright Office.                                                     */
/*                                                                            */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                              */
/*                                                                            */
/* DESCRIPTION:                                                               */
/*                                                                            */
/* Change History:                                                            */
/*                                                                            */
/* Date      Programmer  Defect         Description                           */
/* --------  ----------  ------         -----------                           */
/* 04-01-09  awilkins    LIDB2775-53.5  Creation - z/OS code merge            */
/* 04-03-24  awilkins  LIDB2775.53.5.1  Move to public component              */
/* 04-04-01  hursdlg     196855         Define epoch failure scope id         */
/*                                                                            */
/* ************************************************************************** */

package com.ibm.ws.recoverylog.spi;

public interface FailureScopeFactory
{
    public static final Byte FILE_FAILURE_SCOPE_ID = new Byte((byte)1);
    public static final Byte SERVANT_FAILURE_SCOPE_ID = new Byte((byte)2);
    public static final Byte CONTROLLER_FAILURE_SCOPE_ID = new Byte ((byte)3);
    public static final Byte EPOCH_FAILURE_SCOPE_ID = new Byte ((byte)4);
        
    public FailureScope toFailureScope(byte[] bytes);
    public byte[] toByteArray(FailureScope failureScope);
}
