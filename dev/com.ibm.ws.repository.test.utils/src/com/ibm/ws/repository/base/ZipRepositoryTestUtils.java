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

import com.ibm.ws.repository.connections.ZipRepositoryConnection;
import com.ibm.ws.repository.resources.internal.RepositoryResourceImpl;

/**
 *
 */
public class ZipRepositoryTestUtils implements RepositoryTestUtils<ZipRepositoryConnection> {

    private File _zip;

    /** {@inheritDoc} */
    @Override
    public void setUpClass() {}

    /** {@inheritDoc} */
    @Override
    public void tearDownClass() {}

    private void delete(File f) {
        if (f.exists()) {
            if (!f.delete()) {
                System.out.println("*** FAILED TO DELETE " + f.getAbsolutePath());
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
    public ZipRepositoryConnection setUpTest(boolean wipeBeforeEachTest) throws Exception {
        // A new sub directory is created for each test at the moment, so tests don't reuse the same directory
        _zip = File.createTempFile("repoTest", ".zip");
        System.out.println("testing using " + _zip.getAbsolutePath());
        return new ZipRepositoryConnection(_zip);
    }

    /** {@inheritDoc} */
    @Override
    public void tearDownTest(boolean wipeBeforeEachTest) throws Exception {
        delete(_zip);
    }

    /** {@inheritDoc} */
    @Override
    public ZipRepositoryConnection createNewRepo() throws NoRepoAvailableException {
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
    public ZipRepositoryConnection getRepositoryConnection() {
        // Not used by the client tests
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void refreshTextIndex(String lastAssetId) throws Exception {
        // Nothing to do for zip repos
    }

}
