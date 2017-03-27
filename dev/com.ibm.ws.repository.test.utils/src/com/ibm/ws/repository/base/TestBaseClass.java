package com.ibm.ws.repository.base;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.ibm.ws.repository.base.RepositoryTestUtils.TestType;
import com.ibm.ws.repository.connections.RepositoryConnection;

public abstract class TestBaseClass {

    protected static RepositoryTestUtils<? extends RepositoryConnection> _utils;
    protected RepositoryConnection _repoConnection;

    private static TestType testType;

    private static boolean wipeBeforeEachTest;

    private static final Logger logger = Logger.getLogger(TestBaseClass.class.getName());

    @Rule
    public TestName name = new TestName();

    /**
     * The test type for this test class. Subclasses can override this by defining a public static method with the same signature.
     * <p>
     * The method named "getTestType" on the actual test class will be called reflectively to determine the test type.
     */
    public static TestType getTestType() {
        return TestType.MASSIVE_REPO;
    }

    /**
     * Whether the repository should be cleared before each test. Subclasses can override this by defining a public static method with the same signature.
     * <p>
     * The method named "getWipeBeforeEachTest" on the actual test class will be called reflectively to determine whether to wipe the repository between tests.
     */
    public static boolean getWipeBeforeEachTest() {
        return true;
    }

    @ClassRule
    public static TestRule readSubclassParameters = new TestRule() {

        @Override
        public Statement apply(Statement statement, Description description) {
            try {
                // Get the test class
                Class<?> testClass = description.getTestClass();

                // Reflectively call the getTestType and getWipeBeforeEachTest methods on the actual test class
                testType = (TestType) testClass.getMethod("getTestType").invoke(null);
                wipeBeforeEachTest = (Boolean) testClass.getMethod("getWipeBeforeEachTest").invoke(null);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return statement;
        }
    };

    @BeforeClass
    public static void setupUtils() throws Exception {
        logger.log(Level.INFO, "Setup Utils");
        _utils = RepositoryTestUtilsFactory.getInstance().createTestUtils(testType);
        _utils.setUpClass();
    }

    /**
     * Some repository tests can have intermittent failures.
     * This rule will retry any failing tests twice.
     * 
     * @throws Exception
     */

    @Rule
    public TestRule retry = new RetryRule(2);

    @Before
    public void doSetup() throws Exception {
        logger.log(Level.INFO, "TestBaseClass.Base setup");
        printTestStart();
        _repoConnection = _utils.setUpTest(wipeBeforeEachTest);
    }

    public void printTestStart() {
        logger.log(Level.INFO, "---");
        logger.log(Level.INFO, "--- Start of " + name.getMethodName());
    }

    @After
    public void printTestEnd() throws Exception {
        _utils.tearDownTest(wipeBeforeEachTest);
        logger.log(Level.INFO, "--- End of " + name.getMethodName());
    }

    class RetryRule implements TestRule {

        private final int retryLimit;

        /**
         * @param retryLimit the number of times to retry the test if it fails
         */
        private RetryRule(int retryLimit) {
            this.retryLimit = retryLimit;
        }

        @Override
        public Statement apply(Statement base, Description desc) {
            final Statement baseStatement = base;

            return new Statement() {
                @Override
                public void evaluate() throws Throwable {

                    int runTimes = 0;
                    while (true) {
                        runTimes++;
                        try {
                            baseStatement.evaluate();
                            break; // Break out of retry loop when we succeed
                        } catch (Throwable t) {

                            // If we've reached the retry limit, give up and rethrow the exception
                            if (runTimes > retryLimit) {
                                logger.log(Level.INFO, "Too many failures, giving up");
                                throw t;
                            } else {
                                logger.log(Level.INFO, "Logged failure, retrying: " + t.getMessage());
                            }
                        }
                    }
                }

            };
        }

    }

    /**
     * Invoke a method reflectively if that does not use primitive arguments
     * 
     * @param targetObject
     * @param methodName
     * @param classes
     * @param values
     * @return
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public static Object reflectiveCallAnyTypes(Object targetObject, String methodName, @SuppressWarnings("rawtypes") Class[] classes, Object[] values)
                    throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        // Usage example of this method 
        // int i = (Integer)reflectiveCallAnyTypes(targetObject,"methodName", 
        //       new Class[] {int.class}, 
        //       new Object[] {9});

        // Get the class of the targetObject
        Class<?> c = null;
        if (targetObject instanceof Class) {
            c = (Class<?>) targetObject;
        } else {
            c = targetObject.getClass();
        }

        Method method = null;
        boolean finished = false;
        while (!finished) {

            try {
                // get method
                method = c.getDeclaredMethod(methodName, classes);
                finished = true;
                method.setAccessible(true);
            } catch (NoSuchMethodException nsme) {
                if (c.getName().equals("java.lang.Object")) {
                    throw nsme;
                } else {
                    c = c.getSuperclass();
                }
            }
        }

        // Invoke MassiveResource.getAsset()
        return method.invoke(targetObject, values);
    }

    public static File getFile(String antBuildLocation, String junitLocation) {
        File isRunningLocally = new File("../localoverride");
        if (isRunningLocally.exists()) {
            return new File(antBuildLocation);
        } else {
            return new File(junitLocation);
        }
    }

}
