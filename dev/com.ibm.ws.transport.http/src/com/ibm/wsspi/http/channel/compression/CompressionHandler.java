// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70. (C) COPYRIGHT International Business Machines Corp. 2004, 2009
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.wsspi.http.channel.compression;

import java.util.List;

import com.ibm.wsspi.bytebuffer.WsByteBuffer;
import com.ibm.wsspi.http.channel.values.ContentEncodingValues;

/**
 * Handler interface that allows different compression instances to apply to
 * outbound HTTP messages.
 * 
 */
public interface CompressionHandler {

    /**
     * Query the proper Content-Encoding value for this particular handler.
     * 
     * @return ContentEncodingValues
     */
    ContentEncodingValues getContentEncoding();

    /**
     * Compress the input buffer. The caller is responsible for releasing the
     * new buffers that are returned.
     * 
     * @param buffer
     * @return List<WsByteBuffer>
     */
    List<WsByteBuffer> compress(WsByteBuffer buffer);

    /**
     * Compress the list of input buffers into a list of output buffers. The
     * caller is responsible for releasing the list of new buffers returned.
     * 
     * @param buffers
     * @return List<WsByteBuffer>
     */
    List<WsByteBuffer> compress(WsByteBuffer[] buffers);

    /**
     * Called when the input data is complete, this will trigger any final
     * compression output and return any remaining data to write out. It is
     * the caller's responsibility to release these buffers.
     * 
     * @return List<WsByteBuffer> - any final data not yet passed back
     */
    List<WsByteBuffer> finish();

    /**
     * Query whether the finish() api has already been called.
     * 
     * @return boolean
     */
    boolean isFinished();

    /**
     * Query the number of raw bytes handed to this handler for compression so
     * far.
     * 
     * @return long
     */
    long getBytesRead();

    /**
     * Query the number of compressed bytes handed out by this handler.
     * 
     * @return long
     */
    long getBytesWritten();
}
