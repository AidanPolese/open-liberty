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
package com.ibm.ws.jca.internal;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import javax.net.ssl.SSLSocketFactory;

import org.osgi.service.component.ComponentContext;

import com.ibm.websphere.ssl.JSSEHelper;
import com.ibm.websphere.ssl.SSLException;

/**
 * This class contains code that should only run when the ssl feature is enabled.
 */
//as documentation only at this point:
//@Component(pid="com.ibm.ws.jca.sslHelper")
public class SSLHelperImpl implements SSLHelper {

    /**
     * DS method to activate this component.
     * Best practice: this should be a protected method, not public or private
     * 
     * @param context for this component instance
     */
    protected void activate(ComponentContext context) {}

    /**
     * DS method to deactivate this component.
     * Best practice: this should be a protected method, not public or private
     * 
     * @param context for this component instance
     */
    protected void deactivate(ComponentContext context) {}

    /**
     * @see com.ibm.ws.jca.internal.SSLHelper#getSSLSocketFactory(java.lang.Object)
     */
    @Override
    public SSLSocketFactory getSSLSocketFactory(final String sslConfigID) throws Exception {
        final JSSEHelper jsseHelper = JSSEHelper.getInstance();
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<SSLSocketFactory>() {
                @Override
                public SSLSocketFactory run() throws SSLException {
                    Properties props = jsseHelper.getProperties(sslConfigID);
                    Map<String, Object> connectionInfo = Collections.emptyMap();
                    return jsseHelper.getSSLSocketFactory(connectionInfo, props);
                }
            });
        } catch (PrivilegedActionException e) {
            if (e.getCause() instanceof SSLException)
                throw (SSLException) e.getCause();
            else
                throw e;
        }
    }
}
