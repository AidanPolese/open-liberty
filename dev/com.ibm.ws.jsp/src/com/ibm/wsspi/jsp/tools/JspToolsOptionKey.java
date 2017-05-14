//IBM Confidential OCO Source Material
//5724-i63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997-2004
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.

/*
* Created on Nov 24, 2003
*
* To change the template for this generated file go to
* Window>Preferences>Java>Code Generation>Code and Comments
*/

/*
* Change history:
* defect 215691 "add support for javaEncoding"  2004/07/12  Scott Johnson
* Defect PK26741 - RepeatTag index does not accept "int"
* Defect PK29373  Provide backward compatibility with v5(Page scope variables )
* defect 393421 - CTS:jsp translation error not occuring for wrong  <jsp:output> 2006/09/27 Scott Johnson
* defect 396002 CTS: no jsp fatal translation error for  taglib after actions Scott Johnson 10/17/2006
* "Unmatched end tags" exception not to be thrown in v6 10/18/2006
* defect 402921(PK31135) Change in Iteration eval in v6 causes infinite loops in certain scenarios
* defect PK34989 Supercedes PK26679 (Provides an optional flag to trim text before creating CDATA section) 2007/02/13
* defect PK47738 v6 does not allow page authors to encode params within params tag(spec compliant) maintain backward compatibility with v5 which allows encoding of params. 2007/09/05 Anuradha Natarajan
* defect PK65013 Need ability to customize pageContext variable.                2008/07/07  sartoris
* defect PK72039 Add ability to continue to compile the rest of the JSPs during a batch compile failure  2008/11/15  Jay Sartoris
* defect 650003 Supplemental enhancement for PM06063 (prereq) to support Lotus Expedite team		04/20/10	pmdinh
* defect PM21395 09/02/2010 pmdinh	Decode double quote in the attribute's value of a tag
*  Defect PM41476 07/28/2011 sartoris    Tags have the xmlns attribute when rendered.
* Defect PM94792 07/30/2014 hmpadill    Disable escaping CR, LF, and Tab within an expression
* defect PI12939 07/31/2014 hmpadill    Add ability to delete .class file before compile
* Defect PI30519 11/25/2014 hmpadill    Allow multiple attribute values in tags
* defect PI37304 03/17/2015 hmpadill    Enable appropriate translation of JSP expressions with at least one constant string
*/
package com.ibm.wsspi.jsp.tools;

