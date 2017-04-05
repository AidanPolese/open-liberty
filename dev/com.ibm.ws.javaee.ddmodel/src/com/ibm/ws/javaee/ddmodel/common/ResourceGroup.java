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
 <xsd:group name="resourceGroup">
 <xsd:sequence>
 <xsd:group ref="javaee:resourceBaseGroup"/>
 <xsd:element name="lookup-name"
 type="javaee:xsdStringType"
 minOccurs="0">
 </xsd:element>
 </xsd:sequence>
 </xsd:group>
 */

public class ResourceGroup extends ResourceBaseGroup implements com.ibm.ws.javaee.dd.common.ResourceGroup {

    @Override
    public String getLookupName() {
        return lookup_name != null ? lookup_name.getValue() : null;
    }

    // ResourceBaseGroup fields appear here in sequence
    XSDStringType lookup_name;

    protected ResourceGroup(String element_local_name) {
        super(element_local_name);
    }

    @Override
    public boolean handleChild(DDParser parser, String localName) throws ParseException {
        if (super.handleChild(parser, localName)) {
            return true;
        }
        if ("lookup-name".equals(localName)) {
            XSDStringType lookup_name = new XSDStringType();
            parser.parse(lookup_name);
            this.lookup_name = lookup_name;
            return true;
        }
        return false;
    }

    @Override
    public void describe(DDParser.Diagnostics diag) {
        super.describe(diag);
        diag.describeIfSet("lookup-name", lookup_name);
    }
}
