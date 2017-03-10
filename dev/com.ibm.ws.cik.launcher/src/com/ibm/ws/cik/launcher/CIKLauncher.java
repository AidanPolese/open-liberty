/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013, 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.cik.launcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import com.ibm.ws.install.InstallConstants;
import com.ibm.ws.install.InstallConstants.ExistsAction;
import com.ibm.ws.install.InstallEventListener;
import com.ibm.ws.install.InstallKernel;
import com.ibm.ws.install.InstallKernelFactory;
import com.ibm.ws.install.InstallProgressEvent;
import com.ibm.ws.install.internal.InstallKernelImpl;

/**
 * This class is for testing purpose.
 */
public class CIKLauncher {

    private static InstallEventListener ielistener;

    public static void main(String[] args) {
        if (args == null || args.length == 0)
            System.exit(1);

        trustAll();
        int rc = 1;
        if (args[0].equalsIgnoreCase("install")) {
            if (args.length < 2)
                rc = 2;
            else if (args.length < 4) {
                try {
                    rc = install(args[1], null);
                } catch (Exception e) {
                    e.printStackTrace();
                    rc = 3;
                }
            } else if (args[2].equalsIgnoreCase("--to")) {
                try {
                    rc = install(args[1], args[3]);
                } catch (Exception e) {
                    e.printStackTrace();
                    rc = 4;
                }
            } else {
                System.out.println("Error: unknown install option \"" + args[2] + "\".");
                rc = 5;
            }
        } else if (args[0].equalsIgnoreCase("installFix")) {
            if (args.length < 2)
                rc = 7;
            else if (args.length < 4) {
                try {
                    rc = installFixes(args[1]);
                } catch (Exception e) {
                    e.printStackTrace();
                    rc = 8;
                }
            }

        } else if (args[0].equalsIgnoreCase("uninstall")) {
            if (args.length < 2)
                rc = 2;
            else {
                try {
                    ArrayList<String> features = new ArrayList<String>();
                    String[] seperatedFeatures = null;
                    for (int i = 1; i < args.length; i++) {
                        seperatedFeatures = args[i].split("\\s*,\\s*");
                        features.addAll(Arrays.asList(seperatedFeatures));
                    }
                    rc = uninstallFeatures(features);
                } catch (Exception e) {
                    e.printStackTrace();
                    rc = 3;
                }
            }
        } else if (args[0].equalsIgnoreCase("uninstallFix")) {
            if (args.length != 2)
                rc = 2;
            else {
                try {
                    ArrayList<String> fixes = new ArrayList<String>();
                    String[] seperatedFixes = null;
                    for (int i = 1; i < args.length; i++) {
                        seperatedFixes = args[i].split("\\s*,\\s*");
                        fixes.addAll(Arrays.asList(seperatedFixes));
                    }
                    rc = uninstallFixes(fixes);
                } catch (Exception e) {
                    e.printStackTrace();
                    rc = 3;
                }
            }
        } else {
            System.out.println("Error: unknown action \"" + args[0] + "\".");
            rc = 6;
        }
        System.exit(rc);
    }

    private static int install(String featureNames, String toExtension) {
        System.out.println("CIK launcher install " + featureNames + " to \"" + toExtension + "\"");
        System.out.println("repository.description.url=\"" + System.getProperty("repository.description.url") + "\"");
        String eaString = System.getProperty("when-file-exists", "fail");
        ExistsAction action = ExistsAction.fail;//default
        if (eaString != null) {
            try {
                action = ExistsAction.valueOf(eaString);
            } catch (Exception e) {
                System.out.println("invalid when-file-exists action " + eaString);
                return -2;
            }
        }
        System.out.println("action set to= " + action.toString());

        initializeLog();
        InstallKernel installKernel = InstallKernelFactory.getInstance();
        installKernel.addListener(getListener(), InstallConstants.EVENT_TYPE_PROGRESS);

        try {
            if (featureNames.contains(",")) {
                ArrayList<String> features = new ArrayList<String>();
                String[] seperatedFeatures = featureNames.split(",");
                features.addAll(Arrays.asList(seperatedFeatures));
                installKernel.installFeature(features, toExtension, true, action);
            } else {
                installKernel.installFeature(featureNames, toExtension, true, action);
            }
        } catch (Exception e) {
            System.out.println("CIK launcher install failed: " + e.getMessage());
            installKernel.removeListener(getListener());
            return -1;
        }
        System.out.println("CIK launcher install sucessfully completed");
        installKernel.removeListener(getListener());
        return 0;
    }

