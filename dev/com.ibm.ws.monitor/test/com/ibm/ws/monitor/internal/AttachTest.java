package com.ibm.ws.monitor.internal;

import static org.junit.Assert.assertNotNull;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import org.junit.Test;

public class AttachTest {

    @Test
    public void testGetRuntimeMXBean() {
        RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
        assertNotNull(runtimeMxBean);

        System.out.println("runtimeMxBean = " + runtimeMxBean);
        System.out.println("runtimeMxBean.getName() = " + runtimeMxBean.getName());
    }
}
