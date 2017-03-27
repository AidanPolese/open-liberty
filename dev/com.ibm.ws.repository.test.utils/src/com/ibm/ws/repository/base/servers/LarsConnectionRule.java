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

import java.util.Collection;

import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.ibm.ws.repository.connections.RepositoryConnection;
import com.ibm.ws.repository.connections.RestRepositoryConnection;
import com.ibm.ws.repository.exceptions.RepositoryBackendException;
import com.ibm.ws.repository.exceptions.RepositoryResourceDeletionException;
import com.ibm.ws.repository.resources.RepositoryResource;
import com.ibm.ws.repository.resources.writeable.RepositoryResourceWritable;

public class LarsConnectionRule implements RestRepositoryConnectionRule {

    private final RestRepositoryConnection repo;

    public LarsConnectionRule(LarsServerRule larsFixture) {
        this.repo = larsFixture.getRestConnection();
    }

    @Override
    public RestRepositoryConnection getRestConnection() {
        return repo;
    }

    @Override
    public RepositoryConnection getConnection() {
        return repo;
    }

    /**
     * Empties the repository before and after the test.
     */
    @Override
    public Statement apply(final Statement statement, Description arg1) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                deleteAllAssets();
                try {
                    statement.evaluate();
                } finally {
                    deleteAllAssets();
                }
            }
        };
    }

    /**
     * If the repo can't be cleaned up, fail the test, as following tests
     * will fail if there are extra assets there.
     *
     * @throws RepositoryBackendException
     * @throws RepositoryResourceDeletionException
     */
    private void deleteAllAssets() throws RepositoryBackendException, RepositoryResourceDeletionException {
        Collection<RepositoryResource> resources = repo.getAllResourcesWithDupes();
        for (RepositoryResource res : resources) {
            RepositoryResourceWritable writable = (RepositoryResourceWritable) res;
            writable.delete();
        }
    }

    /**
     * Lars doesn't support updating of assets
     */
    @Override
    public boolean updatesAreSupported() {
        return false;
    }

    /**
     * No-op for lars, the search index doesn't need updating.
     */
    @Override
    public void refreshSearchIndex(String assetId) {}

}
