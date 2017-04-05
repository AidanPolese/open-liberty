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
 *  @(#) 1.2 SERV1/ws/code/web.session.shell/src/com/ibm/ws/session/WasHttpAppSessionAttributeObserver.java, WASCC.web.session, WASX.SERV1, o0901.11 3/12/08 09:24:54 [1/9/09 15:01:00]
 *
 * @(#)file   WasHttpSessionAttributeObserver.java
 * @(#)version   1.2
 * @(#)date      3/12/08
 *
 *COPYRIGHT_END*************************************************************/

package com.ibm.ws.session;

import java.util.ArrayList;
import java.util.logging.Level;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

import com.ibm.ws.session.http.HttpSessionAttributeObserver;
import com.ibm.ws.session.utils.LoggingUtil;
import com.ibm.wsspi.session.IProtocolAdapter;
import com.ibm.wsspi.session.ISession;

/**
 * This implementation of the ISessionStateObserver drives the
 * sessionAttributeListener invocation
 * mechanism by registering for callbacks with the base session manager.
 */
public class WasHttpAppSessionAttributeObserver extends HttpSessionAttributeObserver {

    private static final String methodClassName = "WasHttpAppSessionAttributeObserver";

    // ----------------------------------------
    // Public Constructor
    // ----------------------------------------
    public WasHttpAppSessionAttributeObserver(ArrayList listeners, IProtocolAdapter adapter) {
        super(listeners, adapter);
    }

    // ----------------------------------------
    // Public Methods
    // ----------------------------------------

    /**
     * Method sessionAttributeSet
     * <p>
     * 
     * @see com.ibm.wsspi.session.ISessionStateObserver#sessionAttributeSet(com.ibm.wsspi.session.ISession, java.lang.Object, java.lang.Object, java.lang.Object)
     */
    public void sessionAttributeSet(ISession session, Object name, Object oldValue, Boolean oldIsListener, Object newValue, Boolean newIsListener) {

        HttpSession httpsession = (HttpSession) _adapter.adapt(session);
        HttpSessionBindingEvent addEvent = null; // only init if necessary
        HttpSessionBindingEvent replaceEvent = null; // only init this if
                                                     // necessary..done below

        // do binding listeners first to be consistent with v6.1
        /*
         * Binding listeners will be handled with the first Normal Observer
         * if ((oldValue != null) && (oldIsListener.booleanValue())) {
         * replaceEvent = new HttpSessionBindingEvent(httpsession, (String)name,
         * oldValue);
         * // if (oldValue instanceof HttpSessionBindingListener)
         * ((HttpSessionBindingListener)oldValue).valueUnbound(replaceEvent);
         * }
         * if ( (newValue != null) && (newIsListener.booleanValue())) { //(newValue
         * instanceof HttpSessionBindingListener) ) {
         * addEvent = new HttpSessionBindingEvent(httpsession, (String)name,
         * newValue);
         * ((HttpSessionBindingListener)newValue).valueBound(addEvent);
         * }
         */
        // now do attribute listeners
        HttpSessionAttributeListener listener = null;
        for (int i = 0; i < _sessionAttributeListeners.size(); i++) {
            listener = (HttpSessionAttributeListener) _sessionAttributeListeners.get(i);
            if (oldValue != null) {
                if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && LoggingUtil.SESSION_LOGGER_CORE.isLoggable(Level.FINE)) {
                    LoggingUtil.SESSION_LOGGER_CORE.logp(Level.FINE, methodClassName, "sessionAttrSet", "Calling attributeReplace on listener:" + listener);
                }
                if (replaceEvent == null)
                    replaceEvent = new HttpSessionBindingEvent(httpsession, (String) name, oldValue);
                listener.attributeReplaced(replaceEvent);
            } else {
                if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && LoggingUtil.SESSION_LOGGER_CORE.isLoggable(Level.FINE)) {
                    LoggingUtil.SESSION_LOGGER_CORE.logp(Level.FINE, methodClassName, "sessionAttrSet", "Calling attributeCreated on listener:" + listener);
                }
                if (addEvent == null)
                    addEvent = new HttpSessionBindingEvent(httpsession, (String) name, newValue);
                listener.attributeAdded(addEvent);
            }
        }
    }

    /**
     * Method sessionAttributeRemoved
     * <p>
     * 
     * @see com.ibm.wsspi.session.ISessionStateObserver#sessionAttributeRemoved(com.ibm.wsspi.session.ISession, java.lang.Object, java.lang.Object)
     */
    public void sessionAttributeRemoved(ISession session, Object name, Object value, Boolean oldIsBindingListener) {
        HttpSessionAttributeListener listener = null;
        HttpSessionBindingEvent event = null;
        HttpSession httpsession = (HttpSession) _adapter.adapt(session);

        event = new HttpSessionBindingEvent(httpsession, (String) name, value);
        // if (value instanceof HttpSessionBindingListener) {
        /*
         * if (value!=null && oldIsBindingListener.booleanValue()) {
         * ((HttpSessionBindingListener)value).valueUnbound(event);
         * }
         */
        for (int i = 0; i < _sessionAttributeListeners.size(); i++) {
            listener = (HttpSessionAttributeListener) _sessionAttributeListeners.get(i);
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && LoggingUtil.SESSION_LOGGER_CORE.isLoggable(Level.FINE)) {
                LoggingUtil.SESSION_LOGGER_CORE.logp(Level.FINE, methodClassName, "sessionAttributeRemoved", "SessionManager calling attributeRemoved on listener:" + listener);
            }
            listener.attributeRemoved(event);
        }

    }

}
