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
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.LibertyNotInUse;

/**
 * Represents the &lt;specified-identity> element of run-as-mode.
 */
@LibertyNotInUse
public interface SpecifiedIdentity {

    /**
     * @return role="..." attribute value -- use is required!
     */
    @DDAttribute(name = "role", type = DDAttributeType.String, required = true)
    @DDXMIAttribute(name = "roleName")
    String getRole();

    /**
     * @return description="..." attribute value -- return null if not specified.
     */
    @DDAttribute(name = "description", type = DDAttributeType.String)
    @DDXMIAttribute(name = "description")
    String getDescription();

}
