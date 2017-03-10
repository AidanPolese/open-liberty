/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013, 2016
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package wlp.lib.extract;

public class InstallUtils {

	public static final String PRODUCTNAME = "Liberty";

    public static String getEditionName(String editionCode) {

        String editionCodeUpperCase = editionCode.toUpperCase();
        if (editionCodeUpperCase.equals("BASE"))
            return PRODUCTNAME;
        else if (editionCodeUpperCase.equals("BASE_ILAN"))
            return PRODUCTNAME + " (ILAN)";
        else if (editionCodeUpperCase.equals("DEVELOPERS"))
            return PRODUCTNAME + " for Developers";
        else if (editionCodeUpperCase.equals("EXPRESS"))
            return PRODUCTNAME + " - Express";
        else if (editionCodeUpperCase.equals("EARLY_ACCESS"))
            return PRODUCTNAME + " Early Access";
        else if (editionCodeUpperCase.equals("LIBERTY_CORE"))
            return PRODUCTNAME + " Core";
        else if (editionCodeUpperCase.equals("LIBERTY_CORE_ISV"))
            return PRODUCTNAME + " Core for ISVs";
        else if (editionCodeUpperCase.equals("ND"))
            return PRODUCTNAME + " Network Deployment";
        else if (editionCodeUpperCase.equals("ZOS"))
            return PRODUCTNAME + " z/OS";
        else {
            return editionCode;
        }
    }
}
