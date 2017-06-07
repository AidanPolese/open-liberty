/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.kernel.boot.internal.framework;

import java.io.File;
import java.util.Map;

import com.ibm.wsspi.logprovider.LogProvider;

/**
 *
 */
public class BasicLogProvider implements LogProvider {
    @Override
    public void configure(Map<String, String> config,
                          File logLocation,
                          com.ibm.wsspi.logging.TextFileOutputStreamFactory factory) {}

    @Override
    public void stop() {}
}
