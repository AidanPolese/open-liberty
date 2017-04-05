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
/*  04-03-23  irobins       LIDB3133-23 Added API classification              */
/*                                                                            */
/* ************************************************************************** */

package com.ibm.websphere.jtaextensions;

/**
 * An object implementing this interface is enlisted once through the 
 * {@link com.ibm.websphere.jtaextensions.ExtendedJTATransaction ExtendedJTATransaction}
 * interface and receives notification of the 
 * completion of each subsequent transaction mediated by the transaction 
 * manager in the local JVM. 
 * While this object may execute in a J2EE server, there is no specific J2EE 
 * component active when this object is called and so it has limited <i>direct</i> 
 * access to any J2EE resources.  Specifically, it has no access to the <code>java:</code> 
 * namespace or to any container-mediated resource.  It may cache a reference 
 * to J2EE component, for example a stateless SessionBean, that it delegates 
 * to. Such an EJB would then have all the normal access to J2EE 
 * resources and could be used, for example, to acquire a JDBC connection and 
 * flush updates to a database during <code>beforeCompletion</code>. 
 *
 * @ibm-api
 * @ibm-was-base
 * @ibm-user-implements
 * 
 */
public interface SynchronizationCallback
{

   /**
    *
    * Called before each transaction begins commit processing. Provides
    * an opportunity, for example, to flush data to a persistent store
    * prior to the start of two-phase commit.
    * This method is not called prior to a request to rollback or if the transaction 
    * has been marked rollbackOnly.  
    * The identity of the transaction about to complete is indicated through 
    * both the <code>globallId</code> and <code>localId</code>
    * (either of which can be used by the callback).  
    *
    * @param localId the process-unique id of the transaction about to complete.
    * @param globalId the global transaction identifier, derived from the
    *    <code>PropagationContext</code> of the global transaction
    *    of the transaction about to complete.
    *
    */
    void beforeCompletion(int localId, byte[] globalId);

   /**
    * Called after each transaction is completed.  The transaction is not active 
    * on the thread at this point.  The identity of the transaction just 
    * completed is indicated through both the <code>globallId</code> and <code>localId</code> 
    * (either of which can be used by an callback).  
    *
    * @param localId the process-unique id of the transaction just completed.
    * @param globalId the global transaction identifier, derived from the
    *    <code>PropagationContext</code> of the global transaction
    *    of the transaction just completed.
    * @param committed boolean that is <b>true</b> if the transaction outcome was
    *    committed or <b>false</b> otherwise.
    *
    */
   void afterCompletion(int localId, byte[] globalId, boolean committed);
}

