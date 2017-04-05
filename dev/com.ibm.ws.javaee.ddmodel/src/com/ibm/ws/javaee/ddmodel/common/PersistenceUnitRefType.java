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

import com.ibm.ws.javaee.dd.common.PersistenceUnitRef;
import com.ibm.ws.javaee.ddmodel.DDParser;
import com.ibm.ws.javaee.ddmodel.DDParser.ParsableListImplements;

/*
 <xsd:complexType name="persistence-unit-refType">
 <xsd:sequence>
 <xsd:element name="description"
 type="javaee:descriptionType"
 minOccurs="0"
 maxOccurs="unbounded"/>
 <xsd:element name="persistence-unit-ref-name"
 type="javaee:jndi-nameType">
 </xsd:element>
 <xsd:element name="persistence-unit-name"
 type="javaee:xsdTokenType"
 minOccurs="0">
 </xsd:element>
 <xsd:group ref="javaee:resourceBaseGroup"/>
 </xsd:sequence>
 <xsd:attribute name="id"
 type="xsd:ID"/>
 </xsd:complexType>
 */

public class PersistenceUnitRefType extends PersistenceRefType implements PersistenceUnitRef {

    public static class ListType extends ParsableListImplements<PersistenceUnitRefType, PersistenceUnitRef> {
        @Override
        public PersistenceUnitRefType newInstance(DDParser parser) {
            return new PersistenceUnitRefType();
        }
    }

    public PersistenceUnitRefType() {
        super("persistence-unit-ref-name");
    }
}
