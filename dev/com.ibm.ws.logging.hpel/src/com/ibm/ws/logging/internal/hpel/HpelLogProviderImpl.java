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
package com.ibm.ws.logging.internal.hpel;

import java.io.File;
import java.util.Map;

import com.ibm.websphere.ras.TrConfigurator;
import com.ibm.ws.ffdc.FFDCConfigurator;
import com.ibm.ws.logging.hpel.config.HpelConfigurator;
import com.ibm.ws.logging.internal.impl.LoggingConstants;
import com.ibm.wsspi.logging.TextFileOutputStreamFactory;
import com.ibm.wsspi.logprovider.LogProvider;

/**
 *
 */
public class HpelLogProviderImpl implements LogProvider {

    /**
     * {@inheritDoc}
     */
    @Override
    public void configure(Map<String, String> config, File logLocation, TextFileOutputStreamFactory factory) {
        // Use HPEL as a TrService provider if it was not explicitly set.
        if (!config.containsKey(config.get(LoggingConstants.PROP_TRACE_DELEGATE))) {
            config.put(LoggingConstants.PROP_TRACE_DELEGATE, HpelBaseTraceService.class.getName());
        }
        HpelTraceServiceConfig loggingConfig = new HpelTraceServiceConfig(config, logLocation, factory);

        TrConfigurator.init(loggingConfig);
        FFDCConfigurator.init(loggingConfig);
        HpelConfigurator.init(loggingConfig);
    }

    @Override
    public void stop() {
        // FFDC uses Tr, it must be stopped first
        FFDCConfigurator.stop();
        TrConfigurator.stop();
    }
}
