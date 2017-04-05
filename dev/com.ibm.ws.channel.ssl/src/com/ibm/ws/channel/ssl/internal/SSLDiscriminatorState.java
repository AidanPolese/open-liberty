//IBM Confidential OCO Source Material
//5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70. (C) COPYRIGHT International Business Machines Corp. 1997, 2007
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
//
//Change History:
//Date     UserId      Defect          Description
//--------------------------------------------------------------------------------
//082905   clanzen     LIDB3557-8      Flexible configuration support.
//040407   leeja       LIDB2924-15     Remove JSSE2 usage

package com.ibm.ws.channel.ssl.internal;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;

import com.ibm.wsspi.bytebuffer.WsByteBuffer;

/**
 * This class serves as a container for the results of the SSL Channel discriminator.
 * It is used to either pass results of the discriminator to the ready method of the
 * connection link or to save state between successive calls to the discriminator
 * resulting in a MAYBE response.
 */
public class SSLDiscriminatorState {

    /** SSL context used for the connection. */
    private SSLContext sslContext = null;
    /** SSL engine used during discrimination. */
    private SSLEngine sslEngine = null;
    /** Result from call to the SSL Engine. */
    SSLEngineResult sslEngineResult = null;
    /** Decrypted network buffer ready for application. */
    private WsByteBuffer decryptedNetBuffer = null;
    /** Network buffer position after call to decrypt. */
    private int netBufferPosition = 0;
    /** Network buffer limit after call to decrypt. */
    private int netBufferLimit = 0;

    /**
     * Constructor
     */
    public SSLDiscriminatorState() {
        // nothing to do
    }

    /**
     * Update this state object with current information. This is called when a
     * YES response comes from the discriminator. The position and limit must be
     * saved here so the ready method can adjust them right away.
     * 
     * @param context
     * @param engine
     * @param result
     * @param decNetBuf
     * @param position
     * @param limit
     */
    public void updateState(SSLContext context, SSLEngine engine, SSLEngineResult result, WsByteBuffer decNetBuf, int position, int limit) {
        this.sslContext = context;
        this.sslEngine = engine;
        this.sslEngineResult = result;
        this.decryptedNetBuffer = decNetBuf;
        this.netBufferPosition = position;
        this.netBufferLimit = limit;
    }

    /**
     * Update this state object with current information. This is called when a
     * MAYBE response comes from the discriminator. The position and limit don't
     * need to be updated since the call to unwrap didn't do anything. The result
     * was MAYBE because more data was needed.
     * 
     * @param engine
     * @param decNetBuf
     */
    public void updateState(SSLEngine engine, WsByteBuffer decNetBuf) {
        this.sslEngine = engine;
        this.decryptedNetBuffer = decNetBuf;
    }

    /**
     * Access the ssl engine for this connection.
     * 
     * @return SSLEngine
     */
    public SSLEngine getEngine() {
        return this.sslEngine;
    }

    /**
     * Access the ssl context object for this discrimination attempt.
     * 
     * @return SSLContext
     */
    public SSLContext getSSLContext() {
        return this.sslContext;
    }

    /**
     * Access the ssl engine result object.
     * 
     * @return SSLEngineResult
     */
    public SSLEngineResult getEngineResult() {
        return this.sslEngineResult;
    }

    /**
     * Access the decrypted data buffer.
     * 
     * @return WsByteBuffer
     */
    public WsByteBuffer getDecryptedNetBuffer() {
        return this.decryptedNetBuffer;
    }

    /**
     * Query the saved buffer position at the network layer.
     * 
     * @return int
     */
    public int getNetBufferPosition() {
        return this.netBufferPosition;
    }

    /**
     * Query the saved buffer limit at the network layer.
     * 
     * @return int
     */
    public int getNetBufferLimit() {
        return this.netBufferLimit;
    }
}
