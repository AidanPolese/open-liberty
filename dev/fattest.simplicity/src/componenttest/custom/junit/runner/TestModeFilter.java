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

import java.util.Locale;

import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;

import com.ibm.websphere.simplicity.log.Log;

import componenttest.custom.junit.runner.Mode.TestMode;

/**
 *
 */
public class TestModeFilter extends Filter {

    public static final String FAT_MODE_PROPERTY_NAME = "fat.test.mode";

    //Run levels for various FAT modes
    public static final TestMode FRAMEWORK_TEST_MODE;

    static {
        //get and set the framework mode, default to LITE
        String modeProperty = System.getProperty(FAT_MODE_PROPERTY_NAME);
        FRAMEWORK_TEST_MODE = modeProperty != null ? TestMode.valueOf(modeProperty.toUpperCase(Locale.ROOT)) : TestMode.LITE;

        Log.info(TestModeFilter.class, "<clinit>", "System property: " + FAT_MODE_PROPERTY_NAME + " is " + modeProperty + " running in test mode " + FRAMEWORK_TEST_MODE);
    }

    /** {@inheritDoc} */
    @Override
    public String describe() {
        return null;
    }

    /**
     * Like {@link Description#getTestClass}, but without initializing the class.
     */
    private Class<?> getTestClass(Description desc) {
        try {
            return Class.forName(desc.getClassName(), false, getClass().getClassLoader());
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean shouldRun(Description desc) {
        Mode mode;
        //check for a method level annotation first
        mode = desc.getAnnotation(Mode.class);
        //method level annotations supercede any class level annotation
        if (mode == null) {
            //there was no method level annotation
            //check for a test class level annotation
            mode = getTestClass(desc).getAnnotation(Mode.class);
        }
        //default to lite for unannotated tests
        TestMode testMode = TestMode.LITE;
        //if the test was annotated, get the mode from the annotation
        if (mode != null) {
            testMode = mode.value();
        }

        //compare the current run level of the framework to the
        //test annotated run level
        //exclude the test if the current run mode is lower than the test's
        //e.g. for a test annotated lite
        // FRAMEWORK_TEST_MODE | test annotation level | comparison | result | filter result
        // full                | lite                  | full > lite | > 0 | true, full should run lite tests
        // lite                | lite | lite = lite | 0 | true, lite should run lite tests
        // rapid               | lite | rapid < lite | < 0 | false, rapid should not run lite tests
        if (FRAMEWORK_TEST_MODE.compareTo(testMode) < 0) {
            Log.debug(getClass(), "Removing test " + desc.getMethodName() + " with mode " + testMode + " from list to run, because not valid for current mode "
                                  + FRAMEWORK_TEST_MODE);
            return false;
        } else
            return true;
    }

}
