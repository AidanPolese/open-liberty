//* ===========================================================================
//*
//* IBM SDK, Java(tm) 2 Technology Edition, v5.0
//* (C) Copyright IBM Corp. 2005, 2006
//*
//* The source code for this program is not published or otherwise divested of
//* its trade secrets, irrespective of what has been deposited with the U.S.
//* Copyright office.
//*
//* ===========================================================================
//
//@(#) 1.3 CF/ws/code/channelfw.impl/src/com/ibm/ws/channel/framework/impl/RuntimeState.java, WAS.channelfw, CCX.CF 5/10/04 22:25:08 [5/11/05 12:15:29]

package com.ibm.ws.channelfw.internal;

/**
 * The purpose of this class is to represent the state of an object with an
 * enumerated class as opposed to a set of static ints.
 */
public class RuntimeState implements Comparable<RuntimeState> {
    /**
     * ordinal of this state
     */
    public int ordinal;
    /**
     * to increment the ordinal and get a new ordinal on each state object
     */
    private static int values = 0;

    /**
     * constructor for this state
     * private so only objects in this class can instantiate.
     */
    private RuntimeState() {
        this.ordinal = values++;
    }

    /*
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(RuntimeState o) {
        if (o == null) {
            return -1;
        }
        return hashCode() - o.hashCode();
    }

    /*
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (null == o || !(o instanceof RuntimeState)) {
            return false;
        }
        return hashCode() == o.hashCode();
    }

    /*
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return this.ordinal;
    }

    /**
     * This state is used for
     * 1. after construction, but before init method called
     * 2. after destroy method called
     */
    public final static RuntimeState UNINITIALIZED = new RuntimeState();
    /**
     * This state is used to denote
     * 1. the init method of a channel or chain has been called, but start has
     * not.
     * 2. the stop method has been called.
     */
    public final static RuntimeState INITIALIZED = new RuntimeState();
    /**
     * This state is used to denote the start method of a channel or
     * chain has been called, but stop and destroy have not.
     */
    public final static RuntimeState STARTED = new RuntimeState();
    /**
     * This state is used to denote the stop(time) {quiesce} method
     * has been called, but the stop isn't complete.
     */
    public final static RuntimeState QUIESCED = new RuntimeState();
}
