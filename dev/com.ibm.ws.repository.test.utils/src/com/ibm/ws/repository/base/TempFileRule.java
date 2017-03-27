/*
* IBM Confidential
*
* OCO Source Materials
*
* Copyright IBM Corp. 2017
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.ws.repository.base;

import java.io.File;

import org.junit.rules.ExternalResource;

/**
 * Rule to allocate a temporary file and delete it at the end of the test
 */
public class TempFileRule extends ExternalResource {

    private final String prefix;
    private final String suffix;
    private File file;

    public TempFileRule(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    /**
     * @return the file
     */
    public File getFile() {
        return file;
    }

    /** {@inheritDoc} */
    @Override
    protected void before() throws Throwable {
        file = File.createTempFile(prefix, suffix);
    }

    /** {@inheritDoc} */
    @Override
    protected void after() {
        if (file.exists()) {
            file.delete();
        }
    }

}
