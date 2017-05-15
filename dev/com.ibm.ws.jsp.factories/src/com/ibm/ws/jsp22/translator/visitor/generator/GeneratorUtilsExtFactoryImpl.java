package com.ibm.ws.jsp22.translator.visitor.generator;

import org.osgi.service.component.annotations.Component;

import com.ibm.ws.jsp.translator.visitor.generator.GeneratorUtilsExt;
import com.ibm.ws.jsp.translator.visitor.generator.GeneratorUtilsExtFactory;

@Component(property = { "service.vendor=IBM" })
public class GeneratorUtilsExtFactoryImpl implements GeneratorUtilsExtFactory {

	private static final GeneratorUtilsExtImpl gue = new GeneratorUtilsExtImpl();
	
	@Override
	public GeneratorUtilsExt getGeneratorUtilsExt() {
		// TODO Auto-generated method stub
		return gue;
	}


}
