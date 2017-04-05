/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jaxrs20.support;

import java.io.IOException;

import javax.xml.namespace.QName;

import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.transport.Conduit;
import org.apache.cxf.transport.http.HTTPTransportFactory;
import org.apache.cxf.ws.addressing.EndpointReferenceType;

/**
 * LibertyHTTPTransportFactory provides Liberty extension for CXF internal HTTPTransportFactory, it provides the extra functions below :
 * a. Enable auto redirect function while loading WSDL file, as WebSphere full profile will send a redirect response while accessing WSDL with ?wsdl
 * b. create our LibertyHTTPConduit so that we can set the TCCL when run the handleResponseInternal asynchronously
 */
public class LibertyHTTPTransportFactory extends HTTPTransportFactory {

    private static final QName CXF_TRANSPORT_URI_RESOLVER_QNAME = new QName("http://cxf.apache.org", "TransportURIResolver");

    /**
     * set the auto-redirect to true
     */
    public Conduit getConduit(EndpointInfo endpointInfo, EndpointReferenceType target) throws IOException {
        //create our LibertyHTTPConduit so that we can set the TCCL when run the handleResponseInternal asynchronously
//        LibertyHTTPConduit conduit = new LibertyHTTPConduit(registry.getDestinations()., endpointInfo, target);

        //following are copied from the super class.
        //Spring configure the conduit.  
//        String address = conduit.getAddress();
//        if (address != null && address.indexOf('?') != -1) {
//            address = address.substring(0, address.indexOf('?'));
//        }
//        HTTPConduitConfigurer c1 = bus.getExtension(HTTPConduitConfigurer.class);
//        if (c1 != null) {
//            c1.configure(conduit.getBeanName(), address, conduit);
//        }
//        configure(conduit, conduit.getBeanName(), address);
//        conduit.finalizeConfig();
//        //copy end.
//
//        //open the auto redirect when load wsdl, and close auto redirect when process soap message in default.
//        //users can open the auto redirect for soap message with ibm-ws-bnd.xml
//        if (conduit != null) {
//            HTTPClientPolicy clientPolicy = conduit.getClient();
//
//            clientPolicy.setAutoRedirect(CXF_TRANSPORT_URI_RESOLVER_QNAME.equals(endpointInfo.getName()));
//        }

        return null;
    }
}
