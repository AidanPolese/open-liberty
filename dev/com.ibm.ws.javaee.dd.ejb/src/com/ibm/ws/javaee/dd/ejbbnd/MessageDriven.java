/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.dd.ejbbnd;

import com.ibm.ws.javaee.ddmetadata.annotation.DDElement;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIFlatten;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIType;
import com.ibm.ws.javaee.ddmetadata.annotation.LibertyNotInUse;

/**
 * Represents &lt;message-driven>.
 */
@DDXMIType(name = "MessageDrivenBeanBinding", namespace = "ejbbnd.xmi")
public interface MessageDriven extends EnterpriseBean {

    /**
     * @return &lt;listener-port>, or null if unspecified
     *         Must have either a ListenerPort or a JCAAdapter - but not both.
     *         Test ListenerPort for null, then JCAAdapter for null.
     */
    @LibertyNotInUse
    @DDElement(name = "listener-port")
    ListenerPort getListenerPort();

    /**
     * @return &lt;jca-adapter>, or null if unspecified
     */
    @DDElement(name = "jca-adapter")
    @DDXMIFlatten
    JCAAdapter getJCAAdapter();

}
