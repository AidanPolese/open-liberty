/*COPYRIGHT_START***********************************************************
 *
 * IBM Confidential OCO Source Material
 * 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 *   IBM DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE, INCLUDING
 *   ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 *   PURPOSE. IN NO EVENT SHALL IBM BE LIABLE FOR ANY SPECIAL, INDIRECT OR
 *   CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF
 *   USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR
 *   OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE
 *   OR PERFORMANCE OF THIS SOFTWARE.
 *
 *  @(#) 1.5 SERV1/ws/code/web.session.shell/src/com/ibm/ws/session/WasHttpSessionAdapter.java, WASCC.web.session, WASX.SERV1, o0901.11 5/30/08 16:35:27 [1/9/09 15:01:01]
 *
 * @(#)file   WasHttpSessionAdapter.java
 * @(#)version   1.5
 * @(#)date      5/30/08
 *
 *COPYRIGHT_END*************************************************************/
package com.ibm.ws.session;

import java.util.logging.Level;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;

import com.ibm.ws.session.utils.LoggingUtil;
import com.ibm.wsspi.session.IProtocolAdapter;
import com.ibm.wsspi.session.ISession;

public class WasHttpSessionAdapter implements IProtocolAdapter {
    /*
     * For logging the CMVC file version once.
     */
    private static boolean _loggedVersion = false;

    private ServletContext _servletCtx = null;
    private SessionContext _sessionCtx = null;

    private static final String methodClassName = "WasHttpSessionAdapter";

    /*
     * Default constructor.
     */
    public WasHttpSessionAdapter(SessionContext sessionCtx, ServletContext servletCtx) {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && LoggingUtil.SESSION_LOGGER_CORE.isLoggable(Level.FINE)) {
            if (!_loggedVersion) {
                LoggingUtil.SESSION_LOGGER_CORE.logp(Level.FINE, methodClassName, "", "CMVC Version 1.5 5/30/08 16:35:27");
                _loggedVersion = true;
            }
        }
        _servletCtx = servletCtx;
        _sessionCtx = sessionCtx;
    }

    /*
     * Method adapt
     * Creates a WAS http session wrapper, SessionData, around the ISession
     */
    public Object adapt(ISession isess) {

        Object adaptation = isess.getAdaptation();
        if (null == adaptation) {
            adaptation = _sessionCtx.createSessionObject(isess, _servletCtx);
            isess.setAdaptation(adaptation);
        }
        return adaptation;
    }

    public Object adaptToAppSession(ISession isess) {
        Object adaptation = isess.getAdaptation(ISession.ADAPT_TO_APPLICATION_SESSION);
        if (null == adaptation) {
            // adaptation = _sessionCtx.createAppSessionObject(isess, _servletCtx);
            adaptation = new IBMApplicationSessionImpl(isess);
            isess.setAdaptation(adaptation, ISession.ADAPT_TO_APPLICATION_SESSION);
        }
        return adaptation;

    }

    // XD methods
    // not used in ND
    public Object adapt(ISession session, Integer protocol) {
        return null;
    }

    public Object adapt(ISession session, Integer protocol, ServletRequest request, ServletContext context) {
        return null;
    }

    public Object getCorrelator(ServletRequest request, Object session) {
        return null;
    }

    public Integer getProtocol(Object session) {
        return null;
    }
    // end of XD methods
}
