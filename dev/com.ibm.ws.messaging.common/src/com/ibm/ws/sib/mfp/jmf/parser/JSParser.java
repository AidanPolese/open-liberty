/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/* Generated By:JavaCC: Do not edit this line. JSParser.java */
package com.ibm.ws.sib.mfp.jmf.parser;

import com.ibm.ws.sib.mfp.jmf.*;
import com.ibm.ws.sib.mfp.jmf.impl.*;

import java.util.*;
import java.io.*;

/** A parser for the JMFSchema "minimal" human-readable syntax, aka "JSParser notation."
 * There are a few things (e.g., JMFEnumTypes) that cannot currently be expressed.
 *
 * <p>
 * The syntax is as follows:
 * <xmp>
 * Schema ::= TypeDecl ("," TypeDecl)*
 *
 * TypeDecl ::= ( Name )? Type
 *
 * Name ::= <IDENTIFIER> ":"
 *
 * Type ::= Array | Variant | Tuple | Dynamic | Primitive | Ref
 *
 * Array ::= "*(" TypeDecl ")*"
 *
 * Variant ::= "{" TypeDecl ( "|" TypeDecl )* "}"
 *
 * Tuple ::= "[" ( TypeDecl ( "," TypeDecl )* )? "]"
 *
 * Dynamic ::= "Dynamic"
 *
 * Ref ::= "<" <IDENTIFIER> ">"
 *
 * Primitive ::= ... all possible XML Schema built-in types
 * </xmp>
 *
 * Of the top-level TypeDecls that make up a schema, the first is taken to be the root and
 * the others are used to satisfy 'Ref' clauses in the first schema.
 **/

public final class JSParser implements JSParserConstants {

  /** Parse an Reader stream, returning a JMFType tree, or null on error.  This can be
   * made into a JMFSchema by calling <b>JMFRegistry.instance.createJMFSchema</b>.
   **/

  public static JMFType parse(Reader rdr) {
    JSParser oneParser = new JSParser(rdr);
    try {
      return oneParser.Schema();
    } catch(ParseException e) {
      // No FFDC needed
          IllegalArgumentException ee = new IllegalArgumentException(e.getMessage());
          ee.initCause(e);
          throw ee;
    } finally {
      try {
        rdr.close();
      } catch (IOException e) {
            // No FFDC needed
      }
    }
  }


  /** Parse a file, returning a JMFType tree, or null on error.  This can be made into a
   * JMFSchema by calling <b>JMFRegistry.instance.createJMFSchema</b>.
   **/

  public static JMFType parse(String filename) {
    try {
      return parse(new FileReader(filename));
    } catch(IOException e) {
      // No FFDC needed
          IllegalArgumentException ee = new IllegalArgumentException(e.getMessage());
          ee.initCause(e);
          throw ee;
    }
  }


  // Subroutine to resolve dangling "expected" references

  private static void reconcile(JSType top, List defs, Map refs) {
    List unres = (List) refs.get(top.getFeatureName());
    if (unres != null)
      for (Iterator iter = unres.iterator(); iter.hasNext(); )
        ((JSDynamic) iter.next()).setExpectedType(top);
    for (Iterator iter = defs.iterator(); iter.hasNext(); ) {
      JSType one = (JSType) iter.next();
      List ur = (List) refs.get(one.getFeatureName());
      if (ur != null)
        for (Iterator jter = ur.iterator(); jter.hasNext(); )
          ((JSDynamic) jter.next()).setExpectedType(one);
    }
  }


  // Subroutine to enter a dangling "expected" reference in the refs map

  private static void addRef(Map refs, String key, JSDynamic unres) {
    List thisKey = (List) refs.get(key);
    if (thisKey == null) {
      thisKey = new ArrayList();
      refs.put(key, thisKey);
    }
    thisKey.add(unres);
  }

// Schema ::= TypeDecl ("," TypeDecl)*
  final public JSType Schema() throws ParseException {
  List defs = new ArrayList();
  Map refs = new HashMap();
  JSType top, one;
    top = TypeDecl(refs);
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 1:
        ;
        break;
      default:
        jj_la1[0] = jj_gen;
        break label_1;
      }
      jj_consume_token(1);
      one = TypeDecl(refs);
                                                  defs.add(one);
    }
    reconcile(top, defs, refs);
    {if (true) return top;}
    throw new Error("Missing return statement in function");
  }

// TypeDecl ::= ( Name )? Type
  final public JSType TypeDecl(Map refs) throws ParseException {
                              JSType ans; String name = null;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case IDENTIFIER:
      name = Name();
      break;
    default:
      jj_la1[1] = jj_gen;
      ;
    }
    ans = TypeDef(refs);
    if (name != null)
      ans.setFeatureName(name);
    {if (true) return ans;}
    throw new Error("Missing return statement in function");
  }

// Name ::= <IDENTIFIER> ":"
  final public String Name() throws ParseException {
                  Token name;
    name = jj_consume_token(IDENTIFIER);
    jj_consume_token(2);
                            {if (true) return name.image;}
    throw new Error("Missing return statement in function");
  }

