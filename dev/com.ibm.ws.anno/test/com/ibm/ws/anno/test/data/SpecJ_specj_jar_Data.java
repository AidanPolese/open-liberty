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

public class SpecJ_specj_jar_Data {
    public static final String EAR_NAME = "specj.ear.unpacked";
    public static final String EAR_LIB_PATH = File.separator + "lib";
    public static final String WAR_NAME = "specj.war.unpacked";
    public static final String EJBJAR_NAME = "specj.jar";

    public static ClassSource_Specification_Direct_EJB createClassSourceSpecification(ClassSource_Factory classSourceFactory,
                                                                                      String projectPath,
                                                                                      String dataPath) {
        ClassSource_Specification_Direct_EJB ejbSpecification = classSourceFactory.newEJBSpecification();

        ejbSpecification.setImmediatePath(Common_Data.putIntoPath(projectPath, dataPath, EJBJAR_NAME));
        ejbSpecification.setApplicationLibraryPath(Common_Data.putIntoPath(projectPath, dataPath, EAR_LIB_PATH));

        return ejbSpecification;
    }
}
