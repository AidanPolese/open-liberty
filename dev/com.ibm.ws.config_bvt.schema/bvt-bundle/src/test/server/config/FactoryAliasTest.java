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

import java.util.Dictionary;

import org.osgi.service.cm.ConfigurationException;

public class FactoryAliasTest extends ManagedFactoryTest {

    public FactoryAliasTest(String name, int count) {
        super(name, count);
    }

    @Override
    public void configurationUpdated(String pid, Dictionary properties) throws ConfigurationException {
        String id = (String) properties.get("id");

        assertEquals("kids", new Integer(3), properties.get("kids"));

        if ("simpsons".equals(id)) {
            assertEquals("lastName", "Simpson", properties.get("lastName"));
            assertEquals("firstName", "Homer", properties.get("firstName"));
            assertEquals("fullName", "Homer Simpson", properties.get("fullName"));
        } else if ("griffins".equals(id)) {
            assertEquals("lastName", "Griffin", properties.get("lastName"));
            assertEquals("firstName", "Peter", properties.get("firstName"));
            assertEquals("fullName", "Peter Griffin", properties.get("fullName"));
        } else {
            throw new RuntimeException("Invalid instance id: " + id);
        }
    }

}
