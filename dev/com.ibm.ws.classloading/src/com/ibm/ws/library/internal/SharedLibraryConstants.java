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
package com.ibm.ws.library.internal;

import com.ibm.websphere.ras.annotation.Trivial;

/**
 *
 */

public final class SharedLibraryConstants {

    public static final String TR_GROUP = "SharedLibrary";
    public static final String NLS_PROPS = "com.ibm.ws.classloading.internal.resources.ClassLoadingServiceMessages";

    public static final String SERVICE_PID = "com.ibm.ws.classloading.sharedlibrary";

    @Trivial
    public enum SharedLibraryAttribute {
        name, id, description, filesetRef, apiTypeVisibility, fileRef, folderRef
    }
}