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
/* Date      Programmer  Defect         Description                                  */
/* --------  ----------  ------         -----------                                  */
/* 06/06/03  beavenj     LIDB2472.2     Create                                       */
/* 23/06/03  beavenj     170088         Make FailureScope serializable               */
/* 04-01-09  awilkins    LIDB2775-53.5  z/OS code merge                              */
/* 04-03-26  awilkins  LIDB2775-53.5.2  More z/OS code merge changes                 */
/* 13/04/04  beavenj     LIDB1578.1     Initial supprort for ha-recovery             */
/* 15/06/04  beavenj     216563         Code review changes                          */
/* 05/11/04  beavenj     241848         isSameExecutionZone interface                */
/* ********************************************************************************* */

package com.ibm.ws.recoverylog.spi;

//------------------------------------------------------------------------------
// Interface: FailureScope
//------------------------------------------------------------------------------
/**
* The "failure scope" is defined as a potential region of failure (such as an
* application server) All operations that take place within the WebSphere
* deployment do so under a given failure scope. For example, a transaction 
* running on application server 1 is operating under the failure scope for 
* server 1. 
*/                                                                          
public interface FailureScope
{
  //------------------------------------------------------------------------------
  // Method: FailureScope.isContainedBy
  //------------------------------------------------------------------------------
  /**
  * Returns true if the target failure scope is encompassed by failureScope. For
  * example, if the target failure scope identifies a server region inside a z/OS
  * scalable server identified by failureScope then this method returns true.
  *
  * @param failureScope Failure scope to test
  *
  * @return boolean Flag indicating if the target failure scope is contained by the
  *                 specified failure scope
  */
  public boolean isContainedBy(FailureScope failureScope);
  
  //------------------------------------------------------------------------------
  // Method: FailureScope.serverName
  //------------------------------------------------------------------------------
  /**
  * Returns the name of the server identified by this failure scope.
  *
  * @return String The associated server name
  */
  public String serverName();

  //------------------------------------------------------------------------------
  // Method: FileFailureScope.isSameExecutionZone
  //------------------------------------------------------------------------------
  /**
  * Returns true if this failure scope represents the same general recovery scope as
  * the input parameter.  For instance, if more than one FailureScope was created
  * which referenced the same server, they would be in the same execution zone.
  *
  * @param anotherScope Failure scope to test
  *
  * @return boolean Flag indicating if the target failure scope represents the 
  *                 same logical failure scope as the specified failure scope.
  */
  public boolean isSameExecutionZone(FailureScope anotherScope);
}
