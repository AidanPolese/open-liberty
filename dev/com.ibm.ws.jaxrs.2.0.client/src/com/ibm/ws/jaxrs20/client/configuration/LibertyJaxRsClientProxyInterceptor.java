/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jaxrs20.client.configuration;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.transport.Conduit;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.ProxyServerType;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.jaxrs20.client.JAXRSClientConstants;

/**
 *
 */
public class LibertyJaxRsClientProxyInterceptor extends AbstractPhaseInterceptor<Message> {

    private static final TraceComponent tc = Tr.register(LibertyJaxRsClientProxyInterceptor.class);

    /**
     * @param phase
     */
    public LibertyJaxRsClientProxyInterceptor(String phase) {
        super(phase);
    }

    @Override
    public void handleMessage(Message message) throws Fault {

        Object host = message.get(JAXRSClientConstants.PROXY_HOST);
        Object port = message.get(JAXRSClientConstants.PROXY_PORT);
        Object type = message.get(JAXRSClientConstants.PROXY_TYPE);

        Conduit conduit = message.getExchange().getConduit(message);

        if (host != null) {
            String sHost = host.toString();
            if (!sHost.isEmpty()) {
                configClientProxy(conduit, sHost, port, type);
            }

        }

    }

    private void configClientProxy(Conduit conduit, String host, Object port, Object type) {

        if (conduit instanceof HTTPConduit) {
            HTTPConduit httpConduit = (HTTPConduit) conduit;

            int iPort = JAXRSClientConstants.PROXY_PORT_DEFAULT;
            if (port != null) {
                String sPort = port.toString();
                try {
                    iPort = Integer.parseInt(sPort);
                } catch (NumberFormatException e) {
                    //The proxy server port value {0} that you specified in the property {1} on the JAX-RS Client side is invalid. The value is set to default.
                    Tr.error(tc, "error.jaxrs.client.configuration.proxy.portinvalid", sPort, JAXRSClientConstants.PROXY_PORT, JAXRSClientConstants.PROXY_PORT_DEFAULT,
                             e.getMessage());

                }
            }

            ProxyServerType proxyServerType = ProxyServerType.HTTP;
            if (type != null) {
                String sType = type.toString();

                try {
                    proxyServerType = ProxyServerType.valueOf(sType.toUpperCase());
                } catch (IllegalArgumentException e) {
                    //The proxy server type value {0} that you specified in the property {1} on the JAX-RS Client side is invalid. The value is set to default.
                    Tr.error(tc, "error.jaxrs.client.configuration.proxy.typeinvalid", sType, JAXRSClientConstants.PROXY_TYPE, ProxyServerType.HTTP, e.getMessage());

                }
            }

            httpConduit.getClient().setProxyServer(host);
            httpConduit.getClient().setProxyServerPort(iPort);
            httpConduit.getClient().setProxyServerType(proxyServerType);

        }
    }
}
