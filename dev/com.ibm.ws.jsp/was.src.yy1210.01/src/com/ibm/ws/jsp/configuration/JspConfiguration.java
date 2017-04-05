//IBM Confidential OCO Source Material
//5724-i63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997-2004
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
/*
* Change history:
* defect 223226 "GVT: DBCS in included page is displayed as ???"  2004/08/11  Scott Johnson
* feature LIDB4147-9 "Integrate Unified Expression Language"  2006/08/14  Scott Johnson
* 395182.2  70FVT: make servlet 2.3 compatible with JSP 2.1 for migration 2007/02/07 Scott Johnson
*/



package com.ibm.ws.jsp.configuration;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.el.ExpressionFactory;

public class JspConfiguration { 
	static protected Logger logger;
	private static final String CLASS_NAME="com.ibm.ws.jsp.configuration.JspConfiguration";
	static{
		logger = Logger.getLogger("com.ibm.ws.jsp");
	}
    
    public static final Float twoPointThree = new Float(2.3);
    public static final Float twoPointFour = new Float(2.4);
    public static final Float twoPointFive = new Float(2.5);
    public static final Float onePointTwo = new Float(1.2);
    public static final Float twoPointZero = new Float(2.0);
    public static final Float twoPointOne = new Float(2.1);
    
    //Going to use the servletVersion property in this configuration object to handle spec tightening issues
    //This way we don't have to flag differences
    protected String servletVersion = "2.5"; //default
    protected String jspVersion = "2.1"; //default
    protected boolean jspVersionSet = false;
    protected String responseEncoding = null;	//default to null
    											//added to handle response encodings specified in page directives or BOM    
    protected boolean isXml = false;
    protected boolean isXmlSpecified = false;
    protected boolean elIgnored = true;
    protected boolean elIgnoredSetTrueInPropGrp = false;
    protected boolean elIgnoredSetTrueInPage = false;
    protected boolean scriptingInvalid = false;
    protected String pageEncoding = null;
    protected ArrayList preludeList = new ArrayList();
    protected ArrayList codaList = new ArrayList();
    protected String trimDirectiveWhitespacesValue = null; // jsp2.1work
    protected String deferredSyntaxAllowedAsLiteralValue = null; // jsp2.1ELwork
    protected boolean trimDirectiveWhitespaces = false; // jsp2.1work
    protected boolean deferredSyntaxAllowedAsLiteral = false; // jsp2.1ELwork
    
    protected String defaultContentType = null; // jsp2.1MR2work
    protected String buffer = null; // jsp2.1MR2work
    protected boolean errorOnUndeclaredNamespace = false; // jsp2.1MR2work    
    
    protected JspConfigurationManager configManager = null;
    private ExpressionFactory expressionFactory = ExpressionFactory.newInstance(); //LIDB4147-9
    private ExpressionFactory jcdiWrappedExpressionFactory = null; // only set if jcdi enabled
    
