//IBM Confidential OCO Source Material
//  5724-i63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//  The source code for this program is not published or otherwise divested
//  of its trade secrets, irrespective of what has been deposited with the
//  U.S. Copyright Office.
//
//  Revisions:
//  Defect 213290  2004/06/30  Allow tagfile attributes to be specified as "[L<classname>;"
//  Defect PK03712 2005/05/20  URLEncoded object not converted to String
//  feature LIDB4147-9 "Integrate Unified Expression Language"  2006/08/14  Scott Johnson
//  jsp2.1work
//  Defect 395182.2  70FVT: make servlet 2.3 compatible with JSP 2.1 for migration 2007/02/07 Scott Johnson
//  Feature 4147-24 "JSP 2.1 upgrade: Incorporate resource injection engine"  2007/03/30 Curtiss Howard
//  Feature LIDB3292-43 "Integrate AMM with webcontainer" 2007/10/12 cjhoward
//  Defect PK65013 2008/07/07  Need ability to customize pageContext variable.
//  Defect  PK81147 2009/02/25  fix NPE when customer is using jsp page attribute "extends"
//  Defect PM06063 2010/01/26 pmdinh    Add a flag to disable Feature 4147-24 "JSP 2.1 upgrade: Incorporate resource injection engine"
//  Defect  PM21395 09/02/2010  pmdinh      Decode double quote in the attribute of a tag script

package com.ibm.ws.jsp.translator.visitor.generator;

import java.io.CharArrayWriter;
import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.jsp.tagext.TagAttributeInfo;

import org.w3c.dom.Element;

import com.ibm.ws.jsp.Constants;
import com.ibm.ws.jsp.JspCoreException;
import com.ibm.ws.jsp.JspOptions;
import com.ibm.ws.jsp.configuration.JspConfiguration;
import com.ibm.ws.jsp.translator.utils.FunctionSignature;
import com.ibm.ws.jsp.translator.utils.JspTranslatorUtil;
import com.ibm.ws.jsp.translator.visitor.validator.ValidateJspResult;
import com.ibm.ws.jsp.translator.visitor.validator.ValidateResult;
import com.ibm.wsspi.jsp.context.JspCoreContext;

public class GeneratorUtils {
	static private Logger logger;
	private static final String CLASS_NAME="com.ibm.ws.jsp.translator.visitor.generator.GeneratorUtils";
	static{
		logger = Logger.getLogger("com.ibm.ws.jsp");
	}
	
	public static String classfileVersion="unknown";
	public static String fullClassfileInformation="unknown";
	public static int TAG_FILE_TYPE=1;
	public static int JSP_FILE_TYPE=2;
	
	//PM21395 
    public static String quote(String s) {
        return quote(s, false);
    }
    //PM21395 

    public static String quote(String s, boolean flag) {
        if (s == null)
            return "null";
        //PM21395 starts
        if (flag){
            s = s.replaceAll("&quot;", "\"");
        }
        //PM21395 ends

        return '"' + escape(s) + '"';
    }

