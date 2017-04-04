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
import com.ibm.ws.javaee.ddmetadata.annotation.LibertyNotInUse;

/**
 * Represents &lt;bean-cache>.
 */
@DDIdAttribute
public interface BeanCache {

    enum ActivationPolicyTypeEnum {
        ONCE,
        @LibertyNotInUse // Activity sessions are not supported in Liberty
        ACTIVITY_SESSION,
        TRANSACTION
    }

    /**
     * @return activation-policy="" return one of the ENUM values, return null is not specified.
     */
    @DDAttribute(name = "activation-policy", type = DDAttributeType.Enum)
    @DDXMIAttribute(name = "activateAt")
    ActivationPolicyTypeEnum getActivationPolicy();

}
