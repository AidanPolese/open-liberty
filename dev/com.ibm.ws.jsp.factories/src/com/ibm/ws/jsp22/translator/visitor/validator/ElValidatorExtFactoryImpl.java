package com.ibm.ws.jsp22.translator.visitor.validator;

import org.osgi.service.component.annotations.Component;

import com.ibm.ws.jsp.translator.visitor.validator.ElValidatorExt;
import com.ibm.ws.jsp.translator.visitor.validator.ElValidatorExtFactory;

@Component(property = { "service.vendor=IBM" })
public class ElValidatorExtFactoryImpl implements ElValidatorExtFactory {
	
	private static final ELValidatorExtImpl eve = new ELValidatorExtImpl();

	@Override
	public ElValidatorExt getELValidatorExt() {
		// TODO Auto-generated method stub
		return eve;
	}

}
