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

public interface DefaultValidatedExecutableTypes {
    enum ExecutableTypeEnum {
        NONE,
        CONSTRUCTORS,
        NON_GETTER_METHODS,
        GETTER_METHODS,
        ALL
    }

    List<ExecutableTypeEnum> getExecutableTypes();
}
