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
 * Tag          Person   	Defect/Feature      Comments
 * ----------   ------   	--------------      --------------------------------------------------
 *				ankit_jain		92798			Change the NLS formatting method for exception message
 */
package com.ibm.ws.security.wim.env.was;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.naming.Context;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.websphere.security.wim.ConfigConstants;
import com.ibm.websphere.security.wim.ras.WIMMessageHelper;
import com.ibm.websphere.security.wim.ras.WIMMessageKey;
import com.ibm.websphere.ssl.JSSEHelper;
import com.ibm.websphere.ssl.SSLException;
import com.ibm.ws.security.wim.env.ISSLUtil;
import com.ibm.wsspi.security.wim.exception.WIMException;
import com.ibm.wsspi.security.wim.exception.WIMSystemException;

@Trivial
public class SSLUtilImpl implements ISSLUtil {

	/**
     * Register the class to trace service.
     */
    private final static TraceComponent tc = Tr.register(SSLUtilImpl.class);

	@Override
	public Properties getSSLPropertiesOnThread() {
        return JSSEHelper.getInstance().getSSLPropertiesOnThread();
	}

	@Override
	public void resetSSLAlias() {
        JSSEHelper.getInstance().setSSLPropertiesOnThread(null);
	}

	@Override
	public void setSSLAlias(String sslAlias, Hashtable<?, ?> ldapEnv) throws WIMException {
        final String METHODNAME = "setSSLAlias";
        
        try {
            Map<String, Object> connectionInfo = null;
            Properties props;
            String provider = (String) ldapEnv.get(Context.PROVIDER_URL);
            
            if (provider != null) {
                // Get the first URL
                StringTokenizer providerTokens = new StringTokenizer(provider);
                provider = providerTokens.nextToken();
                
                // Create a URL to extract host-name and port
                provider = provider.replaceFirst("ldap", "http");
                URL providerURL = new URL(provider);
                
                // Set out bound connection info
                connectionInfo = new HashMap<String, Object>();
                connectionInfo.put(com.ibm.websphere.ssl.JSSEHelper.CONNECTION_INFO_DIRECTION, com.ibm.websphere.ssl.JSSEHelper.DIRECTION_OUTBOUND);
                connectionInfo.put(com.ibm.websphere.ssl.JSSEHelper.CONNECTION_INFO_REMOTE_HOST, providerURL.getHost());
                connectionInfo.put(com.ibm.websphere.ssl.JSSEHelper.CONNECTION_INFO_REMOTE_PORT, providerURL.getPort() == -1 ? "636" : Integer.toString(providerURL.getPort()));
            }
            
            if (connectionInfo != null)
                props = JSSEHelper.getInstance().getProperties(sslAlias, connectionInfo, null);
            else
                props = JSSEHelper.getInstance().getProperties(sslAlias);
                
            // Set properties to thread
            JSSEHelper.getInstance().setSSLPropertiesOnThread(props);
            
            if(tc.isDebugEnabled())
                Tr.debug(tc, METHODNAME + " Properties for SSL Alias '" + sslAlias + "':" + props);
        }
        catch (SSLException e) {
            throw new WIMSystemException(WIMMessageKey.INVALID_INIT_PROPERTY, 
                    Tr.formatMessage(
                    tc,
                    WIMMessageKey.INVALID_INIT_PROPERTY,
                    WIMMessageHelper.generateMsgParms(ConfigConstants.CONFIG_PROP_SSL_CONFIGURATION)
                    ));
        } catch (MalformedURLException e) {
            throw new WIMSystemException(WIMMessageKey.INVALID_INIT_PROPERTY, 
                    Tr.formatMessage(
                    tc,
                    WIMMessageKey.INVALID_INIT_PROPERTY,
                    WIMMessageHelper.generateMsgParms((String) ldapEnv.get(Context.PROVIDER_URL))
                    ));
		}
	}

	@Override
	public void setSSLPropertiesOnThread(Properties props) {
        JSSEHelper.getInstance().setSSLPropertiesOnThread(props);
	}
}
