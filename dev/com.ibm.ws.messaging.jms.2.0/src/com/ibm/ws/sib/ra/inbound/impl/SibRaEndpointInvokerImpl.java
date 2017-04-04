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
 * --------------- --------- -------- ---------------------------------------
 * 181851.7.2      18-May-04 dcurrie  Original
 * 195445.24       26-May-04 pnickoll Changing messaging prefix
 * 209436.3        21-Jun-04 pnickoll Updated with milestone 8++ core SPI changes
 * 182745.10.1     14-Jul-04 dcurrie  Removed getSelectorDomain
 * 220406          03-Sep-04 dcurrie  Change ConsumerSession to AbstractConsumerSession
 * 325993          22-Nov-05 ajw      rethrow in invokeEndpoint if err is ThreadDeath
 * 431821          11-Apr-07 pnickoll Added parameter to invokeEndpoint method for test usage 
 * ============================================================================
 */

package com.ibm.ws.sib.ra.inbound.impl;

import java.lang.reflect.Method;

import javax.resource.spi.ResourceAdapterInternalException;
import javax.resource.spi.endpoint.MessageEndpoint;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ejs.ras.TraceNLS;
import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.ws.sib.ra.impl.SibRaUtils;
import com.ibm.ws.sib.ra.inbound.SibRaEndpointInvoker;
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.wsspi.sib.core.AbstractConsumerSession;
import com.ibm.wsspi.sib.core.SIBusMessage;
import com.ibm.wsspi.sib.core.SITransaction;
import com.ibm.wsspi.sib.ra.SibRaMessageListener;

/**
 * Implementation of <code>SibRaEndpointInvocation</code> for the delivery of
 * messages to core SPI message-driven beans.
 */
public final class SibRaEndpointInvokerImpl implements SibRaEndpointInvoker {

    /**
     * An <code>onMessage</code> method.
     */
    private static Method ON_MESSAGE_METHOD;

    /**
     * The component to use for trace.
     */
    private static TraceComponent TRACE = SibRaUtils
            .getTraceComponent(SibRaEndpointInvokerImpl.class);

    /**
     * Provides access to NLS enabled messages.
     */
    private static TraceNLS NLS = SibRaUtils.getTraceNls();

    private static final String FFDC_PROBE_1 = "1";

    private static final String FFDC_PROBE_2 = "2";

    private static final String CLASS_NAME = SibRaEndpointInvokerImpl.class
            .getName();

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.ra.inbound.SibRaEndpointStrategy#getMdbMethod()
     */
    public Method getEndpointMethod() throws ResourceAdapterInternalException {

        final String methodName = "getEndpointMethod";
        if (TRACE.isEntryEnabled()) {
            SibTr.entry(this, TRACE, methodName);
        }

        if (ON_MESSAGE_METHOD == null) {

            try {

                ON_MESSAGE_METHOD = SibRaMessageListener.class.getMethod(
                        "onMessage", new Class[] { SIBusMessage.class,
                                AbstractConsumerSession.class,
                                SITransaction.class });

            } catch (final Exception exception) {

                FFDCFilter.processException(exception, CLASS_NAME + "."
                        + methodName, FFDC_PROBE_2);
                if (TRACE.isEntryEnabled()) {
                    SibTr.exception(TRACE, exception);
                }
                throw new ResourceAdapterInternalException(NLS
                        .getFormattedMessage("ON_MESSAGE_CWSIV0851",
                                new Object[] { exception }, null), exception);

            }

        }

        if (TRACE.isEntryEnabled()) {
            SibTr.exit(this, TRACE, methodName, ON_MESSAGE_METHOD);
        }
        return ON_MESSAGE_METHOD;

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.ra.inbound.SibRaEndpointInvocation#invokeEndpoint(javax.resource.spi.endpoint.MessageEndpoint,
     *      com.ibm.wsspi.sib.core.SIBusMessage,
     *      com.ibm.wsspi.sib.core.ConsumerSession,
     *      com.ibm.wsspi.sib.core.SITransaction)
     */
    public boolean invokeEndpoint(final MessageEndpoint endpoint,
            final SIBusMessage message, final AbstractConsumerSession session,
            final SITransaction transaction, String debugMEName)
            throws ResourceAdapterInternalException {

        final String methodName = "invokeEndpoint";
        if (TRACE.isEntryEnabled()) {
            SibTr.entry(this, TRACE, methodName, new Object[] { endpoint,
                    message, session, transaction });
        }

        // Check that endpoint is a SibRaMessageListener
        if (!(endpoint instanceof SibRaMessageListener)) {

            throw new ResourceAdapterInternalException(NLS.getFormattedMessage(
                    ("UNEXPECTED_ENDPOINT_CWSIV0850"), new Object[] { endpoint,
                            SibRaMessageListener.class }, null));

        }

        final SibRaMessageListener listener = (SibRaMessageListener) endpoint;

        // Wrap session before passing to MDB
        final SibRaAbstractConsumerSession wrappedSession = new SibRaAbstractConsumerSession(
                session);

        boolean success;
        try {

            // Deliver message
            listener.onMessage(message, wrappedSession, transaction);
            success = true;

        } catch (final Throwable exc) {

            // Failed to deliver message
            FFDCFilter.processException(exc, CLASS_NAME + "." + methodName,
                    FFDC_PROBE_1, this);
            if (TRACE.isEventEnabled()) {
                SibTr.exception(this, TRACE, exc);
            }
            success = false;
            // If the throwable is a ThreadDeath then we want to rethrow the
            // exception to make sure that the thread dies correctly
            if (exc instanceof ThreadDeath)
            {
              // we cast to a threaddeath so we don't have to declare the throwable
              throw (ThreadDeath)exc;
            }

        } finally {

            // Stop an MDB that has cached the session reusing it
            wrappedSession.outOfScope();

        }

        if (TRACE.isEntryEnabled()) {
            SibTr.exit(this, TRACE, methodName, Boolean.valueOf(success));
        }
        return success;

    }

}
