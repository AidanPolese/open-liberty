/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
/*
 * Some of the code was derived from code supplied by the Apache Software Foundation licensed under the Apache License, Version 2.0.
 */
package com.ibm.ws.transport.iiop.security.config.tss;

import java.io.Serializable;

import javax.security.auth.Subject;

import org.omg.CSI.IdentityToken;
import org.omg.IOP.Codec;

import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.ws.transport.iiop.security.SASException;

/**
 * @version $Rev: 503274 $ $Date: 2007-02-03 10:19:18 -0800 (Sat, 03 Feb 2007) $
 */
public abstract class TSSSASIdentityToken implements Serializable {

    public abstract short getType();

    public abstract String getOID();

    public abstract Subject check(IdentityToken identityToken, Codec codec) throws SASException;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof TSSSASIdentityToken))
            return false;

        final TSSSASIdentityToken token = (TSSSASIdentityToken) o;

        if (getType() != token.getType())
            return false;
        if (!getOID().equals(token.getOID()))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = getOID().hashCode();
        result = 29 * result + getType();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        toString("", buf);
        return buf.toString();
    }

    @Trivial
    abstract void toString(String spaces, StringBuilder buf);

}
