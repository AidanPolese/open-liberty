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

import com.ibm.ws.javaee.ddmodel.IntegerType;

/*
 <xsd:complexType name="xsdIntegerType">
 <xsd:simpleContent>
 <xsd:extension base="xsd:integer">
 <xsd:attribute name="id"
 type="xsd:ID"/>
 </xsd:extension>
 </xsd:simpleContent>
 </xsd:complexType>
 */

public class XSDIntegerType extends IntegerType {

    @Override
    public boolean isIdAllowed() {
        return true;
    }
}
