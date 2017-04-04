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
package com.ibm.ws.artifact.equinox.module.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.osgi.storage.bundlefile.BundleEntry;

import com.ibm.wsspi.adaptable.module.Container;

/**
 *
 */
public class RootModuleEntry extends BundleEntry {
    private final Container root;
    private final String path;

    /**
     * @param entry
     * @param path
     */
    public RootModuleEntry(Container root) {
        this.root = root;
        this.path = "/";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.osgi.baseadaptor.bundlefile.BundleEntry#getInputStream()
     */
    @Override
    public InputStream getInputStream() throws IOException {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.osgi.baseadaptor.bundlefile.BundleEntry#getSize()
     */
    @Override
    public long getSize() {
        return 0L;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.osgi.baseadaptor.bundlefile.BundleEntry#getName()
     */
    @Override
    public String getName() {
        return path;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.osgi.baseadaptor.bundlefile.BundleEntry#getTime()
     */
    @Override
    public long getTime() {
        return 0L;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.osgi.baseadaptor.bundlefile.BundleEntry#getLocalURL()
     */
    @Override
    public URL getLocalURL() {
        //It is questionable if this is really needed.
        return root.getURLs().iterator().next();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.osgi.baseadaptor.bundlefile.BundleEntry#getFileURL()
     */
    @Override
    public URL getFileURL() {
        //Probably don't need to do this
        throw new UnsupportedOperationException();
    }

}
