//IBM Confidential OCO Source Material
//	5724-i63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.
//Defect PK26233 2006/07/11 ClassCastException seen when foreign jars are used
// defect 414623 FVT:Default JSP version should be 2.0 not 2.1 2007/01/12 10:15:37 Scott Johnson                                                                                        

package com.ibm.ws.jsp.translator.visitor.validator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.security.AccessController;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.jsp.tagext.PageData;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.ws.jsp.Constants;
import com.ibm.ws.jsp.taglib.TagLibraryCache;
import com.ibm.wsspi.webcontainer.util.ThreadContextHelper;

public class PageDataImpl extends PageData {
	
	static private Logger logger;
	private static final String CLASS_NAME="com.ibm.ws.jsp.translator.visitor.validator.PageDataImpl";
	static{
		logger = Logger.getLogger("com.ibm.ws.jsp");
	}

    private DocumentFragment jspDocument = null;
    
    public PageDataImpl(Document document, TagLibraryCache tlc) {
        this.jspDocument = document.createDocumentFragment();
        Node root = jspDocument; 
        if (document.getElementsByTagNameNS(Constants.JSP_NAMESPACE, Constants.JSP_ROOT_TYPE).getLength() == 0) {
            Element rootElement = document.createElementNS(Constants.JSP_NAMESPACE, "jsp:" + Constants.JSP_ROOT_TYPE);
            rootElement.setAttributeNS(Constants.JSP_NAMESPACE, "version", "2.0");
            jspDocument.appendChild(rootElement);
            root = rootElement;             
        }
        //Defect 216189
        copyNodes(document, document, root);
        
        Element rootElement = (Element)jspDocument.getFirstChild();
        
        org.w3c.dom.NamedNodeMap attrs = rootElement.getAttributes();
        
        for (int i = 0; i < attrs.getLength(); i++) {
            Node attr = attrs.item(i);
            if (tlc.getImplicitTagLibPrefixMap().containsValue(attr.getNodeValue())) {
                rootElement.removeAttribute(attr.getNodeName());
            }
        }
        
        NodeList nl = rootElement.getElementsByTagNameNS(Constants.JSP_NAMESPACE, Constants.JSP_PAGE_DIRECTIVE_TYPE);
        
        boolean pageEncodingFound = false;
        boolean contentTypeFound = false;
        
        if (nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {
                Element pageDirectiveElement = (Element)nl.item(i);
                if (pageDirectiveElement.hasAttribute("pageEncoding")) {
                    pageEncodingFound = true;
                    pageDirectiveElement.setAttribute("pageEncoding", "UTF-8");    
                }
                if (pageDirectiveElement.hasAttribute("contentType")) {
                    contentTypeFound = true;
                    pageDirectiveElement.setAttribute("contentType", "text/xml;charset=UTF-8");    
                }
            }
        }
        
        String jspId = rootElement.getAttributeNS(Constants.JSP_NAMESPACE, "id");
        if (pageEncodingFound == false) {
            Element pageDirectiveElement = document.createElementNS(Constants.JSP_NAMESPACE, "jsp:" + Constants.JSP_PAGE_DIRECTIVE_TYPE);
            pageDirectiveElement.setAttributeNS(Constants.JSP_NAMESPACE, "pageEncoding", "UTF-8");
            pageDirectiveElement.setAttributeNS(Constants.JSP_NAMESPACE, "jsp:id", jspId);
            rootElement.insertBefore(pageDirectiveElement, rootElement.getFirstChild());
        }
        
        if (contentTypeFound == false) {
            Element pageDirectiveElement = document.createElementNS(Constants.JSP_NAMESPACE, "jsp:" + Constants.JSP_PAGE_DIRECTIVE_TYPE);
            pageDirectiveElement.setAttributeNS(Constants.JSP_NAMESPACE, "contentType", "text/xml;charset=UTF-8");
            pageDirectiveElement.setAttributeNS(Constants.JSP_NAMESPACE, "jsp:id", jspId);
            rootElement.insertBefore(pageDirectiveElement, rootElement.getFirstChild());
        }
        //JspTranslatorUtil.printElements(document.getDocumentElement(), 0);
    }


	public InputStream getInputStream() {
			if(System.getSecurityManager() != null){
					 return (InputStream) AccessController.doPrivileged(new java.security.PrivilegedAction() {
						public Object run() {
							return _getInputStream();
						}
					});
			}
			else{
				return _getInputStream();
			}
	}

