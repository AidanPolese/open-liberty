/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.security.saml2;

import java.io.Serializable;
import java.util.List;

import javax.xml.namespace.QName;

/**
 *
 */
public interface Saml20Attribute extends Serializable {
    /*
     * return a list of Attribute namespace
     */
    public List<QName> getNameSpaces();

    /*
     * return attribute name
     */
    public String getName();

    /*
     * return attribute name format
     */
    public String getNameFormat();

    /*
     * return attribute's friendly name
     */
    public String getFriendlyName();

    /*
     * return attribute value schema type
     */
    public QName getSchemaType();

    /*
     * return a list of attribute values
     */
    public List<String> getValuesAsString();

    /*
     * return a list of serialized attribute values
     */
    public List<String> getSerializedValues();

    /*
     * return serialized attribute
     */
    public String getSerializedAttribute();

}
