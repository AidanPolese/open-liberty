//-------------------------------------------------------------------------------
//IBM Confidential OCO Source Material
//55724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2003, 2005, 2009
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
//
//Change History:
//
//Change ID     Author    Abstract
//---------     --------  -------------------------------------------------------
//-------------------------------------------------------------------------------
package com.ibm.wsspi.udpchannel;

import java.net.InetSocketAddress;

/**
 * This is a factory for the creation of the connection request objects.
 */
public class UDPRequestContextFactory {
    // Reference to single instance of this class
    private static UDPRequestContextFactory instanceRef = null;

    /**
     * Create a new request context based upon the input needed to fully define
     * the context.
     * 
     * @param _localHostName host name of the local side of the connection. null is valid.
     * @param _localPort port to be used by the local side of the connection
     * @return a connect request context to be used by the channel connection
     */
    public UDPRequestContext createUDPRequestContext(
                                                     String _localHostName,
                                                     int _localPort) {

        return new UDPRequestContextImpl(
                        _localHostName,
                        _localPort);
    }

    /**
     * Create the singleton instance of the class here
     * 
     */
    static private synchronized void createSingleton() {
        if (null == instanceRef) {
            instanceRef = new UDPRequestContextFactory();
        }
    }

    /**
     * This class implements the singleton pattern. This method is provided
     * to return a reference to the single instance of this class in existence.
     * 
     * @return UDPRequestContextFactory
     */
    public static UDPRequestContextFactory getRef() {
        if (instanceRef == null) {
            createSingleton();
        }

        return instanceRef;
    }

    /**
     * Implementation of the connection request context.
     */
    public static class UDPRequestContextImpl implements UDPRequestContext {

        private InetSocketAddress localAddress = null;

        /**
         * Construct a new connection request context based upon the input needed to fully define
         * the context.
         * 
         * @param _localHostName host name of the local side of the connection. null is valid.
         * @param _localPort port to be used by the local side of the connection
         */
        public UDPRequestContextImpl(String _localHostName, int _localPort) {
            if (_localHostName != null && !_localHostName.equals("*")) {
                this.localAddress = new InetSocketAddress(_localHostName, _localPort);
            } else {
                this.localAddress = new InetSocketAddress(_localPort);
            }
        }

        /*
         * @see com.ibm.websphere.udpchannel.UDPRequestContext#getLocalAddress()
         */
        @Override
        public InetSocketAddress getLocalAddress() {
            return this.localAddress;
        }
    }

}
