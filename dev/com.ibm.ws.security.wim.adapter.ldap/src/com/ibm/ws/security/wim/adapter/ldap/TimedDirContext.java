/*******************************************************************************
 * Copyright (c) 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.security.wim.adapter.ldap;

import java.util.Hashtable;

import javax.naming.NamingException;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;

import com.ibm.websphere.ras.annotation.Sensitive;

/**
 * The DirContext which contain the time stamp information.
 */
public class TimedDirContext extends InitialLdapContext {
    private long iCreateTimestamp;
    private long iPoolTimestamp;

    /**
     * @throws javax.naming.NamingException
     */
    public TimedDirContext() throws NamingException {
        super();
    }

    /**
     * @param environment
     * @param connCtls
     * @throws javax.naming.NamingException
     */
    @Sensitive
    public TimedDirContext(@Sensitive Hashtable<?, ?> environment, Control[] connCtls) throws NamingException {
        super(environment, connCtls);
        iCreateTimestamp = System.currentTimeMillis() / 1000;
        iPoolTimestamp = iCreateTimestamp;
    }

    @Sensitive
    public TimedDirContext(@Sensitive Hashtable<?, ?> environment, Control[] connCtls, long createTimestamp) throws NamingException {
        super(environment, connCtls);
        iCreateTimestamp = createTimestamp;
        iPoolTimestamp = createTimestamp;
    }

    public long getCreateTimestamp() {
        return iCreateTimestamp;
    }

    public long getPoolTimestamp() {
        return iPoolTimestamp;
    }

    public void setPoolTimeStamp(long poolTimestamp) {
        iPoolTimestamp = poolTimestamp;
    }

    public void setCreateTimestamp(long createTimestamp) {
        iCreateTimestamp = createTimestamp;
    }
}
