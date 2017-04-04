/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jaxws.bus;

import java.util.Map;

import org.apache.cxf.bus.extension.ExtensionManagerBus;

/**
 *
 */
public class LibertyApplicationBus extends ExtensionManagerBus {

    public enum Type {
        SERVER, CLIENT
    }

    /**
     * @param e
     * @param properties
     * @param extensionClassLoader
     */
    public LibertyApplicationBus(Map<Class<?>, Object> e, Map<String, Object> properties, ClassLoader extensionClassLoader) {
        super(e, properties, extensionClassLoader);
    }

    /**
     * Comparing with getExtension method, getLocalExtension will not trigger to create/search ConfiguredBeanLocator
     * 
     * @param extensionType
     * @return
     */
    public <T> T getLocalExtension(Class<T> extensionType) {
        Object obj = extensions.get(extensionType);
        if (null != obj) {
            return extensionType.cast(obj);
        }
        return null;
    }

    /**
     * Comparing with hasExtensionByName method, hasLocalExtension will not trigger to create/search ConfiguredBeanLocator
     * Also, it will class instance to search the extension map.
     * 
     * @param extensionType
     * @return
     */
    public boolean hasLocalExtension(Class<?> extensionType) {
        return extensions.containsKey(extensionType);
    }
}
