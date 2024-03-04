/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean.validator;

import javax.faces.validator.FacesValidator;

@FacesValidator("zipcodeValidator")
public class ZipcodeValidator extends AbstractValidator {

    @Override
    public String getPattern() {
        return "\\d{5}";
    }

    @Override
    public String getFailedValidationMessage() {
        return "Zipcode validation failed.";
    }

    @Override
    public String getDetailedValidationMessage() {
        return "Zipcode must be exactly 5 digits.";
    }
}
