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

import com.ibm.ws.javaee.dd.common.RunAs;
import com.ibm.ws.javaee.ddmodel.DDParser;
import com.ibm.ws.javaee.ddmodel.DDParser.ParseException;

/*
 <xsd:complexType name="run-asType">
 <xsd:sequence>
 <xsd:element name="description"
 type="javaee:descriptionType"
 minOccurs="0"
 maxOccurs="unbounded"/>
 <xsd:element name="role-name"
 type="javaee:role-nameType"/>
 </xsd:sequence>
 <xsd:attribute name="id"
 type="xsd:ID"/>
 </xsd:complexType>
 */

public class RunAsType extends DescribableType implements RunAs {

    @Override
    public String getRoleName() {
        return role_name.getValue();
    }

    // elements
    XSDTokenType role_name = new XSDTokenType();

    @Override
    public boolean isIdAllowed() {
        return true;
    }

    @Override
    public boolean handleChild(DDParser parser, String localName) throws ParseException {
        if (super.handleChild(parser, localName)) {
            return true;
        }
        if ("role-name".equals(localName)) {
            parser.parse(role_name);
            return true;
        }
        return false;
    }

    @Override
    public void describe(DDParser.Diagnostics diag) {
        super.describe(diag);
        diag.describe("role-name", role_name);
    }
}
