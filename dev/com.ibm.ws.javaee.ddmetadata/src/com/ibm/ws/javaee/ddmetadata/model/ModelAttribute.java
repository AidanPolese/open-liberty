/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.ddmetadata.model;

/**
 * The model for an XML attribute, which typically correlate to a single field
 * in an implementation class.
 */
public class ModelAttribute extends ModelNode {
    /**
     * True if this attribute can be represented in an XMI documented with a
     * child element with an xsi:nil="true" attribute.
     */
    public boolean xmiNillable;

    public ModelAttribute(String name, ModelMethod method, boolean required) {
        super(name, method, required);
    }

    @Override
    public boolean hasXMIAttribute() {
        // An XML attribute that is obtained indirectly via an XMI reference
        // is an XMI element not an XMI attribute.
        return xmiName != null && method.xmiRefField == null;
    }
}
