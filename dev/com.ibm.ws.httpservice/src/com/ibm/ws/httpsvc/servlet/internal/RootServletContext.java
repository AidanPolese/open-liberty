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
package com.ibm.ws.httpsvc.servlet.internal;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;

/**
 *
 */
public class RootServletContext extends ServletContextImpl {
    private final Map<String, String> myInitParams = new HashMap<String, String>();

    public RootServletContext() {}

    public void init(Bundle bundle, Dictionary<?, ?> initProps) {
        super.init(bundle, this, null);

        // convert the possible initparams into our internal storage
        if (null != initProps) {
            Enumeration<?> keys = initProps.keys();
            while (keys.hasMoreElements()) {
                Object key = keys.nextElement();
                Object value = initProps.get(key);

                if (key instanceof String && value instanceof String) {
                    this.myInitParams.put((String) key, (String) value);
                }
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public int getMajorVersion() {
        return 2;
    }

    /** {@inheritDoc} */
    @Override
    public int getMinorVersion() {
        return 5;
    }

    /** {@inheritDoc} */
    @Override
    public String getServerInfo() {
        return "WebSphere HttpService";
    }

    @Override
    public String getInitParameter(String name) {
        return myInitParams.get(name);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return Collections.enumeration(myInitParams.keySet());
    }

    @Override
    public URL getResource(String path) {
        throw new UnsupportedOperationException("Unsupported on root context");
    }

    @Override
    public String getMimeType(String file) {
        throw new UnsupportedOperationException("Unsupported on root context");
    }

    @Override
    public boolean handleSecurity(HttpServletRequest req, HttpServletResponse res)
                    throws IOException {
        throw new UnsupportedOperationException("Unsupported on root context");
    }
}
