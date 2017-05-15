/*
* IBM Confidential
*
* OCO Source Materials
*
* Copyright IBM Corp. 2017
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.ws.security.csiv2.config.tss;

import java.io.Serializable;

import org.omg.CSIIOP.TransportAddress;

/**
 * Wrapper around TransportAddress to be able to uniquely insert into collections that use hash codes.
 */
public class ServerTransportAddress implements Serializable {

    private static final long serialVersionUID = 1L;

    private final TransportAddress transportAddress;

    public ServerTransportAddress(TransportAddress transportAddress) {
        this.transportAddress = transportAddress;
    }

    public String getHost() {
        return transportAddress.host_name;
    }

    public short getPort() {
        return transportAddress.port;
    }

    @Override
    public int hashCode() {
        return transportAddress.host_name.hashCode() + transportAddress.port;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ServerTransportAddress) {
            ServerTransportAddress otherAddress = (ServerTransportAddress) other;
            if (transportAddress.host_name.equals(otherAddress.transportAddress.host_name) && (transportAddress.port == otherAddress.transportAddress.port)) {
                return true;
            }
        }
        return false;
    }

}
