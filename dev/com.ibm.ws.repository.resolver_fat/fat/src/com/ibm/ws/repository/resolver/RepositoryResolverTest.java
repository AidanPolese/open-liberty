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
package com.ibm.ws.repository.resolver;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.osgi.service.resolver.ResolutionException;

import com.ibm.ws.kernel.feature.provisioning.ProvisioningFeatureDefinition;
import com.ibm.ws.kernel.productinfo.ProductInfo;
import com.ibm.ws.kernel.productinfo.ProductInfoParseException;
import com.ibm.ws.product.utility.extension.ifix.xml.IFixInfo;
import com.ibm.ws.product.utility.extension.ifix.xml.UpdatedFile;
import com.ibm.ws.repository.base.NoRepoAvailableException;
import com.ibm.ws.repository.base.servers.DirectoryConnectionRule;
import com.ibm.ws.repository.base.servers.LarsConnectionRule;
import com.ibm.ws.repository.base.servers.MassiveConnectionRule;
import com.ibm.ws.repository.base.servers.RestRepositoryConnectionRule;
import com.ibm.ws.repository.common.enums.InstallPolicy;
import com.ibm.ws.repository.common.enums.ResourceType;
import com.ibm.ws.repository.connections.ProductDefinition;
import com.ibm.ws.repository.connections.RepositoryConnection;
import com.ibm.ws.repository.connections.RepositoryConnectionList;
import com.ibm.ws.repository.connections.SimpleProductDefinition;
import com.ibm.ws.repository.connections.liberty.ProductInfoProductDefinition;
import com.ibm.ws.repository.exceptions.RepositoryBackendException;
import com.ibm.ws.repository.exceptions.RepositoryException;
import com.ibm.ws.repository.exceptions.RepositoryResourceException;
import com.ibm.ws.repository.resolver.RepositoryResolutionException.MissingRequirement;
import com.ibm.ws.repository.resolver.internal.resource.LpmResource;
import com.ibm.ws.repository.resolver.ResolverTestUtils;
import com.ibm.ws.repository.resources.RepositoryResource;
import com.ibm.ws.repository.resources.internal.EsaResourceImpl;
import com.ibm.ws.repository.resources.internal.IfixResourceImpl;
import com.ibm.ws.repository.resources.internal.RepositoryResourceImpl;
import com.ibm.ws.repository.resources.internal.SampleResourceImpl;
import com.ibm.ws.repository.strategies.writeable.BaseStrategy;

/**
 * Tests for {@link RepositoryResolver}
 */
public class RepositoryResolverTest {

    private int _count = 0;

    private RepositoryConnectionList _loginInfoList;

    @Rule
    public RestRepositoryConnectionRule connection;

    private RepositoryConnection getConnection() {
        return connection.getConnection();
    }

    public static class RepositoryResolverTestMassive extends RepositoryResolverTest {
        public RepositoryResolverTestMassive() {
            connection = new MassiveConnectionRule(FATSuite.massiveResource);
        }
    }

    public static class RepositoryResolverTestLars extends RepositoryResolverTest {
        public RepositoryResolverTestLars() {
            if (FATSuite.larsResource != null) {
                connection = new LarsConnectionRule(FATSuite.larsResource);
            }
        }
    }

