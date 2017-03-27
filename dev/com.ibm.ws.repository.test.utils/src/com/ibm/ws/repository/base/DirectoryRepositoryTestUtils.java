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

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import com.ibm.ws.repository.connections.DirectoryRepositoryConnection;
import com.ibm.ws.repository.resources.internal.RepositoryResourceImpl;

/**
 *
 */
public class DirectoryRepositoryTestUtils implements RepositoryTestUtils<DirectoryRepositoryConnection> {

    private File _root;

    /** {@inheritDoc} */
    @Override
    public void setUpClass() {}

    /** {@inheritDoc} */
    @Override
    public void tearDownClass() {}

    private void deleteDir(File f) {
        if (f.exists()) {
            if (f.isDirectory()) {
                for (File child : f.listFiles()) {
                    deleteDir(child);
                }
                if (!f.delete()) {
                    System.out.println("*** FAILED TO DELETE " + f.getAbsolutePath());
                }
            } else {
                if (!f.delete()) {
                    System.out.println("*** FAILED TO DELETE " + f.getAbsolutePath());
                }
            }
        }
        System.out.println("Deleting " + f.getAbsolutePath());
    }

    /**
     * {@inheritDoc}
     * 
     * @return
     */
    @Override
    public DirectoryRepositoryConnection setUpTest(boolean wipeBeforeEachTest) throws Exception {
        // A new sub directory is created for each test at the moment, so tests don't reuse the same directory
        _root = File.createTempFile("repoTest", "test");
        _root.delete();
        _root.mkdir();
        System.out.println("testing using " + _root.getAbsolutePath());
        File testSubDir = new File(_root, "" + Math.random());
        testSubDir.mkdirs();
        return new DirectoryRepositoryConnection(testSubDir);
    }

    /** {@inheritDoc} */
    @Override
    public void tearDownTest(boolean wipeBeforeEachTest) throws Exception {
        deleteDir(_root);
    }

    /** {@inheritDoc} */
    @Override
    public DirectoryRepositoryConnection createNewRepo() throws NoRepoAvailableException {
        // Not used by the client tests
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Collection<RepositoryResourceImpl> getAllResourcesWithDupes() throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, InstantiationException, IllegalArgumentException, InvocationTargetException {
        // Not used by the client tests
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public DirectoryRepositoryConnection getRepositoryConnection() {
        // Not used by the client tests
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void refreshTextIndex(String lastAssetId) throws Exception {
        // Do nothing, there's no text index to refresh for directory repo
    }

}
