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
package com.ibm.ws.javaee.ddmodel.web.common;

import java.util.List;

import com.ibm.ws.javaee.dd.web.common.WelcomeFileList;
import com.ibm.ws.javaee.ddmodel.DDParser;
import com.ibm.ws.javaee.ddmodel.DDParser.ParseException;
import com.ibm.ws.javaee.ddmodel.StringType;

/*
 <xsd:complexType name="welcome-file-listType">
 <xsd:sequence>
 <xsd:element name="welcome-file"
 type="xsd:string"
 maxOccurs="unbounded">
 </xsd:element>
 </xsd:sequence>
 <xsd:attribute name="id"
 type="xsd:ID"/>
 </xsd:complexType>
 */

public class WelcomeFileListType extends DDParser.ElementContentParsable implements WelcomeFileList {

    @Override
    public List<String> getWelcomeFiles() {
        return welcome_file.getList();
    }

    // elements
    StringType.ListType welcome_file = new StringType.ListType();

    @Override
    public boolean isIdAllowed() {
        return true;
    }

    @Override
    public boolean handleChild(DDParser parser, String localName) throws ParseException {
        if ("welcome-file".equals(localName)) {
            StringType welcome_file = new StringType();
            parser.parse(welcome_file);
            this.welcome_file.add(welcome_file);
            return true;
        }
        return false;
    }

    @Override
    public void describe(DDParser.Diagnostics diag) {
        diag.describe("welcome-file", welcome_file);
    }
}