    private static int installFixes(String fix) {
        System.out.println("CIK launcher install fixes " + fix);
        System.out.println("repository.description.url=\"" + System.getProperty("repository.description.url") + "\"");
        initializeLog();
        InstallKernel installKernel = InstallKernelFactory.getInstance();
        installKernel.addListener(getListener(), InstallConstants.EVENT_TYPE_PROGRESS);
        try {
            if (fix.contains(",")) {
                ArrayList<String> ifixes = new ArrayList<String>();
                String[] seperatedFixes = fix.split(",");
                ifixes.addAll(Arrays.asList(seperatedFixes));
                ((InstallKernelImpl) installKernel).install("IFIX", ifixes, null, true, ExistsAction.replace, null, null);
            } else {
                installKernel.installFix(fix);
            }
        } catch (Exception e) {
            System.out.println("CIK launcher install fix failed: " + e.getMessage());
            installKernel.removeListener(getListener());
            return 100;
        }
        System.out.println("CIK launcher install fix successfully completed");
        installKernel.removeListener(getListener());
        return 0;
    }

    public static int uninstallFeatures(ArrayList<String> features) {
        System.out.println("CIK Launcher uninstall features " + features.toString());
        System.out.println("repository.description.url=\"" + System.getProperty("repository.description.url") + "\"");
        initializeLog();
        InstallKernel installKernel = InstallKernelFactory.getInstance();
        installKernel.addListener(getListener(), InstallConstants.EVENT_TYPE_PROGRESS);
        try {
            if (features.size() == 1) {
                installKernel.uninstallFeature(features.get(0), false);
            } else {
                installKernel.uninstallFeature(features);
            }
        } catch (Exception e) {
            System.out.println("CIK launcher uninstall failed: " + e.getMessage());
            installKernel.removeListener(getListener());
            return 100;
        }
        System.out.println("CIK launcher uninstall successfully completed");
        installKernel.removeListener(getListener());
        return 0;
    }

    public static int uninstallFixes(ArrayList<String> fixes) {
        System.out.println("CIK Launcher uninstall fixes " + fixes.toString());
        System.out.println("repository.description.url=\"" + System.getProperty("repository.description.url") + "\"");
        initializeLog();
        InstallKernel installKernel = InstallKernelFactory.getInstance();
        installKernel.addListener(getListener(), InstallConstants.EVENT_TYPE_PROGRESS);
        try {
            if (fixes.size() == 1) {
                installKernel.uninstallFix(fixes.get(0));
            } else {
                System.out.println("CIK launcher does not support uninstall multiple fixes");
                installKernel.removeListener(getListener());
                return 101;
            }
        } catch (Exception e) {
            System.out.println("CIK launcher uninstall failed: " + e.getMessage());
            installKernel.removeListener(getListener());
            return 100;
        }
        System.out.println("CIK launcher uninstall successfully completed");
        installKernel.removeListener(getListener());
        return 0;
    }

    /**
     * @return the ipel
     */
    public static InstallEventListener getListener() {
        if (ielistener == null) {
            ielistener = new InstallEventListener() {
                @Override
                public void handleInstallEvent(InstallProgressEvent event) {
                    switch (event.state) {
                        case InstallProgressEvent.BEGIN:
                            System.out.println("BEGIN..." + event.progress + "% completed");
                            break;
                        case InstallProgressEvent.CHECK:
                            System.out.println("CHECK..." + event.progress + "% completed");
                            break;
                        case InstallProgressEvent.CLEAN_UP:
                            System.out.println("CLEAN_UP..." + event.progress + "% completed");
                            break;
                        case InstallProgressEvent.COMPLETE:
                            System.out.println("COMPLETE..." + event.progress + "% completed");
                            break;
                        case InstallProgressEvent.DOWNLOAD:
                            System.out.println("DOWNLOAD..." + event.progress + "% completed");
                            break;
                        case InstallProgressEvent.INSTALL:
                            System.out.println("INSTALL..." + event.progress + "% completed");
                            break;
                        case InstallProgressEvent.UNINSTALL:
                            System.out.println("UNINSTALL..." + event.progress + "% completed");
                            break;
                        default:
                            break;
                    }
                }
            };
        }
        return ielistener;
    }

    public static void trustAll() {
        HostnameVerifier trustAll = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(trustAll);
    }

    private static void initializeLog() {
        String logLevel = System.getProperty("cik.logLevel");

        if (null != logLevel && !logLevel.trim().equals("")) {
            InstallKernelFactory.getInstance().enableConsoleLog(Level.parse(logLevel));
        }
    }
}
