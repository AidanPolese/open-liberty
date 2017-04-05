/* ********************************************************************************* */
/* COMPONENT_NAME: WAS.transactions                                                  */
/*                                                                                   */
/* ORIGINS: 27                                                                       */
/*                                                                                   */
/* IBM Confidential OCO Source Material                                              */
/* 5724-i63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997, 2007 */
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
/* 06/06/03  beavenj       LIDB2472.2  Create                                        */
/* 11/07/03  beavenj       171515      Extend exception model                        */
/* 13/04/04  beavenj       LIDB1578.1  Initial supprort for ha-recovery              */
/* 15/04/04  beavenj       LIDB1578.3  Termination support for ha-recovery           */
/* 18/05/04  beavenj       LIDB1578.5  Connect up with HA framework                  */
/* 15/06/04  beavenj       216563      Code review changes                           */
/* 26-07-04  beavenj       209636      Add Client Version Support                    */
/* 22/09/04  beavenj       232643      File Locking Support                          */
/* 29/09/04  beavenj       235642      Modify "lock not granted" behaviour           */
/* 01/11/04  beavenj       238410.1    Locking control flags                         */
/* 04/01/05  mdobbie       LIDB3603    Provide isSnapshotSafe method                 */
/* 26/07/07  mallam        453651      logWarning method                             */
/* switch to RTC                                                                     */
/* 27/05/14  slaterpa      136208      switch from logWarning to payloadAdded        */
/* ********************************************************************************* */

package com.ibm.ws.recoverylog.spi;

import java.util.ArrayList;

//------------------------------------------------------------------------------
// Interface: RecoveryAgent
//------------------------------------------------------------------------------
/**
 * <p>
 * Each client service provides its own implementation of the RecoveryAgent
 * interface. A single instance of this object is passed to the RLS during initial
 * service registration.
 * </p>
 * 
 * <p>
 * The RLS will invoke this object asynchronously (by calling
 * <code>RecoveryAgent.initiateRecovery</code>) to direct the client service to
 * handle units of recovery identified by Failure Scope.
 * </p>
 * 
 * <p>
 * The client service responds to these requests by performing recovery
 * processing through interaction with the RLS. Once this is done, the client
 * service invokes the <code>RecoveryDirector.recoveryComplete</code> method to
 * inform the RLS that the recovery processing is complete.
 * </p>
 * 
 * <p>
 * By calling recoveryComplete, the client service tells the RLS that it has
 * finished any processing for a given failure scope that has to be performed
 * synchronously. Once the client service has made this call, the RLS is free
 * to invoke the next RecoveryAgent. In practice this can only occur when the
 * recoveryAgent returns from the initiateRecovery() method. The RecoveryAgent
 * is free to perform additional recovery work after this point asynchronously.
 * </p>
 * 
 * <p>
 * The two likely models are:-
 * 
 * <ul>
 * <li>1. <code>RecoveryAgent.initiateRecovery</code> directs recovery of the
 * failure scope, and calls <code>RecoveryDirector.recoveryComplete</code>
 * when its finished. After this is done, the
 * <code>RecoveryAgent.initiateRecovery()</code> method returns. The RLS
 * then invokes the next registered RecoveryAgent.
 * <li>2. <code>RecoveryAgent.initiateRecovery</code> starts a new thread from
 * which to drive the recovery process and returns immediately. The RLS
 * blocks until the client service informs it that the failure scope has
 * been recovered, through a call to
 * <code>RecoveryDirector.recoveryComplete</code>. This will driven from
 * the new thread once any synchronous recovery processing has been
 * completed.
 * </ul>
 * </p>
 */
public interface RecoveryAgent
{
    //------------------------------------------------------------------------------
    // Method: RecoveryAgent.prepareForRecovery
    //------------------------------------------------------------------------------
    /**
     * Directs the client service to get ready to process recovery for the given
     * FailureScope. The client service uses the RecoveryLogManager instance it
     * obtained when it registered to 'getRecoveryLog' the corresponding recovery
     * 
     * @param failureScope The failure scope for which recovery may be about to start.
     */
    void prepareForRecovery(FailureScope failureScope);

    //------------------------------------------------------------------------------
    // Method: RecoveryAgent.initiateRecovery
    //------------------------------------------------------------------------------
    /**
     * Directs the client service to perform recovery processing for the given
     * FailureScope. The client service uses the RecoveryLogManager instance it
     * obtained when it registered to open the corresponding recovery logs and
     * perform any recovery processing it deems necessary. When this is complete
     * the client service should invoke RecoveryDirector.recoveryComplete()
     * 
     * @param failureScope The failure scope for which recovery is starting
     * 
     * @exception RecoveryFailedException Thrown by the client service if it is
     *                unable to complete recovery processing
     */
    void initiateRecovery(FailureScope failureScope) throws RecoveryFailedException;

