/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.classloading.internal;

import static org.junit.Assert.assertFalse;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Re-purpose all the inherited tests to use {@link ShadowClassLoader}s, which should effect the same semantics.
 */
public class ParentFirstParentLastShadowTest extends ParentFirstParentLastTest {
    @BeforeClass
    public static void createShadowClassLoaders() throws Exception {
        parentFirstLoader = cls.getShadowClassLoader(parentFirstLoader);
        parentLastLoader = cls.getShadowClassLoader(parentLastLoader);
    }

    @Rule
    public TestRule checkAfterEveryTestThatNoClassesWereLoaded = new TestRule() {
        @Override
        public Statement apply(final Statement stmt, Description desc) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    stmt.evaluate();
                    checkNoClassesWereLoaded();
                }
            };
        }
    };

    public void checkNoClassesWereLoaded() throws Exception {
        assertFalse("No classes should have been loaded by the AppClassLoaders", outputManager.checkForTrace("LOAD"));
    }
}
