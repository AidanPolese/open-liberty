/*******************************************************************************
 * Copyright (c) 1997, 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.http.channel.h2internal.frames;

import com.ibm.ws.http.channel.h2internal.FrameReadProcessor;
import com.ibm.ws.http.channel.h2internal.FrameTypes;
import com.ibm.ws.http.channel.h2internal.H2ConnectionSettings;
import com.ibm.ws.http.channel.h2internal.exceptions.FrameSizeException;
import com.ibm.ws.http.channel.h2internal.exceptions.ProtocolException;

public class FramePing extends Frame {

    /*
     * format of ping frame
     *
     * length(24) 00 00 04 length of payload
     * type(8) 06 PING
     * flags(8) xx bit ? ACK = 0
     * R (1) 0
     * Stream (31) 00 00 00 00
     *
     * Payload 00 00 00 00 00 00 00 00 64 bits opaque data. init to 0's
     */

    byte[] opaquePayload;

    /**
     * Write frame constructor
     */
    public FramePing(int streamId, int payloadLength, byte flags, boolean reserveBit, FrameDirection direction) {
        super(streamId, payloadLength, flags, reserveBit, direction);
        frameType = FrameTypes.PING;
    }

    /**
     * Write frame constructor
     */
    public FramePing(int streamId, byte[] payload, boolean reserveBit) {
        super(streamId, 8, (byte) 0x00, reserveBit, FrameDirection.WRITE);
        frameType = FrameTypes.PING;
        this.payloadLength = 8;
        this.opaquePayload = new byte[payloadLength];
        if (payload != null) {
            opaquePayload = payload;
        } else {
            // set each payload byte to 's', for now
            for (int i = 0; i < payloadLength; i++) {
                this.opaquePayload[i] = 's';
            }
        }
        setInitialized(); // we have everything we need to write out, now
    }

    @Override
    public void processPayload(FrameReadProcessor frp) throws FrameSizeException {
        // +---------------------------------------------------------------+
        // |                                                               |
        // |                      Opaque Data (64)                         |
        // |                                                               |
        // +---------------------------------------------------------------+

        setFlags();

        int payloadIndex = 0;
        // grab all the buffered data
        this.opaquePayload = new byte[payloadLength];
        for (int i = 0; payloadIndex++ < payloadLength; i++)
            opaquePayload[i] = frp.grabNextByte();
    }

    @Override
    public byte[] buildFrameForWrite() {

        byte[] frame = super.buildFrameForWrite();

        // add the first 9 bytes of the array
        setFrameHeaders(frame, utils.FRAME_TYPE_PING);

        // now for the Setting Frame payload
        int frameIndex = SIZE_FRAME_BEFORE_PAYLOAD;

        for (int i = 0; i < opaquePayload.length; i++) {
            frame[i + SIZE_FRAME_BEFORE_PAYLOAD] = opaquePayload[i];
        }
        return frame;
    }

    @Override
    public void validate(H2ConnectionSettings settings) throws ProtocolException, FrameSizeException {
        if (streamId != 0) {
            throw new ProtocolException("PING frame streamID must be 0x0; received " + streamId);
        }
        if (this.payloadLength != 8) {
            throw new FrameSizeException("PING opaque data must have a length of 8.  Actual length : " + payloadLength);
        }
    }

    @Override
    protected void setFlags() {
        ACK_FLAG = utils.getFlag(flags, 0);
    }

    public boolean isAckSet() {
        return ACK_FLAG;
    }

    public byte[] getPayload() {
        return this.opaquePayload;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof FramePing)) {
            System.out.println("Object is not FrameSettings");
            return false;
        }

        FramePing framePingToCompare = (FramePing) object;

        if (!super.equals(framePingToCompare))
            return false;

        if (this.getPayload() != framePingToCompare.getPayload()) {
            System.out.println("Payload = opaque data. this.getPayload() = " + this.getPayload() + " framePingToCompare.getPayload() = " + framePingToCompare.getPayload());
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        StringBuilder frameToString = new StringBuilder();

        frameToString.append(super.toString());

        frameToString.append("OpaqueData: " + this.getPayload() + "\n");

        return frameToString.toString();

    }
}
