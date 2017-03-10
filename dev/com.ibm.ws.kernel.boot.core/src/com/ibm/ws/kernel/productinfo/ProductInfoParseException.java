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
package com.ibm.ws.kernel.productinfo;

import java.io.File;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;

@SuppressWarnings("serial")
public class ProductInfoParseException extends Exception {
    private final File file;
    private final String missingKey;

    ProductInfoParseException(File file, Throwable t) {
        super("failed to read " + file, t);
        this.file = file;
        this.missingKey = null;
    }

    ProductInfoParseException(File file, String missingKey) {
        super("missing key " + missingKey + " in " + file);
        this.file = file;
        this.missingKey = missingKey;
    }

    public File getFile() {
        return file;
    }

    public String getMissingKey() {
        return missingKey;
    }

    private void writeObject(ObjectOutputStream oos) throws NotSerializableException {
        throw new NotSerializableException();
    }
}
