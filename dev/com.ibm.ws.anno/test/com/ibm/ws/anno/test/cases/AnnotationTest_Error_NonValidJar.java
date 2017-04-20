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
package com.ibm.ws.anno.test.cases;

import java.io.PrintWriter;

import org.junit.Test;

import com.ibm.ws.anno.test.data.AnnotationTest_Error_Data;

public class AnnotationTest_Error_NonValidJar extends AnnotationTest_BaseErrorClass {

    @Override
    public String getTargetName() {
        return AnnotationTest_Error_Data.WAR_NAME_NONVALID_JAR;
    }

    @Test
    public void testAnnotationTest_Error_NonValidJar() throws Exception {
        runScanTest(DETAIL_IS_ENABLED,
                    getStoragePath(COMMON_TEMP_STORAGE_PATH), STORAGE_NAME_DETAIL,
                    getSeedStorage(), getStoragePath(COMMON_STORAGE_PATH), STORAGE_NAME_DETAIL,
                    new PrintWriter(System.out, true));
    }
}