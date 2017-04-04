/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013, 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.container.service.annotations;

import java.util.List;
import java.util.Set;

import com.ibm.wsspi.anno.classsource.ClassSource_Aggregate;

/**
 * Interface to check if a container has any classes using specific annotations
 */
public interface ContainerAnnotations {

    /**
     * Returns true if the container has any classes directly annotated with the specified annotations.
     * Uses a scan policy of {@link ClassSource_Aggregate.ScanPolicy.SEED}.
     * Inherited annotations are <b>NOT</b> included in the scan results.
     * 
     * @param annotationTypeNames the annotation type names
     * @return true if the container has any classes with the specified annotations
     */
    public boolean hasSpecifiedAnnotations(List<String> annotationTypeNames);

    /**
     * Returns the names of any classes in the container which have any of the specified annotations.
     * Uses a scan policy of {@link ClassSource_Aggregate.ScanPolicy.SEED}.
     * Inherited annotations are included in the scan results.
     * 
     * @param annotationTypeNames the annotation type names
     * @return the names of any classes which have any of the specified annotations (declared or inherited)
     */
    public Set<String> getClassesWithSpecifiedInheritedAnnotations(List<String> annotationTypeNames);
}
