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
package com.ibm.ws.javaee.ddmodel.common.wsclient;

import java.util.Collections;
import java.util.List;

import com.ibm.ws.javaee.dd.common.wsclient.ServiceRef;
import com.ibm.ws.javaee.ddmodel.DDParser;
import com.ibm.ws.javaee.ddmodel.DDParser.ParseException;

/*
 <xsd:group name="service-refGroup">
 <xsd:sequence>
 <xsd:element name="service-ref"
 type="javaee:service-refType"
 minOccurs="0"
 maxOccurs="unbounded">
 <xsd:key name="service-ref_handler-name-key">
 <xsd:selector xpath="javaee:handler"/>
 <xsd:field xpath="javaee:handler-name"/>
 </xsd:key>
 </xsd:element>
 </xsd:sequence>
 </xsd:group>
 */

public class ServiceRefGroup extends DDParser.ElementContentParsable {
    ServiceRefType.ListType service_ref;

    public List<ServiceRef> getServiceRefs() {
        if (service_ref != null) {
            return service_ref.getList();
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public boolean handleChild(DDParser parser, String localName) throws ParseException {
        if ("service-ref".equals(localName)) {
            ServiceRefType service_ref = new ServiceRefType();
            parser.parse(service_ref);
            addServiceRef(service_ref);
            return true;
        }
        return false;
    }

    private void addServiceRef(ServiceRefType service_ref) {
        if (this.service_ref == null) {
            this.service_ref = new ServiceRefType.ListType();
        }
        this.service_ref.add(service_ref);
    }

    @Override
    public void describe(DDParser.Diagnostics diag) {
        diag.describeIfSet("service-ref", service_ref);
    }
}
