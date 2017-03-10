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
package com.ibm.ws.logging.internal;

import java.lang.reflect.Constructor;

import com.ibm.websphere.ras.TraceComponent;

/**
 * Access to the internals of the com.ibm.websphere.ras package.
 */
public abstract class TrSharedSecrets {
    private static TrSharedSecrets instance = createInstance();

    private static TrSharedSecrets createInstance() {
        try {
            Class<? extends TrSharedSecrets> implClass = Class.forName("com.ibm.websphere.ras.TrSharedSecretsImpl").asSubclass(TrSharedSecrets.class);
            Constructor<? extends TrSharedSecrets> constructor = implClass.getDeclaredConstructor((Class<?>[]) null);
            constructor.setAccessible(true);
            return constructor.newInstance((Object[]) null);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static TrSharedSecrets getInstance() {
        return instance;
    }

    public abstract void addGroup(TraceComponent tc, String group);
}
