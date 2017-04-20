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

package com.ibm.ws.anno.test.cases;

import java.io.PrintWriter;

import org.junit.Before;
import org.junit.Test;

import com.ibm.ws.anno.classsource.specification.ClassSource_Specification_Direct_EJB;
import com.ibm.ws.anno.test.data.AppDeployBench_GSEJB_jar_Data;

public class AppDeployBench_GSEJB_jar_Test extends AnnotationTest_BaseClass {

    @Override
    public ClassSource_Specification_Direct_EJB createClassSourceSpecification() {
        return AppDeployBench_GSEJB_jar_Data.createClassSourceSpecification(getClassSourceFactory(),
                                                                            getProjectPath(),
                                                                            getDataPath());
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp(); // throws Exception

        setDataPath(AppDeployBench_GSEJB_jar_Data.EAR_NAME);
    }

    //

    @Override
    public String getTargetName() {
        return AppDeployBench_GSEJB_jar_Data.EJBJAR_NAME;
    }

    @Override
    public int getIterations() {
        return 5;
    }

    //  public boolean getSeedStorage() {
    //  return true;        
    //}

    //   

    @Test
    public void testAppDeployBench_GSEJB_JAR_nodetail_direct() throws Exception {
        runScanTest(DETAIL_IS_NOT_ENABLED,
                    getStoragePath(COMMON_TEMP_STORAGE_PATH), STORAGE_NAME_NO_DETAIL,
                    getSeedStorage(), getStoragePath(COMMON_STORAGE_PATH), STORAGE_NAME_NO_DETAIL,
                    new PrintWriter(System.out, true));
    }

    @Test
    public void testAppDeployBench_GSEJB_JAR_detail_direct() throws Exception {
        runScanTest(DETAIL_IS_ENABLED,
                    getStoragePath(COMMON_TEMP_STORAGE_PATH), STORAGE_NAME_DETAIL,
                    getSeedStorage(), getStoragePath(COMMON_STORAGE_PATH), STORAGE_NAME_DETAIL,
                    new PrintWriter(System.out, true));
    }
}
