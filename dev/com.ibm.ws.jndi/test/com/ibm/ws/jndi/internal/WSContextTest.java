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
package com.ibm.ws.jndi.internal;

import java.util.Dictionary;

import javax.naming.Context;
import javax.naming.Reference;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

public class WSContextTest {
    private final Mockery mockery = new Mockery();
    private int mockeryId;
    private final BundleContext bundleContext = mockery.mock(BundleContext.class);

    private ServiceReference<?> mockServiceReference(final String objectClass,
                                                     final String origin,
                                                     final String explicitClassName,
                                                     final String refClassName) {
        final ServiceReference<?> ref = mockery.mock(ServiceReference.class, ServiceReference.class + " " + mockeryId++);
        mockery.checking(new Expectations() {
            {
                allowing(ref).getProperty(with(Constants.OBJECTCLASS));
                will(returnValue(new String[] { objectClass }));
                allowing(ref).getProperty(with(JNDIServiceBinder.OSGI_JNDI_SERVICE_ORIGIN));
                will(returnValue(origin));
                allowing(ref).getProperty(with(JNDIServiceBinder.OSGI_JNDI_SERVICE_CLASS));
                will(returnValue(explicitClassName));
                if (refClassName != null) {
                    allowing(bundleContext).getService(with(ref));
                    will(returnValue(new Reference(refClassName)));
                }
            }
        });
        return ref;
    }

    @Test
    public void testResolveObjectClassName() {
        WSContext c = new WSContext(bundleContext, null, null);

        final String CO = Object.class.getName();
        final String CC = Context.class.getName();
        final String CI = Integer.class.getName();
        final String CN = Number.class.getName();
        final String CR = Reference.class.getName();
        final String OV = JNDIServiceBinder.OSGI_JNDI_SERVICE_ORIGIN_VALUE;

        Assert.assertEquals(null, c.resolveObjectClassName(null));
        Assert.assertEquals(CI, c.resolveObjectClassName(0));
        Assert.assertEquals(CC, c.resolveObjectClassName(new ContextNode()));

        for (ServiceReferenceWrapper wrapper : ServiceReferenceWrapper.values()) {
            Assert.assertEquals(null, c.resolveObjectClassName(wrapper.wrap(mockServiceReference(CO, OV, null, null))));
            Assert.assertEquals(CN, c.resolveObjectClassName(wrapper.wrap(mockServiceReference(CO, OV, CN, null))));
            Assert.assertEquals(CI, c.resolveObjectClassName(wrapper.wrap(mockServiceReference(CI, null, null, null))));
            Assert.assertEquals(CN, c.resolveObjectClassName(wrapper.wrap(mockServiceReference(CI, null, CN, null))));
            Assert.assertEquals(CN, c.resolveObjectClassName(wrapper.wrap(mockServiceReference(CR, null, null, CN))));
        }
    }

    enum ServiceReferenceWrapper {
        ServiceReference {
            @Override
            public <T> Object wrap(ServiceReference<T> ref) {
                return ref;
            }
        },
        AutoBindNode {
            @Override
            public <T> Object wrap(ServiceReference<T> ref) {
                return new AutoBindNode(ref);
            }
        },
        ServiceRegistration {
            @Override
            public <T> Object wrap(final ServiceReference<T> ref) {
                return new ServiceRegistration<T>() {
                    @Override
                    public ServiceReference<T> getReference() {
                        return ref;
                    }

                    @Override
                    public void setProperties(Dictionary<String, ?> properties) {}

                    @Override
                    public void unregister() {}
                };
            }
        };

        public abstract <T> Object wrap(ServiceReference<T> ref);
    }
}
