/*
 * @start_prolog@
 * Version: @(#) 1.46 SIB/ws/code/sib.trm.client.impl/src/com/ibm/ws/sib/trm/client/ClientBootstrapHandler.java, SIB.trm, WASX.SIB, aa1225.01 08/02/25 21:39:04 [7/2/12 05:58:42]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 *
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70  Copyright IBM Corp. 2004, 2008
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
 * LIDB2117        030509 vaughton Original
 * LIDB3645        050124 vaughton Recovery mode
 * 281683.1        050926 gelderd  SVC: Improved diagnostic on bus connect failure - part 1
 * 290290.3        051101 gelderd  Improved entry/exit trace for sib.trm.client.impl
 * 499831          080222 djvines  Use autoboxing for trace
 * 500546          080226 sibcopyr Automatic update of trace guards 
 * ============================================================================
 */

package com.ibm.ws.sib.trm.client;

import com.ibm.ejs.ras.TraceNLS;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.sib.exception.SIErrorException;
import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.ws.sib.comms.ClientConnection;
import com.ibm.ws.sib.mfp.trm.TrmClientBootstrapRequest;
import com.ibm.ws.sib.mfp.trm.TrmFirstContactMessageType;
import com.ibm.ws.sib.mfp.trm.TrmMessageFactory;
import com.ibm.ws.sib.trm.impl.TrmConstantsImpl;
import com.ibm.ws.sib.utils.ras.SibTr;


/*
 * Client handler specialising in a bootstrap request and reply
 */

class ClientBootstrapHandler extends ClientHandler {

  //@start_class_string_prolog@
  public static final String $sccsid = "@(#) 1.46 SIB/ws/code/sib.trm.client.impl/src/com/ibm/ws/sib/trm/client/ClientBootstrapHandler.java, SIB.trm, WASX.SIB, aa1225.01 08/02/25 21:39:04 [7/2/12 05:58:42]";
  //@end_class_string_prolog@

  private static final String className = ClientBootstrapHandler.class.getName();
  private static final TraceComponent tc = SibTr.register(ClientBootstrapHandler.class, TrmConstantsImpl.MSG_GROUP, TrmConstantsImpl.MSG_BUNDLE);
  private static final TraceNLS nls = TraceNLS.getTraceNLS(TrmConstantsImpl.MSG_BUNDLE);

  static {
    if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "Source Info: " + $sccsid);
  }

  /*
   * Constructor
   */

  private String bootstrapTransportChain;

  public ClientBootstrapHandler (ClientAttachProperties cap, CredentialType credentialType, String bootstrapTransportChain) {
    super(cap, credentialType);

    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(tc, "ClientBootstrapHandler", new Object[] { cap, credentialType, bootstrapTransportChain });

    this.bootstrapTransportChain = bootstrapTransportChain;

    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(tc, "ClientBootstrapHandler", this);
  }

  /*
   * Perform a bootstrap request and receive the reply
   */

  public boolean connect(ClientConnection cc) {

    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(tc, "connect", new Object[]{ cc });

    boolean rc = true;

    try {
     
      //get TrmFactory from bundle service facade
      TrmMessageFactory mf = TrmMessageFactory.getInstance();

      /*
       * Send the bootstrap request message
       */

      TrmClientBootstrapRequest cbr = mf.createNewTrmClientBootstrapRequest();

      cbr.setBusName(cap.getBusName());
      cbr.setCredentialType(credentialType.getCredentialType());
      cbr.setUserid(credentialType.getUserid());
      cbr.setPassword(credentialType.getPassword());
      cbr.setTargetGroupName(cap.getTargetGroupName());
      cbr.setTargetGroupType(cap.getTargetGroupType());
      cbr.setTargetSignificance(cap.getTargetSignificance());
      cbr.setConnectionProximity(cap.getConnectionProximity());
      cbr.setTargetTransportChain(cap.getTargetTransportChain());
      cbr.setBootstrapTransportChain(bootstrapTransportChain);
      cbr.setConnectionMode(cap.getConnectionMode());                                                         //LIDB3645

      byte[] out = cbr.encode(cc);
      if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, Utils.outBound("client bootstrap request"));
      byte[] in = cc.trmHandshakeExchange(out);
      if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, Utils.inBound("client bootstrap reply"));

      /*
       * Process the bootstrap reply message
       */

      fcm = mf.createInboundTrmFirstContactMessage(in, 0, in.length);

      if (fcm.getMessageType() != TrmFirstContactMessageType.CLIENT_BOOTSTRAP_REPLY) {
        Object[] objs = new Object[] { cap.getBusName(),
            fcm.getMessageType().toString(),
            TrmFirstContactMessageType.CLIENT_BOOTSTRAP_REPLY.toString() };
        SibTr.error(tc, "PROTOCOL_ERROR_CWSIT0038", objs);
        setException(new SIErrorException(nls.getFormattedMessage(
            "PROTOCOL_ERROR_CWSIT0038", objs, null)));
        SibTr.exception(tc, getException());
        rc = false;
      }

    } catch (Exception e) {
      FFDCFilter.processException(e, className + ".connect", TrmConstantsImpl.PROBE_1, this);
      SibTr.exception(tc, e);
      setException(e);
      rc = false;
    }

    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(tc, "connect", Boolean.valueOf(rc));
    return rc;
  }

}
