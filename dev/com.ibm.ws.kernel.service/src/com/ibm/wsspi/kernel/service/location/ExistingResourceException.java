/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.kernel.service.location;

import java.io.IOException;

/**
 *
 */
public class ExistingResourceException extends IOException {
    private static final long serialVersionUID = 1L;
    private static final String msgString = "File operation failed, resource already exists (source=%s, target=%s)";

    private final String source;
    private final String target;

    /**
     * @param message
     */
    public ExistingResourceException(String source, String target) {
        super(String.format(msgString, source, target));
        this.source = source;
        this.target = target;
    }

    /**
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * @return the target
     */
    public String getTarget() {
        return target;
    }
}
