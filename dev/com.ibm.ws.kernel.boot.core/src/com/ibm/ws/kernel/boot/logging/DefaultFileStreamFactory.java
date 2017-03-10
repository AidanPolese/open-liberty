/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.kernel.boot.logging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * A basic implementation of a factory for FileOutputStream that defers creation
 * to the base FileOutputStream constructor.
 * <p>
 * Package protected class. This is a delegate of the public factory
 */
class DefaultFileStreamFactory implements TextFileOutputStreamFactory.Delegate {
    /** {@inheritDoc} */
    @Override
    public FileOutputStream createOutputStream(File file) throws FileNotFoundException {
        return new FileOutputStream(file);
    }

    /** {@inheritDoc} */
    @Override
    public FileOutputStream createOutputStream(File file, boolean append) throws FileNotFoundException {
        return new FileOutputStream(file, append);
    }

    /** {@inheritDoc} */
    @Override
    public FileOutputStream createOutputStream(String name) throws FileNotFoundException {
        return new FileOutputStream(name);
    }

    /** {@inheritDoc} */
    @Override
    public FileOutputStream createOutputStream(String name, boolean append) throws FileNotFoundException {
        return new FileOutputStream(name, append);
    }
}
