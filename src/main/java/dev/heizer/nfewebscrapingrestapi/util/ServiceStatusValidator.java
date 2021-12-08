package dev.heizer.nfewebscrapingrestapi.util;

import dev.heizer.nfewebscrapingrestapi.interfaces.TextMatchingValidator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServiceStatusValidator<T> extends TextMatchingValidator<T>
{
    public ServiceStatusValidator(String pattern, T expectedResult)
    {
        super(pattern, expectedResult);
    }

    @Override
    public T validate()
    {
        Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(text);

        return m.find() ? expectedResult : null;
    }
}
