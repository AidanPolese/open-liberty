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
package com.ibm.ws.javaee.dd.commonbnd;

import com.ibm.ws.javaee.ddmetadata.annotation.DDAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.DDAttributeType;
import com.ibm.ws.javaee.ddmetadata.annotation.DDIdAttribute;

/**
 * Represents &lt;interceptor>.
 * 
 * Used in ejb-jar and managed-bean bnd schemas.
 */
@DDIdAttribute
public interface Interceptor extends RefBindingsGroup {
    @DDAttribute(name = "class", type = DDAttributeType.String)
    public String getClassName();
}
