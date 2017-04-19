/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corporation 2011,2012
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

package com.ibm.ws.anno.test.data;

import com.ibm.ws.anno.classsource.specification.ClassSource_Specification_Direct_Bundle;
import com.ibm.wsspi.anno.classsource.ClassSource_Factory;

public class JPATest_JPATest_jar_Data {
    public static final String APP_NAME = "JPATest.app_1.0.0.201111251517.eba.unpacked";
    public static final String EBAJAR_NAME = "JPATest_1.0.0.201111251517.jar";

    public static ClassSource_Specification_Direct_Bundle createClassSourceSpecification(ClassSource_Factory classSourceFactory,
                                                                                         String projectPath,
                                                                                         String dataPath) {
        ClassSource_Specification_Direct_Bundle ebaSpecification = classSourceFactory.newEBASpecification();

        ebaSpecification.setImmediatePath(Common_Data.putIntoPath(projectPath, dataPath, EBAJAR_NAME));

        return ebaSpecification;
    }
}
