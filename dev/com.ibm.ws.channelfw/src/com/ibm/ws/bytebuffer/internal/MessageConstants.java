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

package com.ibm.ws.bytebuffer.internal;

/**
 * Constants used by the ByteBuffer package for user-seen messages.
 */
public interface MessageConstants {
    // -------------------------------------------------------------------------
    // Public Constants
    // -------------------------------------------------------------------------

    /** NLS message bundle */
    String WSBB_BUNDLE = "com.ibm.ws.bytebuffer.internal.resources.ByteBufferMessages";
    /** RAS group name */
    String WSBB_TRACE_NAME = "WsByteBuffer";

    // -------------------------------------------------------------------------
    // NLS Messages
    // -------------------------------------------------------------------------

    /** Reference to the NLS message for an unrecognized custom property */
    String UNRECOGNIZED_CUSTOM_PROPERTY = "UNRECOGNIZED_CUSTOM_PROPERTY";
    /** Reference to the NLS message for an invalid property */
    String NOT_VALID_CUSTOM_PROPERTY = "NOT_VALID_CUSTOM_PROPERTY";
    /** Reference to the NLS message for an invalid numerical property */
    String CONFIG_VALUE_NUMBER_EXCEPTION = "CONFIG_VALUE_NUMBER_EXCEPTION";
    /** Reference to the NLS message for a pool config mismatch */
    String POOL_MISMATCH = "POOL_MISMATCH";

}
