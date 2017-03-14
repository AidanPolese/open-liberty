package com.ibm.ws.config.schemagen.internal;

import javax.xml.xpath.XPathExpressionException;

import org.junit.BeforeClass;
import org.junit.Test;

public class Generatorv1WithSchema11Test extends Generatorv1TestBase {
  @BeforeClass
  public static void setup() throws Exception {
    setup(new String[] {"build/server.xsd", "-outputVersion=1", "-schemaVersion=1.1"});
  }

  @Test
  public void testSchema11Any() throws XPathExpressionException {
	super.testSchema11Any();
  }
}
