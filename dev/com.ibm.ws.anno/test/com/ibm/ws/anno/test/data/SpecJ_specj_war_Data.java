/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package com.ibm.ws.anno.test.data;

import java.io.File;

import com.ibm.ws.anno.classsource.specification.ClassSource_Specification_Direct_WAR;
import com.ibm.wsspi.anno.classsource.ClassSource_Factory;

public class SpecJ_specj_war_Data {
    public static final String EAR_NAME = "specj.ear.unpacked";
    public static final String EAR_LIB_PATH = File.separator + "lib";
    public static final String WAR_NAME = "specj.war.unpacked";
    public static final String EJBJAR_NAME = "specj.jar";

    public static ClassSource_Specification_Direct_WAR createClassSourceSpecification(ClassSource_Factory classSourceFactory,
                                                                                      String projectPath,
                                                                                      String dataPath) {
        ClassSource_Specification_Direct_WAR warSpecification = classSourceFactory.newWARSpecification();

        warSpecification.setImmediatePath(Common_Data.putIntoPath(projectPath, dataPath, WAR_NAME));
        warSpecification.setApplicationLibraryPath(Common_Data.putIntoPath(projectPath, dataPath, EAR_LIB_PATH));

        return warSpecification;
    }

}
