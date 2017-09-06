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
package com.ibm.ws.microprofile.metrics.writer;

import java.io.IOException;

import com.ibm.ws.microprofile.metrics.exceptions.EmptyRegistryException;
import com.ibm.ws.microprofile.metrics.exceptions.NoSuchMetricException;
import com.ibm.ws.microprofile.metrics.exceptions.NoSuchRegistryException;

/**
 *
 */
public interface OutputWriter {

    /**
     *
     * @param registryName
     * @param metricName
     * @throws NoSuchRegistryException
     * @throws NoSuchMetricException
     * @throws IOException
     * @throws EmptyRegistryException
     */
    public void write(String registryName, String metricName) throws NoSuchRegistryException, NoSuchMetricException, IOException, EmptyRegistryException;

    /**
     *
     * @param registryName
     * @throws NoSuchRegistryException
     * @throws EmptyRegistryException
     * @throws IOException
     */
    public void write(String registryName) throws NoSuchRegistryException, EmptyRegistryException, IOException;

    /**
     *
     * @throws IOException
     */
    public void write() throws IOException;
}
