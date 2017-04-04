// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

package com.ibm.websphere.servlet.response;
/**
 * @ibm-api
 * ResponseUtils contains publicly available utilities for working with
 * response data
 */
public class ResponseUtils
{
    /**
     * Searches the passed in String for any characters that could be
     * used in a cross site scripting attack (<, >, +, &, ", ', (, ), %, ;)
     * and converts them to their browser equivalent name or code specification.
     *
     * @param iString contains the String to be encoded
     *
     * @return an encoded String
     */
    public static String encodeDataString(String iString)
    {
        if (iString == null)
            return "";
        
        int strLen = iString.length(), i;

        if (strLen < 1)
            return iString;

        // convert any special chars to their browser equivalent specification
        StringBuffer retString = new StringBuffer(strLen * 2);

	for (i = 0; i < strLen; i++)
        {
	    switch (iString.charAt(i))
            {
                case '<':
                    retString.append("&lt;");
	       	    break;

	       	case '>':
                    retString.append("&gt;");
	       	    break;

	       	case '&':
                    retString.append("&amp;");
	       	    break;

	       	case '\"':
                    retString.append("&quot;");
	       	    break;

	       	case '+':
                    retString.append("&#43;");
	       	    break;

	       	case '(':
                    retString.append("&#40;");
	       	    break;

	       	case ')':
                    retString.append("&#41;");
	       	    break;

	       	case '\'':
                    retString.append("&#39;");
	       	    break;

	       	case '%':
                    retString.append("&#37;");
	       	    break;

	       	case ';':
                    retString.append("&#59;");
	       	    break;

	       	default:
	       	    retString.append(iString.charAt(i));
	       	    break;
	    }
	}
			
	return retString.toString();
    }
}

