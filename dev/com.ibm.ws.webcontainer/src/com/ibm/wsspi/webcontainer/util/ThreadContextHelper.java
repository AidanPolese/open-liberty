// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2007
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

package com.ibm.wsspi.webcontainer.util;

import java.security.AccessController;
import java.security.PrivilegedAction;
import com.ibm.ws.util.ThreadContextAccessor;
import com.ibm.wsspi.webcontainer.WebContainer;

// Unprivileged code must not be given visibility to this class.
@SuppressWarnings("unchecked")
public class ThreadContextHelper {
	
	static final ThreadContextAccessor contextAccessor = (ThreadContextAccessor) AccessController.doPrivileged(new PrivilegedAction() {
		public Object run() {
			return ThreadContextAccessor.getThreadContextAccessor();
		}
	});

	public static Object setClassLoader(final ClassLoader cl) {		
		if (contextAccessor.isPrivileged()) {
			contextAccessor.setContextClassLoader(Thread.currentThread(), cl);
		} else {
			AccessController.doPrivileged(new PrivilegedAction() {
				public Object run() {
					contextAccessor.setContextClassLoader(Thread.currentThread(), cl);
					return null;
				}
			});
		}

		return null;
	}
	
	public static ClassLoader getContextClassLoader() {		
		if (contextAccessor.isPrivileged()) {
			return contextAccessor.getContextClassLoader(Thread.currentThread());
		}

		return (ClassLoader) AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				return contextAccessor.getContextClassLoader(Thread.currentThread());
			}
		});
	}
	
	public static ClassLoader getExtClassLoader(){
		WebContainer webContainer = WebContainer.getWebContainer();
		if (webContainer==null)
			return null;
		else
			return webContainer.getExtClassLoader();
	}
}
