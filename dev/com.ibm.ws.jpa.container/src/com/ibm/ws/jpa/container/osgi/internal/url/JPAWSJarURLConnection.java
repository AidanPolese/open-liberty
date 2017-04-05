/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013, 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jpa.container.osgi.internal.url;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.Permission;

import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.ws.artifact.url.WSJarURLConnection;
import com.ibm.ws.kernel.security.thread.ThreadIdentityManager;

/**
 * JPA URLConnection that presents a "wsjar" URL's resources as a JAR-format InputStream.
 * 
 * @author jgrassel
 * 
 */
public class JPAWSJarURLConnection extends URLConnection {
    private File urlTargetFile = null; // File object referencing the jar archive addressed by the wsjar:file URL
    private String archivePath = null; // Content path within the jar archive, ie "META-INF/MANIFEST.MF"

    private InputStream inputStream = null;

    public JPAWSJarURLConnection(URL url, WSJarURLConnection wsJarUrlConnection) throws MalformedURLException {
        super(url);

        if (url == null || wsJarUrlConnection == null) {
            throw new IllegalArgumentException("JPAWSJarURLConnection ctor cannot take any null arguments.");
        }

        urlTargetFile = wsJarUrlConnection.getFile();
        archivePath = wsJarUrlConnection.getEntry();
        if (archivePath == null || archivePath.isEmpty()) {
            archivePath = "";
        }
    }

    @Override
    @Trivial
    public void connect() throws IOException {
        connected = true;
    }

    /*
     * Passthrough operations for archive referencing wsjar URL support. Synchronized because calling getInputStream()
     * while an InputStream is still active should return the active InputStream.
     */
    @Override
    public synchronized InputStream getInputStream() throws IOException {
        if (connected == false) {
            // Implicitly open the connection if it has not yet been done so.
            connect();
        }

        Object token = ThreadIdentityManager.runAsServer();
        try {
            if (inputStream == null) {
                if ("".equals(archivePath)) {
                    inputStream = new FileInputStream(urlTargetFile);
                } else {
                    inputStream = new FilterZipFileInputStream(urlTargetFile, archivePath);
                }
            }
        } finally {
            ThreadIdentityManager.reset(token);
        }

        return inputStream;
    }

    @Override
    public long getLastModified() {
        return urlTargetFile.lastModified();
    }

    @Override
    public Permission getPermission() throws IOException {
        return new FilePermission(urlTargetFile.getAbsolutePath(), "read");
    }
}
