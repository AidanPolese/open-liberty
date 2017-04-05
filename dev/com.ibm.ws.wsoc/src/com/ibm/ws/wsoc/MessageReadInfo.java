/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.wsoc;

/**
 *
 */
public class MessageReadInfo {

    public enum State {
        COMPLETE,
        PARTIAL_COMPLETE,
        FRAME_INCOMPLETE,
        CONTROL_MESSAGE_EMBEDDED,
        CLOSE_FRAME_ERROR
    }

    private final State state;
    private final OpcodeType type;

    private final boolean moreBuffer;;

    public MessageReadInfo(State s, OpcodeType t, boolean m) {
        state = s;
        type = t;
        moreBuffer = m;

    }

    public State getState() {
        return state;
    }

    public OpcodeType getType() {
        return type;
    }

    public boolean isMoreBufferToProcess() {
        return moreBuffer;
    }

}
