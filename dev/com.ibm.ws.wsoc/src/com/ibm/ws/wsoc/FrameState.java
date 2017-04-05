package com.ibm.ws.wsoc;

public enum FrameState {
    INIT, 
    FIND_16BIT_PAYLOAD_LENGTH,  
    FIND_64BIT_PAYLOAD_LENGTH, 
    FIND_MASK, 
    FIND_PAYLOAD, 
    PAYLOAD_COMPLETE
}
