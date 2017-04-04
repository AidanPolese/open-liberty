/* ************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                           */
/*                                                                            */
/* ORIGINS: 27                                                                */
/*                                                                            */
/* IBM Confidential OCO Source Material                                       */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2002, 2010*/
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
/* 08/03/10  mallam        642260      Custom logs                            */
/* ************************************************************************** */

package com.ibm.ws.recoverylog.spi;

//------------------------------------------------------------------------------
// Class: RecoverableUnitSectionExistsException
//------------------------------------------------------------------------------
/**
* This exception is generated if an attempt is made to create a new 
* RecoverableUnitSection with an identity that is already in use.
*/
public class RecoverableUnitSectionExistsException extends Exception
{
    public RecoverableUnitSectionExistsException()
    {
        this(null);
    }
    
    public RecoverableUnitSectionExistsException(Throwable cause)
    {
        super(cause);    
    }
}

