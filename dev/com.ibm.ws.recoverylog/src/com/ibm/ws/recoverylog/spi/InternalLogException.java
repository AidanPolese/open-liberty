/* ************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                           */
/*                                                                            */
/* ORIGINS: 27                                                                */
/*                                                                            */
/* IBM Confidential OCO Source Material                                       */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2012 */
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
/* 08-02-27  kaczyns       501042      Protected?                             */
/* 12-10-10  nyoung        735581.4    Repackage lib support and allow DB config  */
/* ************************************************************************** */

package com.ibm.ws.recoverylog.spi;

//------------------------------------------------------------------------------
// Class: InternalLogException
//------------------------------------------------------------------------------
/**
* This exception indicates that an operation has failed due to an unexpected
* error condition. The recovery log service is in an undefined state and continued
* use may be impossible.
*/
public class InternalLogException extends Exception
{
    String reason = null;
    public InternalLogException()
    {
        this(null);  
    }
    public InternalLogException(Throwable cause)
    {
        super(cause);
    }
    public InternalLogException(String s,Throwable cause)
    {
        super(s, cause);
        reason = s;
    }  
    public String toString()
    {
        if(reason != null)
            return reason + ", " + super.toString();
        else
            return super.toString();
    }
}

