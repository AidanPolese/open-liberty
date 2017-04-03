package componenttest.custom.junit.runner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import com.ibm.websphere.simplicity.log.Log;

import componenttest.annotation.FeatureRequiresMinimumJavaLevel;
import componenttest.annotation.MaximumJavaLevel;
import componenttest.annotation.MinimumJavaLevel;

public class JavaLevelFilter extends Filter {

    public static final String FEATURE_UNDER_TEST;
    public static final double JAVA_VERSION;

    private static boolean hasRealTests = false;

    static {
        FEATURE_UNDER_TEST = System.getProperty(FeatureFilter.FEATURE_UNDER_TEST_PROPERTY_NAME);
        Log.info(JavaLevelFilter.class, "<clinit>", "System property: " + FeatureFilter.FEATURE_UNDER_TEST_PROPERTY_NAME + " is " + FEATURE_UNDER_TEST);
        String specVersion = System.getProperty("java.specification.version");
        Log.info(JavaLevelFilter.class, "<clinit>", "System property java.specification.version: " + specVersion);
        // Convert to a double (by stripping any non-numeric characters)
        if (specVersion != null) {
            Matcher matcher = Pattern.compile("([0-9][0-9]*\\.[0-9][0-9]*)").matcher(specVersion);
            boolean hasNumber = matcher.find();
            if (hasNumber) {
                JAVA_VERSION = Double.parseDouble(matcher.group(1));
                Log.info(JavaLevelFilter.class, "<clinit>", "Parsed java version: " + JAVA_VERSION);

            } else {
                JAVA_VERSION = 0;
                Log.info(JavaLevelFilter.class, "<clinit>", "Could not parse a java version, so assuming " + JAVA_VERSION);

            }
        } else {
            JAVA_VERSION = 0;
            Log.info(JavaLevelFilter.class, "<clinit>", "Could not find a system property for java.specification.version, so assuming " + JAVA_VERSION);

        }
    }

    private static Class<?> getMyClass() {
        return JavaLevelFilter.class;
    }

    /** {@inheritDoc} */
    @Override
    public String describe() {
        return null;
    }

    /**
     * Like {@link Description#getTestClass}, but without initializing the class.
     */
    private static Class<?> getTestClass(Description desc) {
        try {
            return Class.forName(desc.getClassName(), false, getMyClass().getClassLoader());
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean shouldRun(Description desc) {
        boolean shouldRun = shouldRunTest(desc);
        if (shouldRun) {
            boolean isSyntheticTest = AlwaysPassesTest.ALWAYS_PASSES_METHOD_NAME.equals(desc.getMethodName());
            if (isSyntheticTest) {
                // If there are real tests, don't run synthetic test.
                // If there are no more tests, do run the synthetic test.
                return !hasRealTests;
            } else {
                hasRealTests = true;
            }
        }
        return shouldRun;
    }

    public static boolean hasRealTests() {
        return hasRealTests;
    }

    public static boolean shouldRunTest(Description desc) {
        MaximumJavaLevel maximumJavaLevelAnnotation = desc.getAnnotation(MaximumJavaLevel.class);
        if (maximumJavaLevelAnnotation == null) {
            //there was no method level annotation
            //check for a test class level annotation
            maximumJavaLevelAnnotation = getTestClass(desc).getAnnotation(MaximumJavaLevel.class);
        }
        if (maximumJavaLevelAnnotation != null) {
            if (JAVA_VERSION > maximumJavaLevelAnnotation.javaLevel()) {
                Log.debug(getMyClass(), "Removing test " + desc.getMethodName()
                                        + " from list to run, because its maximum java level is " + maximumJavaLevelAnnotation.javaLevel()
                                        + " and we are running with " + JAVA_VERSION);
                return false;
            }
        }

        MinimumJavaLevel minimumJavaLevelAnnotation = desc.getAnnotation(MinimumJavaLevel.class);
        FeatureRequiresMinimumJavaLevel featureRequiresLevelAnnotation = desc.getAnnotation(FeatureRequiresMinimumJavaLevel.class);
        //check for a method level annotation first

        //method level annotations supercede any class level annotation
        if (minimumJavaLevelAnnotation == null) {
            //there was no method level annotation
            //check for a test class level annotation
            minimumJavaLevelAnnotation = getTestClass(desc).getAnnotation(MinimumJavaLevel.class);
        }
        if (featureRequiresLevelAnnotation == null) {
            //there was no method level annotation
            //check for a test class level annotation
            featureRequiresLevelAnnotation = getTestClass(desc).getAnnotation(FeatureRequiresMinimumJavaLevel.class);
        }

        // If there's a minimum java level annotaton, that sets a global minimum level, so if we don't meet that, don't run tests
        boolean javaLevelTooLowForAllFeatures = minimumJavaLevelAnnotation != null && JAVA_VERSION < minimumJavaLevelAnnotation.javaLevel();
        if (javaLevelTooLowForAllFeatures) {
            Log.debug(getMyClass(), "Removing test " + desc.getMethodName() + " with minimum java level " + minimumJavaLevelAnnotation.javaLevel()
                                    + " from list to run, because it is too high for current java level "
                                    + JAVA_VERSION);
            return false;
        } else {
            // Check if this is testing the feature with the minimum level
            boolean applicableFeaturePresent = featureRequiresLevelAnnotation != null && featureRequiresLevelAnnotation.feature().equals(FEATURE_UNDER_TEST);
            if (applicableFeaturePresent) {
                // This feature has a minimum java level, do we meet it?

                if (JAVA_VERSION < featureRequiresLevelAnnotation.javaLevel()) {
                    Log.debug(getMyClass(), "Removing test " + desc.getMethodName() + " because feature " + featureRequiresLevelAnnotation.feature()
                                            + " from list to run, because it requires java level " + featureRequiresLevelAnnotation.javaLevel()
                                            + " and we are running with "
                                            + JAVA_VERSION);
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Returns a method which can be run as a junit test.
     */
    public FrameworkMethod createSyntheticTest() {
        return new SyntheticAlwaysPassesTest();
    }

    public static Statement emptyStatement() {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {}
        };
    }
}
