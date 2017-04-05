/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jaxws.wsat.components;

import java.net.URL;
import java.util.Map;
import java.util.Properties;

import javax.net.ssl.SSLSocketFactory;

import org.apache.cxf.transport.Conduit;

/**
 *
 */
public interface WSATSSLService {
    public boolean checkId(String id);

    public Properties getProperties(String id) throws Exception;

    public Map<String, Object> getOutboundConnectionMap();

    public SSLSocketFactory getSSLSocketFactory(String id, URL url) throws Exception;

    public void setSSLFactory(Conduit conduit, String sslRef, String certAlias);

}
