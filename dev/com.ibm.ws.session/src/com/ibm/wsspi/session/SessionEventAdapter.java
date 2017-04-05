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
 *  @(#) 1.1 SERV1/ws/code/web.session.core/src/com/ibm/wsspi/session/SessionEventAdapter.java, WASCC.web.session.core, WASX.SERV1, o0901.11 10/13/06 15:53:51 [1/9/09 15:01:34]
 *
 * @(#)file   SessionEventAdapter.java
 * @(#)version   1.1
 * @(#)date      10/13/06
 *
 *COPYRIGHT_END*************************************************************/
package com.ibm.wsspi.session;

/**
 * This class provides a dummy implementation of the ISessionObserver interface.
 * By subclassing this class, an observer can do away with having to provide
 * implementations of
 * all of these methods. <br>
 * 
 */
public class SessionEventAdapter implements ISessionObserver {

    /**
     * Invoked when a session is created.
     * Default implementation is an empty method
     * <p>
     * 
     * @param ISession
     *            session that was created.
     * @see com.ibm.wsspi.session.ISessionObserver#sessionCreated(com.ibm.wsspi.session.ISession)
     */
    public void sessionCreated(ISession session) {

    }

    /**
     * Invoked when a session is accessed.
     * Default implementation is an empty method
     * <p>
     * 
     * @param ISession
     *            session that was accessed
     * @see com.ibm.wsspi.session.ISessionObserver#sessionAccessed(com.ibm.wsspi.session.ISession)
     */
    public void sessionAccessed(ISession session) {

    }

    /**
     * Invoked when a session is destroyed.
     * Default implementation is an empty method
     * <p>
     * 
     * @param ISession
     *            session that was destroyed
     * @see com.ibm.wsspi.session.ISessionObserver#sessionDestroyed(com.ibm.wsspi.session.ISession)
     */
    public void sessionDestroyed(ISession session) {

    }

    /**
     * Invoked when a session is released i.e. when a request that is
     * accessing a session completes..
     * Default implementation is an empty method
     * <p>
     * 
     * @param ISession
     *            session that was released.
     * @see com.ibm.wsspi.session.ISessionObserver#sessionReleased(com.ibm.wsspi.session.ISession)
     */
    public void sessionReleased(ISession session) {

    }

    /**
     * Invoked after a session is flushed out to the persistent store.
     * Default implementation is an empty method
     * <p>
     * 
     * @param ISession
     *            session that was flushed.
     * @see com.ibm.wsspi.session.ISessionObserver#sessionFlushed(com.ibm.wsspi.session.ISession)
     */
    public void sessionFlushed(ISession session) {

    }

    /**
     * Invoked when a session is activated in memory from the persistent store..
     * Default implementation is an empty method
     * <p>
     * 
     * @param ISession
     *            session that was activated.
     * @see com.ibm.wsspi.session.ISessionObserver#sessionDidActivate(com.ibm.wsspi.session.ISession)
     */
    public void sessionDidActivate(ISession session) {

    }

    /**
     * Invoked just before a session is flushed to persistent store.
     * Default implementation is an empty method
     * <p>
     * 
     * @param ISession
     *            session that will be passivated..
     * @see com.ibm.wsspi.session.ISessionObserver#sessionWillPassivate(com.ibm.wsspi.session.ISession)
     */
    public void sessionWillPassivate(ISession session) {

    }

    /**
     * Returns a unique name for this sessionEvent Adapter.
     * Default implementation returns the value null.
     * <p>
     * 
     * @return String id of the adapter.
     * @see com.ibm.wsspi.session.ISessionObserver#getId()
     */
    public String getId() {
        return null;
    }

    /**
     * Invoked when a session is invalidated due to the inactivity timeout being
     * fired.
     * The default implementation is an empty method.
     * 
     * @param ISession
     *            session that will be invalidated.
     * @see com.ibm.wsspi.session.ISessionObserver#sessionDestroyedByTimeout(com.ibm.wsspi.session.ISession)
     */
    public void sessionDestroyedByTimeout(ISession session) {}

    /**
     * Invoked when a session with an unknown id is accessed for the first time.
     * This
     * callback is especially useful for adapters that maintain session
     * statistics.
     * The default implementation is an empty method.
     * 
     * @param Object
     *            id of the unknown session.
     * @see com.ibm.wsspi.session.ISessionObserver#sessionAccessUnknownKey(java.lang.Object)
     */
    public void sessionAccessUnknownKey(Object key) {}

    /**
     * Invoked when a session access reveals that a session whose default
     * affinity is broken has been accessed on the local server jvm.
     * 
     * @param ISession
     *            session whose affinity was found to have been broken.
     * @see com.ibm.wsspi.session.ISessionObserver#sessionAffinityBroke(com.ibm.wsspi.session.ISession)
     */
    public void sessionAffinityBroke(ISession session) {

    }

    /**
     * Invoked when a session is discarded from the cache.
     * 
     * @param Object
     *            value of the discarded session.
     * @see com.ibm.wsspi.session.ISessionObserver#sessionCacheDiscard(java.lang.Object)
     */
    public void sessionCacheDiscard(Object value) {

    }

    /**
     * Invoked when a session is added to the in-memory cache
     * 
     * @param Object
     *            session object
     * @see com.ibm.wsspi.session.ISessionObserver#sessionLiveCountInc(java.lang.Object)
     */
    public void sessionLiveCountInc(Object value) {

    }

    /**
     * SessionLiveCountDec?
     * Invoked when a session is removed from the in-memory cache
     * 
     * @param Object
     *            session object
     * @see com.ibm.wsspi.session.ISessionObserver#sessionLiveCountDec(java.lang.Object)
     */
    public void sessionLiveCountDec(Object value) {

    }
    
    /* (non-Javadoc)
     * @see com.ibm.wsspi.session.ISessionObserver#sessionIdChanged(java.lang.String, com.ibm.wsspi.session.ISession)
     */
    @Override
    public void sessionIdChanged(String oldId, ISession session) {
        // do nothing, needed to satisfy interface ISessionObserver
    }
}
