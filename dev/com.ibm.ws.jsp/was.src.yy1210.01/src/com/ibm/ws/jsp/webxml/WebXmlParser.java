//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.
// defect 400645 "Batchcompiler needs to get webcon custom props"  2004/10/25 Scott Johnson
// defect 395182.2  70FVT: make servlet 2.3 compatible with JSP 2.1 for migration 2007/02/07 Scott Johnson
// APAR   PM21451	check for null is passed to parseWebXml 2011/3/25    mmulholl
//

package com.ibm.ws.jsp.webxml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.ibm.ws.jsp.JspCoreException;
import com.ibm.ws.jsp.JspOptions;
import com.ibm.ws.jsp.configuration.JspConfigProperty;
import com.ibm.ws.jsp.configuration.JspConfigPropertyGroup;
import com.ibm.ws.jsp.configuration.JspXmlExtConfig;
import com.ibm.ws.jsp.translator.visitor.xml.ParserFactory;

public class WebXmlParser extends DefaultHandler implements JspXmlExtConfig { 
    public static final String WEBAPP_DTD_PUBLIC_ID_22 = "-//Sun Microsystems, Inc.//DTD Web Application 2.2//EN";
    public static final String WEBAPP_DTD_RESOURCE_PATH_22 = "/javax/servlet/resources/web-app_2_2.dtd";
    public static final String WEBAPP_DTD_PUBLIC_ID_23 = "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN";
    public static final String WEBAPP_DTD_RESOURCE_PATH_23 = "/javax/servlet/resources/web-app_2_3.dtd";
    public static final String WEBAPP_DTD_PUBLIC_ID_24 = "http://java.sun.com/xml/ns/j2ee web-app_2_4.xsd";
    public static final String WEBAPP_DTD_RESOURCE_PATH_24 = "/javax/servlet/resources/web-app_2_4.xsd";
    
    protected SAXParser saxParser = null;
    protected boolean isServlet24 = false;
    protected boolean isServlet24_or_higher = false;
    protected boolean isServlet25_or_higher = false; //need this to know whether to process #{} in template text
    
    protected StringBuffer chars = null;
    protected HashMap tagLibMap = new HashMap();
    protected String taglibUri = null;
    protected String taglibLocation = null;
    protected List jspPropertyGroups = new ArrayList();
    protected JspConfigPropertyGroup jspConfigPropertyGroup = null;
	protected List jspFileExtensions = new ArrayList();
    protected JspOptions options = null;
    //defect 400645
    protected Properties webConProperties = new Properties();
    private boolean JCDIEnabled = false;
    
    public WebXmlParser(File outputDir) throws JspCoreException {
        try {
            options = new JspOptions(new java.util.Properties());
            if (outputDir == null) {
                String dir = System.getProperty("java.io.tmpdir");
                if (dir != null)
                    outputDir = new File(dir);
            }
            options.setOutputDir(outputDir.getCanonicalFile());
			saxParser = ParserFactory.newSAXParser(true, true);
		}
        catch (ParserConfigurationException e) {
            throw new JspCoreException(e);
        }
        catch (SAXException e) {
            throw new JspCoreException(e);
        }
        catch (IOException e) {
            throw new JspCoreException(e);
        }
    }
    
    public void parseWebXml(InputStream is) throws JspCoreException {
        tagLibMap.clear();
        jspPropertyGroups.clear();
        if (is!=null) {
            try {
                ParserFactory.parseDocument(saxParser, is, this);
            }
            catch (SAXException e) {
                if (e.getCause() != null)
                    throw new JspCoreException(e.getCause());
                else
                    throw new JspCoreException(e);
                
            }
            catch (IOException e) {
                throw new JspCoreException(e);
            }
            finally {
                try {
                    is.close();
                } catch (IOException e) {}
            }
        }            
    } 

    public Map getTagLibMap() {
        return (new HashMap(tagLibMap));
    }
    
