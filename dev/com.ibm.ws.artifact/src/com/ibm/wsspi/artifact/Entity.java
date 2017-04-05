/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */
package com.ibm.wsspi.artifact;

/**
 * Represents common aspects of an Entity, which may be a Container, or an Entry.
 */
public interface Entity {

    /**
     * Get the name of this entity.
     * 
     * @return name of this entity.
     */
    public String getName();

}
