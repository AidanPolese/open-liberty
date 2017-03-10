/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2017
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.zos.core.utils;

import java.nio.ByteBuffer;

/**
 * Provides native related services.
 */
public interface NativeUtils {

    /**
     * Retrieve the current MVS STCK value.
     *
     * @return The current native MVS STCK.
     */
    public long getSTCK();

    /**
     * Create a new {@code DirectByteBuffer} that maps the specified
     * address for the specified size.
     */
    public ByteBuffer mapDirectByteBuffer(final long address, final int size);

    /**
     * Pad/truncate a string to the required length and convert to EBCDIC
     *
     * @param s A Java String
     * @param requiredLength The right length
     * @return an EBCDIC byte array of the required length, padded with blanks
     *         or truncated as necessary. A null if something went wrong.
     */
    public byte[] convertAsciiStringToFixedLengthEBCDIC(final String s, final int requiredLength);

    /**
     * Retrieve the current task ID using pthread_self()
     *
     * @return The current task ID.
     */
    public long getTaskId();

    /**
     * Retrieve the servers process ID using getpid()
     *
     * @return The servers process ID.
     */
    public int getPid();

    /**
     * Retrieve SMF data.
     * psatold 4 ttoken 16 thread id 8 and cvtldto 8
     *
     * @return SMF data.
     */
    public byte[] getSmfData();

    /**
     * Retrieve TIMEUSED data.
     *
     * @return TIMEUSED data.
     */
    public byte[] getTimeusedData();

    /**
     * Retrieve the server process UMASK.
     *
     * @return An decimal representation of the UMASK.
     */
    public int getUmask();

}