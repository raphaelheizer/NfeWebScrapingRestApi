package dev.heizer.nfewebscrapingrestapi.exceptions;

public class AmbiguousValidationConditionException extends RuntimeException
{
    public AmbiguousValidationConditionException() {}

    public AmbiguousValidationConditionException(String message) {super(message);}
}