    public static class RepositoryResolverTestDirectory extends RepositoryResolverTest {
        public RepositoryResolverTestDirectory() {
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

    @Before
    public void setupLoginInfo() throws NoRepoAvailableException {
        // Unlike most tests resolver wants to use a collection of LoginInfoResources so create one and use that instead
        if (connection != null) {
            _loginInfoList = new RepositoryConnectionList(getConnection());
        }
    }

    /**
     * Test to make sure a single feature can be obtained from Massive by symbolic name
     *
     * @throws Throwable
     */
    @Test
    public void testSingleFeatureBySymbolicName() throws Throwable {
        // Add a test resource to massive
        String symbolicName = "com.ibm.ws.test-1.0";
        EsaResourceImpl testResource = createEsaResource(symbolicName, null, null);

        // Now see if we can resolve it!
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(symbolicName);
        assertEquals("There should only be a single list of resources", 1, resolvedResources.size());
        assertEquals("There should be one resolved resource", 1, resolvedResources.iterator().next().size());
        assertTrue("The resolved resource should be the one we supplied", resolvedResources.iterator().next().contains(testResource));
    }

    /**
     * Test to make sure a single feature can be obtained from Massive by short name
     *
     * @throws ResolutionException
     * @throws RepositoryException
     */
    @Test
    public void testSingleFeatureByShortName() throws RepositoryException, ResolutionException {
        String symbolicName = "com.ibm.ws.test-1.0";
        String shortName = "test-1.0";
        EsaResourceImpl testResource = createEsaResource(symbolicName, shortName, null);

        // Now see if we can resolve it!
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(shortName);
        assertEquals("There should only be a single list of resources", 1, resolvedResources.size());
        assertEquals("There should be one resolved resource", 1, resolvedResources.iterator().next().size());
        assertTrue("The resolved resource should be the one we supplied", resolvedResources.iterator().next().contains(testResource));
    }

    /**
     * Test to make sure a single feature can be obtained from Massive by short name with the wrong case
     *
     * @throws ResolutionException
     * @throws RepositoryException
     */
    @Test
    public void testSingleFeatureByWrongCasedShortName() throws RepositoryException, ResolutionException {
        String symbolicName = "com.ibm.ws.test-1.0";
        String shortName = "teST-1.0";
        EsaResourceImpl testResource = createEsaResource(symbolicName, shortName, null);

        // Now see if we can resolve it!
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve("TEst-1.0");
        assertEquals("There should only be a single list of resources", 1, resolvedResources.size());
        assertEquals("There should be one resolved resource", 1, resolvedResources.iterator().next().size());
        assertTrue("The resolved resource should be the one we supplied", resolvedResources.iterator().next().contains(testResource));
    }

    /**
     * Test to make sure that if a feature is already installed you get an empty collection back.
     *
     * @throws RepositoryException
     */
    @Test
    public void testFeatureThatIsAlreadyInstalled() throws RepositoryException {
        String symbolicName = "com.ibm.ws.test-1.0";
        Mockery mockery = new Mockery();
        ProvisioningFeatureDefinition mockFeatureDefinition = ResolverTestUtils.mockSimpleFeatureDefinition(mockery, symbolicName, null, null);

        // Now see if we can resolve it!
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.singleton(mockFeatureDefinition), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(symbolicName);
        assertEquals("The resource is already installed so should not of been returned", 0, resolvedResources.size());

        // Make sure the mockery was happy
        mockery.assertIsSatisfied();
    }

    /**
     * Test to make sure a single feature can be obtained from Massive by symbolic name and version
     *
     * @throws ResolutionException
     * @throws RepositoryException
     */
    @Test
    public void testSingleFeatureBySymbolicNameAndVersion() throws RepositoryException, ResolutionException {
        // Add two test resources to massive, we want to make sure we get the right one
        String symbolicName = "com.ibm.ws.test-1.0";
        String correctVersion = "1.0.0.0";
        createEsaResource(symbolicName, null, "1.0.0.1");
        EsaResourceImpl correctResource = createEsaResource(symbolicName, null, correctVersion);

        // Now see if we can resolve it!
        String toResolve = symbolicName + "/" + correctVersion;
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(toResolve);
        assertEquals("There should only be a single list of resources", 1, resolvedResources.size());
        assertEquals("There should be one resolved resource", 1, resolvedResources.iterator().next().size());
        assertTrue("The resolved resource should be the one we supplied at the right version", resolvedResources.iterator().next().contains(correctResource));
    }

    /**
     * Test a feature dependency in the repo
     */
    @Test
    public void testFeatureDependencyInRepo() throws RepositoryException, ResolutionException {
        // Add two test resources to massive, we want to make sure they both are returned in the right order
        String firstSymbolicName = "com.ibm.ws.test-1.0";
        String secondSymbolicName = "com.ibm.ws.test.dep-1.0";
        EsaResourceImpl firstResource = createEsaResource(firstSymbolicName, null, null, Collections.singleton(secondSymbolicName), null);
        EsaResourceImpl dependencyResource = createEsaResource(secondSymbolicName, null, "1.0.0.1");

        // Now see if we can resolve it!
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(firstSymbolicName);
        assertEquals("There should only be a single list of resources", 1, resolvedResources.size());
        assertEquals("There should be two resolved resource", 2, resolvedResources.iterator().next().size());
        assertEquals("The dependency should be installed first", dependencyResource, resolvedResources.iterator().next().get(0));
        assertEquals("The resource being resolved should be installed second", firstResource, resolvedResources.iterator().next().get(1));
    }

    /**
     * Test a feature dependency in the install
     */
    @Test
    public void testFeatureDependencyInInstall() throws RepositoryException, ResolutionException {
        // Add one test resource to massive, but make a dependency to an already installed feature
        String firstSymbolicName = "com.ibm.ws.test-1.0";
        String secondSymbolicName = "com.ibm.ws.test.dep-1.0";
        EsaResourceImpl firstResource = createEsaResource(firstSymbolicName, null, null, Collections.singleton(secondSymbolicName), null);
        Mockery mockery = new Mockery();
        ProvisioningFeatureDefinition mockFeatureDefinition = ResolverTestUtils.mockSimpleFeatureDefinition(mockery, secondSymbolicName, null, null);

        // Now see if we can resolve it!
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.singleton(mockFeatureDefinition), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(firstSymbolicName);
        assertEquals("There should only be a single list of resources", 1, resolvedResources.size());
        assertEquals("There should be one resolved resource", 1, resolvedResources.iterator().next().size());
        assertEquals("The resource being resolved should be the one in the list", firstResource, resolvedResources.iterator().next().get(0));

        // Make sure the mockery was happy
        mockery.assertIsSatisfied();
    }

    /**
     * Test when there are two features with the same symbolic name that the latest one is picked
     */
    @Test
    public void testFeaturePicked() throws RepositoryException, ResolutionException {
        // Add two test resources to massive, we want to make sure we get the right one
        String symbolicName = "com.ibm.ws.test-1.0";
        createEsaResource(symbolicName, null, "1.0.0.0");
        EsaResourceImpl secondResource = createEsaResource(symbolicName, null, "1.0.0.1");

        // Now see if we can resolve it!
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(symbolicName);
        assertEquals("There should only be a single list of resources", 1, resolvedResources.size());
        assertEquals("There should be one resolved resource", 1, resolvedResources.iterator().next().size());
        assertTrue("The resolved resource should be the one with the latest version", resolvedResources.iterator().next().contains(secondResource));
    }

    /**
     * Test when there is a feature dependency installed and in the repo then the one in the install is picked
     */
    @Test
    public void testFeatureDependencyInInstallPicked() throws RepositoryException, ResolutionException {
        // Add two test resources to massive, and one to the install the resolver should not return the dependency feature from Massive even if it's a better version
        String firstSymbolicName = "com.ibm.ws.test-1.0";
        String secondSymbolicName = "com.ibm.ws.test.dep-1.0";
        EsaResourceImpl firstResource = createEsaResource(firstSymbolicName, null, null, Collections.singleton(secondSymbolicName), null);
        createEsaResource(secondSymbolicName, null, "1.0.0.1");
        Mockery mockery = new Mockery();
        ProvisioningFeatureDefinition mockFeatureDefinition = ResolverTestUtils.mockSimpleFeatureDefinition(mockery, secondSymbolicName, null, "1.0.0.0");

        // Now see if we can resolve it!
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.singleton(mockFeatureDefinition), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(firstSymbolicName);
        assertEquals("There should only be a single list of resources", 1, resolvedResources.size());
        assertEquals("There should be one resolved resource", 1, resolvedResources.iterator().next().size());
        assertEquals("The resource being resolved should be the one in the list", firstResource, resolvedResources.iterator().next().get(0));

        // Make sure the mockery was happy
        mockery.assertIsSatisfied();
    }

    /**
     * Test applies to is used to work out the best feature to install
     *
     * @throws ProductInfoParseException
     * @throws IOException
     */
    @Test
    public void testFeatureWithAppliesToFilters() throws RepositoryException, ResolutionException, IOException, ProductInfoParseException {
        // Add two test resources to massive, we want to make sure we get the right one, even though the second one is a higher number the first should be picked as it applies to the correct product
        String symbolicName = "com.ibm.ws.test-1.0";
        createEsaResource(symbolicName, null, "1.0.0.1", null, "com.ibm.ws.test.product; productVersion=5.0.0.1; productEdition=DEVELOPERS");
        EsaResourceImpl correctResource = createEsaResource(symbolicName, null, "1.0.0.0", null, "com.ibm.ws.test.product; productVersion=5.0.0.0; productEdition=DEVELOPERS");

        ProductInfo productInfo = ResolverTestUtils.createProductInfo("com.ibm.ws.test.product", "DEVELOPERS", "5.0.0.0", null, null);

        // Now see if we can resolve it!
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> singleton(new ProductInfoProductDefinition(productInfo)), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(symbolicName);
        assertEquals("There should only be a single list of resources", 1, resolvedResources.size());
        assertEquals("There should be one resolved resource", 1, resolvedResources.iterator().next().size());
        assertTrue("The resolved resource should be the one with the correct applies to", resolvedResources.iterator().next().contains(correctResource));
    }

    /**
     * Tests that if you have a dependency in the install and a different dependency in the repo then it all works ok
     */
    @Test
    public void testFeatureDependencyInInstallAndRepo() throws RepositoryException, ResolutionException {
        // Add two test resources to massive, and one different dependency to the install the resolver should return the dependency feature from Massive
        String firstSymbolicName = "com.ibm.ws.test-1.0";
        String secondSymbolicName = "com.ibm.ws.test.dep-1.0";
        String thirdSymbolicName = "com.ibm.ws.test.second.dep-1.0";
        Collection<String> dependencies = new HashSet<String>();
        dependencies.add(secondSymbolicName);
        dependencies.add(thirdSymbolicName);
        EsaResourceImpl firstResource = createEsaResource(firstSymbolicName, null, null, dependencies, null);
        EsaResourceImpl dependencyInMassive = createEsaResource(secondSymbolicName, null, null);
        Mockery mockery = new Mockery();
        ProvisioningFeatureDefinition mockFeatureDefinition = ResolverTestUtils.mockSimpleFeatureDefinition(mockery, thirdSymbolicName, null, null);

        // Now see if we can resolve it!
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.singleton(mockFeatureDefinition), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(firstSymbolicName);
        assertEquals("There should only be a single list of resources", 1, resolvedResources.size());
        assertEquals("There should be two resolved resource", 2, resolvedResources.iterator().next().size());
        assertEquals("The dependency in massive should be installed first", dependencyInMassive, resolvedResources.iterator().next().get(0));
        assertEquals("The resource being resolved should be installed last", firstResource, resolvedResources.iterator().next().get(1));

        // Make sure the mockery was happy
        mockery.assertIsSatisfied();
    }

    /**
     * Tests that when you have a chain of feature dependencies then they are all returned and in the right order
     */
    @Test
    public void testChainedFeatureDependencies() throws RepositoryException, ResolutionException {
        // Add three test resources in a chain to massive
        String firstSymbolicName = "com.ibm.ws.test-1.0";
        String secondSymbolicName = "com.ibm.ws.test.dep-1.0";
        String thirdSymbolicName = "com.ibm.ws.test.second.dep-1.0";
        EsaResourceImpl firstResource = createEsaResource(firstSymbolicName, null, null, Collections.singleton(secondSymbolicName), null);
        EsaResourceImpl firstDependency = createEsaResource(secondSymbolicName, null, null, Collections.singleton(thirdSymbolicName), null);
        EsaResourceImpl secondDependency = createEsaResource(thirdSymbolicName, null, null);

        // Now see if we can resolve it!
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(firstSymbolicName);
        assertEquals("There should only be a single list of resources", 1, resolvedResources.size());
        assertEquals("There should be three resolved resource", 3, resolvedResources.iterator().next().size());
        assertEquals("The final dependency in massive should be installed first", secondDependency, resolvedResources.iterator().next().get(0));
        assertEquals("The first dependency in massive should be installed second", firstDependency, resolvedResources.iterator().next().get(1));
        assertEquals("The resource being resolved should be installed last", firstResource, resolvedResources.iterator().next().get(2));
    }

    /**
     * Tests that when you have a chain of feature dependencies with multiple features depending on the end of the chain then the install order is still correct
     */
    @Test
    public void testChainedFeatureDependenciesWithMultipleRoutes() throws RepositoryException, ResolutionException {
        // Add four test resources in a chain to massive with the final resource having two routes to it
        String firstSymbolicName = "com.ibm.ws.test-1.0";
        String secondSymbolicName = "com.ibm.ws.test.dep-1.0";
        String thirdSymbolicName = "com.ibm.ws.test.second.dep-1.0";
        String fourthSymbolicName = "com.ibm.ws.test.third.dep-1.0";
        Collection<String> dependencies = new HashSet<String>();
        dependencies.add(secondSymbolicName);
        dependencies.add(thirdSymbolicName);
        EsaResourceImpl firstResource = createEsaResource(firstSymbolicName, null, null, dependencies, null);
        EsaResourceImpl firstDependency = createEsaResource(secondSymbolicName, null, null, Collections.singleton(fourthSymbolicName), null);
        EsaResourceImpl secondDependency = createEsaResource(thirdSymbolicName, null, null, Collections.singleton(fourthSymbolicName), null);
        EsaResourceImpl finalDependency = createEsaResource(fourthSymbolicName, null, null);

        // Now see if we can resolve it!
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(firstSymbolicName);
        assertEquals("There should only be a single list of resources", 1, resolvedResources.size());
        assertEquals("There should be four resolved resource", 4, resolvedResources.iterator().next().size());
        assertEquals("The final dependency in massive should be installed first", finalDependency, resolvedResources.iterator().next().get(0));
        assertEquals("The resource being resolved should be installed last", firstResource, resolvedResources.iterator().next().get(3));

        // The order of the middle two doesn't matter as long as they are in the middle somewhere
        assertTrue("The first dependency should be in the resolved list", resolvedResources.iterator().next().contains(firstDependency));
        assertTrue("The second dependency should be in the resolved list", resolvedResources.iterator().next().contains(secondDependency));
    }

    /**
     * Test an iFix dependency in the repo
     */
    @Test
    public void testFixDependencyInRepo() throws RepositoryException, ResolutionException {
        // Add a test resource to massive with a dependency on an iFix
        String symbolicName = "com.ibm.ws.test-1.0";
        String fixId = "PM00001";
        EsaResourceImpl testResource = createEsaResource(symbolicName, Collections.singleton(fixId));
        IfixResourceImpl iFixResource = createIFixResource(fixId, null, null);

        // Now see if we can resolve it!
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(symbolicName);
        assertEquals("There should only be a single list of resources", 1, resolvedResources.size());
        assertEquals("There should be two resolved resources", 2, resolvedResources.iterator().next().size());
        assertEquals("The iFix needs to be installed first", iFixResource, resolvedResources.iterator().next().get(0));
        assertEquals("The feature should be installed second", testResource, resolvedResources.iterator().next().get(1));
    }

    /**
     * Test an iFix dependency in the install
     */
    @Test
    public void testFixDependencyInInstall() throws RepositoryException, ResolutionException {
        // Add a test resource to massive with a dependency on an iFix that is already installed
        String symbolicName = "com.ibm.ws.test-1.0";
        String fixId = "PM00001";
        EsaResourceImpl testResource = createEsaResource(symbolicName, Collections.singleton(fixId));

        IFixInfo iFixInfo = new IFixInfo(null, null, Collections.singleton(fixId), null, null, null, null);

        // Now see if we can resolve it!
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.singleton(iFixInfo), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(symbolicName);
        assertEquals("There should only be a single list of resources", 1, resolvedResources.size());
        assertEquals("There should be one resolved resources", 1, resolvedResources.iterator().next().size());
        assertEquals("The feature should be in the resolved list", testResource, resolvedResources.iterator().next().get(0));
    }

    /**
     * Test when there are two iFixes for the same fix that the latest one is picked
     */
    @Test
    public void testBestIFixPicked() throws RepositoryException, ResolutionException {
        // Add a test resource to massive with a dependency on an iFix but where there are two fixes to the same iFix
        String symbolicName = "com.ibm.ws.test-1.0";
        String fixId = "PM00001";
        EsaResourceImpl testResource = createEsaResource(symbolicName, Collections.singleton(fixId));
        createIFixResource(fixId, null, new Date(1));
        IfixResourceImpl newIFixResource = createIFixResource(fixId, null, new Date(10000000));

        // Now see if we can resolve it!
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(symbolicName);
        assertEquals("There should only be a single list of resources", 1, resolvedResources.size());
        assertEquals("There should be two resolved resources", 2, resolvedResources.iterator().next().size());
        assertEquals("The new iFix needs to be installed first", newIFixResource, resolvedResources.iterator().next().get(0));
        assertEquals("The feature should be installed second", testResource, resolvedResources.iterator().next().get(1));
    }

    /**
     * Test when there are two iFixes for the same fix that the one with a date set is picked over a null value
     */
    @Test
    public void testIFixWithDateSetPicked() throws RepositoryException, ResolutionException {
        // Add a test resource to massive with a dependency on an iFix but where there are two fixes to the same iFix
        String symbolicName = "com.ibm.ws.test-1.0";
        String fixId = "PM00001";
        EsaResourceImpl testResource = createEsaResource(symbolicName, Collections.singleton(fixId));
        createIFixResource(fixId, null, null);
        IfixResourceImpl newIFixResource = createIFixResource(fixId, null, new Date(1));

        // Now see if we can resolve it!
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(symbolicName);
        assertEquals("There should only be a single list of resources", 1, resolvedResources.size());
        assertEquals("There should be two resolved resources", 2, resolvedResources.iterator().next().size());
        assertEquals("The new iFix needs to be installed first", newIFixResource, resolvedResources.iterator().next().get(0));
        assertEquals("The feature should be installed second", testResource, resolvedResources.iterator().next().get(1));
    }

    /**
     * Test when there is an iFix dependency installed and in the repo then the one in the install is picked
     */
    @Test
    public void testFixDependencyInInstallPicked() throws RepositoryException, ResolutionException {
        // Add a test resource to massive with a dependency on an iFix that is in the install and massive and make sure that it picks the install one even when the massive one is later
        String symbolicName = "com.ibm.ws.test-1.0";
        String fixId = "PM00001";
        EsaResourceImpl testResource = createEsaResource(symbolicName, Collections.singleton(fixId));
        createIFixResource(fixId, null, new Date(10000000));

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        UpdatedFile updatedFile = new UpdatedFile(null, 0, dateFormat.format(new Date(0)), null);
        IFixInfo iFixInfo = new IFixInfo(null, null, Collections.singleton(fixId), null, null, null, Collections.singleton(updatedFile));

        // Now see if we can resolve it!
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.singleton(iFixInfo), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(symbolicName);
        assertEquals("There should only be a single list of resources", 1, resolvedResources.size());
        assertEquals("There should be one resolved resources", 1, resolvedResources.iterator().next().size());
        assertEquals("The feature should be in the resolved list", testResource, resolvedResources.iterator().next().get(0));
    }

    /**
     * Test applies to is used to work out the best fix to install
     *
     * @throws ProductInfoParseException
     * @throws IOException
     */
    @Test
    public void testFixWithAppliesToFilters() throws RepositoryException, ResolutionException, IOException, ProductInfoParseException {
        // Add a test resource to massive with a dependency on an iFix but where there are two fixes to the same iFix
        String symbolicName = "com.ibm.ws.test-1.0";
        String fixId = "PM00001";
        EsaResourceImpl testResource = createEsaResource(symbolicName, null, null, null, "com.ibm.ws.test.product", null, false, Collections.singleton(fixId));

        createIFixResource(fixId, "com.ibm.ws.test.product; productVersion=5.0.0.1; productEdition=DEVELOPERS", new Date(10000000));
        IfixResourceImpl correctIFix = createIFixResource(fixId, "com.ibm.ws.test.product; productVersion=5.0.0.0; productEdition=DEVELOPERS", new Date(0));

        // Now see if we can resolve it!
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> singleton(new SimpleProductDefinition("com.ibm.ws.test.product", "5.0.0.0", null, null, "DEVELOPERS")), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(symbolicName);
        assertEquals("There should only be a single list of resources", 1, resolvedResources.size());
        assertEquals("There should be two resolved resources", 2, resolvedResources.iterator().next().size());
        assertEquals("The iFix needs to be installed first", correctIFix, resolvedResources.iterator().next().get(0));
        assertEquals("The feature should be installed second", testResource, resolvedResources.iterator().next().get(1));
    }

    /**
     * Tests that if you have an IFix dependency in the install and a different dependency in the repo then it all works ok
     */
    @Test
    public void testFixDependencyInInstallAndRepo() throws RepositoryException, ResolutionException {
        // Create a feature with two iFix dependencies, one in massive, the other in the install
        String symbolicName = "com.ibm.ws.test-1.0";
        String fix1Id = "PM00001";
        String fix2Id = "PM00002";
        Collection<String> fixIds = new HashSet<String>();
        fixIds.add(fix1Id);
        fixIds.add(fix2Id);
        EsaResourceImpl testResource = createEsaResource(symbolicName, fixIds);
        IfixResourceImpl iFixResource = createIFixResource(fix1Id, null, null);

        IFixInfo iFixInfo = new IFixInfo(null, null, Collections.singleton(fix2Id), null, null, null, null);

        // Now see if we can resolve it!
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.singleton(iFixInfo), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(symbolicName);
        assertEquals("There should only be a single list of resources", 1, resolvedResources.size());
        assertEquals("There should be two resolved resources", 2, resolvedResources.iterator().next().size());
        assertEquals("The iFix should be installed first", iFixResource, resolvedResources.iterator().next().get(0));
        assertEquals("The feature should be installed second", testResource, resolvedResources.iterator().next().get(1));

    }

    /**
     * Tests that if you have multiple IFix dependencies in the repo then it all works ok
     */
    @Test
    public void testMultipleFixDependencyInRepo() throws RepositoryException, ResolutionException {
        // Create a feature with two iFix dependencies, one in massive, the other in the install
        String symbolicName = "com.ibm.ws.test-1.0";
        String fix1Id = "PM00001";
        String fix2Id = "PM00002";
        Collection<String> fixIds = new HashSet<String>();
        fixIds.add(fix1Id);
        fixIds.add(fix2Id);
        EsaResourceImpl testResource = createEsaResource(symbolicName, fixIds);
        IfixResourceImpl iFixResource = createIFixResource(fix1Id, null, null);
        IfixResourceImpl iFix2Resource = createIFixResource(fix2Id, null, null);

        // Now see if we can resolve it!
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(symbolicName);
        assertEquals("There should only be a single list of resources", 1, resolvedResources.size());
        List<RepositoryResource> orderedResourceList = resolvedResources.iterator().next();
        assertEquals("There should be three resolved resources", 3, orderedResourceList.size());
        assertEquals("The feature should be installed last", testResource, resolvedResources.iterator().next().get(2));
        assertTrue("The iFix should be installed before the feature", orderedResourceList.contains(iFixResource));
        assertTrue("The second iFix should be installed before the feature", orderedResourceList.contains(iFix2Resource));

    }

    /**
     * Tests that when you have a chain of feature dependencies that each have a required fix then they are all returned and in the right order, the features at each level should
     * always be installed first
     */
    @Test
    public void testChainedFixDependencies() throws RepositoryException, ResolutionException {
        // Kitchen sink test, specifically testing that iFix and feature dependencies at the same level are returned in the right order
        String firstSymbolicName = "com.ibm.ws.test-1.0";
        String secondSymbolicName = "com.ibm.ws.test.dep-1.0";
        String thirdSymbolicName = "com.ibm.ws.test.second.dep-1.0";
        String fourthSymbolicName = "com.ibm.ws.test.fourth.dep-1.0";
        String fix1Id = "PM00001";
        String fix2Id = "PM00002";
        String fix3Id = "PM00003";
        EsaResourceImpl firstResource = createEsaResource(firstSymbolicName, null, null, Collections.singleton(secondSymbolicName), null, null, false,
                                                          Collections.singleton(fix1Id));
        EsaResourceImpl firstDependency = createEsaResource(secondSymbolicName, null, null, Collections.singleton(thirdSymbolicName), null, null, false,
                                                            Collections.singleton(fix2Id));
        EsaResourceImpl secondDependency = createEsaResource(thirdSymbolicName, null, null, Collections.singleton(fourthSymbolicName), null, null, false,
                                                             Collections.singleton(fix3Id));
        IfixResourceImpl iFix1Resource = createIFixResource(fix1Id, null, null);
        IfixResourceImpl iFix2Resource = createIFixResource(fix2Id, null, null);
        Mockery mockery = new Mockery();
        ProvisioningFeatureDefinition mockFeatureDefinition = ResolverTestUtils.mockSimpleFeatureDefinition(mockery, fourthSymbolicName, null, null);
        IFixInfo iFixInfo = new IFixInfo(null, null, Collections.singleton(fix3Id), null, null, null, null);

        // Now see if we can resolve it!
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.singleton(mockFeatureDefinition), Collections.singleton(iFixInfo), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(firstSymbolicName);
        assertEquals("There should only be a single list of resources", 1, resolvedResources.size());
        assertEquals("There should be five resolved resource", 5, resolvedResources.iterator().next().size());
        assertEquals("The final feature dependency in massive should be installed first", secondDependency, resolvedResources.iterator().next().get(0));
        assertEquals("The final iFix dependency should be installed second (after the feature it might fix)", iFix2Resource, resolvedResources.iterator().next().get(1));
        assertEquals("The middle feature dependency in massive should be installed third", firstDependency, resolvedResources.iterator().next().get(2));
        assertEquals("The middle iFix dependency should be installed fourth (after the feature it might fix)", iFix1Resource, resolvedResources.iterator().next().get(3));
        assertEquals("The resource being resolved should be installed last", firstResource, resolvedResources.iterator().next().get(4));

        mockery.assertIsSatisfied();

    }

    /**
     * Make sure the resolution error message is fairly helpful
     *
     * @throws RepositoryException
     */
    @Test
    public void testResolutionMessage() throws RepositoryException {
        String missingSymbolicName = "does.not.exist";
        try {
            new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList).resolve(missingSymbolicName);
            fail("The resource does not exist so should not resolve");
        } catch (RepositoryResolutionException e) {
            assertTrue("The resolution message should state what couldn't be resolved but it is: " + e.getMessage(), e.getMessage().contains("resource=" + missingSymbolicName));
            assertEquals("There should only be one top level resource that wasn't resolved", 1, e.getTopLevelFeaturesNotResolved().size());
            assertTrue("The correct top level feature should be listed", e.getTopLevelFeaturesNotResolved().contains(missingSymbolicName));
            assertEquals("Only one feature should not be found", 1, e.getAllRequirementsNotFound().size());
            assertTrue("The exception should say which feature actually wasn't found", e.getAllRequirementsNotFound().contains(missingSymbolicName));
            assertEquals("Only one requirement should not be found", 1, e.getAllRequirementsResourcesNotFound().size());
            MissingRequirement missingRequirement = e.getAllRequirementsResourcesNotFound().iterator().next();
            assertEquals("The exception should say which requirement actually wasn't found and it should be the requirement on the feature itself "
                         + e.getAllRequirementsResourcesNotFound(), missingSymbolicName, missingRequirement.requirementName);
            assertNull("The exception should say there is no resource that owned the requirement that wasn't found "
                       + e.getAllRequirementsResourcesNotFound(), missingRequirement.owningResource);
            assertTrue("The exception should not have any missing products listed", e.getMissingProducts().isEmpty());
            assertNull("The exception should not have a minimum product value", e.getMinimumVersionForMissingProduct(null, null, null));
            assertNull("The exception should not have a maximum product value", e.getMaximumVersionForMissingProduct(null, null, null));
        }
    }

