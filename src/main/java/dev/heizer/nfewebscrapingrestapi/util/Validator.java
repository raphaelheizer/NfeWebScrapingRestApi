package dev.heizer.nfewebscrapingrestapi.util;

import dev.heizer.nfewebscrapingrestapi.exceptions.AmbiguousValidationConditionException;
import dev.heizer.nfewebscrapingrestapi.exceptions.NonMatchingTextValidatorException;
import dev.heizer.nfewebscrapingrestapi.interfaces.TextMatchingValidator;

import java.util.List;

public class Validator<T>
{
    private T result;
    private T defaultResult;
    private boolean isValid;

    public T validate(List<TextMatchingValidator<T>> validatorList, String validatingText)
    {
        validatorList.forEach(validator -> {
            validator.setTextToValidate(validatingText);

            checkAmbiguity(validatingText, validator.validate());
        });

        return checkForDefault();
    }

    private void checkAmbiguity(String validatingText, T validation)
    {
        if (validation != null)
        {
            if (!isValid)
            {
                result = validation;
                isValid = true;
            } else throw new AmbiguousValidationConditionException(
                    String.format("Validation for %s has already been evaluated. Condition must not return ambiguous result",
                            validatingText));
        }
    }

    public Validator<T> setDefaultCase(T defaultResult)
    {
        this.defaultResult = defaultResult;
        return this;
    }

    private T checkForDefault()
    {
        if (result == null)
            if (defaultResult == null)
                throw new NonMatchingTextValidatorException(
                        "Found no matches to validate. Consider define a default case for field validation");
            else
                return defaultResult;

        return result;
    }
}
