/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.classloader.context.internal;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.RejectedExecutionException;

import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.wsspi.threadcontext.ThreadContext;

/**
 * Classloader context implementation.
 */
public class ClassloaderContextImpl implements ThreadContext {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 2429635965226179594L;

    /**
     * Names of serializable fields.
     * A single character is used for each to reduce the space required.
     */
    static final String CLASS_LOADER_IDENTIFIER = "I";

    /**
     * Fields to serialize.
     */
    private static final ObjectStreamField[] serialPersistentFields =
                    new ObjectStreamField[] {
                                             new ObjectStreamField(CLASS_LOADER_IDENTIFIER, String.class)
                    };

    /**
     * An empty classloader context which erases any classloader context on the thread of execution.
     */
    static final ClassLoader SYSTEM_CLASS_LOADER = AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
        @Override
        public ClassLoader run() {
            // This matches the default used by Java.  We might consider using
            // ClassLoadingService.createThreadContextClassLoader in the future.
            return ClassLoader.getSystemClassLoader();
        }
    });

    transient ClassloaderContextProviderImpl classLoaderContextProvider;

    /**
     * The classloader to propagate.
     */
    transient ClassLoader classLoaderToPropagate;

    /**
     * Class loader identifier. May be null until serialized or deserialized.
     */
    transient String classLoaderIdentifier;

    /**
     * Class loader to restore to the thread after a contextual task completes.
     */
    private transient ClassLoader previousClassLoader;

    /**
     * A context that propagates the current thread context class loader.
     * 
     * @param classloaderContextProviderImpl
     */
    ClassloaderContextImpl(ClassloaderContextProviderImpl provider) {
        this(provider, getCL());
    }

    /**
     * Constructor
     * 
     * @param provider
     * @param classLoaderIdentifier the id of the classloader for this context
     */
    ClassloaderContextImpl(ClassloaderContextProviderImpl provider, String classLoaderIdentifier) {
        this.classLoaderContextProvider = provider;
        this.classLoaderIdentifier = classLoaderIdentifier;
    }

    /**
     * Constructor.
     * 
     * @param cl the class loader to propagate
     */
    ClassloaderContextImpl(ClassloaderContextProviderImpl provider, ClassLoader cl) {
        classLoaderContextProvider = provider;
        classLoaderToPropagate = cl;
    }

    /** {@inheritDoc} */
    @Override
    public ThreadContext clone() {
        try {
            ClassloaderContextImpl copy = (ClassloaderContextImpl) super.clone();
            copy.previousClassLoader = null;
            return copy;
        } catch (CloneNotSupportedException x) {
            throw new RuntimeException(x);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void taskStarting() throws RejectedExecutionException {

        if (classLoaderIdentifier != null && classLoaderToPropagate == null) {
            classLoaderToPropagate = classLoaderIdentifier.length() == 0
                            ? ClassloaderContextImpl.SYSTEM_CLASS_LOADER
                            : classLoaderContextProvider.classLoaderIdentifierService.getClassLoader(classLoaderIdentifier);
        }

        // Save the current thread's classLoader and set the propagated classloader on the current thread.
        previousClassLoader = getCL();
        setCL(classLoaderToPropagate);
    }

    /** {@inheritDoc} */
    @Override
    public void taskStopping() {

        // Retrieve the current thread's original classloader and set it back.
        setCL(previousClassLoader);
        previousClassLoader = null;
    }

    /**
     * Sets the provided classloader on the current thread.
     * 
     * @param cl The clasloader to be set.
     */
    private void setCL(final ClassLoader cl) {
        PrivilegedAction<Object> action = new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                Thread.currentThread().setContextClassLoader(cl);
                return null;
            }
        };
        AccessController.doPrivileged(action);
    }

    /**
     * Retrieves the classloader on the current thread.
     * 
     * @return The classloader on the current thread.
     */
    private static ClassLoader getCL() {
        PrivilegedAction<ClassLoader> action = new PrivilegedAction<ClassLoader>() {
            @Override
            public ClassLoader run()
            {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                return cl;
            }
        };

        return AccessController.doPrivileged(action);
    }

    /**
     * Reads and deserializes the input object.
     * 
     * @param in The object to deserialize.
     * 
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        GetField fields = in.readFields();
        classLoaderIdentifier = (String) fields.get(CLASS_LOADER_IDENTIFIER, null);

        // Note that further processing is required in JEEMetadataContextProviderImpl.deserializeThreadContext
        // in order to re-establish the thread context based on the metadata identifier if not defaulted.
    }

    @Override
    @Trivial
    public String toString() {
        StringBuilder sb = new StringBuilder(100)
                        .append(getClass().getSimpleName()).append('@').append(Integer.toHexString(hashCode())).append(' ');
        if (classLoaderIdentifier != null)
            sb.append(classLoaderIdentifier);
        else
            sb.append(classLoaderToPropagate);
        return sb.toString();
    }

    /**
     * Serialize the given object.
     * 
     * @param outStream The stream to write the serialized data.
     * 
     * @throws IOException
     */
    private void writeObject(ObjectOutputStream outStream) throws IOException {
        if (classLoaderToPropagate == SYSTEM_CLASS_LOADER)
            classLoaderIdentifier = "";
        else if (classLoaderToPropagate != null) {
            classLoaderIdentifier = classLoaderContextProvider.getClassLoaderIdentifier(classLoaderToPropagate);
            if (classLoaderIdentifier == null)
                throw new NotSerializableException(classLoaderToPropagate.getClass().getName());
        }
        PutField fields = outStream.putFields();
        fields.put(CLASS_LOADER_IDENTIFIER, classLoaderIdentifier);
        outStream.writeFields();
    }
}