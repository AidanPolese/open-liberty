/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.kernel.internal.reference;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 *
 */
public class Handler extends URLStreamHandler {

    /** {@inheritDoc} */
    @Override
    protected URLConnection openConnection(URL arg0) throws IOException {
        return new URLConnection(arg0) {
            @Override
            public void connect() throws IOException {}

            @Override
            public InputStream getInputStream() {
                return null;
            }
        };
    }

}
