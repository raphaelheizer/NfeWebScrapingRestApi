package dev.heizer.nfewebscrapingrestapi.exceptions;

public class NonMatchingTextValidatorException extends RuntimeException
{
    public NonMatchingTextValidatorException()
    {
    }

    public NonMatchingTextValidatorException(String message)
    {
        super(message);
    }
}
