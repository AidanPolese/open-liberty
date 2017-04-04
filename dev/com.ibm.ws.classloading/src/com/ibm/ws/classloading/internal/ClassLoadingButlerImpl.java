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
package com.ibm.ws.classloading.internal;

import java.util.HashSet;
import java.util.Set;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.ws.classloading.ClassLoadingButler;
import com.ibm.wsspi.adaptable.module.Container;
import com.ibm.wsspi.adaptable.module.Notifier.Notification;

public class ClassLoadingButlerImpl implements ClassLoadingButler {
    private final static TraceComponent tc = Tr.register(ClassLoadingButlerImpl.class);
    private final Set<ContainerClassLoader> classLoaders = new HashSet<ContainerClassLoader>();

    ClassLoadingButlerImpl(Container appContainer) {}

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.app.manager.monitor.ClassLoadingButler#setClassLoader(com.ibm.ws.classloading.internal.ContainerClassLoader)
     */
    @Override
    @Trivial
    public void addClassLoader(ClassLoader classLoader) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "addClassLoader - " + classLoader);
        }

        if (classLoader instanceof ContainerClassLoader) {
            synchronized (classLoaders) {
                classLoaders.add((ContainerClassLoader) classLoader);
            }
        } else {
            throw new IllegalArgumentException("classLoader is not a ContainerClassLoader");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.app.manager.monitor.ClassLoadingButler#redefineClasses(com.ibm.wsspi.adaptable.module.Notifier.Notification)
     */
    @Override
    public boolean redefineClasses(Notification notification) {
        // if there are no paths to process, this is a minor update
        if (notification.getPaths().isEmpty()) {
            return true;
        }

        boolean success;
        synchronized (classLoaders) {
            if (classLoaders.isEmpty()) {
                success = false;
            } else {
                success = true;
                for (ContainerClassLoader loader : classLoaders) {
                    if (!loader.redefineClasses(notification)) {
                        success = false;
                        break;
                    }
                }
            }
        }

        return success;
    }

}
