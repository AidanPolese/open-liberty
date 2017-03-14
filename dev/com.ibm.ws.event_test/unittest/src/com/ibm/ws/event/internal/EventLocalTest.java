/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2009
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */
package com.ibm.ws.event.internal;

import org.junit.Test;

import com.ibm.websphere.event.EventLocal;
import com.ibm.websphere.event.Topic;

/**
 * Test EventLocals
 */
public class EventLocalTest {

    /**
     * Test creating a second EventLocal with the same name.
     */
    //@Test(expected=IllegalArgumentException.class)
    @SuppressWarnings("unused")
    public void testAlreadyNamedCreation() {
        //! Turn on flag to check for uniqueness
        System.setProperty("defect.7780.enabled", "true");
        EventImpl e = new EventImpl(new Topic("test"));
        CurrentEvent.push(e);
        final EventLocal<String> el1 = EventLocal.createLocal("name1");
        final EventLocal<String> el2 = EventLocal.createLocal("name1");
        CurrentEvent.pop();
    }

    //@Test
    @SuppressWarnings("unused")
    public void testAlreadyNamedCreationDifferentEvents() {
        EventImpl e = new EventImpl(new Topic("test"));
        CurrentEvent.push(e);
        final EventLocal<String> el1 = EventLocal.createLocal("name1");
        EventImpl e1 = new EventImpl(new Topic("test1"));
        CurrentEvent.push(e1);
        final EventLocal<String> el2 = EventLocal.createLocal("name1");
        CurrentEvent.pop();
        CurrentEvent.pop();
    }

    @Test
    @SuppressWarnings("unused")
    public void testCreationOfSecondNamedAfterRemoval() {
        EventImpl e = new EventImpl(new Topic("test"));
        CurrentEvent.push(e);
        final EventLocal<String> el1 = EventLocal.createLocal("name1");
        el1.remove();
        final EventLocal<String> el2 = EventLocal.createLocal("name1");
        CurrentEvent.pop();
    }
}
