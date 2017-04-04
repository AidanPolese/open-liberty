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

import java.util.concurrent.TimeUnit;

import com.ibm.ws.javaee.ddmetadata.annotation.DDAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.DDAttributeType;
import com.ibm.ws.javaee.ddmetadata.annotation.DDIdAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.LibertyDurationType;

/**
 * Represents the &lt;time-out> element of Session.
 */
@DDIdAttribute
public interface TimeOut {

    /**
     * @return value="..." attribute value -- use is required!
     */
    @LibertyDurationType(timeUnit = TimeUnit.SECONDS)
    @DDAttribute(name = "value", type = DDAttributeType.Int, required = true)
    @DDXMIAttribute(name = "timeout")
    int getValue();

}
