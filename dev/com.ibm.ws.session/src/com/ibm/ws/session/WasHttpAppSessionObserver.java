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
 *  @(#) 1.3 SERV1/ws/code/web.session.shell/src/com/ibm/ws/session/WasHttpAppSessionObserver.java, WASCC.web.session, WASX.SERV1, uu1050.39 5/7/10 15:21:53 [12/22/10 17:06:13]
 *
 * @(#)file   WasHttpAppSessionObserver.java
 * @(#)version   1.3
 * @(#)date      5/7/10
 *
 *COPYRIGHT_END*************************************************************/
package com.ibm.ws.session;

import java.util.ArrayList;
import java.util.logging.Level;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.ibm.ws.session.http.HttpSessionImpl;
import com.ibm.ws.session.utils.LoggingUtil;
import com.ibm.wsspi.session.IProtocolAdapter;
import com.ibm.wsspi.session.ISession;

public class WasHttpAppSessionObserver extends WasHttpSessionObserver {

    // For logging.
    private static final String methodClassName = "WasHttpAppSessionObserver";

    /*
     * constructor
     */
    public WasHttpAppSessionObserver(ArrayList listeners, IProtocolAdapter adapter) {
        super(listeners, adapter);
    }
    
    // call the new super constructor on WasHttpSessionObserver
    public WasHttpAppSessionObserver(ArrayList listeners, ArrayList idListeners, IProtocolAdapter adapter) {
        super(listeners, idListeners, adapter);
    }

    public void sessionDestroyed(ISession session) {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && LoggingUtil.SESSION_LOGGER_CORE.isLoggable(Level.FINE)) {
            LoggingUtil.SESSION_LOGGER_CORE.entering(methodClassName, methodNames[SESSION_DESTROYED], "sessionid=" + session.getId());
        }

        ArrayList attributes = session.getListenerAttributeNames();
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && LoggingUtil.SESSION_LOGGER_CORE.isLoggable(Level.FINE)) {
            LoggingUtil.SESSION_LOGGER_CORE.logp(Level.FINE, methodClassName, methodNames[SESSION_DESTROYED], "attributes.size()=:" + attributes.size());
        }

        if (_sessionListeners.size() > 0 || attributes.size() > 0) {
            HttpSession httpsession = (HttpSessionImpl) _adapter.adapt(session);
            HttpSessionEvent event = new HttpSessionEvent(httpsession);
            HttpSessionListener listener = null;

            // synchronized (_sessionListeners) {
            for (int i = _sessionListeners.size() - 1; i >= 0; i--) {
                listener = (HttpSessionListener) _sessionListeners.get(i);
                if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && LoggingUtil.SESSION_LOGGER_CORE.isLoggable(Level.FINE)) {
                    LoggingUtil.SESSION_LOGGER_CORE.logp(Level.FINE, methodClassName, methodNames[SESSION_DESTROYED], "Calling sessionDestroyed on listener:" + listener);
                }
                listener.sessionDestroyed(event);
            }
            // }

            /*
             * if(attributes.size() != 0) {
             * Object value = null;
             * String name = null;
             * for(int i=0;i<attributes.size();i++ ) {
             * name = (String)attributes.get(i);
             * value = session.getAttribute(name);
             * if (null != value) {
             * // we know these are all binding listeners
             * HttpSessionBindingListener bindingListener =
             * (HttpSessionBindingListener) value;
             * HttpSessionBindingEvent bindingEvent = new
             * HttpSessionBindingEvent(httpsession, name);
             * bindingListener.valueUnbound(bindingEvent);
             * }
             * }
             * }
             */
        }

        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && LoggingUtil.SESSION_LOGGER_CORE.isLoggable(Level.FINE)) {
            LoggingUtil.SESSION_LOGGER_CORE.exiting(methodClassName, methodNames[SESSION_DESTROYED]);
        }

    }

    /**
     * Method sessionDidActivate
     * <p>
     * 
     * @see com.ibm.wsspi.session.ISessionObserver#sessionDidActivate(com.ibm.wsspi.session.ISession)
     */
    public void sessionDidActivate(ISession session) {
        /*
         * HttpSession httpsession = (HttpSessionImpl)_adapter.adapt(session);
         * HttpSessionEvent event = new HttpSessionEvent(httpsession);
         * 
         * Enumeration enum1 = session.getAttributeNames();
         * String attrName;
         * Object attr;
         * while (enum1.hasMoreElements()) {
         * attrName = (String)enum1.nextElement();
         * attr = session.getAttribute(attrName);
         * if (attr instanceof HttpSessionActivationListener) {
         * ((HttpSessionActivationListener)attr).sessionDidActivate(event);
         * }
         * }
         */
    }

    /**
     * Method sessionWillPassivate
     * <p>
     * 
     * @see com.ibm.wsspi.session.ISessionObserver#sessionWillPassivate(com.ibm.wsspi.session.ISession)
     */
    public void sessionWillPassivate(ISession session) {
        /*
         * HttpSession httpsession = (HttpSessionImpl)_adapter.adapt(session);
         * HttpSessionEvent event = new HttpSessionEvent(httpsession);
         * 
         * Enumeration enum1 = session.getAttributeNames();
         * String attrName;
         * Object attr;
         * while (enum1.hasMoreElements()) {
         * attrName = (String)enum1.nextElement();
         * attr = session.getAttribute(attrName);
         * if (attr instanceof HttpSessionActivationListener) {
         * ((HttpSessionActivationListener)attr).sessionWillPassivate(event);
         * }
         * }
         */
    }
}
