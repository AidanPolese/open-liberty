/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.thread.zos.hooks.internal;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import org.eclipse.osgi.container.Module;
import org.eclipse.osgi.storage.bundlefile.BundleEntry;
import org.eclipse.osgi.storage.bundlefile.BundleFile;
import org.eclipse.osgi.storage.bundlefile.BundleFileWrapper;

import com.ibm.ws.kernel.security.thread.ThreadIdentityManager;

/**
 * BundleFileWrapper used to wrap OSGi bundles so that bundle access operations are done as the server identity.
 */
public class ThreadIdentityBundleFileWrapper extends BundleFileWrapper {
    public ThreadIdentityBundleFileWrapper(BundleFile bundleFile) {
        super(bundleFile);
    }

    @Override
    public File getFile(String path, boolean nativeCode) {
        Object token = ThreadIdentityManager.runAsServer();
        try {
            return super.getFile(path, nativeCode);
        } finally {
            ThreadIdentityManager.reset(token);
        }
    }

    @Override
    public BundleEntry getEntry(String path) {
        Object token = ThreadIdentityManager.runAsServer();
        try {
            return super.getEntry(path);
        } finally {
            ThreadIdentityManager.reset(token);
        }
    }

    @Override
    public Enumeration<String> getEntryPaths(String path) {
        Object token = ThreadIdentityManager.runAsServer();
        try {
            return super.getEntryPaths(path);
        } finally {
            ThreadIdentityManager.reset(token);
        }
    }

    @Override
    public Enumeration<String> getEntryPaths(String path, boolean recurse) {
        Object token = ThreadIdentityManager.runAsServer();
        try {
            return super.getEntryPaths(path, recurse);
        } finally {
            ThreadIdentityManager.reset(token);
        }
    }

    @Override
    public void close() throws IOException {
        Object token = ThreadIdentityManager.runAsServer();
        try {
            super.close();
        } finally {
            ThreadIdentityManager.reset(token);
        }
    }

    @Override
    public void open() throws IOException {
        Object token = ThreadIdentityManager.runAsServer();
        try {
            super.open();
        } finally {
            ThreadIdentityManager.reset(token);
        }
    }

    @Override
    public boolean containsDir(String dir) {
        Object token = ThreadIdentityManager.runAsServer();
        try {
            return super.containsDir(dir);
        } finally {
            ThreadIdentityManager.reset(token);
        }
    }

    @Override
    protected URL createResourceURL(BundleEntry bundleEntry, Module hostModule, int index, String path) {
        Object token = ThreadIdentityManager.runAsServer();
        try {
            return super.createResourceURL(bundleEntry, hostModule, index, path);
        } finally {
            ThreadIdentityManager.reset(token);
        }
    }
}