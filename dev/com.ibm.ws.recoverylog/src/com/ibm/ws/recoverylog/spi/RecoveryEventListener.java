/* ************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                           */
/*                                                                            */
/*  ORIGINS: 27                                                               */
/*                                                                            */
/* IBM Confidential OCO Source Material                                       */
/* 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2004          */
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
/*  Date      Programmer    Defect      Description                           */
/*  --------  ----------    ------      -----------                           */
/*  03/15/03  sykesm        MD19638     Initial creation                      */
/* ************************************************************************** */
package com.ibm.ws.recoverylog.spi;

/**
 * The <code>RecoveryEventListener</code> is the interface that
 * interested parties must implement in order to be notified of recovery
 * events that have occurred.  This interface is not to be used as a
 * substitute for the <code>RecoveryAgent</code>.  The execution of these
 * callbacks can not effect the recovery process in any way and should not
 * attempt to interact with the RLS proper.
 */
public interface RecoveryEventListener
{
    /**
     * Notify the listener that the recovery director has detected a
     * failure in a nested FailureScope.  When running outside of an HA
     * framework or scalable server environment, there is no guarantee
     * that this method will ever be called.  For example, in the 5.1
     * multi-platform implementation, if a server fails, nobody is around
     * to detect the failure and notify listeners of the action while on
     * z/OS, if a callback implementing this interface were to be
     * registered in the controller, the controller would make this call
     * to notify the listener when a servant terminates.
     *
     * @param fs the <code>FailureScope</code> at which the failure was
     *        detected.
     */
    public void failureOccurred(FailureScope fs);

    /**
     * Notify the listener that the recovery director has (or is about to)
     * direct the specified client to perform recovery processing for the
     * specified <code>FailureScope</code>.
     *
     * @param fs the <code>FailureScope</code> for which the cilent is
     *        being directed to perform recovery.
     * @param clientId the &quot;Recovery Log Client Identifier&quot;
     *        representing the client service that is perform the the 
     *        recovery.
     */
    public void clientRecoveryInitiated(FailureScope fs, int clientId);

    /**
     * Notify the listener that the recovery director has been notified
     * that recovery has been completed by the specified client for the
     * specified <code>FailureScope</code>.
     *
     * @param fs the <code>FailureScope</code> for which the cilent is
     *        was directed to perform recovery.
     * @param clientId the &quot;Recovery Log Client Identifier&quot;
     *        representing the client service that has completed
     *        recovery.
     */
    public void clientRecoveryComplete(FailureScope fs, int clientId);

    /**
     * Notify the listener that recovery has been completed by all
     * services for the specified <code>FailureScope</code>.  This
     * callback will only be made in the JVMs where failureOcurred had
     * been called.
     *
     * @param fs the <code>FailureScope</code> for which recovery has
     *        completed.
     */
    public void recoveryComplete(FailureScope fs);
}
