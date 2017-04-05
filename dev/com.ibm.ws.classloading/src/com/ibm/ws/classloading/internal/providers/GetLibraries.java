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
package com.ibm.ws.classloading.internal.providers;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.classloading.internal.util.BlockingList.Listener;
import com.ibm.ws.classloading.internal.util.BlockingList.Retriever;
import com.ibm.ws.classloading.internal.util.BlockingList.Slot;
import com.ibm.ws.classloading.internal.util.ElementNotReadyException;
import com.ibm.wsspi.library.Library;

/**
 * This listener is static (i.e. a nested class rather than an inner class).
 * It holds no references to the AppClassLoader object, so it cannot prevent
 * the AppClassLoader from being collected.
 */
class GetLibraries implements Retriever<String, Library>, Listener<String, Library> {
    static final TraceComponent tc = Tr.register(GetLibraries.class);
    private final String ownerID;

    /** Create a listener that does not listen straight away */
    GetLibraries(String ownerId) {
        this.ownerID = ownerId;
    }

    @Override
    public Library fetch(String id) throws ElementNotReadyException {
        Library result = Providers.getSharedLibrary(id);
        if (result == null)
            throw new ElementNotReadyException(id);
        return result;
    }

    /** invoked by the blocking list when a synchronous fetch operation fails */
    @Override
    public void listenFor(final String libraryId, final Slot<? super Library> slot) {
        // Create a shared library listener
        new AbstractLibraryListener(libraryId, ownerID, Providers.bundleContext) {
            @Override
            void update() {
                final String methodName = "update(): ";
                // the shared library has arrived! 
                Library library = Providers.getSharedLibrary(libraryId);
                if (library == null) {
                    if (tc.isDebugEnabled())
                        Tr.debug(tc, methodName + "class loader " + ownerID + "received a notification from the shared library " + libraryId
                                     + " but the library could not be retrieved.");
                    return; // do nothing, not even deregister!
                }
                slot.fill(library);
                deregister();
            }
        };
    }
}