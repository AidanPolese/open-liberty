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
import java.util.ArrayList;
import java.util.List;

import com.ibm.ws.anno.classsource.specification.ClassSource_Specification_Direct_EJB;
import com.ibm.wsspi.anno.classsource.ClassSource_Factory;

public class AppDeployBench_GSEJB_jar_Data {
    public static final String EAR_NAME = "AppDeployBench.ear.unpacked";
    public static final String EAR_LIB_PATH = File.separator + "lib";
    public static final String EJBJAR_NAME = "GSEJB.jar";

    public static final List<String> EJBJAR_MANIFEST_PATHS;

    static {
        EJBJAR_MANIFEST_PATHS = new ArrayList<String>();

        EJBJAR_MANIFEST_PATHS.add("GarageSaleUtils.jar");
        EJBJAR_MANIFEST_PATHS.add("WSNClient.jar");
    }

    public static ClassSource_Specification_Direct_EJB createClassSourceSpecification(ClassSource_Factory classSourceFactory,
                                                                                      String projectPath,
                                                                                      String dataPath) {

        ClassSource_Specification_Direct_EJB ejbSpecification = classSourceFactory.newEJBSpecification();

        ejbSpecification.setImmediatePath(Common_Data.putIntoPath(projectPath, dataPath, EJBJAR_NAME));

        // Leave the application library unspecified: No application library directory is available
        // for AppDeployBench.
        //        
        // ejbSpecification.setApplicationLibraryPath( Common_Data.putInProjectData(projectPath, dataPath, EAR_LIB_PATH) );

        ejbSpecification.addManifestJarPaths(Common_Data.putInPath(projectPath, dataPath, EJBJAR_MANIFEST_PATHS));

        return ejbSpecification;
    }

}
