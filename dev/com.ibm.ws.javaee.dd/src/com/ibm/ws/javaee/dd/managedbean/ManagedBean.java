/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011, 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.dd.managedbean;

import com.ibm.ws.javaee.dd.commonbnd.RefBindingsGroup;
import com.ibm.ws.javaee.ddmetadata.annotation.DDAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.DDAttributeType;
import com.ibm.ws.javaee.ddmetadata.annotation.DDIdAttribute;

@DDIdAttribute
public interface ManagedBean extends RefBindingsGroup {

    @DDAttribute(name = "class", type = DDAttributeType.String, required = true)
    public String getClazz();

}
