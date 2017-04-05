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
package com.ibm.wsspi.adaptable.module;

import com.ibm.wsspi.artifact.ArtifactContainer;

/**
 *
 */
public interface FastModeControl {

    /**
     * Instruct this container it may commit to using more resources
     * to speed access to it's content.
     * <p>
     * Fast Mode is enabled at the root container, and enables/disables for all containers beneath that root.<br>
     * Fast Mode does not cascade into new root containers (eg, where Entry.convertToContainer().isRoot()==true)
     * <p>
     * Calling this method requires you to later invoke {@link ArtifactContainer#stopUsingFastMode} <p>
     * This method is equivalent to {@link ArtifactContainer.getRoot().useFastMode()}
     */
    public void useFastMode();

    /**
     * Instruct this container that you no longer require it to consume
     * resources to speed access to it's content.
     * <p>
     * Fast Mode is enabled at the root container, and enables/disables for all containers beneath that root.<br>
     * Fast Mode does not cascade into new root containers (eg, where Entry.convertToContainer().isRoot()==true)
     * <p>
     * Calling this method requires you to have previously invoked {@link ArtifactContainer#useFastMode}<p>
     * This method is equivalent to {@link ArtifactContainer.getRoot().useFastMode()}
     */
    public void stopUsingFastMode();

}
