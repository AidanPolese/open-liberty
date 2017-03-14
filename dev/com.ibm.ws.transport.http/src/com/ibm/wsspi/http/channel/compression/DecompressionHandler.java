// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70. (C) COPYRIGHT International Business Machines Corp. 2004, 2009
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.wsspi.http.channel.compression;

import java.util.List;
import java.util.zip.DataFormatException;

import com.ibm.wsspi.bytebuffer.WsByteBuffer;

/**
 * Interface for a body handler that will decompress the input data.
 * 
 */
public interface DecompressionHandler {

    /**
     * Start decompressing the input buffer. This may or may not use the full
     * input buffer when making the output data. Caller must check the input
     * buffer remaining count after the call to see if unused data was left over.
     * The caller is also responsible for releasing the output buffers that are
     * returned.
     * 
     * @param buffer
     * @return List<WsByteBuffer>
     * @throws DataFormatException
     *             if an error happens while decompressing data
     */
    List<WsByteBuffer> decompress(WsByteBuffer buffer) throws DataFormatException;

    /**
     * Query whether this handler is enabled or not. If not, it might indicate
     * a handler that does no actual decompression (identity).
     * 
     * @return boolean
     */
    boolean isEnabled();

    /**
     * Query whether we have reached the end of the stream.
     * 
     * @return boolean
     */
    boolean isFinished();

    /**
     * When the body is complete, this will notify the handler to cleanup.
     * 
     */
    void close();

    /**
     * Query the number of compressed bytes passed to this handler so far.
     * 
     * @return long
     */
    long getBytesRead();

    /**
     * Query the number of decompressed bytes handed out by this handler.
     * 
     * @return long
     */
    long getBytesWritten();
}
