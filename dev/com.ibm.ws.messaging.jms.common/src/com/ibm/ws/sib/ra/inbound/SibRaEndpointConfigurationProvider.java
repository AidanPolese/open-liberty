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
 *                 18-May-04 dcurrie  Original
 * ============================================================================
 */

package com.ibm.ws.sib.ra.inbound;
//Sanjay Liberty Changes
//import javax.resource.spi.InvalidPropertyException;
//import javax.resource.spi.ResourceAdapterInternalException;
import javax.resource.spi.InvalidPropertyException;
import javax.resource.spi.ResourceAdapterInternalException;


/**
 * Interface implemented by resource adapter <code>ActivationSpec</code>
 * classes in order to supply API agnostic endpoint configuration information.
 */
public interface SibRaEndpointConfigurationProvider {

    /**
     * Returns the endpoint configuration for this activation.
     * 
     * @return the endpoint configuration
     * @throws InvalidPropertyException
     *             if the configuration is not valid
     * @throws ResourceAdapterInternalException
     *             if the configuration cannot be supplied for some other reason
     */
    SibRaEndpointConfiguration getEndpointConfiguration()
            throws InvalidPropertyException, ResourceAdapterInternalException;

    /**
     * Returns an object for invoking endpoints of the type supported by this
     * resource adapter.
     * 
     * @return an object for invoking endpoints
     * @throws ResourceAdapterInternalException
     *             if the invoker cannot be supplied
     */
    SibRaEndpointInvoker getEndpointInvoker()
            throws ResourceAdapterInternalException;

}
