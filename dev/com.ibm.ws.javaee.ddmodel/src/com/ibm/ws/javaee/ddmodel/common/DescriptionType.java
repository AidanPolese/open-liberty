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

import javax.xml.XMLConstants;

import com.ibm.ws.javaee.dd.common.Description;
import com.ibm.ws.javaee.ddmodel.DDParser;
import com.ibm.ws.javaee.ddmodel.DDParser.ParsableListImplements;
import com.ibm.ws.javaee.ddmodel.DDParser.ParseException;
import com.ibm.ws.javaee.ddmodel.TokenType;

/*
 <xsd:complexType name="descriptionType">
 <xsd:simpleContent>
 <xsd:extension base="javaee:xsdStringType">
 <xsd:attribute ref="xml:lang"/>
 </xsd:extension>
 </xsd:simpleContent>
 </xsd:complexType>
 */

public class DescriptionType extends XSDStringType implements Description {

    public static class ListType extends ParsableListImplements<DescriptionType, Description> {
        @Override
        public DescriptionType newInstance(DDParser parser) {
            return new DescriptionType();
        }
    }

    @Override
    public String getLang() {
        return xml_lang != null ? xml_lang.getValue() : null;
    }

    // attributes
    TokenType xml_lang;

    @Override
    public boolean handleAttribute(DDParser parser, String nsURI, String localName, int index) throws ParseException {
        if (XMLConstants.XML_NS_URI.equals(nsURI) && "lang".equals(localName)) {
            xml_lang = parser.parseTokenAttributeValue(index);
            return true;
        }
        return false;
    }

    @Override
    public void describe(DDParser.Diagnostics diag) {
        super.describe(diag);
        diag.describeIfSet("xml:lang", xml_lang);
    }
}
