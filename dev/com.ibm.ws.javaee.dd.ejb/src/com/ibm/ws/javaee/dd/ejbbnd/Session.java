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

import java.util.List;

import com.ibm.ws.javaee.ddmetadata.annotation.DDAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.DDAttributeType;
import com.ibm.ws.javaee.ddmetadata.annotation.DDElement;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIAttribute;
import com.ibm.ws.javaee.ddmetadata.annotation.LibertyNotInUse;

/**
 * Represents &lt;session>.
 */
public interface Session extends EnterpriseBean {

    /**
     * @return &lt;Interface>, or empty list if unspecified
     */
    @LibertyNotInUse
    @DDElement(name = "interface")
    List<Interface> getInterfaces();

    /**
     * @return simple-binding-name="..." , or null if unspecified
     */
    @DDAttribute(name = "simple-binding-name", type = DDAttributeType.String)
    @DDXMIAttribute(name = "jndiName")
    String getSimpleBindingName();

    /**
     * @return component-id="..." , or null if unspecified
     */
    @DDAttribute(name = "component-id", type = DDAttributeType.String)
    String getComponentID();

    /**
     * @return remote-home-binding-name="..." , or null if unspecified
     */
    @DDAttribute(name = "remote-home-binding-name", type = DDAttributeType.String)
    String getRemoteHomeBindingName();

    /**
     * @return local-home-binding-name="..." , or null if unspecified
     */
    @DDAttribute(name = "local-home-binding-name", type = DDAttributeType.String)
    String getLocalHomeBindingName();

}
