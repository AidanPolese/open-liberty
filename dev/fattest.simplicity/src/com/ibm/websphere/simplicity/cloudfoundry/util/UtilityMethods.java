/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.simplicity.cloudfoundry.util;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public final class UtilityMethods {

    public static void main(String[] args) {
        String[] result = findMatchingSubstrings("^([^\\s\"]+|\"([^\"]*)\")", "\"C:\\Program Files (x86)\\CloudFoundry\\gcf.exe\" push");
        for (String string : result) {
            System.out.println(string);
        }
    }

    public static String[] findMatchingSubstrings(String regex, String text) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        ArrayList<String> matches = new ArrayList<String>();
        while (matcher.find()) {
            matches.add(matcher.group());
        }
        return matches.toArray(new String[matches.size()]);
    }

}
