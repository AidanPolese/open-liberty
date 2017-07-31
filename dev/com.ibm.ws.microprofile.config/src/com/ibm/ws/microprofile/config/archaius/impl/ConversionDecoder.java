/*
* IBM Confidential
*
* OCO Source Materials
*
* Copyright IBM Corp. 2017
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.ws.microprofile.config.archaius.impl;

import java.lang.reflect.Type;
import java.util.Map;

import org.eclipse.microprofile.config.spi.Converter;

import com.ibm.ws.microprofile.config.impl.ConversionManager;
import com.netflix.archaius.api.Decoder;

/**
 *
 */
public class ConversionDecoder extends ConversionManager implements Decoder {

    /**
     * Constructor
     * 
     * @param converters
     */
    public ConversionDecoder(Map<Type, Converter<?>> converters) {
        super(converters);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T decode(Class<T> type, String encoded) {
        return (T) convert(encoded, type);
    }

}
