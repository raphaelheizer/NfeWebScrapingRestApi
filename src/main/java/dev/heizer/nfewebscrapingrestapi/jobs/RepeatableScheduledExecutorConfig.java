package dev.heizer.nfewebscrapingrestapi.jobs;

import dev.heizer.nfewebscrapingrestapi.tasks.ScrapeNfeUrlAndGetData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class RepeatableScheduledExecutorConfig
{
    @Bean
    public RepeatableScheduledExecutor scheduledExecutor()
    {
        RepeatableScheduledExecutor executor = new RepeatableScheduledExecutor(5, TimeUnit.MINUTES);

        executor.addTask(new ScrapeNfeUrlAndGetData())
                .run();

        return executor;
    }
}
