// Generated from C:\SIB\Code\WASX.SIB\dd\SIB\ws\code\sib.mfp.impl\src\com\ibm\ws\sib\mfp\schema\JmsBytesBodySchema.schema: do not edit directly
package com.ibm.ws.sib.mfp.schema;
import com.ibm.ws.sib.mfp.jmf.impl.JSchema;
import com.ibm.ws.sib.mfp.jmf.impl.JSType;
import com.ibm.ws.sib.mfp.jmf.parser.JSParser;
import java.io.StringReader;
public final class JmsBytesBodySchema extends JSchema {
  public JmsBytesBodySchema() {
    super((JSType) JSParser.parse(new StringReader("com.ibm.ws.sib.mfp.schema.JmsBytesBody: [ body: { empty: [] | data: [ value: hexBinary ] } ]")));
  }
}
