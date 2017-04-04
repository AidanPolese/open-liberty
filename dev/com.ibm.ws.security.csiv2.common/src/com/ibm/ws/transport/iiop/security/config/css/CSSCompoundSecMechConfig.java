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

import java.io.Serializable;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.UserException;
import org.omg.CSI.EstablishContext;
import org.omg.CSI.SASContextBody;
import org.omg.CSI.SASContextBodyHelper;
import org.omg.IOP.Codec;
import org.omg.IOP.SecurityAttributeService;
import org.omg.IOP.ServiceContext;
import org.omg.PortableInterceptor.ClientRequestInfo;

import com.ibm.ejs.ras.TraceNLS;
import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.ws.transport.iiop.security.config.ConfigUtil;
import com.ibm.ws.transport.iiop.security.config.tss.TSSCompoundSecMechConfig;

/**
 * @version $Rev: 503274 $ $Date: 2007-02-03 10:19:18 -0800 (Sat, 03 Feb 2007) $
 */
public class CSSCompoundSecMechConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final TraceComponent tc = Tr.register(CSSCompoundSecMechConfig.class);

    private short supports;
    private short requires;
    private CSSTransportMechConfig transport_mech;
    private CSSASMechConfig as_mech;
    private CSSSASMechConfig sas_mech;
    private String cantHandleMsg;

    public CSSTransportMechConfig getTransport_mech() {
        return transport_mech;
    }

    public void setTransport_mech(CSSTransportMechConfig transport_mech) {
        this.transport_mech = transport_mech;
        this.supports |= transport_mech.getSupports();
        this.requires |= transport_mech.getRequires();
    }

    public CSSASMechConfig getAs_mech() {
        return as_mech;
    }

    public void setAs_mech(CSSASMechConfig as_mech) {
        this.as_mech = as_mech;
        this.supports |= as_mech.getSupports();
        this.requires |= as_mech.getRequires();
    }

    public CSSSASMechConfig getSas_mech() {
        return sas_mech;
    }

    public void setSas_mech(CSSSASMechConfig sas_mech) {
        this.sas_mech = sas_mech;
        this.supports |= sas_mech.getSupports();
        this.requires |= sas_mech.getRequires();
    }

    public boolean canHandle(TSSCompoundSecMechConfig requirement) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
            Tr.debug(tc, "canHandle()");
            Tr.debug(tc, "    CSS SUPPORTS: " + ConfigUtil.flags(supports));
            Tr.debug(tc, "    CSS REQUIRES: " + ConfigUtil.flags(requires));
            Tr.debug(tc, "    TSS SUPPORTS: " + ConfigUtil.flags(requirement.getSupports()));
            Tr.debug(tc, "    TSS REQUIRES: " + ConfigUtil.flags(requirement.getRequires()));
        }

        if ((supports & requirement.getRequires()) != requirement.getRequires()) {
            buildSupportsFailedMsg(requirement);
            return false;
        }

        if ((requires & requirement.getSupports()) != requires) {
            buildRequiresFailedMsg(requirement);
            return false;
        }

        if (!transport_mech.canHandle(requirement.getTransport_mech(), as_mech.getMechanism())) {
            cantHandleMsg = transport_mech.getCantHandleMsg();
            return false;
        }

        if (!as_mech.canHandle(requirement.getAs_mech())) {
            buildAsFailedMsg(requirement);
            return false;
        }

        if (!sas_mech.canHandle(requirement.getSas_mech(), as_mech.getMechanism())) {
            cantHandleMsg = sas_mech.getCantHandleMsg();
            return false;
        }

        return true;
    }

    /**
     * @param requirement
     */
    private void buildSupportsFailedMsg(TSSCompoundSecMechConfig requirement) {
        if (!as_mech.getMechanism().equalsIgnoreCase(CSSNULLASMechConfig.mechanism)) {
            cantHandleMsg = TraceNLS.getFormattedMessage(this.getClass(),
                                                         com.ibm.ws.security.csiv2.TraceConstants.MESSAGE_BUNDLE,
                                                         "CSIv2_CLIENT_COMPATIBLE_ALL_LAYERS_SUPPORTS_FAILED",
                                                         new Object[] { as_mech.getMechanism(), ConfigUtil.flags(supports), ConfigUtil.flags(requirement.getRequires()) },
                                                         "CWWKS9551E: The client security policy has the transport, authentication and attribute layers configured for <{0}> with <{1}> as Supported in the server.xml file and the server security policy is configured with <{2}> as Required.");
        } else {
            cantHandleMsg = TraceNLS.getFormattedMessage(this.getClass(),
                                                         com.ibm.ws.security.csiv2.TraceConstants.MESSAGE_BUNDLE,
                                                         "CSIv2_CLIENT_COMPATIBLE_ALL_LAYERS_SUPPORTS_NO_AUTH_FAILED",
                                                         new Object[] { ConfigUtil.flags(supports), ConfigUtil.flags(requirement.getRequires()) },
                                                         "CWWKS9552E: The client security policy has the transport and attribute layers configured with <{0}> as Supported in the server.xml file and the server security policy is configured with <{1}> as Required.");

        }
    }

    /**
     * @param requirement
     */
    private void buildRequiresFailedMsg(TSSCompoundSecMechConfig requirement) {
        if (!as_mech.getMechanism().equalsIgnoreCase(CSSNULLASMechConfig.mechanism)) {
            cantHandleMsg = TraceNLS.getFormattedMessage(this.getClass(),
                                                         com.ibm.ws.security.csiv2.TraceConstants.MESSAGE_BUNDLE,
                                                         "CSIv2_CLIENT_COMPATIBLE_ALL_LAYERS_REQUIRES_FAILED",
                                                         new Object[] { as_mech.getMechanism(), ConfigUtil.flags(requires), ConfigUtil.flags(requirement.getSupports()) },
                                                         "CWWKS9553E: The client security policy has the transport, authentication and attribute layers configured for <{0}> with <{1}> as Required in the server.xml file and the server security policy is configured with <{2}> as Supported.");
        } else {
            cantHandleMsg = TraceNLS.getFormattedMessage(this.getClass(),
                                                         com.ibm.ws.security.csiv2.TraceConstants.MESSAGE_BUNDLE,
                                                         "CSIv2_CLIENT_COMPATIBLE_ALL_LAYERS_REQUIRES_NO_AUTH_FAILED",
                                                         new Object[] { ConfigUtil.flags(requires), ConfigUtil.flags(requirement.getSupports()) },
                                                         "CWWKS9554E: The client security policy has the transport and attribute layers configured with <{0}> as Required in the server.xml file and the server security policy is configured with <{1}> as Supported.");
        }
    }

    /**
     * @param requirement
     */
    private void buildAsFailedMsg(TSSCompoundSecMechConfig requirement) {
        String client_mechanism = as_mech.getMechanism();
        String server_mechanism = requirement.getAs_mech().getMechanism();
        if (client_mechanism.equalsIgnoreCase(CSSNULLASMechConfig.mechanism)) {
            cantHandleMsg = TraceNLS.getFormattedMessage(this.getClass(),
                                                         com.ibm.ws.security.csiv2.TraceConstants.MESSAGE_BUNDLE,
                                                         "CSIv2_CLIENT_COMPATIBLE_AUTTH_CLIENT_DISABLED_FAILED",
                                                         new Object[] { server_mechanism },
                                                         "CWWKS9566E: The client security policy authentication layer is disabled in the server.xml file and the server security policy authentication layer is configured with mechanism {0}.");

        } else if (server_mechanism.equalsIgnoreCase(CSSNULLASMechConfig.mechanism)) {
            cantHandleMsg = TraceNLS.getFormattedMessage(this.getClass(),
                                                         com.ibm.ws.security.csiv2.TraceConstants.MESSAGE_BUNDLE,
                                                         "CSIv2_CLIENT_COMPATIBLE_AUTH_SERVER_DISABLED_FAILED",
                                                         new Object[] { client_mechanism },
                                                         "CWWKS9567E: The client security policy has the authentication layer configured with mechanism {0} in the server.xml file and the server security policy authentication layer is disabled.");

        } else {
            cantHandleMsg = TraceNLS.getFormattedMessage(this.getClass(),
                                                         com.ibm.ws.security.csiv2.TraceConstants.MESSAGE_BUNDLE,
                                                         "CSIv2_CLIENT_COMPATIBLE_AUTH_MECHANISMS_FAILED",
                                                         new Object[] { client_mechanism, server_mechanism },
                                                         "CWWKS9565E: The client security policy has the authentication layer configured with mechanism {0} in the server.xml file and the server security policy configured with mechanism {1}.");
        }
    }

    @Trivial
    public String getCantHandleMsg() {
        return cantHandleMsg;
    }

    public ServiceContext generateServiceContext(Codec codec, TSSCompoundSecMechConfig requirement, ClientRequestInfo ri) throws UserException {
        if (as_mech instanceof CSSNULLASMechConfig && sas_mech.isAssertingITTAbsent(requirement.getSas_mech())) {
            return null;
        }

        EstablishContext msg = new EstablishContext();

        msg.client_context_id = 0;
        msg.client_authentication_token = as_mech.encode(requirement.getAs_mech(), sas_mech, ri, codec);
        msg.authorization_token = sas_mech.encodeAuthorizationElement();
        msg.identity_token = sas_mech.encodeIdentityToken(requirement.getSas_mech(), codec);

        ServiceContext context = new ServiceContext();

        SASContextBody sas = new SASContextBody();
        sas.establish_msg(msg);
        Any sas_any = ORB.init().create_any();
        SASContextBodyHelper.insert(sas_any, sas);
        context.context_data = codec.encode_value(sas_any);

        context.context_id = SecurityAttributeService.value;

        return context;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        toString("", buf);
        return buf.toString();
    }

    @Trivial
    void toString(String spaces, StringBuilder buf) {
        String moreSpaces = spaces + "  ";
        buf.append(spaces).append("CSSCompoundSecMechConfig: [\n");
        buf.append(moreSpaces).append("SUPPORTS: ").append(ConfigUtil.flags(supports)).append("\n");
        buf.append(moreSpaces).append("REQUIRES: ").append(ConfigUtil.flags(requires)).append("\n");
        if (transport_mech != null) {
            transport_mech.toString(moreSpaces, buf);
        }
        if (as_mech != null) {
            as_mech.toString(moreSpaces, buf);
        }
        if (sas_mech != null) {
            sas_mech.toString(moreSpaces, buf);
        }
        buf.append(spaces).append("]\n");
    }

}
