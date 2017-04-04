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
 * Represents &lt;interface>.
 */
@DDIdAttribute
@LibertyNotInUse
public interface Interface {

    /**
     * @return binding-name="..." attribute value -- use is required!
     */
    @DDAttribute(name = "binding-name", type = DDAttributeType.String, required = true)
    String getBindingName();

    /**
     * @return class="..." attribute value -- use is required!
     */
    @DDAttribute(name = "class", type = DDAttributeType.String, required = true)
    String getClassName();

}
