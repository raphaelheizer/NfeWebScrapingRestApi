package dev.heizer.nfewebscrapingrestapi.jobs;

import dev.heizer.nfewebscrapingrestapi.tasks.ScrapeNfeUrlAndGetData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class RepeatableScheduledExecutorConfig
{
    private final ScrapeNfeUrlAndGetData scrapeNfeUrlAndGetData;

    @Autowired
    public RepeatableScheduledExecutorConfig(ScrapeNfeUrlAndGetData scrapeNfeUrlAndGetData)
    {
        this.scrapeNfeUrlAndGetData = scrapeNfeUrlAndGetData;
    }

    @Bean
    public Integer getSchedulerTime()
    {
        return 5;
    }

    @Bean
    public TimeUnit getSchedulerTimeTimeUnit()
    {
        return TimeUnit.MINUTES;
    }

    @Bean
    public RepeatableScheduledExecutor scheduledExecutor()
    {

        RepeatableScheduledExecutor executor =
                new RepeatableScheduledExecutor(getSchedulerTime(), getSchedulerTimeTimeUnit());

        executor.addTask(scrapeNfeUrlAndGetData)
                .run();

        return executor;
    }
}
