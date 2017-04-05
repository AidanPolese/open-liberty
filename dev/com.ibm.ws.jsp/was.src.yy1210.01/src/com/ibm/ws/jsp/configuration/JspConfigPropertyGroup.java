//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.

package com.ibm.ws.jsp.configuration;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

import javax.servlet.descriptor.JspPropertyGroupDescriptor;

public class JspConfigPropertyGroup extends ArrayList implements JspPropertyGroupDescriptor {
    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3257289123571511350L;
	protected List<String> urlPatterns = new ArrayList<String>();
	private boolean conversion=false;
	private String buffer;
	private String defaultContentType;
	private String deferredSyntaxAllowedAsLiteral;
	private String elIgnored;
	private String errorOnUndeclaredNamespace;
	private Collection<String> includeCodas = new ArrayList<String>();
	private Collection<String> includePreludes = new ArrayList<String>();;
	private String isXml;
	private String pageEncoding;
	private String scriptingInvalid;
	private String trimDirectiveWhitespaces;
    //private Collection<String> urlPatterns already exists
	
    public JspConfigPropertyGroup() {
    }
    
    public List<String> getUrlPatterns() {
        return (urlPatterns);
    }
    
    public void addUrlPattern(String urlPattern) {
        urlPatterns.add(urlPattern);
    }

    private void convertThis(JspConfigPropertyGroup thisObject) {
        synchronized(thisObject) {
            if (!thisObject.conversion) {
                for (Object o:this) { //going through the list of what was added to this as an arrayList
                    JspConfigProperty myJspProperty = (JspConfigProperty)o; 
                    int type = myJspProperty.getType();
                    switch (type) {
                        case JspConfigProperty.BUFFER:
                            buffer = myJspProperty.getValue().toString();
                            break;
                        case JspConfigProperty.CODA_TYPE:
                            includeCodas.add(myJspProperty.getValue().toString());
                            break;
                        case JspConfigProperty.DEFAULT_CONTENT_TYPE:
                            defaultContentType = myJspProperty.getValue().toString();
                            break;
                        case JspConfigProperty.DEFERRED_SYNTAX_ALLOWED_AS_LITERAL_TYPE:
                            deferredSyntaxAllowedAsLiteral=myJspProperty.getValue().toString();
                            break;
                        /*case JspConfigProperty.EL_IGNORED_SET_TRUE_TYPE:
                            (String)myJspProperty.getValue();
                            break;*/
                        case JspConfigProperty.EL_IGNORED_TYPE:
                            elIgnored = myJspProperty.getValue().toString();
                            break;
                        case JspConfigProperty.ERROR_ON_UNDECLARED_NAMESPACE:
                            errorOnUndeclaredNamespace = myJspProperty.getValue().toString();
                            break;
                        case JspConfigProperty.IS_XML_TYPE:
                            isXml = myJspProperty.getValue().toString();
                            break;
                        case JspConfigProperty.PAGE_ENCODING_TYPE:
                            pageEncoding = myJspProperty.getValue().toString();
                            break;
                        case JspConfigProperty.PRELUDE_TYPE:
                            includePreludes.add(myJspProperty.getValue().toString());
                            break;
                        case JspConfigProperty.SCRIPTING_INVALID_TYPE:
                            scriptingInvalid = myJspProperty.getValue().toString();
                            break;
                        case JspConfigProperty.TRIM_DIRECTIVE_WHITESPACES_TYPE:
                            trimDirectiveWhitespaces = myJspProperty.getValue().toString();
                            break;
                    }
                }
                thisObject.conversion=true;                
            }
        }
    }
    
    @Override
    public String getBuffer() {
        if (!conversion) {
            convertThis(this);
        }
        return buffer;
    }

    @Override
    public String getDefaultContentType() {
        if (!conversion) {
            convertThis(this);
        }
        return defaultContentType;
    }

    @Override
    public String getDeferredSyntaxAllowedAsLiteral() {
        if (!conversion) {
            convertThis(this);
        }
        return deferredSyntaxAllowedAsLiteral;
    }

    @Override
    public String getElIgnored() {
        if (!conversion) {
            convertThis(this);
        }
        return elIgnored;
    }

    @Override
    public String getErrorOnUndeclaredNamespace() {
        if (!conversion) {
            convertThis(this);
        }
        return errorOnUndeclaredNamespace;
    }

    @Override
    public Collection<String> getIncludeCodas() {
        if (!conversion) {
            convertThis(this);
        }
        return includeCodas;
    }

    @Override
    public Collection<String> getIncludePreludes() {
        if (!conversion) {
            convertThis(this);
        }
        return includePreludes;
    }

    @Override
    public String getIsXml() {
        if (!conversion) {
            convertThis(this);
        }
        return isXml;
    }

    @Override
    public String getPageEncoding() {
        if (!conversion) {
            convertThis(this);
        }
        return pageEncoding;
    }

    @Override
    public String getScriptingInvalid() {
        if (!conversion) {
            convertThis(this);
        }
        return scriptingInvalid;
    }

    @Override
    public String getTrimDirectiveWhitespaces() {
        if (!conversion) {
            convertThis(this);
        }
        return trimDirectiveWhitespaces;
    }
}
