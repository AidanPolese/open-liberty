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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Dictionary;

import org.osgi.service.cm.ConfigurationException;

public class FactorySimpleTest extends ManagedFactoryTest {

    public FactorySimpleTest(String name, int count) {
        super(name, count);
    }

    @Override
    public void configurationUpdated(String pid, Dictionary properties) throws ConfigurationException {
        String id = (String) properties.get("id");
        if ("serverInstance".equals(id)) {
            assertEquals("simple attr", "foo", properties.get("simpleAttr"));
            assertArrayEquals("collection attr", new String[] { "Lisa", "Simpson" }, (String[]) properties.get("collAttr"));
        } else {
            throw new RuntimeException("Invalid instance id: " + id);
        }
    }

}