// Type ::= Array | Variant | Tuple | Dynamic | Primitive | Ref
  final public JSType TypeDef(Map refs) throws ParseException {
                             JSType ans;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 3:
      ans = Array(refs);
      break;
    case 5:
      ans = Variant(refs);
      break;
    case 8:
      ans = Tuple(refs);
      break;
    case 10:
      ans = Dynamic();
      break;
    case 11:
      ans = Ref(refs);
      break;
    case 13:
    case 14:
    case 15:
    case 16:
    case 17:
    case 18:
    case 19:
    case 20:
    case 21:
    case 22:
    case 23:
    case 24:
    case 25:
    case 26:
    case 27:
    case 28:
    case 29:
    case 30:
    case 31:
    case 32:
    case 33:
    case 34:
    case 35:
    case 36:
    case 37:
    case 38:
    case 39:
    case 40:
    case 41:
    case 42:
    case 43:
    case 44:
    case 45:
    case 46:
    case 47:
    case 48:
    case 49:
    case 50:
    case 51:
    case 52:
    case 53:
    case 54:
    case 55:
    case 56:
    case 57:
    case 58:
    case 59:
      ans = Primitive();
      break;
    default:
      jj_la1[2] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    {if (true) return ans;}
    throw new Error("Missing return statement in function");
  }

// Array ::= "*(" TypeDecl ")*"
  final public JSRepeated Array(Map refs) throws ParseException {
                               JSType item;
    jj_consume_token(3);
    item = TypeDecl(refs);
    jj_consume_token(4);
    JSRepeated ans = new JSRepeated();
    ans.setItemType(item);
    {if (true) return ans;}
    throw new Error("Missing return statement in function");
  }

// Variant ::= "{" TypeDecl ( "|" TypeDecl )* "}"
  final public JSVariant Variant(Map refs) throws ParseException {
                                JSVariant ans = new JSVariant(); JSType mem;
    jj_consume_token(5);
    mem = TypeDecl(refs);
                             ans.addCase(mem);
    label_2:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 6:
        ;
        break;
      default:
        jj_la1[3] = jj_gen;
        break label_2;
      }
      jj_consume_token(6);
      mem = TypeDecl(refs);
                               ans.addCase(mem);
    }
    jj_consume_token(7);
                                                            {if (true) return ans;}
    throw new Error("Missing return statement in function");
  }

// Tuple ::= "[" ( TypeDecl ( "," TypeDecl )* )? "]"
  final public JSTuple Tuple(Map refs) throws ParseException {
                            JSTuple ans = new JSTuple(); JSType mem;
    jj_consume_token(8);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 3:
    case 5:
    case 8:
    case 10:
    case 11:
    case 13:
    case 14:
    case 15:
    case 16:
    case 17:
    case 18:
    case 19:
    case 20:
    case 21:
    case 22:
    case 23:
    case 24:
    case 25:
    case 26:
    case 27:
    case 28:
    case 29:
    case 30:
    case 31:
    case 32:
    case 33:
    case 34:
    case 35:
    case 36:
    case 37:
    case 38:
    case 39:
    case 40:
    case 41:
    case 42:
    case 43:
    case 44:
    case 45:
    case 46:
    case 47:
    case 48:
    case 49:
    case 50:
    case 51:
    case 52:
    case 53:
    case 54:
    case 55:
    case 56:
    case 57:
    case 58:
    case 59:
    case IDENTIFIER:
      mem = TypeDecl(refs);
                               ans.addField(mem);
      label_3:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case 1:
          ;
          break;
        default:
          jj_la1[4] = jj_gen;
          break label_3;
        }
        jj_consume_token(1);
        mem = TypeDecl(refs);
                                     ans.addField(mem);
      }
      break;
    default:
      jj_la1[5] = jj_gen;
      ;
    }
    jj_consume_token(9);
          {if (true) return ans;}
    throw new Error("Missing return statement in function");
  }

// Dynamic ::= "Dynamic"
  final public JSDynamic Dynamic() throws ParseException {
    jj_consume_token(10);
              {if (true) return new JSDynamic();}
    throw new Error("Missing return statement in function");
  }

// Ref ::= "<" <IDENTIFIER> ">"
  final public JSDynamic Ref(Map refs) throws ParseException {
                            Token tok;  JSDynamic ans = new JSDynamic();
    jj_consume_token(11);
    tok = jj_consume_token(IDENTIFIER);
    jj_consume_token(12);
                               addRef(refs, tok.image, ans);  {if (true) return ans;}
    throw new Error("Missing return statement in function");
  }

