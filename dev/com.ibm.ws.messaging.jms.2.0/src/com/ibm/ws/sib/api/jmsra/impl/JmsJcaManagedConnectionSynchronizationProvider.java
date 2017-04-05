/**
 * 
 * 
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * Copyright IBM Corp. 2012
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * 
 *
 * Change activity:
 *
 * Reason          Date      Origin   Description
 * --------------- --------- -------- -----------------------------------------
 * 188050.2        05-Apr-04 dcurrie  Original
 * 203656          17-May-04 dcurrie  Code cleanup
 * 195445.28       26-May-04 pnickoll Changing messaging prefix
 * 201972.4        28-Jul-04 pnickoll Update core SPI exceptions
 * 221976          05-Aug-04 pnickoll Type in exit trace for getSynchronization
 * 226576          25-Aug-04 dcurrie  Additional exceptions on constructor
 * 238960.3        14-Oct-04 dcurrie  Use subjectToString
 * 313337.2        13-Oct-05 pnickoll Change createUncoordinatedTransaction call so we now pass false as parameter 
 * ============================================================================
 */
package com.ibm.ws.sib.api.jmsra.impl;

//Sanjay Liberty Changes
//javax.transaction.Synchronization and com.ibm.ws.Transaction.SynchronizationProvider could not resolved in 
//JmsJcaManagedConnectionSynchronizationProvider, need to check with transaction team for resolution.  For time being 
//commenting all the contents in this class. So now it behaves like super class JmsJcaManagedConnection


import javax.security.auth.Subject;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ejs.ras.TraceNLS;
import com.ibm.ws.sib.api.jmsra.JmsraConstants;
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.wsspi.sib.core.SICoreConnection;
import com.ibm.wsspi.sib.core.SIUncoordinatedTransaction;
import com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException;
import com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException;

/**
 * Sub-class of <code>JmsJcaManagedConnection</code> supporting the
 * <code>SynchronizationProvider</code> interface to allow sharing of
 * connections with container-managed persistence.
 */
final class JmsJcaManagedConnectionSynchronizationProvider extends
        JmsJcaManagedConnection { //Sanjay Liberty Changes - implements SynchronizationProvider {

    private static final String FFDC_PROBE_1 = "1";

    private static final String FFDC_PROBE_2 = "2";

    /**
     * The current <code>Synchronization</code> registered with a transaction.
     * Set in <code>getSynchronization</code> and unset in
     * <code>afterComplation</code>.
     */
    private SIUncoordinatedTransaction synchronization;

    private static TraceComponent TRACE = SibTr.register(
            JmsJcaManagedConnectionSynchronizationProvider.class,
            JmsraConstants.MSG_GROUP, JmsraConstants.MSG_BUNDLE);

    private static TraceNLS NLS = TraceNLS
            .getTraceNLS(JmsraConstants.MSG_BUNDLE);

    /**
     * Constructs a managed connection that supports connection sharing with
     * CMP.
     * 
     * @param managedConnectionFactory
     *            the parent managed connection factory
     * @param coreConnection
     *            the initial connection
     * @param userDetails
     *            the user details specified when the core connection was
     *            created
     * @param subject
     *            the subject
     * @throws SIConnectionUnavailableException
     *             if the core connection is no longer available
     * @throws SIConnectionDroppedException
     *             if the core connection has been dropped
     */
    JmsJcaManagedConnectionSynchronizationProvider(
            final JmsJcaManagedConnectionFactoryImpl managedConnectionFactory,
            final SICoreConnection coreConnection,
            final JmsJcaUserDetails userDetails, final Subject subject)
            throws SIConnectionDroppedException,
            SIConnectionUnavailableException {

        super(managedConnectionFactory, coreConnection, userDetails, subject);

        if (TRACE.isEntryEnabled()) {
            SibTr.entry(this, TRACE, "JmsJcaManagedConnectionSynchronization",
                    new Object[] { managedConnectionFactory, coreConnection,
                            userDetails, subjectToString(subject) });
            SibTr.exit(this, TRACE, "JmsJcaManagedConnectionSynchronization");
        }

    }
}
