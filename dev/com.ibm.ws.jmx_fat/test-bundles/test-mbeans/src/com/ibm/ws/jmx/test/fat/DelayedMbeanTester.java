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
package com.ibm.ws.jmx.test.fat;

import java.util.Hashtable;

import javax.management.DynamicMBean;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

/**
 * Registers and unregisters some "delayed" mbeans that don't like to return the service references
 */
@Component(configurationPolicy = ConfigurationPolicy.IGNORE,
           property = { "jmx.objectname=WebSphere:name=com.ibm.ws.jmx.test.fat.delayedMbeanTester" })
public class DelayedMbeanTester implements DelayedMbeanTesterMBean {

    private BundleContext bundleContext;
    private ServiceRegistration<?> reg1;
    private boolean allow1;
    private ServiceRegistration<?> reg2;

    protected void activate(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    protected void deactivate() {
        this.bundleContext = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.jmx.test.fat.DelayedMbeanTesterMBean#register()
     */
    @Override
    public void register() {
        Hashtable<String, Object> properties1 = new Hashtable<String, Object>();
        properties1.put("jmx.objectname", "WebSphere:name=com.ibm.ws.jmx.test.fat.delayedMbeanTester.Bean1");
        reg1 = bundleContext.registerService(DynamicMBean.class.getName(),
                                             new ServiceFactory<TestBean>() {

                                                 @Override
                                                 public TestBean getService(Bundle bundle, ServiceRegistration registration) {
                                                     return allow1 ? new TestBean("delayed1") : null;
                                                 }

                                                 @Override
                                                 public void ungetService(Bundle bundle, ServiceRegistration registration, TestBean service) {}
                                             },
                                             properties1);
        Hashtable<String, Object> properties2 = new Hashtable<String, Object>();
        properties2.put("jmx.objectname", "WebSphere:name=com.ibm.ws.jmx.test.fat.delayedMbeanTester.Bean2");
        reg2 = bundleContext.registerService(DynamicMBean.class.getName(),
                                             new ServiceFactory<TestBean>() {

                                                 @Override
                                                 public TestBean getService(Bundle bundle, ServiceRegistration registration) {
                                                     return null;
                                                 }

                                                 @Override
                                                 public void ungetService(Bundle bundle, ServiceRegistration registration, TestBean service) {}
                                             },
                                             properties2);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.jmx.test.fat.DelayedMbeanTesterMBean#allow1Service()
     */
    @Override
    public void allow1Service() {
        allow1 = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.jmx.test.fat.DelayedMbeanTesterMBean#unregister()
     */
    @Override
    public void unregister() {
        reg1.unregister();
        reg2.unregister();
    }
}
