/* ************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                           */
/*                                                                            */
/* ORIGINS: 27                                                                */
/*                                                                            */
/* IBM Confidential OCO Source Material                                       */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70(C) COPYRIGHT International Business Machines Corp. 2010  */
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
/* 19/02/10   mallam        642260      Create                                */
/*                                                                            */
/* ************************************************************************** */

package com.ibm.ws.recoverylog.spi;

public interface RecoveryLogFactory
{
    /*
     * createRecoveryLog Create a custom recoverylog implementation for use by RLS.
     * 
     * @param props properties to be associated with the new recovery log (eg DBase config)
     * 
     * @param agent RecoveryAgent which provides client service data eg clientId
     * 
     * @param logcomp RecoveryLogComponent which can be used by the recovery log to notify failures
     * 
     * @param failureScope the failurescope (server) for which this log is to be created
     * 
     * @return RecoveryLog or MultiScopeLog to be used for logging
     * 
     * @exception InvalidLogPropertiesException thrown if the properties are not consistent with the logFactory
     */
    public RecoveryLog createRecoveryLog(CustomLogProperties props, RecoveryAgent agent, RecoveryLogComponent logComp, FailureScope failureScope) throws InvalidLogPropertiesException;

    public SharedServerLeaseLog createLeaseLog(CustomLogProperties props) throws InvalidLogPropertiesException;
}
