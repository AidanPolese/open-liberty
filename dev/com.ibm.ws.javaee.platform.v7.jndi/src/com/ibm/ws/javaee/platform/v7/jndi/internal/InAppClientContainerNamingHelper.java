/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.platform.v7.jndi.internal;

import java.util.Collection;
import java.util.Collections;

import javax.naming.NameClassPair;
import javax.naming.NamingException;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import com.ibm.ws.container.service.naming.JavaColonNamingHelper;
import com.ibm.ws.container.service.naming.NamingConstants.JavaColonNamespace;
import com.ibm.wsspi.kernel.service.location.WsLocationConstants;

/**
 * Return the Boolean value true for java:comp/InAppClientContainer when the process is running in client container.
 */
@Component(service = JavaColonNamingHelper.class)
public class InAppClientContainerNamingHelper implements JavaColonNamingHelper {
    private static final String COMP_NAME = "InAppClientContainer";
    private BundleContext bundleContext;

    @Activate
    protected void activate(ComponentContext cc) {
        bundleContext = cc.getBundleContext();
    }

    @Deactivate
    protected void deactivate(ComponentContext cc) {}

    @Override
    public Object getObjectInstance(JavaColonNamespace namespace, String name) throws NamingException {
        if (JavaColonNamespace.COMP == namespace && COMP_NAME.equals(name)) {
            return Boolean.valueOf(WsLocationConstants.LOC_PROCESS_TYPE_CLIENT.equals(bundleContext.getProperty(WsLocationConstants.LOC_PROCESS_TYPE)));
        }
        return null;
    }

    @Override
    public boolean hasObjectWithPrefix(JavaColonNamespace namespace, String name) throws NamingException {
        return namespace == JavaColonNamespace.COMP && name.isEmpty();
    }

    @Override
    public Collection<? extends NameClassPair> listInstances(JavaColonNamespace namespace, String nameInContext) throws NamingException {
        if (namespace == JavaColonNamespace.COMP && nameInContext.isEmpty()) {
            return Collections.singletonList(new NameClassPair(COMP_NAME, String.class.getName()));
        }
        return Collections.emptyList();
    }

}
