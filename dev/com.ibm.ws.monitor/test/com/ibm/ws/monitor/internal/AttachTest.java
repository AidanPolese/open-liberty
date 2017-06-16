/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
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