    /**
     * Test to make sure if it isn't a top level feature that is missing you get the right message
     */
    @Test
    public void testResolutionMessageDeepFeatureMissing() throws RepositoryException {
        String firstSymbolicName = "com.ibm.ws.test-1.0";
        String secondSymbolicName = "com.ibm.ws.test.dep-1.0";
        String thirdSymbolicName = "com.ibm.ws.test.other.dep-1.0";
        createEsaResource(firstSymbolicName, null, null, Collections.singleton(secondSymbolicName), null);
        RepositoryResourceImpl secondResource = createEsaResource(secondSymbolicName, null, "1.0.0.1", Collections.singleton(thirdSymbolicName), null);
        try {
            new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList).resolve(Collections.singleton(firstSymbolicName));
            fail("The resource does not exist so should not resolve");
        } catch (RepositoryResolutionException e) {
            assertTrue("The resolution message should state the top level feature that couldn't be resolved but it is: " + e.getMessage(),
                       e.getMessage().contains("resource=" + firstSymbolicName));
            assertEquals("There should only be one top level resource that wasn't resolved", 1, e.getTopLevelFeaturesNotResolved().size());
            assertTrue("The correct top level feature should be listed", e.getTopLevelFeaturesNotResolved().contains(firstSymbolicName));
            assertEquals("Only one feature should not be found", 1, e.getAllRequirementsNotFound().size());
            assertThat("The exception should say which feature actually wasn't found", e.getAllRequirementsNotFound(), contains(thirdSymbolicName));
            assertEquals("Only one requirement should not be found", 1, e.getAllRequirementsResourcesNotFound().size());
            MissingRequirement missingRequirement = e.getAllRequirementsResourcesNotFound().iterator().next();
            assertEquals("The exception should say which requirement actually wasn't found and it should be the requirement on the third feature "
                         + e.getAllRequirementsResourcesNotFound(), thirdSymbolicName, missingRequirement.requirementName);
            assertEquals("The exception should include the resource that owned the requirement that wasn't found "
                         + e.getAllRequirementsResourcesNotFound(), secondResource, missingRequirement.owningResource);
            assertTrue("The exception should not have any missing products listed", e.getMissingProducts().isEmpty());
            assertNull("The exception should not have a minimum product value", e.getMinimumVersionForMissingProduct(null, null, null));
            assertNull("The exception should not have a maximum product value", e.getMaximumVersionForMissingProduct(null, null, null));
        }
    }

