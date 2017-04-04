/* ************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                           */
/*                                                                            */
/* ORIGINS: 27                                                                */
/*                                                                            */
/* IBM Confidential OCO Source Material                                       */
/* 5639-D57, 5630-A36, 5630-A37, 5724-D18.                                    */
/* (C) COPYRIGHT International Business Machines Corp. 2002,2003              */
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
/* Date      Programmer    Defect      Description                            */
/* --------  ----------    ------      -----------                            */
/* 06/06/03  beavenj       LIDB2472.2  Create                                 */
/* ************************************************************************** */

package com.ibm.ws.recoverylog.spi;

//------------------------------------------------------------------------------
// Interface: RecoveryLogManager
//------------------------------------------------------------------------------
/**
 * <p>
 * The RecoveryLogManager interface provides support for access to the recovery
 * logs associated with a client service.
 * </p>
 * 
 * <p>
 * An object that implements this interface is provided to each client service
 * when it registers with the RecoveryDirector.
 * </p>
 */
public interface RecoveryLogManager
{
    //------------------------------------------------------------------------------
    // Method: RecoveryLogManager.getRecoveryLog
    //------------------------------------------------------------------------------
    /**
     * <p>
     * Returns a RecoveryLog that can be used to access a specific recovery log.
     * </p>
     * 
     * <p>
     * Each recovery log is contained within a FailureScope. For example, the
     * transaction service on a distributed system has a transaction log in each
     * server node (ie in each FailureScope). Because of this, the caller must
     * specify the FailureScope of the required recovery log.
     * </p>
     * 
     * <p>
     * Additionally, the caller must specify information regarding the identity and
     * physical properties of the recovery log. This is done through the LogProperties
     * object provided by the client service.
     * </p>
     * 
     * @param FailureScope The required FailureScope
     * @param LogProperties Contains the identity and physical properties of the
     *            recovery log.
     * 
     * @return The RecoveryLog instance.
     * 
     * @exception InvalidLogPropertiesException The RLS does not recognize or cannot
     *                support the supplied LogProperties
     */
    public RecoveryLog getRecoveryLog(FailureScope failureScope, LogProperties logProperties) throws InvalidLogPropertiesException;

    /**
     * @param localRecoveryIdentity
     * @param recoveryGroup
     * @param logProperties
     * @return
     * @throws InvalidLogPropertiesException
     */
    SharedServerLeaseLog getLeaseLog(String localRecoveryIdentity, String recoveryGroup, LogProperties logProperties) throws InvalidLogPropertiesException;
}
