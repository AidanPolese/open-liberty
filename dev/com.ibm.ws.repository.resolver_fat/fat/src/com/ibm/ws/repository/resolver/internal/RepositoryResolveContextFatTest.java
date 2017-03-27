/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.repository.resolver.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;

import com.ibm.ws.repository.base.servers.DirectoryConnectionRule;
import com.ibm.ws.repository.base.servers.LarsConnectionRule;
import com.ibm.ws.repository.base.servers.MassiveConnectionRule;
import com.ibm.ws.repository.base.servers.RestRepositoryConnectionRule;
import com.ibm.ws.repository.common.enums.ResourceType;
import com.ibm.ws.repository.connections.RepositoryConnection;
import com.ibm.ws.repository.connections.RepositoryConnectionList;
import com.ibm.ws.repository.exceptions.RepositoryBackendException;
import com.ibm.ws.repository.exceptions.RepositoryResourceException;
import com.ibm.ws.repository.resolver.FATSuite;
import com.ibm.ws.repository.resolver.RepositoryResolverTest;
import com.ibm.ws.repository.resolver.RepositoryResolverTest.BasicTestStrategy;
import com.ibm.ws.repository.resolver.internal.namespace.InstallableEntityIdentityConstants;
import com.ibm.ws.repository.resolver.internal.namespace.InstallableEntityIdentityConstants.NameAttributes;
import com.ibm.ws.repository.resolver.internal.resource.InstallableEntityRequirement;
import com.ibm.ws.repository.resources.internal.EsaResourceImpl;

/**
 * Tests for the {@link RepositoryResolveContext} class.
 */
public class RepositoryResolveContextFatTest {

    @Rule
    public RestRepositoryConnectionRule connection;

    private RepositoryConnection getConnection() {
        return connection.getConnection();
    }

    public static class RepositoryResolveContextFatTestMassive extends RepositoryResolveContextFatTest {
        public RepositoryResolveContextFatTestMassive() {
            connection = new MassiveConnectionRule(FATSuite.massiveResource);
        }
    }

    public static class RepositoryResolveContextFatTestLars extends RepositoryResolveContextFatTest {
        public RepositoryResolveContextFatTestLars() {
            if (FATSuite.larsResource != null) {
                connection = new LarsConnectionRule(FATSuite.larsResource);
            }
        }
    }

    public static class RepositoryResolveContextFatTestDirectory extends RepositoryResolveContextFatTest {
        public RepositoryResolveContextFatTestDirectory() {
            connection = new DirectoryConnectionRule();
        }
    }

    /**
     * Don't run tests if the connection is null. This will happen where
     * (for example) it is a LARS connection running on a Java 6 VM.
     */
    @Before
    public void skipIfNullConnection() {
        assumeTrue(connection != null);
    }

    /**
     * Tests that if no matching feature is found then it will be loaded from the repository
     *
     * @throws RepositoryBackendException
     * @throws RepositoryResourceException
     */
    @Test
    public void testFeatureFromRepo() throws RepositoryResourceException, RepositoryBackendException {
        Requirement requirement = new InstallableEntityRequirement("foo", InstallableEntityIdentityConstants.TYPE_FEATURE);
        List<Resource> empty = Collections.emptyList();
        EsaResourceImpl resourceToMatch = new EsaResourceImpl(getConnection());
        resourceToMatch.setProvideFeature("foo");
        resourceToMatch.uploadToMassive(new BasicTestStrategy());

        List<Resource> repoResources = new ArrayList<Resource>();
        RepositoryResolveContext testObject = new RepositoryResolveContext(null, null, empty, empty, repoResources, new RepositoryConnectionList(getConnection()));
        List<Capability> foundCapabilities = testObject.findProviders(requirement);
        assertEquals("There should be one match to the repo capability", 1, foundCapabilities.size());
        assertEquals("The repo resource should have been added to the list of repo resources", 1, repoResources.size());
        assertEquals("The match should be to the repo capability", repoResources.get(0).getCapabilities(null).get(0), foundCapabilities.get(0));
    }

    /**
     * Tests that if no matching sample is found then it will be loaded from the repository
     *
     * @throws RepositoryBackendException
     * @throws RepositoryResourceException
     */
    @Test
    public void testSampleFromRepo() throws RepositoryResourceException, RepositoryBackendException {
        Requirement requirement = new InstallableEntityRequirement(NameAttributes.SHORT_NAME, "foo", null, InstallableEntityIdentityConstants.TYPE_SAMPLE);
        List<Resource> empty = Collections.emptyList();
        com.ibm.ws.repository.resources.internal.SampleResourceImpl resourceToMatch = new com.ibm.ws.repository.resources.internal.SampleResourceImpl(getConnection());
        resourceToMatch.setShortName("foo");
        resourceToMatch.setType(ResourceType.PRODUCTSAMPLE);
        resourceToMatch.uploadToMassive(new RepositoryResolverTest.BasicTestStrategy());

        List<Resource> repoResources = new ArrayList<Resource>();
        RepositoryResolveContext testObject = new RepositoryResolveContext(null, null, empty, empty, repoResources, new RepositoryConnectionList(getConnection()));
        List<Capability> foundCapabilities = testObject.findProviders(requirement);
        assertEquals("There should be one match to the repo capability", 1, foundCapabilities.size());
        assertEquals("The repo resource should have been added to the list of repo resources", 1, repoResources.size());
        assertEquals("The match should be to the repo capability", repoResources.get(0).getCapabilities(null).get(0), foundCapabilities.get(0));
    }

}
