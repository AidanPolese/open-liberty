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

public class PlantsV80_PlantsByWebSphere_war_Test_Disabled extends AnnotationTest_BaseClass {
    public static final String TEST_DATA_ROOT = "c:\\annoSamples";

    public static final String EAR_DIR = "pbw-ear.ear.unpacked";
    public static final String EAR_PATH = TEST_DATA_ROOT + "\\" + EAR_DIR;

    public static final String WAR_DIR = "PlantsByWebSphere.war.unpacked";
    public static final String WAR_PATH = EAR_PATH + "\\" + WAR_DIR;

    @Override
    public String getTargetName() {
        return "pbw-ear.ear";
    }

    //    public void testBeenThere_nodetail() throws Exception {
    //        runScanTest(AnnotationTargets_Scanner.DETAIL_IS_NOT_ENABLED,
    //                    CONTAINER_IS_NOT_ENABLED,
    //                    new PrintWriter(System.out));
    //    }
    //
    //    public void testBeenThere_detail() throws Exception {
    //        runScanTest(AnnotationTargets_Scanner.DETAIL_IS_ENABLED,
    //                    CONTAINER_IS_NOT_ENABLED,
    //                    new PrintWriter(System.out));
    //    }

    //        scannerContext.addDirectoryClassSource("WAR classes",
    //                                               WAR_PATH + "\\" + "WEB-INF\\classes",
    //                                               ClassSource_Aggregate.IS_SEED); // throws AnnotationScannerException
    //
    //        scannerContext.addDirectoryClassSource("EAR lib",
    //                                               EAR_PATH + "lib" + "\\" + "pbw-lib.jar",
    //                                               ClassSource_Aggregate.IS_NOT_SEED); // throws AnnotationScannerException
    //
    //        scannerContext.addClassLoaderClassSource("system classloader",
    //                                                 PlantsV80_PlantsByWebSphere_war_Test_Disabled.class.getClassLoader());
    //
    //        return scannerContext;   

    /** {@inheritDoc} */
    @Override
    public ClassSource_Specification_Direct createClassSourceSpecification() {
        // TODO Auto-generated method stub
        return null;
    }
}
