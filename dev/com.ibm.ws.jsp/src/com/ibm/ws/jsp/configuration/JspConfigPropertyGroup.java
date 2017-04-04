//IBM Confidential OCO Source Material
//5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.

package com.ibm.ws.jsp.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.descriptor.JspPropertyGroupDescriptor;

import com.ibm.ws.javaee.dd.jsp.JSPPropertyGroup;

public class JspConfigPropertyGroup implements JspPropertyGroupDescriptor {
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3257289123571511350L;
    protected List<String> urlPatterns = new ArrayList<String>();
    private JSPPropertyGroup propertyGroup;

    //private Collection<String> urlPatterns already exists

    public JspConfigPropertyGroup() {}

    public JspConfigPropertyGroup(JSPPropertyGroup propertyGroup) {
        this.propertyGroup = propertyGroup;
    }

    @Override
    public List<String> getUrlPatterns() {
        return propertyGroup.getURLPatterns();
    }

    @Override
    public String getBuffer() {
        return propertyGroup.getBuffer();
    }

    @Override
    public String getDefaultContentType() {
        return propertyGroup.getDefaultContentType();
    }

    @Override
    public String getDeferredSyntaxAllowedAsLiteral() {
        if(propertyGroup.isSetDeferredSyntaxAllowedAsLiteral()){
            return Boolean.toString(propertyGroup.isDeferredSyntaxAllowedAsLiteral());
        } else {
            return null;
        }
    }

    @Override
    public String getElIgnored() {
        if(propertyGroup.isSetElIgnored()){
            return Boolean.toString(propertyGroup.isElIgnored());
        } else {
            return null;
        }
    }

    @Override
    public String getErrorOnUndeclaredNamespace() {
        if(propertyGroup.isSetErrorOnUndeclaredNamespace()){
            return Boolean.toString(propertyGroup.isErrorOnUndeclaredNamespace());
        } else {
            return null;
        }
    }

    @Override
    public Collection<String> getIncludeCodas() {
        return propertyGroup.getIncludeCodas();
    }

    @Override
    public Collection<String> getIncludePreludes() {
        return propertyGroup.getIncludePreludes();
    }

    @Override
    public String getIsXml() {
        if(propertyGroup.isSetIsXml()){
            return Boolean.toString(propertyGroup.isIsXml());
        } else {
            return null;
        }
    }

    @Override
    public String getPageEncoding() {
        return propertyGroup.getPageEncoding();
    }

    @Override
    public String getScriptingInvalid() {
        if(propertyGroup.isSetScriptingInvalid()){
            return Boolean.toString(propertyGroup.isScriptingInvalid());
        } else {
            return null;
        }
    }

    @Override
    public String getTrimDirectiveWhitespaces() {
        if(propertyGroup.isSetTrimDirectiveWhitespaces()){
            return Boolean.toString(propertyGroup.isTrimDirectiveWhitespaces());
        } else {
            return null;
        }
    }
}
