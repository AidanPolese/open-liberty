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

import org.junit.rules.TestRule;

import com.ibm.ws.repository.connections.RepositoryConnection;
import com.ibm.ws.repository.connections.RestRepositoryConnection;

/**
 * A test rule representing a connection to a repository. The repository will
 * be emptied at the beginning and the end of the test.
 */
public interface RestRepositoryConnectionRule extends TestRule {

    RestRepositoryConnection getRestConnection();

    RepositoryConnection getConnection();

    boolean updatesAreSupported();

    void refreshSearchIndex(String assetId) throws MalformedURLException, ProtocolException, IOException;

}
