/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */

package com.ibm.ws.logging.internal;

import java.io.File;

import com.ibm.wsspi.logprovider.FFDCFilterService;
import com.ibm.wsspi.logprovider.LogProviderConfig;

/**
 * Disabled FFDCFilterService: all methods should be empty. If was.ffdc.enabled
 * is set to false via properties, this delegate will be used instead of the
 * BasicFFDCService, disabling all FFDC output.
 */
public class DisabledFFDCService implements FFDCFilterService {
    /**
     * @see com.ibm.wsspi.logprovider.FFDCFilterService#processException(java.lang.Throwable, java.lang.String, java.lang.String)
     */
    public void processException(Throwable th, String sourceId, String probeId) {}

    /**
     * @see com.ibm.wsspi.logprovider.FFDCFilterService#processException(java.lang.Throwable, java.lang.String, java.lang.String, java.lang.Object)
     */
    public void processException(Throwable th, String sourceId, String probeId, Object callerThis) {}

    /**
     * @see com.ibm.wsspi.logprovider.FFDCFilterService#processException(java.lang.Throwable, java.lang.String, java.lang.String, java.lang.Object[])
     */
    public void processException(Throwable th, String sourceId, String probeId, Object[] objectArray) {}

    /**
     * @see com.ibm.wsspi.logprovider.FFDCFilterService#processException(java.lang.Throwable, java.lang.String, java.lang.String, java.lang.Object, java.lang.Object[])
     */
    public void processException(Throwable th, String sourceId, String probeId, Object callerThis, Object[] objectArray) {}

    @Override
    public void stop() {}

    @Override
    public void init(LogProviderConfig config) {}

    @Override
    public void update(LogProviderConfig config) {}

    @Override
    public void rollLogs() {}

    @Override
    public File getFFDCLogLocation() {
        return new File(".");
    }
}
