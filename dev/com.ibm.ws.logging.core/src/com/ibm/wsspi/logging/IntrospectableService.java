/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011, 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.logging;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Interface for introspect the framework.
 * Services implement this interface can provide the information to dump to a file.
 * 
 * @deprecated Use {@link Introspector} instead.
 */
@Deprecated
public interface IntrospectableService {

    /**
     * used as the file name of the service's dump file
     */
    public String getName();

    public String getDescription();

    /**
     * 
     * @param out, the dump file
     */
    public void introspect(OutputStream out) throws IOException;
}