//IBM Confidential OCO Source Material
//5724-I63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997-2004
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.

/*
 * Created on Nov 24, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.ibm.wsspi.jsp.tools;

import java.util.Map;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Scott Johnson
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface JspTools {
    public boolean compileApp(String contextDir);

    public boolean compileDir(String contextDir, String directory);

    public boolean compileFile(String contextDir, String jspFile);

    public void setForceCompilation(boolean forceCompilation);

    public void setClasspath(String classpath);
    //begin 241038: add new API for setting classpath
    public void setClasspath(String serverClasspath, String appClasspath);
    //end 241038: add new API for setting classpath

    public void setClassloader(ClassLoader loader); //418518

    public void setLooseLibs(Map looseLibs);

    public void setOptions(JspToolsOptionsMap options);

    public void setTaglibs(Hashtable tagLibs);

    public void setCompilerOptions(List compilerOptions);

    public void setKeepGeneratedclassfiles(boolean b);

    public void setCreateDebugClassfiles(boolean createDebugClassfiles);

    public void setLogger(Logger loggerIn);

	// Defect 202493
	public Logger getLogger();
}
