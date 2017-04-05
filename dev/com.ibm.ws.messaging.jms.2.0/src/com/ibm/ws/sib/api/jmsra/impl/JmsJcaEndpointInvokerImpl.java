/**
 * 
 * 
 * ============================================================================
 * IBM Confidential OCO Source Material
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
 * 195445.28       26-May-04 pnickoll Changing messaging prefix
 * 209436.3        21-Jun-04 pnickoll Updated with milestone 8++ core SPI changes
 * 214271          06-Jul-04 pnickoll Wrong class name used for trace
 * 182745.10.1     14-Jul-04 dcurrie  Removed getSelectorDomain
 * 220406          03-Sep-04 dcurrie  Change ConsumerSession to AbstractConsumerSession
 * SIB0121.jms.2   17-Jan-07 jamessid Supporting performance enhancements for large objects
 * 431821          11-Apr-07 pnickoll Added parameter to invokeEndpoint method for test usage
 * 434279          08-May-07 pnickoll Changed test paraemter to be MEName and protected the call to set the value 
 * 438531          11-May-07 pnickoll Changed so that if the test string is the empty string we do not set this value. 
 * ============================================================================
 */

package com.ibm.ws.sib.api.jmsra.impl;

import java.lang.reflect.Method;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ejs.ras.TraceNLS;
//Sanjay Liberty Changes
//import javax.resource.spi.ResourceAdapterInternalException;
//import javax.resource.spi.endpoint.MessageEndpoint;
import javax.resource.spi.ResourceAdapterInternalException;
import javax.resource.spi.endpoint.MessageEndpoint;
import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.ws.sib.api.jms.JmsInternalsFactory;
import com.ibm.ws.sib.api.jms.JmsSharedUtils;
import com.ibm.ws.sib.api.jmsra.JmsraConstants;
import com.ibm.ws.sib.mfp.JsJmsMessage;
import com.ibm.ws.sib.ra.inbound.SibRaEndpointInvoker;
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.wsspi.sib.core.AbstractConsumerSession;
import com.ibm.wsspi.sib.core.SIBusMessage;
import com.ibm.wsspi.sib.core.SITransaction;

/**
 * Implementation of <code>SibRaEndpointInvoker</code> for the delivery of
 * messages to core SPI message-driven beans.
 */
final class JmsJcaEndpointInvokerImpl implements SibRaEndpointInvoker {

    /**
     * An <code>onMessage</code> method.
     */
    private static Method ON_MESSAGE_METHOD;

    /**
     * Utility class for converting to JMS messages.
     */
    private JmsSharedUtils _jmsUtils;

    /**
     * The component to use for trace.
     */
    private static final TraceComponent TRACE = SibTr.register(
            JmsJcaEndpointInvokerImpl.class, JmsraConstants.MSG_GROUP,
            JmsraConstants.MSG_BUNDLE);

    /**
     * Provides access to NLS enabled messages.
     */
    private static final TraceNLS NLS = TraceNLS
            .getTraceNLS(JmsraConstants.MSG_BUNDLE);

    private static final String FFDC_PROBE_1 = "1";

    private static final String FFDC_PROBE_2 = "2";

    private static final String CLASS_NAME = JmsJcaEndpointInvokerImpl.class
            .getName();

    /**
     * Properties passed through from the Activation Spec - config
     */
    private Map passThruProps;
    