    private InputStream _getInputStream() {
        InputStream is = null;
        ClassLoader oldLoader = null;  //PK26233

        try {
			oldLoader = ThreadContextHelper.getContextClassLoader();		//PK26233
			ClassLoader newLoader = ThreadContextHelper.getExtClassLoader();
			if (newLoader==null)
				ThreadContextHelper.setClassLoader(PageDataImpl.class.getClassLoader());		//PK26233
			else
				ThreadContextHelper.setClassLoader(newLoader);
			try
			{
	            TransformerFactory tfactory = TransformerFactory.newInstance();
	            Transformer serializer = tfactory.newTransformer();
	            Properties oprops = new Properties();
	            oprops.put("method", "xml");
	            serializer.setOutputProperties(oprops);
	            StringWriter writer = new StringWriter();
	            Result result = new StreamResult(writer);
	            serializer.transform(new DOMSource(jspDocument), result);
	            is = new ByteArrayInputStream(writer.toString().getBytes("UTF-8"));
        	}finally {
                ThreadContextHelper.setClassLoader(oldLoader);		//PK26233
			}
        }
        catch (TransformerConfigurationException e) {
            logger.logp(Level.WARNING, CLASS_NAME, "_getInputStream", "Encountered configuration error during transform of jspDocument", e);
        }     
        catch (TransformerException e) {
			logger.logp(Level.WARNING, CLASS_NAME, "_getInputStream", "failed to transform jspDocument", e);
        }     
        catch (IOException e) {
			logger.logp(Level.WARNING, CLASS_NAME, "_getInputStream", "Failed to convert document to inputstream", e);
        }

        return (is);
    }
    
    //Defect 216189 
    private void copyNodes(Document document, Node in, Node out) {
        String parentNamespace = in.getNamespaceURI();
        String parentLocalName = in.getLocalName();
        for (int i = 0; i < in.getChildNodes().getLength(); i++) {
            Node childNode = in.getChildNodes().item(i);
            Node newNode = document.importNode(childNode, false);
            //PK53882 - add check for null on childNode
            if (childNode != null && childNode.getNodeType() == Node.CDATA_SECTION_NODE) {
                if (parentNamespace != null) {
                    if (parentNamespace.equals(Constants.JSP_NAMESPACE)) {
                        if (parentLocalName.equals(Constants.JSP_DECLARATION_TYPE) == false &&
                            parentLocalName.equals(Constants.JSP_EXPRESSION_TYPE) == false &&
                            parentLocalName.equals(Constants.JSP_SCRIPTLET_TYPE) == false) {                        
                            String jspId = ((Element)in).getAttributeNS(Constants.JSP_NAMESPACE, "id");
                            Element jspTextElement = document.createElementNS(Constants.JSP_NAMESPACE, "jsp:text");
                            jspTextElement.appendChild(newNode);
                            jspTextElement.setAttributeNS(Constants.JSP_NAMESPACE, "jsp:id", jspId);
                            out.appendChild(jspTextElement);    
                        }
                    }
                    else {
                        String jspId = ((Element)in).getAttributeNS(Constants.JSP_NAMESPACE, "id");
                        Element jspTextElement = document.createElementNS(Constants.JSP_NAMESPACE, "jsp:text");
                        jspTextElement.appendChild(newNode);
                        jspTextElement.setAttributeNS(Constants.JSP_NAMESPACE, "jsp:id", jspId);
                        out.appendChild(jspTextElement);    
                    }
                }
            }
            else {
                //PK53882 - add check for null on newNode
                if (newNode != null) {
                    //Defect 216189 Start
                    if (newNode.getNodeType() == Node.ELEMENT_NODE) {
                        NamedNodeMap attrs = newNode.getAttributes();
                        for (int j = 0; j < attrs.getLength(); j++) {
                            Attr attr = (Attr)attrs.item(j);
                            String value = attr.getValue();
                            value = value.replaceAll("&gt;", ">");
                            value = value.replaceAll("&lt;", "<");
                            value = value.replaceAll("&amp;", "&");
                            attr.setValue(value);
                        }
                    }
                    //Defect 216189 End
                    out.appendChild(newNode);
                }
            }
            //PK53882 - add checking for nulls
            if (childNode != null && newNode != null) {
                copyNodes(document, childNode, newNode);
            }
        }
    }
}
