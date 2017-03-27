/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.repository.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.ws.repository.connections.RepositoryConnectionList;
import com.ibm.ws.repository.connections.RestRepositoryConnection;
import com.ibm.ws.repository.exceptions.RepositoryBackendException;
import com.ibm.ws.repository.resources.RepositoryResource;

public class FATTest {

    Properties dynamicApiKeyProps;
    Properties staticApiKeyProps;
    Properties staticApiKeyPropsInvalidRespositories;
    private final static File resourcesDir = new File("lib/LibertyFATTestFiles");

    public FATTest() throws FileNotFoundException, IOException {
        dynamicApiKeyProps = new Properties();
        dynamicApiKeyProps.load(this.getClass().getResourceAsStream("/testLogin.properties"));
        staticApiKeyProps = new Properties();
        staticApiKeyProps.load(new FileInputStream(new File(resourcesDir, "staticApiKeys.props")));
        staticApiKeyPropsInvalidRespositories = new Properties();
        staticApiKeyPropsInvalidRespositories.load(new FileInputStream(new File(resourcesDir, "staticApiKeysInvalidRepositories.props")));
    }

    @Test
    public void testDynamicApiKey() throws NoRepoAvailableException {
        LoginInfoProvider provider = new LoginInfoProvider(dynamicApiKeyProps);
        RestRepositoryConnection info = provider.getLoginInfo();

        assertNotNull(info.getApiKey());
        assertNotNull(info.getRepositoryUrl());
        assertNotNull(info.getUserId());
        assertNotNull(info.getPassword());

        assertNotNull(info.getClientLoginInfo().getApiKey());
        assertNotNull(info.getClientLoginInfo().getRepositoryUrl());
        assertNotNull(info.getClientLoginInfo().getUserId());
        assertNotNull(info.getClientLoginInfo().getPassword());

        // Check that we're caching the loginInfo
        RestRepositoryConnection info2 = provider.getLoginInfo();
        assertSame("Login info should be cached", info, info2);
    }

    /**
     * Test that we are returned a working server which is one of the two in the
     * properties file and that the fields in the LoginInfo are set as expected.
     * 
     * @throws NoRepoAvailableException
     */
    @Test
    public void testStaticApiKey() throws NoRepoAvailableException {
        LoginInfoProvider provider = new LoginInfoProvider(staticApiKeyProps);
        RestRepositoryConnection info = provider.getLoginInfo();

        if (staticApiKeyProps.get("test1.url").equals(info.getRepositoryUrl())) {
            Assert.assertEquals(staticApiKeyProps.get("test1.apiKey"), info.getApiKey());
        } else {
            Assert.assertEquals(staticApiKeyProps.get("test2.apiKey"), info.getApiKey());
        }

        assertNotNull(info.getApiKey());
        assertNotNull(info.getRepositoryUrl());
        assertNotNull(info.getUserId());
        assertNotNull(info.getPassword());

        assertNotNull(info.getClientLoginInfo().getApiKey());
        assertNotNull(info.getClientLoginInfo().getRepositoryUrl());
        assertNotNull(info.getClientLoginInfo().getUserId());
        assertNotNull(info.getClientLoginInfo().getPassword());
    }

    /**
     * Given a list of massive servers, that contains both of the valid ones (for failover) and a number
     * of invalid ones, that the only servers returned to us are the genuine ones (ie one that we can
     * talk to that give valid results).
     * 
     * @throws NoRepoAvailableException
     * @throws RepositoryBackendException
     */
    @Test
    public void testStaticApiKeyInvalidRespositories() throws NoRepoAvailableException, RepositoryBackendException {

        for (int i = 0; i < 10; i++) {
            LoginInfoProvider provider = new LoginInfoProvider(staticApiKeyPropsInvalidRespositories);
            RestRepositoryConnection info = provider.getLoginInfo();

            Collection<? extends RepositoryResource> resources = new RepositoryConnectionList(info).getAllResources();
            assertEquals("We were able to read from massive but did not get the expected result", 0, resources.size());
        }
    }

    @Test
    public void testInvalidServer() {
        Properties invalid = new Properties();
        invalid.setProperty("bad.url", "hxxp://www.example.com");

        LoginInfoProvider provider = new LoginInfoProvider(invalid);
        try {
            provider.getLoginInfo();
            fail();
        } catch (NoRepoAvailableException ex) {
            // Expected exception
        }
    }

    @Test
    public void testUserNoApiKey() {
        Properties invalid = new Properties(dynamicApiKeyProps);
        invalid.setProperty("userId", "testUser");

        try {
            new LoginInfoProvider(invalid);
            fail();
        } catch (IllegalArgumentException ex) {
            // Expected exception
        }
    }

    @Test
    public void testNoUserApiKey() {
        Properties invalid = new Properties();
        invalid.setProperty("server.url", "baz");
        invalid.setProperty("server.apiKey", "123456789");

        try {
            new LoginInfoProvider(invalid);
            fail();
        } catch (IllegalArgumentException ex) {
            // Expected exception
        }
    }

    @Test
    public void testSomeMissingApiKey() {
        // Copy static apiKey settings
        Properties invalid = new Properties(staticApiKeyProps);
        // Add a new server, but no apiKey
        invalid.setProperty("missingapi.url", "http://www.example.com");

        try {
            new LoginInfoProvider(invalid);
            fail();
        } catch (IllegalArgumentException ex) {
            // Expected exception
        }
    }

}
