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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ManagedService;

/**
 * Test singleton pids
 */
public class NestedSingletonTest extends ManagedTest {

    private final ConfigurationAdmin configAdmin;
    private final String nestedElement;
    private Map<String, Map<String, Object>> expectedProperties;

    public NestedSingletonTest(String name, String nestedElement, ConfigurationAdmin configAdmin) {
        super(name);
        this.configAdmin = configAdmin;
        this.nestedElement = nestedElement;
    }

    public void setExpectedProperties(Map<String, Map<String, Object>> expectedProperties) {
        this.expectedProperties = expectedProperties;
    }

    public static Map<String, Map<String, Object>> getExpectedProperties() {
        Map<String, Object> map1 = new HashMap<String, Object>();
        map1.put("pass", "9999");
        map1.put("fail", "7777");

        Map<String, Object> map2 = new HashMap<String, Object>();
        map2.put("pass", "100");
        map2.put("fail", "0");

        Map<String, Map<String, Object>> expectedProperties = new HashMap<String, Map<String, Object>>();
        expectedProperties.put("one", map1);
        expectedProperties.put("two", map2);

        return expectedProperties;
    }

    public static Map<String, Map<String, Object>> getMetatypeExpectedProperties() {
        Map<String, Object> map1 = new HashMap<String, Object>();
        map1.put("pass", Integer.valueOf(9999));
        map1.put("fail", Integer.valueOf(7777));

        Map<String, Object> map2 = new HashMap<String, Object>();
        map2.put("pass", Integer.valueOf(100));
        map2.put("fail", Integer.valueOf(0));

        Map<String, Map<String, Object>> expectedProperties = new Hashtable<String, Map<String, Object>>();
        expectedProperties.put("one", map1);
        expectedProperties.put("two", map2);

        return expectedProperties;
    }

    @Override
    public String[] getServiceClasses() {
        return new String[] { ManagedService.class.getName() };
    }

    @Override
    public void configurationUpdated(Dictionary properties) throws Exception {
        assertEquals("singletonNested1", (properties.get("name")));

        String[] testResults = (String[]) properties.get(nestedElement);
        assertNotNull(testResults);
        assertEquals(2, testResults.length);

        Map<String, Map<String, Object>> expectedProperties = new HashMap<String, Map<String, Object>>(this.expectedProperties);
        for (int i = 0; i < testResults.length; i++) {
            Configuration config = configAdmin.getConfiguration(testResults[i]);
            Dictionary prop = config.getProperties();
            String innerName = (String) prop.get("name");
            Map<String, Object> expectedProps = expectedProperties.remove(innerName);
            assertNotNull("Unexpected name: " + innerName, expectedProps);
            for (Map.Entry<String, Object> entry : expectedProps.entrySet()) {
                assertEquals("property " + entry.getKey() + " mismatch", entry.getValue(), prop.get(entry.getKey()));
            }
        }

        assertEquals(0, expectedProperties.size());
    }
}
