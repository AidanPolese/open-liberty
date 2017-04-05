/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.dd.ejbext;

import java.util.List;

import com.ibm.ws.javaee.ddmetadata.annotation.DDElement;
import com.ibm.ws.javaee.ddmetadata.annotation.DDXMIElement;
import com.ibm.ws.javaee.ddmetadata.annotation.LibertyNotInUse;

/**
 * Represents &lt;run-as-mode>.
 */
@LibertyNotInUse
public interface RunAsMode extends RunAsModeBase {

    /**
     * @return list of &lt;extended-method> objects, at least one must be provided.
     */
    @DDElement(name = "method", required = true)
    @DDXMIElement(name = "methodElements")
    List<ExtendedMethod> getMethods();

}
