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
package com.ibm.websphere.metatype;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

/**
 *
 */
public interface SchemaGenerator {

    public void generate(OutputStream out, SchemaGeneratorOptions options) throws IOException;

    public void generate(Writer writer, SchemaGeneratorOptions options) throws IOException;
}
