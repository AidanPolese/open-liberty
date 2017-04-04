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
package com.ibm.ws.transport.iiop.security.config.css;

import org.omg.CSI.ITTAbsent;
import org.omg.CSI.IdentityToken;
import org.omg.IOP.Codec;

import com.ibm.websphere.ras.annotation.Trivial;

/**
 * @version $Revision: 503274 $ $Date: 2007-02-03 10:19:18 -0800 (Sat, 03 Feb 2007) $
 */
public class CSSSASITTAbsent implements CSSSASIdentityToken {

    @Override
    public IdentityToken encodeIdentityToken(Codec codec) {

        IdentityToken token = new IdentityToken();
        token.absent(true);
        return token;
    }

    @Override
    @Trivial
    public void toString(String spaces, StringBuilder buf) {
        buf.append(spaces).append("CSSSASITTAbsent\n");
    }

    /** {@inheritDoc} */
    @Override
    public int getType() {
        return ITTAbsent.value;
    }

}
