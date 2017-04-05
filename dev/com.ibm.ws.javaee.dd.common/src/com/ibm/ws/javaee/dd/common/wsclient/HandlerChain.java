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

import java.util.List;

import com.ibm.ws.javaee.dd.common.QName;

/**
 * Represents &lt;handler-chain> in &lt;handler-chains> in
 * &lt;port-component-ref>.
 */
public interface HandlerChain
{
    /**
     * @return &lt;service-name-pattern>
     */
    QName getServiceNamePattern();

    /**
     * @return &lt;port-name-pattern>
     */
    QName getPortNamePattern();

    /**
     * @return &lt;protocol-bindings> as a read-only list
     */
    List<String> getProtocolBindings();

    /**
     * @return &lt;handler> as a read-only list
     */
    List<Handler> getHandlers();
}