/**
* @author Scott Johnson
*
* To change the template for this generated type comment go to
* Window>Preferences>Java>Code Generation>Code and Comments
*/
public class JspToolsOptionKey {
private final static int KEEPGENERATED = 0;
private final static int VERBOSE = 1;
private final static int DEPRECATION = 2;
private final static int COMPILEWITHASSERT = 3;
private final static int TRACKDEPENDENCIES = 4;
private final static int USEPAGETAGPOOL = 5;
private final static int USETHREADTAGPOOL = 6;
private final static int USEJIKES = 7;
private final static int JSPFILEEXTENSIONS = 8;
private final static int JSPCOMPILECLASSPATH = 9;
private final static int USEFULLPACKAGENAMES = 10;
private final static int JAVAENCODING = 11;
private final static int JDKSOURCELEVEL = 12;
private final static int USEJDKCOMPILER = 13;
private final static int USEREPEATINT = 14;
private final static int USESCRIPTVARDUPINIT = 15;
private final static int ALLOWJSPOUTPUTELEMENTMISMATCH = 16;
private final static int ALLOWTAGLIBPREFIXREDEFINITION = 17;
private final static int ALLOWTAGLIBPREFIXUSEBEFOREDEFINITION = 18;
private final static int ALLOWUNMATCHEDENDTAG = 19;
private final static int USEITERATIONEVAL = 20;      //PK31135,402921
private final static int USECDATATRIM = 21;			 //PK34989
private final static int DISABLEURLENCODINGFORPARAMTAG = 22;             //PK47738
private final static int EVALQUOTEDANDESCAPEDEXPRESSION = 23; //PK53233
private final static int CONVERTEXPRESSION = 24;  //PK53703
private final static int ALLOWNULLPARENTINTAGFILE = 25;  //PK62809
private final static int MODIFYPAGECONTEXTVAR = 26;  //PK65013
private final static int COMPILEAFTERFAILURE = 27; //PK72039
private final static int DISABLERESOURCEINJECTION = 28;  //650003
private final static int ENABLEDOUBLEQUOTESDECODING = 29;  //PM21395
private final static int ENABLECDIWRAPPER = 30;  //enableCDIWrapper (ForBatchCompiler)
private final static int REMOVEXMLNSFROMOUTPUT = 31;  //PM41476
private static final int DONOTESCAPEWHITESPACECHARSINEXPRESSION = 32; //PM94792
private final static int DELETECLASSFILESBEFORERECOMPILE = 33;  //PI12939
private static final int ALLOWMULTIPLEATTRIBUTEVALUES = 34; //PI30519
private final static int ALLOWPRECEDENDEINJSPEXPRESSIONSWITHCONSTANTSTRING = 35;  //PI37304

public final static JspToolsOptionKey keepGeneratedKey=new JspToolsOptionKey(KEEPGENERATED);
public final static JspToolsOptionKey verboseKey=new JspToolsOptionKey(VERBOSE);
public final static JspToolsOptionKey deprecationKey=new JspToolsOptionKey(DEPRECATION);
/**
 * @deprecated
 *
 * This method is replaced by {@link #sourceLevelKey}
 */
public final static JspToolsOptionKey compileWithAssertKey=new JspToolsOptionKey(COMPILEWITHASSERT);
public final static JspToolsOptionKey trackDependenciesKey=new JspToolsOptionKey(TRACKDEPENDENCIES);
public final static JspToolsOptionKey usePageTagPoolKey=new JspToolsOptionKey(USEPAGETAGPOOL);
public final static JspToolsOptionKey useThreadTagPoolKey=new JspToolsOptionKey(USETHREADTAGPOOL);
public final static JspToolsOptionKey useJikesKey=new JspToolsOptionKey(USEJIKES);
public final static JspToolsOptionKey jspFileExtensionsKey=new JspToolsOptionKey(JSPFILEEXTENSIONS);
public final static JspToolsOptionKey jspCompileClasspathKey=new JspToolsOptionKey(JSPCOMPILECLASSPATH);
public final static JspToolsOptionKey useFullPackageNamesKey=new JspToolsOptionKey(USEFULLPACKAGENAMES);
public final static JspToolsOptionKey javaEncodingKey=new JspToolsOptionKey(JAVAENCODING);
public final static JspToolsOptionKey jdkSourceLevelKey=new JspToolsOptionKey(JDKSOURCELEVEL);
public final static JspToolsOptionKey useJDKCompilerKey=new JspToolsOptionKey(USEJDKCOMPILER);
public final static JspToolsOptionKey useRepeatInt=new JspToolsOptionKey(USEREPEATINT);
public final static JspToolsOptionKey useScriptVarDupInit=new JspToolsOptionKey(USESCRIPTVARDUPINIT);
public final static JspToolsOptionKey allowJspOutputElementMismatch=new JspToolsOptionKey(ALLOWJSPOUTPUTELEMENTMISMATCH);
public final static JspToolsOptionKey allowTaglibPrefixRedefinition=new JspToolsOptionKey(ALLOWTAGLIBPREFIXREDEFINITION);
public final static JspToolsOptionKey allowTaglibPrefixUseBeforeDefinition=new JspToolsOptionKey(ALLOWTAGLIBPREFIXUSEBEFOREDEFINITION);
public final static JspToolsOptionKey allowUnmatchedEndTag=new JspToolsOptionKey(ALLOWUNMATCHEDENDTAG);
public final static JspToolsOptionKey useIterationEval=new JspToolsOptionKey(USEITERATIONEVAL);
public final static JspToolsOptionKey useCDataTrim=new JspToolsOptionKey(USECDATATRIM); //PK34989
public final static JspToolsOptionKey disableURLEncodingForParamTag=new JspToolsOptionKey(DISABLEURLENCODINGFORPARAMTAG); //PK47738
public final static JspToolsOptionKey evalQuotedAndEscapedExpression=new JspToolsOptionKey(EVALQUOTEDANDESCAPEDEXPRESSION); //PK53233
public final static JspToolsOptionKey convertExpression=new JspToolsOptionKey(CONVERTEXPRESSION);  //PK53703
public final static JspToolsOptionKey allowNullParentInTagFile=new JspToolsOptionKey(ALLOWNULLPARENTINTAGFILE);  //PK62809
public final static JspToolsOptionKey modifyPageContextVar=new JspToolsOptionKey(MODIFYPAGECONTEXTVAR);  //PK65013
public final static JspToolsOptionKey compileAfterFailureKey=new JspToolsOptionKey(COMPILEAFTERFAILURE); //PK72039
public final static JspToolsOptionKey disableResourceInjection=new JspToolsOptionKey(DISABLERESOURCEINJECTION); //650003
public final static JspToolsOptionKey enableDoubleQuotesDecoding=new JspToolsOptionKey(ENABLEDOUBLEQUOTESDECODING); //PM21395
public final static JspToolsOptionKey enableCDIWrapper=new JspToolsOptionKey(ENABLECDIWRAPPER);
public final static JspToolsOptionKey removeXmlnsFromOutput=new JspToolsOptionKey(REMOVEXMLNSFROMOUTPUT); //PM41476
public final static JspToolsOptionKey doNotEscapeWhitespaceCharsInExpression=new JspToolsOptionKey(DONOTESCAPEWHITESPACECHARSINEXPRESSION); //PM94792
public final static JspToolsOptionKey deleteClassFilesBeforeRecompile=new JspToolsOptionKey(DELETECLASSFILESBEFORERECOMPILE); //PI12939
public final static JspToolsOptionKey allowMultipleAttributeValues=new JspToolsOptionKey(ALLOWMULTIPLEATTRIBUTEVALUES); //PI30519
public final static JspToolsOptionKey allowPrecedenceInJspExpressionsWithConstantString=new JspToolsOptionKey(ALLOWPRECEDENDEINJSPEXPRESSIONSWITHCONSTANTSTRING); //PI37304
private int key=0;
/**
 *
 */

private JspToolsOptionKey(int key) {
    this.key = key;
}

}
