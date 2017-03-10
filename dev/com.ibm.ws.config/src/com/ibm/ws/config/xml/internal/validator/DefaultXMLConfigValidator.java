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

import com.ibm.ws.config.xml.internal.ServerConfiguration;

/**
 * Default configuration validator for Liberty.
 * <p>
 * This validator does not perform any validation. It is not used when Liberty
 * is embedded within another product.
 * 
 * @since V8.5 feature XXXXXX.
 */
public class DefaultXMLConfigValidator implements XMLConfigValidator {

    /**
     * Class constructor.
     */
    protected DefaultXMLConfigValidator() {}

    /** {@inheritDoc} */
    @Override
    public InputStream validateResource(InputStream configDocInputStream, String docLocation) {
        return configDocInputStream;
    }

    /** {@inheritDoc} */
    @Override
    public void validateConfig(ServerConfiguration configuration) {}
}
