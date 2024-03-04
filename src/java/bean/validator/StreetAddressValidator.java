/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean.validator;

import javax.faces.validator.FacesValidator;

@FacesValidator("streetAddressValidator")
public class StreetAddressValidator extends AbstractValidator {

    @Override
    public String getPattern() {
        return "^([a-zA-Z]+\\s){1,}([a-zA-Z]+\\s?)*\\d*$";
    }

    @Override
    public String getFailedValidationMessage() {
        return "Address validation failed.";
    }
    
    @Override
    public String getDetailedValidationMessage() {
        return "Street address should contain at least one word, and shouldn't contain special characters";
    }

}
