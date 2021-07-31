package org.arkngbot.services.impl;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Wraps some time-retrieving functionalities to enable mocking dates in tests
 */
@Service
public class TimeSupport {

    private static final String UTC = "UTC";

    @NonNull
    public LocalDateTime getCurrentTimeInUTC() {
        return LocalDateTime.now(ZoneId.of(UTC));
    }

    @NonNull
    public LocalDate getCurrentDateInUTC() {
        return LocalDate.now(ZoneId.of(UTC));
    }

    public int calculateWeekNumber(@NonNull LocalDate currentDate) {
        int year = currentDate.getYear();
        LocalDate checkDate = LocalDate.of(year, 1, 1);

        // Find the first Thursday of the year. It is guaranteed to belong to the first week
        while (checkDate.getDayOfWeek() != DayOfWeek.THURSDAY) {
            checkDate = checkDate.plusDays(1);
        }

        LocalDate weekMonday = checkDate.minusDays(3);
        LocalDate weekSunday = checkDate.plusDays(3);

        // Handle zeroth week case
        if (currentDate.isBefore(weekMonday)) {
            return 0;
        }

        int counter = 1;
        while (currentDate.isBefore(weekMonday) || currentDate.isAfter(weekSunday)) {
            counter++;
            weekMonday = weekMonday.plusWeeks(1);
            weekSunday = weekSunday.plusWeeks(1);
        }

        return counter;
    }
}