    public static String escape(String s) {

        if (s == null)
            return "";

        StringBuffer b = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '"')
                b.append('\\').append('"');
            else if (c == '\\')
                b.append('\\').append('\\');
            else if (c == '\n')
                b.append('\\').append('n');
            else if (c == '\r')
                b.append('\\').append('r');
            else
                b.append(c);
        }
        return b.toString();
    }

    public static String toGetterMethod(String attrName) {
        char[] attrChars = attrName.toCharArray();
        attrChars[0] = Character.toUpperCase(attrChars[0]);
        return "get" + new String(attrChars) + "()";
    }

    public static String toSetterMethodName(String attrName) {
        char[] attrChars = attrName.toCharArray();
        attrChars[0] = Character.toUpperCase(attrChars[0]);
        return "set" + new String(attrChars);
    }

    //  PK65013 - already know if this isTagFile or not and pageContextVar is set accordingly
    public static void generateLocalVariables(JavaCodeWriter out, Element jspElement, String pageContextVar) throws JspCoreException {
        if (hasUseBean(jspElement)) {
            out.println("HttpSession session = "+pageContextVar+".getSession();");
            out.println("ServletContext application = "+pageContextVar+".getServletContext();");
        }
        if (hasUseBean(jspElement) || hasIncludeAction(jspElement) || hasSetProperty(jspElement) || hasForwardAction(jspElement)) {
            out.println("HttpServletRequest request = (HttpServletRequest)"+pageContextVar+".getRequest();");
        }
        if (hasIncludeAction(jspElement)) {
            out.println("HttpServletResponse response = (HttpServletResponse)"+pageContextVar+".getResponse();");
        }
    }

    private static boolean hasUseBean(Element jspElement) {
        boolean b = false;
        if (jspElement.getElementsByTagNameNS(Constants.JSP_NAMESPACE, Constants.JSP_USEBEAN_TYPE).getLength() > 0)
            b = true;
        return (b);
    }

    private static boolean hasIncludeAction(Element jspElement) {
        boolean b = false;
        if (jspElement.getElementsByTagNameNS(Constants.JSP_NAMESPACE, Constants.JSP_INCLUDE_TYPE).getLength() > 0)
            b = true;
        return (b);
    }

    private static boolean hasForwardAction(Element jspElement) {
        boolean b = false;
        if (jspElement.getElementsByTagNameNS(Constants.JSP_NAMESPACE, Constants.JSP_FORWARD_TYPE).getLength() > 0)
            b = true;
        return (b);
    }

    private static boolean hasSetProperty(Element jspElement) {
        boolean b = false;
        if (jspElement.getElementsByTagNameNS(Constants.JSP_NAMESPACE, Constants.JSP_SETPROPERTY_TYPE).getLength() > 0)
            b = true;
        return (b);
    }

    public static boolean isDeferredInput(TagAttributeInfo tai) {
        return (tai != null) ? tai.isDeferredValue() : false;
    }

    public static boolean isDeferredMethodInput(TagAttributeInfo tai) {
        return (tai != null) ? tai.isDeferredMethod() : false;
    }

    public static String getTargetType(Class expectedType) {
        String targetType = expectedType.getName();
        if (expectedType.isPrimitive()) {
            if (expectedType.equals(Boolean.TYPE)) {
                targetType = Boolean.class.getName();
            }
            else if (expectedType.equals(Byte.TYPE)) {
                targetType = Byte.class.getName();
            }
            else if (expectedType.equals(Character.TYPE)) {
                targetType = Character.class.getName();
            }
            else if (expectedType.equals(Short.TYPE)) {
                targetType = Short.class.getName();
            }
            else if (expectedType.equals(Integer.TYPE)) {
                targetType = Integer.class.getName();
            }
            else if (expectedType.equals(Long.TYPE)) {
                targetType = Long.class.getName();
            }
            else if (expectedType.equals(Float.TYPE)) {
                targetType = Float.class.getName();
            }
            else if (expectedType.equals(Double.TYPE)) {
                targetType = Double.class.getName();
            }
        }
        targetType = toJavaSourceType(targetType);
    	return targetType;
    }


    public static String getPrimitiveConverterMethod(Class expectedType) {
        String primitiveConverterMethod = null;
        if (expectedType.isPrimitive()) {
            if (expectedType.equals(Boolean.TYPE)) {
                primitiveConverterMethod = "booleanValue";
            }
            else if (expectedType.equals(Byte.TYPE)) {
                primitiveConverterMethod = "byteValue";
            }
            else if (expectedType.equals(Character.TYPE)) {
                primitiveConverterMethod = "charValue";
            }
            else if (expectedType.equals(Short.TYPE)) {
                primitiveConverterMethod = "shortValue";
            }
            else if (expectedType.equals(Integer.TYPE)) {
                primitiveConverterMethod = "intValue";
            }
            else if (expectedType.equals(Long.TYPE)) {
                primitiveConverterMethod = "longValue";
            }
            else if (expectedType.equals(Float.TYPE)) {
                primitiveConverterMethod = "floatValue";
            }
            else if (expectedType.equals(Double.TYPE)) {
                primitiveConverterMethod = "doubleValue";
            }
        }
    	return primitiveConverterMethod;
    }

    /**
     * Produces a String representing a call to the EL interpreter.
     * @param expression a String containing zero or more "${}" expressions
     * @param expectedType the expected type of the interpreted result
     * @param defaultPrefix Default prefix, or literal "null"
     * @param fnmapvar Variable pointing to a function map.
     * @param XmlEscape True if the result should do XML escaping
     * @param pageContextVar Variable for PageContext variable name in generated Java code.
     * @return a String representing a call to the EL interpreter.
     */
    public static String interpreterCall(
        boolean isTagFile,
        String expression,
        Class expectedType,
        String fnmapvar,
        boolean XmlEscape,
        String pageContextVar) {  //PK65013
            
        /*
         * Determine which context object to use.
         */
        String jspCtxt = null;
        if (isTagFile)
            jspCtxt = "getJspContext()";
        else
            jspCtxt = pageContextVar;

        /*
             * Determine whether to use the expected type's textual name
         * or, if it's a primitive, the name of its correspondent boxed
         * type.
             */
        String targetType = GeneratorUtils.getTargetType(expectedType);
        String primitiveConverterMethod = GeneratorUtils.getPrimitiveConverterMethod(expectedType);

        if (primitiveConverterMethod != null) {
            XmlEscape = false;
        }

        /*
             * Build up the base call to the interpreter.
             */
        // XXX - We use a proprietary call to the interpreter for now
        // as the current standard machinery is inefficient and requires
        // lots of wrappers and adapters.  This should all clear up once
        // the EL interpreter moves out of JSTL and into its own project.
        // In the future, this should be replaced by code that calls
        // ExpressionEvaluator.parseExpression() and then cache the resulting
        // expression objects.  The interpreterCall would simply select
        // one of the pre-cached expressions and evaluate it.
        // Note that PageContextImpl implements VariableResolver and
        // the generated Servlet/SimpleTag implements FunctionMapper, so
        // that machinery is already in place (mroth).
        StringBuffer call =
            new StringBuffer(
                "("
                    + targetType
                    + ") "
                    + "org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate"
                    + "("
                    + quote(expression)
                    + ", "
                    + targetType
                    + ".class, "
                    + "(PageContext)"
                    + jspCtxt
                    + ", "
                    + fnmapvar
                    + ", "
                    + XmlEscape
                    + ")");

        /*
             * Add the primitive converter method if we need to.
             */
        if (primitiveConverterMethod != null) {
            call.insert(0, "(");
            call.append(")." + primitiveConverterMethod + "()");
        }

        return call.toString();
    }

    public static String createValueExpression(StringBuffer sb, String mark, String elContext, String attrValue, boolean isELinput, Class c, TagAttributeInfo tai, String jspCtxt) {
        sb.append("new org.apache.jasper.el.JspValueExpression(");
        sb.append(quote(mark));
        sb.append(',');
        sb.append("_el_expressionfactory");
        sb.append(".createValueExpression(");
        if (true/*isELInput*/) { // optimize
            sb.append(elContext);
            sb.append(',');
        }
        sb.append(quote(attrValue));
        sb.append(',');
        sb.append(GeneratorUtils.toJavaSourceTypeFromTld(GeneratorUtils.getExpectedTypeName(tai)));
        //sb.append(".class");
        sb.append("))");
        // should the expression be evaluated before passing to
        // the setter?
        boolean evaluate = false;
        if (tai.canBeRequestTime()) {
            evaluate = true; // JSP.2.3.2
        }
        if (GeneratorUtils.isDeferredInput(tai)) {
            evaluate = false; // JSP.2.3.3
        }
        if (GeneratorUtils.isDeferredInput(tai) && tai.canBeRequestTime()) {
            evaluate = !attrValue.contains("#{"); // JSP.2.3.5
        }
        if (evaluate) {
            sb.append(".getValue(");
            sb.append(jspCtxt);
            sb.append(".getELContext()");
            sb.append(")");
        }
        attrValue = sb.toString();    	
    	return sb.toString();
    }

    public static String createMethodExpression(StringBuffer sb, String mark, String elContext, String attrValue, boolean isELinput, Class c, TagAttributeInfo tai, String jspCtxt) {
        sb.append("new org.apache.jasper.el.JspMethodExpression(");
        sb.append(quote(mark));
        sb.append(',');
        sb.append("_el_expressionfactory");
        sb.append(".createMethodExpression(");
        sb.append(elContext);
        sb.append(',');
        sb.append(quote(attrValue));
        sb.append(',');
        sb.append(GeneratorUtils.toJavaSourceTypeFromTld(GeneratorUtils.getExpectedTypeName(tai)));
        sb.append(',');
        sb.append("new Class[] {");

        String[] p = getParameterTypeNames(tai);
        for (int i = 0; i < p.length; i++) {
            sb.append(GeneratorUtils.toJavaSourceTypeFromTld(p[i]));
            sb.append(',');
        }
        if (p.length > 0) {
            sb.setLength(sb.length() - 1);
        }

        sb.append("}))");
        attrValue = sb.toString();
    	return sb.toString();
    }
    
    public static String toJavaSourceTypeFromTld(String type) {
        if (type == null || "void".equals(type)) {
            return "Void.TYPE";
        }
        return type + ".class";
    }
    
    
    public static String[] getParameterTypeNames(TagAttributeInfo tai) {
        if (tai != null) {
            if (GeneratorUtils.isDeferredMethodInput(tai)) {
                String m = tai.getMethodSignature();
                if (m != null) {
                    m = m.trim();
                    m = m.substring(m.indexOf('(') + 1);
                    m = m.substring(0, m.length() - 1);
                    if (m.trim().length() > 0) {
                        String[] p = m.split(",");
                        for (int i = 0; i < p.length; i++) {
                            p[i] = p[i].trim();
                        }
                        return p;
                    }
                }
            }
        }
        return new String[0];
    }

    public static String getExpectedTypeName(TagAttributeInfo tai) {
        if (tai != null) {
            if (GeneratorUtils.isDeferredInput(tai)) {
                return tai.getExpectedTypeName();
            } else if (GeneratorUtils.isDeferredMethodInput(tai)) {
                String m = tai.getMethodSignature();
                if (m != null) {
                    int rti = m.trim().indexOf(' ');
                    if (rti > 0) {
                        return m.substring(0, rti).trim();
                    }
                }
            }
        }
        return "java.lang.Object";
    }

    
    public static String nextTemporaryVariableName(Map persistentData) {
        String nextTempVar = "_jspx_temp";

        Integer tempVarIndex = (Integer) persistentData.get("tempVarIndex");

        if (tempVarIndex == null) {
            tempVarIndex = new Integer(0);
        }
        int val = tempVarIndex.intValue();
        tempVarIndex = new Integer(++val);
        nextTempVar = nextTempVar + tempVarIndex;
        persistentData.put("tempVarIndex", tempVarIndex);

        return (nextTempVar);
    }

    //PK65013 add method parameter pageContextVar
    public static String attributeValue(
        String valueIn,
        boolean encode,
        Class expectedType,
        JspConfiguration jspConfig,
        boolean isTagFile,
        String pageContextVar) {
        String value = valueIn;
        value = value.replaceAll("&gt;", ">");
        value = value.replaceAll("&lt;", "<");
        value = value.replaceAll("&amp;", "&");
        value = value.replaceAll("<\\%", "<%");
        value = value.replaceAll("%\\>", "%>");
		if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINEST)){
			logger.logp(Level.FINEST, CLASS_NAME, "attributeValue","valueIn = ["+valueIn+"]");
			logger.logp(Level.FINEST, CLASS_NAME, "attributeValue","encode = ["+encode+"]");
			logger.logp(Level.FINEST, CLASS_NAME, "attributeValue","expectedType = ["+expectedType+"]");
			logger.logp(Level.FINEST, CLASS_NAME, "attributeValue","isTagFile = ["+isTagFile+"]");
		}
        if (JspTranslatorUtil.isExpression(value)) {
            value = value.substring(2, value.length() - 1);
            if (encode) {
                value = "org.apache.jasper.runtime.JspRuntimeLibrary.URLEncode(String.valueOf(" + 
                value + 
                "), request.getCharacterEncoding())";//PK03712
            }
    		if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINEST)){
    			logger.logp(Level.FINEST, CLASS_NAME, "attributeValue","isExpression. value = ["+value+"]");
    		}
        }
        else if (JspTranslatorUtil.isELInterpreterInput(value, jspConfig)) {
        	if(encode){
                //PK65013 - add pageContextVar parameter
        	    value = "org.apache.jasper.runtime.JspRuntimeLibrary.URLEncode(" + 
        	            interpreterCall(isTagFile, value, expectedType, "_jspx_fnmap", false, pageContextVar) + 
						", request.getCharacterEncoding())";
        	}
        	else{
                //PK65013 - add pageContextVar parameter
                value = interpreterCall(isTagFile, value, expectedType, "_jspx_fnmap", false, pageContextVar);
        	}
    		if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINEST)){
    			logger.logp(Level.FINEST, CLASS_NAME, "attributeValue","isELInterpreterInput. value = ["+value+"]");
    		}
        }
        else {
            if (encode) {
                value = "org.apache.jasper.runtime.JspRuntimeLibrary.URLEncode("
                        + quote(value)
                        + ", request.getCharacterEncoding())";
            }
            else {
                value = quote(value);
            }
    		if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINEST)){
    			logger.logp(Level.FINEST, CLASS_NAME, "attributeValue","default. value = ["+value+"]");
    		}
        }

        return (value);
    }

    public static String replace(String name, char replace, String with) {
        StringBuffer buf = new StringBuffer();
        int begin = 0;
        int end;
        int last = name.length();

        while (true) {
            end = name.indexOf(replace, begin);
            if (end < 0) {
                end = last;
            }
            buf.append(name.substring(begin, end));
            if (end == last) {
                break;
            }
            buf.append(with);
            begin = end + 1;
        }

        return buf.toString();
    }

    public static char[] removeQuotes(char[] chars) {
        CharArrayWriter caw = new CharArrayWriter();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '%' && chars[i + 1] == '\\' && chars[i + 2] == '>') {
                caw.write('%');
                caw.write('>');
                i = i + 3;
            }
            else
                caw.write(chars[i]);
        }
        return caw.toCharArray();
    }

    public static char[] escapeQuotes(char[] chars) {
        char[] c = escapeScriptingStart(chars);
        c = escapeScriptingEnd(c);
        return (c);    
    }
    
    private static char[] escapeScriptingStart(char[] chars) {
        // Prescan to convert <\% to <%
        String s = new String(chars);
        while (true) {
            int n = s.indexOf("<\\%");
            if (n < 0)
                break;
            StringBuffer sb = new StringBuffer(s.substring(0, n));
            sb.append("<%");
            sb.append(s.substring(n + 3));
            s = sb.toString();
        }
        chars = s.toCharArray();
        return (chars);
    }

    private static char[] escapeScriptingEnd(char[] chars) {
        // Prescan to convert %\> to %>
        String s = new String(chars);
        while (true) {
            int n = s.indexOf("%\\>");
            if (n < 0)
                break;
            StringBuffer sb = new StringBuffer(s.substring(0, n));
            sb.append("%>");
            sb.append(s.substring(n + 3));
            s = sb.toString();
        }
        chars = s.toCharArray();
        return (chars);
    }


    public static void generateELFunctionCode(JavaCodeWriter writer, ValidateResult validatorResult)
        throws JspCoreException {
        writer.println("private static org.apache.jasper.runtime.ProtectedFunctionMapper _jspx_fnmap = null;");
        
        if (validatorResult.getValidateFunctionMapper().getFnMap().size() > 0) { //LIDB4147-9        
            
            writer.println();
            writer.println("static {");
            writer.println("_jspx_fnmap = org.apache.jasper.runtime.ProtectedFunctionMapper.getInstance();");

            for (Iterator itr = validatorResult.getValidateFunctionMapper().getFnMap().keySet().iterator(); itr.hasNext();) {  //LIDB4147-9 
                String fnQName = (String) itr.next();
                String fnPrefix = fnQName.substring(0, fnQName.indexOf(':'));
                String fnName = fnQName.substring(fnQName.indexOf(':')+1);
                           
                writer.print("_jspx_fnmap.mapFunction(");
                writer.print(GeneratorUtils.quote(fnPrefix));
                writer.print(", ");
                writer.print(GeneratorUtils.quote(fnName));
                writer.print(", ");
                FunctionSignature functionSignature = validatorResult.getValidateFunctionMapper().getSignature(fnQName);
                writer.print(functionSignature.getFunctionClassName() + ".class, ");
                writer.print(GeneratorUtils.quote(functionSignature.getMethodName()));
                writer.print(", ");
                Class[] args = functionSignature.getParameterTypes();
                if (args != null) {
                    writer.print("new Class[] {");
                    for (int j = 0; j < args.length; j++) {
                        if (args[j].isArray()) {
                            writer.print("java.lang.reflect.Array.newInstance("+args[j].getComponentType().getName() + ".class, 0).getClass()");
                        }
                        else {
                            writer.print(args[j].getName() + ".class");
                        }
                        if (j < (args.length - 1)) {
                            writer.print(", ");
                        }
                    }
                    writer.print("} ");
                }
                else {
                    writer.print("null");
                }
                writer.print(");");
                writer.println();     
            }
            writer.println("}");
            writer.println();
        }
    }

    public static void generateDependencyList(JavaCodeWriter writer, ValidateResult validatorResult, JspCoreContext context, boolean isTrackDependencies) {
        writer.println("private static String[] _jspx_dependants;");

        if (validatorResult.getDependencyList().size() > 0 || isTrackDependencies) {
            writer.println("static {");
            writer.print("_jspx_dependants = new String[");
            if (validatorResult.getDependencyList().size() > 0) {
                writer.print("" + validatorResult.getDependencyList().size());
            }
            else {
                writer.print("" + 0);
            }
            writer.print("];");
            writer.println();

            int count = 0;
            String dependencyName=null;
            String dependencyFullName=null;
            File dependencyFile=null;
            for (Iterator itr = validatorResult.getDependencyList().iterator(); itr.hasNext();) {
                writer.print("_jspx_dependants["+(count++)+"] = \"");
                dependencyName=(String) itr.next();
                dependencyFullName=context.getRealPath(dependencyName);
                dependencyFile = new File(dependencyFullName);
                writer.print(dependencyName+Constants.TIMESTAMP_DELIMETER+dependencyFile.lastModified() + Constants.TIMESTAMP_DELIMETER+ (new java.util.Date(dependencyFile.lastModified())).toString());
                writer.print("\";");
                writer.println();
            }
            writer.println("}");
            writer.println();
        }

        writer.println("public String[] getDependants() {");
        writer.println("return _jspx_dependants;");
        writer.println("}");
    }

	public static void generateVersionInformation(JavaCodeWriter writer, boolean isDebugClassFile) {
		writer.println("private static String _jspx_classVersion;");
		//		 begin 228118: JSP container should recompile if debug enabled and jsp was not compiled in debug.
		writer.println("private static boolean _jspx_isDebugClassFile;");
		//		 end 228118: JSP container should recompile if debug enabled and jsp was not compiled in debug.		

		writer.println("static {");
		writer.print("_jspx_classVersion = new String(");
		writer.print("\"" + classfileVersion);
		writer.print("\");");
		writer.println();

		//		 begin 228118: JSP container should recompile if debug enabled and jsp was not compiled in debug.
		writer.print("_jspx_isDebugClassFile = ");
		writer.print("" + isDebugClassFile);
		writer.print(";");
		writer.println();
		//		 end 228118: JSP container should recompile if debug enabled and jsp was not compiled in debug.	
		
		writer.println("}");
		writer.println();

		writer.println("public String getVersionInformation() {");
		writer.println("return _jspx_classVersion;");
		writer.println("}");

		//		 begin 228118: JSP container should recompile if debug enabled and jsp was not compiled in debug.
		writer.println("public boolean isDebugClassFile() {");
		writer.println("return _jspx_isDebugClassFile;");
		writer.println("}");
		//		 end 228118: JSP container should recompile if debug enabled and jsp was not compiled in debug.	

		
	}

    public static String coerceToPrimitiveBoolean(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "org.apache.jasper.runtime.JspRuntimeLibrary.coerceToBoolean(" + s + ")";
        }
        else {
            if (s == null || s.length() == 0)
                return "false";
            else
                return Boolean.valueOf(s).toString();
        }
    }

    public static String coerceToBoolean(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "(Boolean) org.apache.jasper.runtime.JspRuntimeLibrary.coerce(" + s + ", Boolean.class)";
        }
        else {
            if (s == null || s.length() == 0) {
                return "new Boolean(false)";
            }
            else {
                // Detect format error at translation time
                return "new Boolean(" + Boolean.valueOf(s).toString() + ")";
            }
        }
    }

    public static String coerceToPrimitiveByte(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "org.apache.jasper.runtime.JspRuntimeLibrary.coerceToByte(" + s + ")";
        }
        else {
            if (s == null || s.length() == 0)
                return "(byte) 0";
            else
                return "((byte)" + Byte.valueOf(s).toString() + ")";
        }
    }

    public static String coerceToByte(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "(Byte) org.apache.jasper.runtime.JspRuntimeLibrary.coerce(" + s + ", Byte.class)";
        }
        else {
            if (s == null || s.length() == 0) {
                return "new Byte((byte) 0)";
            }
            else {
                // Detect format error at translation time
                return "new Byte((byte)" + Byte.valueOf(s).toString() + ")";
            }
        }
    }

    public static String coerceToChar(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "org.apache.jasper.runtime.JspRuntimeLibrary.coerceToChar(" + s + ")";
        }
        else {
            if (s == null || s.length() == 0) {
                return "(char) 0";
            }
            else {
                char ch = s.charAt(0);
                // this trick avoids escaping issues
                return "((char) " + (int) ch + ")";
            }
        }
    }

    public static String coerceToCharacter(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "(Character) org.apache.jasper.runtime.JspRuntimeLibrary.coerce(" + s + ", Character.class)";
        }
        else {
            if (s == null || s.length() == 0) {
                return "new Character((char) 0)";
            }
            else {
                char ch = s.charAt(0);
                // this trick avoids escaping issues
                return "new Character((char) " + (int) ch + ")";
            }
        }
    }

    public static String coerceToPrimitiveDouble(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "org.apache.jasper.runtime.JspRuntimeLibrary.coerceToDouble(" + s + ")";
        }
        else {
            if (s == null || s.length() == 0)
                return "(double) 0";
            else
                return Double.valueOf(s).toString();
        }
    }

    public static String coerceToDouble(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "(Double) org.apache.jasper.runtime.JspRuntimeLibrary.coerce(" + s + ", Double.class)";
        }
        else {
            if (s == null || s.length() == 0) {
                return "new Double(0)";
            }
            else {
                // Detect format error at translation time
                return "new Double(" + Double.valueOf(s).toString() + ")";
            }
        }
    }

    public static String coerceToPrimitiveFloat(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "org.apache.jasper.runtime.JspRuntimeLibrary.coerceToFloat(" + s + ")";
        }
        else {
            if (s == null || s.length() == 0)
                return "(float) 0";
            else
                return Float.valueOf(s).toString() + "f";
        }
    }

    public static String coerceToFloat(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "(Float) org.apache.jasper.runtime.JspRuntimeLibrary.coerce(" + s + ", Float.class)";
        }
        else {
            if (s == null || s.length() == 0) {
                return "new Float(0)";
            }
            else {
                // Detect format error at translation time
                return "new Float(" + Float.valueOf(s).toString() + "f)";
            }
        }
    }

    public static String coerceToInt(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "org.apache.jasper.runtime.JspRuntimeLibrary.coerceToInt(" + s + ")";
        }
        else {
            if (s == null || s.length() == 0)
                return "0";
            else
                return Integer.valueOf(s).toString();
        }
    }

    public static String coerceToInteger(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "(Integer) org.apache.jasper.runtime.JspRuntimeLibrary.coerce(" + s + ", Integer.class)";
        }
        else {
            if (s == null || s.length() == 0) {
                return "new Integer(0)";
            }
            else {
                // Detect format error at translation time
                return "new Integer(" + Integer.valueOf(s).toString() + ")";
            }
        }
    }

    public static String coerceToPrimitiveShort(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "org.apache.jasper.runtime.JspRuntimeLibrary.coerceToShort(" + s + ")";
        }
        else {
            if (s == null || s.length() == 0)
                return "(short) 0";
            else
                return "((short) " + Short.valueOf(s).toString() + ")";
        }
    }

    public static String coerceToShort(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "(Short) org.apache.jasper.runtime.JspRuntimeLibrary.coerce(" + s + ", Short.class)";
        }
        else {
            if (s == null || s.length() == 0) {
                return "new Short((short) 0)";
            }
            else {
                // Detect format error at translation time
                return "new Short(\"" + Short.valueOf(s).toString() + "\")";
            }
        }
    }

    public static String coerceToPrimitiveLong(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "org.apache.jasper.runtime.JspRuntimeLibrary.coerceToLong(" + s + ")";
        }
        else {
            if (s == null || s.length() == 0)
                return "(long) 0";
            else
                return Long.valueOf(s).toString() + "l";
        }
    }

    public static String coerceToLong(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "(Long) org.apache.jasper.runtime.JspRuntimeLibrary.coerce(" + s + ", Long.class)";
        }
        else {
            if (s == null || s.length() == 0) {
                return "new Long(0)";
            }
            else {
                // Detect format error at translation time
                return "new Long(" + Long.valueOf(s).toString() + "l)";
            }
        }
    }
    
    /* Defect 213290 */
    public static String toJavaSourceType(String type) {

        if (type.charAt(0) != '[') {
            return type;
        }

        int dims = 1;
        String t = null;
        for (int i = 1; i < type.length(); i++) {
            if (type.charAt(i) == '[') {
                dims++;
            }
            else {
                switch (type.charAt(i)) {
                    case 'Z' :
                        t = "boolean";
                        break;
                    case 'B' :
                        t = "byte";
                        break;
                    case 'C' :
                        t = "char";
                        break;
                    case 'D' :
                        t = "double";
                        break;
                    case 'F' :
                        t = "float";
                        break;
                    case 'I' :
                        t = "int";
                        break;
                    case 'J' :
                        t = "long";
                        break;
                    case 'S' :
                        t = "short";
                        break;
                    case 'L' :
                        t = type.substring(i + 1, type.indexOf(';'));
                        break;
                }
                break;
            }
        }
        StringBuffer resultType = new StringBuffer(t);
        for (; dims > 0; dims--) {
            resultType.append("[]");
        }
        return resultType.toString();
    }

    //PM06063
    public static void generateInitSectionCode(JavaCodeWriter writer, int type) {
    	generateInitSectionCode(writer, type, null);
    }
    //PM06063
    
    //jsp2.1work
    public static void generateInitSectionCode(JavaCodeWriter writer, int type, JspOptions jspOptions) {  	//PM06063
        writer.println("private javax.el.ExpressionFactory _el_expressionfactory;");
        if (type==GeneratorUtils.TAG_FILE_TYPE) {
            //LIDB4147-24 & 443243 : need to define injection helpers for .tag files
        	if (jspOptions == null || !jspOptions.isDisableResourceInjection()){	                        //PM06063
        		generateInjectionSection(writer);
        	}																								//PM06063
            writer.println("private void _jspInit(ServletConfig config) {");
        } else {
        	writer.println("public void _jspInit() {");
        }
        writer.print("_el_expressionfactory");
        writer.print(" = _jspxFactory.getJspApplicationContext(");
        if (type==GeneratorUtils.TAG_FILE_TYPE) {
        	writer.print("config");
        } else {
        	writer.print("getServletConfig()");
        }
        writer.print(".getServletContext()).getExpressionFactory();");
        writer.println();
        writer.println();
        
        // LIDB4147-24
        if (jspOptions == null || !jspOptions.isDisableResourceInjection()){		//PM06063
        	
        	writer.print ("com.ibm.wsspi.webcontainer.annotation.AnnotationHelperManager _jspx_aHelper = ");
        	writer.print ("com.ibm.wsspi.webcontainer.annotation.AnnotationHelperManager.getInstance (");
        	//443243: need to have the servletConfig to get the servletContext
        	if (type==GeneratorUtils.TAG_FILE_TYPE) {
        		writer.print("config");
        	} else {
        		writer.print("getServletConfig()");
        	}
        	writer.println(".getServletContext());");
        	writer.println ("_jspx_iaHelper = _jspx_aHelper.getAnnotationHelper();");
        }								                                            //PM06063
        
        //PK81147 start
        //if this is a JSP page, need to set this to true to indicate that we have already run_jspInit() for this generated servlet.  
        if(type==GeneratorUtils.JSP_FILE_TYPE){
            writer.println("_jspx_isJspInited = true;");
        }
        //PK81147 end
        
        writer.println("}");
		
	}

	public static void generateFactoryInitialization(JavaCodeWriter writer, boolean jcdiWrapIt) {
        //If jcdi is enabled, we need to return a wrapped factory, that can be used to get a wrappedExpressionFactory 
	    writer.print("private static final JspFactory _jspxFactory = ");
        if (jcdiWrapIt) {
            writer.print("new org.apache.jasper.runtime.JcdiWrappedJspFactoryImpl(");
        }
        writer.print("JspFactory.getDefaultFactory()");
        if (jcdiWrapIt) {
            writer.print(")");
        }
        writer.println(";");
	}
     
     // LIDB4147-24
     
     public static void generateInjectionSection (JavaCodeWriter writer) {
          writer.println ("private com.ibm.wsspi.webcontainer.annotation.AnnotationHelper _jspx_iaHelper;");
     }
}