    protected JspConfiguration(JspConfigurationManager configManager) {
		if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINER)){
			logger.logp(Level.FINER, CLASS_NAME, "JspConfiguration", "default values isXml = [{0}] isXmlSpecified = [{1}] elIgnored = [{2}] scriptingInvalid = [{3}] pageEncoding = [{4}] trimDirectiveWhitespacesValue = [{5}] deferredSyntaxAllowedAsLiteralValue = [{6}] trimDirectiveWhitespaces = [{7}] deferredSyntaxAllowedAsLiteral = [{8}] elIgnoredSetTrueInPropGrp = [{9}] elIgnoredSetTrueInPage = [{10}]", 
			new Object[] {new Boolean(isXml), new Boolean(isXmlSpecified), new Boolean(elIgnored), new Boolean(scriptingInvalid), pageEncoding, trimDirectiveWhitespacesValue, deferredSyntaxAllowedAsLiteralValue, trimDirectiveWhitespaces, deferredSyntaxAllowedAsLiteral,elIgnoredSetTrueInPropGrp,elIgnoredSetTrueInPage  });
		} 
        this.configManager = configManager;
    }
    
    protected JspConfiguration(JspConfigurationManager configManager,
                               String servletVersion,
                               String jspVersion,
                               boolean isXml, 
                               boolean isXmlSpecified,
                               boolean elIgnored, 
                               boolean scriptingInvalid,
                               boolean trimDirectiveWhitespaces, // jsp2.1work
                               boolean deferredSyntaxAllowedAsLiteral, // jsp2.1ELwork
                               String trimDirectiveWhitespacesValue, // jsp2.1work
                               String deferredSyntaxAllowedAsLiteralValue, // jsp2.1ELwork
                               boolean elIgnoredSetTrueInPropGrp,
                               boolean elIgnoredSetTrueInPage,
                               String defaultContentType,
                               String buffer,
                               boolean errorOnUndeclaredNamespace
                               ) {
		
		if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINER)){
			logger.logp(Level.FINER, CLASS_NAME, "JspConfiguration", "isXml = [{0}] isXmlSpecified = [{1}] elIgnored = [{2}] scriptingInvalid = [{3}] pageEncoding = [{4}] trimDirectiveWhitespacesValue = [{5}] deferredSyntaxAllowedAsLiteralValue = [{6}] trimDirectiveWhitespaces = [{7}] deferredSyntaxAllowedAsLiteral = [{8}] elIgnoredSetTrueInPropGrp = [{9}] elIgnoredSetTrueInPage = [{10}]", 
						new Object[] {new Boolean(isXml), new Boolean(isXmlSpecified), new Boolean(elIgnored), new Boolean(scriptingInvalid), pageEncoding, trimDirectiveWhitespacesValue, deferredSyntaxAllowedAsLiteralValue, trimDirectiveWhitespaces, deferredSyntaxAllowedAsLiteral, elIgnoredSetTrueInPropGrp, elIgnoredSetTrueInPage});
		}
        this.servletVersion=servletVersion;
        this.jspVersion=jspVersion;
        this.isXml = isXml;
        this.isXmlSpecified = isXmlSpecified;
        this.elIgnored = elIgnored;
        this.scriptingInvalid = scriptingInvalid;         	
        this.trimDirectiveWhitespaces = trimDirectiveWhitespaces;// jsp2.1work
        this.deferredSyntaxAllowedAsLiteral = deferredSyntaxAllowedAsLiteral; // jsp2.1ELwork
        this.trimDirectiveWhitespacesValue = trimDirectiveWhitespacesValue;// jsp2.1work
        this.deferredSyntaxAllowedAsLiteralValue = deferredSyntaxAllowedAsLiteralValue; // jsp2.1ELwork
        this.configManager = configManager;
        this.elIgnoredSetTrueInPropGrp = elIgnoredSetTrueInPropGrp;
        this.elIgnoredSetTrueInPage = elIgnoredSetTrueInPage;
        this.defaultContentType = defaultContentType;
        this.buffer = buffer;
        this.errorOnUndeclaredNamespace = errorOnUndeclaredNamespace; 
    }
    
    //This method is used for creating a configuration for a tag file.  The tag file may want to override some properties if it's jsp version in the tld is different than the server version
    public JspConfiguration createClonedJspConfiguration() {
        return new JspConfiguration(configManager, this.getServletVersion(), this.jspVersion, this.isXml, this.isXmlSpecified, this.elIgnored, this.scriptingInvalid(), this.isTrimDirectiveWhitespaces(), this.isDeferredSyntaxAllowedAsLiteral(), this.getTrimDirectiveWhitespaces(), this.getDeferredSyntaxAllowedAsLiteral(), this.elIgnoredSetTrueInPropGrp(), this.elIgnoredSetTrueInPage(), this.getDefaultContentType(), this.getBuffer(), this.isErrorOnUndeclaredNamespace());
    }
    
    public JspConfiguration createEmptyJspConfiguration() {
        return configManager.createJspConfiguration();
    }
    
    public void addIncludePrelude(String prelude) {
        preludeList.add(prelude);
    }
    
    public void addIncludeCoda(String coda) {
        codaList.add(coda);
    }
    
    public boolean isXml() {
        return (isXml);
    }
    
    public boolean elIgnored() {
        return (elIgnored);
    }
    
    public boolean scriptingInvalid() {
        return (scriptingInvalid);
    }
    
    // jsp2.1work
    public String getTrimDirectiveWhitespaces() {
        return (trimDirectiveWhitespacesValue);
    }
    
    // jsp2.1ELwork
    public String getDeferredSyntaxAllowedAsLiteral() {
        return (deferredSyntaxAllowedAsLiteralValue);
    }
    
    // jsp2.1work
    public boolean isTrimDirectiveWhitespaces() {
        return (trimDirectiveWhitespaces);
    }
    
    // jsp2.1ELwork
    public boolean isDeferredSyntaxAllowedAsLiteral() {
        return (deferredSyntaxAllowedAsLiteral);
    }
    
    public boolean elIgnoredSetTrueInPropGrp() {
        return (elIgnoredSetTrueInPropGrp);
    }
    
    public boolean elIgnoredSetTrueInPage() {
        return (elIgnoredSetTrueInPage);
    }
    
    public String getPageEncoding() {
        return (pageEncoding);
    }
    
    public ArrayList getPreludeList() {
        return (preludeList);
    }
    
    public ArrayList getCodaList() {
        return (codaList);
    }
    
    public void setElIgnored(boolean elIgnored) {
        this.elIgnored = elIgnored;
    }
    
    public void setElIgnoredSetTrueInPropGrp(boolean elIgnoredSetTrueInPropGrp) {
        this.elIgnoredSetTrueInPropGrp = elIgnoredSetTrueInPropGrp;
    }
    
    public void setElIgnoredSetTrueInPage(boolean elIgnoredSetTrueInPage) {
        this.elIgnoredSetTrueInPage = elIgnoredSetTrueInPage;
    }
    
    // jsp2.1work
    public void setTrimDirectiveWhitespaces(String trimDirectiveWhitespaces) {
        this.trimDirectiveWhitespacesValue = trimDirectiveWhitespaces;
    }
    
    // jsp2.1ELwork
    public void setDeferredSyntaxAllowedAsLiteral(boolean deferredSyntaxAllowedAsLiteral) {
        this.deferredSyntaxAllowedAsLiteral = deferredSyntaxAllowedAsLiteral;
    }
    
    // jsp2.1work
    public void setTrimDirectiveWhitespaces(boolean trimDirectiveWhitespaces) {
        this.trimDirectiveWhitespaces = trimDirectiveWhitespaces;
    }
    
    // jsp2.1ELwork
    public void setDeferredSyntaxAllowedAsLiteral(String deferredSyntaxAllowedAsLiteral) {
        this.deferredSyntaxAllowedAsLiteralValue = deferredSyntaxAllowedAsLiteral;
    }
    
    // jsp2.1MR2work
	public String getDefaultContentType() {
		return defaultContentType;
	}
    // jsp2.1MR2work
	public void setDefaultContentType(String defaultContentType) {
		this.defaultContentType = defaultContentType;
	}
    // jsp2.1MR2work
	public String getBuffer() {
		return buffer;
	}
    // jsp2.1MR2work
	public void setBuffer(String buffer) {
		this.buffer = buffer;
	}
    // jsp2.1MR2work
	public boolean isErrorOnUndeclaredNamespace() {
		return errorOnUndeclaredNamespace;
	}
    // jsp2.1MR2work
	public void setErrorOnUndeclaredNamespace(boolean errorOnUndeclaredNamespace) {
		this.errorOnUndeclaredNamespace = errorOnUndeclaredNamespace;
	}

    public void setIsXml(boolean isXml) {
        this.isXml = isXml;
        this.isXmlSpecified = true;
    }

    public void setPageEncoding(String pageEncoding) {
    	if (pageEncoding != null){
			this.pageEncoding = com.ibm.wsspi.webcontainer.util.EncodingUtils.getJvmConverter(pageEncoding);
			if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINER)){
				if( this.pageEncoding.equalsIgnoreCase(pageEncoding) == false){
					logger.logp(Level.FINER, CLASS_NAME, "setPageEncoding", "specified pageEncoding [{0}] was converted to [{1}] by WebSphere JVM converter", 
								new Object[] {pageEncoding, this.pageEncoding});
				}
			}
    	}
		else 
			this.pageEncoding = null;        	
    }

    public void setScriptingInvalid(boolean scriptingInvalid) {
        this.scriptingInvalid = scriptingInvalid;
    }
    
    public boolean isXmlSpecified() {
        return isXmlSpecified;
    }
    
    public JspConfigurationManager getConfigManager() {
        return configManager;
    }
    
    //LIDB4147-9 Begin
    public ExpressionFactory getExpressionFactory() {
        //allow JCDI to wrap our expression factory so they can clean up objects after the expressions are through
        if (configManager.isJCDIEnabled()) {
            //wrap expressionFactory
            if (jcdiWrappedExpressionFactory==null) {
                jcdiWrappedExpressionFactory = new org.apache.webbeans.el.WrappedExpressionFactory(expressionFactory);
            }
            return jcdiWrappedExpressionFactory;
        } else {
            return expressionFactory;
        }
    }
    
    public String toString(){
		String separatorString = System.getProperty("line.separator");
    	
		return new String (""+separatorString+
			"isXml =                       	      [" + isXml +"]"+separatorString+
			"isXmlSpecified =              		  [" + isXmlSpecified +"]"+separatorString+
		 	"elIgnored =                          [" + elIgnored +"]"+separatorString+
		 	"elIgnoredSetTrueInPropGrp =          [" + elIgnoredSetTrueInPropGrp +"]"+separatorString+
		 	"elIgnoredSetTrueInPage =             [" + elIgnoredSetTrueInPage +"]"+separatorString+
		 	"scriptingInvalid              		  [" + scriptingInvalid +"]"+separatorString+
			"pageEncoding                  		  [" + pageEncoding +"]"+separatorString+
		 	"preludeList                   		  [" + preludeList +"]"+separatorString+
			"codaList                      		  [" + codaList +"]"+separatorString +
			"trimDirectiveWhitespacesValue        [" + trimDirectiveWhitespacesValue +"]"+separatorString+ // jsp2.1work 
			"deferredSyntaxAllowedAsLiteralValue  [" + deferredSyntaxAllowedAsLiteralValue +"]"+separatorString+ // jsp2.1ELwork 
			"trimDirectiveWhitespaces      		  [" + trimDirectiveWhitespaces +"]"+separatorString+ // jsp2.1work 
			"deferredSyntaxAllowedAsLiteral		  [" + deferredSyntaxAllowedAsLiteral +"]"+separatorString+ // jsp2.1ELwork
            "defaultContentType                   [" + defaultContentType +"]"+separatorString+ // jsp2.1MR2work
            "buffer                               [" + buffer +"]"+separatorString+ // jsp2.1MR2work
            "errorOnUndeclaredNamespace           [" + errorOnUndeclaredNamespace +"]"+separatorString // jsp2.1MR2work
			);
    }

    public String getServletVersion() {
        return servletVersion;
    }

    public void setServletVersion(String servletVersion) {
        this.servletVersion = servletVersion;
        try {
            Float f = Float.valueOf(servletVersion);
            if (f<=twoPointThree) {
                jspVersion="1.2";
                elIgnored=true;
                deferredSyntaxAllowedAsLiteral=true;
            } else if (f<=twoPointFour){
                jspVersion="2.0";
                deferredSyntaxAllowedAsLiteral=true;
            } //else if (f>=twoPointFive){
                //do nothing ... defaults ok
            //}
        } catch (Exception e) {
            
        }
    }
    
    public String getJspVersion() {
        return jspVersion;
    }
    
    public void setJspVersion(String jspVersion) {
        this.jspVersion = jspVersion;
        this.jspVersionSet=true;
        try {
            Float f = Float.valueOf(jspVersion);
            if (f<=onePointTwo) {
                //Setting elIgnored to true is a regression for tags at the 1.2 level in a 2.4 app
                //elIgnored=true;
                deferredSyntaxAllowedAsLiteral=true;
            } else if (f<=twoPointZero){
                deferredSyntaxAllowedAsLiteral=true;
            } //else if (f>=twoPointOne){
                //do nothing ... defaults ok
            //}
        } catch (Exception e) {
            
        }
    }
    
    public boolean isJspVersionSet() {
        return jspVersionSet;
    }
    
    public String getResponseEncoding(){
    	return responseEncoding;
    }
    
    public void setResponseEncoding(String responseEncoding){
    	if (responseEncoding != null){
			this.responseEncoding = com.ibm.wsspi.webcontainer.util.EncodingUtils.getJvmConverter(responseEncoding);
			if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINER)){
				if( this.responseEncoding.equalsIgnoreCase(responseEncoding) == false){
					logger.logp(Level.FINER, CLASS_NAME, "setresponseEncoding", "responseEncoding [{0}] was converted to [{1}] by WebSphere JVM converter", 
								new Object[] {responseEncoding, this.responseEncoding});
				}
			}
    	}
		else {
			this.responseEncoding = null;
		}
    }
    
    
    
}
