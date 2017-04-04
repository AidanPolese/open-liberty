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
/* Date      Programmer    Defect      Description                                   */
/* --------  ----------    ------      -----------                                   */
/* 19/05/04  beavenj       LIDB1578.5  Create                                        */
/* 15/06/04  beavenj       216563      Code review changes                           */
/* ********************************************************************************* */

package com.ibm.ws.recoverylog.spi;

//------------------------------------------------------------------------------
// Interface: RecoveryLogCallBack
//------------------------------------------------------------------------------
/**
* This interface is implemented by those objects that are to be registered as
* recovery log callback objects.
*/
public interface RecoveryLogCallBack
{
   //------------------------------------------------------------------------------
   // Method: RecoveryLogCallBack.recoveryStarted
   //------------------------------------------------------------------------------
   /**
   * A request to begin recovery procesing for the given failure scope has been 
   * received and is about to be processed.
   *
   * @param failureScope The failure scope for which recovery processing is about
   *                     to started.
   */
   void recoveryStarted(FailureScope failureScope);

   //------------------------------------------------------------------------------
   // Method: RecoveryLogCallBack.recoveryCompleted
   //------------------------------------------------------------------------------
   /**
   * Recovery processing for the given failure scope has been completed. This 
   * applies to "first pass" recovery only. Services may be retrying recovery that
   * could not be completed during first pass recovery processing after this call 
   * has been issued. (eg transaction service periodically trying to contact a db
   * that was not contactable.)
   *
   * @param failureScope The failure scope for which recovery processing has just
   *                     been completed.
   */
   void recoveryCompleted(FailureScope failureScope);

   //------------------------------------------------------------------------------
   // Method: RecoveryLogCallBack.terminateStarted
   //------------------------------------------------------------------------------
   /**
   * A request to terminate recovery procesing for the given failure scope has been 
   * received and is about to be processed.
   *
   * @param failureScope The failure scope for which recovery processing is about
   *                     to be terminated.
   */
   void terminateStarted(FailureScope failureScope);

   //------------------------------------------------------------------------------
   // Method: RecoveryLogCallBack.terminateCompleted
   //------------------------------------------------------------------------------
   /**
   * Termination of recovery processing for the given failure scope has just been
   * completed.
   *
   * @param failureScope The failure scope for which recovery processing has just
   *                     been terminated.
   */
   void terminateCompleted(FailureScope failureScope);
}
