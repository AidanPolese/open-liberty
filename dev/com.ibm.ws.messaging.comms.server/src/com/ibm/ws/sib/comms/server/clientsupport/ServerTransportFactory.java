/*
 * @start_prolog@
 * Version: @(#) 1.31 SIB/ws/code/sib.comms.server.impl/src/com/ibm/ws/sib/comms/server/clientsupport/ServerTransportFactory.java, SIB.comms, WASX.SIB, aa1225.01 09/04/16 03:30:31 [7/2/12 05:59:00]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 Copyright IBM Corp. 2004, 2009
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * @end_prolog@
 *
 * Change activity:
 *
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * Creation        030325 schmittm Original
 * d170527         030625 mattheg  Tidy and change to SibTr
 * d170639         030627 mattheg  NLS all the messages
 * f171046         030703 mattheg  Improve the server side logging and error handling
 * f172397         030727 Niall    Use the Generic Accept Listener hierarchy
 * F174602         030820 prestona Switch to using SICommsException
 * f175774         030903 mattheg  Allow a system property to override the default listening port
 * F189351         040203 prestona CF admin support
 * D209401         040611 mattheg  Comms service utility
 * F201972.2       040727 mattheg  Core SPI Exceptions rework (not change flagged)
 * D210259.1       040819 mattheg  Ensure we initialise CommsUtils
 * D225856         041006 mattheg  Update FFDC class name (not change flagged)
 * D321398         051107 mattheg  Initialise the FFDC module
 * D377648         060719 mattheg  Use CommsByteBuffer
 * SIB0048b.com.1  060901 mattheg  Remove reference to CommsServiceUtility and move CommsDiagnosticModule
 * D395634         060510 mattheg  Package change of diagnostic module
 * SIB0100.wmq.3   070816 mleming  Extract AcceptListenerFactoryImpl to its own file
 * 464663          070905 sibcopyr Automatic update of trace guards 
 * 581917          090415 mleming  Don't output SERVER_STARTED_SICO2001 message here
 * ============================================================================
 */
package com.ibm.ws.sib.comms.server.clientsupport;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.ws.sib.comms.CommsConstants;
import com.ibm.ws.sib.comms.common.CommsDiagnosticModule;
import com.ibm.ws.sib.comms.server.AcceptListenerFactoryImpl;
import com.ibm.ws.sib.jfapchannel.server.ServerConnectionManager;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * @author schmittm
 * 
 */
public class ServerTransportFactory {
    /** Class name for FFDC's */
    private static String CLASS_NAME = ServerTransportFactory.class.getName();

    /** Register our trace component */
    private static TraceComponent tc = SibTr.register(ServerTransportFactory.class,
                                                      CommsConstants.MSG_GROUP,
                                                      CommsConstants.MSG_BUNDLE);

    /** Trace the class information */
    static {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            SibTr.debug(tc,
                        "Source info: @(#)SIB/ws/code/sib.comms.server.impl/src/com/ibm/ws/sib/comms/server/clientsupport/ServerTransportFactory.java, SIB.comms, WASX.SIB, aa1225.01 1.31");

        // Initialise the FFDC diagnositic module
        CommsDiagnosticModule.initialise();
    }

    /**
     * Constructor
     * 
     * @param port
     */
    public ServerTransportFactory(int port) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(tc, "<init>", "" + port);

        try {
            ServerConnectionManager.initialise(new AcceptListenerFactoryImpl());
        } catch (Throwable t) {
            FFDCFilter.processException(t, CLASS_NAME + ".<init>",
                                        CommsConstants.SERVERTRANSPORTFACTORY_INIT_02,
                                        this);

            SibTr.error(tc, "SERVER_FAILED_TO_START_SICO2004", t);
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(tc, "<init>");
    }

    /**
     * Starts the comms server communications.
     * 
     */
    public static void startServerComms() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.entry(tc, "startServerComms");

        try {
            ServerConnectionManager.initialise(new AcceptListenerFactoryImpl());
        } catch (Throwable t) {
            FFDCFilter.processException(t, CLASS_NAME + ".startServerComms",
                                        CommsConstants.SERVERTRANSPORTFACTORY_INIT_02,
                                        null);

            SibTr.error(tc, "SERVER_FAILED_TO_START_SICO2004", t);
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            SibTr.exit(tc, "startServerComms");
    }

}
