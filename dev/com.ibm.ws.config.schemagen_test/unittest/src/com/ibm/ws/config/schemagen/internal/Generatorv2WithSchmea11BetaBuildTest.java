package com.ibm.ws.config.schemagen.internal;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileWriter;
import java.util.Properties;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class Generatorv2WithSchmea11BetaBuildTest extends Generatorv2WithSchema11Test {
  private static File version = new File("build/wlp/lib/versions/WebSphereApplicationServer.properties");
  @BeforeClass
  public static void setup() throws Exception {
    version.getParentFile().mkdirs();
    Properties props = new Properties();
    props.setProperty("com.ibm.websphere.productVersion", "2017.1.0.0");
    props.store(new FileWriter(version), null);
    setup(new String[] {"build/server.xsd", "-outputVersion=2", "-schemaVersion=1.1"});
  }

  @AfterClass
  public static void tearDown() {
    version.delete();
  }

  @Test
  public void checkBetaTagOnOCD() throws XPathExpressionException {
    Object obj = xp.evaluate("/schema/complexType[@name='serverType']//element[@name='betaElement']", root, XPathConstants.NODE);
    assertNotNull("betaElement should be in schema", obj);
  }

  @Test
  public void checkBetaTagOnAD() throws XPathExpressionException {
    assertNotNull("betaElement should be in schema", xp.evaluate("/schema/complexType[@name='test']/attribute[@name='beta']", root, XPathConstants.NODE));
  }
}
