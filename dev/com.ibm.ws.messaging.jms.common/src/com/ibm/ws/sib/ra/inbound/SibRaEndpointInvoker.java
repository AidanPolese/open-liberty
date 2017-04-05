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
 * 209436.3        21-Jun-04 pnickoll Updated with milestone 8++ core SPI changes
 * 182745.10.1     14-Jul-04 dcurrie  Removed getSelectorDomain
 * 220406          03-Sep-04 dcurrie  Change ConsumerSession to AbstractConsumerSession
 * 431821          11-Apr-07 pnickoll Added parameter to invokeEndpoint method for test usage 
 * ============================================================================
 */

package com.ibm.ws.sib.ra.inbound;

import java.lang.reflect.Method;

//Sanjay Liberty Changes
//import javax.resource.spi.ResourceAdapterInternalException;
//import javax.resource.spi.endpoint.MessageEndpoint;
import javax.resource.spi.ResourceAdapterInternalException;
import javax.resource.spi.endpoint.MessageEndpoint;
import com.ibm.wsspi.sib.core.AbstractConsumerSession;
import com.ibm.wsspi.sib.core.SIBusMessage;
import com.ibm.wsspi.sib.core.SITransaction;

/**
 * Interface implemented by resource adapters for invocation of message
 * endpoints.
 */
public interface SibRaEndpointInvoker {

    /**
     * Returns the method on the endpoint which will be invoked.
     * 
     * @return the method
     * @throws ResourceAdapterInternalException
     *             if the method cannot be obtained
     */
    Method getEndpointMethod() throws ResourceAdapterInternalException;

    /**
     * Invokes an endpoint with the given message.
     * 
     * @param endpoint
     *            the endpoint to invoke
     * @param message
     *            the message to invoke it with
     * @param session
     *            the session from which the message was consumed
     * @param transaction
     *            the transactin under which the message was consumed
     * @param debugMEName
     * 			  this is always null unless the debug system parameter is switched on
     * @return a flag indicating whether the message was delivered successfully
     * @throws ResourceAdapterInternalException
     *             if the endpoint does not implement the required interface
     */
    boolean invokeEndpoint(MessageEndpoint endpoint, SIBusMessage message,
            AbstractConsumerSession session, SITransaction transaction, String debugMEName)
            throws ResourceAdapterInternalException;

}
