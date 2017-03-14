/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.http;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

/**
 * This class provides static/stateless utility methods for scrubbing URLs for
 * safe use in error messages.
 * 
 * HTML entities in URLs need to be escaped before being used in messages of any kind.
 * <p>
 * 
 */
public class URLEscapingUtils {

    public static final String toSafeString(String url) {
        StringBuilder newURL = new StringBuilder();

        StringCharacterIterator iter = new StringCharacterIterator(url);
        for (char c = iter.first(); c != CharacterIterator.DONE; c = iter.next()) {
            switch (c) {
                case '<':
                    newURL.append("&lt;");
                    break;
                case '>':
                    newURL.append("&gt;");
                    break;
                case '&':
                    newURL.append("&amp;");
                    break;
                case '"':
                    newURL.append("&quot;");
                    break;
                case '\t':
                    newURL.append("&#009;");
                    break;
                case '!':
                    newURL.append("&#033;");
                    break;
                case '#':
                    newURL.append("&#035;");
                    break;
                case '$':
                    newURL.append("&#036;");
                    break;
                case '%':
                    newURL.append("&#037;");
                    break;
                case '\'':
                    newURL.append("&#039;");
                    break;
                case '(':
                    newURL.append("&#040;");
                    break;
                case ')':
                    newURL.append("&#041;");
                    break;
                case '*':
                    newURL.append("&#042;");
                    break;
                case '+':
                    newURL.append("&#043;");
                    break;
                case ',':
                    newURL.append("&#044;");
                    break;
                case '-':
                    newURL.append("&#045;");
                    break;
                case '.':
                    newURL.append("&#046;");
                    break;
                case '/':
                    newURL.append("&#047;");
                    break;
                case ':':
                    newURL.append("&#058;");
                    break;
                case ';':
                    newURL.append("&#059;");
                    break;
                case '=':
                    newURL.append("&#061;");
                    break;
                case '?':
                    newURL.append("&#063;");
                    break;
                case '@':
                    newURL.append("&#064;");
                    break;
                case '[':
                    newURL.append("&#091;");
                    break;
                case '\\':
                    newURL.append("&#092;");
                    break;
                case ']':
                    newURL.append("&#093;");
                    break;
                case '^':
                    newURL.append("&#094;");
                    break;
                case '_':
                    newURL.append("&#095;");
                    break;
                case '`':
                    newURL.append("&#096;");
                    break;
                case '{':
                    newURL.append("&#123;");
                    break;
                case '|':
                    newURL.append("&#124;");
                    break;
                case '}':
                    newURL.append("&#125;");
                    break;
                case '~':
                    newURL.append("&#126;");
                    break;
                default:
                    newURL.append(c);
                    break;
            }
        }

        return newURL.toString();
    }

    private URLEscapingUtils() {};
}