    /**
     * Test to make sure if you get further by doing a short name resolution you get the right message
     */
    @Test
    public void testResolutionShortNameTopLevel() throws RepositoryException {
        String firstSymbolicName = "com.ibm.ws.test-1.0";
        String firstShortName = "test-1.0";
        String secondSymbolicName = "com.ibm.ws.test.dep-1.0";
        RepositoryResourceImpl resource = createEsaResource(firstSymbolicName, firstShortName, null, Collections.singleton(secondSymbolicName), null);
        try {
            new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList).resolve(Collections.singleton(firstShortName));
            fail("The resource does not exist so should not resolve");
        } catch (RepositoryResolutionException e) {
            assertTrue("The resolution message should state the top level feature that couldn't be resolved but it is: " + e.getMessage(),
                       e.getMessage().contains("resource=" + firstShortName));
            assertEquals("There should only be one top level resource that wasn't resolved", 1, e.getTopLevelFeaturesNotResolved().size());
            assertTrue("The correct top level feature should be listed", e.getTopLevelFeaturesNotResolved().contains(firstShortName));
            assertEquals("Only one feature should not be found", 1, e.getAllRequirementsNotFound().size());
            assertTrue("The exception should say which feature actually wasn't found", e.getAllRequirementsNotFound().contains(secondSymbolicName));
            assertEquals("Only one requirement should not be found", 1, e.getAllRequirementsResourcesNotFound().size());
            MissingRequirement missingRequirement = e.getAllRequirementsResourcesNotFound().iterator().next();
            assertEquals("The exception should say which requirement actually wasn't found and it should be the requirement on the second feature "
                         + e.getAllRequirementsResourcesNotFound(), secondSymbolicName, missingRequirement.requirementName);
            assertEquals("The exception should include the resource that owned the requirement that wasn't found "
                         + e.getAllRequirementsResourcesNotFound(), resource, missingRequirement.owningResource);
            assertTrue("The exception should not have any missing products listed", e.getMissingProducts().isEmpty());
            assertNull("The exception should not have a minimum product value", e.getMinimumVersionForMissingProduct(null, null, null));
            assertNull("The exception should not have a maximum product value", e.getMaximumVersionForMissingProduct(null, null, null));
        }
    }

    /**
     * Test to make sure if you get further by doing a case insensitive short name resolution you get the right message
     */
    @Test
    public void testResolutionCaseInsensitiveShortNameTopLevel() throws RepositoryException {
        String firstSymbolicName = "com.ibm.ws.test-1.0";
        String firstShortName = "teST-1.0";
        String otherCaseShortName = "TEst-1.0";
        String secondSymbolicName = "com.ibm.ws.test.dep-1.0";
        RepositoryResourceImpl resource = createEsaResource(firstSymbolicName, firstShortName, null, Collections.singleton(secondSymbolicName), null);
        try {
            new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList).resolve(Collections.singleton(otherCaseShortName));
            fail("The resource does not exist so should not resolve");
        } catch (RepositoryResolutionException e) {
            assertTrue("The resolution message should state the top level feature that couldn't be resolved but it is: " + e.getMessage(),
                       e.getMessage().contains("resource=" + otherCaseShortName));
            assertEquals("There should only be one top level resource that wasn't resolved", 1, e.getTopLevelFeaturesNotResolved().size());
            assertTrue("The correct top level feature should be listed but was: " + e.getTopLevelFeaturesNotResolved(),
                       e.getTopLevelFeaturesNotResolved().contains(otherCaseShortName));
            assertEquals("Only one feature should not be found", 1, e.getAllRequirementsNotFound().size());
            assertThat("The exception should say which feature actually wasn't found", e.getAllRequirementsNotFound(), contains(secondSymbolicName));
            assertEquals("Only one requirement should not be found", 1, e.getAllRequirementsResourcesNotFound().size());
            MissingRequirement missingRequirement = e.getAllRequirementsResourcesNotFound().iterator().next();
            assertEquals("The exception should say which requirement actually wasn't found and it should be the requirement on the second feature "
                         + e.getAllRequirementsResourcesNotFound(), secondSymbolicName, missingRequirement.requirementName);
            assertEquals("The exception should include the resource that owned the requirement that wasn't found "
                         + e.getAllRequirementsResourcesNotFound(), resource, missingRequirement.owningResource);
            assertTrue("The exception should not have any missing products listed", e.getMissingProducts().isEmpty());
            assertNull("The exception should not have a minimum product value", e.getMinimumVersionForMissingProduct(null, null, null));
            assertNull("The exception should not have a maximum product value", e.getMaximumVersionForMissingProduct(null, null, null));
        }
    }

    /**
     * Make sure that iFixes cannot be directly resolved. Note this is a largely artificial restriction added because only feature manager is going to be used instead of a liberty
     * package manager. There is nothing intrinsic in the resolver that stops iFixes being resolved except the {@link LpmResource} says it is only looking for features.
     *
     * @throws RepositoryException
     */
    @Test
    public void testFixesDontResolve() throws RepositoryException {
        String fixId = "PM00001";
        createIFixResource(fixId, null, null);
        try {
            new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList).resolve(fixId);
            fail("The resource being searched for is an IFix so shouldn't resolve");
        } catch (RepositoryResolutionException e) {
            assertTrue("The resolution message should state what couldn't be resolved but it is: " + e.getMessage(), e.getMessage().contains("resource=" + fixId));
            assertEquals("There should only be one top level resource that wasn't resolved", 1, e.getTopLevelFeaturesNotResolved().size());
            assertTrue("The correct top level feature should be listed", e.getTopLevelFeaturesNotResolved().contains(fixId));
            assertEquals("Only one feature should not be found", 1, e.getAllRequirementsNotFound().size());
            assertTrue("The exception should say which feature actually wasn't found", e.getAllRequirementsNotFound().contains(fixId));
            assertEquals("Only one requirement should not be found", 1, e.getAllRequirementsResourcesNotFound().size());
            MissingRequirement missingRequirement = e.getAllRequirementsResourcesNotFound().iterator().next();
            assertEquals("The exception should say which requirement actually wasn't found and it should be the requirement on the fix "
                         + e.getAllRequirementsResourcesNotFound(), fixId, missingRequirement.requirementName);
            assertNull("The exception should state that no resource owns the requirement "
                       + e.getAllRequirementsResourcesNotFound(), missingRequirement.owningResource);
            assertTrue("The exception should not have any missing products listed", e.getMissingProducts().isEmpty());
            assertNull("The exception should not have a minimum product value", e.getMinimumVersionForMissingProduct(null, null, null));
            assertNull("The exception should not have a maximum product value", e.getMaximumVersionForMissingProduct(null, null, null));
        }
    }

    /**
     * Test to make sure multiple features can be resolved in a single step.
     *
     * @throws RepositoryException
     * @throws ResolutionException
     */
    @Test
    public void testMultipleFeatures() throws RepositoryException, ResolutionException {
        String firstSymbolicName = "com.ibm.ws.test-1.0";
        String secondSymbolicName = "com.ibm.ws.test.second-1.0";
        EsaResourceImpl firstResource = createEsaResource(firstSymbolicName, null, null);
        EsaResourceImpl secondResource = createEsaResource(secondSymbolicName, null, null);
        Collection<String> namesToResolve = new HashSet<String>();
        namesToResolve.add(firstSymbolicName);
        namesToResolve.add(secondSymbolicName);

        // Now see if we can resolve it!
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(namesToResolve);
        assertEquals("There should be a list of resources for each top level feature", 2, resolvedResources.size());
        Collection<RepositoryResource> allResolvedResources = new ArrayList<RepositoryResource>();
        for (List<RepositoryResource> massiveResourceList : resolvedResources) {
            allResolvedResources.addAll(massiveResourceList);
        }
        assertEquals("There should be two resolved resource", 2, allResolvedResources.size());
        assertTrue("The first resource should be resolve", allResolvedResources.contains(firstResource));
        assertTrue("The second resource should be resolve", allResolvedResources.contains(secondResource));
    }

    /**
     * Test to make sure multiple features with one missing throws an exception
     *
     * @throws RepositoryException
     * @throws ResolutionException
     */
    @Test
    public void testMultipleFeaturesWithOneMissing() throws RepositoryException, ResolutionException {
        String firstSymbolicName = "com.ibm.ws.test-1.0";
        String secondSymbolicName = "com.ibm.ws.test.missing-1.0";
        createEsaResource(firstSymbolicName, null, null);
        Collection<String> namesToResolve = new HashSet<String>();
        namesToResolve.add(firstSymbolicName);
        namesToResolve.add(secondSymbolicName);

        // Now see if we can resolve it!
        try {
            new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList).resolve(namesToResolve);
            fail("Missing the second feature so should have failed to resolve");
        } catch (RepositoryResolutionException e) {
            assertTrue("The exception should contain the info about the missing resource but was: " + e.getMessage(), e.getMessage().contains("resource=" + secondSymbolicName));
            assertEquals("There should only be one top level resource that wasn't resolved", 1, e.getTopLevelFeaturesNotResolved().size());
            assertTrue("The correct top level feature should be listed", e.getTopLevelFeaturesNotResolved().contains(secondSymbolicName));
            assertEquals("Only one feature should not be found", 1, e.getAllRequirementsNotFound().size());
            assertTrue("The exception should say which feature actually wasn't found", e.getAllRequirementsNotFound().contains(secondSymbolicName));
            assertEquals("Only one requirement should not be found", 1, e.getAllRequirementsResourcesNotFound().size());
            MissingRequirement missingRequirement = e.getAllRequirementsResourcesNotFound().iterator().next();
            assertEquals("The exception should say which requirement actually wasn't found and it should be the requirement on the third feature "
                         + e.getAllRequirementsResourcesNotFound(), secondSymbolicName, missingRequirement.requirementName);
            assertNull("The exception should state no resource owned the requirement that wasn't found "
                       + e.getAllRequirementsResourcesNotFound(), missingRequirement.owningResource);
            assertTrue("The exception should not have any missing products listed", e.getMissingProducts().isEmpty());
            assertNull("The exception should not have a minimum product value", e.getMinimumVersionForMissingProduct(null, null, null));
            assertNull("The exception should not have a maximum product value", e.getMaximumVersionForMissingProduct(null, null, null));
        }
    }

    /**
     * Test to make sure we can resolve multiple features using a mix of short and symbolic names
     *
     * @throws RepositoryException
     */
    @Test
    public void testMultipleFeaturesShortAndSymbolic() throws RepositoryException {
        String firstSymbolicName = "com.ibm.ws.testa-1.0";
        String firstShortName = "TestA-1.0";
        String secondSymbolicName = "com.ibm.ws.testb-1.0";
        String secondShortName = "TestB-1.0";
        String thirdSymbolicName = "com.ibm.ws.testc-1.0";
        String thirdShortName = "TestC-1.0";
        EsaResourceImpl firstResource = createEsaResource(firstSymbolicName, firstShortName, null);
        EsaResourceImpl secondResource = createEsaResource(secondSymbolicName, secondShortName, null);
        EsaResourceImpl thirdResource = createEsaResource(thirdSymbolicName, thirdShortName, null);

        Collection<String> namesToResolve = new HashSet<String>();
        namesToResolve.add(firstSymbolicName);
        namesToResolve.add(secondShortName);
        namesToResolve.add(thirdShortName.toLowerCase());

        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(namesToResolve);
        // Flatten the list of resolvedResources
        Set<RepositoryResource> flatResolvedResources = new HashSet<RepositoryResource>();
        for (List<RepositoryResource> massiveResourceList : resolvedResources) {
            flatResolvedResources.addAll(massiveResourceList);
        }

        assertEquals("There should be three resolved resources", 3, flatResolvedResources.size());
        assertTrue("The first resource should be resolved", flatResolvedResources.contains(firstResource));
        assertTrue("The second resource should be resolved", flatResolvedResources.contains(secondResource));
        assertTrue("The third resource should be resolved", flatResolvedResources.contains(thirdResource));
    }

    /**
     * Test to make sure an auto feature is not automatically installed if its capabilities are not met
     *
     * @throws Throwable
     */
    @Test
    public void testAutoFeatureNotSatisfied() throws Throwable {
        // Add a test resource to massive
        String symbolicName = "com.ibm.ws.test-1.0";
        EsaResourceImpl testResource = createEsaResource(symbolicName, null, null);

        // Now add the unsatisfied auto-resource
        createEsaResource("not.satisfied", null, null, null, null, Collections.singleton("does.not.exist"), true, null);

        // Now see if we can resolve just the single feature
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(symbolicName);
        assertEquals("There should only be a single list of resources, set is:" + resolvedResources, 1, resolvedResources.size());
        assertEquals("There should be one resolved resource", 1, resolvedResources.iterator().next().size());
        assertTrue("The resolved resource should be the one we supplied", resolvedResources.iterator().next().contains(testResource));
    }

    /**
     * Test to make sure an auto feature is not automatically installed if its capabilities are not met due to a missing iFix
     *
     * @throws Throwable
     */
    @Test
    public void testAutoFeatureNotSatisfiedWhenIFixMissing() throws Throwable {
        // Add a test resource to massive
        String symbolicName = "com.ibm.ws.test-1.0";
        EsaResourceImpl testResource = createEsaResource(symbolicName, null, null);

        // Now add the resource that depends on an iFix
        createEsaResource("auto.feature.with.ifix.dep", null, null, null, null, Collections.singleton(symbolicName), true, Collections.singleton("PM00001"));

        // Now see if we can resolve it!
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(symbolicName);
        assertEquals("There should only be a single list of resources, set is:" + resolvedResources, 1, resolvedResources.size());
        assertEquals("There should be one resolved resource", 1, resolvedResources.iterator().next().size());
        assertTrue("The resolved resource should be the one we supplied", resolvedResources.iterator().next().contains(testResource));
    }

    /**
     * Test to make sure an auto feature is automatically installed if its capabilities are met by what is being installed
     *
     * @throws Throwable
     */
    @Test
    public void testAutoFeatureSatisfiedByNewFeature() throws Throwable {
        // Add a test resource to massive
        String symbolicName = "com.ibm.ws.test-1.0";
        EsaResourceImpl testResource = createEsaResource(symbolicName, null, null);

        // Now add the soon-to-be-satisfied auto feature
        EsaResourceImpl autoFeature = createEsaResource("satisfied.auto.feature", null, null, null, null, Collections.singleton(symbolicName), true, null);

        // Now see if we can resolve it!
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(symbolicName);
        assertEquals("There should only be two lists of resources one for the feature being installed and one for the auto feature, set is:" + resolvedResources, 2,
                     resolvedResources.size());
        boolean foundFeatureList = false;
        boolean foundAutoList = false;
        for (List<RepositoryResource> resolvedList : resolvedResources) {
            if (resolvedList.contains(testResource) && resolvedList.size() == 1) {
                foundFeatureList = true;
            } else if (resolvedList.contains(autoFeature)) {
                foundAutoList = true;
                assertEquals("There should be 2 resolved resources in the auto list", 2, resolvedList.size());
                assertEquals("Auto should be installed last", autoFeature, resolvedList.get(1));
                assertEquals("Main feature should be installed first", testResource, resolvedList.get(0));
            } else {
                fail("Unexpected list in the resolve resources: " + resolvedList);
            }
        }
        assertTrue("Should have found feature", foundFeatureList);
        assertTrue("Should have found auto", foundAutoList);
    }

    /**
     * Test to make sure an auto feature is automatically installed if its capabilities are met by what is being installed even if it is through an OR relationships
     *
     * @throws Throwable
     */
    @Test
    public void testAutoFeatureWithSatisfiedByNewFeatureAndOtherAutoFeature() throws Throwable {
        // Add a test resource to massive
        String symbolicName = "com.ibm.ws.test-1.0";
        String requiredAutoFeatureSymbolicName = "com.ibm.ws.auto-1.0";
        EsaResourceImpl testResource = createEsaResource(symbolicName, null, null);
        EsaResourceImpl requiredAutoFeature = createEsaResource(requiredAutoFeatureSymbolicName, null, null, null, null, Collections.singleton(symbolicName), true, null);

        // Now add the soon-to-be-satisfied auto feature
        EsaResourceImpl autoFeature = new EsaResourceImpl(getConnection());
        autoFeature.setProvideFeature("satisfied.auto.feature");
        autoFeature.setName("name");
        String ibmProvisionCapability = "osgi.identity; filter:=\"(&(type=osgi.subsystem.feature)(osgi.identity=" + symbolicName
                                        + "))\", osgi.identity; filter:=\"(&(type=osgi.subsystem.feature)(osgi.identity=" + requiredAutoFeatureSymbolicName + "))\"";
        autoFeature.setProvisionCapability(ibmProvisionCapability.toString());
        autoFeature.setInstallPolicy(InstallPolicy.WHEN_SATISFIED);
        uploadResource(autoFeature);

        // Now see if we can resolve it!
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(symbolicName);
        assertEquals("There should only be three lists of resources one for the feature being installed and one for each of the auto feature, set is:" + resolvedResources, 3,
                     resolvedResources.size());
        boolean foundFeatureList = false;
        boolean foundAutoList = false;
        boolean foundRequiredAutoList = false;
        for (List<RepositoryResource> resolvedList : resolvedResources) {
            if (resolvedList.contains(testResource) && resolvedList.size() == 1) {
                foundFeatureList = true;
            } else if (resolvedList.contains(requiredAutoFeature) && resolvedList.size() == 2) {
                foundRequiredAutoList = true;
                assertEquals("Required Auto should be installed last", requiredAutoFeature, resolvedList.get(1));
                assertEquals("Main feature should be installed first", testResource, resolvedList.get(0));
            } else if (resolvedList.contains(autoFeature)) {
                foundAutoList = true;
                assertEquals("There should be 3 resolved resources in the auto list", 3, resolvedList.size());
                assertEquals("Auto should be installed last", autoFeature, resolvedList.get(2));
                assertEquals("Required auto feature should be installed", requiredAutoFeature, resolvedList.get(1));
                assertEquals("Main feature should be installed first", testResource, resolvedList.get(0));
            } else {
                fail("Unexpected list in the resolve resources: " + resolvedList);
            }
        }
        assertTrue("Should have found feature", foundFeatureList);
        assertTrue("Should have found auto", foundAutoList);
        assertTrue("Should have found required auto", foundRequiredAutoList);
    }

    /**
     * Test to make sure an auto feature is automatically installed if its capabilities are met by what is being installed even if it is through an OR relationships
     *
     * @throws Throwable
     */
    @Test
    public void testAutoFeatureWithOrSatisfiedByNewFeature() throws Throwable {
        // Add a test resource to massive
        String symbolicName = "com.ibm.ws.test-1.0";
        EsaResourceImpl testResource = createEsaResource(symbolicName, null, null);

        // Now add the soon-to-be-satisfied auto feature
        EsaResourceImpl autoFeature = new EsaResourceImpl(getConnection());
        autoFeature.setProvideFeature("satisfied.auto.feature");
        autoFeature.setName("name");
        StringBuilder ibmProvisionCapability = new StringBuilder();
        ibmProvisionCapability.append("osgi.identity; filter:=\"(|(&(type=osgi.subsystem.feature)(osgi.identity=" + symbolicName
                                      + "))(&(type=osgi.subsystem.feature)(osgi.identity=other)))\"");
        autoFeature.setProvisionCapability(ibmProvisionCapability.toString());
        autoFeature.setInstallPolicy(InstallPolicy.WHEN_SATISFIED);
        uploadResource(autoFeature);

        // Now see if we can resolve it!
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(symbolicName);
        assertEquals("There should only be two lists of resources one for the feature being installed and one for the auto feature, set is:" + resolvedResources, 2,
                     resolvedResources.size());
        boolean foundFeatureList = false;
        boolean foundAutoList = false;
        for (List<RepositoryResource> resolvedList : resolvedResources) {
            if (resolvedList.contains(testResource) && resolvedList.size() == 1) {
                foundFeatureList = true;
            } else if (resolvedList.contains(autoFeature)) {
                foundAutoList = true;
                assertEquals("There should be 2 resolved resources in the auto list", 2, resolvedList.size());
                assertEquals("Auto should be installed last", autoFeature, resolvedList.get(1));
                assertEquals("Main feature should be installed first", testResource, resolvedList.get(0));
            } else {
                fail("Unexpected list in the resolve resources: " + resolvedList);
            }
        }
        assertTrue("Should have found feature", foundFeatureList);
        assertTrue("Should have found auto", foundAutoList);
    }

    /**
     * Test to make sure an auto feature that is satisified by a feature in massive that is not in the install list doesn't cause that feature to be installed
     *
     * @throws Throwable
     */
    @Test
    public void testAutoFeatureDoesntPullInMoreFeatures() throws Throwable {
        // Add a test resource to massive
        String symbolicName = "com.ibm.ws.test-1.0";
        String symbolicNameForCapability = "com.ibm.ws.capabilit-1.0";
        EsaResourceImpl testResource = createEsaResource(symbolicName, null, null);
        createEsaResource(symbolicNameForCapability, null, null);

        // Now add the auto feature
        createEsaResource("satisfiable.auto.feature", null, null, null, null, Collections.singleton(symbolicNameForCapability), true, null);

        // Now see if we can resolve it!
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(symbolicName);
        assertEquals("There should only be a single list of resources, set is:" + resolvedResources, 1, resolvedResources.size());
        assertEquals("There should be one resolved resources", 1, resolvedResources.iterator().next().size());
        assertTrue("The resolved resources should contain the one we were looking for", resolvedResources.iterator().next().contains(testResource));
    }

    /**
     * Test to make sure when an auto feature is satisfied but has subsystem content listing another feature then it is installed pulls in the required feature.
     *
     * @throws Throwable
     */
    @Test
    public void testAutoFeaturePullsInSubsystemContentFeatures() throws Throwable {
        // Add a test resource to massive
        String mainFeatureSymbolicName = "com.ibm.ws.test-1.0";
        String autoFeatureSymbolicName = "com.ibm.ws.auto-1.0";
        String dependentFeatureSymbolicName = "com.ibm.ws.dependent-1.0";
        EsaResourceImpl mainFeature = createEsaResource(mainFeatureSymbolicName, null, null);
        EsaResourceImpl dependentFeature = createEsaResource(dependentFeatureSymbolicName, null, null);

        // Now add the auto feature
        EsaResourceImpl autoFeature = createEsaResource(autoFeatureSymbolicName, null, null, Collections.singleton(dependentFeatureSymbolicName), null,
                                                        Collections.singleton(mainFeatureSymbolicName), true,
                                                        null);

        // Now see if we can resolve it!
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(mainFeatureSymbolicName);
        assertEquals("There should be two lists of resources, set is:" + resolvedResources, 2, resolvedResources.size());
        boolean foundAutoList = false;
        boolean foundMainList = false;
        for (List<RepositoryResource> resources : resolvedResources) {
            if (resources.size() == 1) {
                assertEquals(mainFeature, resources.get(0));
                foundMainList = true;
            } else if (resources.size() == 3) {
                assertTrue("Dependencies should be installed first " + resources, resources.contains(mainFeature));
                assertTrue("Dependencies should be installed first " + resources, resources.contains(dependentFeature));
                assertEquals("Auto feature should be installed last as it depends on the other two " + resources, autoFeature, resources.get(2));
                foundAutoList = true;
            } else {
                fail("Unkown list of resources: " + resources);
            }
        }
        assertTrue(foundAutoList);
        assertTrue(foundMainList);
    }

    /**
     * Test to make sure an auto feature is installed if it is explicitly asked for and pulls in it's dependent features.
     *
     * @throws Throwable
     */
    @Test
    public void testAutoFeatureExplicitlyAskedFor() throws Throwable {
        // Add a test resource to massive
        String symbolicName = "com.ibm.ws.test-1.0";
        String autoSymbolicName = "com.ibm.ws.auto-1.0";
        EsaResourceImpl testResource = createEsaResource(symbolicName, null, null);

        // Now add the soon-to-be-satisfied auto feature
        EsaResourceImpl autoFeature = createEsaResource(autoSymbolicName, null, null, null, null, Collections.singleton(symbolicName), true, null);

        // Now see if we can resolve it!
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(autoSymbolicName);
        assertEquals("There should only be a single list of resources, set is:" + resolvedResources, 1, resolvedResources.size());
        List<RepositoryResource> resolvedList = resolvedResources.iterator().next();
        assertEquals("There should be 2 resolved resources in the auto list", 2, resolvedList.size());
        assertEquals("Auto should be installed last", autoFeature, resolvedList.get(1));
        assertEquals("Main feature should be installed first", testResource, resolvedList.get(0));
    }

    /**
     * Test to make sure that if an auto feature is set to be not auto-installable then it is not installed even when it's requirements are satisfied.
     *
     * @throws Throwable
     */
    @Test
    public void testAutoFeatureNotAutoInstallable() throws Throwable {
        // Add a test resource to massive
        String symbolicName = "com.ibm.ws.test-1.0";
        EsaResourceImpl testResource = createEsaResource(symbolicName, null, null);

        // Now add the satisified auto-resource but make it not auto installable
        createEsaResource("satisfied.auto.feature", null, null, null, null, Collections.singleton(symbolicName), false, null);

        // Now see if we can resolve just the single feature
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(symbolicName);
        assertEquals("There should only be a single list of resources, set is:" + resolvedResources, 1, resolvedResources.size());
        assertEquals("There should be one resolved resource", 1, resolvedResources.iterator().next().size());
        assertTrue("The resolved resource should be the one we supplied", resolvedResources.iterator().next().contains(testResource));
    }

    /**
     * Test to make sure an auto feature is automatically installed if its capabilities are met by what is already installed
     */
    @Test
    public void testAutoFeatureSatisfiedByInstalledFeature() throws RepositoryException, ResolutionException {
        // Add one test resource to massive, but make a dependency to an already installed feature
        String repoSymbolicName = "com.ibm.ws.test-1.0";
        String installedSymbolicName = "com.ibm.ws.test.installed-1.0";
        EsaResourceImpl testResource = createEsaResource(repoSymbolicName, null, null);
        Mockery mockery = new Mockery();
        ProvisioningFeatureDefinition mockFeatureDefinition = ResolverTestUtils.mockSimpleFeatureDefinition(mockery, installedSymbolicName, null, null);

        // Create the auto feature requiring the installed feature
        EsaResourceImpl autoFeature = createEsaResource("satisfied.auto.feature", null, null, null, null, Collections.singleton(installedSymbolicName), true, null);

        // Now see if we can resolve it!
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.singleton(mockFeatureDefinition), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(repoSymbolicName);
        assertEquals("There should be two lists of resources as the auto feature doesn't require the massive resource, set is:" + resolvedResources, 2, resolvedResources.size());
        Collection<RepositoryResource> allResolvedResources = new ArrayList<RepositoryResource>();
        for (List<RepositoryResource> massiveResourceList : resolvedResources) {
            allResolvedResources.addAll(massiveResourceList);
        }
        assertEquals("There should be two resolved resources", 2, allResolvedResources.size());
        assertTrue("The resolved resources should contain the one we were looking for", allResolvedResources.contains(testResource));
        assertTrue("The resolved resources should contain the now satisified auto feature", allResolvedResources.contains(autoFeature));

        // Make sure the mockery was happy
        mockery.assertIsSatisfied();
    }

    /**
     * Test to make sure an auto feature is not automatically installed if it is already installed
     */
    @Test
    public void testAutoFeatureAlreadyInstalled() throws RepositoryException, ResolutionException {
        // Add one test resource to massive, but make a dependency to an already installed feature
        String repoSymbolicName = "com.ibm.ws.test-1.0";
        String requiredInstalledSymbolicName = "com.ibm.ws.test.installed-1.0";
        String autoFeatureSymbolicName = "com.ibm.ws.test.auto-1.0";
        EsaResourceImpl testResource = createEsaResource(repoSymbolicName, null, null);
        Mockery mockery = new Mockery();
        ProvisioningFeatureDefinition mockFeatureDefinition = ResolverTestUtils.mockSimpleFeatureDefinition(mockery, requiredInstalledSymbolicName, null, null);
        ProvisioningFeatureDefinition mockAutoFeatureDefinition = ResolverTestUtils.mockSimpleFeatureDefinition(mockery, autoFeatureSymbolicName, null, null);
        Collection<ProvisioningFeatureDefinition> installedFeatures = new HashSet<ProvisioningFeatureDefinition>();
        installedFeatures.add(mockFeatureDefinition);
        installedFeatures.add(mockAutoFeatureDefinition);

        // Create the auto feature requiring the installed feature
        createEsaResource(autoFeatureSymbolicName, null, null, null, null, Collections.singleton(requiredInstalledSymbolicName), true, null);

        // Now see if we can resolve it!
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), installedFeatures, Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(repoSymbolicName);
        assertEquals("There should only be a single list of resources, set is:" + resolvedResources, 1, resolvedResources.size());
        assertEquals("There should be one resolved resource", 1, resolvedResources.iterator().next().size());
        assertEquals("The resource being resolved should be the one in the list", testResource, resolvedResources.iterator().next().get(0));

        // Make sure the mockery was happy
        mockery.assertIsSatisfied();
    }

    /**
     * Test to make sure if an auto feature needs two other features from the repo then everything works
     */
    @Test
    public void testAutoFeatureNeedingTwoFeatures() throws RepositoryException {
        // Add two test resources to massive
        String symbolicName1 = "com.ibm.ws.test.one-1.0";
        EsaResourceImpl testResource1 = createEsaResource(symbolicName1, null, null);
        String symbolicName2 = "com.ibm.ws.test.two-1.0";
        EsaResourceImpl testResource2 = createEsaResource(symbolicName2, null, null);

        // Now add the soon-to-be-satisfied auto feature
        Collection<String> requiredFeatures = new HashSet<String>();
        requiredFeatures.add(symbolicName1);
        requiredFeatures.add(symbolicName2);
        EsaResourceImpl autoFeature = createEsaResource("satisfied.auto.feature", null, null, null, null, requiredFeatures, true, null);

        // Now see if we can resolve it!
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(requiredFeatures);
        assertEquals("There should be three lists of resources one for each of the features asked for and one for the auto feature, set is:" + resolvedResources, 3,
                     resolvedResources.size());

        boolean found1List = false;
        boolean found2List = false;
        boolean foundAutoList = false;
        for (List<RepositoryResource> resolvedList : resolvedResources) {
            if (resolvedList.contains(testResource1) && resolvedList.size() == 1) {
                found1List = true;
            } else if (resolvedList.contains(testResource2) && resolvedList.size() == 1) {
                found2List = true;
            } else if (resolvedList.contains(autoFeature)) {
                foundAutoList = true;
                assertEquals("There should be 3 resolved resources in the auto list", 3, resolvedList.size());
                assertEquals("Auto should be installed last", autoFeature, resolvedList.get(2));
                assertTrue("1 should be installed", resolvedList.contains(testResource1));
                assertTrue("2 should be installed", resolvedList.contains(testResource2));
            } else {
                fail("Unexpected list in the resolve resources: " + resolvedList);
            }
        }
        assertTrue("Should have found 1", found1List);
        assertTrue("Should have found 2", found2List);
        assertTrue("Should have found auto", foundAutoList);
    }

    /**
     * Test to make sure if an auto feature needs two other features from the repo but one is missing then it isn't installed
     */
    @Test
    public void testAutoFeaturePartiallySatisfied() throws RepositoryException {
        // Add two test resources to massive
        String symbolicName1 = "com.ibm.ws.test.one-1.0";
        EsaResourceImpl testResource = createEsaResource(symbolicName1, null, null);
        String symbolicName2 = "com.ibm.ws.test.two-1.0";

        // Now add the soon-to-be-satisfied auto feature
        Collection<String> requiredFeatures = new HashSet<String>();
        requiredFeatures.add(symbolicName1);
        requiredFeatures.add(symbolicName2);
        createEsaResource("satisfied.auto.feature", null, null, null, null, requiredFeatures, true, null);

        // Now see if we can resolve it!
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(symbolicName1);
        assertEquals("There should only be a single list of resources as the auto feature isn't satisified, set is:" + resolvedResources, 1, resolvedResources.size());
        assertEquals("There should be one resolved resource", 1, resolvedResources.iterator().next().size());
        assertEquals("The resource being resolved should be the one in the list", testResource, resolvedResources.iterator().next().get(0));
    }

    /**
     * Test to make sure an auto feature is automatically along with any iFixes it needs
     *
     * @throws Throwable
     */
    @Test
    public void testAutoFeatureRequiringIFix() throws Throwable {
        // Add a test resource to massive
        String symbolicName = "com.ibm.ws.test-1.0";
        EsaResourceImpl testResource = createEsaResource(symbolicName, null, null);
        String fixId = "PM00010";
        IfixResourceImpl iFixResource = createIFixResource(fixId, null, null);

        // Now add the soon-to-be-satisfied auto feature
        EsaResourceImpl autoFeature = createEsaResource("satisfied.auto.feature", null, null, null, null, Collections.singleton(symbolicName), true, Collections.singleton(fixId));

        // Now see if we can resolve it!
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(symbolicName);
        assertEquals("There should only be two lists of resources one for the feature being installed and one for the auto feature, set is:" + resolvedResources, 2,
                     resolvedResources.size());
        boolean foundFeatureList = false;
        boolean foundAutoList = false;
        for (List<RepositoryResource> resolvedList : resolvedResources) {
            if (resolvedList.contains(testResource) && resolvedList.size() == 1) {
                foundFeatureList = true;
            } else if (resolvedList.contains(autoFeature)) {
                foundAutoList = true;
                assertEquals("There should be 3 resolved resources in the auto list", 3, resolvedList.size());
                assertEquals("Auto should be installed last", autoFeature, resolvedList.get(2));
                assertEquals("Main feature should be installed first", testResource, resolvedList.get(0));
                assertEquals("IFix should be installed second", iFixResource, resolvedList.get(1));
            } else {
                fail("Unexpected list in the resolve resources: " + resolvedList);
            }
        }
        assertTrue("Should have found feature", foundFeatureList);
        assertTrue("Should have found auto", foundAutoList);
    }

    /**
     * This method tests that a resolver can be used more than once.
     *
     * @throws RepositoryException
     */
    @Test
    public void testCaching() throws RepositoryException {
        // Add a test resource to massive
        String symbolicName = "com.ibm.ws.test-1.0";
        EsaResourceImpl testResource = createEsaResource(symbolicName, null, null);

        // Now see if we can resolve it!
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<List<RepositoryResource>> firstResolution = resolver.resolve(symbolicName);
        assertEquals("There should only be a single list of resources", 1, firstResolution.size());
        List<RepositoryResource> firstResolutionList = firstResolution.iterator().next();
        assertEquals("There should be one resolved resource", 1, firstResolutionList.size());
        assertTrue("The resolved resource should be the one we supplied", firstResolutionList.contains(testResource));

        Collection<List<RepositoryResource>> secondResolution = resolver.resolve(symbolicName);
        assertEquals("Multiple invocations of resolve should return the same result", firstResolution, secondResolution);
    }

    /**
     * Tests if you have two intersecting feature dependencies then they appear in both lists
     */
    @Test
    public void testIntersectingFeatures() throws RepositoryException {
        // Have a require b and c require b and ask to install a and c, both lists should contain b
        String aSymbolicName = "com.ibm.ws.test.A-1.0";
        String bSymbolicName = "com.ibm.ws.test.B-1.0";
        String cSymbolicName = "com.ibm.ws.test.C-1.0";
        EsaResourceImpl aResource = createEsaResource(aSymbolicName, null, null, Collections.singleton(bSymbolicName), null);
        EsaResourceImpl bResource = createEsaResource(bSymbolicName, null, null);
        EsaResourceImpl cResource = createEsaResource(cSymbolicName, null, null, Collections.singleton(bSymbolicName), null);

        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<String> symbolicNames = new HashSet<String>();
        symbolicNames.add(aSymbolicName);
        symbolicNames.add(cSymbolicName);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(symbolicNames);
        assertEquals("There should be two lists of resolved resources", 2, resolvedResources.size());
        boolean foundAList = false;
        boolean foundCList = false;
        for (List<RepositoryResource> resolvedList : resolvedResources) {
            if (resolvedList.contains(aResource)) {
                foundAList = true;
                assertEquals("There should be 2 resolved resources in the a list", 2, resolvedList.size());
                assertEquals("A should be installed last", aResource, resolvedList.get(1));
                assertEquals("B should be installed first", bResource, resolvedList.get(0));
            } else if (resolvedList.contains(cResource)) {
                foundCList = true;
                assertEquals("There should be 2 resolved resources in the c list", 2, resolvedList.size());
                assertEquals("C should be installed last", cResource, resolvedList.get(1));
                assertEquals("B should be installed first", bResource, resolvedList.get(0));
            } else {
                fail("Unexpected list in the resolve resources: " + resolvedList);
            }
        }
        assertTrue("Should have found A", foundAList);
        assertTrue("Should have found C", foundCList);
    }

    /**
     * Tests if you have two intersecting fix dependencies then they appear in both lists
     */
    @Test
    public void testIntersectingFixes() throws RepositoryException {
        // Have a require b (fix) and c require b and ask to install a and c, both lists should contain b
        String aSymbolicName = "com.ibm.ws.test.A-1.0";
        String bFixId = "PM0000b";
        String cSymbolicName = "com.ibm.ws.test.C-1.0";
        EsaResourceImpl aResource = createEsaResource(aSymbolicName, Collections.singleton(bFixId));
        IfixResourceImpl bResource = createIFixResource(bFixId, null, null);
        EsaResourceImpl cResource = createEsaResource(cSymbolicName, Collections.singleton(bFixId));

        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<String> symbolicNames = new HashSet<String>();
        symbolicNames.add(aSymbolicName);
        symbolicNames.add(cSymbolicName);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(symbolicNames);
        assertEquals("There should be two lists of resolved resources", 2, resolvedResources.size());
        boolean foundAList = false;
        boolean foundCList = false;
        for (List<RepositoryResource> resolvedList : resolvedResources) {
            if (resolvedList.contains(aResource)) {
                foundAList = true;
                assertEquals("There should be 2 resolved resources in the a list", 2, resolvedList.size());
                assertEquals("A should be installed last", aResource, resolvedList.get(1));
                assertEquals("B should be installed first", bResource, resolvedList.get(0));
            } else if (resolvedList.contains(cResource)) {
                foundCList = true;
                assertEquals("There should be 2 resolved resources in the c list", 2, resolvedList.size());
                assertEquals("C should be installed last", cResource, resolvedList.get(1));
                assertEquals("B should be installed first", bResource, resolvedList.get(0));
            } else {
                fail("Unexpected list in the resolve resources: " + resolvedList);
            }
        }
        assertTrue("Should have found A", foundAList);
        assertTrue("Should have found C", foundCList);
    }

    /**
     * This test makes sure that if you have two trees of features that intersect then the intersecting parts appear in both lists of stuff to install.
     *
     * @throws RepositoryException
     */
    @Test
    public void testIntersectingLists() throws RepositoryException {
        // Build up a data structure like so (where -> indicates a requirement and i or f prefix state whether it is an ifix or a feature):
        // fA -> fB -> iC, (fD -> iE)
        // fZ -> iC, fY -> fD -> iE
        // We will then ask to resolve fA and fZ and should then get back the following two lists of stuff to install:
        // [iE, fD, iC, fB, fA]
        // [iE, fD, fY, iC, fZ]
        String fASymbolicName = "com.ibm.ws.test.A-1.0";
        String fBSymbolicName = "com.ibm.ws.test.B-1.0";
        String fDSymbolicName = "com.ibm.ws.test.D-1.0";
        String fZSymbolicName = "com.ibm.ws.test.Z-1.0";
        String fYSymbolicName = "com.ibm.ws.test.Y-1.0";
        String iCId = "PM0000C";
        String iEId = "PM0000E";
        EsaResourceImpl aResource = createEsaResource(fASymbolicName, null, null, Collections.singleton(fBSymbolicName), null);
        EsaResourceImpl bResource = createEsaResource(fBSymbolicName, null, null, Collections.singleton(fDSymbolicName), null, null, false, Collections.singleton(iCId));
        EsaResourceImpl dResource = createEsaResource(fDSymbolicName, Collections.singleton(iEId));
        EsaResourceImpl zResource = createEsaResource(fZSymbolicName, null, null, Collections.singleton(fYSymbolicName), null, null, false, Collections.singleton(iCId));
        EsaResourceImpl yResource = createEsaResource(fYSymbolicName, null, null, Collections.singleton(fDSymbolicName), null);
        IfixResourceImpl iCResource = createIFixResource(iCId, null, null);
        IfixResourceImpl iEResource = createIFixResource(iEId, null, null);

        // Now do the resolution
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<String> symbolicNames = new HashSet<String>();
        symbolicNames.add(fASymbolicName);
        symbolicNames.add(fZSymbolicName);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(symbolicNames);
        assertEquals("There should be two lists of resolved resources", 2, resolvedResources.size());
        boolean foundAList = false;
        boolean foundZList = false;
        for (List<RepositoryResource> resolvedList : resolvedResources) {
            if (resolvedList.contains(aResource)) {
                foundAList = true;
                assertEquals("There should be 5 resolved resources in the a list", 5, resolvedList.size());
                assertEquals("A should be installed last", aResource, resolvedList.get(4));
                assertEquals("B should be installed second last", bResource, resolvedList.get(3));
                assertEquals("C should be installed in the middle", iCResource, resolvedList.get(2));
                assertEquals("D should be installed second ", dResource, resolvedList.get(1));
                assertEquals("E should be installed first", iEResource, resolvedList.get(0));
            } else if (resolvedList.contains(zResource)) {
                foundZList = true;
                assertEquals("There should be 5 resolved resources in the z list", 5, resolvedList.size());
                assertEquals("A should be installed last", zResource, resolvedList.get(4));
                assertEquals("C should be installed second last", iCResource, resolvedList.get(3));
                assertEquals("Y should be installed in the middle", yResource, resolvedList.get(2));
                assertEquals("D should be installed second ", dResource, resolvedList.get(1));
                assertEquals("E should be installed first", iEResource, resolvedList.get(0));
            } else {
                fail("Unexpected list in the resolve resources: " + resolvedList);
            }
        }
        assertTrue("Should have found A", foundAList);
        assertTrue("Should have found Z", foundZList);
    }

    /**
     * Test based on review of work item 118781 (comment 6) where if you have two long paths to the same object then it can appear after things that depend on it.
     *
     * @throws RepositoryException
     * @throws RepositoryResolutionException
     */
    @Test
    public void testOrderingOnMultipleLongPaths() throws RepositoryResolutionException, RepositoryException {
        // Construct the following dependency model in the repo:
        // A -> B, C
        // B -> D
        // C -> E
        // D -> F
        // E -> F
        String aSymbolicName = "com.ibm.ws.test.A-1.0";
        String bSymbolicName = "com.ibm.ws.test.B-1.0";
        String cSymbolicName = "com.ibm.ws.test.C-1.0";
        String dSymbolicName = "com.ibm.ws.test.D-1.0";
        String eSymbolicName = "com.ibm.ws.test.E-1.0";
        String fSymbolicName = "com.ibm.ws.test.F-1.0";
        Collection<String> aDeps = new HashSet<String>();
        aDeps.add(bSymbolicName);
        aDeps.add(cSymbolicName);
        EsaResourceImpl aResource = createEsaResource(aSymbolicName, null, null, aDeps, null);
        EsaResourceImpl bResource = createEsaResource(bSymbolicName, null, null, Collections.singleton(dSymbolicName), null);
        EsaResourceImpl cResource = createEsaResource(cSymbolicName, null, null, Collections.singleton(eSymbolicName), null);
        EsaResourceImpl dResource = createEsaResource(dSymbolicName, null, null, Collections.singleton(fSymbolicName), null);
        EsaResourceImpl eResource = createEsaResource(eSymbolicName, null, null, Collections.singleton(fSymbolicName), null);
        EsaResourceImpl fResource = createEsaResource(fSymbolicName, null, null);

        // Resolve
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(aSymbolicName);

        // Test result
        assertEquals("There should be a single list of resolved resources", 1, resolvedResources.size());
        List<RepositoryResource> resolvedList = resolvedResources.iterator().next();
        assertEquals("There should be 6 resolved resources, list is: " + resolvedList, 6, resolvedList.size());
        assertEquals("Everything depends on f so it should be installed first, list is: " + resolvedList, fResource, resolvedList.get(0));
        assertEquals("A is at the root so should be installed last, list is: " + resolvedList, aResource, resolvedList.get(5));
        assertTrue("The list should contain B, list is: " + resolvedList, resolvedList.contains(bResource));
        assertTrue("The list should contain C, list is: " + resolvedList, resolvedList.contains(cResource));
        assertTrue("The list should contain D, list is: " + resolvedList, resolvedList.contains(dResource));
        assertTrue("The list should contain E, list is: " + resolvedList, resolvedList.contains(eResource));
        assertTrue("B depends on D so should be installed after it, list is: " + resolvedList, resolvedList.indexOf(bResource) > resolvedList.indexOf(dResource));
        assertTrue("C depends on E so should be installed after it, list is: " + resolvedList, resolvedList.indexOf(cResource) > resolvedList.indexOf(eResource));
    }

    /**
     * Same as {@link #testOrderingOnMultipleLongPaths()} but with an extra dependency on the end to ensure that everything that needs to appear at the end does so.
     *
     * @throws RepositoryException
     * @throws RepositoryResolutionException
     */
    @Test
    public void testOrderingOnMultipleLongPathsAndLongTail() throws RepositoryResolutionException, RepositoryException {
        // Construct the following dependency model in the repo:
        // A -> B, C
        // B -> D
        // C -> E
        // D -> F
        // E -> F
        // F -> G
        String aSymbolicName = "com.ibm.ws.test.A-1.0";
        String bSymbolicName = "com.ibm.ws.test.B-1.0";
        String cSymbolicName = "com.ibm.ws.test.C-1.0";
        String dSymbolicName = "com.ibm.ws.test.D-1.0";
        String eSymbolicName = "com.ibm.ws.test.E-1.0";
        String fSymbolicName = "com.ibm.ws.test.F-1.0";
        String gSymbolicName = "com.ibm.ws.test.G-1.0";
        Collection<String> aDeps = new HashSet<String>();
        aDeps.add(bSymbolicName);
        aDeps.add(cSymbolicName);
        EsaResourceImpl aResource = createEsaResource(aSymbolicName, null, null, aDeps, null);
        EsaResourceImpl bResource = createEsaResource(bSymbolicName, null, null, Collections.singleton(dSymbolicName), null);
        EsaResourceImpl cResource = createEsaResource(cSymbolicName, null, null, Collections.singleton(eSymbolicName), null);
        EsaResourceImpl dResource = createEsaResource(dSymbolicName, null, null, Collections.singleton(fSymbolicName), null);
        EsaResourceImpl eResource = createEsaResource(eSymbolicName, null, null, Collections.singleton(fSymbolicName), null);
        EsaResourceImpl fResource = createEsaResource(fSymbolicName, null, null, Collections.singleton(gSymbolicName), null);
        EsaResourceImpl gResource = createEsaResource(gSymbolicName, null, null);

        // Resolve
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(aSymbolicName);

        // Test result
        assertEquals("There should be a single list of resolved resources", 1, resolvedResources.size());
        List<RepositoryResource> resolvedList = resolvedResources.iterator().next();
        assertEquals("There should be 7 resolved resources, list is: " + resolvedList, 7, resolvedList.size());
        assertEquals("Everything depends on g through f so it should be installed first, list is: " + resolvedList, gResource, resolvedList.get(0));
        assertEquals("f should be second, list is: " + resolvedList, fResource, resolvedList.get(1));
        assertEquals("A is at the root so should be installed last, list is: " + resolvedList, aResource, resolvedList.get(6));
        assertTrue("The list should contain B, list is: " + resolvedList, resolvedList.contains(bResource));
        assertTrue("The list should contain C, list is: " + resolvedList, resolvedList.contains(cResource));
        assertTrue("The list should contain D, list is: " + resolvedList, resolvedList.contains(dResource));
        assertTrue("The list should contain E, list is: " + resolvedList, resolvedList.contains(eResource));
        assertTrue("B depends on D so should be installed after it, list is: " + resolvedList, resolvedList.indexOf(bResource) > resolvedList.indexOf(dResource));
        assertTrue("C depends on E so should be installed after it, list is: " + resolvedList, resolvedList.indexOf(cResource) > resolvedList.indexOf(eResource));
    }

    /**
     * <p>Test for work item 129271:</p>
     * <p>Here is the scenario when a feature can not be resolved. </p>
     *
     * <p>Feature A (applies-to ND 8.5.5.2 only) has a dependency on Feature B (applies-to CORE/BASE/ND 8.5.5.2 and Archive Install only).</p>
     *
     * <p>When a user is trying to install Feature A from a Liberty BASE 8.5.5.2 Installation Manager installation which doesn't have Feature B installed, the resolver should throw
     * an exception indicating the feature A is missing the applies-to requirement. </p>
     *
     * <p>But it currently throws an exception indicating the dependent feature is not meeting the requirement. </p>
     *
     * @throws RepositoryException
     * @throws ProductInfoParseException
     * @throws IOException
     */
    @Test
    public void testExceptionFromUnresolvableItemWithUnresolveableDependency() throws RepositoryException, IOException, ProductInfoParseException {
        String firstSymbolicName = "com.ibm.ws.A-1.0";
        String firstAppliesTo = "com.ibm.websphere.appserver; productVersion=8.5.5.2; productEdition=\"ND\"";
        String secondSymbolicName = "com.ibm.ws.B-1.0";
        RepositoryResourceImpl resource = createEsaResource(firstSymbolicName, null, null, Collections.singleton(secondSymbolicName), firstAppliesTo);
        createEsaResource(secondSymbolicName, null, "1.0.0.1", null,
                          "com.ibm.websphere.appserver; productVersion=8.5.5.2; productEdition=\"CORE,BASE,ND\"; productInstallType=Archive");
        ProductInfo productInfo = ResolverTestUtils.createProductInfo("com.ibm.websphere.appserver", "BASE", "8.5.5.2", null, "INSTALLATION_MANAGER");
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> singleton(new ProductInfoProductDefinition(productInfo)), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        try {
            resolver.resolve(firstSymbolicName);
            fail("The resource should not resolve and neither should it's dependency");
        } catch (RepositoryResolutionException e) {
            assertTrue("The resolution message should state what couldn't be resolved but it is: " + e.getMessage(), e.getMessage().contains("resource=" + firstSymbolicName));
            assertEquals("There should only be one top level resource that wasn't resolved", 1, e.getTopLevelFeaturesNotResolved().size());
            assertTrue("The correct top level feature should be listed", e.getTopLevelFeaturesNotResolved().contains(firstSymbolicName));
            assertEquals("Only one requirement should not be found", 1, e.getAllRequirementsNotFound().size());
            assertThat("The exception should say which requirement actually wasn't found and it should be the requirement on the product for the first feature",
                       e.getAllRequirementsNotFound(), contains(firstAppliesTo));
            assertEquals("Only one requirement should not be found", 1, e.getAllRequirementsResourcesNotFound().size());
            MissingRequirement missingRequirement = e.getAllRequirementsResourcesNotFound().iterator().next();
            assertEquals("The exception should say which requirement actually wasn't found and it should be the requirement on the third feature "
                         + e.getAllRequirementsResourcesNotFound(), firstAppliesTo, missingRequirement.requirementName);
            assertEquals("The exception should include the resource that owned the requirement that wasn't found "
                         + e.getAllRequirementsResourcesNotFound(), resource, missingRequirement.owningResource);
            assertEquals("One product should have not been found", 1, e.getMissingProducts().size());
            assertEquals("The exception should list which products features were found for",
                         new ProductRequirementInformation("[8.5.5.2, 8.5.5.2]", "com.ibm.websphere.appserver", null, null, Collections.singletonList("ND")),
                         e.getMissingProducts().iterator().next());
            assertEquals("The exception should contain the right minimum product value", "8.5.5.2",
                         e.getMinimumVersionForMissingProduct("com.ibm.websphere.appserver", null, null));
            assertEquals("The exception should contain the right maximum product value", "8.5.5.2", e.getMaximumVersionForMissingProduct(null, null, null));
        }
        // Just double check we can't resolve the second item either (otherwise this might be the cause of the "correct" error message coming out)
        try {
            resolver.resolve(secondSymbolicName);
            fail("The resource should not resolve");
        } catch (RepositoryResolutionException e) {

        }

    }

    /**
     * <p>Test to demonstrate one of the hard cases in the resolver scalability (similar to web cache monitor). The setup is this:</p>
     *
     * <p>Feature A (applies-to ND, any version) depends on Feature B (applies-to ND, 8.5.5.4)</p>
     *
     * <p>Then try to resolve feature A on 8.5.5.2, you should get an error saying it only applies to 8554 and not one saying that feature B is missing.</p>
     *
     * <p>Note this test also provides a test for defect 173065</p>
     *
     * @throws RepositoryException
     * @throws ProductInfoParseException
     * @throws IOException
     */
    @Test
    public void testExceptionFromUnresolvableDepdendencyDueToBadAppliesTo() throws RepositoryException, IOException, ProductInfoParseException {
        String firstSymbolicName = "com.ibm.ws.A-1.0";
        String firstAppliesTo = "com.ibm.websphere.appserver; productEdition=\"ND\"";
        String secondSymbolicName = "com.ibm.ws.B-1.0";
        String secondAppliesTo = "com.ibm.websphere.appserver; productVersion=8.5.5.4; productEdition=\"ND\"";
        createEsaResource(firstSymbolicName, null, null, Collections.singleton(secondSymbolicName), firstAppliesTo);
        RepositoryResourceImpl secondResource = createEsaResource(secondSymbolicName, null, "1.0.0.1", null, secondAppliesTo);
        ProductInfo productInfo = ResolverTestUtils.createProductInfo("com.ibm.websphere.appserver", "ND", "8.5.5.2", null, "INSTALLATION_MANAGER");
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> singleton(new ProductInfoProductDefinition(productInfo)), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        try {
            resolver.resolve(firstSymbolicName);
            fail("The resource should not resolve and neither should it's dependency");
        } catch (RepositoryResolutionException e) {
            assertTrue("The resolution message should state what couldn't be resolved but it is: " + e.getMessage(), e.getMessage().contains("resource=" + firstSymbolicName));
            assertEquals("There should only be one top level resource that wasn't resolved", 1, e.getTopLevelFeaturesNotResolved().size());
            assertTrue("The correct top level feature should be listed", e.getTopLevelFeaturesNotResolved().contains(firstSymbolicName));
            assertEquals("Only one requirement should not be found", 1, e.getAllRequirementsNotFound().size());
            assertTrue("The exception should say which requirement actually wasn't found and it should be the requirement on the product for the second feature "
                       + e.getAllRequirementsNotFound(), e.getAllRequirementsNotFound().contains(secondAppliesTo));
            assertEquals("Only one requirement should not be found", 1, e.getAllRequirementsResourcesNotFound().size());
            MissingRequirement missingRequirement = e.getAllRequirementsResourcesNotFound().iterator().next();
            assertEquals("The exception should say which requirement actually wasn't found and it should be the requirement on the product for the second feature "
                         + e.getAllRequirementsResourcesNotFound(), secondAppliesTo, missingRequirement.requirementName);
            assertEquals("The exception should say which resources owned the requirement that wasn't found "
                         + e.getAllRequirementsResourcesNotFound(), secondResource, missingRequirement.owningResource);
            assertEquals("One product should have not been found", 1, e.getMissingProducts().size());
            assertEquals("The exception should list which products features were found for",
                         new ProductRequirementInformation("[8.5.5.4, 8.5.5.4]", "com.ibm.websphere.appserver", null, null, Collections.singletonList("ND")),
                         e.getMissingProducts().iterator().next());
            assertEquals("The exception should contain the right minimum product value", "8.5.5.4", e.getMinimumVersionForMissingProduct(null, null, null));
            assertEquals("The exception should contain the right maximum product value", "8.5.5.4",
                         e.getMaximumVersionForMissingProduct("com.ibm.websphere.appserver", null, null));
        }
    }

    /**
     * This tests asks for a feature for 8552 but there are only features for 8553 and 8554 so the resolution fails, we should get information that features were found for the
     * other versions though.
     *
     * @throws ProductInfoParseException
     * @throws IOException
     * @throws RepositoryException
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testFeaturesOnMultipleProductsButNotRightOne() throws IOException, ProductInfoParseException, RepositoryException {
        String symbolicName = "com.ibm.ws.A-1.0";
        String firstAppliesTo = "com.ibm.websphere.appserver; productVersion=8.5.5.3; productEdition=\"BASE\"";
        String secondAppliesTo = "com.ibm.websphere.appserver; productVersion=8.5.5.10; productEdition=\"ND\"";
        String thirdAppliesTo = "com.ibm.websphere.appserver; productVersion=9.0.0.2; productEdition=\"ND\"";
        RepositoryResourceImpl resource1 = createEsaResource(symbolicName, null, null, null, firstAppliesTo);
        RepositoryResourceImpl resource2 = createEsaResource(symbolicName, null, "1.0.0.1", null, secondAppliesTo);
        RepositoryResourceImpl resource3 = createEsaResource(symbolicName, null, "1.0.0.2", null, thirdAppliesTo);
        ProductInfo productInfo = ResolverTestUtils.createProductInfo("com.ibm.websphere.appserver", "BASE", "8.5.5.2", null, null);
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> singleton(new ProductInfoProductDefinition(productInfo)), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        try {
            resolver.resolve(symbolicName);
            fail("The resource should not resolve and neither should it's dependency");
        } catch (RepositoryResolutionException e) {
            assertTrue("The resolution message should state what couldn't be resolved but it is: " + e.getMessage(), e.getMessage().contains("resource=" + symbolicName));
            assertEquals("There should only be one top level resource that wasn't resolved", 1, e.getTopLevelFeaturesNotResolved().size());
            assertTrue("The correct top level feature should be listed", e.getTopLevelFeaturesNotResolved().contains(symbolicName));
            assertEquals("Three requirements should not be found", 3, e.getAllRequirementsNotFound().size());
            assertTrue("The exception should say which requirement actually wasn't found and it should include the requirement on the product for the first feature "
                       + e.getAllRequirementsNotFound(), e.getAllRequirementsNotFound().contains(firstAppliesTo));
            assertTrue("The exception should say which requirement actually wasn't found and it should include the requirement on the product for the second feature "
                       + e.getAllRequirementsNotFound(), e.getAllRequirementsNotFound().contains(secondAppliesTo));
            assertTrue("The exception should say which requirement actually wasn't found and it should include the requirement on the product for the third feature "
                       + e.getAllRequirementsNotFound(), e.getAllRequirementsNotFound().contains(thirdAppliesTo));
            assertEquals("Three requirements should not be found", 3, e.getAllRequirementsResourcesNotFound().size());
            Collection<MissingRequirement> missingRequirements = e.getAllRequirementsResourcesNotFound();
            assertThat("All the requirements should be in the list of missing requirements", missingRequirements,
                       containsInAnyOrder(allOf(hasProperty("requirementName", equalTo(firstAppliesTo)), hasProperty("owningResource", equalTo(resource1))),
                                          allOf(hasProperty("requirementName", equalTo(secondAppliesTo)), hasProperty("owningResource", equalTo(resource2))),
                                          allOf(hasProperty("requirementName", equalTo(thirdAppliesTo)), hasProperty("owningResource", equalTo(resource3)))));
            assertEquals("Two products should have not been found", 3, e.getMissingProducts().size());
            assertTrue("The exception should list that a feature for product at version 8553 was found but was: " + e.getMissingProducts(),
                       e.getMissingProducts().contains(new ProductRequirementInformation("[8.5.5.3, 8.5.5.3]", "com.ibm.websphere.appserver", null, null, Collections.singletonList("BASE"))));
            assertTrue("The exception should list that a feature for product at version 85510 was found but was: " + e.getMissingProducts(),
                       e.getMissingProducts().contains(new ProductRequirementInformation("[8.5.5.10, 8.5.5.10]", "com.ibm.websphere.appserver", null, null, Collections.singletonList("ND"))));
            assertTrue("The exception should list that a feature for product at version 9002 was found but was: " + e.getMissingProducts(),
                       e.getMissingProducts().contains(new ProductRequirementInformation("[9.0.0.2, 9.0.0.2]", "com.ibm.websphere.appserver", null, null, Collections.singletonList("ND"))));
            assertEquals("The exception should contain the right minimum product value", "8.5.5.3", e.getMinimumVersionForMissingProduct(null, null, null));
            assertEquals("The exception should contain the right maximum product value", "9.0.0.2",
                         e.getMaximumVersionForMissingProduct("com.ibm.websphere.appserver", null, null));
            assertEquals("The exception should contain the right minimum product value filtering on edition", "8.5.5.10", e.getMinimumVersionForMissingProduct(null, null, "ND"));
            assertEquals("The exception should contain the right maximum product value filtering on edition", "9.0.0.2",
                         e.getMaximumVersionForMissingProduct("com.ibm.websphere.appserver", null, "ND"));
            assertEquals("The exception should contain the right minimum product value filtering on version", "8.5.5.3",
                         e.getMinimumVersionForMissingProduct(null, "8.5.5.0", null));
            assertEquals("The exception should contain the right maximum product value filtering on version", "8.5.5.10",
                         e.getMaximumVersionForMissingProduct("com.ibm.websphere.appserver", "8.5.5.0", null));
            assertEquals("The exception should contain the right minimum product value filtering on version and edition", "9.0.0.2",
                         e.getMinimumVersionForMissingProduct(null, "9.0.0.0", "ND"));
            assertEquals("The exception should contain the right maximum product value filtering on version", "9.0.0.2",
                         e.getMaximumVersionForMissingProduct("com.ibm.websphere.appserver", "9.0.0.0", "ND"));
        }
    }

    /**
     * Test that you can resolve a sample
     *
     * @throws RepositoryException
     */
    @Test
    public void testSample() throws RepositoryException {
        String name = "testSample";
        SampleResourceImpl testResource = createAndUploadSampleResource(name);

        // Now see if we can resolve it!
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(name);
        assertEquals("There should only be a single list of resources", 1, resolvedResources.size());
        assertEquals("There should be one resolved resource", 1, resolvedResources.iterator().next().size());
        assertTrue("The resolved resource should be the one we supplied", resolvedResources.iterator().next().contains(testResource));
    }

    /**
     * Try to resolve a sample by it's lower case name
     *
     * @throws RepositoryException
     */
    @Test
    public void testSampleByLowerCaseShortName() throws RepositoryException {
        String name = "testSampleByLowerCaseShortName";
        SampleResourceImpl testResource = createAndUploadSampleResource(name);

        // Now see if we can resolve it!
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(name.toLowerCase());
        assertEquals("There should only be a single list of resources", 1, resolvedResources.size());
        assertEquals("There should be one resolved resource", 1, resolvedResources.iterator().next().size());
        assertTrue("The resolved resource should be the one we supplied", resolvedResources.iterator().next().contains(testResource));
    }

    /**
     * Test you can resolve an OSI
     *
     * @throws RepositoryException
     */
    @Test
    public void testOsi() throws RepositoryException {
        String name = "testOsi";
        SampleResourceImpl testResource = createSampleResource(name);
        testResource.setType(ResourceType.OPENSOURCE);
        uploadResource(testResource);

        // Now see if we can resolve it!
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(name);
        assertEquals("There should only be a single list of resources", 1, resolvedResources.size());
        assertEquals("There should be one resolved resource", 1, resolvedResources.iterator().next().size());
        assertTrue("The resolved resource should be the one we supplied", resolvedResources.iterator().next().contains(testResource));
    }

    /**
     * Test that you can resolve more than one sample at once
     *
     * @throws RepositoryException
     */
    @Test
    public void testMultipleSamples() throws RepositoryException {
        String name1 = "testMultipleSamples1";
        SampleResourceImpl testResource1 = createAndUploadSampleResource(name1);
        String name2 = "testMultipleSamples2";
        SampleResourceImpl testResource2 = createAndUploadSampleResource(name2);

        // Now see if we can resolve them!
        Collection<String> names = new HashSet<String>();
        names.add(name1);
        names.add(name2);
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(names);
        assertEquals("There should be a two lists of resources", 2, resolvedResources.size());
        boolean found1 = false;
        boolean found2 = false;
        for (List<RepositoryResource> list : resolvedResources) {
            assertEquals("Each resolve list should only have a single resource " + list, 1, list.size());
            if (list.contains(testResource1)) {
                found1 = true;
            } else if (list.contains(testResource2)) {
                found2 = true;
            } else {
                fail("Unknown resource list resolved: " + list);
            }
        }
        assertTrue("Should have resolved the first sample", found1);
        assertTrue("Should have resolved the second sample", found2);
    }

    /**
     * Tests that if a sample depends on a feature then it is resolved along with the feature and in the right order
     */
    @Test
    public void testSampleWithFeatureDependency() throws RepositoryException {
        String name = "testSampleWithFeatureDependency";
        String featureSymbolicName = "com.ibm.ws.feature";
        SampleResourceImpl sampleResource = createSampleResource(name);
        sampleResource.setRequireFeature(Collections.singleton(featureSymbolicName));
        uploadResource(sampleResource);
        EsaResourceImpl feature = createEsaResource(featureSymbolicName, null, null);

        // Now see if we can resolve it!
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(name);
        assertEquals("There should only be a single list of resources", 1, resolvedResources.size());
        List<RepositoryResource> installList = resolvedResources.iterator().next();
        assertEquals("There should be two resolved resource", 2, installList.size());
        assertEquals("The feature needs installing first so it should be first in the list", feature, installList.get(0));
        assertEquals("The sample needs installing second so it should be second in the list", sampleResource, installList.get(1));
    }

    /**
     * Tests that if there is an applies to to an exact product version then it matches
     *
     * @throws RepositoryException
     * @throws ProductInfoParseException
     * @throws IOException
     */
    @Test
    public void testSampleWithAppliesToExact() throws RepositoryException, IOException, ProductInfoParseException {
        runAppliesToSampleTest("testSampleWithAppliesToExact", "com.ibm.ws.test.product; productVersion=5.0.0.0; productEdition=DEVELOPERS", "5.0.0.0");
    }

    /**
     * Tests that if there is an applies to to a range of product version but the product version is the min version then it matches
     *
     * @throws RepositoryException
     * @throws ProductInfoParseException
     * @throws IOException
     */
    @Test
    public void testSampleWithAppliesToRangeExactVersion() throws RepositoryException, IOException, ProductInfoParseException {
        runAppliesToSampleTest("testSampleWithAppliesToRangeExactVersion", "com.ibm.ws.test.product; productVersion=5.0.0.0+; productEdition=DEVELOPERS", "5.0.0.0");
    }

    /**
     * Tests that if there is an applies to to a range of product version and the product version is the higher than the min version then it matches
     */
    @Test
    public void testSampleWithAppliesToRangeHigherVersion() throws RepositoryException, IOException, ProductInfoParseException {
        runAppliesToSampleTest("testSampleWithAppliesToRangeExactVersion", "com.ibm.ws.test.product; productVersion=5.0.0.0+; productEdition=DEVELOPERS", "6.0.0.0");
    }

    /**
     * Tests that if there is an applies to to a range of product version but the product version is the min version then it matches
     *
     * @throws RepositoryException
     * @throws ProductInfoParseException
     * @throws IOException
     */
    @Test
    public void testFeatureWithAppliesToRangeExactVersion() throws RepositoryException, IOException, ProductInfoParseException {
        String symoblicName = "com.ibm.ws.applies.to.range.exact.version";
        RepositoryResourceImpl testResource = createEsaResource(symoblicName, null, null, null, "com.ibm.ws.test.product; productVersion=5.0.0.0+; productEdition=DEVELOPERS");
        runTestAgainstProductDefinitionWithSingleResult(symoblicName, "5.0.0.0", testResource);
    }

    /**
     * Tests that if there is an applies to to a range of product version and the product version is the higher than the min version then it matches
     */
    @Test
    public void testFeatureWithAppliesToRangeHigherVersion() throws RepositoryException, IOException, ProductInfoParseException {
        String symoblicName = "com.ibm.ws.applies.to.range.higher.version";
        RepositoryResourceImpl testResource = createEsaResource(symoblicName, null, null, null, "com.ibm.ws.test.product; productVersion=5.0.0.0+; productEdition=DEVELOPERS");
        runTestAgainstProductDefinitionWithSingleResult(symoblicName, "6.0.0.0", testResource);
    }

    /**
     * Tests that if there is an applies to to a range of product version and the product version is the lower than the min version then it throws an exception
     */
    @Test
    public void testFeatureWithAppliesToRangeLowerVersion() throws RepositoryException, IOException, ProductInfoParseException {
        String symoblicName = "com.ibm.ws.applies.to.range.higher.version";
        String appliesTo = "com.ibm.ws.test.product; productVersion=5.0.0.0+; productEdition=DEVELOPERS";
        RepositoryResourceImpl testResource = createEsaResource(symoblicName, null, null, null, appliesTo);
        try {
            runTestAgainstProductDefinitionWithSingleResult(symoblicName, "4.0.0.0", testResource);
            fail("Should have thrown an exception");
        } catch (RepositoryResolutionException e) {
            assertTrue("The resolution message should state what couldn't be resolved but it is: " + e.getMessage(), e.getMessage().contains("resource=" + symoblicName));
            assertEquals("There should only be one top level resource that wasn't resolved", 1, e.getTopLevelFeaturesNotResolved().size());
            assertTrue("The correct top level feature should be listed " + e.getTopLevelFeaturesNotResolved(), e.getTopLevelFeaturesNotResolved().contains(symoblicName));
            assertEquals("Only one requirement should not be found", 1, e.getAllRequirementsNotFound().size());
            assertTrue("The exception should say which requirement actually wasn't found and it should be the requirement on the product for the second feature "
                       + e.getAllRequirementsNotFound(), e.getAllRequirementsNotFound().contains(appliesTo));
            assertEquals("Only one requirement should not be found", 1, e.getAllRequirementsResourcesNotFound().size());
            MissingRequirement missingRequirement = e.getAllRequirementsResourcesNotFound().iterator().next();
            assertEquals("The exception should say which requirement actually wasn't found and it should be the requirement on the product for the applies to "
                         + e.getAllRequirementsResourcesNotFound(), appliesTo, missingRequirement.requirementName);
            assertEquals("The exception should say which resources owned the requirement that wasn't found "
                         + e.getAllRequirementsResourcesNotFound(), testResource, missingRequirement.owningResource);
            assertEquals("There should only be one product that couldn't be resolved", 1, e.getMissingProducts().size());
            assertTrue("The exception should list which products features were found for: " + e.getMissingProducts(),
                       e.getMissingProducts().contains(new ProductRequirementInformation("5.0.0.0", "com.ibm.ws.test.product", null, null, Collections.singletonList("DEVELOPERS"))));
            assertEquals("The exception should contain the right minimum product value", "5.0.0.0", e.getMinimumVersionForMissingProduct(null, null, null));
            assertNull("The exception should have no max product version for an unbounded range", e.getMaximumVersionForMissingProduct("com.ibm.websphere.appserver", null, null));
        }
    }

    /**
     * Test that when you have multiple features that are applicable to a given version the correct one is picked. For more comprehensive ordering tests are done in the
     * FeatureResourceTest unit test so this is a sniff test to make sure a basic rule is applied when integrated into the resolver.
     *
     * @throws RepositoryException
     * @throws ProductInfoParseException
     * @throws IOException
     * @throws RepositoryResolutionException
     */
    @Test
    public void testRightFeaturePicked() throws RepositoryResolutionException, IOException, ProductInfoParseException, RepositoryException {
        String symoblicName = "com.ibm.ws.applies.to.range.higher.version";
        createEsaResource(symoblicName, null, null, null, "com.ibm.ws.test.product; productVersion=5.0.0.0+; productEdition=DEVELOPERS");
        RepositoryResourceImpl exactTestResource = createEsaResource(symoblicName, null, null, null, "com.ibm.ws.test.product; productVersion=6.0.0.0; productEdition=DEVELOPERS");
        runTestAgainstProductDefinitionWithSingleResult(symoblicName, "6.0.0.0", exactTestResource);
    }

    /**
     * Tests the exception when a sample with a higher applies to than the current product
     *
     * @throws ProductInfoParseException
     * @throws IOException
     * @throws RepositoryException
     */
    @Test
    public void testSampleWrongProductVersion() throws IOException, ProductInfoParseException, RepositoryException {
        String name = "testSampleWrongProductVersion";
        SampleResourceImpl testResource = createSampleResource(name);
        String appliesTo = "com.ibm.ws.test.product; productVersion=6.0.0.0+; productEdition=DEVELOPERS";
        testResource.setAppliesTo(appliesTo);
        uploadResource(testResource);
        ProductInfo productInfo = ResolverTestUtils.createProductInfo("com.ibm.ws.test.product", "DEVELOPERS", "5.0.0.0", null, null);

        // Now see if we can resolve it!
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> singleton(new ProductInfoProductDefinition(productInfo)), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        try {
            resolver.resolve(name);
            fail("The sample does not apply to this product so should not have resolved");
        } catch (RepositoryResolutionException e) {
            assertTrue("The resolution message should state what couldn't be resolved but it is: " + e.getMessage(), e.getMessage().contains("resource=" + name));
            assertEquals("There should only be one top level resource that wasn't resolved", 1, e.getTopLevelFeaturesNotResolved().size());
            assertTrue("The correct top level feature should be listed", e.getTopLevelFeaturesNotResolved().contains(name));
            assertEquals("Only one requirement should not be found", 1, e.getAllRequirementsNotFound().size());
            assertTrue("The exception should say which requirement actually wasn't found and it should be the requirement on the product for the second feature "
                       + e.getAllRequirementsNotFound(), e.getAllRequirementsNotFound().contains(appliesTo));
            assertEquals("Only one requirement should not be found", 1, e.getAllRequirementsResourcesNotFound().size());
            MissingRequirement missingRequirement = e.getAllRequirementsResourcesNotFound().iterator().next();
            assertEquals("The exception should say which requirement actually wasn't found and it should be the requirement on the product for the applies to "
                         + e.getAllRequirementsResourcesNotFound(), appliesTo, missingRequirement.requirementName);
            assertEquals("The exception should say which resources owned the requirement that wasn't found "
                         + e.getAllRequirementsResourcesNotFound(), testResource, missingRequirement.owningResource);
            assertEquals("There should only be one product that couldn't be resolved", 1, e.getMissingProducts().size());
            assertTrue("The exception should list which products samples were found for: " + e.getMissingProducts(),
                       e.getMissingProducts().contains(new ProductRequirementInformation("6.0.0.0", "com.ibm.ws.test.product", null, null, Collections.singletonList("DEVELOPERS"))));
            assertEquals("The exception should contain the right minimum product value", "6.0.0.0", e.getMinimumVersionForMissingProduct(null, null, null));
            assertNull("The exception should have no max product version for an unbounded range", e.getMaximumVersionForMissingProduct("com.ibm.websphere.appserver", null, null));
        }

    }

    /**
     * Tests the exception when a sample is missing a feature
     *
     * @throws RepositoryException
     */
    @Test
    public void testSampleMissingFeature() throws RepositoryException {
        String name = "testSampleWithFeatureDependency";
        String featureSymbolicName = "com.ibm.ws.feature";
        SampleResourceImpl sampleResource = createSampleResource(name);
        sampleResource.setRequireFeature(Collections.singleton(featureSymbolicName));
        uploadResource(sampleResource);

        // Now see if we can resolve it!
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        try {
            resolver.resolve(name);
            fail("The sample is missing a dependency so should not resolve");
        } catch (RepositoryResolutionException e) {
            assertTrue("The resolution message should state the top level feature that couldn't be resolved but it is: " + e.getMessage(),
                       e.getMessage().contains("resource=" + name));
            assertEquals("There should only be one top level resource that wasn't resolved", 1, e.getTopLevelFeaturesNotResolved().size());
            assertTrue("The correct top level feature should be listed", e.getTopLevelFeaturesNotResolved().contains(name));
            assertEquals("Only one feature should not be found", 1, e.getAllRequirementsNotFound().size());
            assertTrue("The exception should say which feature actually wasn't found", e.getAllRequirementsNotFound().contains(featureSymbolicName));
            assertEquals("Only one requirement should not be found", 1, e.getAllRequirementsResourcesNotFound().size());
            MissingRequirement missingRequirement = e.getAllRequirementsResourcesNotFound().iterator().next();
            assertEquals("The exception should say which requirement actually wasn't found and it should be the requirement on the product for the feature "
                         + e.getAllRequirementsResourcesNotFound(), featureSymbolicName, missingRequirement.requirementName);
            assertEquals("The exception should say which resources owned the requirement that wasn't found "
                         + e.getAllRequirementsResourcesNotFound(), sampleResource, missingRequirement.owningResource);
            assertTrue("The exception should not have any missing products listed", e.getMissingProducts().isEmpty());
            assertNull("The exception should not have a minimum product value", e.getMinimumVersionForMissingProduct(null, null, null));
            assertNull("The exception should not have a maximum product value", e.getMaximumVersionForMissingProduct(null, null, null));
        }
    }

    /**
     * Tests that circles in features are ok. Squares never are though.
     *
     * @throws RepositoryException
     */
    @Test
    public void testCircularRelationship() throws RepositoryException {
        // Add two test resources to massive, we want to make sure they both are returned in the right order
        String firstSymbolicName = "com.ibm.ws.test-1.0";
        String secondSymbolicName = "com.ibm.ws.test.dep-1.0";
        EsaResourceImpl firstResource = createEsaResource(firstSymbolicName, null, null, Collections.singleton(secondSymbolicName), null);
        EsaResourceImpl dependencyResource = createEsaResource(secondSymbolicName, null, "1.0.0.1", Collections.singleton(firstSymbolicName), null);

        // Now see if we can resolve it!
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(firstSymbolicName);
        assertEquals("There should only be a single list of resources", 1, resolvedResources.size());

        // Order is weird here, the two resources are effectively symbiotic so could be installed in either order but we asked for the first one so expect that to be installed last
        assertEquals("There should be two resolved resource", 2, resolvedResources.iterator().next().size());
        assertEquals("The dependency should be installed first", dependencyResource, resolvedResources.iterator().next().get(0));
        assertEquals("The resource being resolved should be installed second", firstResource, resolvedResources.iterator().next().get(1));
    }

    /**
     * Tests that circles in features caused by an auto feature are ok.
     *
     * @throws RepositoryException
     */
    @Test
    public void testAutoFeatureCircularRelationship() throws RepositoryException {
        // Add a test resource to massive
        String symbolicName = "com.ibm.ws.test-1.0";
        String autoFeatureSymbolicName = "satisfied.auto.feature";
        EsaResourceImpl testResource = createEsaResource(symbolicName, null, null, Collections.singleton(autoFeatureSymbolicName), null);

        // Now add the soon-to-be-satisfied auto feature
        EsaResourceImpl autoFeature = createEsaResource(autoFeatureSymbolicName, null, null, null, null, Collections.singleton(symbolicName), true, null);

        // Now see if we can resolve it!
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(symbolicName);
        assertEquals("There should only be two lists of resources one for the feature being installed and one for the auto feature, set is:" + resolvedResources, 2,
                     resolvedResources.size());
        boolean foundFeatureList = false;
        boolean foundAutoList = false;
        for (List<RepositoryResource> resolvedList : resolvedResources) {
            // As it's a circle you should have two lists with both in but in different order
            assertEquals(2, resolvedList.size());
            if (resolvedList.get(0).equals(testResource) && resolvedList.get(1).equals(autoFeature)) {
                foundFeatureList = true;
            } else if (resolvedList.get(1).equals(testResource) && resolvedList.get(0).equals(autoFeature)) {
                foundAutoList = true;

            } else {
                fail("Unexpected list in the resolve resources: " + resolvedList);
            }
        }
        assertTrue("Should have found feature", foundFeatureList);
        assertTrue("Should have found auto", foundAutoList);
    }

    /**
     * Same as {@link #testChainedFeatureDependenciesWithMultipleRoutes()} but with a circle at the end.
     */
    @Test
    public void testChainedFeatureDependenciesWithMultipleRoutesWithCircle() throws RepositoryException, ResolutionException {
        // Add four test resources in a chain to massive with the final resource having two routes to it
        String firstSymbolicName = "com.ibm.ws.test-1.0";
        String secondSymbolicName = "com.ibm.ws.test.dep-1.0";
        String thirdSymbolicName = "com.ibm.ws.test.second.dep-1.0";
        String fourthSymbolicName = "com.ibm.ws.test.third.dep-1.0";
        Collection<String> dependencies = new HashSet<String>();
        dependencies.add(secondSymbolicName);
        dependencies.add(thirdSymbolicName);
        EsaResourceImpl firstResource = createEsaResource(firstSymbolicName, null, null, dependencies, null);
        EsaResourceImpl firstDependency = createEsaResource(secondSymbolicName, null, null, Collections.singleton(fourthSymbolicName), null);
        EsaResourceImpl secondDependency = createEsaResource(thirdSymbolicName, null, null, Collections.singleton(fourthSymbolicName), null);
        EsaResourceImpl finalDependency = createEsaResource(fourthSymbolicName, null, null, Collections.singleton(firstSymbolicName), null);

        // Now see if we can resolve it!
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(firstSymbolicName);
        assertEquals("There should only be a single list of resources", 1, resolvedResources.size());
        assertEquals("There should be four resolved resource", 4, resolvedResources.iterator().next().size());
        assertEquals("The final dependency in massive should be installed first", finalDependency, resolvedResources.iterator().next().get(0));
        assertEquals("The resource being resolved should be installed last", firstResource, resolvedResources.iterator().next().get(3));

        // The order of the middle two doesn't matter as long as they are in the middle somewhere
        assertTrue("The first dependency should be in the resolved list", resolvedResources.iterator().next().contains(firstDependency));
        assertTrue("The second dependency should be in the resolved list", resolvedResources.iterator().next().contains(secondDependency));
    }

    /**
     * Test that if you have a resource with two dependencies, one of which causes a circle then it doesn't loop around the circle.
     */
    @Test
    public void testMultipleDependenciesWithOneCircle() throws RepositoryException, ResolutionException {
        // Add four test resources in a chain to massive with the final resource having two routes to it
        String firstSymbolicName = "com.ibm.ws.test-1.0";
        String secondSymbolicName = "com.ibm.ws.test.dep-1.0";
        String thirdSymbolicName = "com.ibm.ws.test.second.dep-1.0";
        Collection<String> dependencies = new HashSet<String>();
        dependencies.add(secondSymbolicName);
        dependencies.add(thirdSymbolicName);
        EsaResourceImpl firstResource = createEsaResource(firstSymbolicName, null, null, dependencies, null);
        EsaResourceImpl firstDependency = createEsaResource(secondSymbolicName, null, null, Collections.singleton(firstSymbolicName), null);
        EsaResourceImpl secondDependency = createEsaResource(thirdSymbolicName, null, null);

        // Now see if we can resolve it!
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(firstSymbolicName);
        assertEquals("There should only be a single list of resources", 1, resolvedResources.size());
        assertEquals("There should be four resolved resource", 3, resolvedResources.iterator().next().size());
        assertEquals("The resource being resolved should be installed last", firstResource, resolvedResources.iterator().next().get(2));

        // The order of the middle two doesn't matter as long as they are in the middle somewhere
        assertTrue("The first dependency should be in the resolved list", resolvedResources.iterator().next().contains(firstDependency));
        assertTrue("The second dependency should be in the resolved list", resolvedResources.iterator().next().contains(secondDependency));
    }

    /**
     * Has a -> [b -> d -> f -> [a, g], c -> e -> f -> [a, g]]. b -> d -> e -> f -> [a, g].
     *
     * Tests that we always put f's dependency on g at the start of the install list.
     */
    @Test
    public void testLongRoutesWithCircleAndTail() throws RepositoryException, ResolutionException {
        // Add the test resources in a chain to massive with the f resource having two routes to it
        String aSymbolicName = "com.ibm.ws.testA-1.0";
        String bSymbolicName = "com.ibm.ws.testB-1.0";
        String cSymbolicName = "com.ibm.ws.testC-1.0";
        String dSymbolicName = "com.ibm.ws.testD-1.0";
        String eSymbolicName = "com.ibm.ws.testE-1.0";
        String fSymbolicName = "com.ibm.ws.testF-1.0";
        String gSymbolicName = "com.ibm.ws.testG-1.0";
        Collection<String> dependencies = new HashSet<String>();
        dependencies.add(bSymbolicName);
        dependencies.add(cSymbolicName);
        EsaResourceImpl aResource = createEsaResource(aSymbolicName, null, null, dependencies, null);
        EsaResourceImpl bResource = createEsaResource(bSymbolicName, null, null, Collections.singleton(dSymbolicName), null);
        EsaResourceImpl cResource = createEsaResource(cSymbolicName, null, null, Collections.singleton(eSymbolicName), null);
        EsaResourceImpl dResource = createEsaResource(dSymbolicName, null, null, Collections.singleton(fSymbolicName), null);
        EsaResourceImpl eResource = createEsaResource(eSymbolicName, null, null, Collections.singleton(fSymbolicName), null);
        Collection<String> fDependencies = new HashSet<String>();
        fDependencies.add(aSymbolicName);
        fDependencies.add(gSymbolicName);
        EsaResourceImpl fResource = createEsaResource(fSymbolicName, null, null, fDependencies, null);
        EsaResourceImpl gResource = createEsaResource(gSymbolicName, null, null);

        // Now see if we can resolve it!
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(aSymbolicName);
        assertEquals("There should only be a single list of resources", 1, resolvedResources.size());
        assertEquals("There should be 7 resolved resources", 7, resolvedResources.iterator().next().size());
        List<RepositoryResource> resourceList = resolvedResources.iterator().next();
        assertEquals("The tail dependency in massive should be installed first", gResource, resourceList.get(0));
        assertEquals("The resource being resolved should be installed last", fResource, resourceList.get(1));
        assertTrue("The resource in the chains should be there", resourceList.contains(bResource));
        assertTrue("The resource in the chains should be there", resourceList.contains(cResource));
        assertTrue("The resource in the chains should be there", resourceList.contains(dResource));
        assertTrue("The resource in the chains should be there", resourceList.contains(eResource));
        assertEquals("The resource being resolved should be installed last", aResource, resourceList.get(6));
    }

    /**
     * Has a -> b -> c -> b
     */
    @Test
    public void testCircleNotToRoot() throws Exception {
        // Setup resources
        String aSymbolicName = "com.ibm.ws.testA-1.0";
        String bSymbolicName = "com.ibm.ws.testB-1.0";
        String cSymbolicName = "com.ibm.ws.testC-1.0";
        Collection<String> dependencies = new HashSet<String>();
        dependencies.add(bSymbolicName);
        dependencies.add(cSymbolicName);
        EsaResourceImpl aResource = createEsaResource(aSymbolicName, null, null, Collections.singleton(bSymbolicName), null);
        EsaResourceImpl bResource = createEsaResource(bSymbolicName, null, null, Collections.singleton(cSymbolicName), null);
        EsaResourceImpl cResource = createEsaResource(cSymbolicName, null, null, Collections.singleton(bSymbolicName), null);

        // Now see if we can resolve it!
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> emptySet(), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(aSymbolicName);
        assertEquals("There should only be a single list of resources", 1, resolvedResources.size());
        assertEquals("There should be 3 resolved resources", 3, resolvedResources.iterator().next().size());
        List<RepositoryResource> resourceList = resolvedResources.iterator().next();
        assertEquals(cResource, resourceList.get(0));
        assertEquals(bResource, resourceList.get(1));
        assertEquals(aResource, resourceList.get(2));
    }

    /**
     * Run a test to make sure that a sample with an applies to set is resolved correctly
     *
     * @param name The name of the sample
     * @param appliesTo The applies to to put onto the sample
     * @param productVersion The product version to be
     * @throws RepositoryResourceException
     * @throws RepositoryBackendException
     * @throws IOException
     * @throws ProductInfoParseException
     * @throws RepositoryException
     * @throws RepositoryResolutionException
     */
    private void runAppliesToSampleTest(String name, String appliesTo,
                                        String productVersion) throws RepositoryResourceException, RepositoryBackendException, IOException, ProductInfoParseException, RepositoryException, RepositoryResolutionException {
        SampleResourceImpl testResource = createSampleResource(name);
        testResource.setAppliesTo(appliesTo);
        uploadResource(testResource);

        runTestAgainstProductDefinitionWithSingleResult(name, productVersion, testResource);
    }

    /**
     * Run a test against a product definition with the supplied version and expect a single result back.
     *
     * @param name The name to resolve
     * @param productVersion The product version to use
     * @param testResource The resource to expect
     * @throws IOException
     * @throws ProductInfoParseException
     * @throws RepositoryException
     * @throws RepositoryResolutionException
     */
    private void runTestAgainstProductDefinitionWithSingleResult(String name, String productVersion,
                                                                 RepositoryResourceImpl testResource) throws IOException, ProductInfoParseException, RepositoryException, RepositoryResolutionException {
        ProductInfo productInfo = ResolverTestUtils.createProductInfo("com.ibm.ws.test.product", "DEVELOPERS", productVersion, null, null);
        RepositoryResolver resolver = new RepositoryResolver(Collections.<ProductDefinition> singleton(new ProductInfoProductDefinition(productInfo)), Collections.<ProvisioningFeatureDefinition> emptySet(), Collections.<IFixInfo> emptySet(), _loginInfoList);
        Collection<List<RepositoryResource>> resolvedResources = resolver.resolve(name);
        assertEquals("There should only be a single list of resources", 1, resolvedResources.size());
        assertEquals("There should be one resolved resource", 1, resolvedResources.iterator().next().size());
        assertTrue("The resolved resource should be the one we supplied", resolvedResources.iterator().next().contains(testResource));
    }

    /**
     * Create a {@link SampleResourceImpl} of type {@link ResourceType#PRODUCTSAMPLE} with the supplied short name
     *
     * @param shortName
     * @return
     * @throws RepositoryBackendException
     * @throws RepositoryResourceException
     */
    private SampleResourceImpl createAndUploadSampleResource(String shortName) throws RepositoryResourceException, RepositoryBackendException {
        SampleResourceImpl testResource = createSampleResource(shortName);
        uploadResource(testResource);
        return testResource;
    }

    /**
     * @return
     */
    private SampleResourceImpl createSampleResource(String shortName) {
        SampleResourceImpl testResource = new SampleResourceImpl(getConnection());
        testResource.setType(ResourceType.PRODUCTSAMPLE);
        testResource.setShortName(shortName);
        testResource.setName(shortName + " Full Name");
        return testResource;
    }

    /**
     * Creates an {@link EsaResourceImpl} and uploads it to massive with just the core fields set.
     *
     * @param symbolicName The symbolic name of the resource
     * @param shortName The short name of the resource
     * @param version The version of the resource
     * @return The resource
     * @throws RepositoryResourceException
     * @throws RepositoryBackendException
     */
    private EsaResourceImpl createEsaResource(String symbolicName, String shortName, String version) throws RepositoryResourceException, RepositoryBackendException {
        return createEsaResource(symbolicName, shortName, version, null, null);
    }

    /**
     * Creates an ESA resource with a set of required iFixes, only the symbolic name will be set from the core fields.
     *
     * @param symbolicName
     * @param singleton
     * @return
     * @throws RepositoryResourceException
     * @throws RepositoryBackendException
     */
    private EsaResourceImpl createEsaResource(String symbolicName, Collection<String> fixes) throws RepositoryResourceException, RepositoryBackendException {
        return createEsaResource(symbolicName, null, null, null, null, null, false, fixes);
    }

    /**
     * Creates an {@link EsaResourceImpl} and uploads it to massive with all fields set but no fixes
     *
     * @param symbolicName The symbolic name of the resource
     * @param shortName The short name of the resource
     * @param version The version of the resource
     * @param dependencySymoblicName The symbolic names of dependencies
     * @param appliesTo The product this feature applies to
     * @return The resource
     * @throws RepositoryBackendException
     */
    private EsaResourceImpl createEsaResource(String symbolicName, String shortName, String version, Collection<String> dependencySymoblicNames,
                                              String appliesTo) throws RepositoryResourceException, RepositoryBackendException {
        return createEsaResource(symbolicName, shortName, version, dependencySymoblicNames, appliesTo, null, false, null);
    }

    /**
     * Creates an {@link EsaResourceImpl} and uploads it to massive with all fields set.
     *
     * @param symbolicName The symbolic name of the resource
     * @param shortName The short name of the resource
     * @param version The version of the resource
     * @param appliesTo The product this feature applies to
     * @param dependencySymoblicName The symbolic names of dependencies
     * @param provisionSymbolicNames The symbolic name(s) of the capability required for this feature to be auto provision
     * @param autoInstallable The autoInstallable value to use
     * @param requiredFixes fixes required by this feature
     * @return The resource
     * @throws RepositoryBackendException
     */
    private EsaResourceImpl createEsaResource(String symbolicName, String shortName, String version, Collection<String> dependencySymoblicNames, String appliesTo,
                                              Collection<String> provisionSymbolicNames, boolean autoInstallable,
                                              Collection<String> requiredFixes) throws RepositoryResourceException, RepositoryBackendException {
        EsaResourceImpl testResource = new EsaResourceImpl(getConnection());
        testResource.setProvideFeature(symbolicName);
        testResource.setShortName(shortName);
        testResource.setVersion(version);
        testResource.setRequireFeature(dependencySymoblicNames);
        testResource.setAppliesTo(appliesTo);
        String name = "name";
        if (shortName != null) {
            name = shortName;
        } else if (symbolicName != null) {
            name = symbolicName;
        }
        testResource.setName(name);
        if (provisionSymbolicNames != null && !provisionSymbolicNames.isEmpty()) {
            boolean first = true;
            StringBuilder ibmProvisionCapability = new StringBuilder();
            for (String provisionSymbolicName : provisionSymbolicNames) {
                if (!first) {
                    ibmProvisionCapability.append(",");
                }
                ibmProvisionCapability.append("osgi.identity; filter:=\"(&(type=osgi.subsystem.feature)(osgi.identity=" + provisionSymbolicName + "))\"");
                first = false;
            }
            testResource.setProvisionCapability(ibmProvisionCapability.toString());
        }
        testResource.setInstallPolicy(autoInstallable ? InstallPolicy.WHEN_SATISFIED : InstallPolicy.MANUAL);
        if (requiredFixes != null) {
            for (String fix : requiredFixes) {
                testResource.addRequireFix(fix);
            }
        }
        uploadResource(testResource);
        return testResource;
    }

    /**
     * Creates and uploads a new IfixResource in Massive
     *
     * @param fixId
     * @param appliesTo
     * @param lastUpdateDate
     * @return
     * @throws RepositoryResourceException
     * @throws RepositoryBackendException
     */
    private IfixResourceImpl createIFixResource(String fixId, String appliesTo, Date lastUpdateDate) throws RepositoryResourceException, RepositoryBackendException {
        IfixResourceImpl iFixResource = new IfixResourceImpl(getConnection());
        iFixResource.setProvideFix(Collections.singleton(fixId));
        iFixResource.setAppliesTo(appliesTo);
        iFixResource.setDate(lastUpdateDate);
        iFixResource.setName("ifix " + _count++);
        iFixResource.setProviderName("IBM");
        uploadResource(iFixResource);
        return iFixResource;
    }

    private void uploadResource(RepositoryResourceImpl mr)
                    throws RepositoryResourceException, RepositoryBackendException {
        mr.uploadToMassive(new BasicTestStrategy());
    }

    /**
     * A really stupid strategy, that assumes all you want to do is add one asset
     * to an empty repository, and not change it's state, or hide anything else
     * or anything really.
     */
    public static class BasicTestStrategy extends BaseStrategy {
        @Override
        public void uploadAsset(RepositoryResourceImpl resource, List<RepositoryResourceImpl> matchingResources) throws RepositoryBackendException, RepositoryResourceException {
            resource.addAsset();
        }

    }

}
