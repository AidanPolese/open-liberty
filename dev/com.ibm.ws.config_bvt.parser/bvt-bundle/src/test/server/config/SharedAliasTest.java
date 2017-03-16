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

public class SharedAliasTest extends ManagedTest {

    private final String firstName;
    private final String lastName;

    public SharedAliasTest(String name, String firstName, String lastName) {
        super(name);
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public void configurationUpdated(Dictionary properties) throws ConfigurationException {
        assertEquals("kids", "5", properties.get("kids"));
        assertEquals("lastName", lastName, properties.get("lastName"));
        assertEquals("firstName", firstName, properties.get("firstName"));
        assertEquals("fullName", firstName + " " + lastName, properties.get("fullName"));
    }

}
