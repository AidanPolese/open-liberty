/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.dd.ejbext;

import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIType;

/**
 * Represents a &lt;message-driven> bean object in the list of Enterprise Beans.
 */
@DDXMIType(name = "MessageDrivenExtension", namespace = "ejbext.xmi")
public interface MessageDriven extends EnterpriseBean {
    // nothing to extend...
}
