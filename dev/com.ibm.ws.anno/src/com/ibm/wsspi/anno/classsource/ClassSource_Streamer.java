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

import java.io.InputStream;

import com.ibm.wsspi.anno.classsource.ClassSource_Aggregate.ScanPolicy;

/**
 * <p>Call back type for class source processing.</p>
 */
public interface ClassSource_Streamer {
    /**
     * <p>Tell if a specified class is to be scanned.</p>
     * 
     * @param className The name of the class to test.
     * @param scanPolicy The policy to test against.
     * 
     * @return True if the class is to be processed. Otherwise, false.
     */
    boolean doProcess(String className, ScanPolicy scanPolicy);

    /**
     * <p>Process the data for the specified class.</p>
     * 
     * @param classSourceName The name of the class source which contains the class.
     * @param className The name of the class to process.
     * @param inputStream The stream containing the class data.
     * @param scanPolicy The policy active on the class.
     * 
     * @return True if the class was processed. Otherwise, false.
     * 
     * @throws ClassSource_Exception Thrown if an error occurred while
     *             testing the specified class.
     */
    boolean process(String classSourceName,
                    String className, InputStream inputStream,
                    ScanPolicy scanPolicy) throws ClassSource_Exception;

}
