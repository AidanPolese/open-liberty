/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.ddmodel.common;

import com.ibm.ws.javaee.ddmodel.BooleanType;

/*
 <xsd:complexType name="xsdBooleanType">
 <xsd:simpleContent>
 <xsd:extension base="xsd:boolean">
 <xsd:attribute name="id"
 type="xsd:ID"/>
 </xsd:extension>
 </xsd:simpleContent>
 </xsd:complexType>
 */

public class XSDBooleanType extends BooleanType {

    @Override
    public boolean isIdAllowed() {
        return true;
    }
}
