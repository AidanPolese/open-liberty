/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.ddmodel.common.wsclient;

import com.ibm.ws.javaee.ddmodel.DDParser;
import com.ibm.ws.javaee.ddmodel.DDParser.ParseException;
import com.ibm.ws.javaee.ddmodel.common.XSDTokenType;

/*
 * <xsd:complexType name="addressing-responsesType">
 * <xsd:simpleContent>
 * <xsd:restriction base="javaee:xsdTokenType">
 * <xsd:enumeration value="ANONYMOUS"/>
 * <xsd:enumeration value="NON_ANONYMOUS"/>
 * <xsd:enumeration value="ALL"/>
 * </xsd:restriction>
 * </xsd:simpleContent>
 * </xsd:complexType>
 */
public class AddressingResponsesType extends XSDTokenType {

    static enum AddressingResponsesEnum {
        // lexical value must be (ANONYMOUS|NON_ANONYMOUS|ALL)
        ANONYMOUS,
        NON_ANONYMOUS,
        ALL;
    }

    // content
    AddressingResponsesEnum value;

    @Override
    public void finish(DDParser parser) throws ParseException {
        super.finish(parser);
        if (!isNil()) {
            value = parseEnumValue(parser, AddressingResponsesEnum.class);
        }
    }

    @Override
    public void describe(DDParser.Diagnostics diag) {
        diag.describeEnum(value);
    }
}
