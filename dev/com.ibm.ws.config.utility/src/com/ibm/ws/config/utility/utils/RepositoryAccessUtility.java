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
package com.ibm.ws.config.utility.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import com.ibm.ws.install.InstallException;
import com.ibm.ws.install.RepositoryConfigUtils;
import com.ibm.ws.repository.connections.RepositoryConnectionList;
import com.ibm.ws.repository.connections.RestRepositoryConnectionProxy;
import com.ibm.ws.repository.connections.liberty.MainRepository;
import com.ibm.ws.repository.exceptions.RepositoryBackendException;
import com.ibm.ws.repository.exceptions.RepositoryBackendIOException;
import com.ibm.ws.repository.exceptions.RepositoryException;
import com.ibm.ws.repository.exceptions.RepositoryResourceException;
import com.ibm.ws.repository.resources.AttachmentResource;
import com.ibm.ws.repository.resources.ConfigSnippetResource;

/**
 * Utility for downloading the contents of Config Snippets in WASDev Repository
 */
public class RepositoryAccessUtility {

    private static Collection<ConfigSnippetResource> snippet_cache;

    public static StringBuilder getConfigSnippet(String configName) throws IOException, RepositoryBackendException, RepositoryResourceException, InstallException {
        RepositoryConnectionList loginInfo = getLoginInfo();
        Collection<ConfigSnippetResource> configSnippets = loginInfo.getAllConfigSnippets();

        for (ConfigSnippetResource snippet : configSnippets) {
            AttachmentResource att = snippet.getMainAttachment();
            if (att != null && att.getName().equalsIgnoreCase(configName.concat(".xml"))) {
                InputStream is = att.getInputStream();
                StringBuilder sb = new StringBuilder();
                writeInputStream(is, sb);
                return sb;
            }
        }

        return null;
    }

    public static List<String> getConfigSnippetList() throws RepositoryException, InstallException {
        List<String> snippetList = new ArrayList<String>();

        RepositoryConnectionList loginInfo = getLoginInfo();
        Collection<ConfigSnippetResource> configSnippets;

        if (snippet_cache != null) {
            configSnippets = snippet_cache;
        } else {
            configSnippets = loginInfo.getAllConfigSnippets();
            snippet_cache = configSnippets;
        }

        for (ConfigSnippetResource snippet : configSnippets) {
            AttachmentResource att = snippet.getMainAttachment();
            if (att != null) {
                //strip the extension of file
                snippetList.add(att.getName().substring(0, att.getName().lastIndexOf('.')));
            }
        }

        return snippetList;
    }

    public static String getConfigSnippetDescription(String configName) throws RepositoryException, InstallException {
        RepositoryConnectionList loginInfo = getLoginInfo();
        Collection<ConfigSnippetResource> configSnippets;

        if (snippet_cache != null) {
            configSnippets = snippet_cache;
        } else {
            configSnippets = loginInfo.getAllConfigSnippets();
            snippet_cache = configSnippets;
        }

        String desc;

        for (ConfigSnippetResource snippet : configSnippets) {
            AttachmentResource att = snippet.getMainAttachment();
            if (att != null && att.getName().equalsIgnoreCase(configName.concat(".xml"))) {
                desc = snippet.getName() + ": " + System.getProperty("line.separator") + snippet.getShortDescription();
                return desc;
            }
        }

        return null;
    }

    /**
     * Grabs Login Info and sets the Proxy
     * 
     * @return
     * @throws RepositoryBackendIOException
     * @throws InstallException
     */
    private static RepositoryConnectionList getLoginInfo() throws RepositoryBackendIOException, InstallException {
        RestRepositoryConnectionProxy proxy = getProxy();
        RepositoryConnectionList loginInfo = new RepositoryConnectionList(MainRepository.createConnection(proxy));
        return loginInfo;
    }

    /**
     * Gets the properties from the properties file and gets the proxy
     * 
     * @return
     * @throws InstallException
     */

    public static RestRepositoryConnectionProxy getProxy() throws InstallException {
        Properties repoProperties = RepositoryConfigUtils.loadRepoProperties();
        RestRepositoryConnectionProxy proxy = RepositoryConfigUtils.getProxyInfo(repoProperties);
        return proxy;
    }

    private static void writeInputStream(InputStream is, StringBuilder sb) throws IOException {
        byte[] buffer = new byte[2048];
        int length;

        try {
            while ((length = is.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, length));
            }
        } finally {
            is.close();
        }
    }
}
