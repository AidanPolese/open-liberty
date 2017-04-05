/* ************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                           */
/*                                                                            */
/* ORIGINS: 27                                                                */
/*                                                                            */
/* IBM Confidential OCO Source Material                                       */
/* 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2002, 2004    */
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
/* ************************************************************************** */

package com.ibm.ws.recoverylog.spi;

//------------------------------------------------------------------------------
// Class: InvalidRecoverableUnitSectionException
//------------------------------------------------------------------------------
/**
* This exception indicates that an operation has failed as the caller has
* referenced a RecoverableUnitSection that is not recognised by the recovery log 
* service
*/
public class InvalidRecoverableUnitSectionException extends Exception
{
    public InvalidRecoverableUnitSectionException()
    {
        this(null);
    }
    
    protected InvalidRecoverableUnitSectionException(Throwable cause)
    {
        super(cause);
    }
}

