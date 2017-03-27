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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;

import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.ibm.ws.repository.base.DeleteRepo;
import com.ibm.ws.repository.base.RestRepositoryTestUtils;
import com.ibm.ws.repository.connections.RepositoryConnection;
import com.ibm.ws.repository.connections.RestRepositoryConnection;

public class MassiveConnectionRule implements RestRepositoryConnectionRule {

    private final MassiveServerRule massiveFixture;

    public MassiveConnectionRule(MassiveServerRule massiveFixture) {
        this.massiveFixture = massiveFixture;
    }

    @Override
    public RestRepositoryConnection getRestConnection() {
        return massiveFixture.getRepositoryConnection();
    }

    @Override
    public RepositoryConnection getConnection() {
        return massiveFixture.getRepositoryConnection();
    }

    @Override
    public Statement apply(final Statement statement, Description arg1) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                DeleteRepo.deleteRepo(massiveFixture.getRepositoryConnection());
                try {
                    statement.evaluate();
                } finally {
                    DeleteRepo.deleteRepo(massiveFixture.getRepositoryConnection());
                }
            }
        };
    }

    @Override
    public boolean updatesAreSupported() {
        return true;
    }

    @Override
    public void refreshSearchIndex(String assetId) throws MalformedURLException, ProtocolException, IOException {
        RestRepositoryTestUtils.refreshElasticSearchIndex(massiveFixture.getRepositoryConnection(), assetId);

    }

}
