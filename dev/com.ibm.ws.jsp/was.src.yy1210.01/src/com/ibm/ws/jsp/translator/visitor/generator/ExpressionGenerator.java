//IBM Confidential OCO Source Material
//5639-D57,5630-A36,5630-A37,5724-D18 (C) COPYRIGHT International Business Machines Corp. 1997-2005 
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
//
//Changes
//PK70031   sartoris    09/10/2008  Plus signs cause problems in expressions (e.g. <% "x" + i++ %>)
//PM54544   sartoris    12/20/2011  Square brackets are not recognized as an array when in an expression and causes a problem when it contains a plus sign (e.g. <%= testArray[i+1] + "test2" %> )

package com.ibm.ws.jsp.translator.visitor.generator;

import org.w3c.dom.CDATASection;

import com.ibm.ws.jsp.JspCoreException;
import java.util.ArrayList;

public class ExpressionGenerator extends CodeGeneratorBase {
    
    

    public void startGeneration(int section, JavaCodeWriter writer) throws JspCoreException {
        if (section == CodeGenerationPhase.METHOD_SECTION) {
            CDATASection cdata = (CDATASection)element.getFirstChild();
            String data = cdata.getData();
            data = data.replaceAll("&gt;", ">");
            data = data.replaceAll("&lt;", "<");
            data = data.replaceAll("&amp;", "&");
            char[] chars = data.toCharArray();
            writeDebugStartBegin(writer);

            
            String expression= new String(GeneratorUtils.escapeQuotes(chars));
            // detect simple cases of string catenation
            if (expression.indexOf('+') != -1 && expression.indexOf('"') != -1) {
               // there may be a potential string catenation operation with a constant string               
               //System.out.println("TERM "+expression);
               int     parenthesis           = 0;
               int     numberConstantStrings = 0;
               boolean constantString        = false;
               int     quoteMode             = 0;	// singleQuote = 1, doubleQuote = 2
               int     termStart             = 0;
               boolean skipSplit             = false;
               int     bracket               = 0;  //PM54544
               ArrayList terms = new ArrayList();
               // the following code tries to parse the expression, if a simple case is found, then split it
               for (int i = 0; i < expression.length(); i++) {
                   char c = expression.charAt(i);
                   switch(c) {
                   case '(':
                       if (quoteMode == 0) {
                          parenthesis++;
                          constantString = false;
                       }
                       break;
                   case ')':
                       if (quoteMode == 0) {
                          parenthesis--;
                          constantString = false;
                       }
                       break;
                   case '[':    //PM54544  need to handle a case like this <%= testArray[i+1] + "test2" %> 
                       if (quoteMode == 0) {
                          bracket++;
                          constantString = false;
                       }
                       break;
                   case ']':    //PM54544
                       if (quoteMode == 0) {
                          bracket--;
                          constantString = false;
                       }
                       break;
                   case '?':
                   case ':':
                       if (quoteMode == 0) {
                           skipSplit = true;
                       }
                       break;
                   case '\'':
                       if (quoteMode == 0) {
                          quoteMode = 1;
                       } else {
                          quoteMode = 0;
                       }
                       break;
                   case '"':
                       if (quoteMode == 0) {
                          quoteMode = 2;
                          constantString = true;
                       } else {
                          quoteMode = 0;
                       }
                       break;
                   case '\\':
                       if ((quoteMode != 0) && (i < (expression.length()-1))) {
                         char c1 = expression.charAt(i+1);
                         if ((c1 == '\\') || (c1 == '"') || (c1 == '\''))
                           i++;
                       }
                       break;
                   case '+':
                       if (parenthesis == 0 && quoteMode == 0 && bracket == 0) {  //PM54544 checking bracket
                           //PK70031 start
                           //check to see if we have something like: x++
                           //also, trying to avoid a StringIndexOutOfBoundsException
                           if ((i+1) < expression.length()) {
                               char c1 = expression.charAt(i+1);

                               if (c1 == '+') {
                                   //we need to add two because right now the text is just "x"...we weren't meant to add the ++ as part of the text
                                   //also, trying to avoid a StringIndexOutOfBoundsException
                                   if ((i+2) < expression.length()) {
                                       i=i+2;
                                   }
                               }
                           
                           }
                           if (!(expression.substring(termStart,i)).trim().equals("")) {
                           //PK70031 end
                               terms.add(expression.substring(termStart,i));
                               //termStart = i+1; //PK70031 commented out...moved below
                               if (constantString)
                                   numberConstantStrings++;
                           }
                           
                           termStart = i+1; //PK70031 moved this down to here because it will add a single "+" to the term if it remains inside the if statement above
                       }
                       break;
                   case ' ': // don't do anything
                       break;
                   default:
                       if (quoteMode == 0)
                           constantString = false;
                       break;
                   }
                   if (skipSplit) {
                       break;
                   }
               }
               if (parenthesis == 0 && bracket == 0 && termStart < expression.length()) {  //PM54544 checking bracket
                   terms.add(expression.substring(termStart,expression.length()));
                   if (constantString)
                       numberConstantStrings++;
               }
               if (!skipSplit && terms.size() > 1 && numberConstantStrings > 0) {
                   // we detected string catenation with at least one constant string
                   for (int i = 0; i < terms.size(); i++) {
                       String el = (String)terms.get(i);
                       constantString = true;
                       // search for the beginning and end of a constant string
                       for (int j = 0; j < el.length(); j++) {
                           if (el.charAt(j) == '"') break;
                           if (el.charAt(j) != ' ') {
                               constantString = false;
                               break;
                           }
                       }
                       for (int j = el.length()-1; j >= 0; j--) {
                           if (el.charAt(j) == '"') break;
                           if (el.charAt(j) != ' ') {
                               constantString = false;
                               break;
                           }
                       }

                       if (constantString) {
                          // we can use write instead of print because we know that the expression is non-null
                          // (print checks for null and then calls write)
                          writer.println("out.write("+el+");");
                          //System.out.print("   TERM1 ");
                          //System.out.println(el);
                
                       } else {
                          writer.println("out.print("+el+");");
                          //System.out.print("   TERM2 ");
                          //System.out.println(el);
                       }
                   }
               } else // we could not detect any expression, thus go back to the regular code
                   writer.println("out.print("+expression+");");
            } else // we could not detect any expression, thus go back to the regular code
               writer.println("out.print("+expression+");");
            // original code commented out
            //writer.println("out.print("+new String(GeneratorUtils.escapeQuotes(chars))+");");
            
            writeDebugStartEnd(writer);
        }
    }

    public void endGeneration(int section, JavaCodeWriter writer)  throws JspCoreException {
    }
}
