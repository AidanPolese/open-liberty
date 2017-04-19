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
package com.ibm.ws.jaxrs20.cdi.component;

import javax.enterprise.inject.spi.BeanManager;

import org.osgi.service.component.annotations.Component;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.container.service.app.deploy.ApplicationInfo;
import com.ibm.ws.container.service.state.ApplicationStateListener;
import com.ibm.ws.container.service.state.StateChangeException;

/**
 *
 */
@Component(
           name = "com.ibm.ws.jaxrs20.cdi.component.JAXRSCDIServiceImplByJndi",
           service = { ApplicationStateListener.class },
           property = { "service.vendor=IBM" })
public class JAXRSCDIServiceImplByJndi implements ApplicationStateListener {
    private static final TraceComponent tc = Tr.register(JAXRSCDIServiceImplByJndi.class);

    private static BeanManager beanManager = null;

    public static BeanManager getBeanManager() {
        return beanManager;
    }

    public static void setBeanManager(BeanManager manager) {
        beanManager = manager;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.container.service.state.ApplicationStateListener#applicationStarting(com.ibm.ws.container.service.app.deploy.ApplicationInfo)
     */
    @Override
    public void applicationStarting(ApplicationInfo appInfo) throws StateChangeException {

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.container.service.state.ApplicationStateListener#applicationStarted(com.ibm.ws.container.service.app.deploy.ApplicationInfo)
     */
    @Override
    public void applicationStarted(ApplicationInfo appInfo) throws StateChangeException {

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.container.service.state.ApplicationStateListener#applicationStopping(com.ibm.ws.container.service.app.deploy.ApplicationInfo)
     */
    @Override
    public void applicationStopping(ApplicationInfo appInfo) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.container.service.state.ApplicationStateListener#applicationStopped(com.ibm.ws.container.service.app.deploy.ApplicationInfo)
     */
    @Override
    public void applicationStopped(ApplicationInfo appInfo) {
        beanManager = null;

    }
}
