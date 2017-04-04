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

import com.ibm.ws.javaee.dd.common.QName;
import com.ibm.ws.javaee.ddmodel.DDParser;
import com.ibm.ws.javaee.ddmodel.DDParser.ParsableListImplements;
import com.ibm.ws.javaee.ddmodel.QNameType;

/*
 <xsd:complexType name="xsdQNameType">
 <xsd:simpleContent>
 <xsd:extension base="xsd:QName">
 <xsd:attribute name="id"
 type="xsd:ID"/>
 </xsd:extension>
 </xsd:simpleContent>
 </xsd:complexType>
 */

public class XSDQNameType extends QNameType {

    public static class ListType extends ParsableListImplements<XSDQNameType, QName> {
        @Override
        public XSDQNameType newInstance(DDParser parser) {
            return new XSDQNameType();
        }
    }

    @Override
    public boolean isIdAllowed() {
        return true;
    }
}