    public List getJspPropertyGroups() {
        return (new ArrayList(jspPropertyGroups));
    }
    
    public boolean isServlet24() {
        return (isServlet24);
    }
    
    public boolean isServlet24_or_higher() {
        return (isServlet24_or_higher);
    }
    
    public boolean isServlet25_or_higher() {
        return (isServlet25_or_higher);
    }

    public JspOptions getJspOptions() {
        return options;
    }        
    
    private double getVersion(String v) {
        if (v != null) {
            try {
                return Double.parseDouble(v);
            } catch (NumberFormatException e) {
            }
        }
        return 2.3;
    }
    public void startElement(String namespaceURI, 
                             String localName,
                             String elementName, 
                             Attributes attrs) 
        throws SAXException {
        chars = new StringBuffer();
        if (elementName.equals("web-app")) {
        	String version = attrs.getValue("version");
            if (version != null) {
            	if (version.equals("2.4")) {
            		isServlet24 = true;
            	}
            	if (getVersion(version) >= 2.4) {
            		isServlet24_or_higher = true;
            	}
                if (getVersion(version) >=2.5) {
                    isServlet25_or_higher = true;
                }
            }
        }
        else if (elementName.equals("jsp-property-group")) {
            jspConfigPropertyGroup = new JspConfigPropertyGroup();    
        }
    }
    
    public void characters(char[] ch, int start, int length) throws SAXException {
        for (int i = 0; i < length; i++) {
            if (chars != null)
                chars.append(ch[start+i]);
        }
    }

    public void endElement(String namespaceURI,
                           String localName,
                           String elementName)
        throws SAXException {
        if (elementName.equals("taglib")) {
            tagLibMap.put(taglibUri, taglibLocation);
            taglibUri = null;
            taglibLocation = null;    
        }
        else if (elementName.equals("taglib-uri")) {
            taglibUri = chars.toString().trim();
        }
        else if (elementName.equals("taglib-location")) {
            taglibLocation = chars.toString().trim();
        }
        else if (elementName.equals("jsp-property-group")) {
            jspPropertyGroups.add(jspConfigPropertyGroup);
            jspConfigPropertyGroup = null;
            isServlet24 = true;
        }
        else if (elementName.equals("url-pattern")) {
            if (jspConfigPropertyGroup != null)
                jspConfigPropertyGroup.addUrlPattern(chars.toString().trim());
        }
        else if (elementName.equals("el-ignored")) {
            if (jspConfigPropertyGroup != null) {
                jspConfigPropertyGroup.add(new JspConfigProperty(JspConfigProperty.EL_IGNORED_TYPE, 
                                                                 new Boolean(chars.toString().trim())));
                boolean elig=new Boolean(chars.toString().trim()).booleanValue();
                if (elig) {
                    jspConfigPropertyGroup.add(new JspConfigProperty(JspConfigProperty.EL_IGNORED_SET_TRUE_TYPE, 
                            new Boolean(true)));                	
                }
            }
        }
        else if (elementName.equals("scripting-invalid")) {
            if (jspConfigPropertyGroup != null)
                jspConfigPropertyGroup.add(new JspConfigProperty(JspConfigProperty.SCRIPTING_INVALID_TYPE, 
                                                                 new Boolean(chars.toString().trim())));
        }
        else if (elementName.equals("include-prelude")) {
            if (jspConfigPropertyGroup != null)
                jspConfigPropertyGroup.add(new JspConfigProperty(JspConfigProperty.PRELUDE_TYPE, 
                                                                 chars.toString().trim()));
        }
        else if (elementName.equals("include-coda")) {
            if (jspConfigPropertyGroup != null)
                jspConfigPropertyGroup.add(new JspConfigProperty(JspConfigProperty.CODA_TYPE, 
                                                                 chars.toString().trim()));
        }
        else if (elementName.equals("is-xml")) {
            if (jspConfigPropertyGroup != null)
                jspConfigPropertyGroup.add(new JspConfigProperty(JspConfigProperty.IS_XML_TYPE, 
                                                                 new Boolean(chars.toString().trim())));
        }
        // jsp2.1work
        else if (elementName.equals("trim-directive-whitespaces")) {
            if (jspConfigPropertyGroup != null)
                jspConfigPropertyGroup.add(new JspConfigProperty(JspConfigProperty.TRIM_DIRECTIVE_WHITESPACES_TYPE, 
                                                                 new Boolean(chars.toString().trim())));
        }
        // jsp2.1ELwork
        else if (elementName.equals("deferred-syntax-allowed-as-literal")) {
            if (jspConfigPropertyGroup != null)
                jspConfigPropertyGroup.add(new JspConfigProperty(JspConfigProperty.DEFERRED_SYNTAX_ALLOWED_AS_LITERAL_TYPE, 
                                                                 new Boolean(chars.toString().trim())));
        }
        //jsp2.1MR2work
        else if (elementName.equals("default-content-type")) {
            if (jspConfigPropertyGroup != null)
                jspConfigPropertyGroup.add(new JspConfigProperty(JspConfigProperty.DEFAULT_CONTENT_TYPE, 
                                                                 chars.toString().trim()));
        }
        //jsp2.1MR2work
        else if (elementName.equals("buffer")) {
            if (jspConfigPropertyGroup != null)
                jspConfigPropertyGroup.add(new JspConfigProperty(JspConfigProperty.BUFFER, 
                												chars.toString().trim()));
        }
        //jsp2.1MR2work
        else if (elementName.equals("error-on-undeclared-namespace")) {
            if (jspConfigPropertyGroup != null)
                jspConfigPropertyGroup.add(new JspConfigProperty(JspConfigProperty.ERROR_ON_UNDECLARED_NAMESPACE, 
                												new Boolean(chars.toString().trim())));
        }
        else if (elementName.equals("page-encoding")) {
            if (jspConfigPropertyGroup != null)
                jspConfigPropertyGroup.add(new JspConfigProperty(JspConfigProperty.PAGE_ENCODING_TYPE, 
                                                                 chars.toString().trim()));
        }
        chars = null;
    }    
    
