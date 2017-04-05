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
 *  @(#) 1.8 SERV1/ws/code/web.session.core/src/com/ibm/ws/session/http/HttpSessionAttributeObserver.java, WASCC.web.session.core, WASX.SERV1, o0901.11 5/30/08 16:34:55 [1/9/09 15:01:27]
 *
 * @(#)file   HttpSessionAttributeObserver.java
 * @(#)version   1.8
 * @(#)date      5/30/08
 *
 *COPYRIGHT_END*************************************************************/

package com.ibm.ws.session.http;

import java.util.ArrayList;
import java.util.logging.Level;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import com.ibm.ws.session.utils.LoggingUtil;
import com.ibm.wsspi.session.IProtocolAdapter;
import com.ibm.wsspi.session.ISession;
import com.ibm.wsspi.session.ISessionStateObserver;

/**
 * This implementation of the ISessionStateObserver drives the
 * sessionAttributeListener invocation
 * mechanism by registering for callbacks with the base session manager.
 */
public class HttpSessionAttributeObserver implements ISessionStateObserver {

    // ----------------------------------------
    // Private Members
    // ----------------------------------------

    /*
     * For logging the CMVC file version once.
     */
    private static boolean _loggedVersion = false;

    /*
     * A reference to the parent HttpSessionManagers arrayList of
     * sessionAttributeListeners.
     */
    protected ArrayList _sessionAttributeListeners;

    /*
     * A reference to the _adapter object that is used by the parent Http session
     * manager
     * to transform an IManagedSession object to a protocol specific session
     * object.
     */
    protected IProtocolAdapter _adapter = null;

    private static final String methodClassName = "HttpSessionAttributeObserver";

    // ----------------------------------------
    // Public Constructor
    // ----------------------------------------
    public HttpSessionAttributeObserver(ArrayList listeners, IProtocolAdapter adapter) {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && LoggingUtil.SESSION_LOGGER_CORE.isLoggable(Level.FINE)) {
            if (!_loggedVersion) {
                LoggingUtil.SESSION_LOGGER_CORE.logp(Level.FINE, methodClassName, "", "CMVC Version 1.8 5/30/08 16:34:55");
                _loggedVersion = true;
            }
        }
        _sessionAttributeListeners = listeners;
        _adapter = adapter;
    }

    // ----------------------------------------
    // Public Methods
    // ----------------------------------------

    // XD methods
    public void sessionAttributeSet(ISession session, Object name, Object oldValue, Object newValue) {
        // just pass false for listeners
        sessionAttributeSet(session, name, oldValue, false, newValue, false);
    }

    public void sessionAttributeRemoved(ISession session, Object name, Object value) {
        sessionAttributeRemoved(session, name, value, false);
    }

    // end of XD methods
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
        if ((oldValue != null) && (oldIsListener.booleanValue())) {
            replaceEvent = new HttpSessionBindingEvent(httpsession, (String) name, oldValue);
            // if (oldValue instanceof HttpSessionBindingListener)
            ((HttpSessionBindingListener) oldValue).valueUnbound(replaceEvent);
        }
        if ((newValue != null) && (newIsListener.booleanValue())) { // (newValue instanceof HttpSessionBindingListener) ) {
            addEvent = new HttpSessionBindingEvent(httpsession, (String) name, newValue);
            ((HttpSessionBindingListener) newValue).valueBound(addEvent);
        }
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
        if (value != null && oldIsBindingListener.booleanValue()) {
            ((HttpSessionBindingListener) value).valueUnbound(event);
        }
        for (int i = 0; i < _sessionAttributeListeners.size(); i++) {
            listener = (HttpSessionAttributeListener) _sessionAttributeListeners.get(i);
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && LoggingUtil.SESSION_LOGGER_CORE.isLoggable(Level.FINE)) {
                LoggingUtil.SESSION_LOGGER_CORE.logp(Level.FINE, methodClassName, "sessionAttributeRemoved", "SessionManager calling attributeRemoved on listener:" + listener);
            }
            listener.attributeRemoved(event);
        }

    }

    /**
     * Method sessionAttributeAccessed
     * <p>
     * 
     * @see com.ibm.wsspi.session.ISessionStateObserver#sessionAttributeAccessed(com.ibm.wsspi.session.ISession, java.lang.Object, java.lang.Object)
     */
    public void sessionAttributeAccessed(ISession session, Object key, Object value) {

    }

    /**
     * Method sessionUserNameSet
     * <p>
     * 
     * @see com.ibm.wsspi.session.ISessionStateObserver#sessionUserNameSet(com.ibm.wsspi.session.ISession, java.lang.String, java.lang.String)
     */
    public void sessionUserNameSet(ISession session, String oldUserName, String newUserName) {

    }

    /**
     * Method sessionLastAccessTimeSet
     * <p>
     * 
     * @see com.ibm.wsspi.session.ISessionStateObserver#sessionLastAccessTimeSet(com.ibm.wsspi.session.ISession, long, long)
     */
    public void sessionLastAccessTimeSet(ISession session, long old, long newaccess) {

    }

    /**
     * Method sessionMaxInactiveTimeSet
     * <p>
     * 
     * @see com.ibm.wsspi.session.ISessionStateObserver#sessionMaxInactiveTimeSet(com.ibm.wsspi.session.ISession, int, int)
     */
    public void sessionMaxInactiveTimeSet(ISession session, int old, int newval) {

    }

    /**
     * Method sessionExpiryTimeSet
     * <p>
     * 
     * @see com.ibm.wsspi.session.ISessionStateObserver#sessionExpiryTimeSet(com.ibm.wsspi.session.ISession, long, long)
     */
    public void sessionExpiryTimeSet(ISession session, long old, long newone) {

    }

    /**
     * Method getId
     * <p>
     * 
     * @see com.ibm.wsspi.session.ISessionStateObserver#getId()
     */
    public String getId() {
        return "HttpSessionAttributeObserver";
    }

}
