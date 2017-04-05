//IBM Confidential OCO Source Material
//5724-i63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997-2004
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
/*
 * Change history:
 * defect 215691 "add support for javaEncoding"  2004/07/12  Scott Johnson
 * Defect 216829  "JSPs are not compiled with -g in WAS60"  2004/07/15  Scott Johnson
 * defect PK72039 Add ability to continue to compile the rest of the JSPs during a batch compile failure  2008/11/15  Jay Sartoris
 * Defect PM04610 Need option to specify version 1.6 for the jdkSourceLevel attribute  2010/02/03  Jay Sartoris
 */

package com.ibm.ws.jsp.translator.compiler;

import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ibm.ws.jsp.JspCoreException;
import com.ibm.ws.jsp.JspOptions;
import com.ibm.wsspi.jsp.compiler.JspCompiler;
import com.ibm.wsspi.jsp.compiler.JspCompilerResult;
import com.ibm.wsspi.jsp.compiler.JspLineId;
import com.ibm.wsspi.jsp.context.JspClassloaderContext;
import com.ibm.wsspi.jsp.resource.translation.JspResources;

public class StandardJspCompiler implements JspCompiler  {
    static protected Logger logger;
	private static final String CLASS_NAME="com.ibm.ws.jsp.translator.compiler.StandardJspCompiler";
    static {
        logger = Logger.getLogger("com.ibm.ws.jsp");
    }
    private static Pattern warningPattern = Pattern.compile("[0-9]+ warning");
    private static String separatorString = System.getProperty("line.separator");  // Defect xxxxxx
    
    protected CharArrayWriter out = null;
    protected String fullClasspath = null;
    protected String optimizedClasspath = null;
    protected String sourcepath = null;
    protected boolean isClassDebugInfo = false;
    protected boolean isDebugEnabled = false;
    protected boolean isVerbose = false;
    protected boolean isDeprecation = false; 
    protected String jdkSourceLevel=null; 
    protected String javaEncoding = null; 

    protected boolean useOptimizedClasspath = false;
    protected List compilerFailureFileNames = null;  //PK72039
    protected boolean compileAfterFailure = false; //PK72039

