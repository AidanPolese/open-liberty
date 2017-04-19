/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corporation 2011
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

package com.ibm.ws.anno.test.disabled;

import com.ibm.ws.anno.classsource.specification.ClassSource_Specification_Direct;
import com.ibm.ws.anno.test.cases.AnnotationTest_BaseClass;

public class WMAServlet25_wma_servlet25_war_Test_Disabled extends AnnotationTest_BaseClass {
    public static final String TEST_DATA_ROOT = "c:\\annoSamples";

    public static final String WAR_NAME = "wma_servlet25.war";
    public static final String WAR_PATH = TEST_DATA_ROOT + "\\" + WAR_NAME;

    public static final String EXTRA_LIB_NAME = "axis-1_4";
    public static final String EXTRA_LIB_PATH = WAR_PATH + "\\" + EXTRA_LIB_NAME;

    public static final String DEBUG_LOG_NAME = WAR_NAME + ".log";
    public static final String DEBUG_LOG_PATH = TEST_DATA_ROOT + "\\" + DEBUG_LOG_NAME;

    public static final String SUMMARY_LOG_NAME = WAR_NAME + ".log";
    public static final String SUMMARY_LOG_PATH = TEST_DATA_ROOT + "\\" + SUMMARY_LOG_NAME;

    //

    @Override
    public String getTargetName() {
        return "wma_servlet25";
    }

    @Override
    public int getIterations() {
        return 5;
    }

    //

    //        AnnotationTargetsImpl_Context scannerContext =
    //                        AnnotationTargetsImpl_Context.createWARContext(classInternMap,
    //                                                                       WAR_NAME,
    //                                                                       WAR_PATH,
    //                                                                       libPaths, (List<String>) null,
    //                                                                       WMAServlet25_wma_servlet25_war_Test_Disabled.class.getClassLoader());
    //        // 'createWARContext' throws AnnotationScannerException
    //
    //        return scannerContext;   

    /** {@inheritDoc} */
    @Override
    public ClassSource_Specification_Direct createClassSourceSpecification() {
        // TODO Auto-generated method stub
        return null;
    }
}
