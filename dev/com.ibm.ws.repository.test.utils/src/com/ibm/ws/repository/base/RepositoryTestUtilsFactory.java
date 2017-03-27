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

import com.ibm.ws.repository.base.RepositoryTestUtils.TestType;
import com.ibm.ws.repository.connections.RepositoryConnection;

/**
 *
 */
public class RepositoryTestUtilsFactory {

    private static Object lock = new Object();
    private static RepositoryTestUtilsFactory instance;

    /**
     * Get the singleton instance of the factory
     *
     * @return
     */
    public static RepositoryTestUtilsFactory getInstance() {
        synchronized (lock) {
            if (null == instance) {
                instance = new RepositoryTestUtilsFactory();
            }
        }
        return instance;
    }

    /**
     * Private constructor, obtain an instance by the static {@link RepositoryClientFactory#getInstance} method
     */
    private RepositoryTestUtilsFactory() {}

    /**
     * @param login
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends RepositoryTestUtils<? extends RepositoryConnection>> T createTestUtils(TestType type) {
        T utils = null;
        switch (type) {
            case MASSIVE_REPO:
                utils = (T) new RestRepositoryTestUtils();
                break;
            case DIRECTORY_REPO:
                utils = (T) new DirectoryRepositoryTestUtils();
                break;
            case ZIP_REPO:
                utils = (T) new ZipRepositoryTestUtils();
                break;
            default:
                throw new UnsupportedOperationException("A repository connection of " + type + " is not currently supported");
        }
        return utils;
    }
}
