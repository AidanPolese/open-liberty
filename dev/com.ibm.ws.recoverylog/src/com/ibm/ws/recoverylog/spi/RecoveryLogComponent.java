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
// Interface : RecoveryLogComponent
//------------------------------------------------------------------------------
/**
 * Interface to represent the abstraction of functions required by individual services
 * of the recovery log component.
 * Each product specific recoverylog service should implement this interface and make it available
 * to the services (only recoveryDirector at the moment).
 */
public interface RecoveryLogComponent
{
   /**
    * Called to indicate that an IOException was received during a log write operation.
    * This allows the called log service to decide what action to take (eg terminate server).
    * @param fs FailureScope for which a failure was detected
    */
   public void logWriteFailed (FailureScope fs);

   /**
    * Query interface for configuration info.  Format of this may be product specific,
    * so calling service must interpret accordingly
    * @param fs FailureScope for which info is requested
    * @return object containing the requested config info
    */
   public Object getRecoveryLogConfig(FailureScope fs);

   /**
    * Query interface for clustering info (WAS specific :-( )
    * @param fs FailureScope for which info is requested
    * @return Identity object for the given FailureScope
    */
   public Object clusterIdentity(FailureScope fs);


// Methods which should probably disappear and be replaced perhaps by
// using the RecoveryDirector callback mechanism ????

   public void enablePeerRecovery();

   public void localRecoveryFailed();

   public void deactivateGroup(FailureScope fs, int handoverDelay);

   public void leaveGroup(FailureScope fs);

   public void terminateServer();

// clustering stuff
   
   public boolean  joinCluster(FailureScope fs);
   public boolean  leaveCluster(FailureScope fs);
   public Object   getAffinityKey(FailureScope fs);
   public String   getNonNullCurrentFailureScopeIDString(String serverName);

// utility methods

   /**
    * Create a unique (perhaps based on uuid) token to identify suspend call
    * @param bytes  serialized form of token for recreation (null indicates new token required)
    * @return requested token
    */
   public RLSSuspendToken createRLSSuspendToken(byte[] bytes);
}
