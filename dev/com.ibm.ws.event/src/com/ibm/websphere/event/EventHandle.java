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

package com.ibm.websphere.event;

import java.util.Map;

public interface EventHandle {

    // TODO: Rename this class.

    /**
     * Returns the Map of properties on the associated Event
     * 
     * @return properties on the associated Event
     */
    public Map<String, Object> getProperties();

    /**
     * Attempts to cancel execution of this event.
     * This attempt will fail if the event has already completed,
     * already been canceled, or could not be canceled for some other reason.
     * 
     * @param mayInterruptIfRunning
     *            true if this event should be interrupted
     * 
     * @return false if the event could not be canceled
     */
    public boolean cancel(boolean mayInterruptIfRunning);

    /**
     * Returns true if this event was canceled before it completed normally.
     * 
     * @return true if event was canceled before it completed
     */
    public boolean isCancelled();

    /**
     * Returns true if this event has completed.
     * 
     * @return true if this event has completed.
     */
    public boolean isDone();

    /**
     * Waits to return until event processing has completed.
     */
    public void waitForCompletion();

}
