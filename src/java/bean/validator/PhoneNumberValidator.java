/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean.validator;

import javax.faces.validator.FacesValidator;

@FacesValidator("phoneNumberValidator")
public class PhoneNumberValidator extends AbstractValidator {

    @Override
    public String getPattern() {
        return "^[+]?\\d+$";
    }

    @Override
    public String getFailedValidationMessage() {
        return "Phone number validation failed.";
    }

    @Override
    public String getDetailedValidationMessage() {
        return "Phone number can only start with an optional '+' sign, followed by digits. Spaces are not allowed.";
    }
}
