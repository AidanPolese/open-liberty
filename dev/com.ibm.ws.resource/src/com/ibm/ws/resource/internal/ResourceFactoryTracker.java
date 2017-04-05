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
package com.ibm.ws.resource.internal;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import com.ibm.wsspi.resource.ResourceFactory;

public class ResourceFactoryTracker implements ServiceTrackerCustomizer<ResourceFactory, ResourceFactoryTrackerData> {
    private static final String FILTER = "(&" +
                                         "(" + Constants.OBJECTCLASS + "=" + ResourceFactory.class.getName() + ")" +
                                         "(" + ResourceFactory.JNDI_NAME + "=*)" +
                                         "(" + ResourceFactory.CREATES_OBJECT_CLASS + "=*)" +
                                         ")";

    private ServiceTracker<ResourceFactory, ResourceFactoryTrackerData> tracker;

    public void activate(BundleContext context) throws InvalidSyntaxException {
        Filter filter = FrameworkUtil.createFilter(FILTER);
        tracker = new ServiceTracker<ResourceFactory, ResourceFactoryTrackerData>(context, filter, this);
        tracker.open();
    }

    public void deactivate(ComponentContext cc) {
        tracker.close();
    }

    @Override
    public ResourceFactoryTrackerData addingService(ServiceReference<ResourceFactory> ref) {
        ResourceFactoryTrackerData data = new ResourceFactoryTrackerData(ref.getBundle().getBundleContext());
        data.register(ref);
        return data;
    }

    @Override
    public void modifiedService(ServiceReference<ResourceFactory> ref, ResourceFactoryTrackerData data) {
        data.modifed(ref);
    }

    @Override
    public void removedService(ServiceReference<ResourceFactory> ref, ResourceFactoryTrackerData data) {
        data.unregister();
    }
}
