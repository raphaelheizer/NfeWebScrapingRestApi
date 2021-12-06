package dev.heizer.nfewebscrapingrestapi.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.InvalidParameterException;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Polls a queue of {@link Runnable} tasks and executes in a dedicated user thread
 */
public class RepeatableScheduledExecutor
{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final long interval;
    private final TimeUnit timeUnit;
    private final Queue<Runnable> taskQueue = new LinkedBlockingQueue<>();
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private final Runnable task = () -> {
        try
        {
            taskQueue.forEach(Runnable::run);
        }
        catch (Exception e)
        {
            logger.error("Unable to execute task. Attempting restart.", e);
        }
    };

    public RepeatableScheduledExecutor(long interval, TimeUnit timeUnit)
    {
        if (interval <= 0)
            throw new InvalidParameterException("Interval must be greater than zero.");

        this.interval = interval;
        this.timeUnit = timeUnit;
    }

    /**
     * Adds a {@link Runnable} tasks to the queue
     * @param task Task to be executed sequentially by the {@link #executor}
     * @implNote Fluent
     */
    public RepeatableScheduledExecutor addTask(Runnable task)
    {
        taskQueue.add(task);
        return this;
    }

    /**
     * Executes tasks sequentially using an {@link #executor}
     */
    public void run()
    {
        executor.scheduleAtFixedRate(task, 0, interval, timeUnit);
    }
}
