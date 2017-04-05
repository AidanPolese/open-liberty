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
 *  @(#) 1.1 WEB/ws/code/web.session.core/src/com/ibm/wsspi/session/ISessionManagerCustomizer.java, WASCC.web.session.core, CCX.WEB, nn0720.02 10/13/06 15:53:27 [5/31/07 17:29:46]
 *
 * @(#)file   ISessionManagerCustomizer.java
 * @(#)version   1.1
 * @(#)date      10/13/06
 *
 *COPYRIGHT_END*************************************************************/
package com.ibm.wsspi.session;

import java.util.ArrayList;
import java.util.Properties;

import javax.management.ObjectName;
import javax.servlet.ServletContext;

import com.ibm.ws.session.utils.IIDGenerator;

/**
 * The SessionManagerCustomizer for a class allows the user to customize the
 * various aspects of a session manager, such as the ID Generator being used,
 * the affinity manager, the store etc. This ability to customize is what makes
 * the session manager flexible with pluggable hooks.
 * 
 * @author Aditya Desai
 * 
 */
public interface ISessionManagerCustomizer {

    // XD methods
    public static final String SESSION_ADAPTER_OVERRIDE_KEY = "overrideAdapter";
    public static final String SESSION_ADAPTER_PROTOCOL_KEY = "protocolKey";
    public static final String EXT_OG_SESSION_ADAPTER_CLASS_NAME = "com.ibm.ws.httpsession.IBMSessionExtAdapter";
    public static final String BASE_OG_SESSION_ADAPTER_CLASS_NAME = "com.ibm.ws.httpsession.OGHttpSessionAdapter";

    public void createProtocolAdapter(Properties props);

    /**
     * This method allows the component initializing the session manager to
     * register a list of listeners corresponding to those specified in the
     * web.xml.
     * 
     * @param Classloader
     *            classloader to use while loading the listeners.
     * @param ArrayList
     *            list containing String names of the classes corresponding to the
     *            listener objects.
     */
    public void registerListeners(ClassLoader classloader, ArrayList list);

    // end of XD methods

    // ----------------------------------------
    // Public Methods
    // ----------------------------------------

    /**
     * Set or modify the string name for this session Manager instance.
     * 
     * @param String
     *            sessionManagerID
     */
    public void setID(String sessionManagerID);

    /**
     * Register a session observer on this session manager that can
     * listen for various Session related events.
     * <p>
     * 
     * @param ISessionObserver
     *            observer
     * @see com.ibm.wsspi.session.ISessionObserver
     */
    public void registerSessionObserver(ISessionObserver observer);

    /**
     * Register a session state observer on this session manager that can listen
     * to various session state modification events.
     * <p>
     * 
     * @param ISessionStateObserver
     *            observer
     * @see com.ibm.wsspi.session.ISessionStateObserver
     */
    public void registerSessionStateObserver(ISessionStateObserver observer);

    /**
     * Register a store that can hold sessions either in memory or also
     * persist those to permanent storage media.
     * <p>
     * 
     * @param IStore
     *            store
     * @see com.ibm.wsspi.session.IStore
     */
    public void registerStore(IStore store);

    /**
     * Register a storer that can control the frequency with which sessions
     * are flushed out from memory to persistent storage.
     * <p>
     * 
     * @param IStorer
     *            storer
     * @see com.ibm.wsspi.session.IStorer
     */
    public void registerStorer(IStorer storer);

    /**
     * Register an ID Generator. The default implementation creates ids
     * that are 23 characters in length, as required by the base HTTP Session
     * manager in WebSphere.
     * <p>
     * 
     * @param IIDGenerator
     * @see com.ibm.wsspi.session.IIDGenerator
     */
    public void setIDGenerator(IIDGenerator IDGenerator);

    /**
     * This method sets the servletContext for this web application. This
     * information is passed to the Session manager by the component
     * initializing the session manager.
     * 
     * @param IServletContext
     *            context
     */
    public void setServletContext(ServletContext context);

    /**
     * Mutator for the session Timeout
     * <p>
     * 
     * @param int i
     */
    public void setSessionTimeout(int i);

    /**
     * Mutator for the sharing across web modules
     * state of the session manager.
     * <p>
     * 
     * @param boolean share
     */
    public void setSharedAcrossWebApps(boolean share);

    /**
     * Register a session affinity manager that will be capable of
     * examining the incoming request and stripping out affinity related
     * information from it, and then priming a SessionAffinityContext object
     * with this information.
     * <p>
     * 
     * @param ISessionAffinityManager
     * @see com.ibm.wsspi.session.ISessionAffinityManager
     */
    public void registerAffinityManager(ISessionAffinityManager manager);

    /**
     * Allows the creator to register the objectName of the stats MBean object
     * associated with this Session Manager
     * 
     * @param moduleObjectName
     */
    public void registerStatsModuleObjectName(ObjectName moduleObjectName);

    /**
     * Sets the protocol adapter into the session manager
     * 
     * @param adapter
     */
    public void setAdapter(IProtocolAdapter adapter);

    public IProtocolAdapter getAdapter();

}