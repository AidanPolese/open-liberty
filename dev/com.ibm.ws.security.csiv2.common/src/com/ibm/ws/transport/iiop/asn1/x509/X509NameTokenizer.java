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
/*
 * Some of the code was derived from code supplied by the Apache Software Foundation licensed under the Apache License, Version 2.0.
 */

package com.ibm.ws.transport.iiop.asn1.x509;

/**
 * class for breaking up an X500 Name into it's component tokens, ala
 * java.util.StringTokenizer. We need this class as some of the
 * lightweight Java environment don't support classes like
 * StringTokenizer.
 */
public class X509NameTokenizer
{
    private String          value;
    private int             index;
    private char            seperator;
    private StringBuilder    buf = new StringBuilder();

    public X509NameTokenizer(
        String  oid)
    {
        this(oid, ',');
    }

    public X509NameTokenizer(
        String  oid,
        char    seperator)
    {
        this.value = oid;
        this.index = -1;
        this.seperator = seperator;
    }

    public boolean hasMoreTokens()
    {
        return (index != value.length());
    }

    public String nextToken()
    {
        if (index == value.length())
        {
            return null;
        }

        int     end = index + 1;
        boolean quoted = false;
        boolean escaped = false;

        buf.setLength(0);

        while (end != value.length())
        {
            char    c = value.charAt(end);

            if (c == '"')
            {
                if (!escaped)
                {
                    quoted = !quoted;
                }
                else
                {
                    buf.append(c);
                }
                escaped = false;
            }
            else
            {
                if (escaped || quoted)
                {
                    buf.append(c);
                    escaped = false;
                }
                else if (c == '\\')
                {
                    escaped = true;
                }
                else if (c == seperator)
                {
                    break;
                }
                else
                {
                    buf.append(c);
                }
            }
            end++;
        }

        index = end;
        return buf.toString().trim();
    }
}
