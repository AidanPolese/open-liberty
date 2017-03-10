/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */

package com.ibm.ws.config.xml.internal.validator;

import java.io.InputStream;

import com.ibm.websphere.config.ConfigValidationException;
import com.ibm.ws.config.xml.internal.ServerConfiguration;

/**
 * Interface that defines the methods that configuration validators implement.
 * 
 * @since V8.5 feature XXXXXX.
 */
public interface XMLConfigValidator {

    /**
     * Validates a configuration document.
     * 
     * @param configDocInputStream An input stream that contains the configuration
     *            document.
     * @param docLocation The location of the configuration document.
     * 
     * @return An input stream that can be used to read the configuration document.
     *         (If the validator does not read the input <code>InputStream</code>, it can
     *         merely return this stream. Otherwise, it must return an unused copy of the
     *         input <code>InputStream</code>.
     * @throws ConfigValidationException
     */
    public InputStream validateResource(InputStream configDocInputStream, String docLocation) throws ConfigValidationException;

    /**
     * Validates the entire configuration.
     * 
     * @param configuration The configuration to be validated.
     * @throws ConfigValidationException
     */
    public void validateConfig(ServerConfiguration configuration) throws ConfigValidationException;
}
