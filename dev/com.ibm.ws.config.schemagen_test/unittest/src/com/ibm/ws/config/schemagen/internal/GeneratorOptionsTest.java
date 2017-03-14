package com.ibm.ws.config.schemagen.internal;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class GeneratorOptionsTest {

	@Test
	public void testLocalePortuguese() {
		runTest("pt", "BR");
	}

	@Test
	public void testLocaleChinese() {
		runTest("zh", null);
	}

	@Test
	public void testLocaleTraditionalChinese() {
		runTest("zh", "TW");
	}
	
	private void runTest(String lang, String country) {
		String locale = lang + '_' + country;
		
		if (country == null) locale = lang;
		
		GeneratorOptions options = new GeneratorOptions();
		options.processArgs(new String[] {"--locale=" + locale, "file.xsd"});
		
		assertEquals("The language should be pt", lang, options.getLocale().getLanguage());
		if (country != null) {
			assertEquals("The country should be BR", country, options.getLocale().getCountry());
		} else {
			assertEquals("The country should be blank", "", options.getLocale().getCountry());
		}
	}
}
