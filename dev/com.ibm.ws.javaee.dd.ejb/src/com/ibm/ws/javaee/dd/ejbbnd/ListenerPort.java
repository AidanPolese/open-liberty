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

import com.ibm.ws.javaee.ddmetadata.annotation.DDAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.DDAttributeType;
import com.ibm.ws.javaee.ddmetadata.annotation.DDIdAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.LibertyNotInUse;

/**
 * Represents &lt;listener-port>.
 * Schema requires a messageDrivenBean to have either a ListenerPort or a JCAAdapter - but not both.
 * If ListenerPort is null then JCAAdapter must be set.
 */
@DDIdAttribute
@LibertyNotInUse
public interface ListenerPort {

    /**
     * @return name="..." attribute value -- use is required!
     */
    @DDAttribute(name = "name", type = DDAttributeType.String, required = true)
    String getName();

}
