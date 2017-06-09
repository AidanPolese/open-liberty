/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2017
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.jaxrs20.client.security;

import javax.net.ssl.SSLSocketFactory;

import org.apache.cxf.common.util.StringUtils;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.jaxrs.client.spec.TLSConfiguration;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.transport.Conduit;
import org.apache.cxf.transport.http.HTTPConduit;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.jaxrs20.client.JAXRSClientConstants;
import com.ibm.ws.jaxrs20.client.component.JaxRsAppSecurity;

/**
 *
 */
public class LibertyJaxRsClientSSLOutInterceptor extends AbstractPhaseInterceptor<Message> {

    private static final TraceComponent tc = Tr.register(LibertyJaxRsClientSSLOutInterceptor.class);

    private static final String HTTPS_SCHEMA = "https";

    private TLSConfiguration secConfig = null;

    /**
     * @param phase
     */
    public LibertyJaxRsClientSSLOutInterceptor(String phase) {
        super(phase);
    }

    @Override
    public void handleMessage(Message message) throws Fault {

        //SSL check
        //see if HTTPS is used
        boolean isSecured = false;
        String address = (String) message.get(Message.ENDPOINT_ADDRESS);
        isSecured = address == null ? false : address.startsWith(HTTPS_SCHEMA);

        //see if SSL Ref id is used
        Object sslRefObj = message.get(JAXRSClientConstants.SSL_REFKEY);
        String sslRef = null;

        if (sslRefObj != null) {
            sslRef = (String) sslRefObj;
        }

        Object disableCNCheckObj = message.get(JAXRSClientConstants.DISABLE_CN_CHECK);
        //Allow a null sslRef to be used,  Liberty will resolve it to the default // getSSLSocketFactory will return null if either the ssl feature is not enabled
        // or if it is enabled but there is no SSL configuration defined.  A null here
        // means to use the JDK's SSL implementation.
        if (isSecured && JaxRsAppSecurity.getSSLSocketFactory(sslRef, null) != null) {

            boolean disableCNCheck = false;
            if (disableCNCheckObj instanceof Boolean) {
                disableCNCheck = ((Boolean) disableCNCheckObj).booleanValue();
            } else if (disableCNCheckObj instanceof String) {
                disableCNCheck = Boolean.parseBoolean((String) disableCNCheckObj);
            }

            Conduit cd = message.getExchange().getConduit(message);
            configClientSSL(cd, sslRef, disableCNCheck);
        }
    }

    private void configClientSSL(Conduit conduit, String sslRef, boolean disableCNCheck) {

        //for HTTPS protocol
        if (conduit instanceof HTTPConduit) {
            HTTPConduit httpConduit = (HTTPConduit) conduit;

            TLSClientParameters tlsClientParams = retriveHTTPTLSClientParametersUsingSSLRef(httpConduit, sslRef, disableCNCheck);
            if (null != tlsClientParams) {
                httpConduit.setTlsClientParameters(tlsClientParams);
            }
        }
    }

    private TLSClientParameters retriveHTTPTLSClientParametersUsingSSLRef(HTTPConduit httpConduit, String sslRef, boolean disableCNCheck) {
        TLSClientParameters tlsClientParams = null;
        if (this.secConfig == null) {
            tlsClientParams = httpConduit.getTlsClientParameters();
        } else {
            tlsClientParams = this.secConfig.getTlsClientParams();
        }

        SSLSocketFactory sslSocketFactory = null;

        if (!StringUtils.isEmpty(sslRef)) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "Use the sslRef = " + sslRef + " to create the SSLSocketFactory.");
            }
        } else {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "Get Liberty default SSLSocketFactory.");
            }
        }
        sslSocketFactory = JaxRsAppSecurity.getSSLSocketFactory(sslRef, null);

        if (null != sslSocketFactory) {
            if (null == tlsClientParams) {
                tlsClientParams = new TLSClientParameters();
            }
            //let's use liberty SSL configuration
            tlsClientParams.setSSLSocketFactory(sslSocketFactory);
            tlsClientParams.setDisableCNCheck(disableCNCheck);
        } else if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "May not enable feature ssl-1.0 or appSecurity-2.0.");
        }

        return tlsClientParams;
    }

    public void setTLSConfiguration(TLSConfiguration secConfig) {
        this.secConfig = secConfig;
    }

}