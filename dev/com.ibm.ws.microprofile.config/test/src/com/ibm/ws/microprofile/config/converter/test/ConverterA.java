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
package com.ibm.ws.microprofile.config.converter.test;

import org.eclipse.microprofile.config.spi.Converter;

public class ConverterA<T extends ClassA> implements Converter<T> {

    private int conversionCount = 0;

    /** {@inheritDoc} */
    @Override
    public T convert(String value) {
        conversionCount++;
        return (T) ClassB.newClassB(value);
    }

    public int getConversionCount() {
        return conversionCount;
    }

}