    public InputSource resolveEntity(String publicId, String systemId)
        throws SAXException {
        InputSource isrc = null;
        String resourcePath = null;            
        if (publicId.equals(WEBAPP_DTD_PUBLIC_ID_22)) {
            resourcePath = WEBAPP_DTD_RESOURCE_PATH_22;
        }
        else if (publicId.equals(WEBAPP_DTD_PUBLIC_ID_23)) {
            resourcePath = WEBAPP_DTD_RESOURCE_PATH_23;
        }
        else if (publicId.equals(WEBAPP_DTD_PUBLIC_ID_24)) {
            resourcePath = WEBAPP_DTD_RESOURCE_PATH_24;
            isServlet24 = true;
        }
        if (resourcePath != null) {
            InputStream input = this.getClass().getResourceAsStream(resourcePath);
            if (input == null) {
                System.out.println("publicId = " + publicId);
                System.out.println("resourcePath = " + resourcePath);
                throw new SAXException("jsp error internal dtd not found");
            }
            isrc = new InputSource(input);
        }
        return isrc;
    }

	public List getJspFileExtensions() {
		return this.jspFileExtensions;
	}

    public boolean containsServletClassName(String servletClassName) {
        return false;
    }
    
    //defect 400645
    public void setWebContainerProperties(Properties webConProperties) {
    	this.webConProperties=webConProperties;
    }
    
    public Properties getWebContainerProperties() {
    	return webConProperties;
    }
    //defect 400645

    @Override
    public boolean isJCDIEnabledForRuntimeCheck() {
        return JCDIEnabled;
    }

    @Override
    public void setJCDIEnabledForRuntimeCheck(boolean b) {
        JCDIEnabled = b;        
    }
}
