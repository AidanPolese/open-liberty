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

public class ConverterC implements Converter<ClassC> {

    /** {@inheritDoc} */
    @Override
    public ClassC convert(String value) {
        return (ClassC) ClassC.newClassC(value);
    }

}
