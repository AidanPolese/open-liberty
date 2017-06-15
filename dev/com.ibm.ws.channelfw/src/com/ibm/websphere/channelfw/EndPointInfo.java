/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.websphere.channelfw;

import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;

import com.ibm.websphere.endpoint.EndPointInfoMBean;
import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.channelfw.internal.ChannelFrameworkConstants;

/**
 * Temporary version of the WAS runtime EndPointMgr version.
 */
public class EndPointInfo extends StandardMBean implements EndPointInfoMBean {
    /** Trace service */
    private static final TraceComponent tc =
                    Tr.register(EndPointInfo.class,
                                ChannelFrameworkConstants.BASE_TRACE_NAME,
                                ChannelFrameworkConstants.BASE_BUNDLE);

    /** Name of this endpoint */
    private final String name;
    /** Host for this endpoint */
    private final String host;
    /** Port for this endpoint */
    private final int port;

    /**
     * Constructor.
     * 
     * @param name
     * @param host
     * @param port
     * @throws IllegalArgumentException if name or host is empty
     */
    public EndPointInfo(String name, String host, int port) throws NotCompliantMBeanException {
        super(EndPointInfoMBean.class, false);
        if (null == name || 0 == name.length()) {
            throw new IllegalArgumentException("Invalid name: " + name);
        }
        if (null == host || 0 == host.length()) {
            throw new IllegalArgumentException("Invalid host: " + host);
        }
        this.name = name;
        this.host = host;
        this.port = port;
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "Created: " + this);
        }
    }

    /**
     * Query the name of this endpoint.
     * 
     * @return String
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Query the host assigned to this endpoint.
     * 
     * @return String
     */
    @Override
    public String getHost() {
        return this.host;
    }

    /**
     * Query the port assigned to this endpoint.
     * 
     * @return int
     */
    @Override
    public int getPort() {
        return this.port;
    }

    /*
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("EndPoint ").append(getName()).append('=');
        sb.append(getHost()).append(':').append(getPort());
        return sb.toString();
    }
}
