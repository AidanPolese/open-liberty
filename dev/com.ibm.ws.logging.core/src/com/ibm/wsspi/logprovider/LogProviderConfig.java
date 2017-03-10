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
package com.ibm.wsspi.logprovider;

import java.io.File;
import java.util.Map;

import com.ibm.wsspi.logging.TextFileOutputStreamFactory;

/**
 * This provides a representation of the configured LogProvider to the
 * stable/statically-accessed elements of the logging system.
 */
public interface LogProviderConfig {

    /** @return the configured log directory */
    File getLogDirectory();

    /** @return the configured/active TrService delegate instance */
    TrService getTrDelegate();

    /** @return the configured/active FFDCService delegate instance */
    FFDCFilterService getFfdcDelegate();

    /** @return the Factory that should be used to create text file output streams */
    TextFileOutputStreamFactory getTextFileOutputStreamFactory();

    /** @return the configured/active trace string */
    String getTraceString();

    /** @return the configured maximum number of log files */
    int getMaxFiles();

    /**
     * This is how the logging system will push dynamically received
     * updates down to the configured/active log provider.
     * 
     * @param newConfig A Map of String keys to object values: values might
     *            not be strings, as interaction with config admin will pre-convert
     *            the value into an int or a boolean or..
     */
    void update(Map<String, Object> newConfig);
}
