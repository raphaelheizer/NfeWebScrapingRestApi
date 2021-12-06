package dev.heizer.nfewebscrapingrestapi.jobs;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.security.InvalidParameterException;
import java.util.concurrent.TimeUnit;

class RepeatableScheduledExecutorTest
{
    @Test
    void constructor()
    {
        // Values should always be greater than zero
        Assertions.assertThrows(InvalidParameterException.class,
                () -> new RepeatableScheduledExecutor(0, TimeUnit.SECONDS));
    }
}