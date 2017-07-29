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

import javax.annotation.Priority;

import org.eclipse.microprofile.config.spi.Converter;

@Priority(101)
public class StringConverter101b implements Converter<String> {

    /** {@inheritDoc} */
    @Override
    public String convert(String value) throws IllegalArgumentException {
        return "101b=" + value;
    }

}
