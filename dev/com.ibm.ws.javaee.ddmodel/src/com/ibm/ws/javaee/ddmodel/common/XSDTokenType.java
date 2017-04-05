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

import java.util.ArrayList;
import java.util.List;

import com.ibm.ws.javaee.ddmodel.DDParser;
import com.ibm.ws.javaee.ddmodel.DDParser.ParsableList;
import com.ibm.ws.javaee.ddmodel.TokenType;

/*
 <xsd:complexType name="string">
 <xsd:simpleContent>
 <xsd:extension base="xsd:token">
 <xsd:attribute name="id"
 type="xsd:ID"/>
 </xsd:extension>
 </xsd:simpleContent>
 </xsd:complexType>
 */

public class XSDTokenType extends TokenType {

    public static class ListType extends ParsableList<XSDTokenType> {
        @Override
        public XSDTokenType newInstance(DDParser parser) {
            return new XSDTokenType();
        }

        public List<String> getList() {
            List<String> stringList = new ArrayList<String>();
            for (XSDTokenType token : list) {
                stringList.add(token.getValue());
            }
            return stringList;
        }
    }

    @Override
    public boolean isIdAllowed() {
        return true;
    }
}
