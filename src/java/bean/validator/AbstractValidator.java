/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean.validator;

/**
 *
 * @author boone
 */
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

public abstract class AbstractValidator implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        // Ako je vrijednost null, a input polje koristi required="true", JSF prikazuje poruku greške.
        if (value == null) {
            return;
        }
        String inputValue = value.toString();
        if (!inputValue.matches(getPattern())) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN,
                    getFailedValidationMessage(), getDetailedValidationMessage());
            throw new ValidatorException(message);
        }
    }

    public abstract String getPattern();

    public abstract String getFailedValidationMessage();

    public abstract String getDetailedValidationMessage();
}
