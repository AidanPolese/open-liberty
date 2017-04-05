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
import com.ibm.ws.javaee.ddmetadata.annotation.DDElement;
import com.ibm.ws.javaee.ddmetadata.annotation.DDIdAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.LibertyNotInUse;

/**
 * Represents &lt;run-as-mode>.
 */
@DDIdAttribute
@LibertyNotInUse
public interface RunAsModeBase {

    enum ModeTypeEnum {
        CALLER_IDENTITY,
        SPECIFIED_IDENTITY,
        SYSTEM_IDENTITY
    }

    /**
     * @return &lt;specified-identity> return null if not specified.
     *         If Mode is SPECIFIED_IDENTITY then this is required.
     */
    @DDElement(name = "specified-identity")
    SpecifiedIdentity getSpecifiedIdentity();

    /**
     * @return mode="..." attribute value, one of the mode type enums, required
     */
    @DDAttribute(name = "mode", type = DDAttributeType.Enum, required = true)
    ModeTypeEnum getModeType();

    /**
     * @return description="..." attribute value, return null if not specified
     */
    @DDAttribute(name = "description", type = DDAttributeType.String)
    @DDXMIAttribute(name = "description")
    String getDescription();

}
