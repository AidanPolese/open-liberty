/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package test.server.config.nested;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Dictionary;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

import test.server.BaseTestRunner;
import test.server.config.dynamic.ConfigWriter;
import test.server.config.dynamic.FactoryTest;

/**
 *
 */
public class AttributeCopyTestRunner extends BaseTestRunner {

    String parentName = "com.ibm.ws.config.attributeCopy";
    String childName = "com.ibm.ws.config.attributeCopy.child";
    String child2Name = "com.ibm.ws.config.attributeCopy.grandchild";
    String subTypeName = "com.ibm.ws.config.attributeCopy.subType";

    private FactoryTest parent;
    private FactoryTest child;
    private FactoryTest child2;
    private FactoryTest subType;

    private ConfigurationAdmin configAdmin;

    @Override
    protected void activate(ComponentContext context) throws Exception {
        super.activate(context);
        registerServlet("/attribute-copy", new TestDynamicConfigServlet(), null, null);

        this.parent = new FactoryTest(parentName);
        this.child = new FactoryTest(childName);
        this.child2 = new FactoryTest(child2Name);
        this.subType = new FactoryTest(subTypeName);

        addTest(parent);
        addTest(child);
        addTest(child2);
        addTest(subType);

        BundleContext bundleContext = context.getBundleContext();
        ServiceReference<?> ref = bundleContext.getServiceReference(ConfigurationAdmin.class.getName());
        this.configAdmin = (ConfigurationAdmin) bundleContext.getService(ref);
    }

    public void testSimpleAttributeCopy() throws Exception {
        Dictionary<String, Object> dictionary;
        ConfigWriter writer;

        try {
            // Reset to avoid contamination
            resetFactoryTests();

            /*
             * <copytest id="a" name="Bob"/>
             */

            // *** STEP 1 ***
            // Write initial configuration
            writer = readConfiguration();
            writer.addConfig("<copyTest id=\"a\" name=\"Bob\"/>");
            writeConfiguration(writer);

            // wait for the initial configuration to be injected.
            dictionary = parent.waitForUpdate("a");
            assertEquals("id should be \"a\"",
                         "a", dictionary.get("id"));
            assertEquals("Bob", dictionary.get("copiedName"));

        } finally {
            // Clean up
            writer = readConfiguration();
            writer.deleteConfig("copyTest", "a", false);
            writeConfiguration(writer);
        }
    }

    public void testSimpleAttributeCopyArray() throws Exception {
        Dictionary<String, Object> dictionary;
        ConfigWriter writer;

        try {
            // Reset to avoid contamination
            resetFactoryTests();

            /*
             * <copytest id="a" name="Bob"/>
             */

            // *** STEP 1 ***
            // Write initial configuration
            writer = readConfiguration();
            writer.addConfig("<copyTest id=\"a\" hobbies=\"ice fishing, woodworking\"/>");
            writeConfiguration(writer);

            // wait for the initial configuration to be injected.
            dictionary = parent.waitForUpdate("a");
            assertEquals("id should be \"a\"",
                         "a", dictionary.get("id"));
            String[] hobbies = (String[]) dictionary.get("hobbies");
            assertTrue("copiedHobbies should be the same as hobbies", Arrays.equals(hobbies, (String[]) dictionary.get("copiedHobbies")));

        } finally {
            // Clean up
            writer = readConfiguration();
            writer.deleteConfig("copyTest", "a", false);
            writeConfiguration(writer);
        }
    }

    public void testNestedAttributeCopy() throws Exception {
        Dictionary<String, Object> dictionary;
        ConfigWriter writer;

        try {
            // Reset to avoid contamination
            resetFactoryTests();

            /*
             * <copyTest id="a" name="Bob">
             * <copyChild id="child.a" value="Eleanor"/>
             * </copyTest>
             */

            // *** STEP 1 ***
            // Write initial configuration
            writer = readConfiguration();
            writer.addConfig("<copyTest id=\"a\" name=\"Bob\">" +
                             "<child id=\"child.a\" value=\"Eleanor\"/>" +
                             "</copyTest>");
            writeConfiguration(writer);

            // wait for the initial configuration to be injected.
            dictionary = parent.waitForUpdate("a");
            assertEquals("id should be \"a\"",
                         "a", dictionary.get("id"));
            assertEquals("Bob", dictionary.get("copiedName"));
            assertEquals("Eleanor", dictionary.get("copiedChild"));

        } finally {
            // Clean up
            writer = readConfiguration();
            writer.deleteConfig("copyTest", "a", false);
            writeConfiguration(writer);
        }
    }

    public void testDoubleNestedAttributeCopy() throws Exception {
        Dictionary<String, Object> dictionary;
        ConfigWriter writer;

        try {
            // Reset to avoid contamination
            resetFactoryTests();

            /*
             * <copyTest id="a" name="Bob">
             * <child id="child.a" value="Eleanor">
             * <grandchild id="grandchild.a" value="Ringo"/>
             * </child>
             * </copyTest>
             */

            // *** STEP 1 ***
            // Write initial configuration
            writer = readConfiguration();
            writer.addConfig("<copyTest id=\"a\" name=\"Bob\">" +
                             "<child id=\"child.a\" value=\"Eleanor\">" +
                             "<grandchild id=\"grandchild.a\" value=\"Ringo\"/>" +
                             "</child>" +
                             "</copyTest>");
            writeConfiguration(writer);

            // wait for the initial configuration to be injected.
            dictionary = parent.waitForUpdate("a");
            assertEquals("id should be \"a\"",
                         "a", dictionary.get("id"));
            assertEquals("Bob", dictionary.get("copiedName"));
            assertEquals("Eleanor", dictionary.get("copiedChild"));
            assertEquals("Ringo", dictionary.get("copiedGrandchild"));

        } finally {
            // Clean up
            writer = readConfiguration();
            writer.deleteConfig("copyTest", "a", false);
            writeConfiguration(writer);
        }
    }

    public void testExtendsAttributeCopy() throws Exception {
        Dictionary<String, Object> dictionary;
        ConfigWriter writer;

        try {
            // Reset to avoid contamination
            resetFactoryTests();

            /*
             * <acSubType id="a"/>
             */

            // *** STEP 1 ***
            // Write initial configuration
            writer = readConfiguration();
            writer.addConfig("<acSubType id=\"a\"/>");
            writeConfiguration(writer);

            // wait for the initial configuration to be injected.
            dictionary = subType.waitForUpdate("a");
            assertEquals("id should be \"a\"",
                         "a", dictionary.get("id"));
            assertEquals("subType value", dictionary.get("value"));
            assertEquals("subType value", dictionary.get("valueCopy"));
            assertEquals("supertype name", dictionary.get("name"));
            assertEquals("supertype name", dictionary.get("nameCopy"));
            assertEquals("supertype name", dictionary.get("superNameCopy"));

        } finally {
            // Clean up
            writer = readConfiguration();
            writer.deleteConfig("acSubType", "a", false);
            writeConfiguration(writer);
        }
    }

    /**
     * Reset factories to avoid test contamination
     */
    private void resetFactoryTests() {
        parent.reset();
        child.reset();
        child2.reset();
        subType.reset();
    }
}
