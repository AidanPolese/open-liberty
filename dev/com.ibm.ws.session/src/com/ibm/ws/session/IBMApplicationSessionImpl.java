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
 *  @(#) 1.5 SERV1/ws/code/web.session.shell/src/com/ibm/ws/session/IBMApplicationSessionImpl.java, WASCC.web.session, WASX.SERV1, o0901.11 9/5/08 12:57:24 [1/9/09 15:00:58]
 *
 * @(#)file   IBMApplicationSessionImpl.java
 * @(#)version   1.5
 * @(#)date      9/5/08
 *
 *COPYRIGHT_END*************************************************************/
package com.ibm.ws.session;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import javax.servlet.http.HttpSessionBindingListener;

import com.ibm.websphere.servlet.session.IBMApplicationSession;
import com.ibm.wsspi.session.ISession;

public class IBMApplicationSessionImpl implements IBMApplicationSession {
    // this class is only for HTTP shared application sessions
    ISession _iSess;

    Hashtable sessionManagersWithinApp = new Hashtable();

    public IBMApplicationSessionImpl(ISession iSess) {
        _iSess = iSess;
    }

    public void setSessionManagers(Hashtable ht) {
        sessionManagersWithinApp = ht;
    }

    public ISession getISession() {
        return _iSess;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ibm.websphere.servlet.session.IBMApplicationSession#encodeURI(java.
     * lang.Object)
     */
    public void encodeURI(Object URI) {
        // Only for SIP
        return;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ibm.websphere.servlet.session.IBMApplicationSession#encodeURI(java.
     * lang.String)
     */
    public String encodeURI(String URI) {
        // Only for SIP
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ibm.websphere.servlet.session.IBMApplicationSession#getAttribute(java
     * .lang.String)
     */
    public Object getAttribute(String name) {
        if (!_iSess.isValid())
            throw new IllegalStateException();
        Object value = null;
        if (_iSess != null) {
            value = _iSess.getAttribute(name);
        }
        return value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ibm.websphere.servlet.session.IBMApplicationSession#getAttributeNames()
     */
    public Iterator getAttributeNames() {
        if (!_iSess.isValid())
            throw new IllegalStateException();
        Iterator value = null;
        if (_iSess != null) {
            ArrayList list = new ArrayList();
            for (Enumeration e = _iSess.getAttributeNames(); e.hasMoreElements();) {
                list.add(e.nextElement());
            }
            value = list.iterator();
        }
        return value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ibm.websphere.servlet.session.IBMApplicationSession#getCreationTime()
     */
    public long getCreationTime() {
        if (!_iSess.isValid())
            throw new IllegalStateException();
        long value = 0;
        if (_iSess != null) {
            value = _iSess.getCreationTime();
        }
        return value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.websphere.servlet.session.IBMApplicationSession#getId()
     */
    public String getId() {
        String value = null;
        if (_iSess != null) {
            value = _iSess.getId();
        }
        return value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ibm.websphere.servlet.session.IBMApplicationSession#getLastAccessedTime
     * ()
     */
    public long getLastAccessedTime() {
        if (!_iSess.isValid())
            throw new IllegalStateException();
        long value = 0;
        if (_iSess != null) {
            value = _iSess.getLastAccessedTime();
        }
        return value;
    }

    public void updateLastAccessedTime(long laTime) {
        if (!_iSess.isValid())
            throw new IllegalStateException();
        if (_iSess != null) {
            _iSess.updateLastAccessTime(laTime);
        }
    }

    public void incrementRefCount() {
        if (_iSess != null) {
            _iSess.incrementRefCount();
        }
    }

    public void decrementRefCount() {
        if (_iSess != null) {
            _iSess.decrementRefCount();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.websphere.servlet.session.IBMApplicationSession#getSessions()
     */
    public Iterator getSessions() {
        return getSessions("HTTP");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ibm.websphere.servlet.session.IBMApplicationSession#getSessions(java
     * .lang.String)
     */
    public Iterator getSessions(String protocol) {
        Iterator value = null;
        if (protocol.equals("HTTP") && sessionManagersWithinApp != null) {
            ArrayList list = new ArrayList();
            for (Enumeration e = sessionManagersWithinApp.elements(); e.hasMoreElements();) {
                SessionManager sm = (SessionManager) e.nextElement();
                Object o = sm.getSession(_iSess.getId(), false);
                if (o != null) {
                    list.add(o);
                }
            }
            value = list.iterator();
        }
        return value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.websphere.servlet.session.IBMApplicationSession#getTimers()
     */
    public Collection getTimers() {
        // Only for SIP
        return null;
    }

    public boolean isValid() {
        boolean b = false;
        if (_iSess != null) {
            b = _iSess.isValid();
        }
        return b;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.websphere.servlet.session.IBMApplicationSession#invalidate()
     */
    public void invalidate() {
        if (!_iSess.isValid())
            throw new IllegalStateException();
        if (_iSess != null) {
            _iSess.invalidate();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ibm.websphere.servlet.session.IBMApplicationSession#removeAttribute
     * (java.lang.String)
     */
    public void removeAttribute(String name) {
        if (!_iSess.isValid())
            throw new IllegalStateException();
        if (_iSess != null) {
            _iSess.removeAttribute(name);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ibm.websphere.servlet.session.IBMApplicationSession#setAttribute(java
     * .lang.String, java.lang.Object)
     */
    public void setAttribute(String name, Object attribute) {
        if (!_iSess.isValid())
            throw new IllegalStateException();
        if (null != attribute) {
            if (!(attribute instanceof HttpSessionBindingListener)) {
                _iSess.setAttribute(name, attribute, Boolean.FALSE);
            } else {
                _iSess.setAttribute(name, attribute, Boolean.TRUE);
            }
        } else {
            // attribute is null
            // if name is not null, remove it ... otherwise do nothing
            if (name != null) {
                _iSess.removeAttribute(name);
            }
        }
    }

    public void setAttribute(String name, Object attribute, Boolean isListener) {
        if (!_iSess.isValid())
            throw new IllegalStateException();
        if (null != attribute) {
            if (_iSess != null) {
                _iSess.setAttribute(name, attribute, isListener);
            }
        } else {
            // attribute is null
            // if name is not null, remove it ... otherwise do nothing
            if (name != null) {
                _iSess.removeAttribute(name);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ibm.websphere.servlet.session.IBMApplicationSession#setExpires(int)
     */
    public int setExpires(int deltaMinutes) {
        // Only for SIP
        return 0;
    }

    public void sync() {
        // Only for SIP
    }
}
