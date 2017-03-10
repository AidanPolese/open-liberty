/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013, 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.install.repository.internal;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.framework.Version;

import com.ibm.ws.install.InstallConstants;
import com.ibm.ws.kernel.provisioning.VersionUtility;
import com.ibm.ws.repository.resources.internal.AppliesToProcessor;
import com.ibm.ws.repository.transport.model.AppliesToFilterInfo;

/**
 *
 */
public class RepositoryUtils {

    public final static String COM_IBM_WEBSPHERE_PRODUCTINSTALLTYPE = "com.ibm.websphere.productInstallType";
    public final static String COM_IBM_WEBSPHERE_PRODUCTLICENSETYPE = "com.ibm.websphere.productLicenseType";

    private static Locale locale;
    static Logger logger = Logger.getLogger(InstallConstants.LOGGER_NAME);

    private static ResourceBundle messages;
    private static ResourceBundle installMessages;

    public static void setLocale(Locale locale) {
        if (RepositoryUtils.locale == null) {
            RepositoryUtils.locale = locale;
        } else {
            if (RepositoryUtils.locale != locale) {
                RepositoryUtils.locale = locale;
                messages = null;
                installMessages = null;
            }
        }
    }

    public static String getMessage(String key, Object... args) {
        if (messages == null) {
            if (locale == null)
                locale = Locale.getDefault();
            messages = ResourceBundle.getBundle("com.ibm.ws.install.internal.resources.Repository", locale);
        }
        String message = messages.getString(key);
        if (args.length == 0)
            return message;
        MessageFormat messageFormat = new MessageFormat(message, locale);
        return messageFormat.format(args);
    }

    public static String getInstallMessage(String key, Object... args) {
        if (installMessages == null) {
            if (locale == null)
                locale = Locale.getDefault();
            installMessages = ResourceBundle.getBundle("com.ibm.ws.install.internal.resources.InstallKernel", locale);
        }
        String message = installMessages.getString(key);
        if (args.length == 0)
            return message;
        MessageFormat messageFormat = new MessageFormat(message, locale);
        return messageFormat.format(args);
    }

    public static boolean matchAppliesTo(String assetType, String assetName, String fileName, String appliesTo, String productId, String productVersion, String productInstallType,
                                         String productLcenseType, String productEdition) {
        if (appliesTo == null)
            return true;

        List<AppliesToFilterInfo> atfis = AppliesToProcessor.parseAppliesToHeader(appliesTo);
        for (AppliesToFilterInfo atfi : atfis) {
            String pId = atfi.getProductId();
            if (pId != null && !pId.equals(productId)) {
                debug("matchAppliesTo()",
                      String.format("The specified product installation is for product %s and the asset(%s) %s from %s only applies to product %s.",
                                    productId, assetType, assetName, fileName, pId));
                return false;
            }
            if (productVersion != null && atfi.getMinVersion() != null && atfi.getMaxVersion() != null) {
                Version min = VersionUtility.stringToVersion(atfi.getMinVersion().getValue());
                Version max = VersionUtility.stringToVersion(atfi.getMaxVersion().getValue());
                Version v = VersionUtility.stringToVersion(productVersion);
                if (v.compareTo(min) < 0 || v.compareTo(max) > 0) {
                    debug("matchAppliesTo()",
                          String.format("The specified product installation is at version %s and the asset(%s) %s from %s only applies to versions [%s,%s].",
                                        productVersion, assetType, assetName, fileName, atfi.getMinVersion().getValue(), atfi.getMaxVersion().getValue()));
                    return false;
                }
            }
            String pInstallType = atfi.getInstallType();
            if (productInstallType != null && pInstallType != null && !pInstallType.equalsIgnoreCase(productInstallType)) {
                debug("matchAppliesTo()",
                      String.format("The specified product installation is at type %s and the asset(%s) %s from %s only applies to type %s.",
                                    productInstallType, assetType, assetName, fileName, pInstallType));
                return false;
            }

            List<String> pRawEditions = atfi.getRawEditions();
            if (productEdition != null && pRawEditions != null && !pRawEditions.contains(productEdition)) {
                debug("matchAppliesTo()",
                      String.format("The specified product installation is edition %s and the asset(%s) %s from %s only applies to the editions %s.",
                                    productEdition, assetType, assetName, fileName, atfi.getEditions()));
                return false;
            }
        }
        return true;
    }

    private static void debug(String method, String msg) {
        if (msg != null)
            logger.log(Level.FINEST, "RepositoryUtils." + (method == null ? "unknown()" : method) + ": " + msg);
    }
}
