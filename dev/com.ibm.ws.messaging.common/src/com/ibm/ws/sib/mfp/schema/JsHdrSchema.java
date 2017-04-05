// Generated from C:\SIB\Code\WASX.SIB\dd\SIB\ws\code\sib.mfp.impl\src\com\ibm\ws\sib\mfp\schema\JsHdrSchema.schema: do not edit directly
package com.ibm.ws.sib.mfp.schema;
import com.ibm.ws.sib.mfp.jmf.impl.JSchema;
import com.ibm.ws.sib.mfp.jmf.impl.JSType;
import com.ibm.ws.sib.mfp.jmf.parser.JSParser;
import java.io.StringReader;
public final class JsHdrSchema extends JSchema {
  public JsHdrSchema() {
    super((JSType) JSParser.parse(new StringReader("com.ibm.ws.sib.mfp.schema.JsHdrSchema: [ discriminator: string, arrivalTimestamp: long, systemMessageSourceUuid: byte8, systemMessageValue: long, securityUserId: string, securitySentBySystem: boolean, messageType: byte, subType: byte, hdr2: Dynamic, api: { empty: [] | data: Dynamic } ]")));
  }
}
