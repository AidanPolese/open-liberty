/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.threading.internal;

import java.io.PrintWriter;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;

import com.ibm.wsspi.logging.Introspector;
import com.ibm.wsspi.threading.WSExecutorService;

/**
 *
 */
@Component(immediate = true,
           configurationPolicy = ConfigurationPolicy.IGNORE,
           property = { Constants.SERVICE_VENDOR + "=" + "IBM" })
public class ThreadingIntrospector implements Introspector {

    private ExecutorServiceImpl impl;

    @Reference
    protected void setWSExecutorService(WSExecutorService wses) {
        if (wses instanceof ExecutorServiceImpl) {
            impl = (ExecutorServiceImpl) wses;
        }
    }

    protected void unsetWSExecutorService(WSExecutorService wses) {
        impl = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.logging.Introspector#getIntrospectorName()
     */
    @Override
    public String getIntrospectorName() {
        return "ThreadingIntrospector";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.logging.Introspector#getIntrospectorDescription()
     */
    @Override
    public String getIntrospectorDescription() {
        return "Liberty threading diagnostics";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.logging.Introspector#introspect(java.io.PrintWriter)
     */
    @Override
    public void introspect(PrintWriter out) throws Exception {
        if (impl == null) {
            out.println("No ExecutorServiceImpl configured");
        } else {
            ThreadPoolController tpc = impl.threadPoolController;
            tpc.introspect(out);
        }
    }

}
