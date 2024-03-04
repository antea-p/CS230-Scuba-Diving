/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean.validator;

import javax.faces.validator.FacesValidator;

@FacesValidator("usernameValidator")
public class UsernameValidator extends AbstractValidator {

    @Override
    public String getPattern() {
        return "^[a-zA-Z0-9_]+$";
    }

    @Override
    public String getFailedValidationMessage() {
        return "Invalid username.";
    }

    @Override
    public String getDetailedValidationMessage() {
        return "Username can only contain alphanumeric characters and underscores.";
    }
}
