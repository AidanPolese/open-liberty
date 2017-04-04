/*
 * IBM Confidential OCO Source Material
 * 5724-I63, 5724-H88, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2005
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * 
 * @(#) 1.5 SERV1/ws/code/security.crypto/src/com/ibm/ws/ssl/provider/IBMJSSEProvider.java, WAS.security.crypto, WASX.SERV1, pp0919.25 10/18/05 08:41:17 [5/15/09 18:04:38]
 *
 * Date         Defect        CMVC ID    Description
 *
 * 08/20/03     LIDB2905.21   pbirk      Dynamic JSSE provider selection
 * 09/19/03     176876        pbirk      HP platform does not sync.
 * 08/19/05     LIDB3557-1.1  pbirk      3557 Initial Code Drop
 *
 */

package com.ibm.ws.ssl.provider;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.ssl.Constants;
import com.ibm.websphere.ssl.JSSEProvider;
import com.ibm.ws.ssl.JSSEProviderFactory;

/**
 * JSSE provider for an IBM JDK.
 * <p>
 * This is the old IBMJSSE JSSEProvider implementation. This currently assumes
 * IBMJSSE2 is the replacement.
 * </p>
 * 
 * @author IBM Corporation
 * @version WAS 7.0
 * @since WAS 7.0
 */
public class IBMJSSEProvider extends AbstractJSSEProvider implements JSSEProvider {
    private static TraceComponent tc = Tr.register(IBMJSSEProvider.class, "SSL", "com.ibm.ws.ssl.resources.ssl");

    /**
     * Constructor.
     */
    public IBMJSSEProvider() {
        super();
        initialize(JSSEProviderFactory.getKeyManagerFactoryAlgorithm(), JSSEProviderFactory.getTrustManagerFactoryAlgorithm(), Constants.IBMJSSE2_NAME, null,
                   Constants.SOCKET_FACTORY_WAS_DEFAULT, null, Constants.PROTOCOL_SSL_TLS_V2);
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "Created an IBM JSSE provider");
        }
    }

}
