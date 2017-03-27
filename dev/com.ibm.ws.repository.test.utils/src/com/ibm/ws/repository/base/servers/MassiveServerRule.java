/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.repository.base.servers;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.ibm.ws.repository.base.DeleteRepo;
import com.ibm.ws.repository.base.RepositoryTestUtils;
import com.ibm.ws.repository.base.RepositoryTestUtils.TestType;
import com.ibm.ws.repository.base.RepositoryTestUtilsFactory;
import com.ibm.ws.repository.connections.RestRepositoryConnection;

/**
 * A rule to setup and tear down a massive repository for use in the tests
 * use as a class rule
 */
public class MassiveServerRule implements TestRule {

    private RepositoryTestUtils<RestRepositoryConnection> testUtils;

    @Override
    public Statement apply(final Statement statement, Description description) {
        return new Statement() {

            @Override
            public void evaluate() throws Throwable {

                testUtils = RepositoryTestUtilsFactory.getInstance().createTestUtils(TestType.MASSIVE_REPO);
                // This isn't great, it is working around the old structure where
                // the tests all extended the TestBaseClass
                testUtils.setUpClass();
                try {
                    statement.evaluate();
                } finally {
                    DeleteRepo.deleteRepo(testUtils.getRepositoryConnection());
                }

            }
        };
    }

    public RestRepositoryConnection getRepositoryConnection() {
        return testUtils.getRepositoryConnection();
    }

}
