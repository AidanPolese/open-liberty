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
/* 08/02/27  kaczyns       501042      Protected?                             */
/* ************************************************************************** */

package com.ibm.ws.recoverylog.spi;

//------------------------------------------------------------------------------
// Class: LogAllocationException
//------------------------------------------------------------------------------
/**
* This exception is generated when an attempt to create or extend a recovery
* log file failes (eg initial creation of a recovery log file during a cold
* start fails due to lack of disk space)
*/
public class LogAllocationException extends Exception
{
  public LogAllocationException(Throwable cause)
  {
    super(cause);
  }
}

