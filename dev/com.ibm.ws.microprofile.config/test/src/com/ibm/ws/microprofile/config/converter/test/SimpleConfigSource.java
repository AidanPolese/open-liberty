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

import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.microprofile.config.spi.ConfigSource;

/**
 * A config source backed by a map
 */
public class SimpleConfigSource extends ConcurrentHashMap<String, String> implements ConfigSource {

    private static final long serialVersionUID = 1L;

    /** {@inheritDoc} */
    @Override
    public ConcurrentHashMap<String, String> getProperties() {
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public String getValue(String propertyName) {
        return this.get(propertyName);
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return this.getClass().getName();
    }

}
