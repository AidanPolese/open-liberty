/* ************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                           */
/*                                                                            */
/* ORIGINS: 27                                                                */
/*                                                                            */
/* IBM Confidential OCO Source Material                                       */
/* 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2005          */
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
/* Date      Programmer    Defect      Description                            */
/* --------  ----------    ------      -----------                            */
/* 05-01-19  mdobbie       LI3603      Creation                               */
/* ************************************************************************** */

package com.ibm.ws.recoverylog.spi;

//------------------------------------------------------------------------------
// Class: RLSInvalidSuspendTokenException
//------------------------------------------------------------------------------
public class RLSInvalidSuspendTokenException extends Exception
{
    public RLSInvalidSuspendTokenException() 
    {
    }

    public RLSInvalidSuspendTokenException(String msg) 
    {
        super(msg);
    }

    public RLSInvalidSuspendTokenException(Throwable throwable) 
    {
        super(throwable);
    }

    public RLSInvalidSuspendTokenException(String msg, Throwable throwable) 
    {
        super(msg, throwable);
    }
}

