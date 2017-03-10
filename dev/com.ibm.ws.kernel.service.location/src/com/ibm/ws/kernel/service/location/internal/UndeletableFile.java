/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */

package com.ibm.ws.kernel.service.location.internal;

import java.io.File;

import com.ibm.websphere.ras.annotation.Trivial;

/**
 * A simple extension for the base file class that always returns
 * false for <code>delete</code>, to prevent root files from being
 * deleted (at least by users of this service... ).
 */
class UndeletableFile extends File {
    private static final long serialVersionUID = 29757460383711544L;

    /**
     * @param pathname
     */
    @Trivial
    public UndeletableFile(String pathname) {
        super(pathname);
    }

    /**
     * {@inheritDoc}
     */
    @Trivial
    @Override
    public boolean delete() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Trivial
    @Override
    public void deleteOnExit() {}
}