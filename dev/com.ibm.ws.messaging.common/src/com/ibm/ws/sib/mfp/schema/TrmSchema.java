// Generated from C:\SIB\Code\WASX.SIB\dd\SIB\ws\code\sib.mfp.impl\src\com\ibm\ws\sib\mfp\schema\TrmSchema.schema: do not edit directly
package com.ibm.ws.sib.mfp.schema;
import com.ibm.ws.sib.mfp.jmf.impl.JSchema;
import com.ibm.ws.sib.mfp.jmf.impl.JSType;
import com.ibm.ws.sib.mfp.jmf.parser.JSParser;
import java.io.StringReader;
public final class TrmSchema extends JSchema {
  public TrmSchema() {
    super((JSType) JSParser.parse(new StringReader("com.ibm.ws.sib.mfp.schema.TrmSchema: [ magicNumber: long, body: { empty: [] | routeData: [ originator: byte8, *( routeCost: [ cellules: hexBinary, costs: int ] )* ] } ]")));
  }
}
