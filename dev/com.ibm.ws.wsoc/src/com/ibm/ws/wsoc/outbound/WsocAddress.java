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
package com.ibm.ws.wsoc.outbound;

import java.net.InetSocketAddress;
import java.net.URI;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.wsspi.http.channel.outbound.HttpAddress;

/**
 *
 */
public class WsocAddress implements HttpAddress {

    private static final TraceComponent tc = Tr.register(WsocAddress.class);

    private InetSocketAddress isAddr = null;

    private String scheme = "";

    private String schemeKey = "";

    private String host = "";

    // Assume port 80 if none specified
    private int port = 80;

    private URI path = null;

    public WsocAddress(URI path) {

        // convert to lower case to match out chain name, and for any comparisons in this class.   
        schemeKey = path.getScheme().toLowerCase();
        scheme = path.getScheme();
        host = path.getHost();
        port = path.getPort();

        if (port == -1) {
            if ("wss".equals(schemeKey)) {
                port = 443;
            }
            else {
                port = 80;
            }
        }
        this.path = path;
        isAddr = new InetSocketAddress(host, port);

    }

    public String getChainKey() {
        return "OutboundWsoc" + schemeKey;
    }

    public void validateURI() {
        if (!schemeKey.startsWith("ws") && !schemeKey.startsWith("wss")) {
            String msg = Tr.formatMessage(tc, "client.invalid.scheme", scheme);
            Tr.error(tc, "client.invalid.scheme", scheme);
            throw new IllegalArgumentException(msg);
        }

    }

    public boolean isSecure() {
        if ("wss".equals(schemeKey)) {
            return true;
        }
        return false;
    }

    public URI getURI() {
        return path;
    }

    public String getPath() {
        if (path.getPath() == null) {
            return "/";
        }
        else if (path.getPath().equals("")) {
            return "/";
        }

        return path.getPath();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.tcpchannel.TCPConnectRequestContext#getLocalAddress()
     */
    @Override
    public InetSocketAddress getLocalAddress() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.tcpchannel.TCPConnectRequestContext#getRemoteAddress()
     */
    @Override
    public InetSocketAddress getRemoteAddress() {
        return isAddr;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.http.channel.outbound.HttpAddress#getHostname()
     */
    @Override
    public String getHostname() {
        return host;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.http.channel.outbound.HttpAddress#isForwardProxy()
     */
    @Override
    public boolean isForwardProxy() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.tcpchannel.TCPConnectRequestContext#getConnectTimeout()
     */
    @Override
    public int getConnectTimeout() {
        return -1;
    }

    @Override
    public String toString() {
        return path.toString();
    }

}
