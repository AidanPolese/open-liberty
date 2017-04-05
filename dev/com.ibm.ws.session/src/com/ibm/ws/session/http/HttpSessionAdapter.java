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
 *  @(#) 1.5 SERV1/ws/code/web.session.core/src/com/ibm/ws/session/http/HttpSessionAdapter.java, WASCC.web.session.core, WASX.SERV1, o0901.11 5/30/08 16:34:51 [1/9/09 15:01:26]
 *
 * @(#)file   HttpSessionAdapter.java
 * @(#)version   1.5
 * @(#)date      5/30/08
 *
 *COPYRIGHT_END*************************************************************/

package com.ibm.ws.session.http;

import java.util.logging.Level;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;

import com.ibm.ws.session.utils.LoggingUtil;
import com.ibm.wsspi.session.IProtocolAdapter;
import com.ibm.wsspi.session.ISession;

public class HttpSessionAdapter implements IProtocolAdapter {

    // ----------------------------------------
    // Public methods
    // ----------------------------------------
    /*
     * For logging.
     */
    private static final String methodClassName = "HttpSessionAdapter";

    /*
     * For logging the CMVC file version once.
     */
    private static boolean _loggedVersion = false;

    /**
     * Default constructor.
     */
    public HttpSessionAdapter() {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && LoggingUtil.SESSION_LOGGER_CORE.isLoggable(Level.FINE)) {
            if (!_loggedVersion) {
                LoggingUtil.SESSION_LOGGER_CORE.logp(Level.FINE, methodClassName, "", "CMVC Version 1.5 5/30/08 16:34:51");
                _loggedVersion = true;
            }
        }
    }

    /**
     * Method adapt
     * <p>
     * This method adapts a protocol agnostic session into one that conforms to a protocol such as HTTP.
     * 
     * @see com.ibm.wsspi.session.IProtocolAdapter#adapt(com.ibm.wsspi.session.ISession, Integer)
     */
    public Object adapt(ISession session) {

        Object adaptation = session.getAdaptation();
        if (null == adaptation) {
            adaptation = new HttpSessionImpl(session);
            session.setAdaptation(adaptation);
        }
        return adaptation;
    }

    // not supported outside of WAS ... return the same as adapt
    public Object adaptToAppSession(ISession session) {
        return adapt(session);
    }

    // XD methods
    public Object adapt(ISession session, Integer protocol) {
        Object adaptation = session.getAdaptation(protocol);
        if (null == adaptation) {
            adaptation = new HttpSessionImpl(session);
            session.setAdaptation(adaptation, protocol);
        }
        return adaptation;
    }

    public Object adapt(ISession session, Integer protocol, ServletRequest request, ServletContext context) {
        return adapt(session, protocol);
    }

    public Object getCorrelator(ServletRequest request, Object session) {
        return null;
    }

    public Integer getProtocol(Object session) {
        return IProtocolAdapter.HTTP;
    }
    // end of XD methods

}