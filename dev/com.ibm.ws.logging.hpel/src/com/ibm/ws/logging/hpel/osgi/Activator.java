// %Z% %I% %W% %G% %U% [%H% %T%]
/*
 * IBM Confidential OCO Source Material
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2009
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 *
 * Change History:
 *
 * Reason           Version        Date       User id     Description
 * ----------------------------------------------------------------------------
 * 633358           8.0         12/28/2009    spaungam     server does not start when this logger is activated
 */


package com.ibm.ws.logging.hpel.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {
//    private ProviderTracker providerTracker;
//    private static Logger tracer;
    private static BundleContext bundleContext = null;

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
    	 //tracer = Logger.getLogger(Activator.class.getName());
        // create a default provider and register it
//        context.registerService(RASHPELProvider.class.getName(), makeDefaultProvider(), new Hashtable<String, String>());
        // create a tracker and track the log service
//        providerTracker = new ProviderTracker(context);
//        providerTracker.open();
        bundleContext = context;/*
        if (tracer.isLoggable(Level.FINE))
            tracer.log(Level.FINE, "RASHPEL started");*/
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
//        providerTracker.close();
/*//        providerTracker = null;
        if (tracer.isLoggable(Level.FINE))
            tracer.log(Level.FINE, "RASHPEL stopped");*/
    }

    /**
     * provide bundle context for this bundle for some dynamic OSGi work
     * 
     * @return the OSGi bundle context used when starting this bundle
     */
    public static BundleContext getBundleContext() {
        return bundleContext;
    }

}