    public StandardJspCompiler(JspClassloaderContext context, JspOptions options, String optimizedClasspath, boolean useOptimizedClasspath) {
        fullClasspath = context.getClassPath()+ File.pathSeparatorChar + options.getOutputDir().getPath();
        this.optimizedClasspath = optimizedClasspath;
		this.useOptimizedClasspath = useOptimizedClasspath;
        sourcepath = options.getOutputDir().getPath();
        this.isClassDebugInfo = options.isClassDebugInfo();
        this.isDebugEnabled = options.isDebugEnabled();
        this.isVerbose = options.isVerbose();
        this.isDeprecation =  options.isDeprecation();
        this.javaEncoding = options.getJavaEncoding();
        this.jdkSourceLevel=  options.getJdkSourceLevel();
        this.compileAfterFailure = options.isCompileAfterFailure(); //PK72039
        out = new CharArrayWriter();
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)) {
            logger.logp(Level.FINE, CLASS_NAME, "StandardJspCompiler", "Entering StandardJspCompiler.");
        }
    }
    
    public JspCompilerResult compile(JspResources[] jspResources, JspResources[] dependencyResources, Collection jspLineIds, List compilerOptions) {
    	return compile(jspResources[0].getGeneratedSourceFile().getPath(), jspLineIds, compilerOptions);
    }
    
    public JspCompilerResult compile(String source, Collection jspLineIds, List compilerOptions) {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)) {
            logger.logp(Level.FINE, CLASS_NAME, "compile", "Entering StandardJspCompiler.compile");
        }
        out.reset();            
        int rc = 0;

        setCompilerFailureFileNames(null); //PK72039
        
        String cp = null;
        boolean directoryCompile = (source.charAt(0)=='@');
        
        if (directoryCompile && !useOptimizedClasspath) {
            cp = fullClasspath;    
        }
        else {
            cp = optimizedClasspath;
        }
        rc = runCompile(source, compilerOptions, cp);
        
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)) {
            logger.logp(Level.FINE, CLASS_NAME, "compile", "rc = " + rc + " directoryCompile = " + directoryCompile + " useOptimizedClasspath = " + useOptimizedClasspath);
        }
        
        /* don't retry compilation:  the fullClasspath is the same as the optimizedClasspath
        if (rc != 0 && directoryCompile == false && useOptimizedClasspath == false) {
        	// Defect 211450 - change log level to FINE from WARNING
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)) {
                logger.logp(Level.FINE, CLASS_NAME, "compile", "Warning: failed to compile " + source + " with optimized classpath ["+cp+"]");
            }
            
            out.reset();            
            rc = runCompile(source, compilerOptions, fullClasspath);
        }
        */
        
        String output = null;
        
        if (rc != 0 || (rc==0 && (isVerbose || isDeprecation))) {
            output = out.toString();
            if (rc!=0) {
                output = getJspLineErrors(output, jspLineIds);
            }
        }
        
        //JspCompilerResult result = new JspCompilerResultImpl(rc, output);
        JspCompilerResult result = new JspCompilerResultImpl(rc, output, getCompilerFailureFileNames());  //PK72039
        return (result);
    }
    
    private int runCompile(String source, List compilerOptions, String cp) {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)) {
            logger.logp(Level.FINE, CLASS_NAME, "compile", "Entering StandardJspCompiler.runCompile");
        }
        int rc = 0;
        
        List argList = buildArgList(source, compilerOptions, cp);
        String[] args = new String[argList.size()];
        args = (String[])argList.toArray(args);
        
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)) {
            logger.logp(Level.FINE, CLASS_NAME, "runCompile", "compiling " + source);
            logger.logp(Level.FINE, CLASS_NAME, "runCompile", "classpath [" + cp+ "]");
        }
        
        long start = System.currentTimeMillis();
        
        rc = com.sun.tools.javac.Main.compile(args, new PrintWriter(out));
        
        long end = System.currentTimeMillis();
        
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)) {
            logger.logp(Level.FINE, CLASS_NAME, "runCompile", "compile complete for " + source + " time = " + (end - start) + " Milliseconds rc = " + rc);
        }
        return rc;
    }
    
    private List buildArgList(String source, List compilerOptions, String classpath) {
        List argList = new ArrayList();
        
        if (isClassDebugInfo || this.isDebugEnabled) { 
            argList.add("-g");
        }
        
        if (isVerbose) {
            argList.add("-verbose");
        }
        
        if (isDeprecation) {
            argList.add("-deprecation");
        }
        
        //487396.1 jdkSourceLevel is 15 by default now ... should get into if statement
        argList.add("-source");
        if (jdkSourceLevel.equals("14")) {
            argList.add("1.4");
        }
        else if (jdkSourceLevel.equals("15")) {
            argList.add("1.5");
        }
        //PM04610 start
        else if (jdkSourceLevel.equals("16")) {
            argList.add("1.6");
        }
        //PM04610 end
        else if (jdkSourceLevel.equals("17")) {
            argList.add("1.7");
        }
        else {
            argList.add("1.3");
        }
        
        argList.add("-encoding");
        argList.add(this.javaEncoding);
        argList.add("-XDjsrlimit=1000");
        argList.add("-sourcepath");
        argList.add(sourcepath);
        argList.add("-classpath");
        argList.add(classpath);
        
        if (compilerOptions!=null) {
            for (int i=0;i<compilerOptions.size();i++) {
                String compilerOption = (String)compilerOptions.get(i);
                if (compilerOption.equals("-verbose")) {
                    isVerbose = true; 
                }
                if (compilerOption.equals("-deprecation")) {
                    isDeprecation = true; 
                }
                argList.add(compilerOption);
            }
        }
        
        argList.add(source);
        return argList;
    }

    public String getJspLineErrors(String compilerOutput, Collection jspLineIds) {

        StringBuffer errorMsg = new StringBuffer();
        BufferedReader br = new BufferedReader(new StringReader(compilerOutput));

        try {
            String line = br.readLine();
            int warningIndex = -1;
            String warningMatched;
            List failedFiles = new ArrayList();  //PK72039

            while (line != null) {
    
                // Get .java file name
                int javaNameEnd = line.indexOf(".java:");
                String javaName = null;
                if (javaNameEnd > 0) {
                    javaName = line.substring(0, javaNameEnd + 5);
                    // path separator consistent for platform
                    javaName = javaName.replace ('\\', '/');    
                    javaName = javaName.replace('/', File.separatorChar);
                }
    
                // line number is between a set of colons
                int beginColon = line.indexOf(':', 2);
                // Skip over Drive letter on Windows (start search at offset 2 in message)
                int endColon = line.indexOf(':', beginColon + 1);
                Matcher warningMatcher = warningPattern.matcher(line);
                warningMatched=null;
                if (warningMatcher.find()) {
                     warningMatched = warningMatcher.group();
                }
                warningIndex = line.indexOf("warning:");
    
                if (!(javaName == null
                    || beginColon < 0
                    || warningIndex >= 0
                    || warningMatched != null
                    || endColon < 0
                    || line.startsWith("Note: ") // deprecation warning  defect 142599
                    || line.startsWith("[loaded ")
                    || line.startsWith("] ")
                    || line.startsWith("[parsed ")
                    || line.startsWith("[[parsing started ")
                    || line.startsWith("[parsing completed ")
                    || line.startsWith("[loading ")
                    || line.startsWith("[checking ")
                    || line.startsWith("[wrote ")
                    || line.startsWith("[total "))) { // verbose  defect 142599
    
                    try {
                        String nr = line.substring(beginColon + 1, endColon);
                        int lineNr = Integer.parseInt(nr);
    
                        // Now do the mapping
                        String mapping = findMapping(jspLineIds, lineNr, javaName);
                        if (mapping == null) {
                            errorMsg.append(separatorString);  // Defect 211450
                        }
                        else {
                            if (javaName.indexOf('\\') != -1)
                                javaName = javaName.replace('\\', '/');

                            //PK72039 start
                            if (compileAfterFailure) {
                                failedFiles.add("\""+javaName+"\"");
                            }
                            //PK72039 end

                            errorMsg.append(mapping);
                        }
                    }
                    catch (NumberFormatException ex) {
                        // If for some reason our guess at the location of the line
                        // number failed, time to give up.
                    }
                }
                errorMsg.append (line);
                errorMsg.append(separatorString);  // Defect 211450
                if (warningIndex>=0) {
                    line = br.readLine();
                    errorMsg.append (line);
                    errorMsg.append(separatorString);  // Defect 211450
                    line = br.readLine();
                    errorMsg.append (line);
                    errorMsg.append(separatorString);  // Defect 211450
                }
                line = br.readLine();
            }
            
            //PK72039 start
            if (compileAfterFailure) {
                setCompilerFailureFileNames(failedFiles);
            }
            //PK72039 end

            br.close();
            //map.clear();
        }
        catch (IOException e) {
			logger.logp(Level.WARNING, CLASS_NAME, "getJspLineErrors", "Failed to find line number mappings for compiler errors", e);
        }
        
        return errorMsg.toString();
    }

    private String findMapping(Collection jspLineIds, int lineNr, String javaName) {
        String errorMsg = null;
        for (Iterator itr = jspLineIds.iterator(); itr.hasNext();) {
            JspLineId lineId = (JspLineId)itr.next();
            if (lineId.getGeneratedFilePath().equals(javaName)) {
                if (lineId.getStartGeneratedLineCount() <= 1 && lineId.getStartGeneratedLineNum() == lineNr) {
                    errorMsg =  createErrorMsg(lineId, lineNr);
                    break;
                }
                else if (lineId.getStartGeneratedLineNum() <= lineNr && 
                         (lineId.getStartGeneratedLineNum() + lineId.getStartGeneratedLineCount() - 1) >= lineNr) {
                    errorMsg =  createErrorMsg(lineId, lineNr);
                    break;
                }
            }
        }
        
        return errorMsg;
    }

    /**
     * Create error message including the jsp line numbers and file name
     */
	//	defect 203009 - add logic to 1. narrow down error to single line in JSP, if possible, 2) improve
	// 					error messages to indicate when a file is statically included, and give
	//					name of parent file 
    private String createErrorMsg(JspLineId jspLineId, int errorLineNr) {
        StringBuffer compilerOutput = new StringBuffer();

        if (jspLineId.getSourceLineCount() <= 1) {
			Object[] objArray = new Object[] { new Integer(jspLineId.getStartSourceLineNum()), jspLineId.getFilePath()};
			if (jspLineId.getFilePath().equals(jspLineId.getParentFile())) {
				compilerOutput.append(separatorString+JspCoreException.getMsg("jsp.error.single.line.number", objArray));  // Defect 211450
			}
			else {
				compilerOutput.append(separatorString+JspCoreException.getMsg("jsp.error.single.line.number.included.file", objArray));  // Defect 211450
			}
        }
        else {
			// compute exact JSP line number 
			int actualLineNum=jspLineId.getStartSourceLineNum()+(errorLineNr-jspLineId.getStartGeneratedLineNum());
			if (actualLineNum>=jspLineId.getStartSourceLineNum() && actualLineNum <=(jspLineId.getStartSourceLineNum() + jspLineId.getSourceLineCount() - 1)) {
				Object[] objArray = new Object[] { new Integer(actualLineNum), jspLineId.getFilePath()};
				if (jspLineId.getFilePath().equals(jspLineId.getParentFile())) {
					compilerOutput.append(separatorString+JspCoreException.getMsg("jsp.error.single.line.number", objArray));  // Defect 211450
				}
				else {
					compilerOutput.append(separatorString+JspCoreException.getMsg("jsp.error.single.line.number.included.file", objArray));  // Defect 211450
				}
			}
			else {
				Object[] objArray = new Object[] {
					new Integer(jspLineId.getStartSourceLineNum()),
					new Integer((jspLineId.getStartSourceLineNum()) + jspLineId.getSourceLineCount() - 1),
					jspLineId.getFilePath()};
				if (jspLineId.getFilePath().equals(jspLineId.getParentFile())) {
					compilerOutput.append(separatorString+  // Defect 211450
						JspCoreException.getMsg(
							"jsp.error.multiple.line.number", objArray));
				}
				else {
					compilerOutput.append(separatorString+  // Defect 211450
						JspCoreException.getMsg(
							"jsp.error.multiple.line.number.included.file",objArray));
				}
			}			
        }

        compilerOutput.append(separatorString+JspCoreException.getMsg("jsp.error.corresponding.servlet",new Object[] { jspLineId.getParentFile()})+separatorString);  // Defect 211450

        return compilerOutput.toString();
    }

    //PK72039 start
    private void setCompilerFailureFileNames(List failedFiles) {
        compilerFailureFileNames = failedFiles;
    }

    private List getCompilerFailureFileNames() {
        if (compilerFailureFileNames != null && logger != null) {
            logger.logp(Level.FINER,"StandardJspCompiler","getCompilerFailureFileNames", "The following file failed to compile: " + compilerFailureFileNames);
        }
        return compilerFailureFileNames;
    }
    //PK72039 end

}
