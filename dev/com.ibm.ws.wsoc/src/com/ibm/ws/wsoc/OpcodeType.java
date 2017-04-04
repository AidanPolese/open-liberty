package com.ibm.ws.wsoc;

import com.ibm.websphere.ras.annotation.Sensitive;

@Sensitive
public enum OpcodeType {
    TEXT_WHOLE,
    TEXT_PARTIAL_FIRST,
    TEXT_PARTIAL_CONTINUATION,
    TEXT_PARTIAL_LAST,
    BINARY_WHOLE,
    BINARY_PARTIAL_FIRST,
    BINARY_PARTIAL_CONTINUATION,
    BINARY_PARTIAL_LAST,
    CONNECTION_CLOSE,
    PING,
    PONG
}
