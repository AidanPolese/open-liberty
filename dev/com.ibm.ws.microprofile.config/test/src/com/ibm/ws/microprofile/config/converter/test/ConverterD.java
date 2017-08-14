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
package src.com.ibm.ws.microprofile.config.converter.test;

import org.eclipse.microprofile.config.spi.Converter;

/**
 * A converter to convert to ClassD. If the input string is "", a null should be returned.
 */
public class ConverterD implements Converter<ClassD> {

    private final int conversionCount = 0;

    /** {@inheritDoc} */
    @Override
    public ClassD convert(String value) {
        if ("".equals(value)) {
            return null;
        }
        return new ClassD(value);
    }

    public int getConversionCount() {
        return conversionCount;
    }

}
