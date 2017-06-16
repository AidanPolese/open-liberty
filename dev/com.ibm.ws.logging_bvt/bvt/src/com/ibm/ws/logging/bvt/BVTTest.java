/*******************************************************************************
 * Copyright (c) 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.logging.bvt;

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;

public class BVTTest {
    @Test
    public void testConfiguredLogFiles() {
        String serverRoot = System.getProperty("server.root");

        File traceLogFile = new File(serverRoot, "logs/traceLog.log");
        Assert.assertTrue(traceLogFile.exists());

        File loggingTraceFile = new File(serverRoot, "logs/loggingTrace.log");
        Assert.assertTrue(loggingTraceFile.exists());
    }
}
