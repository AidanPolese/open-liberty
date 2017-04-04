/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corporation 2011, 2013
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

package com.ibm.wsspi.anno.classsource;

public interface ClassSource_MappedDirectory extends ClassSource {
    String getDirPath();

    boolean getConvertResourceNames();

    @Override
    String inconvertResourceName(String externalResourceName);

    @Override
    String outconvertResourceName(String internalResourceName);
}