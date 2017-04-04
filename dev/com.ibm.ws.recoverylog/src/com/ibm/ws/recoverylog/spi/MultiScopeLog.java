/* ************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                           */
/*                                                                            */
/* ORIGINS: 27                                                                */
/*                                                                            */
/* IBM Confidential OCO Source Material                                       */
/* 5639-D57, 5630-A36, 5630-A37, 5724-D18 (C) COPYRIGHT International Business Machines Corp. 2010, 2016 */
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
/* Date      Programmer  Defect         Description                           */
/* --------  ----------  ------         -----------                           */
/* 10-02-19  mallam      642260         Create                                */
/* 15-12-09  dmatthew    PI45254        Enhance serviceability on markFailed  */
/* ************************************************************************** */

package com.ibm.ws.recoverylog.spi;

//------------------------------------------------------------------------------
// Interface: MultiScopeLog
//------------------------------------------------------------------------------
/**
 * <p>
 * The MultiScopeLog interface extends (Dsitributed)RecoveryLog and provides methods for
 * logging information based on FailureScope
 * </p>
 * 
 */
public interface MultiScopeLog extends DistributedRecoveryLog
{

    //------------------------------------------------------------------------------
    // Method: RecoveryLog.createRecoverableUnit
    //------------------------------------------------------------------------------
    /**
     * <p>
     * Create a new RecoverableUnit under which to write information to the recovery
     * log.
     * </p>
     * 
     * <p>
     * Information written to the recovery log is grouped by the service into a number
     * of RecoverableUnit objects, each of which is then subdivided into a number of
     * RecoverableUnitSection objects. Information to be logged is passed to a
     * RecoverableUnitSection in the form of a byte array.
     * A MultiScopeLog can hold information from a number of FailureScopes, but these
     * must belong to a single server. eg Multiple Servant regions of a ZOS server.
     * </p>
     * 
     * <p>The identity of the recoverable unit will be allocated by the recovery log.
     * Use of this method <b>must not</b> be mixed with createRecoverableUnit(long)</p>
     * 
     * @param fs The FailureScope to be associated with the new RecoverableUnit
     * @return The new RecoverableUnit.
     * 
     * @exception LogClosedException Thrown if the recovery log is closed and must be
     *                opened before this call can be issued.
     * @exception InternalLogException Thrown if an unexpected error has occured.
     * @exception LogIncompatibleException An attempt has been made access a recovery
     *                log that is not compatible with this version
     *                of the service.
     */
    public RecoverableUnit createRecoverableUnit(FailureScope fs) throws LogClosedException, InternalLogException, LogIncompatibleException;

    //------------------------------------------------------------------------------
    // Method: RecoveryLog.recoverableUnits
    //------------------------------------------------------------------------------
    /**
     * <p>
     * Returns a LogCursor that can be used to itterate through all active
     * RecoverableUnits associated with a specific FailureScope.
     * The order in which they are returned is not defined.
     * </p>
     * 
     * </p>
     * The LogCursor must be closed when it is no longer needed or its itteration
     * is complete. (See the LogCursor class for more information)
     * </p>
     * 
     * <p>
     * Objects returned by <code>LogCursor.next</code> or <code>LogCursor.last</code>
     * must be cast to type RecoverableUnit.
     * </p>
     * 
     * <p>
     * Care must be taken not remove or add recoverable units whilst the resulting
     * LogCursor is open. Doing so will result in a ConcurrentModificationException
     * being thrown.
     * </p>
     * 
     * @param fs The FailureScope with which the returned RecoverableUnits are associated
     * @return A LogCursor that can be used to itterate through all active
     *         RecoverableUnits.
     * 
     * @exception LogClosedException Thrown if the recovery log is closed and must be
     *                opened before this call can be issued.
     */
    public LogCursor recoverableUnits(FailureScope fs) throws LogClosedException;

    //------------------------------------------------------------------------------
    // Method: RecoveryLogImpl.markFailedByAssociation
    //------------------------------------------------------------------------------
    /**
     * Mark this log as failed due to the failure of another log.
     */
    public void markFailedByAssociation();

    //------------------------------------------------------------------------------
    // Method: RecoveryLogImpl.provideServiceability
    //------------------------------------------------------------------------------
    /**
     * provide detailed FFDC about log state.
     */
    public void provideServiceability();
}
