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

import com.ibm.ws.javaee.dd.common.ParamValue;
import com.ibm.ws.javaee.ddmodel.DDParser;
import com.ibm.ws.javaee.ddmodel.DDParser.ParsableListImplements;
import com.ibm.ws.javaee.ddmodel.DDParser.ParseException;

/*
 <xsd:complexType name="param-valueType">
 <xsd:sequence>
 <xsd:element name="description"
 type="javaee:descriptionType"
 minOccurs="0"
 maxOccurs="unbounded"/>
 <xsd:element name="param-name"
 type="javaee:xsdTokenType">
 </xsd:element>
 <xsd:element name="param-value"
 type="javaee:xsdStringType">
 </xsd:element>
 </xsd:sequence>
 <xsd:attribute name="id"
 type="xsd:ID"/>
 </xsd:complexType>
 */

public class ParamValueType extends DescribableType implements ParamValue {

    public static class ListType extends ParsableListImplements<ParamValueType, ParamValue> {
        @Override
        public ParamValueType newInstance(DDParser parser) {
            return new ParamValueType();
        }
    }

    @Override
    public String getName() {
        return param_name.getValue();
    }

    @Override
    public String getValue() {
        return param_value.getValue();
    }

    // elements
    XSDTokenType param_name = new XSDTokenType();
    XSDStringType param_value = new XSDStringType();

    @Override
    public boolean isIdAllowed() {
        return true;
    }

    @Override
    public boolean handleChild(DDParser parser, String localName) throws ParseException {
        if (super.handleChild(parser, localName)) {
            return true;
        }
        if ("param-name".equals(localName)) {
            parser.parse(param_name);
            return true;
        }
        if ("param-value".equals(localName)) {
            parser.parse(param_value);
            return true;
        }
        return false;
    }

    @Override
    public void describe(DDParser.Diagnostics diag) {
        super.describe(diag);
        diag.describe("param-name", param_name);
        diag.describe("param-value", param_value);
    }
}