    //------------------------------------------------------------------------------
    // Method: RecoveryAgent.terminateRecovery
    //------------------------------------------------------------------------------
    /**
     * Directs the client service to halt any recovery processing for the given
     * FailureScope.
     * 
     * @param failureScope The failure scope for which recovery is terminating
     * 
     * @exception TerminationFailedException Thrown by the client service if it is
     *                unable to terminate recovery processing
     */
    void terminateRecovery(FailureScope failureScope) throws TerminationFailedException;

    //------------------------------------------------------------------------------
    // Method: RecoveryAgent.clientIdentifier
    //------------------------------------------------------------------------------
    /**
     * Returns the unique "Recovery Log Client Identifier" (RLCI). RLCI values are
     * owned by the RLS and stored inside com.ibm.ws.recoverylog.spi.ClientId
     * 
     * @return int The client identifier.
     */
    int clientIdentifier();

    //------------------------------------------------------------------------------
    // Method: RecoveryAgent.clientName
    //------------------------------------------------------------------------------
    /**
     * Returns the unique "Recovery Log Client Name" (RLCN). RLCN values are
     * owned by the RLS and stored inside com.ibm.ws.recoverylog.spi.ClientId
     * 
     * @return String The client name.
     */
    String clientName();

    //------------------------------------------------------------------------------
    // Method: RecoveryAgent.clientVersion
    //------------------------------------------------------------------------------
    /**
     * Returns the version number of the client service. Only recovery logs of the
     * same or lesser version number will be accessible through the RLS service. This
     * will prevent accidental processing of a newer recovery log format and is
     * intended to addess changes to the nature of the information written by client
     * services rather than the format of the log itself. Clients should start at '1'
     * and only change this value if their recovery log content changes.
     * 
     * @return int The client version number.
     */
    int clientVersion();

    //------------------------------------------------------------------------------
    // Method: RecoveryAgent.logDirectories
    //------------------------------------------------------------------------------
    /**
     * Returns an array of strings such that each string is a fully qualified log
     * directory that the client indends to use for logging.
     * 
     * @param failureScope The target failure scope
     * 
     * @return String[] The log directory set.
     */
    String[] logDirectories(FailureScope failureScope);

    //------------------------------------------------------------------------------
    // Method: RecoveryAgent.agentReportedFailure()
    //------------------------------------------------------------------------------
    /**
     * Informs the recovery agent that another recovery agent (identified by the client
     * id) has been upable to handle recovery processing for the given failure scope.
     * 
     * @param int The client id of the failing recovery agent.
     * @param failureScope The target failure scope.
     * 
     * @return String[] The log directory set.
     */
    void agentReportedFailure(int clientId, FailureScope failureScope);

    //------------------------------------------------------------------------------
    // Method: RecoveryAgent.disableFileLocking()
    //------------------------------------------------------------------------------
    /**
     * Returns a flag to indicate if the client wants file locking to be DISABLED for
     * file based recovery logs. This method is essentially temporary until the RLS
     * has the WCCM basis to make this chocie for itself. If any client returns TRUE
     * then file locking will be DISABLED, however currently only the transaction service
     * recovery agent is actually checked.
     * 
     * by default, file locking is ENABLED.
     * 
     * @return boolean
     */
    boolean disableFileLocking();

    /**
     * Returns a flag to indicate if the client wants RLS to prepare the recovery logs
     * for a system snapshot in a safe fashion - i.e. the data in the recovery log files
     * provide a consistent state in the event of disaster recovery.
     * 
     * To make the RLS snapshot safe will have a impact on recovery log (and therefore it's
     * client services, such as Transaction).
     * 
     * By default, isSnapshotSafe is FALSE.
     * 
     * @return boolean
     */
    boolean isSnapshotSafe();

    //------------------------------------------------------------------------------
    // Method: RecoveryAgent.logFileWarning()
    //------------------------------------------------------------------------------
    /**
     * Notify RecoveryAgent of logfile space running out.
     * Called when the log file first crosses the 75% full threshold.
     * 
     * @param logname The name provided by the client service on the FileLogProperties
     *            used to create this logfile
     * @param bytesInUse The space required for current log data
     * @param bytesTotal The total space available for data
     */
    void logFileWarning(String logname, int bytesInUse, int bytesTotal);

    String getRecoveryGroup();

    /**
     * @param recoveryIdentity
     * @param recoveryGroup
     * @return
     */
    ArrayList<String> processLeasesForPeers(String recoveryIdentity, String recoveryGroup);

    public boolean claimPeerLeaseForRecovery(String recoveryIdentityToRecover, String myRecoveryIdentity, LeaseInfo leaseInfo) throws Exception;
}
