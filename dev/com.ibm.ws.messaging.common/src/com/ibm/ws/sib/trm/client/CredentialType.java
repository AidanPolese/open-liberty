/*
 * 
 * 
 * ============================================================================
 * IBM Confidential OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * 
 *
 * Change activity:
 *
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * LIDB2117        040818 vaughton Original
 * 229438          040906 matrober Password shown in trace
 * LIDB3818-56     210405 nottinga Updated for componentization
 * SIB0034.trm     050831 tmitchel Added Server Subject/OAT code.
 * sib0034.trm     090905 tmitchel Amend getOpaqueAuthorisationToken method.
 * 290290.3        051101 gelderd  Improved entry/exit trace for sib.trm.client.impl
 * 322231          051109 tmitchel Fixed CREDENTIAL_USER_SUBJECT code in constructor.
 * 321780          051110 nyoung   Support for cross server SIB Server Subject
 * 322231          051118 tmitchel Fixed CREDENTIAL_USER_SUBJECT code in constructor.
 * 364435          060425 nottinga Updated to use OAT for SIB server subject.
 * 374671          060628 nottinga Updated to send empty Subject as in 6.0.2.
 * 374671.1        060629 nottinga Updated to cope with null Subject.
 * 374671.3        060711 matrober SVT:CWSIA0004E on mixed release cluster
 * 377708          060713 matrober Suppress CNFE for AuthUtilsFactory
 * 379782          060815 matrober Non-vargs call of varargs method warning
 * 409598          061207 mnuttall Extend 377708 to underlying ClassResolver/ClassUtil
 * 499831          080222 djvines  Use autoboxing for trace
 * 500546          080226 sibcopyr Automatic update of trace guards
 * 533600          080724 nottinga Update to use SecurityUtils.
 * 604938          090813 djvines  Don't allow FFDC to see the password
 * 609077          091009 mleming  Prevent issues with trace and java 2 security
 * ============================================================================
 */

package com.ibm.ws.sib.trm.client;

import java.security.AccessController;

import javax.security.auth.Subject;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.ws.ffdc.FFDCSelfIntrospectable;
//import com.ibm.ws.sib.shell.util.ClassUtil;
import com.ibm.ws.sib.trm.impl.TrmConstantsImpl;
import com.ibm.ws.sib.trm.security.SecurityUtils;
import com.ibm.ws.sib.trm.utils.TraceUtils;
import com.ibm.ws.sib.utils.RuntimeInfo;
import com.ibm.ws.sib.utils.ras.SibTr;

/*
 * Capture the type of the authentication credentials to be used for a remote
 * client connection.
 */

final class CredentialType implements FFDCSelfIntrospectable {

  static String className = CredentialType.class.getName();
  static TraceComponent tc = SibTr.register(CredentialType.class, TrmConstantsImpl.MSG_GROUP, TrmConstantsImpl.MSG_BUNDLE);

  private static SecurityUtils _utils;

  static {
 
    try {
      AccessController.doPrivileged (
        new java.security.PrivilegedExceptionAction<Object>() {
          public Object run() throws Exception {
            // 409598: call to ClassUtil.loadClass() should not generate an FFDC
            // unless we're in a server environment
            boolean suppressFFDC = RuntimeInfo.isThinClient() || RuntimeInfo.isFatClient();
            
            // Kavitha - commenting out ClassUtil removed
/*
            final Class<?> clazz = ClassUtil.loadClass("com.ibm.ws.sib.trm.security.impl.SecurityUtilsImpl", suppressFFDC);
            _utils = (SecurityUtils)clazz.newInstance();*/

            return null;
          }
      });

    } catch (Exception e)
    {
      // No FFDC code needed
      Throwable cause = e.getCause();
      if (cause instanceof ClassNotFoundException)
      {
        // People get very worried when they see this CNFE (for AuthUtilsFactory), as it is a perfectly normal
        // thing to happen when running outside the server container. Avoid this problem by hiding the exception
        // better.
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "CNFE received - this is NORMAL if we are running in a client environment");
        // The following statement indicates the class that could not be found (AuthUtilsFactory)
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, cause.getMessage());

      } else
      {
        SibTr.exception(tc, e);
      }//if
    }//try

  }

  private String credentialType = TrmConstantsImpl.CREDENTIAL_USERID_PASSWORD;
  private Subject subject = null;
  private byte[] OAT = null;
  private String _busName;
  private String userid = "";
  private String password = "";

  // Constructors

  public CredentialType (String busName, Subject subject) {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(tc, "CredentialType", new Object[]{ busName, TraceUtils.subjectToString(subject)});

    _busName = busName;

    if (!isEmpty(subject))
    {
      this.subject = subject;

      try {
        if (_utils != null) {

          if (_utils.isSIBServerSubject(subject)) {
            credentialType = TrmConstantsImpl.CREDENTIAL_SIB_SUBJECT;
          } else {
            credentialType = TrmConstantsImpl.CREDENTIAL_SUBJECT;
          }
        }

      } catch (Exception e) {
        FFDCFilter.processException(e, className + "<init>", TrmConstantsImpl.PROBE_1, this);
        SibTr.exception(tc, e);
      }
    }

    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.debug(tc, toString());
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(tc, "CredentialType", this);
  }

  public CredentialType (String u, String p) {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(tc, "CredentialType", new Object[]{ u }); // don't trace pwd

    userid = u;
    password = p;

    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.debug(tc, toString());
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(tc, "CredentialType", this);
  }

  // Methods

  public String getCredentialType () {
    return credentialType;
  }

  public Subject getSubject () {
    return subject;
  }

  public String getUserid () {
    return userid;
  }

  public String getPassword () {
    return password;
  }

  public byte[] getOAT (String meName)
  {
    if (OAT == null && _utils != null)
    {
      OAT = _utils.getOpaqueAuthorizationToken(_busName, meName, subject);
    }

    return OAT;
  }

  private boolean isEmpty(Subject identity)
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(tc, "isEmpty", new Object[]{TraceUtils.subjectToString(identity)});
    boolean retVal = false;

    if (identity == null)
    {
      // If identity is null, then clearly it is empty!
      retVal = true;

    } else
    {
      // Identity is not null.

      // identity is trace in the method entry.
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.debug(tc, "publicCred.isEmpty: "+identity.getPublicCredentials().isEmpty());
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.debug(tc, "privateCred.isEmpty: "+identity.getPrivateCredentials().isEmpty());

      retVal = (identity.getPublicCredentials().isEmpty() &&
          identity.getPrivateCredentials().isEmpty());
    }//if

    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(tc, "isEmpty", Boolean.valueOf(retVal));
    return retVal;
  }

  public String toString ()
  {

    String displayPW = null;
    if (password == null)
    {
      displayPW = "<null>";
    } else
    {
      displayPW = "****";
    }

    return "credentialType="+credentialType+",userid="+userid+",password="+displayPW;
  }

  public String[] introspectSelf()
  {
    return new String[] { toString() };
  }
}
