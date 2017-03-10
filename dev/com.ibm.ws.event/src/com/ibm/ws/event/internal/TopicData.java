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

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;

public final class TopicData {

    final private String topic;
    final private ExecutorService executorService;
    final private List<HandlerHolder> eventHandlers;
    final private AtomicReference<TopicData> reference;

    TopicData(String topic, ExecutorService executorService, List<HandlerHolder> eventHandlers) {
        this.topic = topic;
        this.executorService = executorService;
        this.eventHandlers = eventHandlers;
        this.reference = new AtomicReference<TopicData>(this);
    }

    public String getTopic() {
        return topic;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public List<HandlerHolder> getEventHandlers() {
        return eventHandlers;
    }

    public AtomicReference<TopicData> getReference() {
        return reference;
    }

    public void clearReference() {
        reference.set(null);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append(";topic=").append(topic);
        return sb.toString();
    }
}
