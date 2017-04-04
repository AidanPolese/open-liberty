package com.ibm.ws.tx.embeddable;
/* ********************************************************************************* */
/* COMPONENT_NAME: WAS.transactions                                                  */
/*                                                                                   */
/* ORIGINS: 27                                                                       */
/*                                                                                   */
/* IBM Confidential OCO Source Material                                              */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2009, 2011 */
/* The source code for this program is not published or otherwise divested           */
/* of its trade secrets, irrespective of what has been deposited with the            */
/* U.S. Copyright Office.                                                            */
/*                                                                                   */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                                     */
/*                                                                                   */
/*  DESCRIPTION:                                                                     */
/*                                                                                   */
/*  Change History:                                                                  */
/*                                                                                   */
/*  Date      Programmer  Defect       Description                                   */
/*  --------  ----------  ------       -----------                                   */
/*  09-11-03  johawkes    F743-305.1   Creation                                      */
/*  09-11-15  johawkes    F743-305.3   Move in more stuff from WAS                   */
/*  09-12-23  tranteam    F923-4350    Branch coupling support                       */
/*  11-02-15  hursdlg     690084       Update branch coupling support                */
/*  11-11-24  johawkes    723423       Repackaging                                   */
/* ********************************************************************************* */

import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionRolledbackException;
import javax.transaction.xa.XAResource;

import com.ibm.tx.jta.ExtendedTransactionManager;
import com.ibm.tx.jta.OnePhaseXAResource;
import com.ibm.ws.Transaction.UOWCoordinator;
import com.ibm.ws.uow.UOWScopeCallback;

/**
 * This interface extends javax.transactionTransactionManager to provide WebSphere components
 * with facilities to enlist and delist resources with the currently active transaction.
 *
 * <p> This interface is private to WAS.
 * Any use of this interface outside the WAS Express/ND codebase 
 * is not supported.
 *
 */

public interface EmbeddableWebSphereTransactionManager extends ExtendedTransactionManager
{
    /** A Synchronization registered for the outer tier will be driven
     *  before normal beforeCompletion processing and after normal
     *  afterCompletion processing.
     */
    public static final int SYNC_TIER_OUTER = 0;

    /** A Synchronization registered for the normal tier will be driven
     *  normally for both before and after completion processing. It is
     *  equivalent to registering the sync without specifying a tier.
     */
    public static final int SYNC_TIER_NORMAL = 1;

    /** A Synchronization registered for the inner tier will be driven
     *  after normal beforeCompletion processing and before normal
     *  afterCompletion processing.
     */
    public static final int SYNC_TIER_INNER = 2;

    /** A Synchronization registered for the RRS tier will be driven after
     *  inner tier synchronizations during beforeCompletion processing and
     *  before inner tier synchronizations during afterCompletion
     *  processing.  
     */
    public static final int SYNC_TIER_RRS = 3;

    /**
     * Variation on enlist which accepts UOWCoordinator for improved performance.
     *
     * @param coord UOWCoordinator previously obtained from UOWCurrent.
     * @param xaRes The XAResource object representing the resource to enlist.
     * @param recoveryId The identifier returned from a call to registerResourceInfo
     *                   associating the appropriate xaResFactoryClassName/xaResInfo
     *                   necessary for produce a XAResource object.
     * @param branchCoupling Set to xa_start flag value for the XAResource as required
     *                   by the branch coupling setting, or 0 for unset/default.
     *
     * @return <i>true</i> if the resource was enlisted successfully;
     *            otherwise <i>false</i>.
     */
    public boolean enlist(UOWCoordinator coord, XAResource xaRes, int recoveryId,int branchCoupling) // 690084
    throws RollbackException, IllegalStateException, SystemException;

    /**
     * Method to enlist resource with JTA tran via UOWCoordinator
     * for improved performance.
     *
     * @param coord UOWCoordinator previously obtained from UOWCurrent.
     * @param xaRes The XAResource object representing the resource to enlist.
     *
     * @return <i>true</i> if the resource was enlisted successfully;
     *            otherwise <i>false</i>.
     */
    public boolean enlistOnePhase(UOWCoordinator coord, OnePhaseXAResource opXaRes)
    throws RollbackException, IllegalStateException, SystemException;

    /**
     * Start an inactivity timer and call alarm method of parameter when
     * timeout expires.
     * A timer is only started if the transaction is active (including
     * markedrollbackonly)
     *
     * @param t    Transaction associated with this timer.
     * @param iat  callback object to be notified when timer expires.
     * @return     boolean to indicate whether timer started
     */
    public boolean startInactivityTimer (Transaction t, InactivityTimer iat);

    /**
     * Stop inactivity timer associated with transaction.
     *
     * @param t    Transaction associated with this timer.
     */
    public void stopInactivityTimer  (Transaction t);

    /**
     * Interface for use by ejbcontainer to allow timeout of 'sticky'
     * BMT user transactions.
     */
    public interface InactivityTimer
    {
       void alarm ();
    }

    /**
     * Method to register synchronization object with JTA tran via UOWCoordinator
     * for improved performance.
     *
     * @param coord UOWCoordinator previously obtained from UOWCurrent.
     * @param sync  Synchronization object to be registered.
     *
     */
    public void registerSynchronization(UOWCoordinator coord,
                                        javax.transaction.Synchronization sync)
    throws RollbackException, IllegalStateException, SystemException;
    
    /**
     * Method to register synchronization object with a specific tier in
     * the JTA tran represented by the given UOWCoordinator.
     *
     * @param coord UOWCoordinator previously obtained from UOWCurrent.
     * @param sync  Synchronization object to be registered.
     * @param tier  The tier under which the sync should be given
     *
     */
    public void registerSynchronization(UOWCoordinator coord, javax.transaction.Synchronization sync, int tier)
    throws RollbackException, IllegalStateException, SystemException;

    /**
     *  Complete processing of passive transaction timeout.
     */
    public void completeTxTimeout() throws TransactionRolledbackException;

    public void registerCallback(UOWScopeCallback callback);
}
