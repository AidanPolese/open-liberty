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
/* 22/07/04  beavenj         Create                                           */
/* 08/03/10  mallam        642260      Custom logs                            */
/* ************************************************************************** */

package com.ibm.ws.recoverylog.spi;

//------------------------------------------------------------------------------
// Class: LogIncompatibleException
//------------------------------------------------------------------------------
/**
* This exception is generated if an attempt is made by a client service to
* open an "incompatible" recovery log. By this we mean that one or more of the
* following attirubtes does not match the file being opened
*
* 1. Client Service Name
* 2. Client Service Version
* 3. Log Name
*
*/
public class LogIncompatibleException extends Exception
{
  public LogIncompatibleException()
  {
  }
}
