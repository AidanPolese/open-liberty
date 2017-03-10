/*
* IBM Confidential
*
* OCO Source Materials
*
* WLP Copyright IBM Corp. 2017
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.ws.kernel.launch.service;

import java.util.HashMap;

/**
 * Defines a pauseable component that can be driven for pause and resume actions.
 */
public interface PauseableComponent {

    /**
     * Returns the name of the pauseable component.
     *
     * @return the name of the pauseable component. It is the same string that is used when performing targeted
     *         pause or resume operations.
     */
    public String getName();

    /**
     * Pauses the work managed by the pauseable component.
     *
     * @throws PauseableComponentException if the pauseable component experiences problems pausing
     *             the activity it manages.
     */
    public void pause() throws PauseableComponentException;

    /**
     * Resumes the work managed by the pauseable component.
     *
     * @throws PauseableComponentException if the pauseable component experiences problems resuming
     *             the activity it manages.
     */
    public void resume() throws PauseableComponentException;

    /**
     * Indicates whether or not the pauseable component is paused.
     *
     * @return True if the pauseable component is Paused. False Otherwise.
     */
    public boolean isPaused();

    /**
     * Returns pauseable component extended information.
     *
     * @return A map of name and value pairs that describe the pauseable component.
     */
    public HashMap<String, String> getExtendedInfo();
}