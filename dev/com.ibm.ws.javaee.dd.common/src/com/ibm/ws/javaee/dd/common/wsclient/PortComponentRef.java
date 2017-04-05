/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.dd.common.wsclient;

/**
 * Represents &lt;port-component-ref> in &lt;service-ref>.
 */
public interface PortComponentRef
{
    /**
     * @return &lt;service-endpoint-interface>
     */
    String getServiceEndpointInterfaceName();

    /**
     * @return true if &lt;enable-mtom> is specified
     * @see #isEnableMtom
     */
    boolean isSetEnableMtom();

    /**
     * @return &lt;enable-mtom> if specified
     * @see #isSetEnableMtom
     */
    boolean isEnableMtom();

    /**
     * @return true if &lt;mtom-threshold> is specified
     * @see #getMtomThreshold
     */
    boolean isSetMtomThreshold();

    /**
     * @return &lt;mtom-threshold> if specified
     * @see #isSetMtomThreshold
     */
    int getMtomThreshold();

    /**
     * @return &lt;addressing>, or null if unspecified
     */
    Addressing getAddressing();

    /**
     * @return &lt;respect-binding>, or null if unspecified
     */
    RespectBinding getRespectBinding();

    /**
     * @return &lt;port-component-link>, or null if unspecified
     */
    String getPortComponentLink();
}
