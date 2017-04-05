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
/* 01/07/03  beavenj       170101      Create                                 */
/* 04-03-24  awilkins LIDB2775.53.5.1  Exception chaining                     */
/* ************************************************************************** */

package com.ibm.ws.recoverylog.spi;

//------------------------------------------------------------------------------
// Class: LogFullException
//------------------------------------------------------------------------------
/**
* A requested operation is not available or cannot be issued in the present state
*/
public class LogFullException extends InternalLogException
{  
  protected LogFullException(Throwable cause)
  {
      super(cause);
  }
}

