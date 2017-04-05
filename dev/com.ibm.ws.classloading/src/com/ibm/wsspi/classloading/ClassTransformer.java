/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.classloading;

import java.security.CodeSource;

/**
 * This interface allows a class to be transformed before it is loaded.
 * It is intended for use by JPA but is defined here to avoid creating
 * a dependency on any JPA packages.
 */
public interface ClassTransformer {
    /**
     * The following method is to be called before a class is defined.
     * 
     * @param name
     *            Name of the class being defined
     * @param bytes
     *            Byte code as loaded from disk
     * @param source
     *            Code source used to define the class.
     * @param loader
     *            Classloader to create the class from classByte.
     * 
     * @return The transformed byte code returned by the persistence provider. If no transformation
     *         takes place, the original classBytes is returned. All data of the returned byte[]
     *         MUST be used by the classloader to define the POJO entity class. I.e. returnClass =
     *         defineClass(name, classBytes, 0, classBytes.length, cs);
     */
    byte[] transformClass(String name, byte[] bytes, CodeSource source, ClassLoader loader);
}
