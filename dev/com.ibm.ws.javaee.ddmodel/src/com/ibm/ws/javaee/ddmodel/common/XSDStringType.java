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
package com.ibm.ws.javaee.ddmodel.common;

import com.ibm.ws.javaee.ddmodel.DDParser;
import com.ibm.ws.javaee.ddmodel.DDParser.ParseException;
import com.ibm.ws.javaee.ddmodel.StringType;

/*
 <xsd:complexType name="xsdStringType">
 <xsd:simpleContent>
 <xsd:extension base="xsd:string">
 <xsd:attribute name="id"
 type="xsd:ID"/>
 </xsd:extension>
 </xsd:simpleContent>
 </xsd:complexType>
 */

public class XSDStringType extends StringType {
    public static XSDStringType wrap(DDParser parser, String wrapped) throws ParseException {
        return new XSDStringType(parser, wrapped);
    }

    public XSDStringType() {}

    public XSDStringType(boolean untrimmed) {
        super(untrimmed);
    }

    protected XSDStringType(DDParser parser, String lexical) throws ParseException {
        super(Whitespace.preserve, parser, lexical);
    }

    @Override
    public boolean isIdAllowed() {
        return true;
    }
}
