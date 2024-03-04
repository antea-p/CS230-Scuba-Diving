/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean.validator;

import javax.faces.validator.FacesValidator;

@FacesValidator("emailValidator")
public class EmailValidator extends AbstractValidator {

    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";

    @Override
    public String getPattern() {
        return EMAIL_PATTERN;
    }

    @Override
    public String getFailedValidationMessage() {
        return "Email validation failed.";
    }

    @Override
    public String getDetailedValidationMessage() {
        return "Email does not match the standard format (example: name@example.com).";
    }
}
