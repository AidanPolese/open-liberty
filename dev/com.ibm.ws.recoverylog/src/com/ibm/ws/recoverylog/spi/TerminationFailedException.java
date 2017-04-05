/* ********************************************************************************* */
/* COMPONENT_NAME: WAS.transactions                                                  */
/*                                                                                   */
/* ORIGINS: 27                                                                       */
/*                                                                                   */
/* IBM Confidential OCO Source Material                                              */
/* 5724-i63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997, 2004 */
/* The source code for this program is not published or otherwise divested           */
/* of its trade secrets, irrespective of what has been deposited with the            */
/* U.S. Copyright Office.                                                            */
/*                                                                                   */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                                     */
/*                                                                                   */
/* DESCRIPTION:                                                                      */
/*                                                                                   */
/* Change History:                                                                   */
/*                                                                                   */
/* Date      Programmer    Defect       Description                                  */
/* --------  ----------    ------       -----------                                  */
/* 15/04/04  beavenj       LIDB1578.3   Termination support for ha-recovery          */
/* 15/06/04  beavenj       216563       Code review changes                          */
/* ********************************************************************************* */

package com.ibm.ws.recoverylog.spi;

//------------------------------------------------------------------------------
// Class: TerminationFailedException
//------------------------------------------------------------------------------
/**
* This exception can be thrown by a RecoveryAgents terminateRecovery method if
* termination processing failes for some reason.
*/
public class TerminationFailedException extends Exception
{
  public TerminationFailedException()
  {
      super();
  }
  
  public TerminationFailedException(Throwable cause)
  {
      super(cause);
  }
}

