/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.repository.base;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import com.ibm.ws.repository.connections.RepositoryConnection;
import com.ibm.ws.repository.resources.internal.RepositoryResourceImpl;

/**
 *
 */
public interface RepositoryTestUtils<T extends RepositoryConnection> {
    public enum TestType {
        MASSIVE_REPO, DIRECTORY_REPO, ZIP_REPO
    };

    public void setUpClass() throws Exception;

    public void tearDownClass() throws Exception;

    public T setUpTest(boolean wipeBeforeEachTest) throws Exception;

    public void tearDownTest(boolean wipeBeforeEachTest) throws Exception;

    public T createNewRepo() throws NoRepoAvailableException;

    public Collection<RepositoryResourceImpl> getAllResourcesWithDupes() throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, InstantiationException, IllegalArgumentException, InvocationTargetException;

    public T getRepositoryConnection();

    public void refreshTextIndex(String lastAssetId) throws Exception;
}
