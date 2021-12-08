package dev.heizer.nfewebscrapingrestapi.exceptions;

public class NonUniqueResultException extends RuntimeException
{
    public NonUniqueResultException()
    {
        super("Query result returned more than one elements when it should be unique.");
    }
}