    /**
     * Constructor.
     * 
     * @param passThruProps_param Administrative properties passed through from activation spec
     * @throws ResourceAdapterInternalException
     *             if the JMS utility class cannot be obtained
     */
    JmsJcaEndpointInvokerImpl(Map passThruProps_param) throws ResourceAdapterInternalException {

        final String methodName = "JmsJcaEndpointInvokerImpl";
        if (TraceComponent.isAnyTracingEnabled() && TRACE.isEntryEnabled()) {
            SibTr.entry(this, TRACE, methodName, passThruProps_param);
        }

        try {

            _jmsUtils = JmsInternalsFactory.getSharedUtils();
            
        } catch (final JMSException exception) {

            FFDCFilter.processException(exception, CLASS_NAME
                    + ".JmsJcaEndpointInvokerImpl", FFDC_PROBE_2, this);
            if (TRACE.isEventEnabled()) {
                SibTr.exception(this, TRACE, exception);
            }
            throw new ResourceAdapterInternalException(NLS.getFormattedMessage(
                    ("UTILITY_CLASS_CWSJR1481"), new Object[] { exception },
                    null), exception);

        }

        // Store the 'pass through properties' that have come from the ActivationSpec
        passThruProps = passThruProps_param;
        
        if (TraceComponent.isAnyTracingEnabled() && TRACE.isEntryEnabled()) {
            SibTr.exit(this, TRACE, methodName);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.ra.inbound.SibRaEndpointInvoker#getEndpointMethod()
     */
    public Method getEndpointMethod() throws ResourceAdapterInternalException {

        final String methodName = "getEndpointMethod";
        if (TraceComponent.isAnyTracingEnabled() && TRACE.isEntryEnabled()) {
            SibTr.entry(this, TRACE, methodName);
        }

        if (ON_MESSAGE_METHOD == null) {

            try {

                ON_MESSAGE_METHOD = MessageListener.class.getMethod(
                        "onMessage", new Class[] { Message.class });

            } catch (final Exception exception) {

                FFDCFilter.processException(exception, CLASS_NAME + "."
                        + methodName, FFDC_PROBE_2, this);
                if (TraceComponent.isAnyTracingEnabled() && TRACE.isEntryEnabled()) {
                    SibTr.exception(this, TRACE, exception);
                }
                throw new ResourceAdapterInternalException(NLS
                        .getFormattedMessage("ON_MESSAGE_CWSJR1483",
                                new Object[] { exception }, null), exception);

            }

        }

        if (TraceComponent.isAnyTracingEnabled() && TRACE.isEntryEnabled()) {
            SibTr.exit(this, TRACE, methodName, ON_MESSAGE_METHOD);
        }
        return ON_MESSAGE_METHOD;

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.ra.inbound.SibRaEndpointInvoker#invokeEndpoint(javax.resource.spi.endpoint.MessageEndpoint,
     *      com.ibm.wsspi.sib.core.SIBusMessage,
     *      com.ibm.wsspi.sib.core.ConsumerSession,
     *      com.ibm.wsspi.sib.core.SITransaction)
     */
    public boolean invokeEndpoint(final MessageEndpoint endpoint,
            final SIBusMessage message, final AbstractConsumerSession session,
            final SITransaction transaction, String debugMEName)
            throws ResourceAdapterInternalException {

        final String methodName = "invokeEndpoint";
        if (TraceComponent.isAnyTracingEnabled() && TRACE.isEntryEnabled()) {
            SibTr.entry(this, TRACE, methodName, new Object[] { endpoint,
                    message, session, transaction });
        }

        // Check that endpoint is a MessageListener
        if (!(endpoint instanceof MessageListener)) {

            throw new ResourceAdapterInternalException(NLS.getFormattedMessage(
                    ("UNEXPECTED_ENDPOINT_CWSJR1482"), new Object[] { endpoint,
                            MessageListener.class }, null));

        }

        final MessageListener listener = (MessageListener) endpoint;

        boolean success;
        try {

            // Convert to JMS message - no session to pass in, but do pass in 'pass thru' props
            final Message jmsMessage = _jmsUtils.inboundMessagePath(message,
                    null, passThruProps);
            
            // This is a debug property and only set if the value is not null (it will be null unless a
            // runtime system property is set.
            if ((debugMEName != null) && (!debugMEName.equals ("")))
            {
            	if (message instanceof JsJmsMessage)
            	{
            		((JsJmsMessage) message).setObjectProperty ("MEName", debugMEName);
            	}
            	else 
            	{
                    if (TraceComponent.isAnyTracingEnabled() && TRACE.isDebugEnabled()) 
                    {
                    	SibTr.debug(TRACE, "Can not set MDB location in message as the message is not a JsJmsMessage - its a " + jmsMessage.getClass().getName());
                    }
            	}
            }
            
            // Deliver message
            listener.onMessage(jmsMessage);
            success = true;

        } catch (final Throwable exc) {

            // Failed to deliver message
            FFDCFilter.processException(exc, CLASS_NAME + "." + methodName,
                    FFDC_PROBE_1, this);
            if (TRACE.isEventEnabled()) {
                SibTr.exception(this, TRACE, exc);
            }
            success = false;

        }

        if (TraceComponent.isAnyTracingEnabled() && TRACE.isEntryEnabled()) {
            SibTr.exit(this, TRACE, methodName, Boolean.valueOf(success));
        }
        return success;

    }

}
