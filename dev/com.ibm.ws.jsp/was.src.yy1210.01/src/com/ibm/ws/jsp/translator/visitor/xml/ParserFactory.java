//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.

/*
 * 1.2, 6/24/02
 *
 * IBM Confidential OCO Source Material
 * 5639-D57 (C) COPYRIGHT International Business Machines Corp. 2002
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 */

package com.ibm.ws.jsp.translator.visitor.xml;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.ibm.wsspi.webcontainer.util.ThreadContextHelper;

/**
 * The purpose of this class is to force the usage of the Parser provided by the Runtime and not
 * any one that an application may provide.
 */
public class ParserFactory {
    private static DocumentBuilderFactory documentBuilderFactory = null;
    private static DocumentBuilder documentBuilder = null;
    private static SAXParserFactory saxParserFactory = null;
    
	public static synchronized SAXParser newSAXParser(boolean setNamespaceAware, boolean setValidating) throws ParserConfigurationException, SAXException {
		ClassLoader oldLoader = ThreadContextHelper.getContextClassLoader();
        ThreadContextHelper.setClassLoader(ParserFactory.class.getClassLoader());
		try {
            if (saxParserFactory != null) {
                if (saxParserFactory.isNamespaceAware() == setNamespaceAware &&
                    saxParserFactory.isValidating() == setValidating) {
                    return saxParserFactory.newSAXParser();                    
                }
            }
            saxParserFactory =  SAXParserFactory.newInstance();
            saxParserFactory.setValidating(setValidating);
            saxParserFactory.setNamespaceAware(setNamespaceAware);
            return saxParserFactory.newSAXParser();                    
		}
        finally {
            ThreadContextHelper.setClassLoader(oldLoader);
		}
	}
	
	public static synchronized Document newDocument(boolean setNamespaceAware, boolean setValidating) throws ParserConfigurationException{
		ClassLoader oldLoader = ThreadContextHelper.getContextClassLoader();
        ThreadContextHelper.setClassLoader(ParserFactory.class.getClassLoader());
		try {
            if (documentBuilderFactory != null) {
                if (documentBuilderFactory.isNamespaceAware() == setNamespaceAware &&
                    documentBuilderFactory.isValidating() == setValidating) {
                    return documentBuilder.newDocument();                    
                }
            }
            documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(setNamespaceAware);
            documentBuilderFactory.setValidating(setValidating);
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            return documentBuilder.newDocument();                    
		} 
        finally {
            ThreadContextHelper.setClassLoader(oldLoader);
		}
	}
    
	public static void parseDocument(SAXParser parser, InputSource is, DefaultHandler dh) throws IOException, SAXException {
		ClassLoader oldLoader = ThreadContextHelper.getContextClassLoader();
        ThreadContextHelper.setClassLoader(ParserFactory.class.getClassLoader());
		
		try {
			parser.parse(is, dh);
		}
		finally {
            ThreadContextHelper.setClassLoader(oldLoader);
		}
	}

	public static void parseDocument(SAXParser parser, InputStream is, DefaultHandler dh) throws IOException, SAXException {
		ClassLoader oldLoader = ThreadContextHelper.getContextClassLoader();
        ThreadContextHelper.setClassLoader(ParserFactory.class.getClassLoader());
		try {
			parser.parse(is, dh);
		}
		finally {
            ThreadContextHelper.setClassLoader(oldLoader);
		}
	}

    
    
}
