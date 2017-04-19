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

package com.ibm.ws.anno.test.data;

import java.io.File;

import com.ibm.ws.anno.classsource.specification.ClassSource_Specification_Direct_EJB;
import com.ibm.wsspi.anno.classsource.ClassSource_Factory;

/**
 * Test data for testing @Repeatable annotation (new in Java 8)
 * Note the code in the input jar file (RepeatableAnnotation_Test.jar)
 * is compiled using Java 8 using the "-parameters" command line parameter.
 * 
 * */
public class Annotation_Repeatable_Data {
    public static final String EAR_NAME = "RepeatableAnnotation_Test.ear.unpacked";
    public static final String EAR_LIB_PATH = File.separator + "lib";
    public static final String EJBJAR_NAME = "RepeatableAnnotation_Test.jar";

    public static ClassSource_Specification_Direct_EJB createClassSourceSpecification(ClassSource_Factory classSourceFactory,
                                                                                      String projectPath,
                                                                                      String dataPath) {
        ClassSource_Specification_Direct_EJB ejbSpecification = classSourceFactory.newEJBSpecification();

        ejbSpecification.setImmediatePath(Common_Data.putIntoPath(projectPath, dataPath, EJBJAR_NAME));
        ejbSpecification.setApplicationLibraryPath(Common_Data.putIntoPath(projectPath, dataPath, EAR_LIB_PATH));

        return ejbSpecification;
    }
}
