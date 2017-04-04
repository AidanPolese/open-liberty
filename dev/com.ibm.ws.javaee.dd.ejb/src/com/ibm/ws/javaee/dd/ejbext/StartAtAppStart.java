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

import com.ibm.ws.javaee.ddmetadata.annotation.DDAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.DDAttributeType;
import com.ibm.ws.javaee.ddmetadata.annotation.DDIdAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIAttribute;

/**
 * Represents &lt;start-at-app-start>.
 */
@DDIdAttribute
public interface StartAtAppStart {

    /**
     * @return value="true|false" attribute value -- use is required!
     */
    @DDAttribute(name = "value", type = DDAttributeType.Boolean, required = true)
    @DDXMIAttribute(name = "startEJBAtApplicationStart")
    boolean getValue();

}
