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
package componenttest.custom.junit.runner;

import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;

import com.ibm.websphere.simplicity.log.Log;

/**
 *
 */
public class TestNameFilter extends Filter {

    private static final String FAT_TEST_CLASS;
    private static final String FAT_TEST_METHOD;

    static {
        //these properties allow shortcutting to a single test class and/or method
        FAT_TEST_CLASS = System.getProperty("fat.test.class.name");
        FAT_TEST_METHOD = System.getProperty("fat.test.method.name");

        if (FAT_TEST_CLASS != null)
            Log.info(TestNameFilter.class, "<clinit>", "Running only test class with name: " + FAT_TEST_CLASS);
        if (FAT_TEST_METHOD != null)
            Log.info(TestNameFilter.class, "<clinit>", "Running only test with method name: " + FAT_TEST_METHOD);
    }

    @Override
    public String describe() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean shouldRun(Description arg0) {
        if (FAT_TEST_CLASS != null && !arg0.getClassName().equals(FAT_TEST_CLASS)) {
            return false;
        }
        if (FAT_TEST_METHOD != null && !arg0.getMethodName().equals(FAT_TEST_METHOD)) {
            return false;
        }
        return true;
    }

}
