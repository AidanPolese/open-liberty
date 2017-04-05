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

import org.omg.CSI.EstablishContext;
import org.omg.CSIIOP.AS_ContextSec;
import org.omg.CSIIOP.EstablishTrustInClient;
import org.omg.GSSUP.GSSUPMechOID;
import org.omg.IOP.Codec;

import com.ibm.ws.security.csiv2.config.LTPAMech;
import com.ibm.ws.security.csiv2.config.tss.ServerLTPAMechConfigFactory;
import com.ibm.ws.security.csiv2.config.tss.TSSUnknownASMechConfig;
import com.ibm.ws.security.csiv2.util.LocationUtils;
import com.ibm.ws.transport.iiop.security.SASException;
import com.ibm.ws.transport.iiop.security.util.GSSExportedName;
import com.ibm.ws.transport.iiop.security.util.Util;
import com.ibm.wsspi.security.csiv2.TrustedIDEvaluator;

/**
 * @version $Rev: 503274 $ $Date: 2007-02-03 10:19:18 -0800 (Sat, 03 Feb 2007) $
 */
public abstract class TSSASMechConfig implements Serializable {

    public abstract short getSupports();

    public abstract short getRequires();

    public abstract AS_ContextSec encodeIOR(Codec codec) throws Exception;

    public static TSSASMechConfig decodeIOR(AS_ContextSec context) throws Exception {
        TSSASMechConfig result = null;

        if (context.target_supports == 0) {
            result = new TSSNULLASMechConfig();
        } else {
            GSSExportedName name = Util.decodeGSSExportedName(context.target_name);
            if (GSSUPMechOID.value.substring(4).equals(name.getOid())) {
                result = new TSSGSSUPMechConfig(null, name.getName(), (context.target_requires == EstablishTrustInClient.value));
            } else if (LTPAMech.LTPA_OID.substring(4).equals(name.getOid())) {
                if (LocationUtils.isServer()) {
                    result = ServerLTPAMechConfigFactory.getServerLTPAMechConfig(context);
                } else {
                    result = new TSSUnknownASMechConfig("LTPA");
                }
            } else {
                result = new TSSUnknownASMechConfig(name.getOid());
            }
        }

        return result;
    }

    public abstract Subject check(EstablishContext msg, Codec codec) throws SASException;

    public abstract boolean isTrusted(TrustedIDEvaluator trustedIDEvaluator, EstablishContext msg, Codec codec);

    public abstract String getMechanism();

    public abstract void toString(String spaces, StringBuilder buf);

}
