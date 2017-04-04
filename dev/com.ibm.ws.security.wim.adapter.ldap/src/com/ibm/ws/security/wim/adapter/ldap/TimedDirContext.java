/************** Begin Copyright - Do not add comments here **************
 *
 *  
 * IBM Confidential OCO Source Material
 * 5724-H88, 5724-J08, 5724-I63, 5655-W65, 5724-H89, 5722-WE2   Copyright IBM Corp., 2012, 2013
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U. S. Copyright Office.
 * 
 * Change History:
 * 
 * Tag       Person      Defect/Feature      Comments
 * -------   ------      --------------      --------------------------------------------------
 */
package com.ibm.ws.security.wim.adapter.ldap;

import java.util.Hashtable;

import javax.naming.NamingException;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;

import com.ibm.websphere.ras.annotation.Sensitive;

/**
 * The DirContext which contain the time stamp information.
 */
public class TimedDirContext extends InitialLdapContext
{
    private long iCreateTimestamp;
    private long iPoolTimestamp;

    /**
     * @throws javax.naming.NamingException
     */
    public TimedDirContext() throws NamingException
    {
        super();
    }

    /**
     * @param environment
     * @param connCtls
     * @throws javax.naming.NamingException
     */
    @Sensitive
    public TimedDirContext(@Sensitive Hashtable<?, ?> environment, Control[] connCtls) throws NamingException
    {
        super(environment, connCtls);
        iCreateTimestamp = System.currentTimeMillis() / 1000;
        iPoolTimestamp = iCreateTimestamp;
    }

    @Sensitive
    public TimedDirContext(@Sensitive Hashtable<?, ?> environment, Control[] connCtls, long createTimestamp) throws NamingException
    {
        super(environment, connCtls);
        iCreateTimestamp = createTimestamp;
        iPoolTimestamp = createTimestamp;
    }

    public long getCreateTimestamp()
    {
        return iCreateTimestamp;
    }

    public long getPoolTimestamp()
    {
        return iPoolTimestamp;
    }

    public void setPoolTimeStamp(long poolTimestamp)
    {
        iPoolTimestamp = poolTimestamp;
    }

    public void setCreateTimestamp(long createTimestamp)
    {
        iCreateTimestamp = createTimestamp;
    }
}
