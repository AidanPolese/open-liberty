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
package com.ibm.ws.kernel.internal.classloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.cert.Certificate;
import java.util.jar.Manifest;

/**
 */
public class DirectoryResourceEntry implements ResourceEntry {

    private final DirectoryResourceHandler handler;
    private final File file;

    public DirectoryResourceEntry(DirectoryResourceHandler handler, File file) {
        this.handler = handler;
        this.file = file;
    }

    @Override
    public ResourceHandler getResourceHandler() {
        return handler;
    }

    @Override
    public Manifest getManifest() throws IOException {
        return handler.getManifest();
    }

    @Override
    public Certificate[] getCertificates() {
        return null;
    }

    @Override
    public byte[] getBytes() throws IOException {
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            return JarFileClassLoader.getBytes(in, file.length());
        } finally {
            JarFileClassLoader.close(in);
        }
    }

    @Override
    public URL toURL() {
        return JarFileClassLoader.toURL(file);
    }

}
