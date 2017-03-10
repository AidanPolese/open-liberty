/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.product.utility.extension;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import com.ibm.ws.kernel.boot.cmdline.Utils;
import com.ibm.ws.product.utility.BaseCommandTask;
import com.ibm.ws.product.utility.CommandConsole;
import com.ibm.ws.product.utility.CommandUtils;
import com.ibm.ws.product.utility.ExecutionContext;
import com.ibm.ws.product.utility.LicenseUtility;

/**
 *
 */
public class DisplayLicenseAgreementTask extends BaseCommandTask {

    public static final String DISPLAY_LICENSE_AGREEMENT_TASK_NAME = "viewLicenseAgreement";
    private final String LA_PREFIX = "LA";

    /** {@inheritDoc} */
    @Override
    public Set<String> getSupportedOptions() {
        return new HashSet<String>();
    }

    /** {@inheritDoc} */
    @Override
    public String getTaskName() {
        return DISPLAY_LICENSE_AGREEMENT_TASK_NAME;
    }

    /** {@inheritDoc} */
    @Override
    public String getTaskHelp() {
        return super.getTaskHelp("viewLicenseAgreement.desc", "viewLicenseAgreement.usage.options", null, null, null);
    }

    /** {@inheritDoc} */
    @Override
    public String getTaskDescription() {
        return getOption("viewLicenseAgreement.desc");
    }

    /** {@inheritDoc} */
    @Override
    protected void doExecute(ExecutionContext context) {
        CommandConsole commandConsole = context.getCommandConsole();
        File installLicenseDir = new File(Utils.getInstallDir(), "lafiles");
        InputStream licenseFileStream = null;

        try {
            LicenseUtility licenseUtility = new LicenseUtility();
            File laEntry = licenseUtility.getLicenseFile(installLicenseDir, LA_PREFIX);
            licenseFileStream = new FileInputStream(laEntry);

            if (licenseFileStream != null) {
                licenseUtility.displayLicenseFile(licenseFileStream, commandConsole);
            }

        } catch (Exception e) {
            commandConsole.printErrorMessage(CommandUtils.getMessage("LICENSE_NOT_FOUND", e.getMessage()));
        } finally {
            FileUtils.tryToClose(licenseFileStream);
        }

    }

}