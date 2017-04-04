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
package com.ibm.wsspi.collective.plugins.helpers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.ibm.websphere.ras.annotation.Sensitive;

/**
 * Small helper class to mask potential passwords in a string.
 *
 * @ibm-spi
 */
public class PasswordUtils {

    /**
     * This method will mask the password if the parameter contains
     * --.*password.*= or
     * --.*pwd.*=
     *
     * @param inStr
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String maskPasswords(@Sensitive String inStr) {
        if (inStr == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        String[] lines = null;
        try {

            lines = URLDecoder.decode(inStr, "UTF-8").split("\n");

        } catch (UnsupportedEncodingException e) {

        }
        if (lines != null) {
            for (int i = 0; i < lines.length; i++) {
                String[] words = lines[i].split("\\s");
                for (int j = 0; j < words.length; j++) {
                    String word = words[j];
                    word = word.replaceAll("(--.*[pP][Aa]{0,1}[Ss]{0,1}[Ss]{0,1}[Ww][Oo]{0,1}[Rr]{0,1}[Dd].*=)(.*)", "$1********");
                    sb.append(word);
                    if (j < words.length - 1) {
                        sb.append(" ");
                    }
                }
                if (i < lines.length - 1) {
                    sb.append("\n");
                }
            }
        }

        return sb.toString();
    }

    /**
     * This method will mask the password if the key contains
     * --.*password.*= or
     * --.*pwd.*=
     *
     * @param key
     * @param obj
     * @return ******** if key contain password or pwd
     *         obj otherwise
     */
    public static Object maskPasswords(String key, @Sensitive Object obj) {
        if ((key.toLowerCase().contains("password"))
            || (key.toLowerCase().contains("pwd"))) {
            return "********";
        } else {
            return obj;
        }
    }

}
