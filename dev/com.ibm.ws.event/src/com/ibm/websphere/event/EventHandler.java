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

public interface EventHandler {

    /**
     * WebSphere event handlers can be re-entrant. The OSGi Event Admin Service Specification
     * is written such that an implementation must not assume a handler is reentrant.
     * <p>
     * A WebSphere event handler should set the {@code EventEngine#REENTRANT_HANDLER} to indicate
     * that the handler implementation is reentrant. When this property is set to the <code>String</code>
     * value of <code>true</code>, a handler may be called on multiple threads simultaneously.
     * The implication of this is that events may arrive out of order.
     * 
     * @see https://mail.osgi.org/pipermail/osgi-dev/2006-April/000069.html
     * @see EventEngine#REENTRANT_HANDLER
     */
    public void handleEvent(Event event);

}
