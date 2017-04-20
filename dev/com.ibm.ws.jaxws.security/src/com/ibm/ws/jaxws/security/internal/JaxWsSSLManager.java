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
package com.ibm.ws.jaxws.security.internal;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import javax.net.ssl.SSLSocketFactory;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.ssl.JSSEHelper;
import com.ibm.websphere.ssl.SSLException;
import com.ibm.ws.ffdc.annotation.FFDCIgnore;
import com.ibm.wsspi.kernel.service.utils.AtomicServiceReference;
import com.ibm.wsspi.ssl.SSLSupport;

/**
 * Using JaxWsSSLManager to do the SSL stuff
 */
public class JaxWsSSLManager {
    private static final TraceComponent tc = Tr.register(JaxWsSSLManager.class);
    private static final AtomicReference<AtomicServiceReference<SSLSupport>> sslSupportServiceRef = new AtomicReference<AtomicServiceReference<SSLSupport>>();

    protected static void init(AtomicServiceReference<SSLSupport> sslSupportSR) {
        sslSupportServiceRef.set(sslSupportSR);
    }

    public static SSLSocketFactory getProxySSLSocketFactoryBySSLRef(String sslRef, Map<String, Object> props) {
        return new JaxWsProxySSLSocketFactory(sslRef, props);
    }

    public static SSLSocketFactory getProxyDefaultSSLSocketFactory(Map<String, Object> props) {
        return new JaxWsProxySSLSocketFactory(JaxWsSecurityConstants.SERVER_DEFAULT_SSL_CONFIG_ALIAS, props);
    }

    /**
     * Get the SSLSocketFactory by sslRef, if could not get the configuration, try use the server's default
     * ssl configuration when fallbackOnDefault = true
     *
     * @param sslRef
     * @param props the additional props to override the properties in SSLConfig
     * @param fallbackOnDefault if true, will fall back on server default ssl configuration
     * @return
     */
    @FFDCIgnore(PrivilegedActionException.class)
    public static SSLSocketFactory getSSLSocketFactoryBySSLRef(String sslRef, Map<String, Object> props, boolean fallbackOnDefault) {
        SSLSupport sslSupportService = tryGetSSLSupport();

        if (null == sslSupportService) {
            return null;
        }

        JSSEHelper jsseHelper = sslSupportService.getJSSEHelper();
        Properties sslConfig = null;
        try {
            final JSSEHelper f_jsseHelper = jsseHelper;

            if (sslRef != null) {
                final String f_sslRef = sslRef;
                try {
                    sslConfig = AccessController.doPrivileged(new PrivilegedExceptionAction<Properties>() {
                        @Override
                        public Properties run() throws SSLException {
                            return f_jsseHelper.getProperties(f_sslRef);
                        }
                    });

                } catch (PrivilegedActionException pae) {
                    Throwable cause = pae.getCause();
                    throw (SSLException) cause;
                }
            }

            // override the existed property in SSLConfig
            if (null != props && !props.isEmpty() && sslConfig != null) {
                Iterator<Map.Entry<String, Object>> iter = props.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<String, Object> entry = iter.next();
                    if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                        Tr.debug(tc, entry.getKey() + "=" + entry.getValue() + " is overriden in SSLConfig=" + sslRef);
                    }
                    sslConfig.put(entry.getKey(), entry.getValue());
                }
            }

            if (sslConfig != null) {
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                    Tr.debug(tc, "Get the SSLSocketFactory by properties =" + sslConfig);
                }
                return sslSupportService.getSSLSocketFactory(sslConfig);
            } else {
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                    Tr.debug(tc, "Get the default SSLSocketFactory");
                }
                return sslSupportService.getSSLSocketFactory();
            }
        } catch (SSLException e) {
            Tr.error(tc, "err.when.get.ssl.config", sslRef);
            throw new IllegalArgumentException(e);
        } catch (Exception e) {
            Tr.error(tc, "err.when.get.ssl.socket.factory", sslRef, e.getMessage());
            throw new IllegalStateException(e);
        }
    }

    private static SSLSupport tryGetSSLSupport() {
        AtomicServiceReference<SSLSupport> serviceRef = sslSupportServiceRef.get();
        if (null == serviceRef) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "The sslSupportService is not set yet");
            }
            return null;
        }

        return serviceRef.getService();
    }
}
