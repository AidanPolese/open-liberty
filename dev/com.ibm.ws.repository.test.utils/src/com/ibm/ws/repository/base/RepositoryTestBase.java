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

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Before;

import com.ibm.ws.repository.common.enums.ResourceType;
import com.ibm.ws.repository.connections.RepositoryConnection;
import com.ibm.ws.repository.connections.RestRepositoryConnection;
import com.ibm.ws.repository.exceptions.RepositoryBackendException;
import com.ibm.ws.repository.exceptions.RepositoryException;
import com.ibm.ws.repository.exceptions.RepositoryResourceDeletionException;
import com.ibm.ws.repository.exceptions.RepositoryResourceException;
import com.ibm.ws.repository.resources.RepositoryResource;
import com.ibm.ws.repository.resources.internal.RepositoryResourceImpl;
import com.ibm.ws.repository.resources.internal.SampleResourceImpl;
import com.ibm.ws.repository.resources.writeable.RepositoryResourceWritable;
import com.ibm.ws.repository.strategies.writeable.UpdateInPlaceStrategy;
import com.ibm.ws.repository.transport.model.Asset;

public abstract class RepositoryTestBase<T> extends TestBaseClass {

    protected final static File _jarDir = new File("lib/LibertyFATTestFiles/JARs");
    protected final static File _licenseDir = new File("lib/LibertyFATTestFiles/licenses");
    protected final static File _archiveDir = new File("lib/LibertyFATTestFiles/archives");
    protected final static File _readmeDir = new File("lib/LibertyFATTestFiles/Readmes");

    protected static final String PROVIDER_NAME = "Test Provider";

    protected RestRepositoryConnection _restConnection;

    private static final Logger logger = Logger.getLogger(RepositoryTestBase.class.getName());

    // Have to have this since base class throws exception
    public RepositoryTestBase() {
        super();
    }

    protected Collection<String> _newTestAssetIds = new HashSet<String>();

    protected T _testObject;

    /**
     * Create the object we are going to test, this object being one of the
     * utility classes that extend AbstractResource
     * 
     * @param userId
     *            Massive userid
     * @param password
     *            Massive password
     * @param apiKey
     *            Massive apikey
     * @return
     * @throws IOException
     */
    protected abstract T createTestObject(RestRepositoryConnection loginInfo) throws IOException;

    /**
     * Looks for features with symbolic names starting with com.ibm.ws.test and
     * stores them in {@link #existingTestAssetIds}. It also creates the test
     * object
     * 
     * @throws IOException
     */
    @Before
    public void setup() throws IOException {
        // TestCertTrust.trustAll();
        // TestCertTrust.disableSNIExtension();

        _newTestAssetIds = new HashSet<String>();
        _restConnection = (RestRepositoryConnection) _repoConnection;
        _testObject = createTestObject(_restConnection);
    }

    public void recordResource(RepositoryResourceWritable res) {
        logger.log(Level.INFO, "Test resource added:");
        logger.log(Level.INFO, res.toString());
        if (!_newTestAssetIds.contains(res.getId())) {
            _newTestAssetIds.add(res.getId());
        }
    }

    public void removeResource(RepositoryResourceWritable res) {
        _newTestAssetIds.remove(res.getId());
    }

    /**
     * Delete all assets created with the tests provider name. We do a quickn blat before each test, do we need this anymore?
     * 
     * @throws IOException
     * @throws RepositoryResourceDeletionException
     */
    public void deleteCreatedFeatures()
                    throws RepositoryResourceDeletionException, RepositoryException {
        for (String id : _newTestAssetIds) {
            RepositoryResourceImpl res = (RepositoryResourceImpl) _restConnection.getResource(id);
            logger.log(Level.INFO, "Test cleanup deleting resource: "
                                   + res.getId());
            res.delete();
        }
    }

    public void uploadResource(RepositoryResourceImpl mr)
                    throws RepositoryResourceException, RepositoryBackendException {
        mr.uploadToMassive(new UpdateInPlaceStrategy());
        recordResource(mr);
    }

    public void updateResource(RepositoryResourceImpl mr)
                    throws RepositoryResourceException, RepositoryBackendException {
        mr.uploadToMassive(new UpdateInPlaceStrategy());
    }

