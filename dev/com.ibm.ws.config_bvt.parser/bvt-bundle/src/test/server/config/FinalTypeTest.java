/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package test.server.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Dictionary;

/**
 *
 */
public class FinalTypeTest extends ManagedTest {

    /**
     * @param name
     */
    public FinalTypeTest(String name) {
        super(name);
    }

    /** {@inheritDoc} */
    @Override
    public void configurationUpdated(Dictionary properties) throws Exception {
        assertEquals("name", "someName", properties.get("name"));
        assertNull("finalField1 should be null but is " + properties.get("finalField1"), properties.get("finalField1"));
        String configDir = System.getProperty("user.variable");
        assertEquals("finalField2 should be an expanded variable", configDir, properties.get("finalField2"));
        assertEquals("finalField3 should be a default value", "someDefault", properties.get("finalField3"));
    }

}
