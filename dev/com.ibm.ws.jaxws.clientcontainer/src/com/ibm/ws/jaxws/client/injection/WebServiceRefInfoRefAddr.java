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

package com.ibm.ws.jaxws.client.injection;

import javax.naming.RefAddr;

import com.ibm.ws.jaxws.metadata.WebServiceRefInfo;

/**
 * This class serves as a "holder" for our WebServiceRefInfo clas. This holder object will be constructed with an
 * instance of our WebServiceRefInfo class (serializable class which holds service-ref related metadata) and added to
 * naming Reference object that represents a service-ref in the JNDI namespace.
 */
public class WebServiceRefInfoRefAddr extends RefAddr {

    private static final long serialVersionUID = 5645835508590997002L;

    // This is our address type key string
    public static final String ADDR_KEY = "WebServiceRefInfo";

    // Info object
    private WebServiceRefInfo wsrInfo = null;

    public WebServiceRefInfoRefAddr(WebServiceRefInfo info) {
        super(ADDR_KEY);
        this.wsrInfo = info;
    }

    @Override
    public Object getContent() {
        return wsrInfo;
    }
}
