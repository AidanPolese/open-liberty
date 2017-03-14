package com.ibm.ws.config.schemagen.internal;

import javax.xml.xpath.XPathExpressionException;

import org.junit.BeforeClass;
import org.junit.Test;

public class Generatorv1WithSchema10Test extends Generatorv1TestBase {
  @BeforeClass
  public static void setup() throws Exception {
    setup(new String[] {"build/server.xsd", "-outputVersion=1", "-schemaVersion=1.0"});
  }

  @Test
  public void testSchema10Any() throws XPathExpressionException {
    assertXPath("There should not be an xsd:any", "complexType[@name='testAny']/choice/any", null);
    assertXPath("There should be a pid element", "complexType[@name='testAny']/choice/element[@name='pid']/@type", "other");
  }
}