// Primitive ::= ... all possible XML Schema built-in types
  final public JSPrimitive Primitive() throws ParseException {
                            Token t; JSPrimitive ans = new JSPrimitive();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 13:
      t = jj_consume_token(13);
      break;
    case 14:
      t = jj_consume_token(14);
      break;
    case 15:
      t = jj_consume_token(15);
      break;
    case 16:
      t = jj_consume_token(16);
      break;
    case 17:
      t = jj_consume_token(17);
      break;
    case 18:
      t = jj_consume_token(18);
      break;
    case 19:
      t = jj_consume_token(19);
      break;
    case 20:
      t = jj_consume_token(20);
      break;
    case 21:
      t = jj_consume_token(21);
      break;
    case 22:
      t = jj_consume_token(22);
      break;
    case 23:
      t = jj_consume_token(23);
      break;
    case 24:
      t = jj_consume_token(24);
      break;
    case 25:
      t = jj_consume_token(25);
      break;
    case 26:
      t = jj_consume_token(26);
      break;
    case 27:
      t = jj_consume_token(27);
      break;
    case 28:
      t = jj_consume_token(28);
      break;
    case 29:
      t = jj_consume_token(29);
      break;
    case 30:
      t = jj_consume_token(30);
      break;
    case 31:
      t = jj_consume_token(31);
      break;
    case 32:
      t = jj_consume_token(32);
      break;
    case 33:
      t = jj_consume_token(33);
      break;
    case 34:
      t = jj_consume_token(34);
      break;
    case 35:
      t = jj_consume_token(35);
      break;
    case 36:
      t = jj_consume_token(36);
      break;
    case 37:
      t = jj_consume_token(37);
      break;
    case 38:
      t = jj_consume_token(38);
      break;
    case 39:
      t = jj_consume_token(39);
      break;
    case 40:
      t = jj_consume_token(40);
      break;
    case 41:
      t = jj_consume_token(41);
      break;
    case 42:
      t = jj_consume_token(42);
      break;
    case 43:
      t = jj_consume_token(43);
      break;
    case 44:
      t = jj_consume_token(44);
      break;
    case 45:
      t = jj_consume_token(45);
      break;
    case 46:
      t = jj_consume_token(46);
      break;
    case 47:
      t = jj_consume_token(47);
      break;
    case 48:
      t = jj_consume_token(48);
      break;
    case 49:
      t = jj_consume_token(49);
      break;
    case 50:
      t = jj_consume_token(50);
      break;
    case 51:
      t = jj_consume_token(51);
      break;
    case 52:
      t = jj_consume_token(52);
      break;
    case 53:
      t = jj_consume_token(53);
      break;
    case 54:
      t = jj_consume_token(54);
      break;
    case 55:
      t = jj_consume_token(55);
      break;
    case 56:
      t = jj_consume_token(56);
      break;
    case 57:
      t = jj_consume_token(57);
      break;
    case 58:
      t = jj_consume_token(58);
      break;
    case 59:
      t = jj_consume_token(59);
      break;
    default:
      jj_la1[6] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    ans.setXSDTypeName(t.image);
    {if (true) return ans;}
    throw new Error("Missing return statement in function");
  }

  public JSParserTokenManager token_source;
  ASCII_CharStream jj_input_stream;
  public Token token, jj_nt;
  private int jj_ntk;
  private int jj_gen;
  final private int[] jj_la1 = new int[7];
  final private int[] jj_la1_0 = {0x2,0x0,0xffffed28,0x40,0x2,0xffffed28,0xffffe000,};
  final private int[] jj_la1_1 = {0x0,0x0,0xfffffff,0x0,0x0,0xfffffff,0xfffffff,};
  final private int[] jj_la1_2 = {0x0,0x4,0x0,0x0,0x0,0x4,0x0,};

  public JSParser(java.io.InputStream stream) {
    jj_input_stream = new ASCII_CharStream(stream, 1, 1);
    token_source = new JSParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 7; i++) jj_la1[i] = -1;
  }

  public void ReInit(java.io.InputStream stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 7; i++) jj_la1[i] = -1;
  }

  public JSParser(java.io.Reader stream) {
    jj_input_stream = new ASCII_CharStream(stream, 1, 1);
    token_source = new JSParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 7; i++) jj_la1[i] = -1;
  }

  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 7; i++) jj_la1[i] = -1;
  }

  public JSParser(JSParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 7; i++) jj_la1[i] = -1;
  }

  public void ReInit(JSParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 7; i++) jj_la1[i] = -1;
  }

  final private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  final private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.Vector jj_expentries = new java.util.Vector();
  private int[] jj_expentry;
  private int jj_kind = -1;

  final public ParseException generateParseException() {
    jj_expentries.removeAllElements();
    boolean[] la1tokens = new boolean[70];
    for (int i = 0; i < 70; i++) {
      la1tokens[i] = false;
    }
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 7; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
          if ((jj_la1_1[i] & (1<<j)) != 0) {
            la1tokens[32+j] = true;
          }
          if ((jj_la1_2[i] & (1<<j)) != 0) {
            la1tokens[64+j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 70; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.addElement(jj_expentry);
      }
    }
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = (int[])jj_expentries.elementAt(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  final public void enable_tracing() {
  }

  final public void disable_tracing() {
  }

}
