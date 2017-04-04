// Generated from C:\SIB\Code\WASX.SIB\dd\SIB\ws\code\sib.mfp.impl\src\com\ibm\ws\sib\mfp\schema\JmsStreamBodySchema.schema: do not edit directly
package com.ibm.ws.sib.mfp.schema;
import com.ibm.ws.sib.mfp.jmf.impl.JSchema;
import com.ibm.ws.sib.mfp.jmf.impl.JSType;
import com.ibm.ws.sib.mfp.jmf.parser.JSParser;
import java.io.StringReader;
public final class JmsStreamBodySchema extends JSchema {
  public JmsStreamBodySchema() {
    super((JSType) JSParser.parse(new StringReader("com.ibm.ws.sib.mfp.schema.JmsStreamBody: [ body: { empty: [] | data: [ *( value: anySimpleType )* ] } ]")));
  }
}