    public static void checkCopyFields(RepositoryResourceImpl left, RepositoryResourceImpl right)
                    throws IllegalArgumentException, IllegalAccessException,
                    InstantiationException, IOException, NoSuchMethodException,
                    SecurityException, InvocationTargetException {

        ArrayList<String> methodsToIgnore = new ArrayList<String>();
        methodsToIgnore.add("setState");
        methodsToIgnore.add("setType");
        methodsToIgnore.add("setLoginInfoEntry");
        for (Method m : left.getClass().getMethods()) {
            if (m.getName().startsWith("set")) {
                Class<?>[] parameterss = m.getParameterTypes();

                // Not a normal setter, ignore it
                if (parameterss.length != 1) {
                    continue;
                }

                if (methodsToIgnore.contains(m.getName())) {
                    continue;
                }

                Class<?> param = parameterss[0];

                Object p = null;
                if (param.isEnum()) {
                    p = param.getEnumConstants()[0];
                } else if (param.equals(Collection.class)) {
                    logger.log(Level.INFO, "got a collection");
                    p = new ArrayList<Object>();
                } else if (param.isInterface()) {
                    continue;
                } else if (param.isPrimitive()) {
                    p = new Integer(4);
                } else if (param.equals(String.class)) {
                    p = new String("test string");
                } else if (param.equals(RepositoryConnection.class)) {
                    p = new RestRepositoryConnection("a", "b", "c", "d");
                } else {
                    p = param.newInstance();
                }

                m.invoke(left, p);
            }
        }

        Method m = null;

        try {
            m = left.getClass().getDeclaredMethod("copyFieldsFrom",
                                                  RepositoryResourceImpl.class, boolean.class);
        } catch (Exception e) {
            m = left.getClass().getSuperclass().getDeclaredMethod("copyFieldsFrom",
                                                                  RepositoryResourceImpl.class, boolean.class);
        }
        m.setAccessible(true);
        m.invoke(right, left, true);
        if (!left.equivalentWithoutAttachments(right)) {
            logger.log(Level.INFO, "EQUIV FAILED: Left");
            left.dump(System.out);
            logger.log(Level.INFO, "EQUIV FAILED: Right");
            right.dump(System.out);
            fail("Check fields failed: the resources are not equivalent");
        }

        if (!right.equivalentWithoutAttachments(left)) {
            logger.log(Level.INFO, "EQUIV FAILED: Left");
            left.dump(System.out);
            logger.log(Level.INFO, "EQUIV FAILED: Right");
            right.dump(System.out);
            fail("Check fields failed: the resources are not equivalent");
        }
    }

    /**
     * If we need to use the Type enum's createResource method then we can't use a TestResource object (as
     * the enum doesn't know about them), so use a sample resource instead
     * 
     * @throws URISyntaxException
     */
    protected SampleResourceImpl createSampleResource() throws URISyntaxException {
        SampleResourceImpl sampleRes = new SampleResourceImpl(_restConnection);
        populateResource(sampleRes);
        sampleRes.setType(ResourceType.PRODUCTSAMPLE);
        return sampleRes;
    }

    protected void populateResource(RepositoryResourceImpl res) throws URISyntaxException {
        res.setName(name.getMethodName());

        res.setProviderName(PROVIDER_NAME);
        res.setProviderUrl("http://testhost/testfile");

        res.setVersion("version 1");
        res.setDescription("This is a test resource");
    }

    /**
     * Get Asset from MassiveResource reflectively as there is no external API for this
     * 
     * @param mr
     * @return an asset or an exception
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static Asset getAssetReflective(RepositoryResource mr) throws ClassNotFoundException, NoSuchMethodException,
                    SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        // Get MassiveResource
        Class<?> c = RepositoryResourceImpl.class;

        // Get MassiveResource.getAsset()
        Method method = c.getDeclaredMethod("getAsset");

        // Invoke MassiveResource.getAsset()
        method.setAccessible(true);
        Asset asset = (Asset) method.invoke(mr);

        return asset;
    }

    /**
     * Invoke a method reflectively that does not use primitive arguments
     * 
     * @param targetObject
     * @param methodName
     * @param varargs
     * @return
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public static Object reflectiveCallNoPrimitives(Object targetObject, String methodName, Object... varargs)
                    throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, InstantiationException,
                    IllegalArgumentException, InvocationTargetException {

        // Usage example of this method 
        // int i = (Integer)reflectiveCallAnyTypes(targetObject,"methodName",1)

        // create a class array from the vararg object array
        @SuppressWarnings("rawtypes")
        Class[] classes;
        if (varargs != null) {
            classes = new Class[varargs.length];
            for (int i = 0; i < varargs.length; i++) {
                classes[i] = varargs[i].getClass();
            }
        } else {
            classes = new Class[0];
        }

        return reflectiveCallAnyTypes(targetObject, methodName, classes, varargs);
    }

}
