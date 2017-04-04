/* ************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                           */
/*                                                                            */
/* ORIGINS: 27                                                                */
/*                                                                            */
/* IBM Confidential OCO Source Material                                       */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70(C) COPYRIGHT International Business Machines Corp. 2002, 2010 */
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
/* 06/06/03  beavenj       LIDB2472.2  Create                                 */
/* 04-03-24  awilkins LIDB2775.53.5.1  Exception chaining                     */
/* 04-03-26  awilkins LIDB2775-53.5.2  More z/OS code merge changes           */
/* 10-03-08  mallam        642260      Custom logs                            */
/* ************************************************************************** */

package com.ibm.ws.recoverylog.spi;

//------------------------------------------------------------------------------
// Class: LogClosedException
//------------------------------------------------------------------------------
/**
* This exception is generated when an attempt is made to call an operation that
* requires the log to be open but the log is actually closed.
*/
public class LogClosedException extends Exception
{
    public LogClosedException()
    {
        this(null);
    }
    
    public LogClosedException(Throwable cause)
    {
        super(cause);   
    }
}

