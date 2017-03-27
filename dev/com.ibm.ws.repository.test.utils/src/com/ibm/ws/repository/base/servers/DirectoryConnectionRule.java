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

import java.io.File;
import java.io.IOException;

import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.ibm.ws.repository.connections.DirectoryRepositoryConnection;
import com.ibm.ws.repository.connections.RepositoryConnection;
import com.ibm.ws.repository.connections.RestRepositoryConnection;
import com.ibm.ws.repository.test.utils.clients.DirectoryWriteableClient;
import com.ibm.ws.repository.transport.client.RepositoryReadableClient;

public class DirectoryConnectionRule implements RestRepositoryConnectionRule {

    DirectoryWriteableConnection connection;

    public DirectoryConnectionRule() {}

    @Override
    public RestRepositoryConnection getRestConnection() {
        return null;
    }

    @Override
    public RepositoryConnection getConnection() {
        return connection;
    }

    /**
     * Empties the repository before and after the test.
     */
    @Override
    public Statement apply(final Statement statement, Description arg1) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                File tmpRepoRoot = File.createTempFile("tempRepoDir", null);
                tmpRepoRoot.delete();
                File tmpRepoDir = new File(tmpRepoRoot.getPath());
                if (!tmpRepoDir.mkdir()) {
                    throw new IOException("Couldn't create directory for temp repo directory: " + tmpRepoDir);
                }
                tmpRepoDir.deleteOnExit();
                connection = new DirectoryWriteableConnection(tmpRepoDir);
                try {
                    statement.evaluate();
                } finally {
                    recursiveDelete(tmpRepoDir);
                }
            }
        };
    }

    /**
     * Directory repos don't support updating of assets
     */
    @Override
    public boolean updatesAreSupported() {
        return false;
    }

    /**
     * No-op for directory, the search index doesn't need updating.
     */
    @Override
    public void refreshSearchIndex(String assetId) {}

    private void recursiveDelete(File file) {
        if (!file.exists()) {
            return;
        }

        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                recursiveDelete(child);
            }
        }

        file.delete();
    }

    private static class DirectoryWriteableConnection extends DirectoryRepositoryConnection {

        public DirectoryWriteableConnection(File root) {
            super(root);
        }

        @Override
        public RepositoryReadableClient createClient() {
            return new DirectoryWriteableClient(getRoot());
        }

    }

}
