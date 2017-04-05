/* ********************************************************************************* */
/* COMPONENT_NAME: WAS.transactions                                                  */
/*                                                                                   */
/* ORIGINS: 27                                                                       */
/*                                                                                   */
/* IBM Confidential OCO Source Material                                              */
/* 5724-I63, 5724-H88, 5655-N01, 5733-W61 (C) COPYRIGHT International Business Machines Corp. 2007 */
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
/* 17/01/07  mallam                     creation                                     */
/* ********************************************************************************* */

package com.ibm.ws.recoverylog.spi;

import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

//------------------------------------------------------------------------------
// Interface : AccessController
//------------------------------------------------------------------------------
/**
 * Interface to represent the abstraction of functions required by individual services
 * of the recovery log component.
 * Each product specific recoverylog service should implement this interface and make it available
 * to the services.
 */
public interface AccessController
{
   /**
    * Called to perform java2 security security manager (if one is available) function.
    * May throw PrivilegedActionException or PrivilegedExceptionAction.
    * @param action contains the code to run under the security manager control.
    */
   public Object doPrivileged(PrivilegedExceptionAction action) throws PrivilegedActionException;


}
