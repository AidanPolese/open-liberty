/* ************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                           */
/*                                                                            */
/*  ORIGINS: 27                                                               */
/*                                                                            */
/* IBM Confidential OCO Source Material                                       */
/* 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2002, 2004    */
/* The source code for this program is not published or otherwise divested    */
/* of its trade secrets, irrespective of what has been deposited with the     */
/* U.S. Copyright Office.                                                     */
/*                                                                            */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                              */
/*                                                                            */
/*  DESCRIPTION:                                                              */
/*                                                                            */
/*  Change History:                                                           */
/*                                                                            */
/*  Date      Programmer    Defect    Description                             */
/*  --------  ----------    ------    -----------                             */
/*  02-02-14  awilkins      LIDB850   Creation                                */
/*  04-03-23  irobins       LIDB3133-23 Added per-tran Sync. Added API classif*/
/*  06-08-09  awilkins      LIDB4244  Deprecated reg sync for current tx      */ 
/*                                                                            */
/* ************************************************************************** */

package com.ibm.websphere.jtaextensions;

/**
 * A WebSphere programming model extension to the J2EE JTA support.  An 
 * object implementing this interface will be bound, by WebSphere J2EE 
 * containers that support this interface, at 
 * <code>java:comp/websphere/ExtendedJTATransaction</code>. 
 * Access to this object, when 
 * called from an EJB container, is not restricted to BMTs.  
 *
 * @ibm-api
 * @ibm-was-base
 * 
 */
public interface ExtendedJTATransaction 

{
   /**
    * Returns the <code>CosTransactions::PropagationContext::TransIdentity::tid</code> for 
    * the transaction currently associated with the calling thread.  
    * 
    * @return the current transaction <code>tid</code> in the form of a byte array.
    *    If there is no active transaction currently associated with the thread,
    *    returns null;
    */
   byte[] getGlobalId();

   /**
    * Returns a process-unique identifier for the transaction currently associated
    * with the calling thread. The local-id is valid only within the local process.
    * The local-id is recovered as part of the state of a recovered transaction.
    * 
    * @return an integer that uniquely identifies the current transaction within
    *    the calling process. If there is no active transaction currently associated with the thread, returns 0;
    */
   int getLocalId();

   /**
    * Register a SynchronizationCallback
    * {@link com.ibm.websphere.jtaextensions.SynchronizationCallback SynchronizationCallback}
    * object with the transaction manager.
    * The registered <code>sync</code> receives notification of the completion
    * of each transaction mediated by the transaction manager in the local JVM.
    * 
    * @param sync An object implementing the
    *    {@link com.ibm.websphere.jtaextensions.SynchronizationCallback SynchronizationCallback}
    *    interface.
    *
    * @exception NotSupportedException Thrown if this method is called from an environment
    *     or at a time when the function is not available.
    *
    */
   void registerSynchronizationCallback(SynchronizationCallback sync)
                 throws NotSupportedException;

   /**
    * Register a SynchronizationCallback
    * {@link com.ibm.websphere.jtaextensions.SynchronizationCallback SynchronizationCallback}
    * object for the current transaction.
    * The registered <code>sync</code> receives notification of the completion
    * of the transaction in which it is registered.
    * 
    * @param sync An object implementing the
    *    {@link com.ibm.websphere.jtaextensions.SynchronizationCallback SynchronizationCallback}
    *    interface.
    *
    * @exception NotSupportedException Thrown if this method is called from an environment
    *     or at a time when the function is not available.
    *     
    * @deprecated This method is deprecated in favor of registerInterposedSynchronization
    * on javax.transaction.TransactionSynchronizationRegistry.
    */
   void registerSynchronizationCallbackForCurrentTran(SynchronizationCallback sync)
                 throws NotSupportedException;

   /**
     * Unregister a previously registered
     * {@link com.ibm.websphere.jtaextensions.SynchronizationCallback SynchronizationCallback}
     * object, <code>sync</code>.  The object so unregistered will receive no further callbacks
     * from transactions that subsequently complete.  
     * 
     * @param sync A previously registered
     *    {@link com.ibm.websphere.jtaextensions.SynchronizationCallback SynchronizationCallback}
     *    object.
     *
     * @exception CallbackNotRegisteredException Thrown if the specific <code>sync</code>
     *     is not registered with the transaction manager.
     *
     */
   void unRegisterSynchronizationCallback(SynchronizationCallback sync)
                 throws CallbackNotRegisteredException;
}
