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

import java.util.Arrays;
import java.util.Dictionary;
import java.util.Vector;

import org.osgi.service.cm.ConfigurationException;

public class SingletonMetaTypeTest extends ManagedTest {

    public SingletonMetaTypeTest(String name) {
        super(name);
    }

    @Override
    public void configurationUpdated(Dictionary properties) throws ConfigurationException {
        // all picked up directly from metatype
        assertEquals("present", Boolean.TRUE, properties.get("present"));
        assertArrayEquals("int array", new int[] { 1, 2, 3 }, (int[]) properties.get("intColl"));
        assertEquals("long vector", new Vector<Long>(Arrays.asList(4l, 5l, 6l, 7l)), properties.get("longColl"));

        assertEquals("lastname", "Simpson", properties.get("lastname"));
        assertEquals("spouse", "Marge Simpson", properties.get("spouse"));
    }

}
