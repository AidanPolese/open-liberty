/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.config.utility.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class VariableSubstituter {
    private static Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}");

    public static StringBuilder substitute(StringBuilder text, HashMap<String, String> replacements) {
        StringBuilder buffer = new StringBuilder();
        Matcher matcher = pattern.matcher(text);

        int i = 0;
        while (matcher.find()) {
            String replacement = replacements.get(matcher.group(1));
            buffer.append(text.substring(i, matcher.start()));

            if (replacement == null) {
                buffer.append(matcher.group(0));
            } else {
                buffer.append(replacement);
            }

            i = matcher.end();
        }

        buffer.append(text.substring(i, text.length()));
        return buffer;
    }

    public static List<String> getAllVariables(StringBuilder text) {
        List<String> allVariables = new ArrayList<String>();
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            allVariables.add(matcher.group(1));
        }

        return allVariables;
    }
}
