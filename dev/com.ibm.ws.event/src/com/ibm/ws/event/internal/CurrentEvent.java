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

package com.ibm.ws.event.internal;

import java.util.ArrayDeque;
import java.util.Deque;

import com.ibm.websphere.event.Event;

public class CurrentEvent {

    private final static ThreadLocal<Deque<EventImpl>> currentEvent = new ThreadLocal<Deque<EventImpl>>()
    {
        public Deque<EventImpl> initialValue()
    {
        return new ArrayDeque<EventImpl>();
    }
    };

    public static Event get() {
        return currentEvent.get().peekFirst();
    }

    static void push(EventImpl event) {
        currentEvent.get().addFirst(event);
    }

    static EventImpl pop() {
        return currentEvent.get().pollFirst();
    }
}
