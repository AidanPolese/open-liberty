/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.kernel.embeddable;

/**
 * A {@link ServerEvent} listener. <code>ServerEventListener</code> is a listener
 * interface that may be implemented by the embedding runtime.
 * <p>
 * When a <code>ServerEvent</code> is fired, it is asynchronously delivered to
 * a <code>ServerEventListener</code>. The server delivers <code>ServerEvent</code> objects
 * to a <code>ServerEventListener</code> in order and will not concurrently call a <code>ServerEventListener</code>.
 * <p>
 * A <code>ServerEventListener</code> object is registered with a <code>Server</code> using
 * the {@link ServerBuilder#registerServerEventListener(ServerEventListener)} method.
 * <code>ServerEventListener</code> objects
 * are called with a <code>ServerEvent</code> object when the <code>Server</code> created
 * by the <code>ServerBuilder</code> starts or stops.
 */
public interface ServerEventListener {

    /**
     * A <code>ServerEvent</code> is passed to a registered <code>ServerEventListener</code>
     * <p>
     * Consumers of this SPI must not implement this interface.
     */
    static interface ServerEvent {

        /** The type of server event */
        enum Type {
            STARTING,
            STARTED,
            STOPPED,
            FAILED; // used to ensure notification when configuration prevents the server from starting
        }

        /**
         * @return the Server associated with the event.
         */
        Server getServer();

        /**
         * The type of event
         */
        Type getType();

        /**
         * @return a ServerException, or null.
         */
        ServerException getException();
    }

    /**
     * Receives notification of a general {@link ServerEvent} object.
     * 
     * @param event The ServerEvent object
     */
    void serverEvent(ServerEvent event);
}
