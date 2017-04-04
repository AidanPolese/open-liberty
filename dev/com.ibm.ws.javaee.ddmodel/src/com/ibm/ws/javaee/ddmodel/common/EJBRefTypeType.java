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

import com.ibm.ws.javaee.ddmodel.DDParser;
import com.ibm.ws.javaee.ddmodel.DDParser.ParseException;

/*
 <xsd:complexType name="ejb-ref-typeType">
 <xsd:simpleContent>
 <xsd:restriction base="javaee:xsdTokenType">
 <xsd:enumeration value="Entity"/>
 <xsd:enumeration value="Session"/>
 </xsd:restriction>
 </xsd:simpleContent>
 </xsd:complexType>
 */

public class EJBRefTypeType extends XSDTokenType {

    static enum EJBRefTypeEnum {
        // lexical value must be (Entity|Session)
        Entity,
        Session;
    }

    // content
    EJBRefTypeEnum value;

    @Override
    public void finish(DDParser parser) throws ParseException {
        super.finish(parser);
        if (!isNil()) {
            value = parseEnumValue(parser, EJBRefTypeEnum.class);
        }
    }

    @Override
    public void describe(DDParser.Diagnostics diag) {
        diag.describeEnum(value);
    }
}
