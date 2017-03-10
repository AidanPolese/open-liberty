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
package com.ibm.ws.config.xml.internal;

public class ConfigExpressionScanner {
    enum NumericOperator {
        PLUS('+') {
            @Override
            public long evaluate(long value1, long value2) {
                return value1 + value2;
            }
        },
        MINUS('-') {
            @Override
            public long evaluate(long value1, long value2) {
                return value1 - value2;
            }
        },
        MULTIPLY('*') {
            @Override
            public long evaluate(long value1, long value2) {
                return value1 * value2;
            }
        },
        DIVIDE('/') {
            @Override
            public long evaluate(long value1, long value2) {
                return value1 / value2;
            }
        };

        char c;

        NumericOperator(char c) {
            this.c = c;
        }

        public abstract long evaluate(long value1, long value2);
    }

    private final String string;
    private int pos;

    public ConfigExpressionScanner(String string) {
        this.string = string;
    }

    public boolean end() {
        return pos == string.length();
    }

    public boolean scan(char c) {
        if (!end() && string.charAt(pos) == c) {
            pos++;
            return true;
        }
        return false;
    }

    public String scanName() {
        if (end()) {
            return null;
        }

        int cp = string.codePointAt(pos);
        if (!Character.isJavaIdentifierStart(cp)) {
            return null;
        }

        int begin = pos;
        pos += Character.charCount(cp);

        for (; pos < string.length(); pos += Character.charCount(cp)) {
            cp = string.codePointAt(pos);
            if (!Character.isJavaIdentifierPart(cp) && cp != '.') {
                break;
            }
        }

        return string.substring(begin, pos);
    }

    public Long scanLong() {
        if (end()) {
            return null;
        }

        char c = string.charAt(pos);
        if (c < '0' || c > '9') {
            return null;
        }

        int begin = pos++;

        for (; pos < string.length(); pos++) {
            c = string.charAt(pos);
            if (c < '0' || c > '9') {
                break;
            }
        }

        return Long.parseLong(string.substring(begin, pos));
    }

    public NumericOperator scanNumericOperator() {
        if (end()) {
            return null;
        }

        char c = string.charAt(pos);
        for (NumericOperator op : NumericOperator.values()) {
            if (c == op.c) {
                pos++;
                return op;
            }
        }

        return null;
    }

    /**
     * Scan an argument to the ${servicePidOrFilter()} expression.
     * 
     * @return
     */
    public String scanFilterArgument() {
        if (end()) {
            return null;
        }

        int cp = string.codePointAt(pos);
        if (!Character.isJavaIdentifierStart(cp)) {
            return null;
        }

        int begin = pos;
        pos += Character.charCount(cp);

        for (; pos < string.length(); pos += Character.charCount(cp)) {
            cp = string.codePointAt(pos);
            if (!Character.isJavaIdentifierPart(cp) && cp != '.' && cp != '-') {
                break;
            }
        }

        return string.substring(begin, pos);
    }
}
