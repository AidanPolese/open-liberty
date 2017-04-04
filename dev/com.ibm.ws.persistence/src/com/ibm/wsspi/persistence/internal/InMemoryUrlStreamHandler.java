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
package com.ibm.wsspi.persistence.internal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.url.AbstractURLStreamHandlerService;
import org.osgi.service.url.URLStreamHandlerService;

import com.ibm.wsspi.persistence.InMemoryMappingFile;

/**
 * A URLStreamHandler that knows how to process URLs with the 'persistence-service' protocol.
 * 
 */
@Component(name = "InMemoryUrlStreamHandler",
           service = { URLStreamHandlerService.class, InMemoryUrlStreamHandler.class },
           property = { "url.handler.protocol=persistence-service", "url.handler.protocol=persistence-service" })
public class InMemoryUrlStreamHandler extends AbstractURLStreamHandlerService {
    private final Map<String, InMemoryMappingFile> _data;

    public InMemoryUrlStreamHandler() {
        _data = new ConcurrentHashMap<String, InMemoryMappingFile>();
    }

    @Override
    public URLConnection openConnection(URL u) throws IOException {
        return new PsURLConnection(u, _data.get(u.getFile()));
    }

    void clear() {
        _data.clear();
    }

    /**
     * Remove mappings for the provided urls.
     */
    void deregister(List<URL> urls) {
        for (URL url : urls) {
            _data.remove(url.getFile());
        }
    }

    /**
     * Register the URL mapping to the provided InMemoryMappingFile.
     */
    void register(URL url, InMemoryMappingFile immf) {
        _data.put(url.getFile(), immf);
    }

    class PsURLConnection extends URLConnection {
        InMemoryMappingFile _mappingFile;

        public PsURLConnection(URL u, InMemoryMappingFile mappingFile) {
            super(u);
            _mappingFile = mappingFile;
        }

        @Override
        public void connect() throws IOException {
            connected = true;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(_mappingFile.getMappingFile());
        }
    }

}
