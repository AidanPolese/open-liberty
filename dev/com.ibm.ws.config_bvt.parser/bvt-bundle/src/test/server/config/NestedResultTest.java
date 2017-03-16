/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested of
 * its trade secrets, irrespective of what has been deposited with the U.S.
 * Copyright Office.
 */
package test.server.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Dictionary;
import java.util.Map;

import org.osgi.service.cm.ConfigurationException;

public class NestedResultTest extends ManagedFactoryTest {

    private Map<String, Map<String, Object>> expectedProperties;

    public NestedResultTest(String name, int count) {
        super(name, count);
    }

    public void setExpectedProperties(Map<String, Map<String, Object>> expectedProperties) {
        this.expectedProperties = expectedProperties;
    }

    @Override
    public void configurationUpdated(String pid, Dictionary properties) throws ConfigurationException {
        String name = (String) properties.get("name");

        Map<String, Object> expectedProps = expectedProperties.get(name);
        assertNotNull("Unexpected name: " + name, expectedProps);
        for (Map.Entry<String, Object> entry : expectedProps.entrySet()) {
            assertEquals("property " + entry.getKey() + " mismatch", entry.getValue(), properties.get(entry.getKey()));
        }
    }

}
