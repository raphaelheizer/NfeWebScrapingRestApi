package dev.heizer.nfewebscrapingrestapi.util;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimestampCalculator
{
    /**
     * Calculates a {@link Timestamp} backward in time
     * @param time Time to be subtracted from
     * @param regressionAmount Amount of time to subtract to
     * @param timeUnit Unit of time in which will defined the amount
     * @return {@link Timestamp}
     */
    public static Timestamp timeWalk(Timestamp time, Integer regressionAmount, TimeUnit timeUnit)
    {
        long timeInMill = time.getTime();
        long regressionTimeInMill = TimeUnit.MILLISECONDS.convert(regressionAmount, timeUnit);

        long warpedTime = timeInMill - regressionTimeInMill;
        return new Timestamp(warpedTime);
    }

    /**
     * @return Current date and time as {@link Timestamp}
     */
    public static Timestamp now()
    {
        return new Timestamp(new Date().getTime());
    }

    public static Timestamp endOfDay(Date day)
    {
        long endOfDay = Duration.ofDays(1).toMillis();
        return new Timestamp((day.getTime() + (endOfDay - 1)));
    }
}
