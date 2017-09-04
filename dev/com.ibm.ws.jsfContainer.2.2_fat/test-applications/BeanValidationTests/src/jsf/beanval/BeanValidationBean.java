package jsf.beanval;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.validator.LengthValidator;

/**
 * A Managed Bean that is being used with the
 * BeanValidationTests application to test
 * beanValidation capability with JSF 2.2
 */
@ManagedBean(name = "beanValidation")
@SessionScoped
public class BeanValidationBean {

    // used for binding test
    private String bindingValue;

    // Validator used with the bindingValue in the facelet.
    private LengthValidator lengthValidator;

    /**
     * Getter for bindingValue
     */
    public String getBindingValue() {
        return bindingValue;
    }

    /**
     * Setter for bindingValue
     */
    public void setBindingValue(String bindingValue) {
        this.bindingValue = bindingValue;
    }

    /**
     * Getter for lengthValidator
     */
    public LengthValidator getLengthValidator() {
        lengthValidator = new LengthValidator(2);
        return lengthValidator;
    }

    /**
     * Setter for lenghtValidator
     */
    public void setLengthValidator(LengthValidator lengthValidator) {
        this.lengthValidator = lengthValidator;
    }
}
