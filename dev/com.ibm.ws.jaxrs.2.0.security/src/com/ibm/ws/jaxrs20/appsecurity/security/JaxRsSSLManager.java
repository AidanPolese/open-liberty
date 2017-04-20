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
package com.ibm.ws.jaxrs20.appsecurity.security;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Map;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocketFactory;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.ssl.JSSEHelper;
import com.ibm.ws.ffdc.annotation.FFDCIgnore;
import com.ibm.ws.jaxrs20.appsecurity.component.SSLSupportService;
import com.ibm.wsspi.ssl.SSLSupport;

public class JaxRsSSLManager {
    private static final TraceComponent tc = Tr.register(JaxRsSSLManager.class);

    public static SSLSocketFactory getProxySSLSocketFactoryBySSLRef(String sslRef, Map<String, Object> props) {
        return SSLSupportService.isSSLSupportServiceReady() ? new JaxRsProxySSLSocketFactory(sslRef, props) : null;
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

        if (!SSLSupportService.isSSLSupportServiceReady()) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "The SSL support service is not ready and can't create SSLSocketFactory");
            }
            return null;
        }

        SSLSupport sslSupportService = SSLSupportService.getSSLSupport();

        JSSEHelper jsseHelper = sslSupportService.getJSSEHelper();
        Boolean sslCfgExists = null;
        try {
            final JSSEHelper f_jsseHelper = jsseHelper;
            if (sslRef != null) {
                final String f_sslRef = sslRef;
                try {
                    sslCfgExists = AccessController.doPrivileged(new PrivilegedExceptionAction<Boolean>() {
                        @Override
                        public Boolean run() throws SSLException {
                            return Boolean.valueOf(f_jsseHelper.doesSSLConfigExist(f_sslRef));
                        }
                    });

                } catch (PrivilegedActionException pae) {
                    Throwable cause = pae.getCause();
                    throw (SSLException) cause;
                }

                if (!sslCfgExists.booleanValue())
                    return null;
            }

            return sslSupportService.getSSLSocketFactory(sslRef);
        } catch (SSLException e) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "SSL Exception with ssl ref id " + sslRef + ": " + e.toString());
            }
            throw new IllegalArgumentException(e);
        } catch (Exception e) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "Exception with ssl ref id " + sslRef + ": " + e.toString());
            }
            throw new IllegalStateException(e);
        }
    }

}
