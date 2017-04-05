/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.dd.bval;

import java.util.List;

import com.ibm.ws.javaee.dd.DeploymentDescriptor;

public interface ValidationConfig extends DeploymentDescriptor {

    /**
     * Represents "1.0" for {@link #getVersionID}.
     */
    int VERSION_1_0 = 10;

    /**
     * Represents "1.1" for {@link #getVersionID}.
     */
    int VERSION_1_1 = 11;

    int getVersionID();

    String getDefaultProvider();

    String getMessageInterpolator();

    String getTraversableResolver();

    String getConstraintValidatorFactory();

    String getParameterNameProvider();

    ExecutableValidation getExecutableValidation();

    List<String> getConstraintMappings();

    List<Property> getProperties();
}
