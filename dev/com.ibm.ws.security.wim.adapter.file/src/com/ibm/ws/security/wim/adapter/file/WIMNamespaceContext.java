package com.ibm.ws.security.wim.adapter.file;

import java.util.Iterator;
import javax.xml.*;
import javax.xml.namespace.NamespaceContext;

public class WIMNamespaceContext implements NamespaceContext {

    public String getNamespaceURI(String prefix) {
        if (prefix == null)
            throw new NullPointerException("Null prefix");
        else if ("wim".equals(prefix))
            return "http://www.ibm.com/websphere/wim";
        else if ("xml".equals(prefix))
            return XMLConstants.XML_NS_URI;
        else if ("xsi".equals(prefix))
            return XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI;
        else if ("sdo".equals(prefix))
            return "commonj.sdo";
        return XMLConstants.NULL_NS_URI;
    }

    // This method isn't necessary for XPath processing.
    public String getPrefix(String uri) {
        throw new UnsupportedOperationException();
    }

    // This method isn't necessary for XPath processing either.
    public Iterator getPrefixes(String uri) {
        throw new UnsupportedOperationException();
    }
}