/*
IBM Confidential
 *
OCO Source Materials
 *
WLP Copyright IBM Corp. 2014, 2017
 *
The source code for this program is not published or otherwise divested
of its trade secrets, irrespective of what has been deposited with the
U.S. Copyright Office.
 */
package com.ibm.ws.jaxrs20.client.configuration;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;

import org.apache.cxf.configuration.security.ProxyAuthorizationPolicy;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.transport.Conduit;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.cxf.transports.http.configuration.ProxyServerType;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.ws.jaxrs20.client.JAXRSClientConstants;

/**
 *
 */
public class LibertyJaxRsClientProxyInterceptor extends AbstractPhaseInterceptor<Message> {

    private static final TraceComponent tc = Tr.register(LibertyJaxRsClientProxyInterceptor.class);

    static {
        AccessController.doPrivileged(new PrivilegedAction<Void>() {

            private final static String DISABLED_SCHEMES_PROP = "jdk.http.auth.tunneling.disabledSchemes";

            @Override
            public Void run() {
                // This property is required for later versions of Java 8 due to Basic Auth
                // tunneling being disabled in the JVM by default. For more info, see section
                // labeled "Disable Basic authentication for HTTPS tunneling" here:
                // http://www.oracle.com/technetwork/java/javase/8u111-relnotes-3124969.html

                // Only set the property if it is not already set:
                String propVal = System.getProperty(DISABLED_SCHEMES_PROP);
                if (propVal == null) {
                    if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                        Tr.debug(tc, "<clinit> setting property " + DISABLED_SCHEMES_PROP + "=''");
                    }
                    System.setProperty(DISABLED_SCHEMES_PROP, "");
                } else {
                    if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                        Tr.debug(tc, "<clinit> property " + DISABLED_SCHEMES_PROP + " already set to " + propVal);
                    }
                }

                return null;
            }
        });
    }

    @Trivial
    private static String toString(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof List<?>) {
            Object o2 = ((List<?>) o).get(0);
            o = o2;
        }
        if (o instanceof String) {
            return (String) o;
        }
        return o.toString();

    }

    /**
     * @param phase
     */
    public LibertyJaxRsClientProxyInterceptor(String phase) {
        super(phase);
    }

    @Override
    public void handleMessage(Message message) throws Fault {

        String host = toString(message.get(JAXRSClientConstants.PROXY_HOST));
        String port = toString(message.get(JAXRSClientConstants.PROXY_PORT));
        String type = toString(message.get(JAXRSClientConstants.PROXY_TYPE));
        String proxyAuthType = toString(message.get(JAXRSClientConstants.PROXY_AUTH_TYPE));
        String proxyAuthUser = toString(message.get(JAXRSClientConstants.PROXY_USERNAME));
        String proxyAuthPW = toString(message.get(JAXRSClientConstants.PROXY_PASSWORD));

        Conduit conduit = message.getExchange().getConduit(message);

        if (host != null) {
            String sHost = host.toString();
            if (!sHost.isEmpty() && conduit instanceof HTTPConduit) {
                configClientProxy((HTTPConduit) conduit, sHost, port, type, proxyAuthType,
                                  proxyAuthUser, proxyAuthPW);
            }

        }

    }

    private void configClientProxy(HTTPConduit httpConduit, String host, String port, String type, String proxyAuthType,
                                   String proxyAuthUser, String proxyAuthPW) {

        int iPort = JAXRSClientConstants.PROXY_PORT_DEFAULT;
        if (port != null) {
            try {
                iPort = Integer.parseInt(port);
            } catch (NumberFormatException e) {
                //The proxy server port value {0} that you specified in the property {1} on the JAX-RS Client side is invalid. The value is set to default.
                Tr.error(tc, "error.jaxrs.client.configuration.proxy.portinvalid", port, JAXRSClientConstants.PROXY_PORT, JAXRSClientConstants.PROXY_PORT_DEFAULT,
                         e.getMessage());

            }
        }

        ProxyServerType proxyServerType = ProxyServerType.HTTP;
        if (type != null) {
            try {
                proxyServerType = ProxyServerType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                //The proxy server type value {0} that you specified in the property {1} on the JAX-RS Client side is invalid. The value is set to default.
                Tr.error(tc, "error.jaxrs.client.configuration.proxy.typeinvalid", type, JAXRSClientConstants.PROXY_TYPE, ProxyServerType.HTTP, e.getMessage());

            }
        }

        httpConduit.getClient().setProxyServer(host);
        httpConduit.getClient().setProxyServerPort(iPort);
        httpConduit.getClient().setProxyServerType(proxyServerType);

        HTTPClientPolicy clientPolicy = new HTTPClientPolicy();
        clientPolicy.setProxyServer(host);
        clientPolicy.setProxyServerPort(iPort);
        clientPolicy.setProxyServerType(proxyServerType);
        httpConduit.setClient(clientPolicy);

        ProxyAuthorizationPolicy authPolicy = null;

        if (proxyAuthUser != null || proxyAuthPW != null) { // authType / authUser / authPW client props
            authPolicy = new ProxyAuthorizationPolicy();
            // for now, always use Basic auth type
            if (proxyAuthType != null && !JAXRSClientConstants.PROXY_AUTH_TYPE_DEFAULT.equalsIgnoreCase(proxyAuthType)) {
                //TODO make warning
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                    Tr.debug(tc, "Unrecognized proxy authorization type, \"" + proxyAuthType + "\".  Only \"Basic\" is recognized.");
                }
            }
            proxyAuthType = JAXRSClientConstants.PROXY_AUTH_TYPE_DEFAULT;

            authPolicy.setAuthorizationType(proxyAuthType);

            if (proxyAuthUser != null) {
                authPolicy.setUserName(proxyAuthUser);
            } else {
                //TODO: make warning
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                    Tr.debug(tc, "No proxy authorization username specified.  No proxy authorization data will be generated.");
                }
                authPolicy = null;
            }

            if (authPolicy != null && proxyAuthPW != null) {
                authPolicy.setPassword(proxyAuthPW);
            } else if (proxyAuthPW == null) {
                //TODO: make warning
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                    Tr.debug(tc, "No proxy authorization password specified.  No proxy authorization data will be generated.");
                }
                authPolicy = null;
            }
        }

        if (authPolicy != null) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "configuring proxy auth policy " + authPolicy + " + to httpConduit " + httpConduit);
            }
            httpConduit.setProxyAuthorization(authPolicy);
        }

    }
}