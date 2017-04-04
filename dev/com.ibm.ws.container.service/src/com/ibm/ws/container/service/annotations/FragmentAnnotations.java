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
package com.ibm.ws.container.service.annotations;

import java.util.Set;

import com.ibm.wsspi.adaptable.module.UnableToAdaptException;

/**
 *
 */
public interface FragmentAnnotations {

    /**
     * <p>Target helper: Select the classes which are recorded as having
     * the specified annotation as a class annotation.</p>
     * 
     * @param annotationClass The class annotation to use for the selection.
     * 
     * @return The names of classes having the annotation as a class annotation.
     * 
     * @throws UnableToAdaptException Thrown by an error processing fragment paths.
     */
    Set<String> selectAnnotatedClasses(Class<?> annotationClass) throws UnableToAdaptException;
}
