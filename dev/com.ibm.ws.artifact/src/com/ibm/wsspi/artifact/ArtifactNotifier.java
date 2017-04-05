/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.artifact;

import java.util.Collection;

public interface ArtifactNotifier {

    /**
     * Association of paths within a Container.<br>
     * Note that if the Notification represents a delete, associated paths may not be valid within the container.<br>
     */
    public interface ArtifactNotification {

        /**
         * @return the associated container
         */
        public ArtifactContainer getContainer();

        /**
         * @return the paths
         */
        public Collection<String> getPaths();

    }

    /**
     * Implemented by people wanting notifications of changes within Artifacts.
     */
    public interface ArtifactListener {
        /**
         * Called to inform the listener that changes have happened to entries.
         * <p>
         * Each notification contains the container it's associated paths are intended for, in case a listener is registered to multiple containers.<br>
         * Paths within the notifications will always be absolute, and will never contain the '!' prefix used when registering to request non-recursive registrations.<br>
         * The 3 parameters will never be null.
         * 
         * @param added
         * @param removed
         * @param modified
         */
        public void notifyEntryChange(ArtifactNotification added, ArtifactNotification removed, ArtifactNotification modified);
    }

    /**
     * Registers for notifications within the target Containers/Entries<p>
     * You can only register for notifications that are at paths within the container this notifier is from.
     * Attempting to use this Notifier with other Containers or Entries (from unrelated containers, nested containers, or nested nested), will result in an
     * IllegalArgumentException.<p>
     * 
     * <em>Note: listeners should be removed from the <b>same notifier instance</b> they are added to.</em>
     * 
     * @see #removeListener(ArtifactListener)
     * 
     * @param targets the locations to monitor for change
     * @param callbackObject the listener to notify if changes occur to entities in the target collection *
     * @return true if the registration was successful, false otherwise.
     * @throws IllegalArgumentException if any Container within targets is a new root, or beneath a new root for this notifier, or a container associated with a different notifier.
     */
    public boolean registerForNotifications(ArtifactNotification targets, ArtifactListener callbackObject) throws IllegalArgumentException;

    /**
     * Removes a listener from <em>THIS</em> notifier. <p>
     * <em>Note: listeners should be removed from the <b>same notifier instance</b> they are added to.</em>
     * 
     * @see #registerForNotifications(ArtifactNotification, ArtifactListener)
     * 
     * @param listenerToRemove
     * @return true if the listener was removed, false otherwise.
     */
    public boolean removeListener(ArtifactListener listenerToRemove);

    /**
     * 
     * @param interval interval to use in milliseconds. Not used if useMBean is true.
     * @param useMBean true if should use mbean, rather than timed intervals.
     * @return true if the values given were used, false if options could not be set
     */
    public boolean setNotificationOptions(long interval, boolean useMBean);
}
