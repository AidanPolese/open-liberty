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
package com.ibm.ws.jaxws.jmx.internal;

import javax.management.MBeanServer;

import org.apache.cxf.Bus;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.jaxws.bus.LibertyApplicationBusListener;

/**
 * 
 */
public class JMXBusInitializer implements LibertyApplicationBusListener {

    private static final TraceComponent tc = Tr.register(JMXBusInitializer.class);

    private final MBeanServer mbs;

    public JMXBusInitializer(MBeanServer mbs) {
        this.mbs = mbs;
    }

    @Override
    public void preInit(Bus bus) {

        if (bus == null) {
            return;
        }

        /* enable CXF JMX */
        if (this.mbs != null) {
            bus.setProperty("bus.jmx.enabled", "true");
            bus.setProperty("bus.jmx.usePlatformMBeanServer", "true");
            bus.setProperty("bus.jmx.createMBServerConnectorFactory", "false");
            bus.setExtension(this.mbs, MBeanServer.class);
        } else {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "Unable to get MBeanServer reference from PlatformMBeanService service, CXF JMX will be disabled");
            }
            bus.setProperty("bus.jmx.enabled", "false");
        }
    }

    @Override
    public void initComplete(Bus bus) {}

    @Override
    public void preShutdown(Bus bus) {}

    @Override
    public void postShutdown(Bus bus) {}

}
