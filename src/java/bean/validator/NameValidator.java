/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean.validator;

import javax.faces.validator.FacesValidator;

@FacesValidator("nameValidator")
public class NameValidator extends AbstractValidator {

    private static final String NAME_PATTERN = "^([A-Za-zÀ-ÖØ-öø-ÿĐČŠŽĆ]+[ ]?)+$";

    @Override
    public String getPattern() {
        return NAME_PATTERN;
    }

    @Override
    public String getFailedValidationMessage() {
        return "Name validation failed.";
    }

    @Override
    public String getDetailedValidationMessage() {
        return "The field should only contain letters and spaces. It should not start or end with a space.";
    }
}
