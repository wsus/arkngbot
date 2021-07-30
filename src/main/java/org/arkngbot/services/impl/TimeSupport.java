package org.arkngbot.services.impl;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

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
}
