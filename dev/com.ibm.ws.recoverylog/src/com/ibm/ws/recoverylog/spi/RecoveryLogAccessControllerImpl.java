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





//------------------------------------------------------------------------------
// Class: RecoveryLogAccessControllerImpl
//------------------------------------------------------------------------------
/**
 * WAS implementation of the service interface required by the recovery
 * log component.
 */
public class RecoveryLogAccessControllerImpl implements AccessController
{
//  private static final TraceComponent tc = Tr.register(RecoveryLogAccessControllerImpl.class,
//      TraceConstants.TRACE_GROUP, TraceConstants.NLS_FILE);


   /**
    * 
    */
   public Object doPrivileged(java.security.PrivilegedExceptionAction action) 
       throws  java.security.PrivilegedActionException
   {
//        if (tc.isEntryEnabled()) Tr.entry(tc, "doPrivileged", action);

//        Object result = com.ibm.ws.security.util.AccessController.doPrivileged(action);
       java.lang.SecurityManager sm = System.getSecurityManager(); 
       if (sm == null) { 
           try { 
               return action.run(); 
           } catch (java.lang.RuntimeException e) { 
               throw e; 
           } catch (Exception e) { 
               throw new java.security.PrivilegedActionException(e); 
           } 
       } else { 
           return java.security.AccessController.doPrivileged(action); 
       } 
//        if(tc.isEntryEnabled()) Tr.exit(tc, "doPrivileged", result);
//        return result;
   }

}
