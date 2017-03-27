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

public class SingletonAliasTest extends ManagedTest {

    public SingletonAliasTest(String name) {
        super(name);
    }

    @Override
    public void configurationUpdated(Dictionary properties) throws ConfigurationException {
        assertEquals("kids", new Integer(2), properties.get("kids"));
        assertEquals("lastName", "Smith", properties.get("lastName"));
        assertEquals("firstName", "Stan", properties.get("firstName"));
        assertEquals("fullName", "Stan Smith", properties.get("fullName"));
        assertEquals("others", "Roger Smith, Klaus Smith", properties.get("others"));
    }

}
