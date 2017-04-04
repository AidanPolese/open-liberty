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
 *  @(#) 1.3 SERV1/ws/code/web.session.shell/src/com/ibm/ws/session/SessionApplicationParameters.java, WASCC.web.session, WASX.SERV1, o0901.11 10/1/07 16:36:11 [1/9/09 15:00:59]
 *
 * @(#)file   SessionApplicationParameters.java
 * @(#)version   1.3
 * @(#)date      10/1/07
 *
 *COPYRIGHT_END*************************************************************/
package com.ibm.ws.session;

import java.util.EnumSet;

import javax.servlet.ServletContext;
import javax.servlet.SessionTrackingMode;

public class SessionApplicationParameters {

    private String sapAppName = null;
    private long sapSessionTimeout = 0;
    private SessionCookieConfigImpl sapSessionCookieConfig = null;
    private EnumSet<SessionTrackingMode> sapSessionTrackingMode = null;
    private final boolean sapDistributableWebApp;
    private final boolean sapAllowDispatchRemoteInclude;
    private final ServletContext sapServletContext;
    private String _J2EEName = null;
    private final ClassLoader _appClassLoader;
    private boolean hasApplicationSession = false;
    private String applicationSessionName = null;
    private boolean sessionConfigOverridden = false;

    public SessionApplicationParameters(String appName,
                                        boolean session_timeout_set,
                                        long session_timeout,
                                        boolean distributableWebApp,
                                        boolean allowDispatchRemoteInclude,
                                        ServletContext sc,
                                        ClassLoader appClassLoader,
                                        String j2eeName,
                                        SessionCookieConfigImpl cookieConfig,
                                        boolean moduleSessionTrackingModeSet,
                                        EnumSet<SessionTrackingMode> sessionTrackingMode) {
        sapAppName = appName;
        _J2EEName = j2eeName;

        //need to allow the session timeout to function as it did in v7.0 as that was previously supported by the specification
        if (!(session_timeout_set)) {
            sapSessionTimeout = 0;
        } else if (session_timeout > 0) {
            sapSessionTimeout = session_timeout * 60;
        } else {
            sapSessionTimeout = -1;
        }

        //this happens before the creation of the new SessionContext, so the cookieConfig received is only what is within the web.xml
        //this is stored in the sapSessionCookieConfig so as to update the SessionManagerConfig's values after we get the SMC 
        if ((sapSessionCookieConfig = cookieConfig) != null) {
            sessionConfigOverridden = true;
        }
        if (moduleSessionTrackingModeSet) {
            sessionConfigOverridden = true;
            sapSessionTrackingMode = sessionTrackingMode;
        }
        sapDistributableWebApp = distributableWebApp;
        sapAllowDispatchRemoteInclude = allowDispatchRemoteInclude;
        sapServletContext = sc;
        _appClassLoader = appClassLoader;
    }

    //Liberty - Used by extensions to set the sessionTimeout
    public void setSapSessionTimeout(long sapSessionTimeout) {
        this.sapSessionTimeout = sapSessionTimeout;
    }

    public String getAppName() {
        return sapAppName;
    }

    public long getSessionTimeout() {
        return sapSessionTimeout;
    }

    SessionCookieConfigImpl getSessionCookieConfig() {
        return sapSessionCookieConfig;
    }

    EnumSet<SessionTrackingMode> getSessionTrackingModes() {
        return sapSessionTrackingMode;
    }

    boolean getDistributableWebApp() {
        return sapDistributableWebApp;
    }

    public boolean getAllowDispatchRemoteInclude() {
        return sapAllowDispatchRemoteInclude;
    }

    public ServletContext getServletContext() {
        return sapServletContext;
    }

    public String getJ2EEName() {
        return _J2EEName;
    }

    public ClassLoader getAppClassLoader() {
        return _appClassLoader;
    }

    // for application Sessions
    public void setHasApplicationSession(boolean b) {
        hasApplicationSession = b;
    }

    public boolean getHasApplicationSession() {
        return hasApplicationSession;
    }

    public void setApplicationSessionName(String s) {
        applicationSessionName = s;
    }

    public String getApplicationSessionName() {
        return applicationSessionName;
    }

    public boolean isSessionConfigOverridden() {
        return sessionConfigOverridden;
    }
}
