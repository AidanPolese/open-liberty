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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 */
public class JarResourceHandler implements ResourceHandler {

    private final URL url;
    private final boolean verify;

    private File file;
    private boolean downloadedFile;
    private volatile JarFile jarFile;

    public JarResourceHandler(URL url, boolean verify) throws URISyntaxException {
        this.url = url;
        this.verify = verify;

        if ("file".equals(url.getProtocol())) {
            this.file = new File(url.toURI().getPath());
        }
    }

    private File downloadJarFile() throws IOException {
        File file = null;
        OutputStream out = null;
        InputStream in = null;
        try {
            file = File.createTempFile("jarclassloader.", ".jar");
            out = new BufferedOutputStream(new FileOutputStream(file), 2048);
            in = new BufferedInputStream(url.openStream(), 2048);

            JarFileClassLoader.copy(in, out);

            file.deleteOnExit();
        } catch (Exception e) {
            JarFileClassLoader.close(out);

            if (file != null) {
                file.delete();
            }

            throw new IOException("Unable to cache JarFile", e);
        } finally {
            JarFileClassLoader.close(in);
            JarFileClassLoader.close(out);
        }

        return file;
    }

    private void ensureOpen() throws IOException {
        if (jarFile == null) {
            synchronized (this) {
                if (jarFile == null) {
                    if (file == null) {
                        file = downloadJarFile();
                        downloadedFile = true;
                    }
                    jarFile = new JarFile(file, verify);
                }
            }
        }
    }

    @Override
    public void close() throws IOException {
        if (jarFile != null) {
            jarFile.close();
        }
        if (downloadedFile) {
            file.delete();
        }
    }

    @Override
    public ResourceEntry getEntry(String name) {
        try {
            ensureOpen();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        JarEntry jarEntry = jarFile.getJarEntry(name);
        if (jarEntry == null) {
            return null;
        } else {
            return new JarResourceEntry(this, jarEntry);
        }
    }

    @Override
    public URL toURL() {
        return url;
    }

    @Override
    public Manifest getManifest() throws IOException {
        try {
            ensureOpen();
        } catch (IOException e) {
            return null;
        }

        return jarFile.getManifest();
    }

    JarFile getJarFile() {
        return jarFile;
    }
}
