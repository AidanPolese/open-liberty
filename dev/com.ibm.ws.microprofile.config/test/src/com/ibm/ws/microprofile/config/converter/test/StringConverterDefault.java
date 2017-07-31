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

//no @Priority, should default to 100
public class StringConverterDefault implements Converter<String> {

    /** {@inheritDoc} */
    @Override
    public String convert(String value) throws IllegalArgumentException {
        return "100default=" + value;
    }

}
