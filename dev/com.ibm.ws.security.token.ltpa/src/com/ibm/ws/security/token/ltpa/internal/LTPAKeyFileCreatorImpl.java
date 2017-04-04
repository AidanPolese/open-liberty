/*
* IBM Confidential
*
* OCO Source Materials
*
* WLP Copyright IBM Corp. 2016
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.ws.security.token.ltpa.internal;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.ras.annotation.Sensitive;
import com.ibm.websphere.security.UserRegistry;
import com.ibm.ws.crypto.ltpakeyutil.LTPAKeyFileUtilityImpl;
import com.ibm.wsspi.kernel.service.location.WsLocationAdmin;
import com.ibm.wsspi.kernel.service.location.WsResource;
import com.ibm.wsspi.kernel.service.location.WsResource.Type;
import com.ibm.wsspi.kernel.service.utils.FileUtils;
import com.ibm.wsspi.security.registry.RegistryHelper;

/**
 *
 */
public class LTPAKeyFileCreatorImpl extends LTPAKeyFileUtilityImpl implements LTPAKeyFileCreator {

    private static final TraceComponent tc = Tr.register(LTPAKeyFileCreatorImpl.class);

    /**
     * Obtains the realm name of the configured UserRegistry, if one is available.
     *
     * @return The configured realm name, or "defaultRealm" if no UserRegistry is present
     */
    private String getRealmName() {
        String realm = "defaultRealm";
        try {
            UserRegistry ur = RegistryHelper.getUserRegistry(null);
            if (ur != null) {
                String r = ur.getRealm();
                if (r != null) {
                    realm = r;
                }
            }
        } catch (Exception ex) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "Cannot get the UR service since it may not be available so use the default value for the realm.", ex);
            }
        }

        return realm;
    }

    /** {@inheritDoc} */
    @Override
    public Properties createLTPAKeysFile(WsLocationAdmin locService, String keyFile, @Sensitive byte[] keyPasswordBytes) throws Exception {
        Properties ltpaProps = generateLTPAKeys(keyPasswordBytes, getRealmName());
        addLTPAKeysToFile(getOutputStream(locService, keyFile), ltpaProps);
        return ltpaProps;
    }

    private OutputStream getOutputStream(WsLocationAdmin locService, final String keyImportFile) throws IOException {
        // Get the WsResource and create the file
        WsResource ltpaFile = locService.resolveResource(keyImportFile);
        ltpaFile.create();
        if (ltpaFile.isType(Type.FILE)) {
            FileUtils.setUserReadWriteOnly(ltpaFile.asFile());
        }

        // Get the output stream form the resource service
        return ltpaFile.putStream();
    }

}
