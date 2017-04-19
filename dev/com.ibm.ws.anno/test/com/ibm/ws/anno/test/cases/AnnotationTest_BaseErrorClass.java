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

import org.junit.Before;

import com.ibm.ws.anno.classsource.specification.ClassSource_Specification_Direct;
import com.ibm.ws.anno.targets.internal.AnnotationTargetsImpl_Targets;
import com.ibm.ws.anno.test.data.AnnotationTest_Error_Data;

public abstract class AnnotationTest_BaseErrorClass extends AnnotationTest_BaseClass {

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp(); // throws Exception

        setDataPath(AnnotationTest_Error_Data.EAR_NAME);
    }

    @Override
    public ClassSource_Specification_Direct createClassSourceSpecification() {
        return AnnotationTest_Error_Data.createClassSourceSpecification(getClassSourceFactory(),
                                                                        getProjectPath(),
                                                                        getDataPath(),
                                                                        getTargetName());
    }

    //

    @Override
    public abstract String getTargetName();

    public String[] getValidPackageNames() {
        return AnnotationTest_Error_Data.VALID_PACKAGE_NAMES;
    }

    public String[] getNonValidPackageNames() {
        return AnnotationTest_Error_Data.NON_VALID_PACKAGE_NAMES;
    }

    public String[] getValidClassNames() {
        return AnnotationTest_Error_Data.VALID_CLASS_NAMES;
    }

    public String[] getNonValidClassNames() {
        return AnnotationTest_Error_Data.NON_VALID_CLASS_NAMES;
    }

    @Override
    protected void verifyTargetMappings(PrintWriter writer,
                                        AnnotationTargetsImpl_Targets annotationTargets,
                                        AnnotationTest_TestResult scanResult) {

        super.verifyTargetMappings(writer, annotationTargets, scanResult);

        verifyClasses(writer, annotationTargets, scanResult,
                      getValidClassNames(), getNonValidClassNames());
    }
}